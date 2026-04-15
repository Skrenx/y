package com.example.hydrant

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HydrantChangeListenerService : Service() {

    private var hydrantNotifListener: ListenerRegistration? = null
    private var fireIncidentListener: ListenerRegistration? = null
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    // Flag to skip the initial snapshot (only notify on NEW docs after startup)
    private var isHydrantFirstLoad = true
    private var isIncidentFirstLoad = true

    companion object {
        const val CHANNEL_ID = "hydrant_updates_channel"
        const val FOREGROUND_CHANNEL_ID = "firegrid_service_channel"
        const val FOREGROUND_NOTIF_ID = 9999
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(FOREGROUND_NOTIF_ID, buildForegroundNotification())
        startListeningForFireIncidents()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY  // Android will restart this service if it gets killed
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        hydrantNotifListener?.remove()
        fireIncidentListener?.remove()
        super.onDestroy()
    }

    // ─── Listen to hydrant_notifications (written by sendHydrantChangeNotificationToAdmins) ───

    private fun startListeningForHydrantNotifications() {
        val currentUserEmail = auth.currentUser?.email?.lowercase()

        hydrantNotifListener = firestore.collection("hydrant_notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("HydrantListener", "Listen failed", error)
                    return@addSnapshotListener
                }

                // Skip initial load — we only want documents added AFTER the service starts
                if (isHydrantFirstLoad) {
                    isHydrantFirstLoad = false
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val doc = change.document
                        val title = doc.getString("title") ?: return@forEach
                        val message = doc.getString("message") ?: return@forEach
                        val performedBy = doc.getString("performedBy")?.lowercase() ?: ""

                        // Don't notify the person who made the change
                        if (performedBy == currentUserEmail) return@forEach

                        showChangeNotification(title, message)
                    }
                }
            }
    }

    // ─── Listen for fire incident reports ───

    private fun startListeningForFireIncidents() {
        val currentUserEmail = auth.currentUser?.email?.lowercase() ?: return

        fireIncidentListener = firestore.collection("fire_incident_reports")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("FireIncidentListener", "Listen failed", error)
                    return@addSnapshotListener
                }

                if (isIncidentFirstLoad) {
                    isIncidentFirstLoad = false
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val doc = change.document

                        // Check if current user is admin before notifying
                        firestore.collection("admins")
                            .document(currentUserEmail)
                            .get()
                            .addOnSuccessListener { adminDoc ->
                                if (adminDoc.exists()) {
                                    val reporterName = doc.getString("reporterName") ?: "Unknown"
                                    val incidentLocation = doc.getString("incidentLocation") ?: "Unknown location"
                                    val description = doc.getString("description") ?: ""

                                    showFireIncidentNotification(
                                        context = this,
                                        reporterName = reporterName,
                                        incidentLocation = incidentLocation,
                                        description = description
                                    )
                                }
                            }
                    }
                }
            }
    }

    // ─── Show a hydrant change notification ───

    private fun showChangeNotification(title: String, message: String) {
        val prefs = getSharedPreferences("firegrid_prefs", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("notifications_enabled", true) ||
            !prefs.getBoolean("hydrant_updates_enabled", true)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) return
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        try {
            NotificationManagerCompat.from(this)
                .notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // ─── Foreground notification (required to keep service alive) ───

    private fun buildForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("FireGrid Active")
            .setContentText("Monitoring hydrant changes")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel for actual hydrant notifications (high importance)
            val updatesChannel = NotificationChannel(
                CHANNEL_ID,
                "Hydrant Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for hydrant add, edit, and delete changes"
            }
            // Separate silent channel just for the foreground service
            val serviceChannel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "FireGrid Service",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Background service indicator"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(updatesChannel)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}