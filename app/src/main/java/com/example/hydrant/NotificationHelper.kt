package com.example.hydrant

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * NotificationHelper - Utility class for managing notifications in FireGrid app
 * Handles local notifications, FCM token management, and notification preferences
 */
object NotificationHelper {

    private const val TAG = "NotificationHelper"

    // Notification Channel IDs (same as FireGridMessagingService)
    const val CHANNEL_EMERGENCY = "emergency_channel"
    const val CHANNEL_HYDRANT_UPDATES = "hydrant_updates_channel"
    const val CHANNEL_GENERAL = "general_channel"

    // SharedPreferences keys
    private const val PREFS_NAME = "firegrid_notification_prefs"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_EMERGENCY_ENABLED = "emergency_notifications_enabled"
    private const val KEY_HYDRANT_UPDATES_ENABLED = "hydrant_updates_enabled"
    private const val KEY_FCM_TOKEN = "fcm_token"

    /**
     * Initialize notification channels (call this in Application or MainActivity)
     */
    fun initializeNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Emergency Channel (High Priority)
            val emergencyChannel = NotificationChannel(
                CHANNEL_EMERGENCY,
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical fire incident and emergency notifications"
                enableLights(true)
                lightColor = android.graphics.Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setShowBadge(true)
            }

            // Hydrant Updates Channel
            val hydrantChannel = NotificationChannel(
                CHANNEL_HYDRANT_UPDATES,
                "Hydrant Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Fire hydrant status updates and maintenance alerts"
                enableLights(true)
                lightColor = android.graphics.Color.GREEN
                setShowBadge(true)
            }

            // General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications and updates"
                setShowBadge(true)
            }

            notificationManager.createNotificationChannels(
                listOf(emergencyChannel, hydrantChannel, generalChannel)
            )

            Log.d(TAG, "Notification channels created")
        }
    }

    /**
     * Check if notification permission is granted (Android 13+)
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required below Android 13
        }
    }

    /**
     * Show a local notification
     */
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String = CHANNEL_GENERAL,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        if (!hasNotificationPermission(context)) {
            Log.w(TAG, "Notification permission not granted")
            return
        }

        // Create intent to open app
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(
                if (channelId == CHANNEL_EMERGENCY)
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )

        // Set color based on channel
        val color = when (channelId) {
            CHANNEL_EMERGENCY -> 0xFFD32F2F.toInt()
            CHANNEL_HYDRANT_UPDATES -> 0xFF4CAF50.toInt()
            else -> 0xFFFF6B35.toInt()
        }
        notificationBuilder.setColor(color)

        // Add vibration for emergency
        if (channelId == CHANNEL_EMERGENCY) {
            notificationBuilder.setVibrate(longArrayOf(0, 500, 200, 500))
        }

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId, notificationBuilder.build())
                Log.d(TAG, "Notification shown: $title")
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception showing notification: ${e.message}")
            }
        }
    }

    /**
     * Show emergency notification (fire incident)
     */
    fun showEmergencyNotification(
        context: Context,
        title: String,
        message: String,
        location: String? = null
    ) {
        val fullMessage = if (location != null) {
            "$message\n📍 Location: $location"
        } else {
            message
        }

        showNotification(
            context = context,
            title = "🔥 $title",
            message = fullMessage,
            channelId = CHANNEL_EMERGENCY
        )
    }

    /**
     * Show hydrant status update notification
     */
    fun showHydrantUpdateNotification(
        context: Context,
        hydrantId: String,
        status: String,
        municipality: String
    ) {
        val statusEmoji = if (status == "In Service") "✅" else "❌"
        showNotification(
            context = context,
            title = "$statusEmoji Hydrant Status Update",
            message = "Hydrant $hydrantId in $municipality is now $status",
            channelId = CHANNEL_HYDRANT_UPDATES
        )
    }

    /**
     * Get FCM token
     */
    suspend fun getFCMToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FCM token: ${e.message}")
            null
        }
    }

    /**
     * Save FCM token to Firestore
     */
    suspend fun saveFCMTokenToFirestore(token: String) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        val userEmail = auth.currentUser?.email?.lowercase() ?: return

        try {
            val firestore = FirebaseFirestore.getInstance()

            // Update user document with FCM token
            firestore.collection("users")
                .document(userEmail)
                .update(
                    mapOf(
                        "fcmToken" to token,
                        "fcmTokenUpdatedAt" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()

            Log.d(TAG, "FCM token saved to Firestore")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save FCM token: ${e.message}")
        }
    }

    /**
     * Subscribe to topic for receiving broadcast notifications
     */
    fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Subscribed to topic: $topic")
                } else {
                    Log.e(TAG, "Failed to subscribe to topic: $topic")
                }
            }
    }

    /**
     * Unsubscribe from topic
     */
    fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Unsubscribed from topic: $topic")
                } else {
                    Log.e(TAG, "Failed to unsubscribe from topic: $topic")
                }
            }
    }

    /**
     * Subscribe to municipality-specific notifications
     */
    fun subscribeToMunicipality(municipality: String) {
        val topic = "municipality_${municipality.lowercase().replace(" ", "_")}"
        subscribeToTopic(topic)
    }

    /**
     * Subscribe to all emergency notifications
     */
    fun subscribeToEmergencyAlerts() {
        subscribeToTopic("emergency_alerts")
        subscribeToTopic("fire_incidents")
    }

    /**
     * Subscribe to hydrant updates
     */
    fun subscribeToHydrantUpdates() {
        subscribeToTopic("hydrant_updates")
    }

    /**
     * Save notification preferences
     */
    fun saveNotificationPreferences(
        context: Context,
        notificationsEnabled: Boolean,
        emergencyEnabled: Boolean = true,
        hydrantUpdatesEnabled: Boolean = true
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, notificationsEnabled)
            putBoolean(KEY_EMERGENCY_ENABLED, emergencyEnabled)
            putBoolean(KEY_HYDRANT_UPDATES_ENABLED, hydrantUpdatesEnabled)
            apply()
        }

        // Update topic subscriptions based on preferences
        if (notificationsEnabled) {
            if (emergencyEnabled) subscribeToEmergencyAlerts()
            if (hydrantUpdatesEnabled) subscribeToHydrantUpdates()
        } else {
            unsubscribeFromTopic("emergency_alerts")
            unsubscribeFromTopic("fire_incidents")
            unsubscribeFromTopic("hydrant_updates")
        }
    }

    /**
     * Get notification preferences
     */
    fun getNotificationPreferences(context: Context): NotificationPreferences {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return NotificationPreferences(
            notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
            emergencyEnabled = prefs.getBoolean(KEY_EMERGENCY_ENABLED, true),
            hydrantUpdatesEnabled = prefs.getBoolean(KEY_HYDRANT_UPDATES_ENABLED, true)
        )
    }

    /**
     * Cancel a specific notification
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}

/**
 * Data class to hold notification preferences
 */
data class NotificationPreferences(
    val notificationsEnabled: Boolean,
    val emergencyEnabled: Boolean,
    val hydrantUpdatesEnabled: Boolean
)