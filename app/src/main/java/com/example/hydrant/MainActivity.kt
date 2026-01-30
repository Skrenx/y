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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.border
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.Brush
import android.widget.Toast
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow


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
    val timestamp: com.google.firebase.Timestamp?,
    val municipality: String = "" // ✅ NEW: Add municipality field
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

data class FirefighterLocation(
    val uid: String,
    val displayName: String,
    val email: String,
    val latitude: Double,
    val longitude: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

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
    val status: String, // "pending", "acknowledged", "resolved"
    val municipality: String = "" // ✅ NEW: Add municipality field
)

// =====================================================
// ADMIN ROLE SYSTEM
// =====================================================

enum class AdminRole(val displayName: String, val level: Int) {
    CHIEF_ADMINISTRATOR("Chief Administrator", 100),
    MUNICIPALITY_ADMIN("Municipality Admin", 10);

    companion object {
        fun fromString(value: String): AdminRole {
            return when (value.uppercase()) {
                "CHIEF_ADMINISTRATOR", "CHIEF ADMINISTRATOR" -> CHIEF_ADMINISTRATOR
                "MUNICIPALITY_ADMIN", "MUNICIPALITY ADMIN" -> MUNICIPALITY_ADMIN
                else -> MUNICIPALITY_ADMIN
            }
        }
    }
}

data class AdminUser(
    val email: String = "",
    val uid: String = "",
    val displayName: String = "",
    val role: AdminRole = AdminRole.MUNICIPALITY_ADMIN,
    val municipality: String? = null,
    val isAdmin: Boolean = true,
    val promotedAt: com.google.firebase.Timestamp? = null,
    val promotedBy: String = ""
) {
    fun canManageAdmins(): Boolean {
        return role == AdminRole.CHIEF_ADMINISTRATOR
    }

    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "email" to email,
            "uid" to uid,
            "displayName" to displayName,
            "role" to role.name,
            "municipality" to municipality,
            "isAdmin" to isAdmin,
            "promotedAt" to promotedAt,
            "promotedBy" to promotedBy
        )
    }

    companion object {
        fun fromDocument(doc: com.google.firebase.firestore.DocumentSnapshot): AdminUser? {
            return try {
                AdminUser(
                    email = doc.getString("email") ?: doc.id,
                    uid = doc.getString("uid") ?: "",
                    displayName = doc.getString("displayName") ?: "",
                    role = AdminRole.fromString(doc.getString("role") ?: "MUNICIPALITY_ADMIN"),
                    municipality = doc.getString("municipality"),
                    isAdmin = doc.getBoolean("isAdmin") ?: true,
                    promotedAt = doc.getTimestamp("promotedAt"),
                    promotedBy = doc.getString("promotedBy") ?: ""
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

val lagunaMunicipalities = listOf(
    "Alaminos", "Bay", "Biñan", "Cabuyao", "Calamba", "Calauan",
    "Cavinti", "Famy", "Kalayaan", "Liliw", "Los Baños", "Luisiana",
    "Lumban", "Mabitac", "Magdalena", "Majayjay", "Nagcarlan", "Paete",
    "Pagsanjan", "Pakil", "Pangil", "Pila", "Rizal", "San Pablo",
    "San Pedro", "Santa Cruz", "Santa Maria", "Santa Rosa", "Siniloan", "Victoria"
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

// Add this function to detect municipality from coordinates
fun getMunicipalityFromCoordinates(lat: Double, lng: Double): String? {
    // Define boundaries for each municipality (approximate center points with radius)
    val municipalityBounds = mapOf(
        "Alaminos" to Pair(LatLng(14.0649, 121.2458), 0.05),
        "Bay" to Pair(LatLng(14.1872, 121.2850), 0.05),
        "Biñan" to Pair(LatLng(14.3414, 121.0809), 0.05),
        "Cabuyao" to Pair(LatLng(14.2724, 121.1251), 0.05),
        "Calamba" to Pair(LatLng(14.2116, 121.1653), 0.05),
        "Calauan" to Pair(LatLng(14.1469, 121.3204), 0.05),
        "Cavinti" to Pair(LatLng(14.2419, 121.5097), 0.05),
        "Famy" to Pair(LatLng(14.4386, 121.4483), 0.05),
        "Kalayaan" to Pair(LatLng(14.3254, 121.4810), 0.05),
        "Liliw" to Pair(LatLng(14.1286, 121.4342), 0.05),
        "Los Baños" to Pair(LatLng(14.1694, 121.2428), 0.05),
        "Luisiana" to Pair(LatLng(14.1847, 121.5106), 0.05),
        "Lumban" to Pair(LatLng(14.2944, 121.4603), 0.05),
        "Mabitac" to Pair(LatLng(14.4297, 121.4275), 0.05),
        "Magdalena" to Pair(LatLng(14.2000, 121.4297), 0.05),
        "Majayjay" to Pair(LatLng(14.1461, 121.4728), 0.05),
        "Nagcarlan" to Pair(LatLng(14.1364, 121.4150), 0.05),
        "Paete" to Pair(LatLng(14.3636, 121.4847), 0.05),
        "Pagsanjan" to Pair(LatLng(14.2728, 121.4550), 0.05),
        "Pakil" to Pair(LatLng(14.3797, 121.4775), 0.05),
        "Pangil" to Pair(LatLng(14.4039, 121.4656), 0.05),
        "Pila" to Pair(LatLng(14.2333, 121.3644), 0.05),
        "Rizal" to Pair(LatLng(14.1072, 121.3931), 0.05),
        "San Pablo" to Pair(LatLng(14.0689, 121.3256), 0.05),
        "San Pedro" to Pair(LatLng(14.3595, 121.0473), 0.05),
        "Santa Cruz" to Pair(LatLng(14.2814, 121.4156), 0.05),
        "Santa Maria" to Pair(LatLng(14.4708, 121.4250), 0.05),
        "Santa Rosa" to Pair(LatLng(14.3122, 121.1114), 0.05),
        "Siniloan" to Pair(LatLng(14.4247, 121.4486), 0.05),
        "Victoria" to Pair(LatLng(14.2256, 121.3281), 0.05)
    )

    // Find closest municipality
    var closestMunicipality: String? = null
    var minDistance = Double.MAX_VALUE

    for ((municipality, bounds) in municipalityBounds) {
        val center = bounds.first
        val radius = bounds.second

        // Calculate distance
        val distance = Math.sqrt(
            Math.pow(lat - center.latitude, 2.0) +
                    Math.pow(lng - center.longitude, 2.0)
        )

        if (distance < minDistance) {
            minDistance = distance
            closestMunicipality = municipality
        }
    }

    return closestMunicipality
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

/**
 * Update firefighter location when admin interacts with fire incidents
 * This captures the admin's current location and uploads it to Firebase
 */
fun updateFirefighterLocation(
    context: Context,
    onLocationRequired: () -> Unit,
    onSuccess: () -> Unit = {},
    onFailure: (String) -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    if (currentUser == null) {
        onFailure("User not logged in")
        return
    }

    // Check location permission
    if (!checkLocationPermission(context)) {
        onLocationRequired()
        return
    }

    // Get current location and upload to Firebase
    getCurrentLocation(context) { location ->
        val firestore = Firebase.firestore
        val userEmail = currentUser.email?.lowercase() ?: return@getCurrentLocation

        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis(),
            "updatedAt" to com.google.firebase.Timestamp.now()
        )

        // Update location in users/{email}/location/current
        firestore.collection("users")
            .document(userEmail)
            .collection("location")
            .document("current")
            .set(locationData)
            .addOnSuccessListener {
                android.util.Log.d("FirefighterLocation", "Location updated for $userEmail")
                onSuccess()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirefighterLocation", "Failed to update location", e)
                onFailure(e.message ?: "Unknown error")
            }

        // Also update the isAdmin flag in users collection if not already set
        firestore.collection("users")
            .document(userEmail)
            .update("isAdmin", true)
            .addOnFailureListener {
                // If update fails (field doesn't exist), set it
                firestore.collection("users")
                    .document(userEmail)
                    .set(hashMapOf("isAdmin" to true), com.google.firebase.firestore.SetOptions.merge())
            }
    }
}

/**
 * Start continuous firefighter location tracking
 */
fun startFirefighterLocationTracking(
    context: Context,
    onLocationRequired: () -> Unit,
    onSuccess: () -> Unit = {},
    onFailure: (String) -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    if (currentUser == null) {
        onFailure("User not logged in")
        return
    }

    // Check location permission
    if (!checkLocationPermission(context)) {
        onLocationRequired()
        return
    }

    // Check if service is already running
    if (FirefighterLocationService.isServiceRunning()) {
        android.util.Log.d("FirefighterLocation", "Tracking already active")
        onSuccess()
        return
    }

    // Start the foreground service for continuous tracking
    try {
        FirefighterLocationService.startTracking(context)
        onSuccess()
    } catch (e: Exception) {
        android.util.Log.e("FirefighterLocation", "Failed to start tracking service", e)
        onFailure(e.message ?: "Failed to start location tracking")
    }
}

/**
 * Stop firefighter location tracking
 */
fun stopFirefighterLocationTracking(context: Context) {
    FirefighterLocationService.stopTracking(context)
}

fun submitFireIncidentReport(
    context: Context,
    reporterName: String,
    reporterContact: String,
    reporterEmail: String,
    reporterCurrentLocation: String,
    incidentLocation: String,
    description: String,
    municipality: String, // ✅ NEW: Add municipality parameter
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
        "municipality" to municipality, // ✅ NEW: Save municipality
        "timestamp" to com.google.firebase.Timestamp.now(),
        "status" to "pending",
        "notified" to false,
        "notifiedAdmins" to emptyList<String>() // ✅ NEW: Track which admins have been notified
    )

    firestore.collection("fire_incident_reports")
        .add(reportData)
        .addOnSuccessListener { documentReference ->
            android.util.Log.d("FireIncident", "Report submitted: ${documentReference.id}")

            // ✅ NEW: Notify relevant admins (municipality admin + chief administrators)
            notifyRelevantAdmins(
                context = context,
                municipality = municipality,
                reporterName = reporterName,
                incidentLocation = incidentLocation,
                description = description,
                documentId = documentReference.id
            )

            onSuccess()
        }
        .addOnFailureListener { e ->
            android.util.Log.e("FireIncident", "Error submitting report", e)
            onFailure(e.message ?: "Unknown error")
        }
}

/**
 * Notify relevant admins about a fire incident
 * - All Chief Administrators
 * - Municipality Admin for the specific municipality
 */
fun notifyRelevantAdmins(
    context: Context,
    municipality: String,
    reporterName: String,
    incidentLocation: String,
    description: String,
    documentId: String
) {
    val firestore = Firebase.firestore

    // Get all admins
    firestore.collection("admins")
        .get()
        .addOnSuccessListener { documents ->
            val notifiedAdmins = mutableListOf<String>()

            for (doc in documents) {
                val adminEmail = doc.getString("email") ?: doc.id
                val role = doc.getString("role") ?: "MUNICIPALITY_ADMIN"
                val adminMunicipality = doc.getString("municipality")

                val isChiefAdmin = role.uppercase() == "CHIEF_ADMINISTRATOR"
                val isMunicipalityAdmin = role.uppercase() == "MUNICIPALITY_ADMIN" &&
                        adminMunicipality == municipality

                // Notify if Chief Admin OR if Municipality Admin for this municipality
                if (isChiefAdmin || isMunicipalityAdmin) {
                    notifiedAdmins.add(adminEmail)
                    android.util.Log.d("FireIncident", "Admin to notify: $adminEmail (Chief: $isChiefAdmin, Municipality: $isMunicipalityAdmin)")
                }
            }

            // Update the document with list of notified admins
            if (notifiedAdmins.isNotEmpty()) {
                firestore.collection("fire_incident_reports")
                    .document(documentId)
                    .update("notifiedAdmins", notifiedAdmins)
                    .addOnSuccessListener {
                        android.util.Log.d("FireIncident", "Updated notified admins list: $notifiedAdmins")
                    }
            }
        }
        .addOnFailureListener { e ->
            android.util.Log.e("FireIncident", "Error getting admins", e)
        }
}

/**
 * Check if current admin should see a fire incident report
 */
fun shouldAdminSeeReport(
    adminEmail: String,
    adminRole: AdminRole,
    adminMunicipality: String?,
    reportMunicipality: String
): Boolean {
    // Chief Admin sees all reports
    if (adminRole == AdminRole.CHIEF_ADMINISTRATOR) {
        return true
    }

    // Municipality Admin sees only reports from their municipality
    if (adminRole == AdminRole.MUNICIPALITY_ADMIN && adminMunicipality == reportMunicipality) {
        return true
    }

    return false
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

        // ✅ REMOVED: Do NOT start LogoutService - it exits the app
        // startService(Intent(this, LogoutService::class.java))

        // ✅ Keep this: Start the hydrant change listener service
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

    var logoutTrigger by remember { mutableStateOf(0) }
    var isSigningUp by remember { mutableStateOf(false) } // ✅ NEW: Prevent premature navigation

    // ✅ Only check auth if not in signup process
    val isLoggedIn = remember(logoutTrigger, isSigningUp) {
        !isSigningUp && auth.currentUser != null && auth.currentUser?.email != null
    }

    var showSignUp by remember { mutableStateOf(false) }
    var signUpSuccessMessage by remember { mutableStateOf<String?>(null) }

    val userName = auth.currentUser?.displayName ?: "Full Name"

    when {
        isLoggedIn -> MainApp(
            userName = userName,
            onLogout = {
                FirebaseAuth.getInstance().signOut()
                logoutTrigger++
                showSignUp = false
                signUpSuccessMessage = null
                isSigningUp = false
            }
        )
        showSignUp -> {
            // ✅ Set isSigningUp when showing signup screen
            LaunchedEffect(Unit) {
                isSigningUp = true
            }
            SignUpScreen(
                onSignUpSuccess = {
                    // ✅ Called when Firestore write completes successfully
                    isSigningUp = false
                    logoutTrigger++  // Now trigger navigation
                },
                onBackToLogin = {
                    showSignUp = false
                    isSigningUp = false
                },
                onSignUpComplete = { message ->
                    signUpSuccessMessage = message
                    showSignUp = false
                    isSigningUp = false
                }
            )
        }
        else -> LoginScreen(
            onLoginSuccess = {
                logoutTrigger++
                signUpSuccessMessage = null
            },
            onNavigateToSignUp = {
                showSignUp = true
            },
            successMessage = signUpSuccessMessage
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
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Top Logo Row: app_logo + "FireGrid" text + app_logo
                item {
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

                        // FireGrid text
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

                        // Right logo (app_logo.png) - 80dp
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(80.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

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

                // Password field in SignUpScreen
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

                // Confirm Password field in SignUpScreen
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

    // ✅ UPDATED: Check if user is admin and listen for fire incident reports based on their role
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        isAdminForIncident = true

                        val adminRole = AdminRole.fromString(document.getString("role") ?: "MUNICIPALITY_ADMIN")
                        val adminMunicipality = document.getString("municipality")
                        val isChiefAdmin = adminRole == AdminRole.CHIEF_ADMINISTRATOR

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
                                        val reportMunicipality = doc.getString("municipality") ?: ""

                                        // ✅ Check if this admin should see this report
                                        val shouldNotify = isChiefAdmin ||
                                                (adminRole == AdminRole.MUNICIPALITY_ADMIN && adminMunicipality == reportMunicipality)

                                        if (shouldNotify) {
                                            val incident = FireIncidentAlert(
                                                documentId = doc.id,
                                                reporterName = doc.getString("reporterName") ?: "",
                                                reporterContact = doc.getString("reporterContact") ?: "",
                                                reporterEmail = doc.getString("reporterEmail") ?: "",
                                                reporterCurrentLocation = doc.getString("reporterCurrentLocation") ?: "",
                                                incidentLocation = doc.getString("incidentLocation") ?: "",
                                                description = doc.getString("description") ?: "",
                                                timestamp = doc.getTimestamp("timestamp"),
                                                municipality = reportMunicipality
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
                                                incidentLocation = "${incident.incidentLocation}, ${incident.municipality}",
                                                description = incident.description
                                            )
                                        }
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
                },
                onLogout = onLogout  // ADD THIS LINE
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

    // ✅ NEW: Create and remember the LazyListState with saved scroll position
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = hydrantUiState.scrollIndex,
        initialFirstVisibleItemScrollOffset = hydrantUiState.scrollOffset
    )

    // ✅ NEW: Save scroll position automatically when it changes
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            hydrantViewModel.saveScrollPosition(index, offset)
        }
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filterExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    // ✅ UPDATED: Check if user is admin AND has access to this municipality
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""
    val firestore = Firebase.firestore
    var isAdmin by remember { mutableStateOf(false) }
    var hasAccessToMunicipality by remember { mutableStateOf(false) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    // ✅ UPDATED: Check admin status AND municipality access
    LaunchedEffect(userEmail, municipalityName) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        isAdmin = true
                        val role = AdminRole.fromString(document.getString("role") ?: "MUNICIPALITY_ADMIN")
                        val adminMunicipality = document.getString("municipality")

                        val isChiefAdmin = role == AdminRole.CHIEF_ADMINISTRATOR
                        hasAccessToMunicipality = isChiefAdmin || adminMunicipality == municipalityName
                    } else {
                        isAdmin = false
                        hasAccessToMunicipality = false
                    }
                    isCheckingAdmin = false
                }
                .addOnFailureListener {
                    isAdmin = false
                    hasAccessToMunicipality = false
                    isCheckingAdmin = false
                }
        } else {
            isAdmin = false
            hasAccessToMunicipality = false
            isCheckingAdmin = false
        }
    }

    LaunchedEffect(municipalityName) {
        hydrantViewModel.loadHydrants(municipalityName)
    }

    fun hasInvalidCoordinates(hydrant: FireHydrant): Boolean {
        val lat = hydrant.latitude.toDoubleOrNull()
        val lng = hydrant.longitude.toDoubleOrNull()
        return lat == null || lng == null ||
                lat < -90 || lat > 90 ||
                lng < -180 || lng > 180 ||
                (lat == 0.0 && lng == 0.0)
    }

    val filteredHydrants = hydrantUiState.hydrants.filter { hydrant ->
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

    @Composable
    fun FilterDropdownContent() {
        DropdownMenuItem(
            text = { Text("All") },
            onClick = {
                selectedFilter = "All"
                filterExpanded = false
            },
            colors = MenuDefaults.itemColors(
                textColor = if (selectedFilter == "All") Color(0xFF1976D2) else Color.Black
            ),
            modifier = if (selectedFilter == "All") {
                Modifier.background(Color(0xFFE3F2FD))
            } else {
                Modifier
            }
        )
        DropdownMenuItem(
            text = { Text("In Service") },
            onClick = {
                selectedFilter = "In Service"
                filterExpanded = false
            },
            colors = MenuDefaults.itemColors(
                textColor = if (selectedFilter == "In Service") Color(0xFF1976D2) else Color.Black
            ),
            modifier = if (selectedFilter == "In Service") {
                Modifier.background(Color(0xFFE3F2FD))
            } else {
                Modifier
            }
        )
        DropdownMenuItem(
            text = { Text("Out of Service") },
            onClick = {
                selectedFilter = "Out of Service"
                filterExpanded = false
            },
            colors = MenuDefaults.itemColors(
                textColor = if (selectedFilter == "Out of Service") Color(0xFF1976D2) else Color.Black
            ),
            modifier = if (selectedFilter == "Out of Service") {
                Modifier.background(Color(0xFFE3F2FD))
            } else {
                Modifier
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
                            searchQuery = ""
                            isSearchActive = false
                            selectedFilter = "All"
                            hydrantViewModel.resetScrollPosition()  // ✅ Reset scroll when leaving screen
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
                // ✅ NEW: LazyColumn now uses the listState to preserve scroll position
                LazyColumn(
                    state = listState,  // ✅ CRITICAL: This enables scroll preservation
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = filteredHydrants,
                        key = { it.id }  // ✅ CRITICAL: Stable keys are required for proper scroll restoration
                    ) { hydrant ->
                        FireHydrantCard(
                            hydrant = hydrant,
                            onEditClick = { onEditHydrant(hydrant) },
                            onViewLocationClick = { onViewLocation(hydrant) },
                            showInvalidWarning = hasInvalidCoordinates(hydrant),
                            isAdmin = hasAccessToMunicipality,
                            isCheckingAdmin = isCheckingAdmin
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

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
                        Text(
                            text = "⚠️",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (!isCheckingAdmin && isAdmin) {
                                "Invalid coordinates - Please update"
                            } else {
                                "Invalid coordinates"
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

            // Details
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

                // Row 3: Coordinates
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

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ✅ UPDATED: Only show Edit/Fix buttons for admins with access to this municipality
                if (!isCheckingAdmin && isAdmin) {
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

                // View Location Button - always visible to everyone
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

    // Track deletion and update process separately
    var isDeleting by rememberSaveable { mutableStateOf(false) }
    var isUpdating by rememberSaveable { mutableStateOf(false) }

    // Check if there are unsaved changes
    val hasChanges = exactLocation != hydrant.exactLocation ||
            latitude != hydrant.latitude ||
            longitude != hydrant.longitude ||
            typeColor != hydrant.typeColor ||
            serviceStatus != hydrant.serviceStatus ||
            remarks != hydrant.remarks

    // Handle update success
    LaunchedEffect(hydrantUiState.updateSuccess) {
        if (hydrantUiState.updateSuccess) {
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
            isUpdating = false
            onSuccess()
        }
    }

    // Handle delete success
    LaunchedEffect(hydrantUiState.deleteSuccess) {
        if (hydrantUiState.deleteSuccess) {
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
            isDeleting = false
            onDelete()
        }
    }

    // Handle errors
    LaunchedEffect(hydrantUiState.error) {
        hydrantUiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            hydrantViewModel.clearError()
            isDeleting = false // Reset deleting state on error
            isUpdating = false // Reset updating state on error
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
                        if (exactLocation.isNotBlank()) {
                            hydrantViewModel.updateFireHydrant(
                                hydrant.copy(
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
                        isDeleting = true
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
                            if (!hydrantUiState.isLoading && !isDeleting && !isUpdating) {
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
            // Hydrant Name - READ ONLY
            Column {
                Text(
                    "Hydrant Name",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hydrant.hydrantName,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false,
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
                    enabled = !hydrantUiState.isLoading && !isDeleting && !isUpdating,
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
                        enabled = !hydrantUiState.isLoading && !isDeleting && !isUpdating,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        placeholder = { Text("Longitude") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !hydrantUiState.isLoading && !isDeleting,
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
                    enabled = !hydrantUiState.isLoading && !isDeleting,
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
                    onExpandedChange = {
                        if (!hydrantUiState.isLoading && !isDeleting && !isUpdating) expanded = !expanded
                    }
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
                        enabled = !hydrantUiState.isLoading && !isDeleting,
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
                    enabled = !hydrantUiState.isLoading && !isDeleting,
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
                    enabled = !hydrantUiState.isLoading && !isDeleting && !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350),
                        disabledContainerColor = Color(0xFFBDBDBD)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Delete Hydrant")
                    }
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
                                isUpdating = true
                                hydrantViewModel.updateFireHydrant(
                                    hydrant.copy(
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
                    enabled = !hydrantUiState.isLoading && !isDeleting && !isUpdating && hasChanges,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF81C784),
                        disabledContainerColor = Color(0xFFBDBDBD)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (hydrantUiState.isLoading || isUpdating) {
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

private fun wrapText(text: String, maxWidth: Float, paint: android.graphics.Paint): List<String> {
    if (text.isEmpty()) return listOf("")

    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = ""

    for (word in words) {
        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
        val textWidth = paint.measureText(testLine)

        if (textWidth <= maxWidth) {
            currentLine = testLine
        } else {
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine)
            }
            // If single word is too long, break it
            if (paint.measureText(word) > maxWidth) {
                var remaining = word
                while (remaining.isNotEmpty()) {
                    var end = remaining.length
                    while (end > 0 && paint.measureText(remaining.substring(0, end)) > maxWidth) {
                        end--
                    }
                    if (end == 0) end = 1
                    lines.add(remaining.substring(0, end))
                    remaining = remaining.substring(end)
                }
                currentLine = ""
            } else {
                currentLine = word
            }
        }
    }

    if (currentLine.isNotEmpty()) {
        lines.add(currentLine)
    }

    return if (lines.isEmpty()) listOf(text) else lines
}

// Create circular marker with emoji and pointed bottom with hollow circle tip
private fun createEmojiMarkerIcon(
    context: Context,
    emoji: String,
    sizeDp: Int = 80
): com.google.android.gms.maps.model.BitmapDescriptor {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()
    val pointerHeight = (25 * density).toInt()
    val tipCircleRadius = 8 * density

    val bitmap = android.graphics.Bitmap.createBitmap(sizePx, sizePx + pointerHeight + tipCircleRadius.toInt() + (4 * density).toInt(), android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    val centerX = sizePx / 2f
    val centerY = sizePx / 2f
    val radius = sizePx / 2f - 8 * density
    val tipY = sizePx.toFloat() + pointerHeight

    // Draw shadow
    val shadowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#40000000")
        maskFilter = android.graphics.BlurMaskFilter(10 * density, android.graphics.BlurMaskFilter.Blur.NORMAL)
    }
    canvas.drawCircle(centerX + 3 * density, centerY + 5 * density, radius + 3 * density, shadowPaint)

    // Draw gray border circle (behind white)
    val borderPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#CCCCCC")
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(centerX, centerY, radius + 3 * density, borderPaint)

    // Draw gray pointer border
    val pointerBorderPath = android.graphics.Path().apply {
        moveTo(centerX - 14 * density, centerY + radius - 5 * density)
        lineTo(centerX - 4 * density, tipY)
        lineTo(centerX + 4 * density, tipY)
        lineTo(centerX + 14 * density, centerY + radius - 5 * density)
        close()
    }
    canvas.drawPath(pointerBorderPath, borderPaint)

    // Draw white circle (main background)
    val whitePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(centerX, centerY, radius, whitePaint)

    // Draw white pointer (narrower, connects to ring)
    val pointerPath = android.graphics.Path().apply {
        moveTo(centerX - 10 * density, centerY + radius - 5 * density)
        lineTo(centerX - 3 * density, tipY)
        lineTo(centerX + 3 * density, tipY)
        lineTo(centerX + 10 * density, centerY + radius - 5 * density)
        close()
    }
    canvas.drawPath(pointerPath, whitePaint)

    // Draw hollow circle (ring) at tip - gray outer
    val ringOuterPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#CCCCCC")
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 5 * density
    }
    canvas.drawCircle(centerX, tipY + tipCircleRadius, tipCircleRadius, ringOuterPaint)

    // Draw hollow circle (ring) at tip - white ring
    val ringPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 3 * density
    }
    canvas.drawCircle(centerX, tipY + tipCircleRadius, tipCircleRadius, ringPaint)

    // Draw emoji in center
    val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        textSize = sizePx * 0.45f
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val textBounds = android.graphics.Rect()
    textPaint.getTextBounds(emoji, 0, emoji.length, textBounds)
    val textY = centerY + textBounds.height() / 2f
    canvas.drawText(emoji, centerX, textY, textPaint)

    return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap)
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

    // ✅ UPDATED: Separate states for admin check and municipality access
    var isAdmin by remember { mutableStateOf(false) }
    var hasAccessToMunicipality by remember { mutableStateOf(false) }
    var isChiefAdmin by remember { mutableStateOf(false) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    // Dialog states for success/error
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var showReportOptionsDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    // Function to get municipality logo
    fun getMunicipalityLogo(name: String): Int {
        return when (name) {
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
            else -> R.drawable.alaminos_logo
        }
    }

    // Calculate values needed by the PDF functions
    val temperature = weatherData?.temp?.toInt() ?: 0
    val condition = weatherData?.condition ?: "Loading..."
    val tempMin = weatherData?.tempMin?.toInt() ?: 0
    val tempMax = weatherData?.tempMax?.toInt() ?: 0
    val lowHigh = "$tempMin° / $tempMax°"
    val humidity = "${weatherData?.humidity ?: 0}%"
    val windSpeedKmh = ((weatherData?.windSpeed ?: 0.0) * 3.6).toInt()
    val windSpeed = "$windSpeedKmh km/h"

    val outOfService = remember(hydrantUiState.outOfServiceCount) { hydrantUiState.outOfServiceCount }
    val inService = remember(hydrantUiState.inServiceCount) { hydrantUiState.inServiceCount }
    val totalHydrants = remember(outOfService, inService) { outOfService + inService }

    // ✅ UPDATED: Check if user is admin AND has access to this municipality
    LaunchedEffect(userEmail, municipalityName) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        isAdmin = true
                        val role = AdminRole.fromString(document.getString("role") ?: "MUNICIPALITY_ADMIN")
                        val adminMunicipality = document.getString("municipality")

                        isChiefAdmin = role == AdminRole.CHIEF_ADMINISTRATOR
                        hasAccessToMunicipality = isChiefAdmin || adminMunicipality == municipalityName
                    } else {
                        isAdmin = false
                        hasAccessToMunicipality = false
                        isChiefAdmin = false
                    }
                    isCheckingAdmin = false
                }
                .addOnFailureListener {
                    isAdmin = false
                    hasAccessToMunicipality = false
                    isChiefAdmin = false
                    isCheckingAdmin = false
                }
        } else {
            isAdmin = false
            hasAccessToMunicipality = false
            isChiefAdmin = false
            isCheckingAdmin = false
        }
    }

    // ========== DEFINE PDF FUNCTIONS ==========

    fun generateAndDownloadPdfReport() {
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
                    val pageWidth = 842
                    val pageHeight = 595
                    val pdfDocument = android.graphics.pdf.PdfDocument()
                    val headerHeight = 100
                    val tableHeaderHeight = 40
                    val rowHeight = 45
                    val marginTop = 30
                    val marginBottom = 30
                    val availableHeight = pageHeight - marginTop - headerHeight - tableHeaderHeight - marginBottom
                    val rowsPerPage = availableHeight / rowHeight
                    val totalPages = kotlin.math.ceil(hydrants.size.toDouble() / rowsPerPage).toInt().coerceAtLeast(1)

                    val colWidths = intArrayOf(70, 40, 155, 90, 90, 75, 45, 45, 150)
                    val tableStartX = 20f

                    val cellPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 6f
                    }

                    for (pageNum in 0 until totalPages) {
                        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum + 1).create()
                        val page = pdfDocument.startPage(pageInfo)
                        val canvas = page.canvas

                        val titlePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 14f
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        val subtitlePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            textSize = 11f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        val headerPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 7f
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                        }

                        val linePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            strokeWidth = 0.5f
                            style = android.graphics.Paint.Style.STROKE
                        }

                        val headerBgPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.rgb(230, 230, 230)
                            style = android.graphics.Paint.Style.FILL
                        }

                        var yPos = marginTop.toFloat()
                        canvas.drawText("FIRE HYDRANT INVENTORY REPORT", pageWidth / 2f, yPos, titlePaint)
                        yPos += 20
                        canvas.drawText("$municipalityName, Laguna", pageWidth / 2f, yPos, subtitlePaint)
                        yPos += 18

                        val datePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 9f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        canvas.drawText(
                            "Date Generated: ${java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.ENGLISH).format(java.util.Date())}",
                            pageWidth / 2f, yPos, datePaint
                        )
                        yPos += 14
                        canvas.drawText(
                            "Total: $totalHydrants | Operational: $inService | Non-Operational: $outOfService | Page ${pageNum + 1} of $totalPages",
                            pageWidth / 2f, yPos, datePaint
                        )
                        yPos += 20

                        val tableHeaderY = yPos
                        canvas.drawRect(tableStartX, tableHeaderY, tableStartX + colWidths.sum(), tableHeaderY + tableHeaderHeight, headerBgPaint)

                        val headers = arrayOf(
                            "STATION/\nOWNED BY", "NO. OF\nHYDRANT", "EXACT LOCATION/\nADDRESS",
                            "LATITUDE", "LONGITUDE", "TYPE/\nCOLOR", "OPER.", "NON-\nOPER.", "REMARKS"
                        )

                        var xPos = tableStartX
                        for (i in headers.indices) {
                            canvas.drawRect(xPos, tableHeaderY, xPos + colWidths[i], tableHeaderY + tableHeaderHeight, linePaint)
                            val lines = headers[i].split("\n")
                            val lineHeight = 9f
                            val startY = tableHeaderY + (tableHeaderHeight - lines.size * lineHeight) / 2 + lineHeight
                            for (lineIndex in lines.indices) {
                                canvas.drawText(lines[lineIndex], xPos + 3, startY + lineIndex * lineHeight, headerPaint)
                            }
                            xPos += colWidths[i]
                        }

                        yPos = tableHeaderY + tableHeaderHeight
                        val startIndex = pageNum * rowsPerPage
                        val endIndex = minOf(startIndex + rowsPerPage, hydrants.size)

                        for (i in startIndex until endIndex) {
                            val hydrant = hydrants[i]

                            val latitudeDisplay = hydrant.latitude.ifEmpty { "-" }
                            val longitudeDisplay = hydrant.longitude.ifEmpty { "-" }

                            val operational = if (hydrant.serviceStatus == "In Service") "1" else ""
                            val nonOperational = if (hydrant.serviceStatus == "Out of Service") "1" else ""

                            val rowData = arrayOf(
                                stationName,
                                (i + 1).toString(),
                                hydrant.exactLocation.ifEmpty { "-" },
                                latitudeDisplay,
                                longitudeDisplay,
                                hydrant.typeColor.ifEmpty { "-" },
                                operational,
                                nonOperational,
                                hydrant.remarks.ifEmpty { "" }
                            )

                            xPos = tableStartX
                            for (j in rowData.indices) {
                                canvas.drawRect(xPos, yPos, xPos + colWidths[j], yPos + rowHeight, linePaint)

                                val cellWidth = colWidths[j] - 6
                                val text = rowData[j]
                                val wrappedLines = wrapText(text, cellWidth.toFloat(), cellPaint)

                                val textLineHeight = 8f
                                val totalTextHeight = wrappedLines.size * textLineHeight
                                val textStartY = yPos + (rowHeight - totalTextHeight) / 2 + textLineHeight - 2

                                for (lineIndex in wrappedLines.indices) {
                                    if (textStartY + lineIndex * textLineHeight < yPos + rowHeight - 2) {
                                        canvas.drawText(wrappedLines[lineIndex], xPos + 3, textStartY + lineIndex * textLineHeight, cellPaint)
                                    }
                                }
                                xPos += colWidths[j]
                            }
                            yPos += rowHeight
                        }

                        val footerPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 7f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        canvas.drawText(
                            "Generated by FireGrid App | Bureau of Fire Protection - Laguna Province",
                            pageWidth / 2f, (pageHeight - 15).toFloat(), footerPaint
                        )
                        pdfDocument.finishPage(page)
                    }

                    val fileName = "FireHydrant_Report_${municipalityName}_${System.currentTimeMillis()}.pdf"
                    val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                    val file = java.io.File(downloadsDir, fileName)

                    val outputStream = java.io.FileOutputStream(file)
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()
                    pdfDocument.close()

                    withContext(Dispatchers.Main) {
                        dialogMessage = "✅ PDF Downloaded Successfully!\n\nSaved to: Downloads/$fileName\n\nTotal hydrants: $totalHydrants"
                        showSuccessDialog = true
                        isGeneratingReport = false

                        android.widget.Toast.makeText(
                            context,
                            "Report saved to Downloads folder",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    dialogMessage = "Failed to download PDF: ${e.message}"
                    showErrorDialog = true
                    isGeneratingReport = false
                }
            }
        }
    }

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
                    val pageWidth = 842
                    val pageHeight = 595
                    val pdfDocument = android.graphics.pdf.PdfDocument()
                    val headerHeight = 100
                    val tableHeaderHeight = 40
                    val rowHeight = 45
                    val marginTop = 30
                    val marginBottom = 30
                    val availableHeight = pageHeight - marginTop - headerHeight - tableHeaderHeight - marginBottom
                    val rowsPerPage = availableHeight / rowHeight
                    val totalPages = kotlin.math.ceil(hydrants.size.toDouble() / rowsPerPage).toInt().coerceAtLeast(1)

                    val colWidths = intArrayOf(70, 40, 155, 90, 90, 75, 45, 45, 150)
                    val tableStartX = 20f

                    val cellPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 6f
                    }

                    for (pageNum in 0 until totalPages) {
                        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum + 1).create()
                        val page = pdfDocument.startPage(pageInfo)
                        val canvas = page.canvas

                        val titlePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 14f
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        val subtitlePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            textSize = 11f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        val headerPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 7f
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                        }

                        val linePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            strokeWidth = 0.5f
                            style = android.graphics.Paint.Style.STROKE
                        }

                        val headerBgPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.rgb(230, 230, 230)
                            style = android.graphics.Paint.Style.FILL
                        }

                        var yPos = marginTop.toFloat()
                        canvas.drawText("FIRE HYDRANT INVENTORY REPORT", pageWidth / 2f, yPos, titlePaint)
                        yPos += 20
                        canvas.drawText("$municipalityName, Laguna", pageWidth / 2f, yPos, subtitlePaint)
                        yPos += 18

                        val datePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 9f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        canvas.drawText(
                            "Date Generated: ${java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.ENGLISH).format(java.util.Date())}",
                            pageWidth / 2f, yPos, datePaint
                        )
                        yPos += 14
                        canvas.drawText(
                            "Total: $totalHydrants | Operational: $inService | Non-Operational: $outOfService | Page ${pageNum + 1} of $totalPages",
                            pageWidth / 2f, yPos, datePaint
                        )
                        yPos += 20

                        val tableHeaderY = yPos
                        canvas.drawRect(tableStartX, tableHeaderY, tableStartX + colWidths.sum(), tableHeaderY + tableHeaderHeight, headerBgPaint)

                        val headers = arrayOf(
                            "STATION/\nOWNED BY", "NO. OF\nHYDRANT", "EXACT LOCATION/\nADDRESS",
                            "LATITUDE", "LONGITUDE", "TYPE/\nCOLOR", "OPER.", "NON-\nOPER.", "REMARKS"
                        )

                        var xPos = tableStartX
                        for (i in headers.indices) {
                            canvas.drawRect(xPos, tableHeaderY, xPos + colWidths[i], tableHeaderY + tableHeaderHeight, linePaint)
                            val lines = headers[i].split("\n")
                            val lineHeight = 9f
                            val startY = tableHeaderY + (tableHeaderHeight - lines.size * lineHeight) / 2 + lineHeight
                            for (lineIndex in lines.indices) {
                                canvas.drawText(lines[lineIndex], xPos + 3, startY + lineIndex * lineHeight, headerPaint)
                            }
                            xPos += colWidths[i]
                        }

                        yPos = tableHeaderY + tableHeaderHeight
                        val startIndex = pageNum * rowsPerPage
                        val endIndex = minOf(startIndex + rowsPerPage, hydrants.size)

                        for (i in startIndex until endIndex) {
                            val hydrant = hydrants[i]

                            val latitudeDisplay = hydrant.latitude.ifEmpty { "-" }
                            val longitudeDisplay = hydrant.longitude.ifEmpty { "-" }

                            val operational = if (hydrant.serviceStatus == "In Service") "1" else ""
                            val nonOperational = if (hydrant.serviceStatus == "Out of Service") "1" else ""

                            val rowData = arrayOf(
                                stationName,
                                (i + 1).toString(),
                                hydrant.exactLocation.ifEmpty { "-" },
                                latitudeDisplay,
                                longitudeDisplay,
                                hydrant.typeColor.ifEmpty { "-" },
                                operational,
                                nonOperational,
                                hydrant.remarks.ifEmpty { "" }
                            )

                            xPos = tableStartX
                            for (j in rowData.indices) {
                                canvas.drawRect(xPos, yPos, xPos + colWidths[j], yPos + rowHeight, linePaint)

                                val cellWidth = colWidths[j] - 6
                                val text = rowData[j]
                                val wrappedLines = wrapText(text, cellWidth.toFloat(), cellPaint)

                                val textLineHeight = 8f
                                val totalTextHeight = wrappedLines.size * textLineHeight
                                val textStartY = yPos + (rowHeight - totalTextHeight) / 2 + textLineHeight - 2

                                for (lineIndex in wrappedLines.indices) {
                                    if (textStartY + lineIndex * textLineHeight < yPos + rowHeight - 2) {
                                        canvas.drawText(wrappedLines[lineIndex], xPos + 3, textStartY + lineIndex * textLineHeight, cellPaint)
                                    }
                                }
                                xPos += colWidths[j]
                            }
                            yPos += rowHeight
                        }

                        val footerPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 7f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        canvas.drawText(
                            "Generated by FireGrid App | Bureau of Fire Protection - Laguna Province",
                            pageWidth / 2f, (pageHeight - 15).toFloat(), footerPaint
                        )
                        pdfDocument.finishPage(page)
                    }

                    val fileName = "FireHydrant_Report_${municipalityName}_${System.currentTimeMillis()}.pdf"
                    val file = java.io.File(context.cacheDir, fileName)
                    val outputStream = java.io.FileOutputStream(file)
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()
                    pdfDocument.close()

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

    // ========== DIALOGS ==========

    if (showReportOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showReportOptionsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📄", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Report", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Choose how you want to receive the report:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Surface(
                        onClick = {
                            showReportOptionsDialog = false
                            generateAndDownloadPdfReport()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📥", fontSize = 32.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Download to Device",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2)
                                )
                                Text(
                                    "Save PDF to your downloads folder",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Surface(
                        onClick = {
                            showReportOptionsDialog = false
                            generateAndSendPdfReport()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✉️", fontSize = 32.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Send to Email",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    "Send report to project.fhydrant@gmail.com",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showReportOptionsDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

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

    // ========== LAUNCHED EFFECTS ==========

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

    // ========== UI ==========

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(id = getMunicipalityLogo(municipalityName)),
                            contentDescription = "$municipalityName Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(municipalityName, color = Color.White)
                    }
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
                    // ✅ UPDATED: Only show Generate Report if admin has access to this municipality
                    if (hasAccessToMunicipality && !isCheckingAdmin) {
                        Button(
                            onClick = { showReportOptionsDialog = true },
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
                                    text = "Generating...",
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
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFC107), // Red-Yellow
                            Color(0xFFFF6B35), // Orange
                            Color(0xFFFF4500), // Red-Orange
                            Color(0xFFFFB627), // Yellow-Orange
                            Color(0xFFFF5722)  // Orange-Red
                        )
                    )
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

            // ✅ UPDATED: Only show Add Fire Hydrant button if admin has access to this municipality
            item {
                if (hasAccessToMunicipality && !isCheckingAdmin) {
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

private fun getTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60

    return when {
        minutes < 1 -> "Just now"
        minutes == 1L -> "1 minute ago"
        minutes < 60 -> "$minutes minutes ago"
        else -> "${minutes / 60} hours ago"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    initialIncidentLocation: Pair<Double, Double>? = null,
    onIncidentHandled: () -> Unit = {},
    onMailClick: () -> Unit = {}
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

    // Fire incident report counts for mail badge
    var pendingCount by remember { mutableStateOf(0) }
    var acknowledgedCount by remember { mutableStateOf(0) }
    val totalUnreadCount = pendingCount + acknowledgedCount

    // Search state
    var isSearchOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchSuggestions by remember { mutableStateOf<List<FireHydrant>>(emptyList()) }
    var totalFilteredCount by remember { mutableStateOf(0) }  // NEW: Track total count separately
    var selectedSearchHydrant by remember { mutableStateOf<FireHydrant?>(null) }
    var selectedMunicipality by remember { mutableStateOf<String?>(null) }
    var showMunicipalityDropdown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Directions state
    var directionsResult by remember { mutableStateOf<DirectionsResult?>(null) }
    var userCurrentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoadingDirections by remember { mutableStateOf(false) }

    // Firefighter location tracking states
    var firefighterLocations by remember { mutableStateOf<List<FirefighterLocation>>(emptyList()) }
    var isLoadingFirefighters by remember { mutableStateOf(false) }
    var showFirefighterLocations by remember { mutableStateOf(false) }
    var selectedSearchHydrantDistance by remember { mutableStateOf<Double?>(null) }

    // Fire Incident Location States
    var incidentMarkerLocation by remember { mutableStateOf<LatLng?>(null) }
    var showIncidentMarker by remember { mutableStateOf(false) }
    var showIncidentInfoCard by remember { mutableStateOf(false) }
    var selectedHydrantMarker by remember { mutableStateOf<FireHydrant?>(null) }
    var showHydrantDetailsCard by remember { mutableStateOf(false) }
    var selectedHydrantForCard by remember { mutableStateOf<FireHydrant?>(null) }

    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    BackHandler(enabled = isDrawerOpen || isSearchOpen || selectedHydrantMarker != null || showHydrantDetailsCard) {
        when {
            showHydrantDetailsCard -> {
                showHydrantDetailsCard = false
                selectedHydrantForCard = null
            }
            selectedHydrantMarker != null -> {
                selectedHydrantMarker = null
            }
            isSearchOpen -> {
                isSearchOpen = false
                searchQuery = ""
                searchSuggestions = emptyList()
                totalFilteredCount = 0
                selectedMunicipality = null
                showMunicipalityDropdown = false
                keyboardController?.hide()
            }
            else -> {
                isDrawerOpen = false
            }
        }
    }

    LaunchedEffect(Unit) { hydrantViewModel.loadAllHydrants() }

    // Fetch fire incident report counts from Firebase
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val userEmail = currentUser.email ?: return@LaunchedEffect

            // ✅ FIRST: Check if user is admin and get their role
            db.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { adminDoc ->
                    val isAdmin = adminDoc.exists()
                    val isChiefAdmin = if (isAdmin) {
                        AdminRole.fromString(adminDoc.getString("role") ?: "MUNICIPALITY_ADMIN") == AdminRole.CHIEF_ADMINISTRATOR
                    } else {
                        false
                    }
                    val adminMunicipality = adminDoc.getString("municipality")

                    // ✅ SECOND: Listen for reports based on role
                    val query = if (isAdmin) {
                        if (isChiefAdmin) {
                            // Chief Admin sees ALL reports
                            db.collection("fire_incident_reports")
                        } else {
                            // Municipality Admin sees reports from their municipality
                            db.collection("fire_incident_reports")
                                .whereEqualTo("municipality", adminMunicipality)
                        }
                    } else {
                        // Regular user sees only their own reports
                        db.collection("fire_incident_reports")
                            .whereEqualTo("reporterEmail", userEmail)
                    }

                    // ✅ THIRD: Listen for changes with the correct query
                    query.addSnapshotListener { snapshot, error ->
                        if (error != null || snapshot == null) {
                            pendingCount = 0
                            acknowledgedCount = 0
                            return@addSnapshotListener
                        }

                        var pending = 0
                        var acknowledged = 0

                        for (doc in snapshot.documents) {
                            val status = doc.getString("status")?.trim()?.lowercase() ?: ""
                            when (status) {
                                "pending" -> pending++
                                "acknowledged" -> acknowledged++
                            }
                        }

                        pendingCount = pending
                        acknowledgedCount = acknowledged
                        android.util.Log.d("MapScreen", "Badge counts - Pending: $pending, Acknowledged: $acknowledged")
                    }
                }
                .addOnFailureListener {
                    // Not an admin - show only own reports
                    db.collection("fire_incident_reports")
                        .whereEqualTo("reporterEmail", userEmail)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null || snapshot == null) {
                                pendingCount = 0
                                acknowledgedCount = 0
                                return@addSnapshotListener
                            }

                            var pending = 0
                            var acknowledged = 0

                            for (doc in snapshot.documents) {
                                val status = doc.getString("status")?.trim()?.lowercase() ?: ""
                                when (status) {
                                    "pending" -> pending++
                                    "acknowledged" -> acknowledged++
                                }
                            }

                            pendingCount = pending
                            acknowledgedCount = acknowledged
                        }
                }
        } else {
            pendingCount = 0
            acknowledgedCount = 0
        }
    }

    val hydrantsWithValidCoords = hydrantUiState.allHydrants.filter { hydrant ->
        val lat = hydrant.latitude.toDoubleOrNull()
        val lng = hydrant.longitude.toDoubleOrNull()
        lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0 && !(lat == 0.0 && lng == 0.0)
    }

    // Get unique municipalities for filter
    val availableMunicipalities = remember(hydrantsWithValidCoords) {
        hydrantsWithValidCoords.map { it.municipality }.distinct().sorted()
    }

    // FIXED: Update search suggestions when query or municipality filter changes
    LaunchedEffect(searchQuery, hydrantsWithValidCoords, selectedMunicipality) {
        val filteredByMunicipality = if (selectedMunicipality != null) {
            hydrantsWithValidCoords.filter { it.municipality == selectedMunicipality }
        } else {
            hydrantsWithValidCoords
        }

        if (searchQuery.isNotBlank() && searchQuery.length >= 1) {
            val query = searchQuery.lowercase().trim()
            val allMatches = filteredByMunicipality.filter { hydrant ->
                hydrant.hydrantName.lowercase().contains(query) ||
                        hydrant.exactLocation.lowercase().contains(query) ||
                        hydrant.municipality.lowercase().contains(query)
            }
            totalFilteredCount = allMatches.size  // Store total count BEFORE taking limited results
            searchSuggestions = allMatches.take(50) // Show up to 50 results for better UX
        } else if (selectedMunicipality != null) {
            // Show all hydrants from selected municipality when no search query
            totalFilteredCount = filteredByMunicipality.size  // Store total count
            searchSuggestions = filteredByMunicipality.take(50) // Show up to 50 results
        } else {
            totalFilteredCount = 0
            searchSuggestions = emptyList()
        }
    }

    // Focus on search field when opened
    LaunchedEffect(isSearchOpen) {
        if (isSearchOpen) {
            kotlinx.coroutines.delay(100)
            focusRequester.requestFocus()
        }
    }

    var greenMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var redMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var blueMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var fireIncidentMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var markersReady by remember { mutableStateOf(false) }
    var firetruckMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }

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

    LaunchedEffect(Unit) {
        firetruckMarkerIcon = createEmojiMarkerIcon(context, "🚒", 80)
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

    // Real-time firefighter location tracking (ONLY ADMINS)
    LaunchedEffect(showFirefighterLocations) {
        if (showFirefighterLocations) {
            isLoadingFirefighters = true
            val db = FirebaseFirestore.getInstance()

            // Store listener registrations so we can remove them later
            val locationListeners = mutableListOf<com.google.firebase.firestore.ListenerRegistration>()

            // Listen to admins collection
            val adminsListener = db.collection("admins")
                .addSnapshotListener { adminSnapshot, error ->
                    if (error != null || adminSnapshot == null) {
                        isLoadingFirefighters = false
                        android.util.Log.e("FirefighterLocations", "Error fetching admins: ${error?.message}")
                        return@addSnapshotListener
                    }

                    // Clear old location listeners when admin list changes
                    locationListeners.forEach { it.remove() }
                    locationListeners.clear()

                    if (adminSnapshot.documents.isEmpty()) {
                        firefighterLocations = emptyList()
                        isLoadingFirefighters = false
                        return@addSnapshotListener
                    }

                    // For each admin, set up a REAL-TIME listener on their location
                    for (adminDoc in adminSnapshot.documents) {
                        val adminEmail = adminDoc.id
                        val displayName = adminDoc.getString("displayName") ?: "Firefighter"
                        val adminUid = adminDoc.getString("uid") ?: ""

                        // ✅ Use addSnapshotListener for REAL-TIME updates
                        val locationListener = db.collection("users")
                            .document(adminEmail)
                            .collection("location")
                            .document("current")
                            .addSnapshotListener { locationDoc, locationError ->
                                if (locationError != null) {
                                    android.util.Log.e("FirefighterLocations", "Error listening to $adminEmail: ${locationError.message}")
                                    return@addSnapshotListener
                                }

                                if (locationDoc != null && locationDoc.exists()) {
                                    val lat = locationDoc.getDouble("latitude")
                                    val lng = locationDoc.getDouble("longitude")
                                    val timestamp = locationDoc.getLong("timestamp") ?: 0L
                                    val isTracking = locationDoc.getBoolean("isTracking") ?: false

                                    // Only show if actively tracking and location is recent (within 10 minutes)
                                    val tenMinutesAgo = System.currentTimeMillis() - (10 * 60 * 1000)

                                    if (lat != null && lng != null && isTracking && timestamp > tenMinutesAgo) {
                                        // ✅ Update the firefighter location in real-time
                                        val newLocation = FirefighterLocation(
                                            uid = adminUid,
                                            displayName = displayName,
                                            email = adminEmail,
                                            latitude = lat,
                                            longitude = lng,
                                            lastUpdated = timestamp
                                        )

                                        // Update the list - remove old entry for this admin and add new one
                                        firefighterLocations = firefighterLocations
                                            .filter { it.email != adminEmail }
                                            .plus(newLocation)

                                        android.util.Log.d("FirefighterLocations", "📍 REAL-TIME UPDATE: $adminEmail at $lat, $lng")
                                    } else {
                                        // Admin stopped tracking or location is stale - remove from list
                                        firefighterLocations = firefighterLocations.filter { it.email != adminEmail }
                                        android.util.Log.d("FirefighterLocations", "❌ Removed $adminEmail (tracking: $isTracking, stale: ${timestamp <= tenMinutesAgo})")
                                    }
                                } else {
                                    // No location document - remove from list
                                    firefighterLocations = firefighterLocations.filter { it.email != adminEmail }
                                }

                                isLoadingFirefighters = false
                            }

                        locationListeners.add(locationListener)
                    }
                }

            // Clean up listeners when showFirefighterLocations becomes false
            // This is handled by the else branch below
        } else {
            // Clear when hidden
            firefighterLocations = emptyList()
            android.util.Log.d("FirefighterLocations", "Cleared firefighter locations")
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

    fun selectHydrantFromSearch(hydrant: FireHydrant) {
        selectedSearchHydrant = hydrant
        nearestHydrant = hydrant
        isSearchOpen = false
        searchQuery = ""
        searchSuggestions = emptyList()
        totalFilteredCount = 0
        selectedMunicipality = null
        showMunicipalityDropdown = false
        keyboardController?.hide()

        val lat = hydrant.latitude.toDoubleOrNull()
        val lng = hydrant.longitude.toDoubleOrNull()
        if (lat != null && lng != null) {
            // Calculate distance from user's current location
            if (checkLocationPermission(context)) {
                getCurrentLocation(context) { location ->
                    val distance = calculateDistance(location.latitude, location.longitude, lat, lng)
                    selectedSearchHydrantDistance = distance
                }
            }

            scope.launch {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 17f), durationMs = 800)
            }
        }
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

        // ✅ UPDATED: Auto-detect municipality from current location
        var detectedMunicipality by remember { mutableStateOf<String?>(null) }
        var currentLatLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }

        LaunchedEffect(Unit) { reporterName = FirebaseAuth.getInstance().currentUser?.displayName ?: "" }

        // ✅ NEW: Auto-detect municipality when location is obtained
        LaunchedEffect(currentLocation) {
            if (currentLocation.isNotEmpty()) {
                try {
                    val regex = """Lat:\s*([-\d.]+),\s*Lng:\s*([-\d.]+)""".toRegex()
                    val match = regex.find(currentLocation)
                    if (match != null) {
                        val lat = match.groupValues[1].toDouble()
                        val lng = match.groupValues[2].toDouble()
                        currentLatLng = Pair(lat, lng)
                        detectedMunicipality = getMunicipalityFromCoordinates(lat, lng)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

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

                    // ✅ UPDATED: Show detected municipality when location is obtained
                    if (currentLocation.isNotEmpty()) {
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📍", fontSize = 20.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("Your Location:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                        Text(currentLocation, style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))

                                        // ✅ Show detected municipality
                                        if (detectedMunicipality != null) {
                                            Spacer(Modifier.height(4.dp))
                                            Surface(
                                                color = Color(0xFF4CAF50),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "📍 Detected: $detectedMunicipality",
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                    IconButton(onClick = { currentLocation = ""; detectedMunicipality = null; currentLatLng = null }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Close, "Clear", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    } else if (locationError) {
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️", fontSize = 16.sp); Spacer(Modifier.width(8.dp))
                                Text("Please get your current location before submitting", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // Fire Incident Location
                    Text("Fire Incident Location *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    Text("Enter the exact address or location where the fire is happening", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    OutlinedTextField(
                        value = incidentLocation,
                        onValueChange = { incidentLocation = it },
                        label = { Text("Fire Location") },
                        placeholder = { Text("e.g., 123 Main St, Brgy. Real") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), focusedLabelColor = Color(0xFFD32F2F))
                    )

                    OutlinedTextField(
                        value = incidentDescription,
                        onValueChange = { incidentDescription = it },
                        label = { Text("Description (Optional)") },
                        placeholder = { Text("Describe what you see") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), focusedLabelColor = Color(0xFFD32F2F))
                    )

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
                        detectedMunicipality == null -> android.widget.Toast.makeText(context, "Unable to detect municipality. Please try again.", android.widget.Toast.LENGTH_SHORT).show()
                        incidentLocation.isBlank() -> android.widget.Toast.makeText(context, "Please enter the fire incident location", android.widget.Toast.LENGTH_SHORT).show()
                        else -> {
                            isSubmitting = true
                            submitFireIncidentReport(
                                context = context,
                                reporterName = reporterName,
                                reporterContact = "+63$reporterContact",
                                reporterEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown",
                                reporterCurrentLocation = currentLocation,
                                incidentLocation = incidentLocation,
                                description = incidentDescription,
                                municipality = detectedMunicipality!!, // ✅ Use auto-detected municipality
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
                    val markerState = remember(hydrant.id) { com.google.maps.android.compose.MarkerState(position = LatLng(lat, lng)) }

// Close info window when selected marker is cleared
                    LaunchedEffect(selectedHydrantMarker) {
                        if (selectedHydrantMarker?.id != hydrant.id) {
                            markerState.hideInfoWindow()
                        }
                    }

                    com.google.maps.android.compose.Marker(
                        state = markerState,
                        title = "${hydrant.municipality} - ${hydrant.hydrantName}",
                        icon = markerIcon,
                        onClick = { marker ->
                            // Check if nearest hydrant card or search hydrant card is already showing
                            val isNearestOrSearchHydrantShowing = nearestHydrant != null || selectedSearchHydrant != null
                            val isClickingSameHydrant = (nearestHydrant?.id == hydrant.id && nearestHydrant?.municipality == hydrant.municipality) ||
                                    (selectedSearchHydrant?.id == hydrant.id && selectedSearchHydrant?.municipality == hydrant.municipality)

                            if (isNearestOrSearchHydrantShowing && isClickingSameHydrant) {
                                // If clicking the SAME hydrant that's showing in the card, ONLY show info window (no card)
                                selectedHydrantMarker = hydrant
                                selectedHydrantForCard = null
                                showHydrantDetailsCard = false
                            } else if (isNearestOrSearchHydrantShowing) {
                                // If clicking a DIFFERENT hydrant while a card is showing:
                                // 1. Close the nearest/search hydrant card
                                nearestHydrant = null
                                selectedSearchHydrant = null
                                directionsResult = null
                                selectedSearchHydrantDistance = null

                                // 2. Show the new hydrant's details card
                                selectedHydrantMarker = hydrant
                                selectedHydrantForCard = hydrant
                                showHydrantDetailsCard = true
                            } else {
                                // Normal behavior - no cards showing, so show both info window and card
                                selectedHydrantMarker = hydrant
                                selectedHydrantForCard = hydrant
                                showHydrantDetailsCard = true
                            }

                            marker.showInfoWindow()

                            // Center camera slightly below marker to show info window better
                            scope.launch {
                                val projection = cameraPositionState.projection
                                val markerScreenPos = projection?.toScreenLocation(LatLng(lat, lng))
                                if (markerScreenPos != null) {
                                    val offsetScreenPos = android.graphics.Point(
                                        markerScreenPos.x,
                                        markerScreenPos.y + 30
                                    )
                                    val offsetLatLng = projection.fromScreenLocation(offsetScreenPos)
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLng(offsetLatLng),
                                        durationMs = 500
                                    )
                                }
                            }
                            true
                        }
                    )
                }

                // Fire Incident Marker
                if (showIncidentMarker && incidentMarkerLocation != null && fireIncidentMarkerIcon != null) {
                    com.google.maps.android.compose.Marker(state = com.google.maps.android.compose.MarkerState(position = incidentMarkerLocation!!), title = "🔥 FIRE INCIDENT", snippet = "Reporter's Location", icon = fireIncidentMarkerIcon)
                }
            }

            // Directions polyline
            directionsResult?.let { Polyline(points = it.polylinePoints, color = Color(0xFF2196F3), width = 12f) }

            // Firefighter Location Markers (INSIDE GoogleMap)
            if (showFirefighterLocations && firefighterLocations.isNotEmpty() && firetruckMarkerIcon != null) {
                firefighterLocations.forEach { firefighter ->
                    com.google.maps.android.compose.Marker(
                        state = com.google.maps.android.compose.MarkerState(
                            position = LatLng(firefighter.latitude, firefighter.longitude)
                        ),
                        title = firefighter.displayName,
                        snippet = "Last updated: ${getTimeAgo(firefighter.lastUpdated)}",
                        icon = firetruckMarkerIcon,
                        anchor = androidx.compose.ui.geometry.Offset(0.5f, 1f)
                    )
                }
            }
        }

        // Search Overlay
        if (isSearchOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Search Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back button
                        IconButton(
                            onClick = {
                                isSearchOpen = false
                                searchQuery = ""
                                searchSuggestions = emptyList()
                                totalFilteredCount = 0
                                selectedMunicipality = null
                                showMunicipalityDropdown = false
                                keyboardController?.hide()
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF5F6368)
                            )
                        }

                        // Search TextField
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            placeholder = { Text("Search hydrants...", color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF6B35),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                cursorColor = Color(0xFFFF6B35)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    keyboardController?.hide()
                                    if (searchSuggestions.isNotEmpty()) {
                                        selectHydrantFromSearch(searchSuggestions.first())
                                    }
                                }
                            )
                        )
                    }

                    // Municipality Filter
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Surface(
                            onClick = { showMunicipalityDropdown = !showMunicipalityDropdown },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (selectedMunicipality != null) Color(0xFFFF6B35).copy(alpha = 0.1f) else Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (selectedMunicipality != null) Color(0xFFFF6B35) else Color(0xFFE0E0E0)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(
                                            id = if (selectedMunicipality != null)
                                                getMunicipalityLogo(selectedMunicipality!!)
                                            else
                                                R.drawable.laguna_logo
                                        ),
                                        contentDescription = if (selectedMunicipality != null)
                                            "$selectedMunicipality logo"
                                        else
                                            "Laguna logo",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = selectedMunicipality ?: "All Cities and Municipalities",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selectedMunicipality != null) Color(0xFFFF6B35) else Color(0xFF666666),
                                        fontWeight = if (selectedMunicipality != null) FontWeight.Medium else FontWeight.Normal
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selectedMunicipality != null) {
                                        // Clear filter button
                                        IconButton(
                                            onClick = {
                                                selectedMunicipality = null
                                                showMunicipalityDropdown = false
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Clear filter",
                                                tint = Color(0xFFFF6B35),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Icon(
                                        if (showMunicipalityDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = if (selectedMunicipality != null) Color(0xFFFF6B35) else Color.Gray
                                    )
                                }
                            }
                        }

                        // Dropdown Menu
                        DropdownMenu(
                            expanded = showMunicipalityDropdown,
                            onDismissRequest = { showMunicipalityDropdown = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .heightIn(max = 300.dp)
                        ) {
                            // All Cities and Municipalities option
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = R.drawable.laguna_logo),
                                            contentDescription = "Laguna logo",
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            "All Cities and Municipalities",
                                            fontWeight = if (selectedMunicipality == null) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    selectedMunicipality = null
                                    showMunicipalityDropdown = false
                                }
                            )

                            HorizontalDivider(color = Color(0xFFE0E0E0))

                            // Municipality options
                            availableMunicipalities.forEach { municipality ->
                                val count = hydrantsWithValidCoords.count { it.municipality == municipality }
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Municipality Logo
                                            Image(
                                                painter = painterResource(id = getMunicipalityLogo(municipality)),
                                                contentDescription = "$municipality logo",
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                municipality,
                                                fontWeight = if (selectedMunicipality == municipality) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selectedMunicipality == municipality) Color(0xFFFF6B35) else Color.Black
                                            )
                                            Spacer(Modifier.weight(1f))
                                            Surface(
                                                color = if (selectedMunicipality == municipality) Color(0xFFFF6B35) else Color(0xFFE0E0E0),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    "$count",
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (selectedMunicipality == municipality) Color.White else Color(0xFF666666),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedMunicipality = municipality
                                        showMunicipalityDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // Search Suggestions
                    if (searchQuery.isNotEmpty() || selectedMunicipality != null) {
                        if (searchSuggestions.isEmpty()) {
                            // No results
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        if (selectedMunicipality != null && searchQuery.isEmpty())
                                            "No hydrants in $selectedMunicipality"
                                        else
                                            "No hydrants found for \"$searchQuery\"${if (selectedMunicipality != null) " in $selectedMunicipality" else ""}",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            // Results header - FIXED: Now shows totalFilteredCount instead of searchSuggestions.size
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Results",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.Gray
                                )
                                Text(
                                    "$totalFilteredCount found${if (selectedMunicipality != null) " in $selectedMunicipality" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFF6B35)
                                )
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(searchSuggestions) { hydrant ->
                                    Surface(
                                        onClick = { selectHydrantFromSearch(hydrant) },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color.Transparent
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Search icon
                                            Icon(
                                                Icons.Default.Search,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.width(16.dp))

                                            // Hydrant info
                                            Column(modifier = Modifier.weight(1f)) {
                                                // Highlight matching text
                                                Text(
                                                    text = hydrant.hydrantName,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${hydrant.exactLocation}, ${hydrant.municipality}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            // Status indicator
                                            Surface(
                                                color = if (hydrant.serviceStatus == "In Service")
                                                    Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = if (hydrant.serviceStatus == "In Service")
                                                        Color(0xFF4CAF50) else Color(0xFFEF5350),
                                                    modifier = Modifier
                                                        .padding(6.dp)
                                                        .size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                    HorizontalDivider(
                                        modifier = Modifier.padding(start = 56.dp),
                                        color = Color(0xFFF0F0F0)
                                    )
                                }

                                // Show "showing X of Y" if there are more results than displayed
                                if (totalFilteredCount > searchSuggestions.size) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Showing ${searchSuggestions.size} of $totalFilteredCount results",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Show recent or popular searches placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color(0xFFE0E0E0),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "Search for hydrants",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "By name, location, or select a municipality",
                                    color = Color(0xFFBDBDBD),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // Fire Incident Info Card (Top) - Fixed position to avoid overlap with top buttons
        if (showIncidentInfoCard && incidentMarkerLocation != null && !isSearchOpen) {
            // Check if current user is admin - MOVED TO TOP
            var isAdmin by remember { mutableStateOf(false) }
            var isCheckingAdmin by remember { mutableStateOf(true) }

// Check admin status when card is shown - CHECK ADMINS COLLECTION
            LaunchedEffect(Unit) {
                isCheckingAdmin = true
                val auth = FirebaseAuth.getInstance()
                val currentUserEmail = auth.currentUser?.email

                android.util.Log.d("FireIncident", "Checking admin status for: $currentUserEmail")

                if (currentUserEmail != null) {
                    val db = FirebaseFirestore.getInstance()

                    // Check the ADMINS collection - document ID is the lowercase email
                    db.collection("admins")
                        .document(currentUserEmail.lowercase())
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                // User is in admins collection
                                val adminStatus = document.getBoolean("isAdmin")
                                android.util.Log.d("FireIncident", "Admin document found! isAdmin: $adminStatus")
                                isAdmin = adminStatus == true
                            } else {
                                android.util.Log.d("FireIncident", "Not an admin: $currentUserEmail")
                                isAdmin = false
                            }
                            isCheckingAdmin = false
                            android.util.Log.d("FireIncident", "Final state - isAdmin: $isAdmin")
                        }
                        .addOnFailureListener { error ->
                            android.util.Log.e("FireIncident", "Error fetching admin: ${error.message}")
                            isAdmin = false
                            isCheckingAdmin = false
                        }
                } else {
                    android.util.Log.d("FireIncident", "No current user")
                    isAdmin = false
                    isCheckingAdmin = false
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 120.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Header Row
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥", fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "FIRE INCIDENT LOCATION",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                        IconButton(
                            onClick = {
                                showIncidentInfoCard = false
                                showIncidentMarker = false
                                incidentMarkerLocation = null
                                nearestHydrant = null
                                directionsResult = null
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, "Close", tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Coordinates
                    Text(
                        "Lat: ${String.format("%.6f", incidentMarkerLocation!!.latitude)}, Lng: ${String.format("%.6f", incidentMarkerLocation!!.longitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )

                    // Nearest Hydrant Info
                    if (nearestHydrant != null) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Nearest Hydrant: ${nearestHydrant!!.hydrantName}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Text(
                                            nearestHydrant!!.exactLocation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF4CAF50)
                                        )
                                        Text(
                                            if (directionsResult != null) "${directionsResult!!.distance} • ${directionsResult!!.duration}"
                                            else nearestHydrantDistance?.let { if (it < 1) "%.0f meters".format(it * 1000) else "%.2f km".format(it) } ?: "Calculating...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF1976D2),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Direction Buttons Row - Different for admin vs regular users
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isCheckingAdmin) {
                            // Show loading while checking admin status
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFFFF6B35),
                                    strokeWidth = 2.dp
                                )
                            }
                        } else if (isAdmin) {
                            // ADMIN BUTTONS: To Hydrant and To Fire

                            // Direction to Hydrant Button (Green)
                            if (nearestHydrant != null) {
                                Button(
                                    onClick = {
                                        // First update firefighter location for tracking
                                        updateFirefighterLocation(
                                            context = context,
                                            onLocationRequired = {
                                                locationPermissionLauncher.launch(
                                                    arrayOf(
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                    )
                                                )
                                            },
                                            onSuccess = {
                                                Toast.makeText(context, "Location shared", Toast.LENGTH_SHORT).show()
                                            },
                                            onFailure = { }
                                        )

                                        // Then proceed with directions (existing code)
                                        val hydrantLat = nearestHydrant!!.latitude.toDoubleOrNull()
                                        val hydrantLng = nearestHydrant!!.longitude.toDoubleOrNull()
                                        if (hydrantLat != null && hydrantLng != null) {
                                            if (checkLocationPermission(context)) {
                                                getCurrentLocation(context) { location ->
                                                    userCurrentLocation = LatLng(location.latitude, location.longitude)
                                                    isLoadingDirections = true
                                                    scope.launch {
                                                        directionsResult = fetchDirections(
                                                            origin = LatLng(location.latitude, location.longitude),
                                                            destination = LatLng(hydrantLat, hydrantLng)
                                                        )
                                                        isLoadingDirections = false
                                                        if (directionsResult != null) {
                                                            cameraPositionState.animate(
                                                                CameraUpdateFactory.newLatLngZoom(
                                                                    LatLng(hydrantLat, hydrantLng),
                                                                    15f
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                android.widget.Toast.makeText(context, "Location permission required", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    enabled = !isLoadingDirections
                                ) {
                                    if (isLoadingDirections) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.Directions,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "To Hydrant",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Direction to Fire Location Button (Red)
                            Button(
                                onClick = {
                                    if (checkLocationPermission(context)) {
                                        getCurrentLocation(context) { location ->
                                            userCurrentLocation = LatLng(location.latitude, location.longitude)
                                            isLoadingDirections = true
                                            scope.launch {
                                                directionsResult = fetchDirections(
                                                    origin = LatLng(location.latitude, location.longitude),
                                                    destination = incidentMarkerLocation!!
                                                )
                                                isLoadingDirections = false
                                                if (directionsResult != null) {
                                                    cameraPositionState.animate(
                                                        CameraUpdateFactory.newLatLngZoom(
                                                            incidentMarkerLocation!!,
                                                            15f
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        android.widget.Toast.makeText(context, "Location permission required", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                enabled = !isLoadingDirections
                            ) {
                                if (isLoadingDirections) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Directions,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                }
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "To Fire",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        } else {
                            // REGULAR USER BUTTON: View Firefighter Locations
                            Button(
                                onClick = {
                                    showFirefighterLocations = !showFirefighterLocations

                                    if (showFirefighterLocations) {
                                        android.widget.Toast.makeText(
                                            context,
                                            "Showing firefighter locations",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (showFirefighterLocations) Color(0xFFFF6B35) else Color(0xFF2196F3)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(
                                    if (showFirefighterLocations) Icons.Default.Close else Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (showFirefighterLocations) "Hide Firefighters (${firefighterLocations.size})"
                                    else "Show Firefighter Locations",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                if (isLoadingFirefighters) {
                                    Spacer(Modifier.width(8.dp))
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Hydrant Details Card (Bottom) - Google Maps style
        if (showHydrantDetailsCard && selectedHydrantForCard != null && !isSearchOpen) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header Row with Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedHydrantForCard!!.hydrantName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )

                            Spacer(Modifier.height(4.dp))

                            Surface(
                                color = if (selectedHydrantForCard!!.serviceStatus == "In Service")
                                    Color(0xFF4CAF50) else Color(0xFFEF5350),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = selectedHydrantForCard!!.serviceStatus,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        // Close Button
                        IconButton(
                            onClick = {
                                showHydrantDetailsCard = false

                                // Hide the info window by clearing the selected marker
                                selectedHydrantMarker = null

                                // Store reference before clearing
                                val prevHydrant = selectedHydrantForCard
                                selectedHydrantForCard = null

                                // Briefly pan away and back to force info window closure
                                scope.launch {
                                    val current = cameraPositionState.position.target
                                    // Pan 0.0001 degrees away
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLng(
                                            LatLng(current.latitude + 0.0001, current.longitude)
                                        ),
                                        durationMs = 1
                                    )
                                    kotlinx.coroutines.delay(50)
                                    // Pan back
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLng(current),
                                        durationMs = 1
                                    )
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                "Close",
                                tint = Color(0xFF757575),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedHydrantForCard!!.exactLocation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = selectedHydrantForCard!!.municipality,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF757575)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (checkLocationPermission(context)) {
                                getCurrentLocation(context) { location ->
                                    userCurrentLocation = LatLng(location.latitude, location.longitude)
                                    val hydrantLat = selectedHydrantForCard!!.latitude.toDoubleOrNull()
                                    val hydrantLng = selectedHydrantForCard!!.longitude.toDoubleOrNull()

                                    if (hydrantLat != null && hydrantLng != null) {
                                        isLoadingDirections = true
                                        scope.launch {
                                            directionsResult = fetchDirections(
                                                origin = LatLng(location.latitude, location.longitude),
                                                destination = LatLng(hydrantLat, hydrantLng)
                                            )
                                            isLoadingDirections = false
                                            nearestHydrant = selectedHydrantForCard
                                            showHydrantDetailsCard = false

                                            if (directionsResult == null) {
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "Could not fetch directions",
                                                    android.widget.Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A73E8)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isLoadingDirections
                    ) {
                        if (isLoadingDirections) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Loading...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Icon(
                                Icons.Default.Directions,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Directions",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Menu Button (hide when search is open)
        if (!isSearchOpen) {
            FloatingActionButton(onClick = { isDrawerOpen = true }, modifier = Modifier.align(Alignment.TopStart).padding(start = 12.dp, top = 12.dp).statusBarsPadding().size(40.dp), containerColor = Color.White, contentColor = Color(0xFF5F6368), elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp), shape = RoundedCornerShape(8.dp)) { Text("≡", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF5F6368), fontWeight = FontWeight.Bold) }
        }

        // Emergency, Mail and Search Button Row (hide when search is open)
        if (!isSearchOpen) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 12.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Emergency Button (Left) - with text
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

                // Mail Button (Middle) - icon only with dual badges
                Box {
                    FloatingActionButton(
                        onClick = onMailClick,
                        modifier = Modifier.size(40.dp),
                        containerColor = Color(0xFF2196F3),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Mail",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Dual Badge Container (top-right corner)
                    if (pendingCount > 0 || acknowledgedCount > 0) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-8).dp),
                            horizontalArrangement = Arrangement.spacedBy((-8).dp)
                        ) {
                            // Pending Badge (Red) - LEFT
                            if (pendingCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(Color(0xFFE53935), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (pendingCount > 9) "9+" else pendingCount.toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Acknowledged Badge (Orange) - RIGHT
                            if (acknowledgedCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(Color(0xFFFFA726), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (acknowledgedCount > 9) "9+" else acknowledgedCount.toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Search Button (Right) - icon only
                FloatingActionButton(
                    onClick = { isSearchOpen = true },
                    modifier = Modifier.size(40.dp),
                    containerColor = Color(0xFFFF6B35),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Zoom Controls (hide when search is open OR when hydrant details card is shown OR when nearest hydrant card is shown)
        if (!isSearchOpen && !showHydrantDetailsCard && nearestHydrant == null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 12.dp,
                        bottom = 16.dp
                    ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // My Location Button
                FloatingActionButton(
                    onClick = {
                        if (checkLocationPermission(context)) {
                            isMyLocationEnabled = true
                            getCurrentLocation(context) { location ->
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(location.latitude, location.longitude),
                                            15f
                                        )
                                    )
                                }
                            }
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    containerColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        "My Location",
                        Modifier.size(20.dp),
                        tint = Color(0xFF5F6368)
                    )
                }

                // Zoom In Button
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.zoomTo(cameraPositionState.position.zoom + 1f)
                            )
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    containerColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "+",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF5F6368),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Zoom Out Button
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.zoomTo(cameraPositionState.position.zoom - 1f)
                            )
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    containerColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "−",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF5F6368),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Nearest Hydrant Card (Bottom) - only when NOT showing incident card and search is closed
        if (nearestHydrant != null && !showIncidentInfoCard && !isSearchOpen) {
            Card(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(nearestHydrant!!.hydrantName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF212121), modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Surface(color = if (nearestHydrant!!.serviceStatus == "In Service") Color(0xFF4CAF50) else Color(0xFFEF5350), shape = RoundedCornerShape(20.dp)) {
                            Text(nearestHydrant!!.serviceStatus, Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color.White, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = { nearestHydrant = null; directionsResult = null; selectedSearchHydrant = null; selectedSearchHydrantDistance = null }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Close, "Close", tint = Color.Gray, modifier = Modifier.size(20.dp)) }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(nearestHydrant!!.exactLocation, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF757575))
                    Text("Municipality: ${nearestHydrant!!.municipality}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF757575))
                    Spacer(Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(20.dp)) {
                            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp))
                                Text(
                                    if (directionsResult != null)
                                        "${directionsResult!!.distance} • ${directionsResult!!.duration}"
                                    else {
                                        val distanceToShow = if (selectedSearchHydrant != null) selectedSearchHydrantDistance else nearestHydrantDistance
                                        distanceToShow?.let {
                                            if (it < 1) "%.0f meters away".format(it * 1000)
                                            else "%.2f km away".format(it)
                                        } ?: "Calculating..."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1976D2),
                                    fontWeight = FontWeight.SemiBold
                                )
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

        // Scrim (hide when search is open since search has its own overlay)
        if (scrimAlpha > 0f && !isSearchOpen) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = scrimAlpha)).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { isDrawerOpen = false })

        // Drawer (hide when search is open)
        if (!isSearchOpen) {
            Surface(modifier = Modifier.fillMaxHeight().width(280.dp).offset(x = drawerOffsetX).align(Alignment.CenterStart), color = Color.White, shadowElevation = if (isDrawerOpen) 16.dp else 0.dp) {
                Column(Modifier.fillMaxSize()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B35), // Orange
                                        Color(0xFFFF4500), // Red-Orange
                                        Color(0xFFFFB627), // Yellow-Orange
                                    )
                                )
                            )
                            .statusBarsPadding()
                            .padding(20.dp)
                    ) {
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
                    Surface(onClick = { isDrawerOpen = false; directionsResult = null; selectedSearchHydrant = null; if (checkLocationPermission(context)) { isSearchingNearestHydrant = true; isMyLocationEnabled = true; getCurrentLocation(context) { location -> userCurrentLocation = LatLng(location.latitude, location.longitude); scope.launch { val (nearest, distance) = findNearestHydrant(location.latitude, location.longitude, hydrantUiState.allHydrants); if (nearest != null) { nearestHydrant = nearest; nearestHydrantDistance = distance; nearest.latitude.toDoubleOrNull()?.let { lat -> nearest.longitude.toDoubleOrNull()?.let { lng -> cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 16f)) } } } else android.widget.Toast.makeText(context, "No hydrants found", android.widget.Toast.LENGTH_SHORT).show(); isSearchingNearestHydrant = false } } } else locationPermissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)) }, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Search, null, tint = Color(0xFF2196F3), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text("Find Nearest Hydrant", style = MaterialTheme.typography.bodyLarge) }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = Color(0xFFE0E0E0))

                    // Reset
                    Surface(onClick = { isDrawerOpen = false; nearestHydrant = null; showIncidentMarker = false; showIncidentInfoCard = false; incidentMarkerLocation = null; directionsResult = null; selectedSearchHydrant = null; scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(lagunaLake, 10f)) } }, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Refresh, null, tint = Color(0xFFFF6B35), modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text("Reset to Laguna View", style = MaterialTheme.typography.bodyLarge) }
                    }

                    // My Location
                    Surface(onClick = { isDrawerOpen = false; nearestHydrant = null; selectedSearchHydrant = null; if (checkLocationPermission(context)) { isMyLocationEnabled = true; getCurrentLocation(context) { location -> scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f)) } } } else locationPermissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)) }, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
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
}

@Composable
fun getMunicipalityLogo(municipality: String): Int {
    return when (municipality) {
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
        else -> R.drawable.ic_launcher_foreground // Fallback logo
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
    var isChiefAdmin by remember { mutableStateOf(false) }
    var adminMunicipality by remember { mutableStateOf<String?>(null) }
    var showReportDetailDialog by remember { mutableStateOf(false) }
    var selectedReport by remember { mutableStateOf<FireIncidentReport?>(null) }

    // Check if user is admin and get their role
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        isAdmin = true
                        val role = AdminRole.fromString(document.getString("role") ?: "MUNICIPALITY_ADMIN")
                        isChiefAdmin = role == AdminRole.CHIEF_ADMINISTRATOR
                        adminMunicipality = document.getString("municipality")
                    }
                }
        }
    }

    // Load fire incident reports
    LaunchedEffect(userEmail, isAdmin, isChiefAdmin, adminMunicipality) {
        firestore.collection("fire_incident_reports")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }

                val allReports = snapshot?.documents?.mapNotNull { doc ->
                    FireIncidentReport(
                        documentId = doc.id,
                        reporterName = doc.getString("reporterName") ?: "",
                        reporterContact = doc.getString("reporterContact") ?: "",
                        reporterEmail = doc.getString("reporterEmail") ?: "",
                        reporterCurrentLocation = doc.getString("reporterCurrentLocation") ?: "",
                        incidentLocation = doc.getString("incidentLocation") ?: "",
                        description = doc.getString("description") ?: "",
                        timestamp = doc.getTimestamp("timestamp"),
                        status = doc.getString("status") ?: "pending",
                        municipality = doc.getString("municipality") ?: ""
                    )
                } ?: emptyList()

                // ✅ Filter reports based on admin role
                reports = if (isAdmin) {
                    if (isChiefAdmin) {
                        // Chief Admin sees ALL reports
                        allReports
                    } else {
                        // Municipality Admin sees only reports from their municipality
                        // AND their own reports (as a reporter)
                        allReports.filter { report ->
                            report.municipality == adminMunicipality ||
                                    report.reporterEmail == userEmail
                        }
                    }
                } else {
                    // Regular users see only their own reports
                    allReports.filter { it.reporterEmail == userEmail }
                }

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
            isAdmin = isAdmin && (isChiefAdmin || adminMunicipality == selectedReport!!.municipality), // ✅ Check municipality access for actions
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
                    Column {
                        Text(
                            "Fire Incident Reports",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        if (isAdmin && !isChiefAdmin && adminMunicipality != null) {
                            Text(
                                "Showing reports for $adminMunicipality",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else if (isChiefAdmin) {
                            Text(
                                "Showing all reports (Chief Admin)",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
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

            // ✅ NEW: Show municipality badge
            if (report.municipality.isNotEmpty()) {
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏛️", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = report.municipality,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1565C0)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

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
    val context = LocalContext.current
    var isUpdatingLocation by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            Toast.makeText(context, "Location permission granted. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

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
        onDismissRequest = { if (!isUpdatingLocation) onDismiss() },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fire Incident Report", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    Spacer(Modifier.height(4.dp))
                    Text(formattedTime, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                IconButton(
                    onClick = { if (!isUpdatingLocation) onDismiss() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reporter Location
                if (report.reporterCurrentLocation.isNotEmpty()) {
                    Surface(Modifier.fillMaxWidth(), color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("📍 Reporter's Location", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Spacer(Modifier.height(4.dp))
                            Text(report.reporterCurrentLocation)
                        }
                    }
                }

                // Fire Location
                Surface(Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("🔥 Fire Incident Location", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        Spacer(Modifier.height(4.dp))
                        Text(report.incidentLocation, fontWeight = FontWeight.Medium)
                        if (report.municipality.isNotEmpty()) {
                            Text(report.municipality, style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                        }
                    }
                }

                // Description
                if (report.description.isNotEmpty()) {
                    Surface(Modifier.fillMaxWidth(), color = Color(0xFFFFF8E1), shape = RoundedCornerShape(8.dp)) {
                        Column(Modifier.padding(12.dp)) {
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
                        // Acknowledge Button - STARTS CONTINUOUS TRACKING
                        if (report.status == "pending") {
                            Button(
                                onClick = {
                                    isUpdatingLocation = true
                                    startFirefighterLocationTracking(
                                        context = context,
                                        onLocationRequired = {
                                            isUpdatingLocation = false
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        },
                                        onSuccess = {
                                            isUpdatingLocation = false
                                            Toast.makeText(context, "📍 Location tracking started", Toast.LENGTH_SHORT).show()
                                            onStatusChange("acknowledged")
                                        },
                                        onFailure = {
                                            isUpdatingLocation = false
                                            onStatusChange("acknowledged")
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                                enabled = !isUpdatingLocation
                            ) {
                                if (isUpdatingLocation) {
                                    CircularProgressIndicator(Modifier.size(16.dp), Color.White, strokeWidth = 2.dp)
                                } else {
                                    Text("Acknowledge", fontSize = 12.sp)
                                }
                            }
                        }

                        // Mark Resolved - NO TRACKING (just changes status)
                        Button(
                            onClick = {
                                // Stop tracking when resolved
                                stopFirefighterLocationTracking(context)
                                onStatusChange("resolved")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            enabled = !isUpdatingLocation
                        ) {
                            Text("Mark Resolved", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // View on Map - STARTS CONTINUOUS TRACKING
                if (coordinates != null) {
                    Button(
                        onClick = {
                            isUpdatingLocation = true
                            startFirefighterLocationTracking(
                                context = context,
                                onLocationRequired = {
                                    isUpdatingLocation = false
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                onSuccess = {
                                    isUpdatingLocation = false
                                    Toast.makeText(context, "📍 Location tracking started", Toast.LENGTH_SHORT).show()
                                    onViewOnMap(coordinates.first, coordinates.second)
                                },
                                onFailure = {
                                    isUpdatingLocation = false
                                    onViewOnMap(coordinates.first, coordinates.second)
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        enabled = !isUpdatingLocation
                    ) {
                        if (isUpdatingLocation) {
                            CircularProgressIndicator(Modifier.size(18.dp), Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Icon(Icons.Default.LocationOn, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("View on Map")
                    }
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

    // Fire gradient brush
    val fireGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFFC107), // Red-Yellow
            Color(0xFFFF6B35), // Orange
            Color(0xFFFF5722), // Orange-Red
            Color(0xFFFFB627), // Yellow-Orange
            Color(0xFFFF4500)  // Red-Orange
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header with fire gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(fireGradient)
                .systemBarsPadding()
                .padding(horizontal = 14.dp)
                .padding(bottom = 10.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Laguna Logo
                        Image(
                            painter = painterResource(id = R.drawable.laguna_logo),
                            contentDescription = "Laguna Logo",
                            modifier = Modifier.size(68.dp)
                        )

                        Column {
                            Text(
                                text = "Laguna",
                                style = MaterialTheme.typography.headlineMedium,
                                fontSize = 28.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Cities & Municipalities",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (hydrantUiState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = Color(0xFFFF6B35)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Loading...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Black
                                        )
                                    } else {
                                        Text(
                                            text = "Total Hydrant : $totalHydrants",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search button
                    IconButton(
                        onClick = { isSearchActive = true },
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White, shape = RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFFF6B35),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // Municipality list
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
    var adminType by remember { mutableStateOf<String?>(null) }
    var municipalityName by remember { mutableStateOf<String?>(null) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    // Show logout confirmation dialog
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userEmail) {
        firestore.collection("admins")
            .document(userEmail.lowercase())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    val municipality = document.getString("municipality")

                    adminType = role
                    municipalityName = municipality
                }
                isCheckingAdmin = false
            }
            .addOnFailureListener { e ->
                adminType = null
                isCheckingAdmin = false
            }
    }

    val initials = userName.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                )
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.signOut()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // =====================================================
        // GRADIENT HEADER WITH PROFILE INFO - FIRE THEMED!
        // =====================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF5722), // Orange-Red (start)
                            Color(0xFFFF6B35), // Original Orange (middle)
                            Color(0xFFFFC107)  // Yellow (end)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 22.sp,
                        color = Color(0xFFFF6B35),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    if (isCheckingAdmin) {
                        Spacer(modifier = Modifier.height(6.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else if (adminType != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier.wrapContentWidth(),
                            color = Color.White,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Display emoji based on admin type
                                Text(
                                    text = when (adminType) {
                                        "CHIEF_ADMINISTRATOR" -> "⭐"
                                        "MUNICIPALITY_ADMIN" -> "🏛️"
                                        else -> "✓"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (adminType) {
                                        "CHIEF_ADMINISTRATOR" -> "Chief Admin"
                                        "MUNICIPALITY_ADMIN" -> municipalityName ?: "Municipality Admin"
                                        else -> "Admin"
                                    },
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

        Spacer(modifier = Modifier.height(12.dp))

        // All menu items
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color.White,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 1.dp
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    iconBackground = Color(0xFFE3F2FD),
                    iconTint = Color(0xFF1976D2),
                    title = "Settings",
                    onClick = onSettingsClick
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )
                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    iconBackground = Color(0xFFFFF3E0),
                    iconTint = Color(0xFFF57C00),
                    title = "Help / FAQ",
                    onClick = onHelpFaqClick
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )
                ProfileMenuItem(
                    icon = Icons.Default.Email,
                    iconBackground = Color(0xFFE8F5E9),
                    iconTint = Color(0xFF388E3C),
                    title = "Contact Support",
                    onClick = onContactSupportClick
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )
                ProfileMenuItem(
                    icon = Icons.Default.Warning,
                    iconBackground = Color(0xFFFFF9C4),
                    iconTint = Color(0xFFF9A825),
                    title = "Report a Problem",
                    onClick = onReportProblemClick
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )
                ProfileMenuItem(
                    icon = Icons.Default.Star,
                    iconBackground = Color(0xFFF3E5F5),
                    iconTint = Color(0xFF7B1FA2),
                    title = "About the App",
                    onClick = onAboutAppClick
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )
                ProfileMenuItem(
                    icon = Icons.Default.AccountBox,
                    iconBackground = Color(0xFFE0F2F1),
                    iconTint = Color(0xFF00897B),
                    title = "Terms & Privacy Policy",
                    onClick = onTermsPrivacyClick
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )
                // Show confirmation dialog instead of direct logout
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    iconBackground = Color(0xFFFFEBEE),
                    iconTint = Color(0xFFD32F2F),
                    title = "Logout",
                    titleColor = Color(0xFFD32F2F),
                    onClick = { showLogoutDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    titleColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
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
                // Icon with circular background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = titleColor,
                    fontWeight = if (titleColor == Color(0xFFD32F2F)) FontWeight.Medium else FontWeight.Normal
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color(0xFF9E9E9E)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "About the App",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFB627), // Yellow-Orange
                            Color(0xFFFF4500), // Red-Orange
                            Color(0xFFFF6B35), // Orange
                            Color(0xFFFFC107), // Red-Yellow
                            Color(0xFFFFC107), // Red-Yellow
                        )
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsPrivacyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Terms & Privacy Policy",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFC107), // Red-Yellow
                            Color(0xFFFF6B35), // Orange
                            Color(0xFFFF4500), // Red-Orange
                            Color(0xFFFFB627), // Yellow-Orange
                            Color(0xFFFF5722)  // Orange-Red
                        )
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpFaqScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Help / FAQ",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF5722), // Orange-Red
                            Color(0xFFFF6B35), // Orange
                            Color(0xFFFFC107), // Red-Yellow
                            Color(0xFFFFB627), // Yellow-Orange
                            Color(0xFFFF4500)  // Red-Orange
                        )
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Contact Support",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF5722), // Orange-Red
                            Color(0xFFFFC107), // Red-Yellow
                            Color(0xFFFF6B35), // Orange
                            Color(0xFFFF4500), // Red-Orange
                            Color(0xFFFFB627), // Yellow-Orange
                        )
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportProblemScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Get current user from Firebase
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "Unknown User"
    val userEmail = currentUser?.email ?: "no-email@firehydrant.app"

    var problemType by rememberSaveable { mutableStateOf("Bug") }
    var problemDescription by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isSubmitting by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Report a Problem",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF4500),
                            Color(0xFFFFB627),
                            Color(0xFFFF5722),
                            Color(0xFFFFC107),
                            Color(0xFFFF6B35)
                        )
                    )
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
                        coroutineScope.launch {
                            val result = EmailSender.sendProblemReport(
                                problemType = problemType,
                                description = problemDescription,
                                userName = userName,
                                userEmail = userEmail
                            )

                            isSubmitting = false

                            if (result.isSuccess) {
                                Toast.makeText(
                                    context,
                                    "Report submitted successfully. Thank you!",
                                    Toast.LENGTH_LONG
                                ).show()
                                onBack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to submit report. Please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please describe the problem",
                            Toast.LENGTH_SHORT
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
    onRemoveAdminClick: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""
    val firestore = Firebase.firestore

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var deleteConfirmationText by remember { mutableStateOf("") }
    var isDeleting by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var emergencyAlertsEnabled by remember { mutableStateOf(true) }
    var hydrantUpdatesEnabled by remember { mutableStateOf(true) }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }

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

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            notificationsEnabled = true
            showTestNotification(
                context = context,
                title = "🔔 Notifications Enabled!",
                message = "You will now receive FireGrid alerts and updates."
            )
        }
    }

    LaunchedEffect(Unit) {
        createNotificationChannels(context)
        val prefs = context.getSharedPreferences("firegrid_prefs", Context.MODE_PRIVATE)
        notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        emergencyAlertsEnabled = prefs.getBoolean("emergency_enabled", true)
        hydrantUpdatesEnabled = prefs.getBoolean("hydrant_updates_enabled", true)
    }

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

    // DELETE ACCOUNT DIALOG
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteAccountDialog = false
                    deleteConfirmationText = ""
                }
            },
            title = {
                Text(
                    text = "⚠️ Delete Account",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Are you sure you want to delete your account?",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "This action cannot be undone. All your data will be permanently deleted.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Please type CONFIRM and press delete account button to complete the action:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )

                    OutlinedTextField(
                        value = deleteConfirmationText,
                        onValueChange = { deleteConfirmationText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Type CONFIRM") },
                        singleLine = true,
                        enabled = !isDeleting,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD32F2F),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleting = true
                        val user = auth.currentUser

                        if (user != null && userEmail.isNotEmpty()) {
                            firestore.collection("users")
                                .document(userEmail)
                                .delete()
                                .addOnSuccessListener {
                                    firestore.collection("admins")
                                        .document(userEmail.lowercase())
                                        .delete()
                                        .addOnSuccessListener {
                                            user.delete().addOnCompleteListener { task ->
                                                isDeleting = false
                                                if (task.isSuccessful) {
                                                    auth.signOut()
                                                    android.util.Log.d("SettingsScreen", "Account deleted, calling onLogout")
                                                    android.widget.Toast.makeText(
                                                        context,
                                                        "Account deleted successfully",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                    showDeleteAccountDialog = false
                                                    deleteConfirmationText = ""
                                                    onLogout()
                                                } else {
                                                    android.widget.Toast.makeText(
                                                        context,
                                                        "Failed to delete account: ${task.exception?.message}",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                        .addOnFailureListener {
                                            user.delete().addOnCompleteListener { task ->
                                                isDeleting = false
                                                if (task.isSuccessful) {
                                                    auth.signOut()
                                                    android.util.Log.d("SettingsScreen", "Account deleted, calling onLogout")
                                                    android.widget.Toast.makeText(
                                                        context,
                                                        "Account deleted successfully",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                    showDeleteAccountDialog = false
                                                    deleteConfirmationText = ""
                                                    onLogout()
                                                } else {
                                                    android.widget.Toast.makeText(
                                                        context,
                                                        "Failed to delete account: ${task.exception?.message}",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                }
                                .addOnFailureListener { e ->
                                    isDeleting = false
                                    android.widget.Toast.makeText(
                                        context,
                                        "Failed to delete user data: ${e.message}",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                        } else {
                            isDeleting = false
                            android.widget.Toast.makeText(
                                context,
                                "No user logged in",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = deleteConfirmationText == "CONFIRM" && !isDeleting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        disabledContainerColor = Color(0xFFE0E0E0)
                    )
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Delete Account", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        deleteConfirmationText = ""
                    },
                    enabled = !isDeleting
                ) {
                    Text("Cancel", color = Color(0xFF666666))
                }
            },
            containerColor = Color.White
        )
    }

    // NOTIFICATION SETTINGS DIALOG
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
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (hasNotificationPermission && notificationsEnabled) {
                            showTestNotification(
                                context = context,
                                title = "🧪 Test Notification",
                                message = "This is a test notification from FireGrid!"
                            )
                            android.widget.Toast.makeText(context, "Test notification sent!", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            android.widget.Toast.makeText(context, "Enable notifications first", android.widget.Toast.LENGTH_SHORT).show()
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text("Settings", color = Color.White, fontWeight = FontWeight.Bold)
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFB627),
                            Color(0xFFFF6B35),
                            Color(0xFFFF5722),
                            Color(0xFFFFC107),
                            Color(0xFFFFC107)
                        )
                    )
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
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666)
            )

            // Change Password Card
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
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFE0B2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔒", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Change Password", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("Update your account password", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Change Password", tint = Color(0xFF757575))
                    }
                }
            }

            // Delete Account Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Surface(
                    onClick = { showDeleteAccountDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFCDD2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🗑️", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Delete Account", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Color(0xFFD32F2F))
                                Text("Permanently delete your account", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Delete Account", tint = Color(0xFF757575))
                    }
                }
            }

            // Admin Settings Section
            if (isCheckingAdmin) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp), color = Color(0xFFFF6B35))
                }
            } else if (isAdmin) {
                Text(
                    text = "Admin Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )

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
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFC8E6C9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👑", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("Add Admin", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                    Text("Promote a user to admin", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Add Admin", tint = Color(0xFF757575))
                        }
                    }
                }

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
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFCDD2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("❌", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("Remove Admin", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                    Text("Revoke admin privileges", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Remove Admin", tint = Color(0xFF757575))
                        }
                    }
                }
            }

            Text(
                text = "App Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Notifications Card
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
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFBBDEFB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔔", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Notifications", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text(
                                    text = if (notificationsEnabled && hasNotificationPermission) "Enabled" else "Tap to configure",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (notificationsEnabled && hasNotificationPermission) Color(0xFF4CAF50) else Color.Gray
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (notificationsEnabled && hasNotificationPermission) {
                                Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp)) {
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
                            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Notification Settings", tint = Color(0xFF757575))
                        }
                    }
                }
            }

            Text(
                text = "FireGrid Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

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

/**
 * Check if admin has access to a specific municipality
 * Returns: Pair<hasAccess, isChiefAdmin>
 */
fun checkMunicipalityAccessSync(
    document: com.google.firebase.firestore.DocumentSnapshot?,
    municipalityName: String
): Pair<Boolean, Boolean> {
    if (document == null || !document.exists()) {
        return Pair(false, false)
    }

    val role = AdminRole.fromString(document.getString("role") ?: "MUNICIPALITY_ADMIN")
    val adminMunicipality = document.getString("municipality")

    val isChiefAdmin = role == AdminRole.CHIEF_ADMINISTRATOR
    val hasAccess = isChiefAdmin || adminMunicipality == municipalityName

    return Pair(hasAccess, isChiefAdmin)
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
    var selectedRole by remember { mutableStateOf<AdminRole?>(null) }
    var selectedMunicipality by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var roleDropdownExpanded by remember { mutableStateOf(false) }
    var municipalityDropdownExpanded by remember { mutableStateOf(false) }

    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""

    // Check if current user is Chief Administrator
    var currentUserRole by remember { mutableStateOf<AdminRole?>(null) }
    var isCheckingPermission by remember { mutableStateOf(true) }

    // Function to get municipality logo
    fun getMunicipalityLogo(municipality: String): Int {
        return when (municipality) {
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
            else -> R.drawable.ic_launcher_foreground // fallback
        }
    }

    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        currentUserRole = AdminRole.fromString(doc.getString("role") ?: "MUNICIPALITY_ADMIN")
                    }
                    isCheckingPermission = false
                }
                .addOnFailureListener {
                    isCheckingPermission = false
                }
        } else {
            isCheckingPermission = false
        }
    }

    fun isValidEmail(emailStr: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr.trim()).matches()
    }

    // Access Denied Screen for non-Chief Administrators
    if (!isCheckingPermission && currentUserRole != AdminRole.CHIEF_ADMINISTRATOR) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑 ", fontSize = 24.sp)
                            Text("Add Admin", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF6B35))
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🚫", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Access Denied", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Only Chief Administrators can add new admins.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)), shape = RoundedCornerShape(8.dp)) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👑 ", fontSize = 24.sp)
                        Text("Add Admin", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { if (!isLoading) onBack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF6B35))
            )
        }
    ) { padding ->
        if (isCheckingPermission) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF6B35))
            }
        } else {
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
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Promote User to Admin", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFFF6B35))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Enter user details and assign admin role", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }

                // Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Email Field
                        Column {
                            Text("Email Address", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it; errorMessage = "" },
                                placeholder = { Text("Enter user's email") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                enabled = !isLoading,
                                shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35), focusedLabelColor = Color(0xFFFF6B35))
                            )
                        }

                        // UID Field
                        Column {
                            Text("User UID", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uid,
                                onValueChange = { uid = it; errorMessage = "" },
                                placeholder = { Text("Enter user's UID") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                enabled = !isLoading,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35), focusedLabelColor = Color(0xFFFF6B35))
                            )
                        }

                        // Role Selection Dropdown
                        Column {
                            Text("Admin Role", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            ExposedDropdownMenuBox(
                                expanded = roleDropdownExpanded,
                                onExpandedChange = { if (!isLoading) roleDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = if (selectedRole != null) {
                                        "${if (selectedRole == AdminRole.CHIEF_ADMINISTRATOR) "⭐" else "🏛️"} ${selectedRole!!.displayName}"
                                    } else {
                                        "Select Admin Role"
                                    },
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35), focusedLabelColor = Color(0xFFFF6B35))
                                )
                                ExposedDropdownMenu(expanded = roleDropdownExpanded, onDismissRequest = { roleDropdownExpanded = false }) {
                                    AdminRole.values().forEach { role ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(role.displayName, fontWeight = FontWeight.Medium)
                                                    Text(
                                                        when (role) {
                                                            AdminRole.CHIEF_ADMINISTRATOR -> "Full access to all municipalities, can add/remove admins"
                                                            AdminRole.MUNICIPALITY_ADMIN -> "Access to assigned municipality only"
                                                        },
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedRole = role
                                                if (role == AdminRole.CHIEF_ADMINISTRATOR) selectedMunicipality = null
                                                roleDropdownExpanded = false
                                            },
                                            leadingIcon = {
                                                Text(if (role == AdminRole.CHIEF_ADMINISTRATOR) "⭐" else "🏛️", fontSize = 20.sp)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Municipality Selection (only for Municipality Admin)
                        if (selectedRole == AdminRole.MUNICIPALITY_ADMIN) {
                            Column {
                                Text("Municipality", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(8.dp))
                                ExposedDropdownMenuBox(
                                    expanded = municipalityDropdownExpanded,
                                    onExpandedChange = { if (!isLoading) municipalityDropdownExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = selectedMunicipality ?: "Select Municipality",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = municipalityDropdownExpanded) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35), focusedLabelColor = Color(0xFFFF6B35))
                                    )
                                    ExposedDropdownMenu(expanded = municipalityDropdownExpanded, onDismissRequest = { municipalityDropdownExpanded = false }) {
                                        lagunaMunicipalities.forEach { municipality ->
                                            DropdownMenuItem(
                                                text = { Text(municipality) },
                                                onClick = {
                                                    selectedMunicipality = municipality
                                                    municipalityDropdownExpanded = false
                                                },
                                                leadingIcon = {
                                                    Image(
                                                        painter = painterResource(id = getMunicipalityLogo(municipality)),
                                                        contentDescription = "$municipality logo",
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Role Info Card
                        if (selectedRole != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.Top) {
                                    Text("ℹ️", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            when (selectedRole) {
                                                AdminRole.CHIEF_ADMINISTRATOR -> "Chief Administrator Privileges:"
                                                AdminRole.MUNICIPALITY_ADMIN -> "Municipality Admin Privileges:"
                                                else -> ""
                                            },
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFE65100)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            when (selectedRole) {
                                                AdminRole.CHIEF_ADMINISTRATOR -> "• Full access to all municipalities\n• Can add and remove admins\n• Can manage all system settings"
                                                AdminRole.MUNICIPALITY_ADMIN -> "• Access only to assigned municipality\n• Cannot add or remove admins\n• Can manage municipality-specific data"
                                                else -> ""
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFE65100)
                                        )
                                    }
                                }
                            }
                        }

                        // Error Message
                        if (errorMessage.isNotEmpty()) {
                            Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚠️", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(errorMessage, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Promote Button
                Button(
                    onClick = {
                        when {
                            email.isBlank() -> errorMessage = "Please enter an email address"
                            !isValidEmail(email) -> errorMessage = "Invalid email format"
                            uid.isBlank() -> errorMessage = "Please enter the user's UID"
                            uid.trim().length < 20 -> errorMessage = "UID is invalid"
                            selectedRole == null -> errorMessage = "Please select an admin role"
                            selectedRole == AdminRole.MUNICIPALITY_ADMIN && selectedMunicipality == null -> errorMessage = "Please select a municipality"
                            else -> {
                                isLoading = true
                                errorMessage = ""
                                val emailToSave = email.trim().lowercase()
                                val uidToSave = uid.trim()

                                firestore.collection("users").document(uidToSave).get()
                                    .addOnSuccessListener { userDoc ->
                                        if (userDoc.exists()) {
                                            val storedEmail = userDoc.getString("email")?.lowercase() ?: ""
                                            if (storedEmail != emailToSave) {
                                                isLoading = false
                                                errorMessage = "Email does not match this UID"
                                            } else {
                                                firestore.collection("admins").document(emailToSave).get()
                                                    .addOnSuccessListener { adminDoc ->
                                                        if (adminDoc.exists()) {
                                                            isLoading = false
                                                            errorMessage = "This user is already an admin"
                                                        } else {
                                                            val displayName = userDoc.getString("displayName") ?: ""
                                                            val adminData = AdminUser(
                                                                email = emailToSave,
                                                                uid = uidToSave,
                                                                displayName = displayName,
                                                                role = selectedRole!!,
                                                                municipality = if (selectedRole == AdminRole.MUNICIPALITY_ADMIN) selectedMunicipality else null,
                                                                isAdmin = true,
                                                                promotedAt = com.google.firebase.Timestamp.now(),
                                                                promotedBy = auth.currentUser?.email ?: "Unknown"
                                                            )
                                                            firestore.collection("admins").document(emailToSave).set(adminData.toMap())
                                                                .addOnSuccessListener { isLoading = false; onSuccess(emailToSave) }
                                                                .addOnFailureListener { e -> isLoading = false; errorMessage = "Failed to promote: ${e.message}" }
                                                        }
                                                    }
                                                    .addOnFailureListener { e -> isLoading = false; errorMessage = "Error: ${e.message}" }
                                            }
                                        } else {
                                            firestore.collection("users").whereEqualTo("email", emailToSave).get()
                                                .addOnSuccessListener { querySnapshot ->
                                                    if (querySnapshot.isEmpty) {
                                                        isLoading = false
                                                        errorMessage = "User does not exist in the system"
                                                    } else {
                                                        val userDocFromQuery = querySnapshot.documents.first()
                                                        val userUidFromDoc = userDocFromQuery.getString("uid") ?: userDocFromQuery.id
                                                        if (userUidFromDoc != uidToSave) {
                                                            isLoading = false
                                                            errorMessage = "UID does not match"
                                                        } else {
                                                            firestore.collection("admins").document(emailToSave).get()
                                                                .addOnSuccessListener { adminDoc ->
                                                                    if (adminDoc.exists()) {
                                                                        isLoading = false
                                                                        errorMessage = "This user is already an admin"
                                                                    } else {
                                                                        val displayName = userDocFromQuery.getString("displayName") ?: ""
                                                                        val adminData = AdminUser(
                                                                            email = emailToSave,
                                                                            uid = uidToSave,
                                                                            displayName = displayName,
                                                                            role = selectedRole!!,
                                                                            municipality = if (selectedRole == AdminRole.MUNICIPALITY_ADMIN) selectedMunicipality else null,
                                                                            isAdmin = true,
                                                                            promotedAt = com.google.firebase.Timestamp.now(),
                                                                            promotedBy = auth.currentUser?.email ?: "Unknown"
                                                                        )
                                                                        firestore.collection("admins").document(emailToSave).set(adminData.toMap())
                                                                            .addOnSuccessListener { isLoading = false; onSuccess(emailToSave) }
                                                                            .addOnFailureListener { e -> isLoading = false; errorMessage = "Failed: ${e.message}" }
                                                                    }
                                                                }
                                                                .addOnFailureListener { e -> isLoading = false; errorMessage = "Error: ${e.message}" }
                                                        }
                                                    }
                                                }
                                                .addOnFailureListener { e -> isLoading = false; errorMessage = "Error: ${e.message}" }
                                        }
                                    }
                                    .addOnFailureListener { e -> isLoading = false; errorMessage = "Error: ${e.message}" }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !isLoading && email.isNotBlank() && uid.isNotBlank() && selectedRole != null && (selectedRole == AdminRole.CHIEF_ADMINISTRATOR || selectedMunicipality != null),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Promote as ${selectedRole?.displayName ?: "Admin"}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
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
    var selectedAdmin by remember { mutableStateOf<AdminUser?>(null) }
    var uid by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingAdmins by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var adminsList by remember { mutableStateOf<List<AdminUser>>(emptyList()) }
    var showUidDialog by remember { mutableStateOf(false) }

    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""

    // Check if current user is Chief Administrator
    var currentUserRole by remember { mutableStateOf<AdminRole?>(null) }
    var isCheckingPermission by remember { mutableStateOf(true) }

    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        currentUserRole = AdminRole.fromString(doc.getString("role") ?: "MUNICIPALITY_ADMIN")
                    }
                    isCheckingPermission = false
                }
                .addOnFailureListener {
                    isCheckingPermission = false
                }
        } else {
            isCheckingPermission = false
        }
    }

    fun loadAdmins() {
        isLoadingAdmins = true
        firestore.collection("admins").orderBy("email").get()
            .addOnSuccessListener { documents ->
                adminsList = documents.documents.mapNotNull { doc -> AdminUser.fromDocument(doc) }
                isLoadingAdmins = false
            }
            .addOnFailureListener { e ->
                isLoadingAdmins = false
                errorMessage = "Failed to load admins: ${e.message}"
            }
    }

    LaunchedEffect(Unit) { loadAdmins() }

    // Access Denied Screen
    if (!isCheckingPermission && currentUserRole != AdminRole.CHIEF_ADMINISTRATOR) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("❌ ", fontSize = 24.sp)
                            Text("Remove Admin", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFEF5350))
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🚫", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Access Denied", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Only Chief Administrators can remove admins.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)), shape = RoundedCornerShape(8.dp)) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
        return
    }

    // UID Confirmation Dialog
    if (showUidDialog && selectedAdmin != null) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) { showUidDialog = false; uid = ""; errorMessage = "" } },
            title = { Text("Remove Admin", fontWeight = FontWeight.Bold, color = Color(0xFFEF5350)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("You are about to remove:", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(selectedAdmin!!.email, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            Text("Name: ${selectedAdmin!!.displayName}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                            Text("Role: ${selectedAdmin!!.role.displayName}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                            if (selectedAdmin!!.municipality != null) {
                                Text("Municipality: ${selectedAdmin!!.municipality}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                            }
                        }
                    }

                    if (selectedAdmin!!.role == AdminRole.CHIEF_ADMINISTRATOR) {
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Warning: Removing a Chief Administrator!", color = Color(0xFFE65100), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uid,
                        onValueChange = { uid = it; errorMessage = "" },
                        label = { Text("Enter UID to Confirm") },
                        placeholder = { Text("Enter user's UID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFEF5350), focusedLabelColor = Color(0xFFEF5350))
                    )

                    if (errorMessage.isNotEmpty()) {
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(errorMessage, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val enteredUid = uid.trim()
                        val chiefAdminCount = adminsList.count { it.role == AdminRole.CHIEF_ADMINISTRATOR }

                        when {
                            enteredUid.isEmpty() -> errorMessage = "Please enter the UID"
                            enteredUid != selectedAdmin!!.uid -> errorMessage = "UID does not match"
                            adminsList.size <= 1 -> errorMessage = "Cannot remove the last admin"
                            selectedAdmin!!.role == AdminRole.CHIEF_ADMINISTRATOR && chiefAdminCount <= 1 -> errorMessage = "Cannot remove the last Chief Administrator"
                            else -> {
                                isLoading = true
                                errorMessage = ""
                                val adminEmail = selectedAdmin!!.email
                                firestore.collection("admins").document(adminEmail).delete()
                                    .addOnSuccessListener {
                                        isLoading = false
                                        showUidDialog = false
                                        uid = ""
                                        selectedAdmin = null
                                        loadAdmins()
                                        onSuccess(adminEmail)
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        errorMessage = "Failed to remove: ${e.message}"
                                    }
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("Remove Admin")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUidDialog = false; uid = ""; errorMessage = ""; selectedAdmin = null }, enabled = !isLoading) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("❌ ", fontSize = 24.sp)
                        Text("Remove Admin", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { if (!isLoading) onBack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFEF5350))
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
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Remove Admin Privileges", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFEF5350))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (isLoadingAdmins) "Loading admins..." else "Select an admin to remove (${adminsList.size} admin${if (adminsList.size != 1) "s" else ""})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Admin List
            if (isLoadingAdmins || isCheckingPermission) {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFEF5350))
                }
            } else if (adminsList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No admins found", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                adminsList.forEach { admin ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Surface(
                            onClick = { selectedAdmin = admin; showUidDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Transparent
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(if (admin.role == AdminRole.CHIEF_ADMINISTRATOR) Color(0xFFFFF3E0) else Color(0xFFFFCDD2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (admin.role == AdminRole.CHIEF_ADMINISTRATOR) {
                                        Text("⭐", fontSize = 24.sp)
                                    } else {
                                        val initials = admin.displayName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase().ifEmpty { "A" }
                                        Text(initials, style = MaterialTheme.typography.titleMedium, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(admin.displayName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                        if (admin.role == AdminRole.CHIEF_ADMINISTRATOR) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(4.dp)) {
                                                Text("Chief", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Text(admin.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    if (admin.municipality != null) {
                                        Text("🏛️ ${admin.municipality}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
                                    }
                                }
                                Icon(Icons.Default.KeyboardArrowRight, "Select", tint = Color(0xFFEF5350))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
    val context = LocalContext.current
    var isAcknowledged by remember { mutableStateOf(false) }
    var isUpdatingLocation by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            Toast.makeText(context, "Location permission granted. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", java.util.Locale.getDefault())
    val formattedTime = incident.timestamp?.toDate()?.let { dateFormat.format(it) } ?: "Unknown time"

    AlertDialog(
        onDismissRequest = { if (!isUpdatingLocation) onDismiss() },
        title = {
            Box(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 28.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("FIRE INCIDENT ALERT", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 18.sp)
                        Text(formattedTime, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                IconButton(
                    onClick = { if (!isUpdatingLocation) onDismiss() },
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp)
                ) {
                    Text("✕", fontSize = 24.sp, color = Color.Gray)
                }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Acknowledged badge
                if (isAcknowledged) {
                    Surface(Modifier.fillMaxWidth(), color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("✅", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("This report has been acknowledged", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }

                // Municipality
                if (incident.municipality.isNotEmpty()) {
                    Surface(Modifier.fillMaxWidth(), color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🏛️", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Municipality", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1565C0))
                                Text(incident.municipality, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                            }
                        }
                    }
                }

                // Reporter Info
                Surface(Modifier.fillMaxWidth(), color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("👤 Reporter Information", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                        Spacer(Modifier.height(4.dp))
                        Text("Name: ${incident.reporterName}")
                        Text("Contact: ${incident.reporterContact}")
                        Text("Email: ${incident.reporterEmail}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                // Reporter Location
                if (incident.reporterCurrentLocation.isNotEmpty() && incident.reporterCurrentLocation != "Not provided") {
                    Surface(Modifier.fillMaxWidth(), color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("📍 Reporter's Location", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Spacer(Modifier.height(4.dp))
                            Text(incident.reporterCurrentLocation)
                        }
                    }
                }

                // Fire Location
                Surface(Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("🔥 Fire Incident Location", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        Spacer(Modifier.height(4.dp))
                        Text(incident.incidentLocation, fontWeight = FontWeight.Medium)
                    }
                }

                // Description
                if (incident.description.isNotEmpty()) {
                    Surface(Modifier.fillMaxWidth(), color = Color(0xFFFFF8E1), shape = RoundedCornerShape(8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("📝 Description", fontWeight = FontWeight.Bold, color = Color(0xFFF57C00))
                            Spacer(Modifier.height(4.dp))
                            Text(incident.description)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Go to Location - STARTS CONTINUOUS TRACKING
                Button(
                    onClick = {
                        isUpdatingLocation = true
                        startFirefighterLocationTracking(
                            context = context,
                            onLocationRequired = {
                                isUpdatingLocation = false
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            onSuccess = {
                                isUpdatingLocation = false
                                Toast.makeText(context, "📍 Location tracking started", Toast.LENGTH_SHORT).show()
                                onGoToLocation()
                            },
                            onFailure = {
                                isUpdatingLocation = false
                                onGoToLocation()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUpdatingLocation
                ) {
                    if (isUpdatingLocation) {
                        CircularProgressIndicator(Modifier.size(18.dp), Color.White, strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Go to Location")
                }

                // Acknowledge - STARTS CONTINUOUS TRACKING
                if (!isAcknowledged) {
                    Button(
                        onClick = {
                            isUpdatingLocation = true
                            startFirefighterLocationTracking(
                                context = context,
                                onLocationRequired = {
                                    isUpdatingLocation = false
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                onSuccess = {
                                    isUpdatingLocation = false
                                    Toast.makeText(context, "📍 Location tracking started", Toast.LENGTH_SHORT).show()
                                    onAcknowledge()
                                    isAcknowledged = true
                                },
                                onFailure = {
                                    isUpdatingLocation = false
                                    onAcknowledge()
                                    isAcknowledged = true
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdatingLocation
                    ) {
                        if (isUpdatingLocation) {
                            CircularProgressIndicator(Modifier.size(18.dp), Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Acknowledge")
                    }
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