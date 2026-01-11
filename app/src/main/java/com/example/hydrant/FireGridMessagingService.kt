package com.example.hydrant

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * FireGrid Firebase Cloud Messaging Service
 * Handles push notifications for fire incidents, hydrant updates, and system alerts
 */
class FireGridMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FireGridFCM"

        // Notification Channel IDs
        const val CHANNEL_EMERGENCY = "emergency_channel"
        const val CHANNEL_HYDRANT_UPDATES = "hydrant_updates_channel"
        const val CHANNEL_GENERAL = "general_channel"

        // Notification Types
        const val TYPE_FIRE_INCIDENT = "fire_incident"
        const val TYPE_HYDRANT_STATUS = "hydrant_status"
        const val TYPE_MAINTENANCE = "maintenance"
        const val TYPE_GENERAL = "general"
    }

    /**
     * Called when a new FCM token is generated
     * This token should be sent to your server for targeting this device
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token: $token")

        // Send token to your server
        sendTokenToServer(token)
    }

    /**
     * Called when a message is received from FCM
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        // Check if message contains a notification payload
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Notification Title: ${notification.title}")
            Log.d(TAG, "Notification Body: ${notification.body}")

            sendNotification(
                title = notification.title ?: "FireGrid Alert",
                message = notification.body ?: "",
                type = remoteMessage.data["type"] ?: TYPE_GENERAL,
                data = remoteMessage.data
            )
        }

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data Payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
    }

    /**
     * Handle data-only messages (for background processing)
     */
    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: TYPE_GENERAL
        val title = data["title"] ?: "FireGrid Notification"
        val message = data["message"] ?: data["body"] ?: ""

        when (type) {
            TYPE_FIRE_INCIDENT -> {
                // High priority fire incident notification
                sendNotification(
                    title = "🔥 FIRE INCIDENT ALERT",
                    message = message,
                    type = type,
                    data = data,
                    isHighPriority = true
                )
            }
            TYPE_HYDRANT_STATUS -> {
                // Hydrant status change notification
                val hydrantId = data["hydrant_id"] ?: ""
                val status = data["status"] ?: ""
                val municipality = data["municipality"] ?: ""

                sendNotification(
                    title = "Hydrant Status Update",
                    message = "Hydrant $hydrantId in $municipality is now $status",
                    type = type,
                    data = data
                )
            }
            TYPE_MAINTENANCE -> {
                // Scheduled maintenance notification
                sendNotification(
                    title = "🔧 Maintenance Alert",
                    message = message,
                    type = type,
                    data = data
                )
            }
            else -> {
                // General notification
                sendNotification(
                    title = title,
                    message = message,
                    type = type,
                    data = data
                )
            }
        }
    }

    /**
     * Create and display a notification
     */
    private fun sendNotification(
        title: String,
        message: String,
        type: String,
        data: Map<String, String> = emptyMap(),
        isHighPriority: Boolean = false
    ) {
        // Create intent to open the app when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Pass notification data to the activity
            putExtra("notification_type", type)
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Choose notification channel based on type
        val channelId = when (type) {
            TYPE_FIRE_INCIDENT -> CHANNEL_EMERGENCY
            TYPE_HYDRANT_STATUS, TYPE_MAINTENANCE -> CHANNEL_HYDRANT_UPDATES
            else -> CHANNEL_GENERAL
        }

        // Choose notification icon and color based on type
        val (iconColor, priority) = when (type) {
            TYPE_FIRE_INCIDENT -> Pair(0xFFD32F2F.toInt(), NotificationCompat.PRIORITY_HIGH)
            TYPE_HYDRANT_STATUS -> Pair(0xFF4CAF50.toInt(), NotificationCompat.PRIORITY_DEFAULT)
            TYPE_MAINTENANCE -> Pair(0xFFFF9800.toInt(), NotificationCompat.PRIORITY_DEFAULT)
            else -> Pair(0xFFFF6B35.toInt(), NotificationCompat.PRIORITY_DEFAULT)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Make sure to create this icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setColor(iconColor)
            .setPriority(if (isHighPriority) NotificationCompat.PRIORITY_HIGH else priority)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        // Add vibration for emergency notifications
        if (isHighPriority || type == TYPE_FIRE_INCIDENT) {
            notificationBuilder.setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels(notificationManager)
        }

        // Show the notification
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    /**
     * Create notification channels for Android O+
     */
    private fun createNotificationChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            }

            // General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications and updates"
            }

            notificationManager.createNotificationChannels(
                listOf(emergencyChannel, hydrantChannel, generalChannel)
            )
        }
    }

    /**
     * Send FCM token to your backend server
     */
    private fun sendTokenToServer(token: String) {
        // TODO: Implement sending token to your server
        // This allows your server to send targeted notifications to this device

        // Example: Save to Firestore under user's document
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            firestore.collection("users")
                .document(userId)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d(TAG, "FCM Token saved to Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to save FCM token: ${e.message}")
                }
        }
    }
}