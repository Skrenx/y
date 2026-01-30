package com.example.hydrant

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirefighterLocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    companion object {
        const val CHANNEL_ID = "firefighter_location_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "START_TRACKING"
        const val ACTION_STOP = "STOP_TRACKING"

        private var isRunning = false

        fun isServiceRunning(): Boolean = isRunning

        fun startTracking(context: Context) {
            val intent = Intent(context, FirefighterLocationService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopTracking(context: Context) {
            val intent = Intent(context, FirefighterLocationService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        setupLocationCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startLocationTracking()
            ACTION_STOP -> stopLocationTracking()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Firefighter Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when your location is being shared with users"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        // Intent to open the app when notification is tapped
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent to stop tracking
        val stopIntent = Intent(this, FirefighterLocationService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🚒 Location Sharing Active")
            .setContentText("Your location is visible to users reporting fires")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop Sharing",
                stopPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    uploadLocationToFirebase(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun startLocationTracking() {
        if (isRunning) {
            Log.d("FirefighterLocation", "Service already running")
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("FirefighterLocation", "No user logged in, stopping service")
            stopSelf()
            return
        }

        // Start foreground service with notification
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                )
            } else {
                startForeground(NOTIFICATION_ID, createNotification())
            }
        } catch (e: Exception) {
            Log.e("FirefighterLocation", "Failed to start foreground service", e)
            stopSelf()
            return
        }

        // Location request - update every 5 seconds
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L // 5 seconds interval
        ).apply {
            setMinUpdateIntervalMillis(3000L) // Minimum 3 seconds between updates
            setWaitForAccurateLocation(false)
            setMaxUpdateDelayMillis(10000L) // Maximum 10 seconds delay
        }.build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            isRunning = true
            Log.d("FirefighterLocation", "Location tracking started for ${currentUser.email}")

            // Also set tracking status in Firebase
            setTrackingStatus(true)

        } catch (e: SecurityException) {
            Log.e("FirefighterLocation", "Location permission not granted", e)
            stopSelf()
        }
    }

    private fun stopLocationTracking() {
        if (!isRunning) {
            stopSelf()
            return
        }

        fusedLocationClient.removeLocationUpdates(locationCallback)
        isRunning = false

        // Update tracking status in Firebase
        setTrackingStatus(false)

        Log.d("FirefighterLocation", "Location tracking stopped")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun uploadLocationToFirebase(latitude: Double, longitude: Double) {
        val currentUser = auth.currentUser ?: return
        val userEmail = currentUser.email?.lowercase() ?: return

        val locationData = hashMapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "timestamp" to System.currentTimeMillis(),
            "updatedAt" to com.google.firebase.Timestamp.now(),
            "isTracking" to true
        )

        firestore.collection("users")
            .document(userEmail)
            .collection("location")
            .document("current")
            .set(locationData)
            .addOnSuccessListener {
                Log.d("FirefighterLocation", "Location updated: $latitude, $longitude")
            }
            .addOnFailureListener { e ->
                Log.e("FirefighterLocation", "Failed to update location", e)
            }
    }

    private fun setTrackingStatus(isTracking: Boolean) {
        val currentUser = auth.currentUser ?: return
        val userEmail = currentUser.email?.lowercase() ?: return

        if (isTracking) {
            // Set tracking status
            firestore.collection("users")
                .document(userEmail)
                .set(
                    hashMapOf(
                        "isAdmin" to true,
                        "isTracking" to true,
                        "trackingStartedAt" to com.google.firebase.Timestamp.now()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                )
        } else {
            // Clear tracking status and location
            firestore.collection("users")
                .document(userEmail)
                .update("isTracking", false)

            // Optionally remove location data when stopping
            firestore.collection("users")
                .document(userEmail)
                .collection("location")
                .document("current")
                .update("isTracking", false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRunning) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isRunning = false
            setTrackingStatus(false)
        }
    }
}