package com.example.hydrant

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydrant.ui.theme.HYDRANTTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydrant.viewmodel.AuthViewModel
import com.example.hydrant.viewmodel.FireHydrantViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Directions
import androidx.activity.compose.BackHandler
import com.google.firebase.auth.EmailAuthProvider
import android.content.Intent
import com.google.maps.android.compose.Polyline
import org.json.JSONObject
import java.net.URL
import java.net.HttpURLConnection
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.hydrant.model.FireHydrant
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.FileProvider
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.app.PendingIntent
import android.media.RingtoneManager
import android.content.Context
import androidx.compose.ui.platform.LocalContext


// Weather Data Classes
data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val dt_txt: String
)

data class Main(
    val temp: Double,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double
)

data class Weather(
    val main: String,
    val description: String
)

data class Wind(
    val speed: Double
)

data class DailyWeather(
    val temp: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val windSpeed: Double,
    val condition: String
)

// Directions Data Class
data class DirectionsResult(
    val polylinePoints: List<LatLng>,
    val distance: String,
    val duration: String
)

// Data class for user info
data class UserInfo(
    val uid: String,
    val email: String,
    val displayName: String,
    val isAdmin: Boolean
)

// Fire Incident Alert Data Class
data class FireIncidentAlert(
    val documentId: String,
    val reporterName: String,
    val reporterContact: String,
    val reporterEmail: String,
    val reporterCurrentLocation: String,
    val incidentLocation: String,
    val description: String,
    val timestamp: com.google.firebase.Timestamp?
) {
    fun parseReporterCoordinates(): Pair<Double, Double>? {
        return try {
            val regex = """Lat:\s*([-\d.]+),\s*Lng:\s*([-\d.]+)""".toRegex()
            val match = regex.find(reporterCurrentLocation)
            if (match != null) {
                val lat = match.groupValues[1].toDouble()
                val lng = match.groupValues[2].toDouble()
                Pair(lat, lng)
            } else null
        } catch (e: Exception) { null }
    }
}

// Fire Incident Report for Mail Screen
data class FireIncidentReport(
    val documentId: String,
    val reporterName: String,
    val reporterContact: String,
    val reporterEmail: String,
    val reporterCurrentLocation: String,
    val incidentLocation: String,
    val description: String,
    val timestamp: com.google.firebase.Timestamp?,
    val status: String // "pending", "acknowledged", "resolved"
)

// Weather API Functions
suspend fun fetchWeatherData(municipalityName: String): DailyWeather? {
    val apiKey = "4bc22a06c908152ed3f0a3a89613b138"
    val url = "https://api.openweathermap.org/data/2.5/forecast?q=$municipalityName,Laguna,PH&units=metric&appid=$apiKey"

    return withContext(Dispatchers.IO) {
        try {
            val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                parseForecastJson(response)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun parseForecastJson(json: String): DailyWeather? {
    return try {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        val listPattern = """"list":\[(.*?)\]""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val listMatch = listPattern.find(json) ?: return null
        val listContent = listMatch.groupValues[1]

        val entries = listContent.split("\"dt_txt\"").drop(1)

        var minTemp = Double.MAX_VALUE
        var maxTemp = Double.MIN_VALUE
        var currentTemp = 0.0
        var avgHumidity = 0
        var avgWindSpeed = 0.0
        var condition = "Clear"
        var count = 0
        var tempCount = 0

        for (entry in entries) {
            val dateRegex = """:\s*"([^"]+)"""".toRegex()
            val dateMatch = dateRegex.find(entry)
            val entryDate = dateMatch?.groupValues?.get(1) ?: continue

            if (!entryDate.startsWith(today)) {
                if (count > 0) break
                continue
            }

            val tempRegex = """"temp":\s*([\d.]+)""".toRegex()
            val tempMatches = tempRegex.findAll(entry).toList()

            for (tempMatch in tempMatches) {
                val temp = tempMatch.groupValues[1].toDoubleOrNull() ?: continue
                minTemp = minOf(minTemp, temp)
                maxTemp = maxOf(maxTemp, temp)
                if (tempCount == 0) currentTemp = temp
                tempCount++
            }

            val tempMinRegex = """"temp_min":\s*([\d.]+)""".toRegex()
            tempMinRegex.findAll(entry).forEach { match ->
                val tMin = match.groupValues[1].toDoubleOrNull()
                if (tMin != null) minTemp = minOf(minTemp, tMin)
            }

            val tempMaxRegex = """"temp_max":\s*([\d.]+)""".toRegex()
            tempMaxRegex.findAll(entry).forEach { match ->
                val tMax = match.groupValues[1].toDoubleOrNull()
                if (tMax != null) maxTemp = maxOf(maxTemp, tMax)
            }

            val humidityRegex = """"humidity":\s*(\d+)""".toRegex()
            val humidity = humidityRegex.find(entry)?.groupValues?.get(1)?.toIntOrNull() ?: 0

            val windRegex = """"speed":\s*([\d.]+)""".toRegex()
            val windSpeed = windRegex.find(entry)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0

            if (count == 0) {
                val weatherRegex = """"main":\s*"([^"]+)"""".toRegex()
                condition = weatherRegex.find(entry)?.groupValues?.get(1) ?: "Clear"
            }

            avgHumidity += humidity
            avgWindSpeed += windSpeed
            count++
        }

        if (count == 0 || minTemp == Double.MAX_VALUE || maxTemp == Double.MIN_VALUE) {
            val tempRegex = """"temp":\s*([\d.]+)""".toRegex()
            val temp = tempRegex.find(json)?.groupValues?.get(1)?.toDoubleOrNull() ?: 28.0

            val tempMinRegex = """"temp_min":\s*([\d.]+)""".toRegex()
            minTemp = tempMinRegex.find(json)?.groupValues?.get(1)?.toDoubleOrNull() ?: (temp - 3)

            val tempMaxRegex = """"temp_max":\s*([\d.]+)""".toRegex()
            maxTemp = tempMaxRegex.find(json)?.groupValues?.get(1)?.toDoubleOrNull() ?: (temp + 3)

            val humidityRegex = """"humidity":\s*(\d+)""".toRegex()
            avgHumidity = humidityRegex.find(json)?.groupValues?.get(1)?.toIntOrNull() ?: 70

            val windRegex = """"speed":\s*([\d.]+)""".toRegex()
            avgWindSpeed = windRegex.find(json)?.groupValues?.get(1)?.toDoubleOrNull() ?: 3.0

            val weatherRegex = """"main":\s*"([^"]+)"""".toRegex()
            condition = weatherRegex.find(json)?.groupValues?.get(1) ?: "Clear"

            currentTemp = temp
            count = 1
        } else {
            avgHumidity /= count
            avgWindSpeed /= count
        }

        DailyWeather(
            temp = if (currentTemp > 0) currentTemp else (minTemp + maxTemp) / 2,
            tempMin = minTemp,
            tempMax = maxTemp,
            humidity = avgHumidity,
            windSpeed = avgWindSpeed,
            condition = condition
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Directions API Function
suspend fun fetchDirections(origin: LatLng, destination: LatLng): DirectionsResult? {
    val apiKey = "AIzaSyCdwrCeDEi3tPzjg6-2lGNueLJqHJFv1ZQ"
    val url = "https://maps.googleapis.com/maps/api/directions/json?" +
            "origin=${origin.latitude},${origin.longitude}" +
            "&destination=${destination.latitude},${destination.longitude}" +
            "&mode=driving" +
            "&key=$apiKey"

    return withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                parseDirectionsJson(response)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun parseDirectionsJson(json: String): DirectionsResult? {
    return try {
        val jsonObject = JSONObject(json)
        val status = jsonObject.getString("status")

        if (status != "OK") {
            return null
        }

        val routes = jsonObject.getJSONArray("routes")
        if (routes.length() == 0) return null

        val route = routes.getJSONObject(0)
        val overviewPolyline = route.getJSONObject("overview_polyline")
        val encodedPoints = overviewPolyline.getString("points")

        val legs = route.getJSONArray("legs")
        val leg = legs.getJSONObject(0)
        val distance = leg.getJSONObject("distance").getString("text")
        val duration = leg.getJSONObject("duration").getString("text")

        val decodedPath = decodePolyline(encodedPoints)

        DirectionsResult(
            polylinePoints = decodedPath,
            distance = distance,
            duration = duration
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun decodePolyline(encoded: String): List<LatLng> {
    val poly = mutableListOf<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
        poly.add(p)
    }
    return poly
}

// Add this AFTER the decodePolyline function (around line 350)

fun logHydrantChange(
    context: Context,
    changeType: String, // "added", "updated", "deleted"
    hydrantId: String,
    hydrantName: String = "",
    newStatus: String = "",
    municipality: String,
    performedBy: String
) {
    val firestore = Firebase.firestore

    val changeData = hashMapOf(
        "changeType" to changeType,
        "hydrantId" to hydrantId,
        "hydrantName" to hydrantName,
        "newStatus" to newStatus,
        "municipality" to municipality,
        "performedBy" to performedBy,
        "timestamp" to com.google.firebase.Timestamp.now(),
        "notified" to false
    )

    firestore.collection("hydrant_changes")
        .add(changeData)
        .addOnSuccessListener { documentReference ->
            android.util.Log.d("HydrantChange", "Change logged: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            android.util.Log.e("HydrantChange", "Error logging change", e)
        }
}

fun submitFireIncidentReport(
    context: Context,
    reporterName: String,
    reporterContact: String,
    reporterEmail: String,
    reporterCurrentLocation: String,
    incidentLocation: String,
    description: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val firestore = Firebase.firestore

    val reportData = hashMapOf(
        "reporterName" to reporterName,
        "reporterContact" to reporterContact,
        "reporterEmail" to reporterEmail,
        "reporterCurrentLocation" to reporterCurrentLocation,
        "incidentLocation" to incidentLocation,
        "description" to description,
        "timestamp" to com.google.firebase.Timestamp.now(),
        "status" to "pending", // pending, acknowledged, resolved
        "notified" to false
    )

    firestore.collection("fire_incident_reports")
        .add(reportData)
        .addOnSuccessListener { documentReference ->
            android.util.Log.d("FireIncident", "Report submitted: ${documentReference.id}")
            onSuccess()
        }
        .addOnFailureListener { e ->
            android.util.Log.e("FireIncident", "Error submitting report", e)
            onFailure(e.message ?: "Unknown error")
        }
}

/**
 * Show fire incident notification to admins
 */
fun showFireIncidentNotification(
    context: Context,
    reporterName: String,
    incidentLocation: String,
    description: String
) {
    // Check permission first (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    // Check if notifications are enabled
    val prefs = context.getSharedPreferences("firegrid_prefs", Context.MODE_PRIVATE)
    val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
    val emergencyEnabled = prefs.getBoolean("emergency_enabled", true)

    if (!notificationsEnabled || !emergencyEnabled) {
        return
    }

    val title = "🔥 FIRE INCIDENT REPORTED!"
    val message = "Reporter: $reporterName\nLocation: $incidentLocation"

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notification = NotificationCompat.Builder(context, "emergency_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("$message\n\nDescription: ${description.take(100)}${if (description.length > 100) "..." else ""}"))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setSound(soundUri)
        .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
        .setColor(0xFFD32F2F.toInt())
        .build()

    with(NotificationManagerCompat.from(context)) {
        try {
            notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

fun extractHydrantNumber(hydrantId: String): Int {
    return try {
        val numberPart = hydrantId
            .replace("Id_", "", ignoreCase = true)
            .replace("Hydrant Id_", "", ignoreCase = true)
            .trim()
        numberPart.toIntOrNull() ?: Int.MAX_VALUE
    } catch (e: Exception) {
        Int.MAX_VALUE
    }
}

fun validatePassword(password: String): String? {
    return when {
        password.length < 12 -> "Password must be at least 12 characters"
        !password.any { it.isUpperCase() } -> "Password must contain at least 1 uppercase letter"
        !password.any { it.isDigit() } -> "Password must contain at least 1 number"
        !password.any { !it.isLetterOrDigit() } -> "Password must contain at least 1 special character"
        else -> null
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Start the logout service
        startService(Intent(this, LogoutService::class.java))

        // ✅ ADD THIS LINE: Start the hydrant change listener service
        startService(Intent(this, HydrantChangeListenerService::class.java))

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HYDRANTTheme {
                HYDRANTApp()
            }
        }
    }
}



@PreviewScreenSizes
@Composable
fun HYDRANTApp() {
    val auth = FirebaseAuth.getInstance()
    // ✅ FIX: Check if user is ACTUALLY logged in by verifying email is verified or user exists
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var showSignUp by rememberSaveable { mutableStateOf(false) }
    var signUpSuccessMessage by rememberSaveable { mutableStateOf<String?>(null) }

    // ✅ FIX: Check auth state on app start
    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        isLoggedIn = currentUser != null && currentUser.email != null
    }

    val userName = auth.currentUser?.displayName ?: "Full Name"

    when {
        isLoggedIn -> MainApp(
            userName = userName,
            onLogout = {
                isLoggedIn = false
            }
        )
        showSignUp -> SignUpScreen(
            onSignUpSuccess = {
                // This callback is no longer used since we redirect to login
            },
            onBackToLogin = {
                showSignUp = false
            },
            onSignUpComplete = { message ->  // NEW: Added this callback
                // New callback for successful sign-up that redirects to login
                signUpSuccessMessage = message
                showSignUp = false
            }
        )
        else -> LoginScreen(
            onLoginSuccess = {
                isLoggedIn = true
                signUpSuccessMessage = null  // NEW: Clear the message after login
            },
            onNavigateToSignUp = { showSignUp = true },
            successMessage = signUpSuccessMessage  // NEW: Pass the success message
        )
    }
}


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    successMessage: String? = null
) {
    val viewModel: AuthViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var showForgotPasswordDialog by rememberSaveable { mutableStateOf(false) }
    var resetEmailSent by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf("") }
        var dialogError by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showForgotPasswordDialog = false
                    resetEmailSent = false
                    dialogError = ""
                }
            },
            title = { Text("Reset Password") },
            text = {
                Column {
                    if (resetEmailSent) {
                        Text("Password reset link sent! Check your email.")
                    } else {
                        Text("Enter your email address and we'll send you a password reset link.")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = {
                                resetEmail = it
                                dialogError = ""
                            },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            enabled = !isLoading,
                            isError = dialogError.isNotEmpty()
                        )
                        if (dialogError.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dialogError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (resetEmailSent) {
                    TextButton(
                        onClick = {
                            showForgotPasswordDialog = false
                            resetEmailSent = false
                        }
                    ) {
                        Text("OK")
                    }
                } else {
                    TextButton(
                        onClick = {
                            if (resetEmail.isNotBlank()) {
                                isLoading = true
                                FirebaseAuth.getInstance().sendPasswordResetEmail(resetEmail)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        resetEmailSent = true
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        dialogError = when {
                                            e.message?.contains("user") == true -> "No account found with this email"
                                            e.message?.contains("network") == true -> "Network error. Check your connection"
                                            else -> "Failed to send reset email"
                                        }
                                    }
                            } else {
                                dialogError = "Please enter your email"
                            }
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Send Reset Link")
                        }
                    }
                }
            },
            dismissButton = {
                if (!resetEmailSent) {
                    TextButton(
                        onClick = {
                            showForgotPasswordDialog = false
                            dialogError = ""
                        },
                        enabled = !isLoading
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .systemBarsPadding()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Top Logo Row: app_logo + "FireGrid" text + second_logo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left logo (app_logo.png) - 80dp
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(80.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // FireGrid text - reduced size
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xFFFF6B35))) {
                                    append("Fire")
                                }
                                withStyle(style = SpanStyle(color = Color(0xFFFF4500))) {
                                    append("Grid")
                                }
                            },
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 30.sp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Right logo (second_logo.png) - 100dp (MUCH LARGER than left and text)
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "Second Logo",
                            modifier = Modifier.size(80.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (uiState.error != null) {
                                viewModel.clearError()
                            }
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !uiState.isLoading,
                        isError = uiState.error != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (uiState.error != null) {
                                viewModel.clearError()
                            }
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "👁" else "👁‍🗨️",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        isError = uiState.error != null
                    )

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Login Error",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD32F2F)
                                )
                                Text(
                                    text = uiState.error!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            showForgotPasswordDialog = true
                        }) {
                            Text(
                                "Forgot Password?",
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.signIn(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B35)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Login")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onNavigateToSignUp) {
                        Text(
                            "Don't have an account? Sign Up",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Bottom BFP Logo - positioned at the absolute bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bfp_logo),
                        contentDescription = "BFP Logo",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    onSignUpComplete: (String) -> Unit = {}
) {
    val viewModel: AuthViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    var firstName by rememberSaveable { mutableStateOf("") }
    var middleName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var localError by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    // Helper function to validate name fields
    fun isValidName(name: String): Boolean {
        // Name must contain at least 2 letters and only letters, spaces, hyphens, or apostrophes
        val trimmedName = name.trim()
        if (trimmedName.length < 2) return false
        // Check if it contains at least 2 letters
        val letterCount = trimmedName.count { it.isLetter() }
        if (letterCount < 2) return false
        // Check if all characters are valid (letters, spaces, hyphens, apostrophes)
        return trimmedName.all { it.isLetter() || it == ' ' || it == '-' || it == '\'' }
    }

    // Helper function to validate email format
    fun isValidEmail(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email.trim()).matches()
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            val user = FirebaseAuth.getInstance().currentUser
            val fullName = if (middleName.isNotBlank()) {
                "${firstName.trim()} ${middleName.trim()} ${lastName.trim()}"
            } else {
                "${firstName.trim()} ${lastName.trim()}"
            }
            val profileUpdates = userProfileChangeRequest {
                displayName = fullName.trim()
            }
            user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                // Save user to Firestore - USING EMAIL AS DOCUMENT ID
                user.let { firebaseUser ->
                    val emailKey = firebaseUser.email?.lowercase() ?: "" // Get email as key

                    val userData = hashMapOf(
                        "uid" to firebaseUser.uid,
                        "email" to emailKey,
                        "displayName" to fullName.trim(),
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )

                    Firebase.firestore.collection("users")
                        .document(emailKey)  // ✅ Use EMAIL as document ID instead of UID
                        .set(userData)
                        .addOnCompleteListener {
                            FirebaseAuth.getInstance().signOut()
                            viewModel.resetAuthState()
                            onSignUpComplete("Account created successfully! Please login.")
                        }
                }
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(padding)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { Spacer(modifier = Modifier.height(32.dp)) }

                item {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFFFF6B35))) {
                                append("Fire")
                            }
                            withStyle(style = SpanStyle(color = Color(0xFFFF4500))) {
                                append("Grid")
                            }
                        },
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // First Name
                item {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            localError = ""
                        },
                        label = { Text("First Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            focusedLabelColor = Color(0xFFFF6B35)
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Text
                        )
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // Middle Name (Optional)
                item {
                    OutlinedTextField(
                        value = middleName,
                        onValueChange = {
                            middleName = it
                            localError = ""
                        },
                        label = { Text("Middle Name (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            focusedLabelColor = Color(0xFFFF6B35)
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Text
                        )
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // Last Name
                item {
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            localError = ""
                        },
                        label = { Text("Last Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            focusedLabelColor = Color(0xFFFF6B35)
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Text
                        )
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // Email
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            localError = ""
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            focusedLabelColor = Color(0xFFFF6B35)
                        )
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // Password field in SignUpScreen (around line 1096)
                item {
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it.filter { char -> !char.isWhitespace() } // ✅ Filter out spaces
                            localError = ""
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "👁" else "👁‍🗨️",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            focusedLabelColor = Color(0xFFFF6B35)
                        )
                    )
                }

                item {
                    Text(
                        text = "Password must have: 12 characters, 1 uppercase, 1 number, 1 special character",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

// Confirm Password field in SignUpScreen (around line 1131)
                item {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it.filter { char -> !char.isWhitespace() } // ✅ Filter out spaces
                            localError = ""
                        },
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Text(
                                    text = if (confirmPasswordVisible) "👁" else "👁‍🗨️",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            focusedLabelColor = Color(0xFFFF6B35)
                        )
                    )
                }

                if (localError.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item {
                        Text(
                            text = localError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    Button(
                        onClick = {
                            when {
                                firstName.isBlank() -> {
                                    localError = "First name is required"
                                }
                                !isValidName(firstName) -> {
                                    localError = "First name must contain at least 2 letters and only letters"
                                }
                                lastName.isBlank() -> {
                                    localError = "Last name is required"
                                }
                                !isValidName(lastName) -> {
                                    localError = "Last name must contain at least 2 letters and only letters"
                                }
                                middleName.isNotBlank() && !isValidName(middleName) -> {
                                    localError = "Middle name must contain at least 2 letters and only letters"
                                }
                                email.isBlank() -> {
                                    localError = "Email is required"
                                }
                                !isValidEmail(email) -> {
                                    localError = "Please enter a valid email address"
                                }
                                password.isBlank() -> {
                                    localError = "Password is required"
                                }
                                confirmPassword.isBlank() -> {
                                    localError = "Please confirm your password"
                                }
                                password != confirmPassword -> {
                                    localError = "Passwords do not match"
                                }
                                else -> {
                                    val passwordValidationError = validatePassword(password)
                                    if (passwordValidationError != null) {
                                        localError = passwordValidationError
                                    } else {
                                        localError = ""
                                        viewModel.signUp(email.trim(), password)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B35)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Sign Up")
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    TextButton(onClick = onBackToLogin) {
                        Text(
                            "Already have an account? Login",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun MainApp(userName: String, onLogout: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""
    val firestore = Firebase.firestore

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var selectedMunicipality by rememberSaveable { mutableStateOf<String?>(null) }
    var showAddHydrantScreen by rememberSaveable { mutableStateOf(false) }
    var showHydrantListScreen by rememberSaveable { mutableStateOf(false) }
    var showHydrantMapScreen by rememberSaveable { mutableStateOf(false) }
    var selectedHydrantForEdit by remember { mutableStateOf<FireHydrant?>(null) }
    var selectedHydrantForView by remember { mutableStateOf<FireHydrant?>(null) }
    var showHelpFaqScreen by rememberSaveable { mutableStateOf(false) }
    var showContactSupportScreen by rememberSaveable { mutableStateOf(false) }
    var showReportProblemScreen by rememberSaveable { mutableStateOf(false) }
    var showAboutAppScreen by rememberSaveable { mutableStateOf(false) }
    var showTermsPrivacyScreen by rememberSaveable { mutableStateOf(false) }
    var showSettingsScreen by rememberSaveable { mutableStateOf(false) }
    var showAddAdminScreen by rememberSaveable { mutableStateOf(false) }
    var showRemoveAdminScreen by rememberSaveable { mutableStateOf(false) }
    var showInvalidCoordinatesOnly by rememberSaveable { mutableStateOf(false) }

    // Fire Incident Alert States
    var showFireIncidentAlert by remember { mutableStateOf(false) }
    var currentFireIncident by remember { mutableStateOf<FireIncidentAlert?>(null) }
    var navigateToMapWithIncident by remember { mutableStateOf(false) }
    var incidentLocationForMap by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isAdminForIncident by remember { mutableStateOf(false) }
    var showMailScreen by rememberSaveable { mutableStateOf(false) }

    var backPressedOnce by remember { mutableStateOf(false) }

    val favoritesScrollState = androidx.compose.foundation.lazy.rememberLazyListState()

    LaunchedEffect(backPressedOnce) {
        if (backPressedOnce) {
            kotlinx.coroutines.delay(2000)
            backPressedOnce = false
        }
    }

    // ✅ ADD THIS - Check if user is admin and listen for fire incident reports
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        isAdminForIncident = true

                        // Listen for new pending fire incident reports
                        firestore.collection("fire_incident_reports")
                            .whereEqualTo("status", "pending")
                            .whereEqualTo("notified", false)
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    android.util.Log.e("FireIncident", "Listen failed", error)
                                    return@addSnapshotListener
                                }

                                snapshot?.documentChanges?.forEach { change ->
                                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                                        val doc = change.document
                                        val incident = FireIncidentAlert(
                                            documentId = doc.id,
                                            reporterName = doc.getString("reporterName") ?: "",
                                            reporterContact = doc.getString("reporterContact") ?: "",
                                            reporterEmail = doc.getString("reporterEmail") ?: "",
                                            reporterCurrentLocation = doc.getString("reporterCurrentLocation") ?: "",
                                            incidentLocation = doc.getString("incidentLocation") ?: "",
                                            description = doc.getString("description") ?: "",
                                            timestamp = doc.getTimestamp("timestamp")
                                        )

                                        // Mark as notified so it doesn't show again
                                        firestore.collection("fire_incident_reports")
                                            .document(doc.id)
                                            .update("notified", true)

                                        // Show the alert dialog
                                        currentFireIncident = incident
                                        showFireIncidentAlert = true

                                        // Also show push notification
                                        showFireIncidentNotification(
                                            context = context,
                                            reporterName = incident.reporterName,
                                            incidentLocation = incident.incidentLocation,
                                            description = incident.description
                                        )
                                    }
                                }
                            }
                    } else {
                        isAdminForIncident = false
                    }
                }
                .addOnFailureListener {
                    isAdminForIncident = false
                }
        }
    }

    when {
        showHelpFaqScreen -> {
            BackHandler { showHelpFaqScreen = false }
            HelpFaqScreen(onBack = { showHelpFaqScreen = false })
        }
        showContactSupportScreen -> {
            BackHandler { showContactSupportScreen = false }
            ContactSupportScreen(onBack = { showContactSupportScreen = false })
        }
        showReportProblemScreen -> {
            BackHandler { showReportProblemScreen = false }
            ReportProblemScreen(onBack = { showReportProblemScreen = false })
        }
        showAboutAppScreen -> {
            BackHandler { showAboutAppScreen = false }
            AboutAppScreen(onBack = { showAboutAppScreen = false })
        }
        showTermsPrivacyScreen -> {
            BackHandler { showTermsPrivacyScreen = false }
            TermsPrivacyScreen(onBack = { showTermsPrivacyScreen = false })
        }
        showAddAdminScreen -> {
            BackHandler {
                showAddAdminScreen = false
                showSettingsScreen = true
            }
            AddAdminScreen(
                onBack = {
                    showAddAdminScreen = false
                    showSettingsScreen = true
                },
                onSuccess = { email ->
                    showAddAdminScreen = false
                    showSettingsScreen = true
                    android.widget.Toast.makeText(
                        context,
                        "$email has been promoted to admin!",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
        showRemoveAdminScreen -> {
            BackHandler {
                showRemoveAdminScreen = false
                showSettingsScreen = true
            }
            RemoveAdminScreen(
                onBack = {
                    showRemoveAdminScreen = false
                    showSettingsScreen = true
                },
                onSuccess = { email ->
                    showRemoveAdminScreen = false
                    showSettingsScreen = true
                    android.widget.Toast.makeText(
                        context,
                        "$email has been removed from admin!",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
        showSettingsScreen -> {
            BackHandler { showSettingsScreen = false }
            SettingsScreen(
                onBack = { showSettingsScreen = false },
                onAddAdminClick = {
                    showSettingsScreen = false
                    showAddAdminScreen = true
                },
                onRemoveAdminClick = {
                    showSettingsScreen = false
                    showRemoveAdminScreen = true
                }
            )
        }

        showMailScreen -> {
            BackHandler { showMailScreen = false }
            FireIncidentReportsScreen(
                onBack = { showMailScreen = false },
                onViewOnMap = { lat, lng ->
                    showMailScreen = false
                    incidentLocationForMap = Pair(lat, lng)
                    navigateToMapWithIncident = true
                    currentDestination = AppDestinations.MAP
                }
            )
        }

        selectedHydrantForView != null -> {
            BackHandler { selectedHydrantForView = null }
            ViewHydrantLocationScreen(
                hydrant = selectedHydrantForView!!,
                onBack = { selectedHydrantForView = null }
            )
        }
        selectedHydrantForEdit != null && selectedMunicipality != null -> {
            BackHandler { selectedHydrantForEdit = null }
            EditFireHydrantScreen(
                hydrant = selectedHydrantForEdit!!,
                onBack = { selectedHydrantForEdit = null },
                onSuccess = { selectedHydrantForEdit = null },
                onDelete = { selectedHydrantForEdit = null }
            )
        }
        showHydrantMapScreen && selectedMunicipality != null -> {
            BackHandler {
                showHydrantMapScreen = false
                // ✅ FIX: DON'T clear selectedMunicipality
            }
            FireHydrantMapScreen(
                municipalityName = selectedMunicipality!!,
                onBack = {
                    showHydrantMapScreen = false
                    // ✅ FIX: DON'T clear selectedMunicipality
                },
                onShowInvalidCoordinates = {
                    showInvalidCoordinatesOnly = true
                    showHydrantMapScreen = false
                    showHydrantListScreen = true
                }
            )
        }
        showHydrantListScreen && selectedMunicipality != null -> {
            BackHandler {
                showHydrantListScreen = false
                showInvalidCoordinatesOnly = false
                // ✅ FIX: DON'T clear selectedMunicipality - just hide the list screen
            }
            FireHydrantListScreen(
                municipalityName = selectedMunicipality!!,
                onBack = {
                    showHydrantListScreen = false
                    showInvalidCoordinatesOnly = false
                    // ✅ FIX: DON'T clear selectedMunicipality - just hide the list screen
                },
                onEditHydrant = { hydrant ->
                    selectedHydrantForEdit = hydrant
                },
                onViewLocation = { hydrant ->
                    selectedHydrantForView = hydrant
                },
                showInvalidCoordinatesOnly = showInvalidCoordinatesOnly,
                onClearInvalidFilter = { showInvalidCoordinatesOnly = false }
            )
        }
        showAddHydrantScreen && selectedMunicipality != null -> {
            BackHandler {
                showAddHydrantScreen = false
                // ✅ FIX: DON'T clear selectedMunicipality
            }
            AddFireHydrantScreen(
                municipalityName = selectedMunicipality!!,
                onBack = {
                    showAddHydrantScreen = false
                    // ✅ FIX: DON'T clear selectedMunicipality
                },
                onSuccess = { showAddHydrantScreen = false }
            )
        }
        selectedMunicipality != null -> {
            BackHandler {
                selectedMunicipality = null  // ✅ CORRECT: Clear municipality to go back to Home
            }
            MunicipalityDetailScreen(
                municipalityName = selectedMunicipality!!,
                onBack = {
                    selectedMunicipality = null  // ✅ CORRECT: Clear municipality to go back to Home
                },
                onAddHydrant = { showAddHydrantScreen = true },
                onShowHydrantList = { showHydrantListScreen = true },
                onShowHydrantMap = { showHydrantMapScreen = true }
            )
        }
        else -> {
            BackHandler {
                if (backPressedOnce) {
                    (context as? ComponentActivity)?.finish()
                } else {
                    backPressedOnce = true
                    android.widget.Toast.makeText(
                        context,
                        "Press back again to exit",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Fire Incident Alert Dialog
            if (showFireIncidentAlert && currentFireIncident != null) {
                FireIncidentAlertDialog(
                    incident = currentFireIncident!!,
                    onDismiss = {
                        showFireIncidentAlert = false
                        currentFireIncident = null
                    },
                    onAcknowledge = {
                        Firebase.firestore.collection("fire_incident_reports")
                            .document(currentFireIncident!!.documentId)
                            .update("status", "acknowledged")
                        showFireIncidentAlert = false
                        currentFireIncident = null
                    },
                    onGoToLocation = {
                        val coords = currentFireIncident!!.parseReporterCoordinates()
                        if (coords != null) {
                            incidentLocationForMap = coords
                            navigateToMapWithIncident = true
                            currentDestination = AppDestinations.MAP
                        }
                        showFireIncidentAlert = false
                        currentFireIncident = null
                    }
                )
            }

            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    AppDestinations.entries.forEach { destination ->
                        item(
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.label
                                )
                            },
                            label = { Text(destination.label) },
                            selected = destination == currentDestination,
                            onClick = { currentDestination = destination }
                        )
                    }
                }
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentDestination) {
                        AppDestinations.HOME -> FavoritesScreen(
                            modifier = Modifier.padding(innerPadding),
                            scrollState = favoritesScrollState,
                            onMunicipalityClick = { municipality ->
                                selectedMunicipality = municipality
                            }
                        )
                        AppDestinations.MAP -> MapScreen(
                            modifier = Modifier.padding(innerPadding),
                            initialIncidentLocation = if (navigateToMapWithIncident) incidentLocationForMap else null,
                            onIncidentHandled = {
                                navigateToMapWithIncident = false
                                incidentLocationForMap = null
                            },
                            onMailClick = { showMailScreen = true }
                        )
                        AppDestinations.PROFILE -> ProfileScreen(
                            modifier = Modifier.padding(innerPadding),
                            userName = userName,
                            userEmail = userEmail,
                            onLogout = onLogout,
                            onSettingsClick = { showSettingsScreen = true },
                            onHelpFaqClick = { showHelpFaqScreen = true },
                            onContactSupportClick = { showContactSupportScreen = true },
                            onReportProblemClick = { showReportProblemScreen = true },
                            onAboutAppClick = { showAboutAppScreen = true },
                            onTermsPrivacyClick = { showTermsPrivacyScreen = true }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FireHydrantListScreen(
    municipalityName: String,
    onBack: () -> Unit,
    onEditHydrant: (FireHydrant) -> Unit,
    onViewLocation: (FireHydrant) -> Unit,
    showInvalidCoordinatesOnly: Boolean = false,
    onClearInvalidFilter: () -> Unit = {}
) {
    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filterExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    // ✅ ADD THESE LINES - Check if user is admin
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""
    val firestore = Firebase.firestore
    var isAdmin by remember { mutableStateOf(false) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    // ✅ ADD THIS LaunchedEffect - Check admin status
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { document ->
                    isAdmin = document.exists()
                    isCheckingAdmin = false
                }
                .addOnFailureListener {
                    isAdmin = false
                    isCheckingAdmin = false
                }
        } else {
            isAdmin = false
            isCheckingAdmin = false
        }
    }

    LaunchedEffect(municipalityName) {
        hydrantViewModel.loadHydrants(municipalityName)
    }

    // Helper function to check if coordinates are invalid
    fun hasInvalidCoordinates(hydrant: FireHydrant): Boolean {
        val lat = hydrant.latitude.toDoubleOrNull()
        val lng = hydrant.longitude.toDoubleOrNull()
        return lat == null || lng == null ||
                lat < -90 || lat > 90 ||
                lng < -180 || lng > 180 ||
                (lat == 0.0 && lng == 0.0)
    }

    val filteredHydrants = hydrantUiState.hydrants.filter { hydrant ->
        // Search ALL fields EXCEPT municipality (already filtered by selection from Home)
        val matchesSearch = searchQuery.isEmpty() ||
                hydrant.hydrantName.contains(searchQuery, ignoreCase = true) ||
                hydrant.exactLocation.contains(searchQuery, ignoreCase = true) ||
                hydrant.id.contains(searchQuery, ignoreCase = true) ||
                hydrant.typeColor.contains(searchQuery, ignoreCase = true) ||
                hydrant.serviceStatus.contains(searchQuery, ignoreCase = true) ||
                hydrant.latitude.contains(searchQuery, ignoreCase = true) ||
                hydrant.longitude.contains(searchQuery, ignoreCase = true) ||
                hydrant.remarks.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "In Service" -> hydrant.serviceStatus == "In Service"
            "Out of Service" -> hydrant.serviceStatus == "Out of Service"
            else -> true
        }

        val matchesInvalidFilter = if (showInvalidCoordinatesOnly) {
            hasInvalidCoordinates(hydrant)
        } else {
            true
        }

        matchesSearch && matchesFilter && matchesInvalidFilter
    }.sortedBy { extractHydrantNumber(it.id) }

    // Reusable Filter Dropdown Menu content
    @Composable
    fun FilterDropdownContent() {
        DropdownMenuItem(
            text = { Text("All") },
            onClick = {
                selectedFilter = "All"
                filterExpanded = false
            }
        )
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("In Service")
                    if (selectedFilter == "In Service") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            onClick = {
                selectedFilter = "In Service"
                filterExpanded = false
            }
        )
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Out of Service")
                    if (selectedFilter == "Out of Service") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            onClick = {
                selectedFilter = "Out of Service"
                filterExpanded = false
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (showInvalidCoordinatesOnly && isSearchActive) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text("Search", color = Color.Gray)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.Gray
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            searchQuery = ""
                                            isSearchActive = false
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close search",
                                            tint = Color.Gray
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent
                                )
                            )
                        } else {
                            Text(
                                if (showInvalidCoordinatesOnly) "Invalid Coordinates" else "Fire Hydrant Lists",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            // ✅ FIX: Clear search and filters before going back
                            searchQuery = ""
                            isSearchActive = false
                            selectedFilter = "All"
                            onBack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        if (showInvalidCoordinatesOnly && !isSearchActive) {
                            IconButton(
                                onClick = { isSearchActive = true },
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(40.dp)
                                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFFEF5350)
                                )
                            }

                            Box {
                                IconButton(
                                    onClick = { filterExpanded = true },
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Filter",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = filterExpanded,
                                    onDismissRequest = { filterExpanded = false }
                                ) {
                                    FilterDropdownContent()
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFEF5350)
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Search bar and Filter button - Only for regular Fire Hydrant Lists
            if (!showInvalidCoordinatesOnly) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Search", color = Color.Gray)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFFEF5350)
                        )
                    )

                    Box {
                        Button(
                            onClick = { filterExpanded = true },
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF424242)
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Filter",
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Filter options",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = { filterExpanded = false }
                        ) {
                            FilterDropdownContent()
                        }
                    }
                }
            }

            // Warning banner for invalid coordinates filter
            if (showInvalidCoordinatesOnly) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Showing hydrants with invalid coordinates",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "Please update the coordinates for these hydrants",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFF8F00)
                                )
                            }
                        }
                        IconButton(
                            onClick = onClearInvalidFilter,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear filter",
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Show active filter indicator if not "All"
            if (selectedFilter != "All" && !showInvalidCoordinatesOnly) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter: $selectedFilter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1976D2)
                        )
                        IconButton(
                            onClick = { selectedFilter = "All" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear filter",
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (hydrantUiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFEF5350))
                }
            } else if (filteredHydrants.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (showInvalidCoordinatesOnly) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.Search
                            },
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = if (showInvalidCoordinatesOnly) Color(0xFF4CAF50) else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (showInvalidCoordinatesOnly) {
                                "Great! All hydrants have valid coordinates"
                            } else if (searchQuery.isNotEmpty() || selectedFilter != "All") {
                                "No hydrants match your search/filter"
                            } else {
                                "No fire hydrants found in $municipalityName"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        if (showInvalidCoordinatesOnly) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onClearInvalidFilter,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Text("Show All Hydrants")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredHydrants) { hydrant ->
                        FireHydrantCard(
                            hydrant = hydrant,
                            onEditClick = { onEditHydrant(hydrant) },
                            onViewLocationClick = { onViewLocation(hydrant) },
                            showInvalidWarning = hasInvalidCoordinates(hydrant),
                            isAdmin = isAdmin,  // ✅ ADD THIS LINE
                            isCheckingAdmin = isCheckingAdmin  // ✅ ADD THIS LINE
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// Also update the FireHydrantCard to fix the warning icon:

@Composable
fun FireHydrantCard(
    hydrant: FireHydrant,
    onEditClick: () -> Unit,
    onViewLocationClick: () -> Unit,
    showInvalidWarning: Boolean = false,
    isAdmin: Boolean = false,
    isCheckingAdmin: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (showInvalidWarning) Color(0xFFFFF8E1) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        border = if (showInvalidWarning) BorderStroke(2.dp, Color(0xFFFFB300)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Invalid coordinates warning banner at top of card
            // ✅ UPDATED: Different message for admin vs regular user
            if (showInvalidWarning) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    color = Color(0xFFFFE082),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Warning icon using emoji - simple and reliable
                        Text(
                            text = "⚠️",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (!isCheckingAdmin && isAdmin) {
                                "Invalid coordinates - Please update"  // ✅ Admin message
                            } else {
                                "Invalid coordinates"  // ✅ Regular user message
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            // Header Row - Title and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hydrant ${hydrant.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = if (hydrant.serviceStatus == "In Service")
                        Color(0xFF81C784) else Color(0xFFEF5350),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = hydrant.serviceStatus.ifEmpty { "Unknown" },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details - Single Column Layout for better visibility
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: Hydrant Name and Type/Color
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hydrant Name:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = hydrant.hydrantName.ifEmpty { "-" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF212121)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Type/Color:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = hydrant.typeColor.ifEmpty { "-" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF212121)
                        )
                    }
                }

                // Row 2: Exact Location
                Column {
                    Text(
                        text = "Exact Location:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = hydrant.exactLocation.ifEmpty { "-" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF212121)
                    )
                }

                // Row 3: Coordinates - Highlight if invalid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Latitude:",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (showInvalidWarning) Color(0xFFE65100) else Color(0xFF757575)
                        )
                        Text(
                            text = hydrant.latitude.ifEmpty { "Not set" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (showInvalidWarning) Color(0xFFE65100) else Color(0xFF212121),
                            fontWeight = if (showInvalidWarning) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Longitude:",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (showInvalidWarning) Color(0xFFE65100) else Color(0xFF757575)
                        )
                        Text(
                            text = hydrant.longitude.ifEmpty { "Not set" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (showInvalidWarning) Color(0xFFE65100) else Color(0xFF212121),
                            fontWeight = if (showInvalidWarning) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }

                // Row 4: Municipality and Remarks
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Municipality:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = hydrant.municipality.ifEmpty { "-" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF212121)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Remarks:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = hydrant.remarks.ifEmpty { "-" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF212121)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons - Fixed layout with single line text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ✅ Only show Edit/Fix buttons for admins
                if (!isCheckingAdmin && isAdmin) {
                    // Fix Coordinates / Edit Info Button - Single line
                    if (showInvalidWarning) {
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Fix Coordinates",
                                color = Color.White,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF757575)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Edit Info",
                                maxLines = 1
                            )
                        }
                    }
                }

                // View Location Button - Single line (always visible to everyone)
                // ✅ THIS SHOULD ONLY APPEAR ONCE - NOT TWICE!
                OutlinedButton(
                    onClick = onViewLocationClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF757575)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "View Location",
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HydrantDetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575)
        )
        Text(
            text = value.ifEmpty { "-" },
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF212121)
        )
    }
}

@Composable
fun HydrantDetailItemRight(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575)
        )
        Text(
            text = value.ifEmpty { "-" },
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF212121)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFireHydrantScreen(
    hydrant: FireHydrant,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onDelete: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Form state - initialized with existing hydrant data
    // REMOVED: hydrantName state - it's now read-only and uses hydrant.hydrantName directly
    var exactLocation by rememberSaveable { mutableStateOf(hydrant.exactLocation) }
    var latitude by rememberSaveable { mutableStateOf(hydrant.latitude) }
    var longitude by rememberSaveable { mutableStateOf(hydrant.longitude) }
    var typeColor by rememberSaveable { mutableStateOf(hydrant.typeColor) }
    var serviceStatus by rememberSaveable { mutableStateOf(hydrant.serviceStatus) }
    var remarks by rememberSaveable { mutableStateOf(hydrant.remarks) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var validationError by rememberSaveable { mutableStateOf("") }

    // Dialog states
    var showUnsavedChangesDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    // Check if there are unsaved changes (hydrantName removed - not editable)
    val hasChanges = exactLocation != hydrant.exactLocation ||
            latitude != hydrant.latitude ||
            longitude != hydrant.longitude ||
            typeColor != hydrant.typeColor ||
            serviceStatus != hydrant.serviceStatus ||
            remarks != hydrant.remarks

    // Handle update success
    LaunchedEffect(hydrantUiState.updateSuccess) {
        if (hydrantUiState.updateSuccess) {
            // ✅ Log the change to Firestore instead of showing notification directly
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"
            logHydrantChange(
                context = context,
                changeType = "updated",
                hydrantId = hydrant.id,
                hydrantName = hydrant.hydrantName,
                newStatus = serviceStatus,
                municipality = hydrant.municipality,
                performedBy = currentUserEmail
            )

            snackbarHostState.showSnackbar("Fire hydrant updated successfully!")
            hydrantViewModel.resetUpdateSuccess()
            onSuccess()
        }
    }

    // Handle delete success
    LaunchedEffect(hydrantUiState.deleteSuccess) {
        if (hydrantUiState.deleteSuccess) {
            // ✅ Log the deletion to Firestore
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"
            logHydrantChange(
                context = context,
                changeType = "deleted",
                hydrantId = hydrant.id,
                hydrantName = hydrant.hydrantName,
                municipality = hydrant.municipality,
                performedBy = currentUserEmail
            )

            snackbarHostState.showSnackbar("Fire hydrant deleted successfully!")
            hydrantViewModel.resetDeleteSuccess()
            onDelete()
        }
    }

    // Handle errors
    LaunchedEffect(hydrantUiState.error) {
        hydrantUiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            hydrantViewModel.clearError()
        }
    }

    // Unsaved Changes Dialog
    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved changes. Do you want to save them before leaving?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUnsavedChangesDialog = false
                        // Save changes (hydrantName stays the same)
                        if (exactLocation.isNotBlank()) {
                            hydrantViewModel.updateFireHydrant(
                                hydrant.copy(
                                    // hydrantName is NOT changed - stays as original
                                    exactLocation = exactLocation,
                                    latitude = latitude,
                                    longitude = longitude,
                                    typeColor = typeColor,
                                    serviceStatus = serviceStatus,
                                    remarks = remarks
                                )
                            )
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            showUnsavedChangesDialog = false
                            onBack()
                        }
                    ) {
                        Text("No")
                    }
                    TextButton(
                        onClick = { showUnsavedChangesDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Hydrant") },
            text = { Text("Are you sure you want to delete this fire hydrant? This action cannot be undone. All subsequent hydrant IDs will be renumbered.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        hydrantViewModel.deleteHydrantAndRenumber(hydrant.municipality, hydrant.id)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFEF5350)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Fire Hydrant",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!hydrantUiState.isLoading) {
                                if (hasChanges) {
                                    showUnsavedChangesDialog = true
                                } else {
                                    onBack()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hydrant Name - READ ONLY (not editable)
            Column {
                Text(
                    "Hydrant Name",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hydrant.hydrantName, // Display original name
                    onValueChange = { }, // No action - read only
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false, // Disabled - cannot edit
                    readOnly = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color(0xFF666666),
                        disabledBorderColor = Color(0xFFBDBDBD),
                        disabledContainerColor = Color(0xFFF5F5F5)
                    )
                )
                Text(
                    text = "Auto-generated name cannot be edited",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Exact Location
            Column {
                Text(
                    "Exact Location",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = exactLocation,
                    onValueChange = {
                        exactLocation = it
                        validationError = ""
                    },
                    placeholder = { Text("Enter exact location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !hydrantUiState.isLoading,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Coordinates
            Column {
                Text(
                    "Coordinates",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        placeholder = { Text("Latitude") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !hydrantUiState.isLoading,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        placeholder = { Text("Longitude") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !hydrantUiState.isLoading,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Type/Color
            Column {
                Text(
                    "Type/Color",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = typeColor,
                    onValueChange = { typeColor = it },
                    placeholder = { Text("Enter type/color") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !hydrantUiState.isLoading,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Service Status
            Column {
                Text(
                    "Service Status",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { if (!hydrantUiState.isLoading) expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = serviceStatus,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        enabled = !hydrantUiState.isLoading,
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("In Service") },
                            onClick = {
                                serviceStatus = "In Service"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Out of Service") },
                            onClick = {
                                serviceStatus = "Out of Service"
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Remarks
            Column {
                Text(
                    "Remarks",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    placeholder = { Text("Enter remarks (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !hydrantUiState.isLoading,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Validation Error
            if (validationError.isNotEmpty()) {
                Text(
                    text = validationError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons Row - Delete Hydrant and Save Changes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Delete Hydrant Button
                Button(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    enabled = !hydrantUiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete Hydrant")
                }

                // Save Changes Button
                Button(
                    onClick = {
                        when {
                            exactLocation.isBlank() -> validationError = "Exact location is required"
                            latitude.isBlank() -> validationError = "Latitude is required"
                            longitude.isBlank() -> validationError = "Longitude is required"
                            typeColor.isBlank() -> validationError = "Type/Color is required"
                            serviceStatus.isBlank() -> validationError = "Service status is required"
                            else -> {
                                hydrantViewModel.updateFireHydrant(
                                    hydrant.copy(
                                        // hydrantName stays the same (not editable)
                                        exactLocation = exactLocation,
                                        latitude = latitude,
                                        longitude = longitude,
                                        typeColor = typeColor,
                                        serviceStatus = serviceStatus,
                                        remarks = remarks
                                    )
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    enabled = !hydrantUiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF81C784)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (hydrantUiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Changes")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFireHydrantScreen(
    municipalityName: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // REMOVED: hydrantName - it will be auto-generated
    var exactLocation by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var typeColor by rememberSaveable { mutableStateOf("") }
    var serviceStatus by rememberSaveable { mutableStateOf("In Service") }
    var remarks by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var validationError by rememberSaveable { mutableStateOf("") }
    var isSubmitting by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(hydrantUiState.addSuccess) {
        if (hydrantUiState.addSuccess) {
            // ✅ Log the addition to Firestore
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"
            logHydrantChange(
                context = context,
                changeType = "added",
                hydrantId = "",
                municipality = municipalityName,
                performedBy = currentUserEmail
            )

            snackbarHostState.showSnackbar("Fire hydrant added successfully!")
            hydrantViewModel.resetAddSuccess()
            isSubmitting = false
            onSuccess()
        }
    }

    LaunchedEffect(hydrantUiState.error) {
        hydrantUiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            hydrantViewModel.clearError()
            isSubmitting = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Fire Hydrant",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { if (!hydrantUiState.isLoading) onBack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hydrant Name field REMOVED - will be auto-generated as "Hydrant Id_X"

            Column {
                Text(
                    "Exact Location",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = exactLocation,
                    onValueChange = {
                        exactLocation = it
                        validationError = ""
                    },
                    placeholder = { Text("Enter exact location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !hydrantUiState.isLoading,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Column {
                Text(
                    "Coordinates",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = {
                            latitude = it
                            validationError = ""
                        },
                        placeholder = { Text("Latitude") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !hydrantUiState.isLoading,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = {
                            longitude = it
                            validationError = ""
                        },
                        placeholder = { Text("Longitude") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !hydrantUiState.isLoading,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Column {
                Text(
                    "Type/Color",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = typeColor,
                    onValueChange = {
                        typeColor = it
                        validationError = ""
                    },
                    placeholder = { Text("Enter type/color") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !hydrantUiState.isLoading,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Column {
                Text(
                    "Service Status",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { if (!hydrantUiState.isLoading) expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = serviceStatus,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        enabled = !hydrantUiState.isLoading,
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("In Service") },
                            onClick = {
                                serviceStatus = "In Service"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Out of Service") },
                            onClick = {
                                serviceStatus = "Out of Service"
                                expanded = false
                            }
                        )
                    }
                }
            }

            Column {
                Text(
                    "Remarks",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    placeholder = { Text("Enter remarks (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !hydrantUiState.isLoading,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            if (validationError.isNotEmpty()) {
                Text(
                    text = validationError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { if (!hydrantUiState.isLoading) onBack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    enabled = !hydrantUiState.isLoading,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        when {
                            isSubmitting || hydrantUiState.isLoading -> {
                                // Do nothing if already submitting - prevents multiple clicks
                            }
                            exactLocation.isBlank() -> validationError = "Exact location is required"
                            latitude.isBlank() -> validationError = "Latitude is required"
                            longitude.isBlank() -> validationError = "Longitude is required"
                            typeColor.isBlank() -> validationError = "Type/Color is required"
                            latitude.toDoubleOrNull() == null -> validationError = "Latitude must be a valid number"
                            longitude.toDoubleOrNull() == null -> validationError = "Longitude must be a valid number"
                            latitude.toDouble() < -90 || latitude.toDouble() > 90 -> validationError = "Latitude must be between -90 and 90"
                            longitude.toDouble() < -180 || longitude.toDouble() > 180 -> validationError = "Longitude must be between -180 and 180"
                            else -> {
                                // Set flag to prevent multiple submissions
                                isSubmitting = true

                                // Hydrant name will be auto-generated in repository
                                hydrantViewModel.addFireHydrant(
                                    hydrantName = "", // Will be auto-generated
                                    exactLocation = exactLocation,
                                    latitude = latitude,
                                    longitude = longitude,
                                    typeColor = typeColor,
                                    serviceStatus = serviceStatus,
                                    remarks = remarks,
                                    municipality = municipalityName
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    enabled = !isSubmitting && !hydrantUiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF81C784)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isSubmitting || hydrantUiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Add Fire Hydrant")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun showHydrantUpdateNotification(
    context: Context,
    hydrantId: String,
    hydrantName: String,
    newStatus: String,
    municipality: String
) {
    // Check permission first (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    // Check if hydrant notifications are enabled
    val prefs = context.getSharedPreferences("firegrid_prefs", Context.MODE_PRIVATE)
    val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
    val hydrantUpdatesEnabled = prefs.getBoolean("hydrant_updates_enabled", true)

    if (!notificationsEnabled || !hydrantUpdatesEnabled) {
        return
    }

    // Create notification content
    val statusEmoji = if (newStatus == "In Service") "✅" else "❌"
    val title = "$statusEmoji Hydrant Updated"
    val message = "Hydrant $hydrantId ($hydrantName) in $municipality is now $newStatus"

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notification = NotificationCompat.Builder(context, "hydrant_updates_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setSound(soundUri)
        .setVibrate(longArrayOf(0, 250, 250, 250))
        .setColor(0xFF4CAF50.toInt())
        .build()

    with(NotificationManagerCompat.from(context)) {
        try {
            notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

/**
 * Show notification when a new hydrant is added (for admins)
 */
fun showHydrantAddedNotification(
    context: Context,
    municipality: String
) {
    // Check permission first (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    // Check if hydrant notifications are enabled
    val prefs = context.getSharedPreferences("firegrid_prefs", Context.MODE_PRIVATE)
    val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
    val hydrantUpdatesEnabled = prefs.getBoolean("hydrant_updates_enabled", true)

    if (!notificationsEnabled || !hydrantUpdatesEnabled) {
        return
    }

    val title = "🆕 New Hydrant Added"
    val message = "A new fire hydrant has been added in $municipality"

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notification = NotificationCompat.Builder(context, "hydrant_updates_channel")
        .setSmallIcon(android.R.drawable.ic_input_add)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setSound(soundUri)
        .setVibrate(longArrayOf(0, 250, 250, 250))
        .setColor(0xFF2196F3.toInt())
        .build()

    with(NotificationManagerCompat.from(context)) {
        try {
            notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

/**
 * Show notification when a hydrant is deleted (for admins)
 */
fun showHydrantDeletedNotification(
    context: Context,
    hydrantId: String,
    municipality: String
) {
    // Check permission first (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    // Check if hydrant notifications are enabled
    val prefs = context.getSharedPreferences("firegrid_prefs", Context.MODE_PRIVATE)
    val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
    val hydrantUpdatesEnabled = prefs.getBoolean("hydrant_updates_enabled", true)

    if (!notificationsEnabled || !hydrantUpdatesEnabled) {
        return
    }

    val title = "🗑️ Hydrant Deleted"
    val message = "Hydrant $hydrantId in $municipality has been removed"

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notification = NotificationCompat.Builder(context, "hydrant_updates_channel")
        .setSmallIcon(android.R.drawable.ic_delete)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setSound(soundUri)
        .setVibrate(longArrayOf(0, 250, 250, 250))
        .setColor(0xFFEF5350.toInt())
        .build()

    with(NotificationManagerCompat.from(context)) {
        try {
            notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunicipalityDetailScreen(
    municipalityName: String,
    onBack: () -> Unit,
    onAddHydrant: () -> Unit,
    onShowHydrantList: () -> Unit,
    onShowHydrantMap: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""
    val firestore = Firebase.firestore

    var weatherData by remember { mutableStateOf<DailyWeather?>(null) }
    var isWeatherLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isGeneratingReport by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    // Dialog states for success/error
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✅", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Success!", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                }
            },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("OK")
                }
            },
            containerColor = Color.White
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("❌", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Error", fontWeight = FontWeight.Bold, color = Color(0xFFEF5350))
                }
            },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
                ) {
                    Text("OK")
                }
            },
            containerColor = Color.White
        )
    }

    // Check if user is admin
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { document ->
                    isAdmin = document.exists()
                    isCheckingAdmin = false
                }
                .addOnFailureListener {
                    isAdmin = false
                    isCheckingAdmin = false
                }
        } else {
            isAdmin = false
            isCheckingAdmin = false
        }
    }

    LaunchedEffect(municipalityName) {
        isWeatherLoading = true
        errorMessage = null
        scope.launch {
            val data = fetchWeatherData(municipalityName)
            if (data != null) {
                weatherData = data
            } else {
                errorMessage = "Unable to fetch weather data"
            }
            isWeatherLoading = false
        }
    }

    LaunchedEffect(municipalityName) {
        hydrantViewModel.loadHydrants(municipalityName)
        hydrantViewModel.loadHydrantCounts(municipalityName)
    }

    val temperature = weatherData?.temp?.toInt() ?: 0
    val condition = weatherData?.condition ?: "Loading..."
    val tempMin = weatherData?.tempMin?.toInt() ?: 0
    val tempMax = weatherData?.tempMax?.toInt() ?: 0
    val lowHigh = "$tempMin° / $tempMax°"
    val humidity = "${weatherData?.humidity ?: 0}%"
    val windSpeedKmh = ((weatherData?.windSpeed ?: 0.0) * 3.6).toInt()
    val windSpeed = "$windSpeedKmh km/h"

    // ✅ FIX: Remember counts to prevent glitching
    val outOfService = remember(hydrantUiState.outOfServiceCount) { hydrantUiState.outOfServiceCount }
    val inService = remember(hydrantUiState.inServiceCount) { hydrantUiState.inServiceCount }
    val totalHydrants = remember(outOfService, inService) { outOfService + inService }

    // Function to convert decimal degrees to DMS format
    fun decimalToDMS(decimal: Double, isLatitude: Boolean): String {
        val absolute = kotlin.math.abs(decimal)
        val degrees = absolute.toInt()
        val minutesDecimal = (absolute - degrees) * 60
        val minutes = minutesDecimal.toInt()
        val seconds = (minutesDecimal - minutes) * 60

        val direction = if (isLatitude) {
            if (decimal >= 0) "N" else "S"
        } else {
            if (decimal >= 0) "E" else "W"
        }

        return "%d° %02d' %.2f\" %s".format(degrees, minutes, seconds, direction)
    }

    // =====================================================
    // AUTOMATIC PDF GENERATION AND EMAIL SENDING
    // =====================================================
    fun generateAndSendPdfReport() {
        isGeneratingReport = true
        scope.launch {
            try {
                val hydrants = hydrantUiState.hydrants

                if (hydrants.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        dialogMessage = "No hydrants to generate report"
                        showErrorDialog = true
                        isGeneratingReport = false
                    }
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    val stationName = "$municipalityName FS"

                    // Page dimensions (A4 Landscape)
                    val pageWidth = 842
                    val pageHeight = 595

                    val pdfDocument = android.graphics.pdf.PdfDocument()

                    val headerHeight = 120
                    val tableHeaderHeight = 50
                    val rowHeight = 35
                    val marginTop = 40
                    val marginBottom = 40
                    val availableHeight = pageHeight - marginTop - headerHeight - tableHeaderHeight - marginBottom
                    val rowsPerPage = availableHeight / rowHeight

                    val totalPages = kotlin.math.ceil(hydrants.size.toDouble() / rowsPerPage).toInt().coerceAtLeast(1)

                    val colWidths = intArrayOf(85, 45, 130, 95, 95, 80, 50, 50, 130)
                    val tableStartX = 40f

                    for (pageNum in 0 until totalPages) {
                        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum + 1).create()
                        val page = pdfDocument.startPage(pageInfo)
                        val canvas = page.canvas

                        val titlePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 16f
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        val subtitlePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            textSize = 12f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        val headerPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 8f
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                        }

                        val cellPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 7f
                        }

                        val linePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            strokeWidth = 1f
                            style = android.graphics.Paint.Style.STROKE
                        }

                        val headerBgPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.rgb(240, 240, 240)
                            style = android.graphics.Paint.Style.FILL
                        }

                        var yPos = marginTop.toFloat()

                        canvas.drawText("FIRE HYDRANT INVENTORY REPORT", pageWidth / 2f, yPos, titlePaint)
                        yPos += 25

                        canvas.drawText("$municipalityName, Laguna", pageWidth / 2f, yPos, subtitlePaint)
                        yPos += 20

                        val datePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 10f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        canvas.drawText(
                            "Date Generated: ${java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.ENGLISH).format(java.util.Date())}",
                            pageWidth / 2f, yPos, datePaint
                        )
                        yPos += 15

                        canvas.drawText(
                            "Total: $totalHydrants | Operational: $inService | Non-Operational: $outOfService | Page ${pageNum + 1} of $totalPages",
                            pageWidth / 2f, yPos, datePaint
                        )
                        yPos += 30

                        val tableHeaderY = yPos
                        canvas.drawRect(tableStartX, tableHeaderY, tableStartX + colWidths.sum(), tableHeaderY + tableHeaderHeight, headerBgPaint)

                        val headers = arrayOf(
                            "STATION/\nOWNED BY",
                            "NO. OF\nHYDRANT",
                            "EXACT LOCATION/\nADDRESS",
                            "LATITUDE",
                            "LONGITUDE",
                            "TYPE/\nCOLOR",
                            "OPER.",
                            "NON-\nOPER.",
                            "REMARKS"
                        )

                        var xPos = tableStartX
                        for (i in headers.indices) {
                            canvas.drawRect(xPos, tableHeaderY, xPos + colWidths[i], tableHeaderY + tableHeaderHeight, linePaint)

                            val lines = headers[i].split("\n")
                            val lineHeight = 10f
                            val startY = tableHeaderY + (tableHeaderHeight - lines.size * lineHeight) / 2 + lineHeight

                            for ((lineIndex, line) in lines.withIndex()) {
                                canvas.drawText(line, xPos + 4, startY + lineIndex * lineHeight, headerPaint)
                            }
                            xPos += colWidths[i]
                        }

                        yPos = tableHeaderY + tableHeaderHeight

                        val startIndex = pageNum * rowsPerPage
                        val endIndex = minOf(startIndex + rowsPerPage, hydrants.size)

                        for (i in startIndex until endIndex) {
                            val hydrant = hydrants[i]

                            val lat = hydrant.latitude.toDoubleOrNull()
                            val lng = hydrant.longitude.toDoubleOrNull()

                            val latitudeDMS = if (lat != null && lat != 0.0) {
                                decimalToDMS(lat, true)
                            } else {
                                hydrant.latitude.ifEmpty { "-" }
                            }

                            val longitudeDMS = if (lng != null && lng != 0.0) {
                                decimalToDMS(lng, false)
                            } else {
                                hydrant.longitude.ifEmpty { "-" }
                            }

                            val operational = if (hydrant.serviceStatus == "In Service") "1" else ""
                            val nonOperational = if (hydrant.serviceStatus == "Out of Service") "1" else ""

                            val rowData = arrayOf(
                                stationName,
                                (i + 1).toString(),
                                hydrant.exactLocation.ifEmpty { "-" },
                                latitudeDMS,
                                longitudeDMS,
                                hydrant.typeColor.ifEmpty { "-" },
                                operational,
                                nonOperational,
                                hydrant.remarks.ifEmpty { "" }
                            )

                            xPos = tableStartX
                            for (j in rowData.indices) {
                                canvas.drawRect(xPos, yPos, xPos + colWidths[j], yPos + rowHeight, linePaint)

                                val maxChars = (colWidths[j] / 5).coerceAtLeast(5)
                                val displayText = if (rowData[j].length > maxChars) {
                                    rowData[j].take(maxChars - 2) + ".."
                                } else {
                                    rowData[j]
                                }

                                canvas.drawText(displayText, xPos + 4, yPos + rowHeight / 2 + 3, cellPaint)
                                xPos += colWidths[j]
                            }

                            yPos += rowHeight
                        }

                        val footerPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 8f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        canvas.drawText(
                            "Generated by FireGrid App | Bureau of Fire Protection - Laguna Province",
                            pageWidth / 2f,
                            (pageHeight - 20).toFloat(),
                            footerPaint
                        )

                        pdfDocument.finishPage(page)
                    }

                    // Save PDF
                    val fileName = "FireHydrant_Report_${municipalityName}_${System.currentTimeMillis()}.pdf"
                    val file = java.io.File(context.cacheDir, fileName)
                    val outputStream = java.io.FileOutputStream(file)
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()
                    pdfDocument.close()

                    // =====================================================
                    // AUTOMATIC EMAIL SENDING - NO DIALOG, NO USER INPUT
                    // =====================================================
                    val subject = "Fire Hydrant Inventory Report - $municipalityName (${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.ENGLISH).format(java.util.Date())})"
                    val body = """
                        Fire Hydrant Inventory Report
                        
                        Municipality: $municipalityName, Laguna
                        Date: ${java.text.SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", java.util.Locale.ENGLISH).format(java.util.Date())}
                        
                        Summary:
                        - Total Hydrants: $totalHydrants
                        - Operational: $inService
                        - Non-Operational: $outOfService
                        
                        Please find the detailed report attached as PDF.
                        
                        ---
                        Generated by FireGrid App
                        Bureau of Fire Protection - Laguna Province
                    """.trimIndent()

                    // SEND EMAIL AUTOMATICALLY using EmailSender class
                    val emailResult = EmailSender.sendEmailWithAttachment(
                        subject = subject,
                        body = body,
                        attachmentFile = file
                    )

                    withContext(Dispatchers.Main) {
                        emailResult.fold(
                            onSuccess = { message ->
                                dialogMessage = "PDF Report has been automatically sent to:\nproject.fhydrant@gmail.com\n\nTotal hydrants: $totalHydrants"
                                showSuccessDialog = true
                            },
                            onFailure = { error ->
                                dialogMessage = "Failed to send email: ${error.message}\n\nPlease check your internet connection."
                                showErrorDialog = true
                            }
                        )
                        isGeneratingReport = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    dialogMessage = "Failed to generate PDF: ${e.message}"
                    showErrorDialog = true
                    isGeneratingReport = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(municipalityName, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Generate Report Button - Only visible for admins
                    if (isAdmin && !isCheckingAdmin) {
                        Button(
                            onClick = { generateAndSendPdfReport() },
                            enabled = !isGeneratingReport && !hydrantUiState.isLoading,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            if (isGeneratingReport) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sending...",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Generate\nReport",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Weather for $municipalityName",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF666666)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isWeatherLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFFFF6B35)
                            )
                        } else if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "Error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text(
                                text = "$temperature°C",
                                style = MaterialTheme.typography.displayLarge,
                                fontSize = 72.sp,
                                fontWeight = FontWeight.Light
                            )

                            Text(
                                text = condition,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF666666)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                WeatherDetail("Low / High", lowHigh)
                                WeatherDetail("Humidity", humidity)
                                WeatherDetail("Wind", windSpeed)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$outOfService",
                                style = MaterialTheme.typography.displayMedium,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                            Text(
                                text = "Out Of Service",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$inService",
                                style = MaterialTheme.typography.displayMedium,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF388E3C)
                            )
                            Text(
                                text = "In Service",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF388E3C)
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(56.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Hydrants: $totalHydrants",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            item {
                // Only show "Add Fire Hydrant" button if user is admin
                if (isAdmin && !isCheckingAdmin) {
                    Button(
                        onClick = onAddHydrant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Add Fire Hydrant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onShowHydrantList,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Fire Hydrant List",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onShowHydrantMap,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Fire Hydrant Map",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun WeatherDetail(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF999999)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// Municipality coordinates for Laguna
fun getMunicipalityCoordinates(municipality: String): LatLng {
    return when (municipality) {
        "Alaminos" -> LatLng(14.0649, 121.2458)
        "Bay" -> LatLng(14.1872, 121.2850)
        "Biñan" -> LatLng(14.3414, 121.0809)
        "Cabuyao" -> LatLng(14.2724, 121.1251)
        "Calamba" -> LatLng(14.2116, 121.1653)
        "Calauan" -> LatLng(14.1469, 121.3204)
        "Cavinti" -> LatLng(14.2419, 121.5097)
        "Famy" -> LatLng(14.4386, 121.4483)
        "Kalayaan" -> LatLng(14.3254, 121.4810)
        "Liliw" -> LatLng(14.1286, 121.4342)
        "Los Baños" -> LatLng(14.1694, 121.2428)
        "Luisiana" -> LatLng(14.1847, 121.5106)
        "Lumban" -> LatLng(14.2944, 121.4603)
        "Mabitac" -> LatLng(14.4297, 121.4275)
        "Magdalena" -> LatLng(14.2000, 121.4297)
        "Majayjay" -> LatLng(14.1461, 121.4728)
        "Nagcarlan" -> LatLng(14.1364, 121.4150)
        "Paete" -> LatLng(14.3636, 121.4847)
        "Pagsanjan" -> LatLng(14.2728, 121.4550)
        "Pakil" -> LatLng(14.3797, 121.4775)
        "Pangil" -> LatLng(14.4039, 121.4656)
        "Pila" -> LatLng(14.2333, 121.3644)  // ADD THIS LINE
        "Rizal" -> LatLng(14.1072, 121.3931)
        "San Pablo" -> LatLng(14.0689, 121.3256)
        "San Pedro" -> LatLng(14.3595, 121.0473)
        "Santa Cruz" -> LatLng(14.2814, 121.4156)
        "Santa Maria" -> LatLng(14.4708, 121.4250)
        "Santa Rosa" -> LatLng(14.3122, 121.1114)
        "Siniloan" -> LatLng(14.4247, 121.4486)
        "Victoria" -> LatLng(14.2256, 121.3281)
        else -> LatLng(14.2700, 121.4100) // Default to center of Laguna
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FireHydrantMapScreen(
    municipalityName: String,
    onBack: () -> Unit,
    onShowInvalidCoordinates: () -> Unit
) {
    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    val municipalityLocation = getMunicipalityCoordinates(municipalityName)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(municipalityLocation, 12f)
    }

    // State to track if markers are ready
    var markersReady by remember { mutableStateOf(false) }
    var greenMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var redMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }

    // Create markers after composition in LaunchedEffect
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100) // Small delay to ensure everything is initialized
        try {
            // Create green marker
            val greenBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val greenCanvas = android.graphics.Canvas(greenBitmap)
            val greenPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#4CAF50")
                style = android.graphics.Paint.Style.FILL
            }

            val centerX = 24f
            val topRadius = 14f
            val bottomY = 44f

            greenCanvas.drawCircle(centerX, topRadius + 4f, topRadius, greenPaint)

            val trianglePath = android.graphics.Path()
            trianglePath.moveTo(centerX, bottomY)
            trianglePath.lineTo(centerX - 12f, topRadius + 12f)
            trianglePath.lineTo(centerX + 12f, topRadius + 12f)
            trianglePath.close()
            greenCanvas.drawPath(trianglePath, greenPaint)

            greenPaint.color = android.graphics.Color.WHITE
            greenCanvas.drawCircle(centerX, topRadius + 4f, 6f, greenPaint)

            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(greenBitmap)

            // Create red marker
            val redBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val redCanvas = android.graphics.Canvas(redBitmap)
            val redPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#EF5350")
                style = android.graphics.Paint.Style.FILL
            }

            redCanvas.drawCircle(centerX, topRadius + 4f, topRadius, redPaint)

            val redTrianglePath = android.graphics.Path()
            redTrianglePath.moveTo(centerX, bottomY)
            redTrianglePath.lineTo(centerX - 12f, topRadius + 12f)
            redTrianglePath.lineTo(centerX + 12f, topRadius + 12f)
            redTrianglePath.close()
            redCanvas.drawPath(redTrianglePath, redPaint)

            redPaint.color = android.graphics.Color.WHITE
            redCanvas.drawCircle(centerX, topRadius + 4f, 6f, redPaint)

            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(redBitmap)

            markersReady = true
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to default markers
            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
            )
            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
            )
            markersReady = true
        }
    }

    LaunchedEffect(municipalityName) {
        hydrantViewModel.loadHydrants(municipalityName)
    }

    val hydrantsWithValidCoords = hydrantUiState.hydrants.filter { hydrant ->
        val lat = hydrant.latitude.toDoubleOrNull()
        val lng = hydrant.longitude.toDoubleOrNull()
        lat != null && lng != null &&
                lat in -90.0..90.0 &&
                lng in -180.0..180.0 &&
                !(lat == 0.0 && lng == 0.0)
    }

    val invalidCoordinatesCount = hydrantUiState.hydrants.size - hydrantsWithValidCoords.size
    val hasInvalidCoordinates = invalidCoordinatesCount > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "$municipalityName Hydrant Map",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (hasInvalidCoordinates) {
                        IconButton(
                            onClick = onShowInvalidCoordinates,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(48.dp)
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Invalid Coordinates Warning",
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEF5350)
                )
            )
        }
    ) { padding ->
        val scope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                )
            ) {
                // Only show markers if they're ready
                if (markersReady && greenMarkerIcon != null && redMarkerIcon != null) {
                    hydrantsWithValidCoords.forEach { hydrant ->
                        val lat = hydrant.latitude.toDoubleOrNull() ?: return@forEach
                        val lng = hydrant.longitude.toDoubleOrNull() ?: return@forEach

                        val markerIcon = if (hydrant.serviceStatus == "In Service") {
                            greenMarkerIcon!!
                        } else {
                            redMarkerIcon!!
                        }

                        com.google.maps.android.compose.Marker(
                            state = com.google.maps.android.compose.MarkerState(
                                position = LatLng(lat, lng)
                            ),
                            title = "Hydrant ${hydrant.id}",
                            snippet = "${hydrant.hydrantName} - ${hydrant.serviceStatus}",
                            icon = markerIcon
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Hydrants on Map: ${hydrantsWithValidCoords.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " In Service",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " Out of Service",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (hasInvalidCoordinates) {
                        Surface(
                            onClick = onShowInvalidCoordinates,
                            modifier = Modifier.padding(top = 4.dp),
                            color = Color.Transparent
                        ) {
                            Text(
                                text = "$invalidCoordinatesCount hydrant(s) have invalid coordinates",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFEF5350),
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val currentZoom = cameraPositionState.position.zoom
                            cameraPositionState.animate(
                                CameraUpdateFactory.zoomTo(currentZoom + 1f)
                            )
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    containerColor = Color.White,
                    contentColor = Color(0xFF5F6368),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 3.dp,
                        pressedElevation = 5.dp
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF5F6368)
                    )
                }

                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val currentZoom = cameraPositionState.position.zoom
                            cameraPositionState.animate(
                                CameraUpdateFactory.zoomTo(currentZoom - 1f)
                            )
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    containerColor = Color.White,
                    contentColor = Color(0xFF5F6368),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 3.dp,
                        pressedElevation = 5.dp
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF5F6368)
                    )
                }
            }

            if (hydrantUiState.isLoading || !markersReady) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFEF5350)
                    )
                }
            }
        }
    }
}

// Add this import at the top of your file:
// import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewHydrantLocationScreen(
    hydrant: FireHydrant,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var isMapReady by remember { mutableStateOf(false) }
    var mapLoaded by remember { mutableStateOf(false) }
    var markerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var markersReady by remember { mutableStateOf(false) }

    val lat = hydrant.latitude.toDoubleOrNull()
    val lng = hydrant.longitude.toDoubleOrNull()

    // Check if coordinates are valid
    val hasValidCoordinates = lat != null && lng != null &&
            lat >= -90 && lat <= 90 &&
            lng >= -180 && lng <= 180 &&
            lat != 0.0 && lng != 0.0

    val hydrantLocation = remember(hasValidCoordinates, lat, lng, hydrant.municipality) {
        if (hasValidCoordinates) {
            LatLng(lat!!, lng!!)
        } else {
            getMunicipalityCoordinates(hydrant.municipality)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(hydrantLocation, 17f)
    }

    // Create custom marker after composition - same approach as FireHydrantMapScreen
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100) // Small delay to ensure everything is initialized
        try {
            val width = 48
            val height = 48
            val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            val paint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = if (hydrant.serviceStatus == "In Service") {
                    android.graphics.Color.parseColor("#4CAF50")
                } else {
                    android.graphics.Color.parseColor("#EF5350")
                }
                style = android.graphics.Paint.Style.FILL
            }

            val centerX = 24f
            val topRadius = 14f
            val bottomY = 44f

            // Draw filled circle for the main body
            canvas.drawCircle(centerX, topRadius + 4f, topRadius, paint)

            // Draw the bottom triangle/point
            val trianglePath = android.graphics.Path()
            trianglePath.moveTo(centerX, bottomY)
            trianglePath.lineTo(centerX - 12f, topRadius + 12f)
            trianglePath.lineTo(centerX + 12f, topRadius + 12f)
            trianglePath.close()
            canvas.drawPath(trianglePath, paint)

            // Draw white circle in center (the hole)
            paint.color = android.graphics.Color.WHITE
            canvas.drawCircle(centerX, topRadius + 4f, 6f, paint)

            markerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap)
            markersReady = true
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to default marker if custom marker creation fails
            markerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                if (hydrant.serviceStatus == "In Service")
                    com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
                else
                    com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
            )
            markersReady = true
        }
    }

    // Delay showing the map to ensure proper initialization
    LaunchedEffect(Unit) {
        try {
            // Initialize MapsInitializer
            com.google.android.gms.maps.MapsInitializer.initialize(
                context,
                com.google.android.gms.maps.MapsInitializer.Renderer.LATEST
            ) { }
            kotlinx.coroutines.delay(500)
            isMapReady = true
        } catch (e: Exception) {
            e.printStackTrace()
            kotlinx.coroutines.delay(500)
            isMapReady = true
        }
    }

    // Show snackbar if coordinates are invalid
    LaunchedEffect(hasValidCoordinates) {
        if (!hasValidCoordinates) {
            snackbarHostState.showSnackbar(
                "Invalid coordinates. Showing municipality center instead."
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hydrant ${hydrant.id} Location",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEF5350)
                )
            )
        }
    ) { padding ->
        val scope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                !isMapReady -> {
                    // Show loading indicator while map initializes
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFEF5350)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading map...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
                else -> {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false
                        ),
                        properties = MapProperties(
                            isMyLocationEnabled = false
                        ),
                        onMapLoaded = {
                            mapLoaded = true
                        }
                    ) {
                        // Only show marker if it's ready and coordinates are valid
                        if (hasValidCoordinates && markersReady && markerIcon != null) {
                            com.google.maps.android.compose.Marker(
                                state = com.google.maps.android.compose.MarkerState(
                                    position = hydrantLocation
                                ),
                                title = "Hydrant ${hydrant.id}",
                                snippet = "${hydrant.hydrantName} - ${hydrant.serviceStatus}",
                                icon = markerIcon!!
                            )
                        }
                    }
                }
            }

            // Custom zoom controls - only show after map is loaded
            if (mapLoaded) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Location pin button - reset to hydrant location
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                try {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(hydrantLocation, 17f)
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.size(36.dp),
                        containerColor = Color.White,
                        contentColor = Color(0xFF5F6368),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 5.dp
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Reset View",
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF5F6368)
                        )
                    }

                    // Zoom in button
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                try {
                                    val currentZoom = cameraPositionState.position.zoom
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.zoomTo(currentZoom + 1f)
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.size(36.dp),
                        containerColor = Color.White,
                        contentColor = Color(0xFF5F6368),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 5.dp
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF5F6368)
                        )
                    }

                    // Zoom out button
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                try {
                                    val currentZoom = cameraPositionState.position.zoom
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.zoomTo(currentZoom - 1f)
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.size(36.dp),
                        containerColor = Color.White,
                        contentColor = Color(0xFF5F6368),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 5.dp
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "−",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF5F6368)
                        )
                    }
                }
            }

            // Info card showing hydrant details
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hydrant ${hydrant.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = if (hydrant.serviceStatus == "In Service")
                                Color(0xFF81C784) else Color(0xFFEF5350),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = hydrant.serviceStatus,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = hydrant.hydrantName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = hydrant.exactLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Text(
                            text = "Lat: ${hydrant.latitude}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Lng: ${hydrant.longitude}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Show loading overlay if markers aren't ready yet
            if (!markersReady) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFEF5350)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    initialIncidentLocation: Pair<Double, Double>? = null,
    onIncidentHandled: () -> Unit = {},
            onMailClick: () -> Unit = {}  // ADD THIS PARAMETER
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lagunaLake = LatLng(14.3500, 121.2500)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lagunaLake, 10f)
    }

    var isMyLocationEnabled by remember { mutableStateOf(false) }
    var isDrawerOpen by remember { mutableStateOf(false) }
    var isSearchingNearestHydrant by remember { mutableStateOf(false) }
    var nearestHydrant by remember { mutableStateOf<FireHydrant?>(null) }
    var nearestHydrantDistance by remember { mutableStateOf<Double?>(null) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showReportFireDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Directions state
    var directionsResult by remember { mutableStateOf<DirectionsResult?>(null) }
    var userCurrentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoadingDirections by remember { mutableStateOf(false) }

    // Fire Incident Location States
    var incidentMarkerLocation by remember { mutableStateOf<LatLng?>(null) }
    var showIncidentMarker by remember { mutableStateOf(false) }
    var showIncidentInfoCard by remember { mutableStateOf(false) }

    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    BackHandler(enabled = isDrawerOpen) { isDrawerOpen = false }

    LaunchedEffect(Unit) { hydrantViewModel.loadAllHydrants() }

    val hydrantsWithValidCoords = hydrantUiState.allHydrants.filter { hydrant ->
        val lat = hydrant.latitude.toDoubleOrNull()
        val lng = hydrant.longitude.toDoubleOrNull()
        lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0 && !(lat == 0.0 && lng == 0.0)
    }

    var greenMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var redMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var blueMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var fireIncidentMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var markersReady by remember { mutableStateOf(false) }

    // Create marker icons
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        try {
            val centerX = 24f
            val topRadius = 14f
            val bottomY = 44f

            // Green marker
            val greenBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val greenCanvas = android.graphics.Canvas(greenBitmap)
            val greenPaint = android.graphics.Paint().apply { isAntiAlias = true; color = android.graphics.Color.parseColor("#4CAF50"); style = android.graphics.Paint.Style.FILL }
            greenCanvas.drawCircle(centerX, topRadius + 4f, topRadius, greenPaint)
            val greenTriangle = android.graphics.Path()
            greenTriangle.moveTo(centerX, bottomY); greenTriangle.lineTo(centerX - 12f, topRadius + 12f); greenTriangle.lineTo(centerX + 12f, topRadius + 12f); greenTriangle.close()
            greenCanvas.drawPath(greenTriangle, greenPaint)
            greenPaint.color = android.graphics.Color.WHITE
            greenCanvas.drawCircle(centerX, topRadius + 4f, 6f, greenPaint)
            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(greenBitmap)

            // Red marker
            val redBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val redCanvas = android.graphics.Canvas(redBitmap)
            val redPaint = android.graphics.Paint().apply { isAntiAlias = true; color = android.graphics.Color.parseColor("#EF5350"); style = android.graphics.Paint.Style.FILL }
            redCanvas.drawCircle(centerX, topRadius + 4f, topRadius, redPaint)
            val redTriangle = android.graphics.Path()
            redTriangle.moveTo(centerX, bottomY); redTriangle.lineTo(centerX - 12f, topRadius + 12f); redTriangle.lineTo(centerX + 12f, topRadius + 12f); redTriangle.close()
            redCanvas.drawPath(redTriangle, redPaint)
            redPaint.color = android.graphics.Color.WHITE
            redCanvas.drawCircle(centerX, topRadius + 4f, 6f, redPaint)
            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(redBitmap)

            // Blue marker
            val blueBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val blueCanvas = android.graphics.Canvas(blueBitmap)
            val bluePaint = android.graphics.Paint().apply { isAntiAlias = true; color = android.graphics.Color.parseColor("#2196F3"); style = android.graphics.Paint.Style.FILL }
            blueCanvas.drawCircle(centerX, topRadius + 4f, topRadius, bluePaint)
            val blueTriangle = android.graphics.Path()
            blueTriangle.moveTo(centerX, bottomY); blueTriangle.lineTo(centerX - 12f, topRadius + 12f); blueTriangle.lineTo(centerX + 12f, topRadius + 12f); blueTriangle.close()
            blueCanvas.drawPath(blueTriangle, bluePaint)
            bluePaint.color = android.graphics.Color.WHITE
            blueCanvas.drawCircle(centerX, topRadius + 4f, 6f, bluePaint)
            blueMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(blueBitmap)

            // Fire Incident Marker (Orange - Larger)
            val fireBitmap = android.graphics.Bitmap.createBitmap(56, 56, android.graphics.Bitmap.Config.ARGB_8888)
            val fireCanvas = android.graphics.Canvas(fireBitmap)
            val firePaint = android.graphics.Paint().apply { isAntiAlias = true; color = android.graphics.Color.parseColor("#FF5722"); style = android.graphics.Paint.Style.FILL }
            val fireCenterX = 28f; val fireTopRadius = 18f; val fireBottomY = 52f
            fireCanvas.drawCircle(fireCenterX, fireTopRadius + 4f, fireTopRadius, firePaint)
            val fireTriangle = android.graphics.Path()
            fireTriangle.moveTo(fireCenterX, fireBottomY); fireTriangle.lineTo(fireCenterX - 14f, fireTopRadius + 14f); fireTriangle.lineTo(fireCenterX + 14f, fireTopRadius + 14f); fireTriangle.close()
            fireCanvas.drawPath(fireTriangle, firePaint)
            firePaint.color = android.graphics.Color.parseColor("#FFC107")
            fireCanvas.drawCircle(fireCenterX, fireTopRadius + 4f, 10f, firePaint)
            firePaint.color = android.graphics.Color.parseColor("#FF5722")
            fireCanvas.drawCircle(fireCenterX, fireTopRadius + 4f, 5f, firePaint)
            fireIncidentMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(fireBitmap)

            markersReady = true
        } catch (e: Exception) {
            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN)
            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED)
            blueMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE)
            fireIncidentMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE)
            markersReady = true
        }
    }

    // Handle incoming incident location from alert
    LaunchedEffect(initialIncidentLocation) {
        initialIncidentLocation?.let { (lat, lng) ->
            val incidentLatLng = LatLng(lat, lng)
            incidentMarkerLocation = incidentLatLng
            showIncidentMarker = true
            showIncidentInfoCard = true
            scope.launch {
                kotlinx.coroutines.delay(500)
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(incidentLatLng, 16f), durationMs = 1000)
            }
            onIncidentHandled()
        }
    }

    // Find nearest IN SERVICE hydrant when incident location is set
    LaunchedEffect(hydrantUiState.allHydrants, incidentMarkerLocation) {
        if (incidentMarkerLocation != null && hydrantUiState.allHydrants.isNotEmpty()) {
            val lat = incidentMarkerLocation!!.latitude
            val lng = incidentMarkerLocation!!.longitude
            var nearest: FireHydrant? = null
            var minDistance = Double.MAX_VALUE

            for (hydrant in hydrantUiState.allHydrants) {
                if (hydrant.serviceStatus != "In Service") continue
                val hydrantLat = hydrant.latitude.toDoubleOrNull() ?: continue
                val hydrantLng = hydrant.longitude.toDoubleOrNull() ?: continue
                if (hydrantLat < -90 || hydrantLat > 90 || hydrantLng < -180 || hydrantLng > 180) continue
                if (hydrantLat == 0.0 && hydrantLng == 0.0) continue

                val r = 6371
                val dLat = Math.toRadians(hydrantLat - lat)
                val dLon = Math.toRadians(hydrantLng - lng)
                val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(hydrantLat)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
                val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                val distance = r * c

                if (distance < minDistance) { minDistance = distance; nearest = hydrant }
            }

            if (nearest != null) {
                nearestHydrant = nearest
                nearestHydrantDistance = minDistance
                val hydrantLat = nearest.latitude.toDoubleOrNull()
                val hydrantLng = nearest.longitude.toDoubleOrNull()
                if (hydrantLat != null && hydrantLng != null) {
                    isLoadingDirections = true
                    val result = fetchDirections(origin = incidentMarkerLocation!!, destination = LatLng(hydrantLat, hydrantLng))
                    directionsResult = result
                    isLoadingDirections = false
                }
            }
        }
    }

    val drawerOffsetX by animateDpAsState(targetValue = if (isDrawerOpen) 0.dp else (-280).dp, animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = "drawerOffset")
    val scrimAlpha by animateFloatAsState(targetValue = if (isDrawerOpen) 0.5f else 0f, animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = "scrimAlpha")

    val locationPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                isMyLocationEnabled = true
                getCurrentLocation(context) { location -> scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f)) } }
            }
            else -> android.widget.Toast.makeText(context, "Location permission denied", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371; val dLat = Math.toRadians(lat2 - lat1); val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); return r * c
    }

    fun findNearestHydrant(userLat: Double, userLng: Double, hydrants: List<FireHydrant>): Pair<FireHydrant?, Double?> {
        var nearest: FireHydrant? = null; var minDistance = Double.MAX_VALUE
        for (hydrant in hydrants) {
            val lat = hydrant.latitude.toDoubleOrNull() ?: continue; val lng = hydrant.longitude.toDoubleOrNull() ?: continue
            if (lat < -90 || lat > 90 || lng < -180 || lng > 180 || (lat == 0.0 && lng == 0.0)) continue
            val distance = calculateDistance(userLat, userLng, lat, lng)
            if (distance < minDistance) { minDistance = distance; nearest = hydrant }
        }
        return if (nearest != null) Pair(nearest, minDistance) else Pair(null, null)
    }

    // EMERGENCY DIALOG
    if (showEmergencyDialog) {
        AlertDialog(
            onDismissRequest = { showEmergencyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Report Emergency", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                }
            },
            text = {
                Column {
                    Text("Call emergency services immediately:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))

                    // Report a Fire Incident - NOW FIRST
                    Surface(onClick = { showEmergencyDialog = false; showReportFireDialog = true }, modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("🔥 Report a Fire Incident", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                Text("Submit incident report to admins", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                            }
                            Icon(Icons.Default.KeyboardArrowRight, "Report", tint = Color(0xFFD32F2F), modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    // Emergency Hotline 911
                    Surface(onClick = { val intent = android.content.Intent(android.content.Intent.ACTION_DIAL); intent.data = android.net.Uri.parse("tel:911"); context.startActivity(intent) }, modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text("Emergency Hotline", fontWeight = FontWeight.Bold); Text("911", style = MaterialTheme.typography.titleLarge, color = Color(0xFFE65100), fontWeight = FontWeight.Bold) }
                            Icon(Icons.Default.KeyboardArrowRight, "Call", tint = Color(0xFFE65100), modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    // Bureau of Fire Protection 160
                    Surface(onClick = { val intent = android.content.Intent(android.content.Intent.ACTION_DIAL); intent.data = android.net.Uri.parse("tel:160"); context.startActivity(intent) }, modifier = Modifier.fillMaxWidth(), color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text("Bureau of Fire Protection", fontWeight = FontWeight.Bold); Text("160", style = MaterialTheme.typography.titleLarge, color = Color(0xFF1565C0), fontWeight = FontWeight.Bold) }
                            Icon(Icons.Default.KeyboardArrowRight, "Call", tint = Color(0xFF1565C0), modifier = Modifier.size(32.dp))
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showEmergencyDialog = false }) { Text("Close", color = Color.Gray) } },
            containerColor = Color.White
        )
    }
    // REPORT FIRE DIALOG
    if (showReportFireDialog) {
        var incidentLocation by remember { mutableStateOf("") }
        var currentLocation by remember { mutableStateOf("") }
        var incidentDescription by remember { mutableStateOf("") }
        var isSubmitting by remember { mutableStateOf(false) }
        var isGettingLocation by remember { mutableStateOf(false) }
        var reporterName by remember { mutableStateOf("") }
        var reporterContact by remember { mutableStateOf("") }
        var locationError by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) { reporterName = FirebaseAuth.getInstance().currentUser?.displayName ?: "" }

        AlertDialog(
            onDismissRequest = { if (!isSubmitting) showReportFireDialog = false },
            title = { Row(verticalAlignment = Alignment.CenterVertically) { Text("🔥", fontSize = 24.sp); Spacer(Modifier.width(8.dp)); Text("Report Fire Incident", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F)) } },
            text = {
                Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Your Name
                    OutlinedTextField(
                        value = reporterName,
                        onValueChange = { reporterName = it },
                        label = { Text("Your Name") },
                        placeholder = { Text("Enter your name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), focusedLabelColor = Color(0xFFD32F2F))
                    )

                    // Contact Number with +63 prefix
                    OutlinedTextField(
                        value = reporterContact,
                        onValueChange = { newValue ->
                            // Only allow digits and limit to 10 characters
                            val digitsOnly = newValue.filter { it.isDigit() }
                            if (digitsOnly.length <= 10) {
                                reporterContact = digitsOnly
                            }
                        },
                        label = { Text("Contact Number") },
                        placeholder = { Text("") },
                        leadingIcon = {
                            Text(
                                text = "+63",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD32F2F),
                            focusedLabelColor = Color(0xFFD32F2F)
                        ),
                        supportingText = {
                            Text(
                                text = "${reporterContact.length}/10 digits",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (reporterContact.length == 10) Color(0xFF4CAF50) else Color.Gray
                            )
                        },
                        isError = reporterContact.isNotEmpty() && reporterContact.length != 10
                    )

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // Your Current Location - REQUIRED
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Your Current Location", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        Text(" *", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }

                    Button(onClick = {
                        if (checkLocationPermission(context)) {
                            isGettingLocation = true
                            locationError = false
                            getCurrentLocation(context) { location ->
                                currentLocation = "Lat: %.6f, Lng: %.6f".format(location.latitude, location.longitude)
                                isGettingLocation = false
                            }
                        }
                        else locationPermissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION))
                    }, modifier = Modifier.fillMaxWidth(), enabled = !isSubmitting && !isGettingLocation, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), shape = RoundedCornerShape(8.dp)) {
                        if (isGettingLocation) { CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)); Text("Getting Location...") }
                        else { Icon(Icons.Default.MyLocation, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Get My Current Location") }
                    }

                    // Show success message if location is obtained
                    if (currentLocation.isNotEmpty()) {
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("📍", fontSize = 20.sp); Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) { Text("Your Location:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold); Text(currentLocation, style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32)) }
                                IconButton(onClick = { currentLocation = "" }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Close, "Clear", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp)) }
                            }
                        }
                    } else if (locationError) {
                        // Show error if location not provided
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️", fontSize = 16.sp); Spacer(Modifier.width(8.dp))
                                Text("Please get your current location before submitting", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Text("Fire Incident Location *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    Text("Enter the exact address or location where the fire is happening", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    OutlinedTextField(value = incidentLocation, onValueChange = { incidentLocation = it }, label = { Text("Fire Location") }, placeholder = { Text("e.g., 123 Main St, Calamba City, Laguna") }, modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 3, enabled = !isSubmitting, shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), focusedLabelColor = Color(0xFFD32F2F)))
                    OutlinedTextField(value = incidentDescription, onValueChange = { incidentDescription = it }, label = { Text("Description (Optional)") }, placeholder = { Text("Describe what you see") }, modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 5, enabled = !isSubmitting, shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), focusedLabelColor = Color(0xFFD32F2F)))

                    Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Text("⚠️", fontSize = 20.sp); Spacer(Modifier.width(8.dp)); Text("For immediate emergencies, please call 911 or 160 directly!", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F)) }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    when {
                        reporterName.isBlank() -> android.widget.Toast.makeText(context, "Please enter your name", android.widget.Toast.LENGTH_SHORT).show()
                        reporterContact.isBlank() -> android.widget.Toast.makeText(context, "Please enter your contact number", android.widget.Toast.LENGTH_SHORT).show()
                        reporterContact.length != 10 -> android.widget.Toast.makeText(context, "Contact number must be 10 digits", android.widget.Toast.LENGTH_SHORT).show()
                        currentLocation.isBlank() -> {
                            locationError = true
                            android.widget.Toast.makeText(context, "Please get your current location", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        incidentLocation.isBlank() -> android.widget.Toast.makeText(context, "Please enter the fire incident location", android.widget.Toast.LENGTH_SHORT).show()
                        else -> {
                            isSubmitting = true
                            submitFireIncidentReport(
                                context = context,
                                reporterName = reporterName,
                                reporterContact = "+63$reporterContact",  // Add +63 prefix when submitting
                                reporterEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown",
                                reporterCurrentLocation = currentLocation,
                                incidentLocation = incidentLocation,
                                description = incidentDescription,
                                onSuccess = { isSubmitting = false; showReportFireDialog = false; android.widget.Toast.makeText(context, "Fire incident reported successfully!", android.widget.Toast.LENGTH_LONG).show() },
                                onFailure = { error -> isSubmitting = false; android.widget.Toast.makeText(context, "Failed: $error", android.widget.Toast.LENGTH_LONG).show() }
                            )
                        }
                    }
                }, enabled = !isSubmitting, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) {
                    if (isSubmitting) { CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)); Text("Submitting...") } else Text("Submit Report")
                }
            },
            dismissButton = { TextButton(onClick = { showReportFireDialog = false }, enabled = !isSubmitting) { Text("Cancel", color = Color.Gray) } },
            containerColor = Color.White
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState, properties = MapProperties(isMyLocationEnabled = isMyLocationEnabled), uiSettings = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)) {
            if (markersReady && greenMarkerIcon != null && redMarkerIcon != null) {
                hydrantsWithValidCoords.forEach { hydrant ->
                    val lat = hydrant.latitude.toDoubleOrNull() ?: return@forEach
                    val lng = hydrant.longitude.toDoubleOrNull() ?: return@forEach
                    val isNearest = nearestHydrant?.let { it.id == hydrant.id && it.municipality == hydrant.municipality } ?: false
                    val markerIcon = when { isNearest -> blueMarkerIcon!!; hydrant.serviceStatus == "In Service" -> greenMarkerIcon!!; else -> redMarkerIcon!! }
                    com.google.maps.android.compose.Marker(state = com.google.maps.android.compose.MarkerState(position = LatLng(lat, lng)), title = "${hydrant.municipality} - ${hydrant.hydrantName}", snippet = "${hydrant.exactLocation} | ${hydrant.serviceStatus}", icon = markerIcon)
                }

                // Fire Incident Marker
                if (showIncidentMarker && incidentMarkerLocation != null && fireIncidentMarkerIcon != null) {
                    com.google.maps.android.compose.Marker(state = com.google.maps.android.compose.MarkerState(position = incidentMarkerLocation!!), title = "🔥 FIRE INCIDENT", snippet = "Reporter's Location", icon = fireIncidentMarkerIcon)
                }
            }
            directionsResult?.let { Polyline(points = it.polylinePoints, color = Color(0xFF2196F3), width = 12f) }
        }

        // Fire Incident Info Card (Top)
        if (showIncidentInfoCard && incidentMarkerLocation != null) {
            Card(modifier = Modifier.align(Alignment.TopCenter).padding(top = 70.dp, start = 16.dp, end = 16.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Text("🔥", fontSize = 24.sp); Spacer(Modifier.width(8.dp)); Text("FIRE INCIDENT LOCATION", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F)) }
                        IconButton(onClick = { showIncidentInfoCard = false; showIncidentMarker = false; incidentMarkerLocation = null; nearestHydrant = null; directionsResult = null }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Close, "Close", tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp)) }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Lat: ${String.format("%.6f", incidentMarkerLocation!!.latitude)}, Lng: ${String.format("%.6f", incidentMarkerLocation!!.longitude)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF666666))

                    if (nearestHydrant != null) {
                        Spacer(Modifier.height(8.dp))
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Nearest Hydrant: ${nearestHydrant!!.hydrantName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    Text(nearestHydrant!!.exactLocation, style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                                    Text(if (directionsResult != null) "${directionsResult!!.distance} • ${directionsResult!!.duration}" else nearestHydrantDistance?.let { if (it < 1) "%.0f meters".format(it * 1000) else "%.2f km".format(it) } ?: "Calculating...", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Menu Button
        FloatingActionButton(onClick = { isDrawerOpen = true }, modifier = Modifier.align(Alignment.TopStart).padding(start = 12.dp, top = 12.dp).statusBarsPadding().size(40.dp), containerColor = Color.White, contentColor = Color(0xFF5F6368), elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp), shape = RoundedCornerShape(8.dp)) { Text("≡", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF5F6368), fontWeight = FontWeight.Bold) }

        // Emergency Button and Mail Button Row
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 12.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            /// Mail Button
            Button(
                onClick = onMailClick,  // Changed from Toast to callback
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Text("✉️", fontSize = 18.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Mail",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Emergency Button
            Button(
                onClick = { showEmergencyDialog = true },
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Emergency",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Zoom Controls
        Column(modifier = Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = if (nearestHydrant != null && !showIncidentInfoCard) 260.dp else 16.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FloatingActionButton(onClick = { if (checkLocationPermission(context)) { isMyLocationEnabled = true; getCurrentLocation(context) { location -> scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f)) } } } else locationPermissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)) }, modifier = Modifier.size(40.dp), containerColor = Color.White, elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp), shape = RoundedCornerShape(8.dp)) { Icon(Icons.Default.MyLocation, "My Location", Modifier.size(20.dp), tint = Color(0xFF5F6368)) }
            FloatingActionButton(onClick = { scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomTo(cameraPositionState.position.zoom + 1f)) } }, modifier = Modifier.size(40.dp), containerColor = Color.White, elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp), shape = RoundedCornerShape(8.dp)) { Text("+", style = MaterialTheme.typography.titleLarge, color = Color(0xFF5F6368), fontWeight = FontWeight.Bold) }
            FloatingActionButton(onClick = { scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomTo(cameraPositionState.position.zoom - 1f)) } }, modifier = Modifier.size(40.dp), containerColor = Color.White, elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp), shape = RoundedCornerShape(8.dp)) { Text("−", style = MaterialTheme.typography.titleLarge, color = Color(0xFF5F6368), fontWeight = FontWeight.Bold) }
        }

        // Nearest Hydrant Card (Bottom) - only when NOT showing incident card
        if (nearestHydrant != null && !showIncidentInfoCard) {
            Card(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(8.dp)); Text("Nearest Hydrant", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3)) }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = if (nearestHydrant!!.serviceStatus == "In Service") Color(0xFF4CAF50) else Color(0xFFEF5350), shape = RoundedCornerShape(20.dp)) { Text(nearestHydrant!!.serviceStatus, Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color.White, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.width(8.dp)); IconButton(onClick = { nearestHydrant = null; directionsResult = null }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Close, "Close", tint = Color.Gray, modifier = Modifier.size(20.dp)) }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(nearestHydrant!!.hydrantName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                    Spacer(Modifier.height(4.dp))
                    Text(nearestHydrant!!.exactLocation, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF757575))
                    Text("Municipality: ${nearestHydrant!!.municipality}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF757575))
                    Spacer(Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(20.dp)) {
                            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp))
                                Text(if (directionsResult != null) "${directionsResult!!.distance} • ${directionsResult!!.duration}" else nearestHydrantDistance?.let { if (it < 1) "%.0f meters away".format(it * 1000) else "%.2f km away".format(it) } ?: "Calculating...", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1976D2), fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Button(onClick = {
                            userCurrentLocation?.let { currentLoc ->
                                val hydrantLat = nearestHydrant!!.latitude.toDoubleOrNull(); val hydrantLng = nearestHydrant!!.longitude.toDoubleOrNull()
                                if (hydrantLat != null && hydrantLng != null) { isLoadingDirections = true; scope.launch { directionsResult = fetchDirections(origin = currentLoc, destination = LatLng(hydrantLat, hydrantLng)); isLoadingDirections = false; if (directionsResult == null) android.widget.Toast.makeText(context, "Could not fetch directions", android.widget.Toast.LENGTH_SHORT).show() } }
                            } ?: run {
                                if (checkLocationPermission(context)) { getCurrentLocation(context) { location -> userCurrentLocation = LatLng(location.latitude, location.longitude); val hydrantLat = nearestHydrant!!.latitude.toDoubleOrNull(); val hydrantLng = nearestHydrant!!.longitude.toDoubleOrNull(); if (hydrantLat != null && hydrantLng != null) { isLoadingDirections = true; scope.launch { directionsResult = fetchDirections(origin = LatLng(location.latitude, location.longitude), destination = LatLng(hydrantLat, hydrantLng)); isLoadingDirections = false } } } }
                                else android.widget.Toast.makeText(context, "Location permission required", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }, enabled = !isLoadingDirections, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), shape = RoundedCornerShape(20.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                            if (isLoadingDirections) CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp) else Icon(Icons.Default.Directions, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp)); Text(if (isLoadingDirections) "Loading..." else "Directions", color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Loading
        if (hydrantUiState.isLoading || isSearchingNearestHydrant) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { CircularProgressIndicator(color = Color(0xFFFF6B35)); Spacer(Modifier.height(16.dp)); Text(if (isSearchingNearestHydrant) "Finding nearest hydrant..." else "Loading hydrants...") }
                }
            }
        }

        // Scrim
        if (scrimAlpha > 0f) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = scrimAlpha)).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { isDrawerOpen = false })

        // Drawer
        Surface(modifier = Modifier.fillMaxHeight().width(280.dp).offset(x = drawerOffsetX).align(Alignment.CenterStart), color = Color.White, shadowElevation = if (isDrawerOpen) 16.dp else 0.dp) {
            Column(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxWidth().background(Color(0xFFFF6B35)).statusBarsPadding().padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column {
                            Text(buildAnnotatedString { withStyle(SpanStyle(color = Color.White)) { append("Fire") }; withStyle(SpanStyle(color = Color(0xFFFFE0B2))) { append("Grid") } }, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp)); Text("Map Options", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        }
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Hydrants: ${hydrantsWithValidCoords.size}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp)); Text(" ${hydrantsWithValidCoords.count { it.serviceStatus == "In Service" }}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium); Spacer(Modifier.width(8.dp)); Icon(Icons.Default.LocationOn, null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp)); Text(" ${hydrantsWithValidCoords.count { it.serviceStatus != "In Service" }}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium) }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                // Find Nearest
                Surface(onClick = { isDrawerOpen = false; directionsResult = null; if (checkLocationPermission(context)) { isSearchingNearestHydrant = true; isMyLocationEnabled = true; getCurrentLocation(context) { location -> userCurrentLocation = LatLng(location.latitude, location.longitude); scope.launch { val (nearest, distance) = findNearestHydrant(location.latitude, location.longitude, hydrantUiState.allHydrants); if (nearest != null) { nearestHydrant = nearest; nearestHydrantDistance = distance; nearest.latitude.toDoubleOrNull()?.let { lat -> nearest.longitude.toDoubleOrNull()?.let { lng -> cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 16f)) } } } else android.widget.Toast.makeText(context, "No hydrants found", android.widget.Toast.LENGTH_SHORT).show(); isSearchingNearestHydrant = false } } } else locationPermissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)) }, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Search, null, tint = Color(0xFF2196F3), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text("Find Nearest Hydrant", style = MaterialTheme.typography.bodyLarge) }
                }
                HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = Color(0xFFE0E0E0))

                // Reset
                Surface(onClick = { isDrawerOpen = false; nearestHydrant = null; showIncidentMarker = false; showIncidentInfoCard = false; incidentMarkerLocation = null; directionsResult = null; scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(lagunaLake, 10f)) } }, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Refresh, null, tint = Color(0xFFFF6B35), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text("Reset to Laguna View", style = MaterialTheme.typography.bodyLarge) }
                }

                // My Location
                Surface(onClick = { isDrawerOpen = false; nearestHydrant = null; if (checkLocationPermission(context)) { isMyLocationEnabled = true; getCurrentLocation(context) { location -> scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f)) } } } else locationPermissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)) }, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.MyLocation, null, tint = Color(0xFF5F6368), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text("Go to My Location", style = MaterialTheme.typography.bodyLarge) }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp), color = Color(0xFFE0E0E0))

                Text("Map Legend", style = MaterialTheme.typography.labelLarge, color = Color.Gray, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text("In Service Hydrant") }
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, tint = Color(0xFFEF5350), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text("Out of Service Hydrant") }
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text("Nearest Hydrant") }
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, tint = Color(0xFFFF5722), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text("Fire Incident Location") }

                Spacer(Modifier.weight(1f))
                HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = Color(0xFFE0E0E0))
                Text("FireGrid v1.0.0", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(16.dp).fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FireIncidentReportsScreen(
    onBack: () -> Unit,
    onViewOnMap: (Double, Double) -> Unit = { _, _ -> }
) {
    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""

    var reports by remember { mutableStateOf<List<FireIncidentReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    var isAdmin by remember { mutableStateOf(false) }
    var showReportDetailDialog by remember { mutableStateOf(false) }
    var selectedReport by remember { mutableStateOf<FireIncidentReport?>(null) }

    // Check if user is admin
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { document ->
                    isAdmin = document.exists()
                }
        }
    }

    // Load fire incident reports
    LaunchedEffect(Unit) {
        firestore.collection("fire_incident_reports")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }

                val reportsList = snapshot?.documents?.mapNotNull { doc ->
                    FireIncidentReport(
                        documentId = doc.id,
                        reporterName = doc.getString("reporterName") ?: "",
                        reporterContact = doc.getString("reporterContact") ?: "",
                        reporterEmail = doc.getString("reporterEmail") ?: "",
                        reporterCurrentLocation = doc.getString("reporterCurrentLocation") ?: "",
                        incidentLocation = doc.getString("incidentLocation") ?: "",
                        description = doc.getString("description") ?: "",
                        timestamp = doc.getTimestamp("timestamp"),
                        status = doc.getString("status") ?: "pending"
                    )
                } ?: emptyList()

                reports = reportsList
                isLoading = false
            }
    }

    val filteredReports = when (selectedTab) {
        1 -> reports.filter { it.status == "pending" }
        2 -> reports.filter { it.status == "acknowledged" }
        3 -> reports.filter { it.status == "resolved" }
        else -> reports
    }

    if (showReportDetailDialog && selectedReport != null) {
        FireIncidentReportDetailDialog(
            report = selectedReport!!,
            isAdmin = isAdmin,
            onDismiss = {
                showReportDetailDialog = false
                selectedReport = null
            },
            onStatusChange = { newStatus ->
                firestore.collection("fire_incident_reports")
                    .document(selectedReport!!.documentId)
                    .update("status", newStatus)
                    .addOnSuccessListener {
                        showReportDetailDialog = false
                        selectedReport = null
                    }
            },
            onViewOnMap = { lat, lng ->
                showReportDetailDialog = false
                selectedReport = null
                onViewOnMap(lat, lng)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Fire Incident Reports",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF2196F3),
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("All")
                            if (reports.isNotEmpty()) {
                                Spacer(Modifier.width(4.dp))
                                Badge { Text("${reports.size}") }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔴 Pending")
                            val count = reports.count { it.status == "pending" }
                            if (count > 0) {
                                Spacer(Modifier.width(4.dp))
                                Badge(containerColor = Color(0xFFD32F2F)) {
                                    Text("$count", color = Color.White)
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🟡 Acknowledged")
                            val count = reports.count { it.status == "acknowledged" }
                            if (count > 0) {
                                Spacer(Modifier.width(4.dp))
                                Badge(containerColor = Color(0xFFFFA000)) {
                                    Text("$count", color = Color.White)
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🟢 Resolved")
                            val count = reports.count { it.status == "resolved" }
                            if (count > 0) {
                                Spacer(Modifier.width(4.dp))
                                Badge(containerColor = Color(0xFF4CAF50)) {
                                    Text("$count", color = Color.White)
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2196F3))
                }
            } else if (filteredReports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (selectedTab) {
                                1 -> "No pending reports"
                                2 -> "No acknowledged reports"
                                3 -> "No resolved reports"
                                else -> "No fire incident reports"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredReports) { report ->
                        FireIncidentReportCard(
                            report = report,
                            onClick = {
                                selectedReport = report
                                showReportDetailDialog = true
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun FireIncidentReportCard(
    report: FireIncidentReport,
    onClick: () -> Unit
) {
    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", java.util.Locale.getDefault())
    val formattedTime = report.timestamp?.toDate()?.let { dateFormat.format(it) } ?: "Unknown time"

    val statusColor = when (report.status) {
        "pending" -> Color(0xFFD32F2F)
        "acknowledged" -> Color(0xFFFFA000)
        "resolved" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    val statusEmoji = when (report.status) {
        "pending" -> "🔴"
        "acknowledged" -> "🟡"
        "resolved" -> "🟢"
        else -> "⚪"
    }

    val statusBgColor = when (report.status) {
        "pending" -> Color(0xFFFFEBEE)
        "acknowledged" -> Color(0xFFFFF8E1)
        "resolved" -> Color(0xFFE8F5E9)
        else -> Color(0xFFF5F5F5)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = statusBgColor,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(statusEmoji, fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = report.status.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Top) {
                Text("🔥", fontSize = 24.sp)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Fire Incident Location",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = report.incidentLocation,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👤", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Reported by: ${report.reporterName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }

            if (report.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = report.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClick) {
                    Text("View Details", color = Color(0xFF2196F3))
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FireIncidentReportDetailDialog(
    report: FireIncidentReport,
    isAdmin: Boolean,
    onDismiss: () -> Unit,
    onStatusChange: (String) -> Unit,
    onViewOnMap: (Double, Double) -> Unit
) {
    val dateFormat = java.text.SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", java.util.Locale.getDefault())
    val formattedTime = report.timestamp?.toDate()?.let { dateFormat.format(it) } ?: "Unknown time"

    val statusColor = when (report.status) {
        "pending" -> Color(0xFFD32F2F)
        "acknowledged" -> Color(0xFFFFA000)
        "resolved" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    val coordinates = report.reporterCurrentLocation.let {
        try {
            val regex = """Lat:\s*([-\d.]+),\s*Lng:\s*([-\d.]+)""".toRegex()
            val match = regex.find(it)
            if (match != null) {
                Pair(match.groupValues[1].toDouble(), match.groupValues[2].toDouble())
            } else null
        } catch (e: Exception) { null }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 28.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Fire Incident Report",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Status
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = when (report.status) {
                        "pending" -> Color(0xFFFFEBEE)
                        "acknowledged" -> Color(0xFFFFF8E1)
                        "resolved" -> Color(0xFFE8F5E9)
                        else -> Color(0xFFF5F5F5)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status:", fontWeight = FontWeight.Bold)
                        Surface(
                            color = statusColor,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = report.status.replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Reporter Info
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("👤 Reporter Information", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                        Spacer(Modifier.height(8.dp))
                        Text("Name: ${report.reporterName}")
                        Text("Contact: ${report.reporterContact}")
                        Text("Email: ${report.reporterEmail}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Reporter Location
                if (report.reporterCurrentLocation.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📍 Reporter's Location", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Spacer(Modifier.height(4.dp))
                            Text(report.reporterCurrentLocation)
                        }
                    }
                }

                // Fire Location
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("🔥 Fire Incident Location", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        Spacer(Modifier.height(4.dp))
                        Text(report.incidentLocation, fontWeight = FontWeight.Medium)
                    }
                }

                // Description
                if (report.description.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📝 Description", fontWeight = FontWeight.Bold, color = Color(0xFFF57C00))
                            Spacer(Modifier.height(4.dp))
                            Text(report.description)
                        }
                    }
                }

                // Admin Actions
                if (isAdmin && report.status != "resolved") {
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Text("Admin Actions", fontWeight = FontWeight.Bold, color = Color(0xFF666666))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (report.status == "pending") {
                            Button(
                                onClick = { onStatusChange("acknowledged") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                            ) {
                                Text("Acknowledge", fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = { onStatusChange("resolved") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Mark Resolved", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (coordinates != null) {
                    Button(
                        onClick = { onViewOnMap(coordinates.first, coordinates.second) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.LocationOn, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("View on Map")
                    }
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        },
        dismissButton = null,
        containerColor = Color.White
    )
}

fun checkLocationPermission(context: android.content.Context): Boolean {
    return androidx.core.content.ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: android.content.Context,
    onLocationReceived: (android.location.Location) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            onLocationReceived(it)
        } ?: run {
            android.widget.Toast.makeText(
                context,
                "Unable to get current location",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
}

// =====================================================
// UPDATED FavoritesScreen (Home) Header (around line 2600)
// =====================================================
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    scrollState: androidx.compose.foundation.lazy.LazyListState = androidx.compose.foundation.lazy.rememberLazyListState(),
    onMunicipalityClick: (String) -> Unit
) {
    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    val municipalities = listOf(
        "Alaminos", "Bay", "Biñan", "Cabuyao", "Calamba", "Calauan",
        "Cavinti", "Famy", "Kalayaan", "Liliw", "Los Baños", "Luisiana",
        "Lumban", "Mabitac", "Magdalena", "Majayjay", "Nagcarlan", "Paete",
        "Pagsanjan", "Pakil", "Pangil", "Pila", "Rizal", "San Pablo", "San Pedro",
        "Santa Cruz", "Santa Maria", "Santa Rosa", "Siniloan", "Victoria"
    )

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hydrantViewModel.loadAllHydrants()
    }

    val totalHydrants = hydrantUiState.allHydrants.size

    val filteredMunicipalities = if (searchQuery.isEmpty()) {
        municipalities
    } else {
        val query = searchQuery.trim()
        municipalities.filter {
            it.startsWith(query, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // ✅ REDUCED Header with logo, title and search button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6B35))
                .systemBarsPadding()
                .padding(horizontal = 14.dp)  // ✅ Reduced from 16dp
                .padding(bottom = 10.dp)       // ✅ Reduced from 12dp
        ) {
            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search", color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                isSearchActive = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close search",
                                tint = Color.Gray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color(0xFFFF6B35)
                    )
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),  // ✅ Reduced from 12dp
                        modifier = Modifier.weight(1f)
                    ) {
                        // ✅ REDUCED Laguna Logo
                        Image(
                            painter = painterResource(id = R.drawable.laguna_logo),
                            contentDescription = "Laguna Logo",
                            modifier = Modifier.size(68.dp)  // ✅ Reduced from 80dp
                        )

                        Column {
                            Text(
                                text = "Laguna",
                                style = MaterialTheme.typography.headlineMedium,
                                fontSize = 28.sp,  // ✅ Slightly reduced
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Municipalities",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp,  // ✅ Slightly reduced
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(3.dp))  // ✅ Reduced from 4dp
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),  // ✅ Reduced padding
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (hydrantUiState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),  // ✅ Reduced from 16dp
                                            strokeWidth = 2.dp,
                                            color = Color(0xFFFF6B35)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))  // ✅ Reduced from 8dp
                                        Text(
                                            text = "Loading...",
                                            style = MaterialTheme.typography.bodySmall,  // ✅ Reduced size
                                            color = Color.Black
                                        )
                                    } else {
                                        Text(
                                            text = "Total Hydrant : $totalHydrants",
                                            style = MaterialTheme.typography.bodySmall,  // ✅ Reduced size
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ✅ REDUCED Search button
                    IconButton(
                        onClick = { isSearchActive = true },
                        modifier = Modifier
                            .size(50.dp)  // ✅ Reduced from 56dp
                            .background(Color.White, shape = RoundedCornerShape(10.dp))  // ✅ Reduced corner radius
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFFF6B35),
                            modifier = Modifier.size(26.dp)  // ✅ Reduced from 28dp
                        )
                    }
                }
            }
        }

        // Municipality list remains the same...
        if (filteredMunicipalities.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No municipalities found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredMunicipalities) { municipality ->
                    MunicipalityButton(
                        name = municipality,
                        onClick = { onMunicipalityClick(municipality) }
                    )
                }
            }
        }
    }
}

@Composable
fun MunicipalityButton(name: String, onClick: () -> Unit) {
    // Get drawable resource ID for municipality logo if it exists
    val logoResId = when (name) {
        "Alaminos" -> R.drawable.alaminos_logo
        "Bay" -> R.drawable.bay_logo
        "Biñan" -> R.drawable.binan_logo
        "Cabuyao" -> R.drawable.cabuyao_logo
        "Calamba" -> R.drawable.calamba_logo
        "Calauan" -> R.drawable.calauan_logo
        "Cavinti" -> R.drawable.cavinti_logo
        "Famy" -> R.drawable.famy_logo
        "Kalayaan" -> R.drawable.kalayaan_logo
        "Liliw" -> R.drawable.liliw_logo
        "Los Baños" -> R.drawable.los_banos_logo
        "Luisiana" -> R.drawable.luisiana_logo
        "Lumban" -> R.drawable.lumban_logo
        "Mabitac" -> R.drawable.mabitac_logo
        "Magdalena" -> R.drawable.magdalena_logo
        "Majayjay" -> R.drawable.majayjay_logo
        "Nagcarlan" -> R.drawable.nagcarlan_logo
        "Paete" -> R.drawable.paete_logo
        "Pagsanjan" -> R.drawable.pagsanjan_logo
        "Pakil" -> R.drawable.pakil_logo
        "Pangil" -> R.drawable.pangil_logo
        "Pila" -> R.drawable.pila_logo
        "Rizal" -> R.drawable.rizal_logo
        "San Pablo" -> R.drawable.san_pablo_logo
        "San Pedro" -> R.drawable.san_pedro_logo
        "Santa Cruz" -> R.drawable.santa_cruz_logo
        "Santa Maria" -> R.drawable.santa_maria_logo
        "Santa Rosa" -> R.drawable.santa_rosa_logo
        "Siniloan" -> R.drawable.siniloan_logo
        "Victoria" -> R.drawable.victoria_logo
        else -> null
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Show logo if available
                if (logoResId != null) {
                    Image(
                        painter = painterResource(id = logoResId),
                        contentDescription = "$name Logo",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF212121),
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "View $name",
                tint = Color(0xFFFF6B35)
            )
        }
    }
}

// =====================================================
// UPDATED ProfileScreen - Replace your existing one
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userName: String,
    userEmail: String,
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpFaqClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onReportProblemClick: () -> Unit,
    onAboutAppClick: () -> Unit,
    onTermsPrivacyClick: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel()
    val firestore = Firebase.firestore
    var isAdmin by remember { mutableStateOf(false) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    LaunchedEffect(userEmail) {
        firestore.collection("admins")
            .document(userEmail.lowercase())
            .get()
            .addOnSuccessListener { document ->
                isAdmin = document.exists()
                isCheckingAdmin = false
            }
            .addOnFailureListener {
                isAdmin = false
                isCheckingAdmin = false
            }
    }

    val initials = userName.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // ✅ REDUCED Header with profile info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6B35))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)  // ✅ Reduced from 24dp
                    .padding(bottom = 12.dp)       // ✅ Reduced from 16dp
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)  // ✅ Reduced from 64dp
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 22.sp,  // ✅ Slightly reduced font
                        color = Color(0xFFFF6B35),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))  // ✅ Reduced from 16dp

                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 22.sp,  // ✅ Slightly reduced font
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    if (isCheckingAdmin) {
                        Spacer(modifier = Modifier.height(6.dp))  // ✅ Reduced from 8dp
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),  // ✅ Reduced from 24dp
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else if (isAdmin) {
                        Spacer(modifier = Modifier.height(6.dp))  // ✅ Reduced from 8dp
                        Surface(
                            modifier = Modifier.wrapContentWidth(),
                            color = Color.White,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),  // ✅ Reduced from 12dp
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.bodyMedium,  // ✅ Reduced size
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Admin",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))  // ✅ Reduced from 16dp

        // All menu items in one card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color.White,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 1.dp
        ) {
            Column {
                // ✅ Settings - ALWAYS VISIBLE FOR ALL USERS
                ProfileMenuItem(
                    title = "Settings",
                    onClick = onSettingsClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                ProfileMenuItem(
                    title = "Help / FAQ",
                    onClick = onHelpFaqClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                ProfileMenuItem(
                    title = "Contact Support",
                    onClick = onContactSupportClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                ProfileMenuItem(
                    title = "Report a Problem",
                    onClick = onReportProblemClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                ProfileMenuItem(
                    title = "About the App",
                    onClick = onAboutAppClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                ProfileMenuItem(
                    title = "Terms & Privacy Policy",
                    onClick = onTermsPrivacyClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                ProfileMenuItem(
                    title = "Logout",
                    onClick = {
                        viewModel.signOut()
                        onLogout()
                    },
                    isLogout = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// =====================================================
// NEW SCREEN: About the App Screen
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About the App",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // App Logo/Name
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFFFF6B35))) {
                        append("Fire")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFFFF4500))) {
                        append("Grid")
                    }
                },
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Description Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "FireGrid is a fire hydrant management and mapping application for Laguna province. It helps fire departments and local government units track, manage, and locate fire hydrants efficiently.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color(0xFF666666)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Features Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Add and manage fire hydrants", color = Color(0xFF666666))
                    Text("• View hydrant locations on map", color = Color(0xFF666666))
                    Text("• Track service status", color = Color(0xFF666666))
                    Text("• Weather information by municipality", color = Color(0xFF666666))
                    Text("• Search municipalities", color = Color(0xFF666666))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "© 2025 FireGrid Team",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "All Rights Reserved",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// =====================================================
// NEW SCREEN: Terms & Privacy Policy Screen
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsPrivacyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Terms & Privacy Policy",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Terms of Service",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B35)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "By using FireGrid, you agree to use this application responsibly and only for its intended purpose of fire hydrant management and emergency preparedness.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B35)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "We collect and store:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Your name and email for account management", color = Color(0xFF666666))
                        Text("• Location data when you use mapping features", color = Color(0xFF666666))
                        Text("• Fire hydrant data you submit", color = Color(0xFF666666))
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Data Security",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B35)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your data is securely stored using Firebase and is only accessible to authorized personnel. We do not sell or share your personal information with third parties.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Last Updated: December 2025",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

// =====================================================
// NEW SCREEN: Help / FAQ Screen
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpFaqScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Help / FAQ",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                FaqCard(
                    question = "How do I add a fire hydrant?",
                    answer = "Go to Home > Select a municipality > Tap 'Add Fire Hydrant' and fill in the required details including hydrant name, location, coordinates, type/color, and service status."
                )
            }

            item {
                FaqCard(
                    question = "How do I view hydrant locations on the map?",
                    answer = "Select a municipality from Home, then tap 'Fire Hydrant Map' to see all hydrants pinned on the map. Green pins indicate hydrants that are 'In Service', while red pins indicate 'Out of Service'."
                )
            }

            item {
                FaqCard(
                    question = "What do the hydrant colors mean?",
                    answer = "Green markers indicate hydrants that are 'In Service' and operational. Red markers indicate hydrants that are 'Out of Service' and may need maintenance or repair."
                )
            }

            item {
                FaqCard(
                    question = "How do I edit hydrant information?",
                    answer = "Go to Fire Hydrant List, find the hydrant you want to edit, and tap 'Edit Info'. You can update all the hydrant details and save your changes."
                )
            }

            item {
                FaqCard(
                    question = "How do I delete a hydrant?",
                    answer = "Go to Fire Hydrant List, tap 'Edit Info' on the hydrant you want to delete, then tap the 'Delete Hydrant' button. Confirm the deletion when prompted."
                )
            }

            item {
                FaqCard(
                    question = "How do I become an admin?",
                    answer = "Tap 'Apply as Admin' on your profile page to submit an admin application request. Your request will be reviewed by the system administrators."
                )
            }

            item {
                FaqCard(
                    question = "Why can't I see the weather data?",
                    answer = "Weather data requires an internet connection. Make sure you're connected to the internet. If the problem persists, try refreshing the page or check back later."
                )
            }

            item {
                FaqCard(
                    question = "How accurate are the hydrant locations?",
                    answer = "Hydrant locations are based on the coordinates entered when adding a hydrant. For best accuracy, use GPS coordinates from your device when at the hydrant location."
                )
            }
        }
    }
}

@Composable
fun FaqCard(question: String, answer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
        }
    }
}

// =====================================================
// NEW SCREEN: Contact Support Screen
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contact Support",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Need help? Reach out to us through any of the following channels:",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF666666)
            )

            // Email Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE0B2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✉️", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "project.fhydrant@gmail.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            // Phone Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC8E6C9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📞", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Phone",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+63 998 042 6652",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            // Hours Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFBBDEFB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🕐", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Business Hours",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mon-Fri, 8:00 AM - 5:00 PM",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "We typically respond within 24-48 hours during business days.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// =====================================================
// NEW SCREEN: Report a Problem Screen
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportProblemScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var problemType by rememberSaveable { mutableStateOf("Bug") }
    var problemDescription by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isSubmitting by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Report a Problem",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Help us improve by reporting any issues you encounter.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF666666)
            )

            // Problem Type Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Problem Type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = problemType,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("Bug", "Incorrect Data", "App Crash", "Feature Request", "Other").forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        problemType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Description Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = problemDescription,
                        onValueChange = { problemDescription = it },
                        placeholder = { Text("Describe the problem in detail...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5,
                        maxLines = 8,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Submit Button
            Button(
                onClick = {
                    if (problemDescription.isNotBlank()) {
                        isSubmitting = true
                        // Simulate submission
                        android.widget.Toast.makeText(
                            context,
                            "Report submitted successfully. Thank you!",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                        onBack()
                    } else {
                        android.widget.Toast.makeText(
                            context,
                            "Please describe the problem",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Submit Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onAddAdminClick: () -> Unit,
    onRemoveAdminClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""
    val firestore = Firebase.firestore

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    // =====================================================
    // NEW: NOTIFICATION STATES
    // =====================================================
    var notificationsEnabled by remember { mutableStateOf(true) }
    var emergencyAlertsEnabled by remember { mutableStateOf(true) }
    var hydrantUpdatesEnabled by remember { mutableStateOf(true) }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }

    // Check if notification permission is granted
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    // Permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            notificationsEnabled = true
            // Show success notification
            showTestNotification(
                context = context,
                title = "🔔 Notifications Enabled!",
                message = "You will now receive FireGrid alerts and updates."
            )
        }
    }

    // Initialize notification channels when screen loads
    LaunchedEffect(Unit) {
        createNotificationChannels(context)

        // Load saved preferences
        val prefs = context.getSharedPreferences("firegrid_prefs", Context.MODE_PRIVATE)
        notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        emergencyAlertsEnabled = prefs.getBoolean("emergency_enabled", true)
        hydrantUpdatesEnabled = prefs.getBoolean("hydrant_updates_enabled", true)
    }

    // Check if user is admin
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { document ->
                    isAdmin = document.exists()
                    isCheckingAdmin = false
                }
                .addOnFailureListener {
                    isAdmin = false
                    isCheckingAdmin = false
                }
        } else {
            isAdmin = false
            isCheckingAdmin = false
        }
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onSuccess = {
                showChangePasswordDialog = false
                android.widget.Toast.makeText(
                    context,
                    "Password changed successfully!",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    // =====================================================
    // NEW: NOTIFICATION SETTINGS DIALOG
    // =====================================================
    if (showNotificationSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationSettingsDialog = false },
            title = {
                Text(
                    text = "🔔 Notification Settings",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Main notification toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Notifications",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Receive all FireGrid alerts",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = notificationsEnabled && hasNotificationPermission,
                            onCheckedChange = { enabled ->
                                if (enabled && !hasNotificationPermission) {
                                    // Request permission on Android 13+
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    } else {
                                        hasNotificationPermission = true
                                        notificationsEnabled = true
                                    }
                                } else {
                                    notificationsEnabled = enabled
                                    saveNotificationPreferences(context, enabled, emergencyAlertsEnabled, hydrantUpdatesEnabled)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4CAF50)
                            )
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // Emergency alerts toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔥 Emergency Alerts",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Fire incidents & urgent alerts",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = emergencyAlertsEnabled && notificationsEnabled,
                            onCheckedChange = { enabled ->
                                emergencyAlertsEnabled = enabled
                                saveNotificationPreferences(context, notificationsEnabled, enabled, hydrantUpdatesEnabled)
                            },
                            enabled = notificationsEnabled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFD32F2F)
                            )
                        )
                    }

                    // Hydrant updates toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🚒 Hydrant Updates",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Status changes & maintenance",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = hydrantUpdatesEnabled && notificationsEnabled,
                            onCheckedChange = { enabled ->
                                hydrantUpdatesEnabled = enabled
                                saveNotificationPreferences(context, notificationsEnabled, emergencyAlertsEnabled, enabled)
                            },
                            enabled = notificationsEnabled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4CAF50)
                            )
                        )
                    }

                    // Permission warning if needed
                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "⚠️ Permission Required",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notification permission is required to receive alerts.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = {
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Text("Open Settings", color = Color(0xFFE65100))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B35)
                    )
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                // Test notification button
                TextButton(
                    onClick = {
                        if (hasNotificationPermission && notificationsEnabled) {
                            showTestNotification(
                                context = context,
                                title = "🧪 Test Notification",
                                message = "This is a test notification from FireGrid!"
                            )
                            android.widget.Toast.makeText(
                                context,
                                "Test notification sent!",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                "Enable notifications first",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Test", color = Color(0xFF4CAF50))
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Settings Section - ALWAYS VISIBLE
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Change Password Card - ALWAYS VISIBLE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Surface(
                    onClick = { showChangePasswordDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFE0B2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔒", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Change Password",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Update your account password",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Change Password",
                            tint = Color(0xFF757575)
                        )
                    }
                }
            }

            // Admin Settings Section - ONLY VISIBLE FOR ADMINS
            if (isCheckingAdmin) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFFFF6B35)
                    )
                }
            } else if (isAdmin) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Admin Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Add Admin Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Surface(
                        onClick = onAddAdminClick,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFC8E6C9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👑", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Add Admin",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Promote a user to admin",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Add Admin",
                                tint = Color(0xFF757575)
                            )
                        }
                    }
                }

                // Remove Admin Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Surface(
                        onClick = onRemoveAdminClick,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFCDD2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("❌", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Remove Admin",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Revoke admin privileges",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Remove Admin",
                                tint = Color(0xFF757575)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Settings Section - ALWAYS VISIBLE
            Text(
                text = "App Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // =====================================================
            // NOTIFICATIONS CARD - NOW CLICKABLE AND WORKING!
            // =====================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Surface(
                    onClick = { showNotificationSettingsDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFBBDEFB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔔", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Notifications",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (notificationsEnabled && hasNotificationPermission)
                                        "Enabled"
                                    else
                                        "Tap to configure",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (notificationsEnabled && hasNotificationPermission)
                                        Color(0xFF4CAF50)
                                    else
                                        Color.Gray
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status indicator
                            if (notificationsEnabled && hasNotificationPermission) {
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "ON",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Notification Settings",
                                tint = Color(0xFF757575)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "FireGrid Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// =====================================================
// HELPER FUNCTIONS - ADD THESE OUTSIDE THE COMPOSABLE
// =====================================================

/**
 * Create notification channels (required for Android 8.0+)
 */
fun createNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Emergency Channel (High Priority)
        val emergencyChannel = NotificationChannel(
            "emergency_channel",
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
            "hydrant_updates_channel",
            "Hydrant Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Fire hydrant status updates and maintenance alerts"
            enableLights(true)
            lightColor = android.graphics.Color.GREEN
        }

        // General Channel
        val generalChannel = NotificationChannel(
            "general_channel",
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
 * Show a test notification
 */
fun showTestNotification(context: Context, title: String, message: String) {
    // Check permission first
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notification = NotificationCompat.Builder(context, "general_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon - replace with your own
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setSound(soundUri)
        .setVibrate(longArrayOf(0, 250, 250, 250))
        .build()

    with(NotificationManagerCompat.from(context)) {
        try {
            notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

/**
 * Save notification preferences
 */
fun saveNotificationPreferences(
    context: Context,
    notificationsEnabled: Boolean,
    emergencyEnabled: Boolean,
    hydrantUpdatesEnabled: Boolean
) {
    val prefs = context.getSharedPreferences("firegrid_prefs", Context.MODE_PRIVATE)
    prefs.edit().apply {
        putBoolean("notifications_enabled", notificationsEnabled)
        putBoolean("emergency_enabled", emergencyEnabled)
        putBoolean("hydrant_updates_enabled", hydrantUpdatesEnabled)
        apply()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdminScreen(
    onBack: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var email by remember { mutableStateOf("") }
    var uid by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    fun isValidEmail(emailStr: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr.trim()).matches()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Admin",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (!isLoading) onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("👑", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Promote User to Admin",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter the user's email and UID to grant admin privileges",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Email Field
                    Column {
                        Text(
                            text = "Email Address",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessage = ""
                            },
                            placeholder = { Text("Enter user's email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isLoading,
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF6B35),
                                focusedLabelColor = Color(0xFFFF6B35)
                            )
                        )
                    }

                    // UID Field
                    Column {
                        Text(
                            text = "User UID",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uid,
                            onValueChange = {
                                uid = it
                                errorMessage = ""
                            },
                            placeholder = { Text("Enter user's UID") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isLoading,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF6B35),
                                focusedLabelColor = Color(0xFFFF6B35)
                            )
                        )
                    }

                    // Error Message
                    if (errorMessage.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage,
                                    color = Color(0xFFD32F2F),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Promote Button - Green
            Button(
                onClick = {
                    when {
                        email.isBlank() -> {
                            errorMessage = "Please enter an email address"
                        }
                        !isValidEmail(email) -> {
                            errorMessage = "Invalid email format"
                        }
                        uid.isBlank() -> {
                            errorMessage = "Please enter the user's UID"
                        }
                        uid.trim().length < 20 -> {
                            errorMessage = "UID is invalid"
                        }
                        else -> {
                            isLoading = true
                            errorMessage = ""

                            val emailToSave = email.trim().lowercase()
                            val uidToSave = uid.trim()

                            // Step 1: Try to find user by UID first, then by email
                            firestore.collection("users")
                                .document(uidToSave)
                                .get()
                                .addOnSuccessListener { userDoc ->
                                    if (userDoc.exists()) {
                                        // Found by UID document ID
                                        val storedEmail = userDoc.getString("email")?.lowercase() ?: ""

                                        if (storedEmail != emailToSave) {
                                            isLoading = false
                                            errorMessage = "Email does not match this UID"
                                        } else {
                                            // Check if already admin (using EMAIL as document ID)
                                            firestore.collection("admins")
                                                .document(emailToSave)  // ✅ Use EMAIL as document ID
                                                .get()
                                                .addOnSuccessListener { adminDoc ->
                                                    if (adminDoc.exists()) {
                                                        isLoading = false
                                                        errorMessage = "This user is already an admin"
                                                    } else {
                                                        // Promote to admin (using EMAIL as document ID)
                                                        val displayName = userDoc.getString("displayName") ?: ""

                                                        val adminData = hashMapOf(
                                                            "email" to emailToSave,
                                                            "uid" to uidToSave,
                                                            "displayName" to displayName,
                                                            "isAdmin" to true,
                                                            "promotedAt" to com.google.firebase.Timestamp.now(),
                                                            "promotedBy" to (auth.currentUser?.email ?: "Unknown")
                                                        )

                                                        firestore.collection("admins")
                                                            .document(emailToSave)  // ✅ Use EMAIL as document ID
                                                            .set(adminData)
                                                            .addOnSuccessListener {
                                                                isLoading = false
                                                                onSuccess(emailToSave)
                                                            }
                                                            .addOnFailureListener { e: Exception ->
                                                                isLoading = false
                                                                errorMessage = "Failed to promote: ${e.message}"
                                                            }
                                                    }
                                                }
                                                .addOnFailureListener { e: Exception ->
                                                    isLoading = false
                                                    errorMessage = "Error checking admin status: ${e.message}"
                                                }
                                        }
                                    } else {
                                        // User document not found by UID, try querying by email
                                        firestore.collection("users")
                                            .whereEqualTo("email", emailToSave)
                                            .get()
                                            .addOnSuccessListener { querySnapshot ->
                                                if (querySnapshot.isEmpty) {
                                                    isLoading = false
                                                    errorMessage = "User does not exist in the system"
                                                } else {
                                                    val userDocFromQuery = querySnapshot.documents.first()
                                                    val userUidFromDoc = userDocFromQuery.getString("uid") ?: userDocFromQuery.id

                                                    if (userUidFromDoc != uidToSave) {
                                                        isLoading = false
                                                        errorMessage = "UID does not match. Found UID: $userUidFromDoc"
                                                    } else {
                                                        // Check if already admin (using EMAIL as document ID)
                                                        firestore.collection("admins")
                                                            .document(emailToSave)  // ✅ Use EMAIL as document ID
                                                            .get()
                                                            .addOnSuccessListener { adminDoc ->
                                                                if (adminDoc.exists()) {
                                                                    isLoading = false
                                                                    errorMessage = "This user is already an admin"
                                                                } else {
                                                                    val displayName = userDocFromQuery.getString("displayName") ?: ""

                                                                    val adminData = hashMapOf(
                                                                        "email" to emailToSave,
                                                                        "uid" to uidToSave,
                                                                        "displayName" to displayName,
                                                                        "isAdmin" to true,
                                                                        "promotedAt" to com.google.firebase.Timestamp.now(),
                                                                        "promotedBy" to (auth.currentUser?.email ?: "Unknown")
                                                                    )

                                                                    firestore.collection("admins")
                                                                        .document(emailToSave)  // ✅ Use EMAIL as document ID
                                                                        .set(adminData)
                                                                        .addOnSuccessListener {
                                                                            isLoading = false
                                                                            onSuccess(emailToSave)
                                                                        }
                                                                        .addOnFailureListener { e: Exception ->
                                                                            isLoading = false
                                                                            errorMessage = "Failed to promote: ${e.message}"
                                                                        }
                                                                }
                                                            }
                                                            .addOnFailureListener { e: Exception ->
                                                                isLoading = false
                                                                errorMessage = "Error: ${e.message}"
                                                            }
                                                    }
                                                }
                                            }
                                            .addOnFailureListener { e: Exception ->
                                                isLoading = false
                                                errorMessage = "Error searching for user: ${e.message}"
                                            }
                                    }
                                }
                                .addOnFailureListener { e: Exception ->
                                    isLoading = false
                                    errorMessage = "Error checking user: ${e.message}"
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && email.isNotBlank() && uid.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Promote as Admin",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Cancel Button
            OutlinedButton(
                onClick = { if (!isLoading) onBack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveAdminScreen(
    onBack: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedAdmin by remember { mutableStateOf<Map<String, Any>?>(null) }
    var uid by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingAdmins by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var adminsList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var showUidDialog by remember { mutableStateOf(false) }

    val firestore = Firebase.firestore

    // Function to load admins
    fun loadAdmins() {
        isLoadingAdmins = true
        firestore.collection("admins")
            .orderBy("email")
            .get()
            .addOnSuccessListener { documents ->
                val admins = documents.documents.mapNotNull { doc ->
                    val email = doc.getString("email") ?: doc.id  // Use doc.id as fallback (which is the email)
                    val displayName = doc.getString("displayName") ?: ""
                    val uidValue = doc.getString("uid") ?: ""
                    mapOf(
                        "email" to email,
                        "displayName" to displayName,
                        "uid" to uidValue,
                        "docId" to doc.id  // ✅ Document ID is the email
                    )
                }
                adminsList = admins
                isLoadingAdmins = false
            }
            .addOnFailureListener { e ->
                isLoadingAdmins = false
                errorMessage = "Failed to load admins: ${e.message}"
            }
    }

    // Load admins on screen launch
    LaunchedEffect(Unit) {
        loadAdmins()
    }

    // UID Confirmation Dialog
    if (showUidDialog && selectedAdmin != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showUidDialog = false
                    uid = ""
                    errorMessage = ""
                }
            },
            title = {
                Text(
                    text = "Remove Admin",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF5350)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "You are about to remove:",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedAdmin!!["email"] as String,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Display Name: ${selectedAdmin!!["displayName"] as String}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uid,
                        onValueChange = {
                            uid = it
                            errorMessage = ""
                        },
                        label = { Text("Enter UID to Confirm") },
                        placeholder = { Text("Enter user's UID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF5350),
                            focusedLabelColor = Color(0xFFEF5350)
                        )
                    )

                    if (errorMessage.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage,
                                    color = Color(0xFFD32F2F),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val enteredUid = uid.trim()
                        val actualAdminUid = selectedAdmin!!["uid"] as String
                        val docId = selectedAdmin!!["docId"] as String  // ✅ This is the email

                        when {
                            enteredUid.isEmpty() -> {
                                errorMessage = "Please enter the UID"
                            }
                            enteredUid != actualAdminUid -> {
                                errorMessage = "UID does not match. Expected: $actualAdminUid"
                            }
                            adminsList.size <= 1 -> {
                                errorMessage = "Cannot remove the last admin"
                            }
                            else -> {
                                isLoading = true
                                errorMessage = ""

                                val adminEmail = selectedAdmin!!["email"] as String

                                // ✅ Delete using EMAIL as document ID
                                firestore.collection("admins")
                                    .document(docId)  // docId is the email
                                    .delete()
                                    .addOnSuccessListener {
                                        isLoading = false
                                        showUidDialog = false
                                        uid = ""
                                        selectedAdmin = null

                                        // Reload the admins list to reflect changes
                                        loadAdmins()

                                        // Show success message
                                        onSuccess(adminEmail)
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        errorMessage = "Failed to remove admin: ${e.message}"
                                    }
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Remove Admin")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showUidDialog = false
                        uid = ""
                        errorMessage = ""
                        selectedAdmin = null
                    },
                    enabled = !isLoading
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Remove Admin",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (!isLoading) onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEF5350)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("❌", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Remove Admin Privileges",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF5350)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isLoadingAdmins) {
                            "Loading admins..."
                        } else {
                            "Select an admin to remove (${adminsList.size} admin${if (adminsList.size != 1) "s" else ""})"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Admin List
            if (isLoadingAdmins) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFEF5350))
                }
            } else if (adminsList.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No admins found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(adminsList) { admin ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Surface(
                                onClick = {
                                    selectedAdmin = admin
                                    showUidDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFCDD2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val initials = (admin["displayName"] as String)
                                            .split(" ")
                                            .mapNotNull { it.firstOrNull()?.toString() }
                                            .take(2)
                                            .joinToString("")
                                            .uppercase()
                                            .ifEmpty { "A" }
                                        Text(
                                            text = initials,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color(0xFFD32F2F),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = admin["displayName"] as String,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = admin["email"] as String,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Select",
                                        tint = Color(0xFFEF5350)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Cancel Button
            OutlinedButton(
                onClick = { if (!isLoading) onBack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "Change Password",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Current Password
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it.filter { char -> !char.isWhitespace() } // ✅ Filter out spaces
                        errorMessage = ""
                    },
                    label = { Text("Current Password") },
                    singleLine = true,
                    visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                            Text(
                                text = if (currentPasswordVisible) "👁" else "👁‍🗨️",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B35),
                        focusedLabelColor = Color(0xFFFF6B35)
                    )
                )

                // New Password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it.filter { char -> !char.isWhitespace() } // ✅ Filter out spaces
                        errorMessage = ""
                    },
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Text(
                                text = if (newPasswordVisible) "👁" else "👁‍🗨️",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B35),
                        focusedLabelColor = Color(0xFFFF6B35)
                    )
                )

                // Password requirements hint
                Text(
                    text = "Must have: 12 chars, 1 uppercase, 1 number, 1 special char",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                // Confirm New Password
                OutlinedTextField(
                    value = confirmNewPassword,
                    onValueChange = {
                        confirmNewPassword = it.filter { char -> !char.isWhitespace() } // ✅ Filter out spaces
                        errorMessage = ""
                    },
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Text(
                                text = if (confirmPasswordVisible) "👁" else "👁‍🗨️",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B35),
                        focusedLabelColor = Color(0xFFFF6B35)
                    )
                )

                // Error Message
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validation
                    when {
                        currentPassword.isBlank() -> {
                            errorMessage = "Please enter your current password"
                        }
                        newPassword.isBlank() -> {
                            errorMessage = "Please enter a new password"
                        }
                        confirmNewPassword.isBlank() -> {
                            errorMessage = "Please confirm your new password"
                        }
                        newPassword != confirmNewPassword -> {
                            errorMessage = "New passwords do not match"
                        }
                        currentPassword == newPassword -> {
                            errorMessage = "New password must be different from current password"
                        }
                        else -> {
                            val passwordValidationError = validatePassword(newPassword)
                            if (passwordValidationError != null) {
                                errorMessage = passwordValidationError
                            } else {
                                // Proceed with password change
                                isLoading = true
                                errorMessage = ""

                                val email = user?.email
                                if (email != null) {
                                    // Re-authenticate user first
                                    val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
                                    user.reauthenticate(credential)
                                        .addOnSuccessListener {
                                            // Now update password
                                            user.updatePassword(newPassword)
                                                .addOnSuccessListener {
                                                    isLoading = false
                                                    onSuccess()
                                                }
                                                .addOnFailureListener { e ->
                                                    isLoading = false
                                                    errorMessage = "Failed to update password: ${e.message}"
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            errorMessage = when {
                                                e.message?.contains("password") == true -> "Current password is incorrect"
                                                e.message?.contains("network") == true -> "Network error. Please check your connection"
                                                else -> "Authentication failed: ${e.message}"
                                            }
                                        }
                                } else {
                                    isLoading = false
                                    errorMessage = "User not found. Please log in again."
                                }
                            }
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Change Password")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun FireIncidentAlertDialog(
    incident: FireIncidentAlert,
    onDismiss: () -> Unit,
    onAcknowledge: () -> Unit,
    onGoToLocation: () -> Unit
) {
    var isAcknowledged by remember { mutableStateOf(false) }  // Track if acknowledged

    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", java.util.Locale.getDefault())
    val formattedTime = incident.timestamp?.toDate()?.let { dateFormat.format(it) } ?: "Unknown time"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 28.sp)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("FIRE INCIDENT ALERT", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 18.sp)
                    Text(formattedTime, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Show "Acknowledged" badge if acknowledged
                if (isAcknowledged) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✅", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "This report has been acknowledged",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                // Reporter Info
                Surface(Modifier.fillMaxWidth(), color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("👤 Reporter Information", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                        Spacer(Modifier.height(4.dp))
                        Text("Name: ${incident.reporterName}", style = MaterialTheme.typography.bodyMedium)
                        Text("Contact: ${incident.reporterContact}", style = MaterialTheme.typography.bodyMedium)
                        Text("Email: ${incident.reporterEmail}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                // Reporter Location
                if (incident.reporterCurrentLocation.isNotEmpty() && incident.reporterCurrentLocation != "Not provided") {
                    Surface(Modifier.fillMaxWidth(), color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("📍 Reporter's Location", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Spacer(Modifier.height(4.dp))
                            Text(incident.reporterCurrentLocation, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                // Fire Location
                Surface(Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("🔥 Fire Incident Location", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        Spacer(Modifier.height(4.dp))
                        Text(incident.incidentLocation, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                }

                // Description
                if (incident.description.isNotEmpty()) {
                    Surface(Modifier.fillMaxWidth(), color = Color(0xFFFFF8E1), shape = RoundedCornerShape(8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("📝 Description", fontWeight = FontWeight.Bold, color = Color(0xFFF57C00))
                            Spacer(Modifier.height(4.dp))
                            Text(incident.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Go to Location Button
                Button(
                    onClick = onGoToLocation,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📍 Go to Location")
                }

                // Acknowledge Button - changes to "Acknowledged" after click
                if (!isAcknowledged) {
                    Button(
                        onClick = {
                            onAcknowledge()
                            isAcknowledged = true  // Don't dismiss, just mark as acknowledged
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("✓ Acknowledge")
                    }
                }

                // Close Button - always visible
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color.Gray)
                }
            }
        },
        dismissButton = null,
        containerColor = Color.White
    )
}

// Helper Composables for Profile Screen
@Composable
fun FaqItem(question: String, answer: String) {
    Column {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6B35)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun ContactItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF212121)
        )
    }
}

@Composable
fun ProfileMenuItem(title: String, onClick: () -> Unit, isLogout: Boolean = false) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isLogout) MaterialTheme.colorScheme.error else Color(0xFF212121)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = if (isLogout) MaterialTheme.colorScheme.error else Color(0xFF757575)
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector
) {
    HOME("Home", Icons.Default.Home),
    MAP("Map", Icons.Default.LocationOn),  // Changed from FAVORITES to MAP
    PROFILE("Profile", Icons.Default.AccountBox)
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    HYDRANTTheme {
        LoginScreen(onLoginSuccess = {}, onNavigateToSignUp = {})
    }
}