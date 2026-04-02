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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.PopupProperties
import androidx.compose.material.icons.filled.Check

data class DirectionStep(
    val instruction: String,
    val distance: String,
    val duration: String,
    val startLocation: LatLng,
    val maneuver: String = ""
)

data class DirectionsResult(
    val polylinePoints: List<LatLng>,
    val distance: String,
    val duration: String,
    val steps: List<DirectionStep> = emptyList()
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

// Directions API Function
suspend fun fetchDirections(origin: LatLng, destination: LatLng): DirectionsResult? {
    val apiKey = "AIzaSyCdwrCeDEi3tPzjg6-2lGNueLJqHJFv1ZQ"
    val url = "https://maps.googleapis.com/maps/api/directions/json?" +
            "origin=${origin.latitude},${origin.longitude}" +
            "&destination=${destination.latitude},${destination.longitude}" +
            "&mode=driving" +
            "&alternatives=true" +
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

        // Pick the fastest route
        var bestRouteIndex = 0
        var bestDuration = Int.MAX_VALUE
        for (i in 0 until routes.length()) {
            val r = routes.getJSONObject(i)
            val durationVal = r.getJSONArray("legs")
                .getJSONObject(0)
                .getJSONObject("duration")
                .getInt("value")
            if (durationVal < bestDuration) {
                bestDuration = durationVal
                bestRouteIndex = i
            }
        }
        val route = routes.getJSONObject(bestRouteIndex)
        val legs = route.getJSONArray("legs")
        val leg = legs.getJSONObject(0)
        val distance = leg.getJSONObject("distance").getString("text")
        val duration = leg.getJSONObject("duration").getString("text")

        val stepsArray = leg.getJSONArray("steps")

// Build accurate polyline from each step
        val decodedPath = mutableListOf<LatLng>()
        for (i in 0 until stepsArray.length()) {
            val step = stepsArray.getJSONObject(i)
            val stepPolyline = step.getJSONObject("polyline").getString("points")
            decodedPath.addAll(decodePolyline(stepPolyline))
        }

// Parse turn-by-turn steps
        val steps = mutableListOf<DirectionStep>()
        for (i in 0 until stepsArray.length()) {
            val step = stepsArray.getJSONObject(i)
            val instruction = step.optString("html_instructions", "Continue")
            val maneuver = step.optString("maneuver", "")
            val stepDistance = step.getJSONObject("distance").getString("text")
            val stepDuration = step.getJSONObject("duration").getString("text")
            val startLoc = step.getJSONObject("start_location")
            val startLatLng = LatLng(
                startLoc.getDouble("lat"),
                startLoc.getDouble("lng")
            )
            steps.add(
                DirectionStep(
                    instruction = instruction,
                    distance = stepDistance,
                    duration = stepDuration,
                    startLocation = startLatLng,
                    maneuver = maneuver
                )
            )
        }

        for (i in 0 until stepsArray.length()) {
            val step = stepsArray.getJSONObject(i)
            val instruction = step.optString("html_instructions", "Continue")
            val stepDistance = step.getJSONObject("distance").getString("text")
            val maneuver2 = step.optString("maneuver", "")
            val stepDuration = step.getJSONObject("duration").getString("text")
            val startLoc = step.getJSONObject("start_location")
            val startLatLng = LatLng(
                startLoc.getDouble("lat"),
                startLoc.getDouble("lng")
            )

            steps.add(
                DirectionStep(
                    instruction = instruction,
                    distance = stepDistance,
                    duration = stepDuration,
                    startLocation = startLatLng,
                    maneuver = maneuver2
                )
            )
        }

        DirectionsResult(
            polylinePoints = decodedPath,
            distance = distance,
            duration = duration,
            steps = steps
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
    changeType: String,
    hydrantId: String,
    hydrantName: String = "",
    exactLocation: String = "",
    latitude: String = "",
    longitude: String = "",
    typeColor: String = "",
    newStatus: String = "",
    remarks: String = "",
    municipality: String,
    performedBy: String,
    changedFields: Map<String, Pair<Any?, Any?>> = emptyMap()
) {
    val firestore = Firebase.firestore

    val folder = when (changeType) {
        "added"   -> "Added"
        "deleted" -> "Deleted"
        else      -> "Changes"
    }

    val sdfDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH)
    val sdfTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.ENGLISH)
    sdfDate.timeZone = java.util.TimeZone.getTimeZone("Asia/Manila")
    sdfTime.timeZone = java.util.TimeZone.getTimeZone("Asia/Manila")
    val dateId = sdfDate.format(java.util.Date())
    val timeId = sdfTime.format(java.util.Date())

    val changeData = hashMapOf<String, Any?>(
        "ChangeType"    to changeType.replaceFirstChar { it.uppercase() },
        "HydrantId"     to hydrantId,
        "HydrantName"   to hydrantName,
        "ExactLocation" to exactLocation,
        "Latitude"      to latitude,
        "Longitude"     to longitude,
        "TypeColor"     to typeColor,
        "ServiceStatus" to newStatus,
        "Remarks"       to remarks,
        "Municipality"  to municipality,
        "PerformedBy"   to performedBy,
        "Timestamp"     to com.google.firebase.Timestamp.now()
    )


    if (folder == "Changes" && changedFields.isNotEmpty()) {
        changeData["ChangedFields"] = changedFields.map { (field, values) ->
            hashMapOf(
                "Field"    to field,
                "OldValue" to (values.first?.toString() ?: ""),
                "NewValue" to (values.second?.toString() ?: "")
            )
        }
    }

    firestore.collection("hydrant_changes")
        .document(folder)
        .collection(dateId)
        .document(timeId)
        .set(changeData)
        .addOnSuccessListener {
            android.util.Log.d("HydrantChange", "Change logged: $dateId/$timeId")
        }
        .addOnFailureListener { e ->
            android.util.Log.e("HydrantChange", "Error logging change", e)
        }
}

fun sendHydrantChangeNotificationToAdmins(
    context: Context,
    changeType: String,
    hydrantId: String,
    hydrantName: String,
    municipality: String,
    performedBy: String
) {
    val firestore = Firebase.firestore
    val title = when (changeType) {
        "added"   -> "🆕 Hydrant Added"
        "deleted" -> "🗑️ Hydrant Deleted"
        else      -> "✏️ Hydrant Updated"
    }
    val message = when (changeType) {
        "added"   -> "$hydrantName added in $municipality by $performedBy"
        "deleted" -> "$hydrantName deleted in $municipality by $performedBy"
        else      -> "$hydrantName updated in $municipality by $performedBy"
    }
    firestore.collection("hydrant_notifications")
        .add(hashMapOf(
            "title"        to title,
            "message"      to message,
            "changeType"   to changeType,
            "hydrantId"    to hydrantId,
            "hydrantName"  to hydrantName,
            "municipality" to municipality,
            "performedBy"  to performedBy,
            "timestamp"    to com.google.firebase.Timestamp.now(),
            "seen"         to false
        ))
        .addOnFailureListener { e ->
            android.util.Log.e("HydrantNotif", "Failed to send notification", e)
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
    val notifiedAdmins = mutableListOf<String>()

// Get all chief admins
    firestore.collection("admins")
        .document("chief_admin")
        .collection("all")
        .get()
        .addOnSuccessListener { chiefDocs ->
            chiefDocs.documents.forEach { doc ->
                val adminEmail = doc.getString("email") ?: doc.id
                notifiedAdmins.add(adminEmail)
            }

            // Get municipality admins for this specific municipality
            firestore.collection("admins")
                .document("municipality_admin")
                .collection(municipality)
                .get()
                .addOnSuccessListener { munDocs ->
                    munDocs.documents.forEach { doc ->
                        val adminEmail = doc.getString("email") ?: doc.id
                        notifiedAdmins.add(adminEmail)
                    }

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
                    android.util.Log.e("FireIncident", "Error getting municipality admins", e)
                }
        }
        .addOnFailureListener { e ->
            android.util.Log.e("FireIncident", "Error getting chief admins", e)
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
                            val slashProgress = remember { Animatable(if (passwordVisible) 0f else 1f) }

                            LaunchedEffect(passwordVisible) {
                                slashProgress.animateTo(
                                    targetValue = if (passwordVisible) 0f else 1f,
                                    animationSpec = tween(durationMillis = 150, easing = LinearEasing)
                                )
                            }

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Box(
                                    modifier = Modifier.size(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.eye_on),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Canvas(modifier = Modifier.size(24.dp)) {
                                        val p = slashProgress.value
                                        if (p > 0f) {
                                            val startX = size.width * 0.15f  // was 0.85f
                                            val startY = size.height * 0.05f
                                            val endX = size.width * 0.85f    // was 0.15f
                                            val endY = size.height * 0.95f

                                            val animEndX = startX + (endX - startX) * p
                                            val animEndY = startY + (endY - startY) * p

                                            drawLine(
                                                color = Color.White,
                                                start = Offset(startX, startY),
                                                end = Offset(animEndX, animEndY),
                                                strokeWidth = 5.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                            drawLine(
                                                color = Color(0xFF444444),
                                                start = Offset(startX, startY),
                                                end = Offset(animEndX, animEndY),
                                                strokeWidth = 2.5.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        }
                                    }
                                }
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
    var isPasswordFocused by remember { mutableStateOf(false) } // ✅ NEW: Track password field focus

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

                // Password field with focus tracking - ✅ MODIFIED
                item {
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it.filter { char -> !char.isWhitespace() }
                            localError = ""
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val slashProgress = remember { Animatable(if (passwordVisible) 0f else 1f) }

                            LaunchedEffect(passwordVisible) {
                                slashProgress.animateTo(
                                    targetValue = if (passwordVisible) 0f else 1f,
                                    animationSpec = tween(durationMillis = 150, easing = LinearEasing)
                                )
                            }

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Box(
                                    modifier = Modifier.size(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.eye_on),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Canvas(modifier = Modifier.size(24.dp)) {
                                        val p = slashProgress.value
                                        if (p > 0f) {
                                            val startX = size.width * 0.15f
                                            val startY = size.height * 0.05f
                                            val endX = size.width * 0.85f
                                            val endY = size.height * 0.95f

                                            val animEndX = startX + (endX - startX) * p
                                            val animEndY = startY + (endY - startY) * p

                                            drawLine(
                                                color = Color.White,
                                                start = Offset(startX, startY),
                                                end = Offset(animEndX, animEndY),
                                                strokeWidth = 5.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                            drawLine(
                                                color = Color(0xFF444444),
                                                start = Offset(startX, startY),
                                                end = Offset(animEndX, animEndY),
                                                strokeWidth = 2.5.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isPasswordFocused = focusState.isFocused // ✅ Track focus state
                            },
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            focusedLabelColor = Color(0xFFFF6B35)
                        )
                    )
                }

                // ✅ MODIFIED: Only show password hint when field is focused
                if (isPasswordFocused) {
                    item {
                        Text(
                            text = "Password must have: 12 characters, 1 uppercase, 1 number, 1 special character",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // Confirm Password field
                item {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it.filter { char -> !char.isWhitespace() }
                            localError = ""
                        },
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val slashProgress = remember { Animatable(if (confirmPasswordVisible) 0f else 1f) }

                            LaunchedEffect(confirmPasswordVisible) {
                                slashProgress.animateTo(
                                    targetValue = if (confirmPasswordVisible) 0f else 1f,
                                    animationSpec = tween(durationMillis = 150, easing = LinearEasing)
                                )
                            }

                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Box(
                                    modifier = Modifier.size(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.eye_on),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Canvas(modifier = Modifier.size(24.dp)) {
                                        val p = slashProgress.value
                                        if (p > 0f) {
                                            val startX = size.width * 0.15f
                                            val startY = size.height * 0.05f
                                            val endX = size.width * 0.85f
                                            val endY = size.height * 0.95f

                                            val animEndX = startX + (endX - startX) * p
                                            val animEndY = startY + (endY - startY) * p

                                            drawLine(
                                                color = Color.White,
                                                start = Offset(startX, startY),
                                                end = Offset(animEndX, animEndY),
                                                strokeWidth = 5.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                            drawLine(
                                                color = Color(0xFF444444),
                                                start = Offset(startX, startY),
                                                end = Offset(animEndX, animEndY),
                                                strokeWidth = 2.5.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        }
                                    }
                                }
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
    var hydrantListInitialFilter by rememberSaveable { mutableStateOf("All") }

    // Fire Incident Alert States
    var showFireIncidentAlert by remember { mutableStateOf(false) }
    var currentFireIncident by remember { mutableStateOf<FireIncidentAlert?>(null) }
    var navigateToMapWithIncident by remember { mutableStateOf(false) }
    var incidentLocationForMap by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isAdminForIncident by remember { mutableStateOf(false) }
    var showMailScreen by rememberSaveable { mutableStateOf(false) }
    var pendingHydrantNavigate by remember { mutableStateOf<FireHydrant?>(null) }
    var pendingHydrantDirections by remember { mutableStateOf<FireHydrant?>(null) }

    var backPressedOnce by remember { mutableStateOf(false) }

    val favoritesScrollState = androidx.compose.foundation.lazy.rememberLazyListState()

    // ✅ Preload hydrant data at app start so Map is ready before user taps it
    val sharedHydrantViewModel: FireHydrantViewModel = viewModel()
    LaunchedEffect(Unit) {
        sharedHydrantViewModel.loadAllHydrants()
    }

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
                .document("chief_admin")
                .collection("all")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { chiefDoc ->
                    if (chiefDoc.exists()) {
                        isAdminForIncident = true
                        val isChiefAdmin = true
                        val adminMunicipality: String? = null

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
                                        firestore.collection("fire_incident_reports")
                                            .document(doc.id)
                                            .update("notified", true)
                                        currentFireIncident = incident
                                        showFireIncidentAlert = true
                                        showFireIncidentNotification(
                                            context = context,
                                            reporterName = incident.reporterName,
                                            incidentLocation = "${incident.incidentLocation}, ${incident.municipality}",
                                            description = incident.description
                                        )
                                    }
                                }
                            }
                    } else {
                        // Check municipality_admin
                        firestore.collection("admins")
                            .document("municipality_admin")
                        val tasks = lagunaMunicipalities.map { mun ->
                            firestore.collection("admins")
                                .document("municipality_admin")
                                .collection(mun)
                                .document(userEmail.lowercase())
                                .get()
                        }
                        com.google.android.gms.tasks.Tasks.whenAllSuccess<com.google.firebase.firestore.DocumentSnapshot>(tasks)
                            .addOnSuccessListener { results ->
                                val munDoc = results.firstOrNull { it.exists() }
                                if (munDoc != null) {
                                    isAdminForIncident = true
                                    val adminMunicipality = munDoc.getString("municipality")

                                    firestore.collection("fire_incident_reports")
                                        .whereEqualTo("status", "pending")
                                        .whereEqualTo("notified", false)
                                        .addSnapshotListener { snapshot, error ->
                                            if (error != null) return@addSnapshotListener
                                            snapshot?.documentChanges?.forEach { change ->
                                                if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                                                    val doc = change.document
                                                    val reportMunicipality = doc.getString("municipality") ?: ""
                                                    if (adminMunicipality == reportMunicipality) {
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
                                                        firestore.collection("fire_incident_reports")
                                                            .document(doc.id)
                                                            .update("notified", true)
                                                        currentFireIncident = incident
                                                        showFireIncidentAlert = true
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
                .addOnFailureListener {
                    isAdminForIncident = false
                }
        }
    }

    // Fire Incident Alert Dialog — shown on top of everything
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

    // Determine if any overlay screen is active
    val isOverlayActive = showHelpFaqScreen || showContactSupportScreen || showReportProblemScreen ||
            showAboutAppScreen || showTermsPrivacyScreen || showAddAdminScreen ||
            showRemoveAdminScreen || showSettingsScreen || showMailScreen ||
            selectedHydrantForView != null || selectedHydrantForEdit != null ||
            (showHydrantMapScreen && selectedMunicipality != null) ||
            (showHydrantListScreen && selectedMunicipality != null) ||
            showAddHydrantScreen

    // ── PERMANENT BASE: NavigationSuiteScaffold + MapScreen always in composition ──
    BackHandler(enabled = !isOverlayActive) {
        if (backPressedOnce) {
            backPressedOnce = false
            (context as? ComponentActivity)?.finish()
        } else {
            backPressedOnce = true
            android.widget.Toast.makeText(context, "Press back again to exit", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

                // MapScreen — always alive, just hidden when another tab is active
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = if (currentDestination == AppDestinations.MAP) 1f else 0f
                        }
                ) {
                    MapScreen(
                        modifier = Modifier.fillMaxSize(),
                        initialIncidentLocation = if (navigateToMapWithIncident) incidentLocationForMap else null,
                        onIncidentHandled = {
                            navigateToMapWithIncident = false
                            incidentLocationForMap = null
                        },
                        onMailClick = { showMailScreen = true },
                        initialHydrantForNavigate = pendingHydrantNavigate,
                        onHydrantNavigateHandled = { pendingHydrantNavigate = null },
                        initialHydrantForDirections = pendingHydrantDirections,
                        onHydrantDirectionsHandled = { pendingHydrantDirections = null }
                    )
                }

                // Home and Profile are lightweight — only compose when active
                when (currentDestination) {
                    AppDestinations.HOME -> FavoritesScreen(
                        modifier = Modifier.fillMaxSize(),
                        scrollState = favoritesScrollState,
                        onMunicipalityClick = { municipality ->
                            selectedMunicipality = municipality
                        }
                    )
                    AppDestinations.PROFILE -> ProfileScreen(
                        modifier = Modifier.fillMaxSize(),
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
                    else -> {}
                }
            }
        }
        }

        // ── OVERLAY SCREENS: rendered on top, MapScreen stays alive underneath ──
        when {
            showHelpFaqScreen -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    BackHandler { showHelpFaqScreen = false }
                    HelpFaqScreen(onBack = { showHelpFaqScreen = false })
                }
            }
            showContactSupportScreen -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    BackHandler { showContactSupportScreen = false }
                    ContactSupportScreen(onBack = { showContactSupportScreen = false })
                }
            }
            showReportProblemScreen -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    BackHandler { showReportProblemScreen = false }
                    ReportProblemScreen(onBack = { showReportProblemScreen = false })
                }
            }
            showAboutAppScreen -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    BackHandler { showAboutAppScreen = false }
                    AboutAppScreen(onBack = { showAboutAppScreen = false })
                }
            }
            showTermsPrivacyScreen -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    BackHandler { showTermsPrivacyScreen = false }
                    TermsPrivacyScreen(onBack = { showTermsPrivacyScreen = false })
                }
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
                    android.widget.Toast.makeText(context, "$email has been promoted to admin!", android.widget.Toast.LENGTH_LONG).show()
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
                    android.widget.Toast.makeText(context, "$email has been removed from admin!", android.widget.Toast.LENGTH_LONG).show()
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
                onLogout = onLogout
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
                onBack = { selectedHydrantForView = null },
                onNavigate = { hydrant ->
                    selectedHydrantForView = null
                    showHydrantListScreen = false
                    showHydrantMapScreen = false
                    showAddHydrantScreen = false
                    selectedMunicipality = null
                    pendingHydrantNavigate = hydrant
                    currentDestination = AppDestinations.MAP
                },
                onDirections = { hydrant ->
                    selectedHydrantForView = null
                    showHydrantListScreen = false
                    showHydrantMapScreen = false
                    showAddHydrantScreen = false
                    selectedMunicipality = null
                    pendingHydrantDirections = hydrant
                    currentDestination = AppDestinations.MAP
                }
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
            BackHandler { showHydrantMapScreen = false }
            FireHydrantMapScreen(
                municipalityName = selectedMunicipality!!,
                onBack = { showHydrantMapScreen = false },
                onShowInvalidCoordinates = {
                    showInvalidCoordinatesOnly = true
                    showHydrantMapScreen = false
                    showHydrantListScreen = true
                },
                onNavigate = { hydrant ->
                    showHydrantMapScreen = false
                    showHydrantListScreen = false
                    selectedMunicipality = null
                    pendingHydrantNavigate = hydrant
                    currentDestination = AppDestinations.MAP
                },
                onDirections = { hydrant ->
                    showHydrantMapScreen = false
                    showHydrantListScreen = false
                    selectedMunicipality = null
                    pendingHydrantDirections = hydrant
                    currentDestination = AppDestinations.MAP
                }
            )
        }
        showHydrantListScreen && selectedMunicipality != null -> {
            BackHandler { showHydrantListScreen = false }
            FireHydrantListScreen(
                municipalityName = selectedMunicipality!!,
                onBack = { showHydrantListScreen = false },
                onEditHydrant = { hydrant ->
                    selectedHydrantForEdit = hydrant
                },
                onViewLocation = { hydrant ->
                    selectedHydrantForView = hydrant
                },
                showInvalidCoordinatesOnly = showInvalidCoordinatesOnly,
                onClearInvalidFilter = { showInvalidCoordinatesOnly = false },
                initialFilter = hydrantListInitialFilter
            )
        }
        showAddHydrantScreen && selectedMunicipality != null -> {
            BackHandler { showAddHydrantScreen = false }
            AddFireHydrantScreen(
                municipalityName = selectedMunicipality!!,
                onBack = { showAddHydrantScreen = false },
                onSuccess = { showAddHydrantScreen = false }
            )
        }
        selectedMunicipality != null -> {
            BackHandler { selectedMunicipality = null }
            MunicipalityDetailScreen(
                municipalityName = selectedMunicipality!!,
                onBack = { selectedMunicipality = null },
                onAddHydrant = { showAddHydrantScreen = true },
                onShowHydrantList = { filter ->
                    hydrantListInitialFilter = filter
                    showHydrantListScreen = true
                },
                onShowHydrantMap = { showHydrantMapScreen = true }
            )
        }
    }
} // closes root Box
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FireHydrantListScreen(
    municipalityName: String,
    onBack: () -> Unit,
    onEditHydrant: (FireHydrant) -> Unit,
    onViewLocation: (FireHydrant) -> Unit,
    showInvalidCoordinatesOnly: Boolean = false,
    onClearInvalidFilter: () -> Unit = {},
    initialFilter: String = "All"
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
    var selectedFilter by rememberSaveable { mutableStateOf(initialFilter) }
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
                .document("chief_admin")
                .collection("all")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { chiefDoc ->
                    if (chiefDoc.exists()) {
                        isAdmin = true
                        hasAccessToMunicipality = true
                        isCheckingAdmin = false
                    } else {
                        firestore.collection("admins")
                            .document("municipality_admin")
                            .collection(municipalityName)
                            .document(userEmail.lowercase())
                            .get()
                            .addOnSuccessListener { munDoc ->
                                isAdmin = munDoc.exists()
                                hasAccessToMunicipality = munDoc.exists()
                                isCheckingAdmin = false
                            }
                            .addOnFailureListener {
                                isAdmin = false
                                hasAccessToMunicipality = false
                                isCheckingAdmin = false
                            }
                    }
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
                                    onDismissRequest = { filterExpanded = false },
                                    properties = PopupProperties(focusable = true, clippingEnabled = false)
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
                            onDismissRequest = { filterExpanded = false },
                            properties = PopupProperties(focusable = true, clippingEnabled = false)
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
                    color = when (hydrant.serviceStatus) {
                        "In Service" -> Color(0xFF81C784)
                        "Occupied" -> Color(0xFFFF9800)
                        else -> Color(0xFFEF5350)
                    },
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
                exactLocation = exactLocation,
                latitude = latitude,
                longitude = longitude,
                typeColor = typeColor,
                newStatus = serviceStatus,
                remarks = remarks,
                municipality = hydrant.municipality,
                performedBy = currentUserEmail,
                changedFields = buildMap {
                    if (exactLocation != hydrant.exactLocation)
                        put("exactLocation", Pair(hydrant.exactLocation, exactLocation))
                    if (latitude != hydrant.latitude)
                        put("latitude", Pair(hydrant.latitude, latitude))
                    if (longitude != hydrant.longitude)
                        put("longitude", Pair(hydrant.longitude, longitude))
                    if (typeColor != hydrant.typeColor)
                        put("typeColor", Pair(hydrant.typeColor, typeColor))
                    if (serviceStatus != hydrant.serviceStatus)
                        put("serviceStatus", Pair(hydrant.serviceStatus, serviceStatus))
                    if (remarks != hydrant.remarks)
                        put("remarks", Pair(hydrant.remarks, remarks))
                }
            )

            sendHydrantChangeNotificationToAdmins(
                context = context,
                changeType = "updated",
                hydrantId = hydrant.id,
                hydrantName = hydrant.hydrantName,
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
                exactLocation = hydrant.exactLocation,
                latitude = hydrant.latitude,
                longitude = hydrant.longitude,
                typeColor = hydrant.typeColor,
                newStatus = hydrant.serviceStatus,
                remarks = hydrant.remarks,
                municipality = hydrant.municipality,
                performedBy = currentUserEmail
            )

            sendHydrantChangeNotificationToAdmins(
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
                hydrantId = hydrantUiState.lastAddedHydrantName,
                hydrantName = hydrantUiState.lastAddedHydrantName,
                exactLocation = exactLocation,
                latitude = latitude,
                longitude = longitude,
                typeColor = typeColor,
                newStatus = serviceStatus,
                remarks = remarks,
                municipality = municipalityName,
                performedBy = currentUserEmail
            )

            sendHydrantChangeNotificationToAdmins(
                context = context,
                changeType = "added",
                hydrantId = hydrantUiState.lastAddedHydrantName,
                hydrantName = hydrantUiState.lastAddedHydrantName,
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

fun showHydrantOccupiedNotification(
    context: Context,
    hydrantId: String,
    hydrantName: String,
    municipality: String,
    occupiedBy: String,
    occupiedAt: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return
    }

    val notifId = (hydrantId + municipality).hashCode()

    // === ACKNOWLEDGE: extends 1hr before next notification ===
    val acknowledgeIntent = Intent(context, HydrantOccupiedReceiver::class.java).apply {
        action = "ACTION_ACKNOWLEDGE"
        putExtra("hydrantId", hydrantId)
        putExtra("hydrantName", hydrantName)
        putExtra("municipality", municipality)
        putExtra("occupiedBy", occupiedBy)
        putExtra("notifId", notifId)
    }
    val acknowledgePendingIntent = PendingIntent.getBroadcast(
        context, notifId + 1, acknowledgeIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // === RELEASE: releases the hydrant in Firebase ===
    val releaseIntent = Intent(context, HydrantOccupiedReceiver::class.java).apply {
        action = "ACTION_RELEASE"
        putExtra("hydrantId", hydrantId)
        putExtra("hydrantName", hydrantName)
        putExtra("municipality", municipality)
        putExtra("notifId", notifId)
    }
    val releasePendingIntent = PendingIntent.getBroadcast(
        context, notifId + 2, releaseIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "hydrant_updates_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("Hydrant Still Occupied")
        .setContentText("$hydrantName ($municipality) has been occupied for 1 hour by $occupiedBy")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("$hydrantName in $municipality has been occupied for 1 hour.\nOccupied by: $occupiedBy"))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(false)   // keep it until acted upon
        .setOngoing(false)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setVibrate(longArrayOf(0, 400, 200, 400))
        .addAction(android.R.drawable.ic_input_add, "Acknowledge", acknowledgePendingIntent)
        .addAction(android.R.drawable.ic_delete, "Release", releasePendingIntent)
        .build()

    with(NotificationManagerCompat.from(context)) {
        try { notify(notifId, notification) } catch (e: SecurityException) { e.printStackTrace() }
    }
}

fun showHydrantChangeNotification(context: Context, title: String, message: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return
    }
    val prefs = context.getSharedPreferences("firegrid_prefs", Context.MODE_PRIVATE)
    if (!prefs.getBoolean("notifications_enabled", true) ||
        !prefs.getBoolean("hydrant_updates_enabled", true)) return

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val notification = NotificationCompat.Builder(context, "hydrant_updates_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setVibrate(longArrayOf(0, 250, 250, 250))
        .build()
    with(NotificationManagerCompat.from(context)) {
        try { notify(System.currentTimeMillis().toInt(), notification) }
        catch (e: SecurityException) { e.printStackTrace() }
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
    onShowHydrantList: (String) -> Unit,
    onShowHydrantMap: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""
    val firestore = Firebase.firestore

    var isGeneratingReport by remember { mutableStateOf(false) }

    // Map marker states
    var markersReady by remember { mutableStateOf(false) }
    var greenMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var redMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var orangeMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }

    var isAdmin by remember { mutableStateOf(false) }
    var hasAccessToMunicipality by remember { mutableStateOf(false) }
    var isChiefAdmin by remember { mutableStateOf(false) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    // Dialog states
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var showReportOptionsDialog by remember { mutableStateOf(false) }
    var showReportDropdown by remember { mutableStateOf(false) }

    // ✅ NEW: Track which filter is selected when showing report options
    var currentReportFilter by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

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

    val outOfService = remember(hydrantUiState.hydrants) {
        hydrantUiState.hydrants.count { it.serviceStatus == "Out of Service" }
    }
    val inService = remember(hydrantUiState.hydrants) {
        hydrantUiState.hydrants.count { it.serviceStatus == "In Service" || it.serviceStatus == "Occupied" }
    }
    val totalHydrants = remember(outOfService, inService) { outOfService + inService }

    LaunchedEffect(userEmail, municipalityName) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("admins")
                .document("chief_admin")
                .collection("all")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { chiefDoc ->
                    if (chiefDoc.exists()) {
                        isAdmin = true
                        isChiefAdmin = true
                        hasAccessToMunicipality = true
                        isCheckingAdmin = false
                    } else {
                        firestore.collection("admins")
                            .document("municipality_admin")
                            .collection(municipalityName)
                            .document(userEmail.lowercase())
                            .get()
                            .addOnSuccessListener { munDoc ->
                                isAdmin = munDoc.exists()
                                isChiefAdmin = false
                                hasAccessToMunicipality = munDoc.exists()
                                isCheckingAdmin = false
                            }
                            .addOnFailureListener {
                                isAdmin = false
                                isChiefAdmin = false
                                hasAccessToMunicipality = false
                                isCheckingAdmin = false
                            }
                    }
                }
                .addOnFailureListener {
                    isAdmin = false
                    isChiefAdmin = false
                    hasAccessToMunicipality = false
                    isCheckingAdmin = false
                }
        } else {
            isAdmin = false
            isChiefAdmin = false
            hasAccessToMunicipality = false
            isCheckingAdmin = false
        }
    }


    // ========== PDF FUNCTIONS ==========

    // ✅ UPDATED: accepts filterStatus — null = all hydrants, "Out of Service" = filtered
    fun generateAndDownloadPdfReport(filterStatus: String? = null) {
        isGeneratingReport = true
        scope.launch {
            try {
                // ✅ Filter hydrants based on filterStatus
                val hydrants = hydrantUiState.hydrants.let { list ->
                    if (filterStatus != null) list.filter { it.serviceStatus == filterStatus } else list
                }

                if (hydrants.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        dialogMessage = if (filterStatus != null)
                            "No \"$filterStatus\" hydrants to generate report"
                        else
                            "No hydrants to generate report"
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

                    val colWidths = intArrayOf(75, 45, 170, 85, 85, 80, 45, 45, 170)
                    val tableWidth = colWidths.sum()         // = 800
                    val tableStartX = (pageWidth - tableWidth) / 2f  // centers table: (842 - 800) / 2 = 21f

                    val cellPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 6f
                    }

                    // ✅ Counts based on filtered list
                    val filteredInService = hydrants.count { it.serviceStatus == "In Service" }
                    val filteredOutOfService = hydrants.count { it.serviceStatus == "Out of Service" }
                    val filteredTotal = hydrants.size

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
                        yPos += 16

                        // ✅ Filter label
                        val filterLabelPaint = android.graphics.Paint().apply {
                            textSize = 9f
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                            color = if (filterStatus == "Out of Service")
                                android.graphics.Color.rgb(211, 47, 47)
                            else
                                android.graphics.Color.rgb(46, 125, 50)
                        }
                        val filterLabel = if (filterStatus != null) "Status: $filterStatus Only" else "Status: All Hydrants"
                        canvas.drawText(filterLabel, pageWidth / 2f, yPos, filterLabelPaint)
                        yPos += 16

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
                            "Total: $filteredTotal | Operational: $filteredInService | Non-Operational: $filteredOutOfService | Page ${pageNum + 1} of $totalPages",
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

                            // ✅ Extract number from hydrant name (e.g. "hydrant id_10" → "10")
                            val hydrantNumber = hydrant.hydrantName
                                .filter { it.isDigit() || it == '_' }
                                .trimStart('_')
                                .filter { it.isDigit() }
                                .takeIf { it.isNotEmpty() } ?: (i + 1).toString()

                            val rowData = arrayOf(
                                stationName,
                                hydrantNumber,
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

                    // ✅ Filename reflects filter
                    val filterSuffix = if (filterStatus != null) "_OutOfService" else "_AllHydrants"
                    val fileName = "FireHydrant_Report_${municipalityName}${filterSuffix}_${System.currentTimeMillis()}.pdf"
                    val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                    val file = java.io.File(downloadsDir, fileName)

                    val outputStream = java.io.FileOutputStream(file)
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()
                    pdfDocument.close()

                    withContext(Dispatchers.Main) {
                        dialogMessage = "✅ PDF Downloaded Successfully!\n\nSaved to: Downloads/$fileName\n\nTotal hydrants: $filteredTotal"
                        showSuccessDialog = true
                        isGeneratingReport = false
                        android.widget.Toast.makeText(context, "Report saved to Downloads folder", android.widget.Toast.LENGTH_LONG).show()
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

    // ✅ UPDATED: accepts filterStatus — null = all hydrants, "Out of Service" = filtered
    fun generateAndSendPdfReport(filterStatus: String? = null) {
        isGeneratingReport = true
        scope.launch {
            try {
                // ✅ Filter hydrants based on filterStatus
                val hydrants = hydrantUiState.hydrants.let { list ->
                    if (filterStatus != null) list.filter { it.serviceStatus == filterStatus } else list
                }

                if (hydrants.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        dialogMessage = if (filterStatus != null)
                            "No \"$filterStatus\" hydrants to generate report"
                        else
                            "No hydrants to generate report"
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

                    val colWidths = intArrayOf(75, 45, 170, 85, 85, 80, 45, 45, 170)
                    val tableWidth = colWidths.sum()         // = 800
                    val tableStartX = (pageWidth - tableWidth) / 2f  // centers table: (842 - 800) / 2 = 21f

                    val cellPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 6f
                    }

                    // ✅ Counts based on filtered list
                    val filteredInService = hydrants.count { it.serviceStatus == "In Service" }
                    val filteredOutOfService = hydrants.count { it.serviceStatus == "Out of Service" }
                    val filteredTotal = hydrants.size

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
                        yPos += 16

                        // ✅ Filter label
                        val filterLabelPaint = android.graphics.Paint().apply {
                            textSize = 9f
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                            color = if (filterStatus == "Out of Service")
                                android.graphics.Color.rgb(211, 47, 47)
                            else
                                android.graphics.Color.rgb(46, 125, 50)
                        }
                        val filterLabel = if (filterStatus != null) "Status: $filterStatus Only" else "Status: All Hydrants"
                        canvas.drawText(filterLabel, pageWidth / 2f, yPos, filterLabelPaint)
                        yPos += 16

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
                            "Total: $filteredTotal | Operational: $filteredInService | Non-Operational: $filteredOutOfService | Page ${pageNum + 1} of $totalPages",
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

                            // ✅ Extract number from hydrant name (e.g. "hydrant id_10" → "10")
                            val hydrantNumber = hydrant.hydrantName
                                .filter { it.isDigit() || it == '_' }
                                .trimStart('_')
                                .filter { it.isDigit() }
                                .takeIf { it.isNotEmpty() } ?: (i + 1).toString()

                            val rowData = arrayOf(
                                stationName,
                                hydrantNumber,
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

                    // ✅ Filename reflects filter
                    val filterSuffix = if (filterStatus != null) "_OutOfService" else "_AllHydrants"
                    val fileName = "FireHydrant_Report_${municipalityName}${filterSuffix}_${System.currentTimeMillis()}.pdf"
                    val file = java.io.File(context.cacheDir, fileName)
                    val outputStream = java.io.FileOutputStream(file)
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()
                    pdfDocument.close()

                    val filterLabel = if (filterStatus != null) "$filterStatus Hydrants" else "All Hydrants"
                    val subject = "Fire Hydrant Inventory Report ($filterLabel) - $municipalityName (${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.ENGLISH).format(java.util.Date())})"
                    // Build position label
                    val positionLabel = when {
                        isChiefAdmin -> "Chief Administrator"
                        else -> "Municipality Admin ($municipalityName)"
                    }

                    val body = """
    Fire Hydrant Inventory Report
    
    Municipality: $municipalityName, Laguna
    Report Type: $filterLabel
    Date: ${java.text.SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", java.util.Locale.ENGLISH).format(java.util.Date())}
    
    Summary:
    - Total Hydrants in Report: $filteredTotal
    - Operational: $filteredInService
    - Non-Operational: $filteredOutOfService
    
    Reported By:
    Name: ${auth.currentUser?.displayName ?: "N/A"}
    Email: $userEmail
    Position: $positionLabel
    
    Please find the detailed report attached as PDF.
    
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
                                dialogMessage = "PDF Report ($filterLabel) has been automatically sent to:\nproject.fhydrant@gmail.com\n\nTotal hydrants: $filteredTotal"
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
                            // ✅ Pass the current filter
                            generateAndDownloadPdfReport(filterStatus = currentReportFilter)
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
                            // ✅ Pass the current filter
                            generateAndSendPdfReport(filterStatus = currentReportFilter)
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
                    Text("Success", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
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

    LaunchedEffect(municipalityName, hydrantUiState.hydrants.isNotEmpty()) {
        markersReady = false
        greenMarkerIcon = null
        redMarkerIcon = null
        orangeMarkerIcon = null
        kotlinx.coroutines.delay(800)
        try {
            val greenBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val greenCanvas = android.graphics.Canvas(greenBitmap)
            val greenPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#4CAF50")
                style = android.graphics.Paint.Style.FILL
            }
            val centerX = 24f; val topRadius = 14f; val bottomY = 44f
            greenCanvas.drawCircle(centerX, topRadius + 4f, topRadius, greenPaint)
            val gTriangle = android.graphics.Path().apply {
                moveTo(centerX, bottomY); lineTo(centerX - 12f, topRadius + 12f); lineTo(centerX + 12f, topRadius + 12f); close()
            }
            greenCanvas.drawPath(gTriangle, greenPaint)
            greenPaint.color = android.graphics.Color.WHITE
            greenCanvas.drawCircle(centerX, topRadius + 4f, 6f, greenPaint)
            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(greenBitmap)

            val redBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val redCanvas = android.graphics.Canvas(redBitmap)
            val redPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#EF5350")
                style = android.graphics.Paint.Style.FILL
            }
            redCanvas.drawCircle(centerX, topRadius + 4f, topRadius, redPaint)
            val rTriangle = android.graphics.Path().apply {
                moveTo(centerX, bottomY); lineTo(centerX - 12f, topRadius + 12f); lineTo(centerX + 12f, topRadius + 12f); close()
            }
            redCanvas.drawPath(rTriangle, redPaint)
            redPaint.color = android.graphics.Color.WHITE
            redCanvas.drawCircle(centerX, topRadius + 4f, 6f, redPaint)
            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(redBitmap)

            val orangeBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val orangeCanvas = android.graphics.Canvas(orangeBitmap)
            val orangePaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#FF9800")
                style = android.graphics.Paint.Style.FILL
            }
            orangeCanvas.drawCircle(centerX, topRadius + 4f, topRadius, orangePaint)
            val oTriangle = android.graphics.Path().apply {
                moveTo(centerX, bottomY); lineTo(centerX - 12f, topRadius + 12f); lineTo(centerX + 12f, topRadius + 12f); close()
            }
            orangeCanvas.drawPath(oTriangle, orangePaint)
            orangePaint.color = android.graphics.Color.WHITE
            orangeCanvas.drawCircle(centerX, topRadius + 4f, 6f, orangePaint)
            orangeMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(orangeBitmap)

            markersReady = true
        } catch (e: Exception) {
            e.printStackTrace()
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
                    if (hasAccessToMunicipality && !isCheckingAdmin) {
                        Box {
                            Button(
                                onClick = { showReportDropdown = !showReportDropdown },
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Generate\nReport",
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            lineHeight = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        val arrowRotation by animateFloatAsState(
                                            targetValue = if (showReportDropdown) 180f else 0f,
                                            animationSpec = tween(durationMillis = 250),
                                            label = "arrowRotation"
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.arrow_drop_down),
                                            contentDescription = "Dropdown",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .rotate(arrowRotation)
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = showReportDropdown,
                                onDismissRequest = { showReportDropdown = false },
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .wrapContentWidth()
                            ) {
                                // ✅ "For Out of Service" sets filter to "Out of Service"
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "For Out of Service",
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFD32F2F)
                                        )
                                    },
                                    onClick = {
                                        showReportDropdown = false
                                        currentReportFilter = "Out of Service"
                                        showReportOptionsDialog = true
                                    }
                                )
                                // ✅ "For Hydrant Lists" sets filter to null (all hydrants)
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "For Hydrant Lists",
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF1976D2)
                                        )
                                    },
                                    onClick = {
                                        showReportDropdown = false
                                        currentReportFilter = null
                                        showReportOptionsDialog = true
                                    }
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
                            Color(0xFFFFC107),
                            Color(0xFFFF6B35),
                            Color(0xFFFF4500),
                            Color(0xFFFFB627),
                            Color(0xFFFF5722)
                        )
                    )
                )
            )
        }
    ) { padding ->
        val municipalityLocation = remember(municipalityName) { getMunicipalityCoordinates(municipalityName) }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(municipalityLocation, 12f)
        }
        val mapScope = rememberCoroutineScope()
        val initialCameraPosition = remember(municipalityName) { CameraPosition.fromLatLngZoom(municipalityLocation, 12f) }
        val isCameraMoved = remember { mutableStateOf(false) }

        LaunchedEffect(cameraPositionState.isMoving) {
            if (!cameraPositionState.isMoving) {
                val current = cameraPositionState.position
                val moved = Math.abs(current.target.latitude - initialCameraPosition.target.latitude) > 0.001 ||
                        Math.abs(current.target.longitude - initialCameraPosition.target.longitude) > 0.001 ||
                        Math.abs(current.zoom - initialCameraPosition.zoom) > 0.1f
                isCameraMoved.value = moved
            }
        }

        val mapProperties = remember { MapProperties() }
        val mapUiSettings = remember {
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false
            )
        }
        val hydrantsWithValidCoords = remember(hydrantUiState.hydrants) {
            hydrantUiState.hydrants.filter { hydrant ->
                val lat = hydrant.latitude.toDoubleOrNull()
                val lng = hydrant.longitude.toDoubleOrNull()
                lat != null && lng != null &&
                        lat in -90.0..90.0 &&
                        lng in -180.0..180.0 &&
                        !(lat == 0.0 && lng == 0.0)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(260.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = mapUiSettings,
                        properties = mapProperties,
                        onMapClick = { onShowHydrantMap() }
                    ) {
                        if (markersReady && greenMarkerIcon != null && redMarkerIcon != null && hydrantUiState.hydrants.isNotEmpty()) {
                            hydrantsWithValidCoords.forEach { hydrant ->
                                val lat = hydrant.latitude.toDoubleOrNull() ?: return@forEach
                                val lng = hydrant.longitude.toDoubleOrNull() ?: return@forEach
                                val markerIcon = when (hydrant.serviceStatus) {
                                    "In Service" -> greenMarkerIcon!!
                                    "Occupied" -> orangeMarkerIcon ?: redMarkerIcon!!
                                    else -> redMarkerIcon!!
                                }
                                val markerState = remember(hydrant.id) {
                                    com.google.maps.android.compose.MarkerState(position = LatLng(lat, lng))
                                }
                                com.google.maps.android.compose.Marker(
                                    state = markerState,
                                    title = hydrant.hydrantName,
                                    snippet = hydrant.serviceStatus,
                                    icon = markerIcon,
                                    onInfoWindowClick = { },
                                    onClick = { marker ->
                                        marker.showInfoWindow()
                                        mapScope.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLng(LatLng(lat, lng))
                                            )
                                        }
                                        true
                                    }
                                )
                            }
                        }
                    }

                    if (isCameraMoved.value) {
                        FloatingActionButton(
                            onClick = {
                                mapScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newCameraPosition(initialCameraPosition),
                                        durationMs = 600
                                    )
                                    isCameraMoved.value = false
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(40.dp),
                            containerColor = Color.White,
                            contentColor = Color(0xFFEF5350),
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Map",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("In Service", style = MaterialTheme.typography.labelSmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Out of Service", style = MaterialTheme.typography.labelSmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Occupied", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f).clickable { onShowHydrantList("Out of Service") },
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
                            modifier = Modifier.weight(1f).clickable { onShowHydrantList("In Service") },
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

                if (hasAccessToMunicipality && !isCheckingAdmin) {
                    item {
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
                            onClick = { onShowHydrantList("All") },
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
    onShowInvalidCoordinates: () -> Unit,
    onNavigate: (FireHydrant) -> Unit = {},
    onDirections: (FireHydrant) -> Unit = {}
) {
    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    // ADD these two new state variables
    var showOccupyConfirmDialog by remember { mutableStateOf(false) }
    var occupyTargetHydrant by remember { mutableStateOf<FireHydrant?>(null) }
    var isCardAdmin by remember { mutableStateOf(false) }
    var isCardCheckingAdmin by remember { mutableStateOf(false) }
    val municipalityLocation = getMunicipalityCoordinates(municipalityName)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(municipalityLocation, 12f)
    }

    var markersReady by remember { mutableStateOf(false) }
    var greenMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var redMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var orangeMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }

    // CHANGED: Added isCardVisible state
    var selectedHydrant by remember { mutableStateOf<FireHydrant?>(null) }
    var isCardVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var pendingAction by remember { mutableStateOf("") }

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            selectedHydrant?.let { hydrant ->
                when (pendingAction) {
                    "navigate" -> onNavigate(hydrant)
                    "directions" -> onDirections(hydrant)
                }
            }
            pendingAction = ""
        } else {
            pendingAction = ""
            android.widget.Toast.makeText(context, "Location is required", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
            com.google.android.gms.location.LocationServices.getSettingsClient(context)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener {
                    selectedHydrant?.let { hydrant ->
                        when (pendingAction) {
                            "navigate" -> onNavigate(hydrant)
                            "directions" -> onDirections(hydrant)
                        }
                    }
                    pendingAction = ""
                }
                .addOnFailureListener { exception ->
                    if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                        try {
                            locationSettingsLauncher.launch(
                                androidx.activity.result.IntentSenderRequest.Builder(
                                    exception.resolution.intentSender
                                ).build()
                            )
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Please turn on location", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        } else {
            pendingAction = ""
            android.widget.Toast.makeText(context, "Location permission is required", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun checkAndProceed(action: String) {
        pendingAction = action
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
        com.google.android.gms.location.LocationServices.getSettingsClient(context)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener {
                selectedHydrant?.let { hydrant ->
                    when (action) {
                        "navigate" -> onNavigate(hydrant)
                        "directions" -> onDirections(hydrant)
                    }
                }
                pendingAction = ""
            }
            .addOnFailureListener { exception ->
                if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                    try {
                        locationSettingsLauncher.launch(
                            androidx.activity.result.IntentSenderRequest.Builder(
                                exception.resolution.intentSender
                            ).build()
                        )
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Please turn on location", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        try {
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

            // Orange marker for Occupied
            val orangeBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val orangeCanvas = android.graphics.Canvas(orangeBitmap)
            val orangePaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#FF9800")
                style = android.graphics.Paint.Style.FILL
            }
            orangeCanvas.drawCircle(centerX, topRadius + 4f, topRadius, orangePaint)
            val orangeTrianglePath = android.graphics.Path()
            orangeTrianglePath.moveTo(centerX, bottomY)
            orangeTrianglePath.lineTo(centerX - 12f, topRadius + 12f)
            orangeTrianglePath.lineTo(centerX + 12f, topRadius + 12f)
            orangeTrianglePath.close()
            orangeCanvas.drawPath(orangeTrianglePath, orangePaint)
            orangePaint.color = android.graphics.Color.WHITE
            orangeCanvas.drawCircle(centerX, topRadius + 4f, 6f, orangePaint)
            orangeMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(orangeBitmap)

            markersReady = true
        } catch (e: Exception) {
            e.printStackTrace()
            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
            )
            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
            )
            markersReady = true
        }
    }

    LaunchedEffect(hydrantUiState.hydrants) {
        val current = selectedHydrant ?: return@LaunchedEffect
        val updated = hydrantUiState.hydrants.find {
            it.id == current.id && it.municipality == current.municipality
        }
        if (updated != null && updated != current) {
            selectedHydrant = updated
        }
    }

    LaunchedEffect(selectedHydrant) {
        val h = selectedHydrant
        if (h == null) { isCardAdmin = false; return@LaunchedEffect }
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) { isCardAdmin = false; return@LaunchedEffect }
        val email = currentUser.email?.lowercase() ?: run { isCardAdmin = false; return@LaunchedEffect }
        isCardCheckingAdmin = true
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("admins").document("chief_admin").collection("all").document(email).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    isCardAdmin = true; isCardCheckingAdmin = false
                } else {
                    db.collection("admins").document("municipality_admin")
                        .collection(h.municipality).document(email).get()
                        .addOnSuccessListener { munDoc -> isCardAdmin = munDoc.exists(); isCardCheckingAdmin = false }
                        .addOnFailureListener { isCardAdmin = false; isCardCheckingAdmin = false }
                }
            }
            .addOnFailureListener { isCardAdmin = false; isCardCheckingAdmin = false }
    }

    LaunchedEffect(municipalityName) {
        hydrantViewModel.loadHydrants(municipalityName)
    }

    val hydrantsWithValidCoords = hydrantUiState.hydrants.filter { hydrant ->
        val lat = hydrant.latitude.trim().replace(",", ".").toDoubleOrNull()
        val lng = hydrant.longitude.trim().replace(",", ".").toDoubleOrNull()
        lat != null && lng != null &&
                lat in -90.0..90.0 &&
                lng in -180.0..180.0 &&
                !(lat == 0.0 && lng == 0.0)
    }

    val invalidCoordinatesCount = hydrantUiState.hydrants.size - hydrantsWithValidCoords.size
    val hasInvalidCoordinates = invalidCoordinatesCount > 0

    // CHANGED: uses isCardVisible instead of selectedHydrant != null
    BackHandler(enabled = isCardVisible) {
        isCardVisible = false
    }

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
                ),
                // CHANGED: sets isCardVisible to false instead of null
                onMapClick = {
                    isCardVisible = false
                }
            ) {
                if (markersReady && greenMarkerIcon != null && redMarkerIcon != null && orangeMarkerIcon != null) {
                    hydrantsWithValidCoords.forEach { hydrant ->
                        val lat = hydrant.latitude.trim().replace(",", ".").toDoubleOrNull() ?: return@forEach
                        val lng = hydrant.longitude.trim().replace(",", ".").toDoubleOrNull() ?: return@forEach

                        val markerIcon = when (hydrant.serviceStatus) {
                            "In Service" -> greenMarkerIcon!!
                            "Occupied"   -> orangeMarkerIcon ?: redMarkerIcon!!
                            else         -> redMarkerIcon!!
                        }

                        val markerState = remember(hydrant.id) {
                            com.google.maps.android.compose.MarkerState(position = LatLng(lat, lng))
                        }

                        com.google.maps.android.compose.Marker(
                            state = markerState,
                            title = hydrant.hydrantName,
                            icon = markerIcon,
                            onClick = { _ ->
                                // CHANGED: also sets isCardVisible to true
                                selectedHydrant = hydrant
                                isCardVisible = true
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLng(LatLng(lat, lng))
                                    )
                                }
                                true
                            }
                        )
                    }
                }
            }

            // Legend card
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
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
                        Text(text = " In Service", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(text = " Out of Service", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(text = " Occupied", style = MaterialTheme.typography.bodySmall)
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

            // Zoom controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 12.dp,
                        bottom = if (isCardVisible) 220.dp else 16.dp
                    ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val currentZoom = cameraPositionState.position.zoom
                            cameraPositionState.animate(CameraUpdateFactory.zoomTo(currentZoom + 1f))
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    containerColor = Color.White,
                    contentColor = Color(0xFF5F6368),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp, pressedElevation = 5.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(text = "+", style = MaterialTheme.typography.titleLarge, color = Color(0xFF5F6368))
                }
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val currentZoom = cameraPositionState.position.zoom
                            cameraPositionState.animate(CameraUpdateFactory.zoomTo(currentZoom - 1f))
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    containerColor = Color.White,
                    contentColor = Color(0xFF5F6368),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp, pressedElevation = 5.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(text = "−", style = MaterialTheme.typography.titleLarge, color = Color(0xFF5F6368))
                }
            }


            // CHANGED: AnimatedVisibility now uses isCardVisible + LaunchedEffect delay
            androidx.compose.animation.AnimatedVisibility(
                visible = isCardVisible,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = androidx.compose.animation.slideInVertically { it } +
                        androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + androidx.compose.animation.fadeOut(
                    animationSpec = androidx.compose.animation.core.tween(300)
                )
            ) {
                // CHANGED: clears selectedHydrant only after exit animation completes
                LaunchedEffect(isCardVisible) {
                    if (!isCardVisible) {
                        kotlinx.coroutines.delay(300)
                        selectedHydrant = null
                    }
                }

                selectedHydrant?.let { hydrant ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                                    color = when (hydrant.serviceStatus) {
                                        "In Service" -> Color(0xFF81C784)
                                        "Occupied"   -> Color(0xFFFF9800)
                                        else         -> Color(0xFFEF5350)
                                    },
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

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = hydrant.exactLocation,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row {
                                Text(
                                    text = "Lat: ${hydrant.latitude}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Lng: ${hydrant.longitude}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row {
                                Text(
                                    text = "Type/Color: ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    text = hydrant.typeColor,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (!checkLocationPermission(context)) {
                                            pendingAction = "navigate"
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        } else {
                                            checkAndProceed("navigate")
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFEF5350)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_navigation),
                                        contentDescription = "Navigate",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Navigate",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        if (!checkLocationPermission(context)) {
                                            pendingAction = "directions"
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        } else {
                                            checkAndProceed("directions")
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFEF5350)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFEF5350)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Directions,
                                        contentDescription = "Directions",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Directions",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Occupy / Release button row
                            if (isCardAdmin && (hydrant.serviceStatus == "In Service" || hydrant.serviceStatus == "Occupied")) {
                                Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                val isOccupied = hydrant.serviceStatus == "Occupied"
                                OutlinedButton(
                                    onClick = {
                                        occupyTargetHydrant = hydrant
                                        showOccupyConfirmDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (isOccupied) Color(0xFF757575) else Color(0xFFFF9800)
                                    ),
                                    border = BorderStroke(1.dp, if (isOccupied) Color(0xFF757575) else Color(0xFFFF9800)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isOccupied) "Release Hydrant" else "Occupy Hydrant",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                }
                            } // closes if
                        }
                    }
                }
            } // closes AnimatedVisibility

            // ✅ Occupy / Release confirmation dialog — PASTE HERE
            if (showOccupyConfirmDialog && occupyTargetHydrant != null) {
                val h = occupyTargetHydrant!!
                val isOccupied = h.serviceStatus == "Occupied"
                AlertDialog(
                    onDismissRequest = { showOccupyConfirmDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning, null,
                                tint = if (isOccupied) Color(0xFF757575) else Color(0xFFFF9800),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (isOccupied) "Release Hydrant" else "Occupy Hydrant",
                                fontWeight = FontWeight.Bold,
                                color = if (isOccupied) Color(0xFF757575) else Color(0xFFFF9800)
                            )
                        }
                    },
                    text = {
                        Text(
                            if (isOccupied)
                                "Mark \"${h.hydrantName}\" as In Service (released)?"
                            else
                                "Mark \"${h.hydrantName}\" as Occupied? This hydrant will be shown as in use on the map."
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showOccupyConfirmDialog = false
                                val newStatus = if (isOccupied) "In Service" else "Occupied"
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                val performedBy = currentUser?.displayName?.takeIf { it.isNotBlank() }
                                    ?: currentUser?.email ?: "Unknown"
                                val rtdb = com.google.firebase.database.FirebaseDatabase
                                    .getInstance("https://firegrid-58bc3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                val sdf = java.text.SimpleDateFormat(
                                    "MMMM dd, yyyy 'at' h:mm:ss a 'UTC+8'", java.util.Locale.ENGLISH
                                )
                                sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Manila")
                                val timestamp = sdf.format(java.util.Date())
                                rtdb.getReference("fire_hydrants")
                                    .child(h.municipality)
                                    .child(h.id)
                                    .updateChildren(
                                        if (newStatus == "Occupied") {
                                            mapOf(
                                                "ServiceStatus" to newStatus,
                                                "LastUpdated" to timestamp,
                                                "OccupiedBy" to performedBy,
                                                "OccupiedAt" to timestamp
                                            )
                                        } else {
                                            mapOf(
                                                "ServiceStatus" to newStatus,
                                                "LastUpdated" to timestamp,
                                                "OccupiedBy" to "",
                                                "OccupiedAt" to ""
                                            )
                                        }
                                    )
                                    .addOnSuccessListener {
                                        if (newStatus == "Occupied") {
                                            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                                            val notifId = (h.id + h.municipality).hashCode()
                                            val scheduleIntent = Intent(context, HydrantOccupiedReceiver::class.java).apply {
                                                action = "ACTION_FIRE_NOTIFICATION"
                                                putExtra("hydrantId", h.id)
                                                putExtra("hydrantName", h.hydrantName)
                                                putExtra("municipality", h.municipality)
                                                putExtra("occupiedBy", performedBy)
                                                putExtra("notifId", notifId)
                                            }
                                            val pi = PendingIntent.getBroadcast(
                                                context, notifId + 10, scheduleIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                            )
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                alarmManager.setExactAndAllowWhileIdle(
                                                    android.app.AlarmManager.RTC_WAKEUP,
                                                    System.currentTimeMillis() + 60 * 60 * 1000L, pi
                                                )
                                            } else {
                                                alarmManager.setExact(
                                                    android.app.AlarmManager.RTC_WAKEUP,
                                                    System.currentTimeMillis() + 60 * 60 * 1000L, pi
                                                )
                                            }
                                        }
                                        // Update the displayed card immediately
                                        selectedHydrant = h.copy(serviceStatus = newStatus)
                                        occupyTargetHydrant = null
                                        Toast.makeText(
                                            context,
                                            if (newStatus == "Occupied") "🟠 Hydrant marked as Occupied"
                                            else "🟢 Hydrant released (In Service)",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                                    }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOccupied) Color(0xFF757575) else Color(0xFFFF9800)
                            )
                        ) {
                            Text(if (isOccupied) "Release" else "Occupy", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showOccupyConfirmDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    containerColor = Color.White
                )
            }

            if (hydrantUiState.isLoading || !markersReady) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFEF5350))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewHydrantLocationScreen(
    hydrant: FireHydrant,
    onBack: () -> Unit,
    onNavigate: (FireHydrant) -> Unit = {},
    onDirections: (FireHydrant) -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingAction by remember { mutableStateOf("") }

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            when (pendingAction) {
                "navigate" -> onNavigate(hydrant)
                "directions" -> onDirections(hydrant)
            }
            pendingAction = ""
        } else {
            pendingAction = ""
            android.widget.Toast.makeText(context, "Location is required", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun checkAndProceed(action: String) {
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
        com.google.android.gms.location.LocationServices.getSettingsClient(context)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener {
                when (action) {
                    "navigate" -> onNavigate(hydrant)
                    "directions" -> onDirections(hydrant)
                }
            }
            .addOnFailureListener { exception ->
                if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                    try {
                        pendingAction = action
                        locationSettingsLauncher.launch(
                            androidx.activity.result.IntentSenderRequest.Builder(
                                exception.resolution.intentSender
                            ).build()
                        )
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Please turn on location", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            checkAndProceed(pendingAction)
        } else {
            pendingAction = ""
            android.widget.Toast.makeText(context, "Location permission is required", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    var isMapReady by remember { mutableStateOf(false) }
    var mapLoaded by remember { mutableStateOf(false) }
    var markerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var markersReady by remember { mutableStateOf(false) }
    var hasCameraMoved by remember { mutableStateOf(false) }

    val lat = hydrant.latitude.toDoubleOrNull()
    val lng = hydrant.longitude.toDoubleOrNull()

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

    // Detect camera movement
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            hasCameraMoved = true
        }
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
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

            canvas.drawCircle(centerX, topRadius + 4f, topRadius, paint)

            val trianglePath = android.graphics.Path()
            trianglePath.moveTo(centerX, bottomY)
            trianglePath.lineTo(centerX - 12f, topRadius + 12f)
            trianglePath.lineTo(centerX + 12f, topRadius + 12f)
            trianglePath.close()
            canvas.drawPath(trianglePath, paint)

            paint.color = android.graphics.Color.WHITE
            canvas.drawCircle(centerX, topRadius + 4f, 6f, paint)

            markerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap)
            markersReady = true
        } catch (e: Exception) {
            e.printStackTrace()
            markerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                if (hydrant.serviceStatus == "In Service")
                    com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
                else
                    com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
            )
            markersReady = true
        }
    }

    LaunchedEffect(Unit) {
        try {
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

            // Reset button - only visible after camera has been moved
            if (mapLoaded && hasCameraMoved) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 200.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                try {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(hydrantLocation, 17f)
                                    )
                                    hasCameraMoved = false
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
                }
            }

            // Info card
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

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = hydrant.exactLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        Text(
                            text = "Lat: ${hydrant.latitude}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Lng: ${hydrant.longitude}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        Text(
                            text = "Type/Color: ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = hydrant.typeColor,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!checkLocationPermission(context)) {
                                    pendingAction = "navigate"
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                } else {
                                    checkAndProceed("navigate")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEF5350)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_navigation),
                                contentDescription = "Navigate",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Navigate",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                if (!checkLocationPermission(context)) {
                                    pendingAction = "directions"
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                } else {
                                    checkAndProceed("directions")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF5350)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFEF5350)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Directions,
                                contentDescription = "Directions",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Directions",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

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

fun getCurrentLocationWithTimeout(
    context: android.content.Context,
    timeoutMs: Long = 8000L,
    onSuccess: (android.location.Location) -> Unit,
    onTimeout: () -> Unit
) {
    val fusedClient = com.google.android.gms.location.LocationServices
        .getFusedLocationProviderClient(context)

    var delivered = false
    val handler = android.os.Handler(android.os.Looper.getMainLooper())

    fun deliver(location: android.location.Location?) {
        if (delivered) return
        delivered = true
        handler.removeCallbacksAndMessages(null)
        if (location != null) onSuccess(location) else onTimeout()
    }

    // Hard timeout — ALWAYS fires, guarantees spinner stops
    handler.postDelayed({
        deliver(null)
    }, timeoutMs)

    // Try lastLocation first — instant if available
    try {
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    deliver(location)
                }
                // if null, do nothing — requestLocationUpdates below will handle it
            }
    } catch (e: SecurityException) { /* ignore */ }

    // Also simultaneously request a fresh location — whichever comes first wins
    try {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 500L
        ).setMaxUpdates(1)
            .setMinUpdateIntervalMillis(0L)
            .build()

        val callback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                fusedClient.removeLocationUpdates(this)
                deliver(result.lastLocation)
            }
        }

        fusedClient.requestLocationUpdates(
            locationRequest,
            callback,
            android.os.Looper.getMainLooper()
        )
    } catch (e: SecurityException) {
        deliver(null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    initialIncidentLocation: Pair<Double, Double>? = null,
    onIncidentHandled: () -> Unit = {},
    onMailClick: () -> Unit = {},
    initialHydrantForNavigate: FireHydrant? = null,
    onHydrantNavigateHandled: () -> Unit = {},
    initialHydrantForDirections: FireHydrant? = null,
    onHydrantDirectionsHandled: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lagunaLake = LatLng(14.3500, 121.2500)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lagunaLake, 10f)
    }
    val density = LocalDensity.current

    var isMyLocationEnabled by remember { mutableStateOf(false) }
    var isDrawerOpen by remember { mutableStateOf(false) }
    var showOccupiedHydrantsDialog by remember { mutableStateOf(false) }
    var isSearchingNearestHydrant by remember { mutableStateOf(false) }
    var nearestHydrant by remember { mutableStateOf<FireHydrant?>(null) }
    var nearestHydrantDistance by remember { mutableStateOf<Double?>(null) }
    var nearestHydrants by remember { mutableStateOf<List<Pair<FireHydrant, Double>>>(emptyList()) }
    var showNearestDropdown by remember { mutableStateOf(false) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showReportFireDialog by remember { mutableStateOf(false) }
    var isNavigating by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var navigationHydrant by remember { mutableStateOf<FireHydrant?>(null) }
    val scope = rememberCoroutineScope()
    var triggerGoToMyLocation by remember { mutableStateOf(false) }
    var triggerFindNearestHydrant by remember { mutableStateOf(false) }
    var triggerNavigate by remember { mutableStateOf(false) }
    var triggerDirections by remember { mutableStateOf(false) }
    var pendingLocationAction by remember { mutableStateOf("") }
    var hydrantDirectionsFetched by remember { mutableStateOf(false) }
    var fireDirectionsFetched by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var lastSpokenStepIndex by remember { mutableStateOf(-1) }
    var hasSpoken300m by remember { mutableStateOf(false) }
    var hasSpokenNow by remember { mutableStateOf(false) }

    // Fire incident report counts for mail badge
    var pendingCount by remember { mutableStateOf(0) }
    var acknowledgedCount by remember { mutableStateOf(0) }
    val totalUnreadCount = pendingCount + acknowledgedCount

    // Search state
    var isSearchOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchSuggestions by remember { mutableStateOf<List<FireHydrant>>(emptyList()) }
    var totalFilteredCount by remember { mutableStateOf(0) }  // NEW: Track total count separately
    var displayLimit by remember { mutableStateOf(50) }
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

    // ── LOCATION SETTINGS CACHE ──────────────────────────────────────────────
    // Pre-checked once at startup so every button click is instant (no async IPC delay)
    var locationSettingsOk by remember { mutableStateOf(false) }
    val cachedLocationRequest = remember {
        com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
    val cachedLocationSettingsRequest = remember(cachedLocationRequest) {
        com.google.android.gms.location.LocationSettingsRequest.Builder()
            .addLocationRequest(cachedLocationRequest)
            .setAlwaysShow(true)
            .build()
    }
    val cachedSettingsClient = remember(context) {
        com.google.android.gms.location.LocationServices.getSettingsClient(context)
    }
    // ─────────────────────────────────────────────────────────────────────────
    var selectedHydrantMarker by remember { mutableStateOf<FireHydrant?>(null) }
    var showHydrantDetailsCard by remember { mutableStateOf(false) }
    var selectedHydrantForCard by remember { mutableStateOf<FireHydrant?>(null) }
    var isCardVisible by remember { mutableStateOf(false) }
    var isCardAdmin by remember { mutableStateOf(false) }
    var isCardCheckingAdmin by remember { mutableStateOf(false) }
    var showOccupyConfirmDialog by remember { mutableStateOf(false) }
    val cardTranslationY by animateFloatAsState(
        targetValue = if (isCardVisible) 0f else 1000f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "cardSlide"
    )

    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    LaunchedEffect(hydrantUiState.allHydrants, hydrantUiState.hydrants) {
        val current = selectedHydrantForCard ?: return@LaunchedEffect
        val updated = (hydrantUiState.allHydrants + hydrantUiState.hydrants).find {
            it.id == current.id && it.municipality == current.municipality
        }
        if (updated != null && updated != current) {
            selectedHydrantForCard = updated
        }
    }

    // Check admin role whenever the details card opens for a hydrant
    LaunchedEffect(selectedHydrantForCard) {
        val h = selectedHydrantForCard
        if (h == null) { isCardAdmin = false; return@LaunchedEffect }
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) { isCardAdmin = false; return@LaunchedEffect }
        val email = currentUser.email?.lowercase() ?: run { isCardAdmin = false; return@LaunchedEffect }
        isCardCheckingAdmin = true
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("admins").document("chief_admin").collection("all").document(email).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    isCardAdmin = true; isCardCheckingAdmin = false
                } else {
                    db.collection("admins").document("municipality_admin")
                        .collection(h.municipality).document(email).get()
                        .addOnSuccessListener { munDoc -> isCardAdmin = munDoc.exists(); isCardCheckingAdmin = false }
                        .addOnFailureListener { isCardAdmin = false; isCardCheckingAdmin = false }
                }
            }
            .addOnFailureListener { isCardAdmin = false; isCardCheckingAdmin = false }
    }

    BackHandler(enabled = isDrawerOpen || isSearchOpen || selectedHydrantMarker != null || isCardVisible) {
        when {
            isCardVisible -> {
                isCardVisible = false
                showHydrantDetailsCard = false
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

    val mapLocationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            when (pendingLocationAction) {
                "goToLocation" -> triggerGoToMyLocation = true
                "findNearest" -> triggerFindNearestHydrant = true
                "navigate" -> triggerNavigate = true
                "directions" -> triggerDirections = true
            }
            pendingLocationAction = ""
        } else {
            val action = pendingLocationAction
            pendingLocationAction = ""
            when (action) {
                "findNearest" -> isSearchingNearestHydrant = false
                "navigate", "directions" -> isLoadingDirections = false
            }
            android.widget.Toast.makeText(
                context,
                "Location is required for this feature",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    // TTS engine — speaks navigation instructions aloud
    DisposableEffect(Unit) {
        var instance: android.speech.tts.TextToSpeech? = null
        instance = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                instance?.language = java.util.Locale.getDefault()
                tts = instance
            }
        }
        onDispose {
            tts = null
            instance?.stop()
            instance?.shutdown()
        }
    }

    // Pre-check location settings once when the screen loads so buttons respond instantly
    LaunchedEffect(Unit) {
        cachedSettingsClient.checkLocationSettings(cachedLocationSettingsRequest)
            .addOnSuccessListener { locationSettingsOk = true }
            .addOnFailureListener { locationSettingsOk = false }
    }

    LaunchedEffect(triggerGoToMyLocation) {
        if (triggerGoToMyLocation) {
            triggerGoToMyLocation = false
            isMyLocationEnabled = true
            getCurrentLocationWithTimeout(
                context = context,
                timeoutMs = 8000L,
                onSuccess = { location ->
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                15f
                            )
                        )
                    }
                },
                onTimeout = {
                    android.widget.Toast.makeText(
                        context,
                        "Could not get location. Try again.",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    LaunchedEffect(triggerFindNearestHydrant) {
        if (triggerFindNearestHydrant) {
            triggerFindNearestHydrant = false
            if (checkLocationPermission(context)) {
                isSearchingNearestHydrant = true
                isMyLocationEnabled = true
                getCurrentLocationWithTimeout(
                    context = context,
                    timeoutMs = 8000L,
                    onSuccess = { location ->
                        userCurrentLocation = LatLng(location.latitude, location.longitude)
                        val allHydrants = hydrantUiState.allHydrants

                        // Calculate top 3 nearest hydrants inline
                        val results = allHydrants.mapNotNull { hydrant ->
                            val lat = hydrant.latitude.toDoubleOrNull() ?: return@mapNotNull null
                            val lng = hydrant.longitude.toDoubleOrNull() ?: return@mapNotNull null
                            if (lat < -90 || lat > 90 || lng < -180 || lng > 180) return@mapNotNull null
                            if (lat == 0.0 && lng == 0.0) return@mapNotNull null

                            val r = 6371.0
                            val dLat = Math.toRadians(lat - location.latitude)
                            val dLon = Math.toRadians(lng - location.longitude)
                            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                    Math.cos(Math.toRadians(location.latitude)) *
                                    Math.cos(Math.toRadians(lat)) *
                                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
                            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                            val distance = r * c
                            Pair(hydrant, distance)
                        }.sortedBy { it.second }

                        // ✅ Set all 3 nearest for the dropdown
                        nearestHydrants = results.take(3)

                        val nearest: FireHydrant? = results.firstOrNull()?.first
                        val minDistance: Double? = results.firstOrNull()?.second

                        scope.launch {
                            if (nearest != null && minDistance != null) {
                                nearestHydrant = nearest
                                nearestHydrantDistance = minDistance
                                val hydrantLat = nearest.latitude.toDoubleOrNull()
                                val hydrantLng = nearest.longitude.toDoubleOrNull()
                                if (hydrantLat != null && hydrantLng != null) {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(hydrantLat, hydrantLng), 16f
                                        )
                                    )
                                }
                            } else {
                                android.widget.Toast.makeText(
                                    context,
                                    "No hydrants found",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                            isSearchingNearestHydrant = false
                        }
                    },
                    onTimeout = {
                        scope.launch {
                            isSearchingNearestHydrant = false
                            android.widget.Toast.makeText(
                                context,
                                "Could not get location. Try again.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }

    LaunchedEffect(triggerNavigate) {
        if (triggerNavigate) {
            triggerNavigate = false
            if (checkLocationPermission(context)) {
                isLoadingDirections = true
                getCurrentLocationWithTimeout(
                    context = context,
                    timeoutMs = 8000L,
                    onSuccess = { location ->
                        userCurrentLocation = LatLng(location.latitude, location.longitude)
                        val hydrantLat = selectedHydrantForCard?.latitude?.toDoubleOrNull()
                        val hydrantLng = selectedHydrantForCard?.longitude?.toDoubleOrNull()
                        if (hydrantLat != null && hydrantLng != null) {
                            scope.launch {
                                directionsResult = fetchDirections(
                                    origin = LatLng(location.latitude, location.longitude),
                                    destination = LatLng(hydrantLat, hydrantLng)
                                )
                                isLoadingDirections = false
                                if (directionsResult != null) {
                                    navigationHydrant = selectedHydrantForCard
                                    nearestHydrant = selectedHydrantForCard
                                    currentStepIndex = 0
                                    isNavigating = true
                                    lastSpokenStepIndex = -1
                                    hasSpoken300m = false
                                    hasSpokenNow = false
                                    showHydrantDetailsCard = false
                                    selectedHydrantForCard = null
                                    // Calculate bearing from user to first waypoint for navigation tilt
                                    val bearing = run {
                                        val firstStep = directionsResult?.steps?.firstOrNull()
                                        val secondStep = directionsResult?.steps?.getOrNull(1)
                                        val from = firstStep?.startLocation
                                        val to = secondStep?.startLocation ?: firstStep?.startLocation
                                        if (from != null && to != null && from != to) {
                                            val lat1 = Math.toRadians(from.latitude)
                                            val lat2 = Math.toRadians(to.latitude)
                                            val dLng = Math.toRadians(to.longitude - from.longitude)
                                            val y = Math.sin(dLng) * Math.cos(lat2)
                                            val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng)
                                            ((Math.toDegrees(Math.atan2(y, x)) + 360) % 360).toFloat()
                                        } else 0f
                                    }
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newCameraPosition(
                                            CameraPosition.Builder()
                                                .target(LatLng(location.latitude, location.longitude))
                                                .zoom(18f)
                                                .tilt(60f)
                                                .bearing(bearing)
                                                .build()
                                        ),
                                        durationMs = 1000
                                    )
                                } else {
                                    android.widget.Toast.makeText(context, "Could not fetch directions", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            isLoadingDirections = false
                        }
                    },
                    onTimeout = {
                        isLoadingDirections = false
                        android.widget.Toast.makeText(context, "Could not get location. Try again.", android.widget.Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    LaunchedEffect(triggerDirections) {
        if (triggerDirections) {
            triggerDirections = false
            if (checkLocationPermission(context)) {
                isLoadingDirections = true
                getCurrentLocationWithTimeout(
                    context = context,
                    timeoutMs = 8000L,
                    onSuccess = { location ->
                        userCurrentLocation = LatLng(location.latitude, location.longitude)
                        val hydrantLat = selectedHydrantForCard?.latitude?.toDoubleOrNull()
                        val hydrantLng = selectedHydrantForCard?.longitude?.toDoubleOrNull()
                        if (hydrantLat != null && hydrantLng != null) {
                            scope.launch {
                                directionsResult = fetchDirections(
                                    origin = LatLng(location.latitude, location.longitude),
                                    destination = LatLng(hydrantLat, hydrantLng)
                                )
                                isLoadingDirections = false
                                nearestHydrant = selectedHydrantForCard
                                showHydrantDetailsCard = false
                                isCardVisible = false
                                if (directionsResult == null) {
                                    android.widget.Toast.makeText(context, "Could not fetch directions", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            isLoadingDirections = false
                        }
                    },
                    onTimeout = {
                        isLoadingDirections = false
                        android.widget.Toast.makeText(context, "Could not get location. Try again.", android.widget.Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    LaunchedEffect(Unit) { hydrantViewModel.loadAllHydrants() }

    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email?.lowercase() ?: ""
    DisposableEffect(Unit) {
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        // Use only ONE filter to avoid needing a composite index
        val listenerStartTime = com.google.firebase.Timestamp.now()
        val listener = firestore.collection("hydrant_notifications")
            .whereEqualTo("seen", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("HydrantNotif", "Listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener
                snapshot.documentChanges.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val doc = change.document
                        // Skip docs older than when this listener started
                        val docTime = doc.getTimestamp("timestamp") ?: return@forEach
                        if (docTime < listenerStartTime) return@forEach
                        val performedBy = doc.getString("performedBy")?.lowercase() ?: ""
                        if (performedBy == currentUserEmail) return@forEach
                        val title = doc.getString("title") ?: return@forEach
                        val message = doc.getString("message") ?: return@forEach
                        showHydrantChangeNotification(context, title, message)
                        doc.reference.update("seen", true)
                    }
                }
            }
        onDispose { listener.remove() }
    }

    // Fetch fire incident report counts from Firebase
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val userEmail = currentUser.email ?: return@LaunchedEffect

            // ✅ FIRST: Check if user is admin and get their role
            db.collection("admins")
                .document("chief_admin")
                .collection("all")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { chiefDoc ->
                    if (chiefDoc.exists()) {
                        // Chief Admin - sees ALL reports
                        db.collection("fire_incident_reports")
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
                    } else {
                        // Check municipality_admin
                        val tasks = lagunaMunicipalities.map { mun ->
                            db.collection("admins")
                                .document("municipality_admin")
                                .collection(mun)
                                .document(userEmail.lowercase())
                                .get()
                        }
                        com.google.android.gms.tasks.Tasks.whenAllSuccess<com.google.firebase.firestore.DocumentSnapshot>(tasks)
                            .addOnSuccessListener { results ->
                                val munDoc = results.firstOrNull { it.exists() }
                                if (munDoc != null) {
                                    // Municipality Admin - sees only their municipality
                                    val adminMunicipality = munDoc.getString("municipality")
                                    db.collection("fire_incident_reports")
                                        .whereEqualTo("municipality", adminMunicipality)
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
                                } else {
                                    // Regular user - sees only own reports
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
                            }
                            .addOnFailureListener {
                                pendingCount = 0
                                acknowledgedCount = 0
                            }
                    }
                }
                .addOnFailureListener {
                    pendingCount = 0
                    acknowledgedCount = 0
                }
        } else {
            pendingCount = 0
            acknowledgedCount = 0
        }
    }

    // Memoized — only re-filters when allHydrants actually changes, not on every recomposition
    val hydrantsWithValidCoords = remember(hydrantUiState.allHydrants) {
        hydrantUiState.allHydrants.filter { hydrant ->
            val lat = hydrant.latitude.toDoubleOrNull()
            val lng = hydrant.longitude.toDoubleOrNull()
            lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0 && !(lat == 0.0 && lng == 0.0)
        }
    }

    // Get unique municipalities for filter
    val availableMunicipalities = remember(hydrantsWithValidCoords) {
        hydrantsWithValidCoords.map { it.municipality }.distinct().sorted()
    }

    // FIXED: Debounced search — waits 150ms after last keystroke before filtering, runs off main thread
    LaunchedEffect(searchQuery, hydrantsWithValidCoords, selectedMunicipality, isSearchOpen) {
        displayLimit = 50
        if (searchQuery.isNotBlank()) kotlinx.coroutines.delay(150)
        val (suggestions, count) = withContext(Dispatchers.Default) {
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
                allMatches.take(displayLimit) to allMatches.size
            } else if (selectedMunicipality != null) {
                filteredByMunicipality.take(displayLimit) to filteredByMunicipality.size
            } else {
                hydrantsWithValidCoords.take(displayLimit) to hydrantsWithValidCoords.size
            }
        }
        searchSuggestions = suggestions
        totalFilteredCount = count
    }

    LaunchedEffect(displayLimit) {
        if (displayLimit == 50) return@LaunchedEffect
        val (suggestions, count) = withContext(Dispatchers.Default) {
            val filteredByMunicipality = if (selectedMunicipality != null) {
                hydrantsWithValidCoords.filter { it.municipality == selectedMunicipality }
            } else {
                hydrantsWithValidCoords
            }
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase().trim()
                val allMatches = filteredByMunicipality.filter { hydrant ->
                    hydrant.hydrantName.lowercase().contains(query) ||
                            hydrant.exactLocation.lowercase().contains(query) ||
                            hydrant.municipality.lowercase().contains(query)
                }
                allMatches.take(displayLimit) to allMatches.size
            } else if (selectedMunicipality != null) {
                filteredByMunicipality.take(displayLimit) to filteredByMunicipality.size
            } else {
                hydrantsWithValidCoords.take(displayLimit) to hydrantsWithValidCoords.size
            }
        }
        searchSuggestions = suggestions
        totalFilteredCount = count
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
    var orangeMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var fireIncidentMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var markersReady by remember { mutableStateOf(false) }
    var firetruckMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }

    // ✅ Build marker bitmaps on background thread — map appears without waiting
    LaunchedEffect(Unit) {
        try {
            val centerX = 24f
            val topRadius = 14f
            val bottomY = 44f

            val bitmapList = withContext(Dispatchers.Default) {
                val greenBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
                val greenCanvas = android.graphics.Canvas(greenBitmap)
                val greenPaint = android.graphics.Paint().apply { isAntiAlias = true; color = android.graphics.Color.parseColor("#4CAF50"); style = android.graphics.Paint.Style.FILL }
                greenCanvas.drawCircle(centerX, topRadius + 4f, topRadius, greenPaint)
                val greenTriangle = android.graphics.Path()
                greenTriangle.moveTo(centerX, bottomY); greenTriangle.lineTo(centerX - 12f, topRadius + 12f); greenTriangle.lineTo(centerX + 12f, topRadius + 12f); greenTriangle.close()
                greenCanvas.drawPath(greenTriangle, greenPaint)
                greenPaint.color = android.graphics.Color.WHITE
                greenCanvas.drawCircle(centerX, topRadius + 4f, 6f, greenPaint)

                val redBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
                val redCanvas = android.graphics.Canvas(redBitmap)
                val redPaint = android.graphics.Paint().apply { isAntiAlias = true; color = android.graphics.Color.parseColor("#EF5350"); style = android.graphics.Paint.Style.FILL }
                redCanvas.drawCircle(centerX, topRadius + 4f, topRadius, redPaint)
                val redTriangle = android.graphics.Path()
                redTriangle.moveTo(centerX, bottomY); redTriangle.lineTo(centerX - 12f, topRadius + 12f); redTriangle.lineTo(centerX + 12f, topRadius + 12f); redTriangle.close()
                redCanvas.drawPath(redTriangle, redPaint)
                redPaint.color = android.graphics.Color.WHITE
                redCanvas.drawCircle(centerX, topRadius + 4f, 6f, redPaint)

                val blueBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
                val blueCanvas = android.graphics.Canvas(blueBitmap)
                val bluePaint = android.graphics.Paint().apply { isAntiAlias = true; color = android.graphics.Color.parseColor("#2196F3"); style = android.graphics.Paint.Style.FILL }
                blueCanvas.drawCircle(centerX, topRadius + 4f, topRadius, bluePaint)
                val blueTriangle = android.graphics.Path()
                blueTriangle.moveTo(centerX, bottomY); blueTriangle.lineTo(centerX - 12f, topRadius + 12f); blueTriangle.lineTo(centerX + 12f, topRadius + 12f); blueTriangle.close()
                blueCanvas.drawPath(blueTriangle, bluePaint)
                bluePaint.color = android.graphics.Color.WHITE
                blueCanvas.drawCircle(centerX, topRadius + 4f, 6f, bluePaint)

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

                // Orange bitmap for "Occupied" hydrants
                val orangeBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
                val orangeCanvas = android.graphics.Canvas(orangeBitmap)
                val orangePaint = android.graphics.Paint().apply { isAntiAlias = true; color = android.graphics.Color.parseColor("#FF9800"); style = android.graphics.Paint.Style.FILL }
                orangeCanvas.drawCircle(centerX, topRadius + 4f, topRadius, orangePaint)
                val orangeTriangle = android.graphics.Path()
                orangeTriangle.moveTo(centerX, bottomY); orangeTriangle.lineTo(centerX - 12f, topRadius + 12f); orangeTriangle.lineTo(centerX + 12f, topRadius + 12f); orangeTriangle.close()
                orangeCanvas.drawPath(orangeTriangle, orangePaint)
                orangePaint.color = android.graphics.Color.WHITE
                orangeCanvas.drawCircle(centerX, topRadius + 4f, 6f, orangePaint)

                listOf(greenBitmap, redBitmap, blueBitmap, fireBitmap, orangeBitmap)
            }

            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmapList[0])
            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmapList[1])
            blueMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmapList[2])
            fireIncidentMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmapList[3])
            orangeMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmapList[4])
            markersReady = true
        } catch (e: Exception) {
            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN)
            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED)
            blueMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE)
            fireIncidentMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE)
            orangeMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW)
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

    // Handle Navigate triggered from ViewHydrantLocationScreen
    LaunchedEffect(initialHydrantForNavigate) {
        initialHydrantForNavigate?.let { hydrant ->
            val hydrantLat = hydrant.latitude.toDoubleOrNull()
            val hydrantLng = hydrant.longitude.toDoubleOrNull()
            if (hydrantLat != null && hydrantLng != null) {
                selectedHydrantForCard = hydrant
                showHydrantDetailsCard = true
                isCardVisible = true
                val hydrantLatLng = LatLng(hydrantLat, hydrantLng)
                scope.launch {
                    kotlinx.coroutines.delay(300)
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(hydrantLatLng, 17f), durationMs = 800)
                }
                // Trigger navigate
                kotlinx.coroutines.delay(500)
                triggerNavigate = true
            }
            onHydrantNavigateHandled()
        }
    }

    // Handle Directions triggered from ViewHydrantLocationScreen
    LaunchedEffect(initialHydrantForDirections) {
        initialHydrantForDirections?.let { hydrant ->
            val hydrantLat = hydrant.latitude.toDoubleOrNull()
            val hydrantLng = hydrant.longitude.toDoubleOrNull()
            if (hydrantLat != null && hydrantLng != null) {
                selectedHydrantForCard = hydrant
                showHydrantDetailsCard = true
                isCardVisible = true
                val hydrantLatLng = LatLng(hydrantLat, hydrantLng)
                scope.launch {
                    kotlinx.coroutines.delay(300)
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(hydrantLatLng, 17f), durationMs = 800)
                }
                // Trigger directions
                kotlinx.coroutines.delay(500)
                triggerDirections = true
            }
            onHydrantDirectionsHandled()
        }
    }

    // Auto-advance navigation steps + Google Maps-style voice announcements
    LaunchedEffect(isNavigating, userCurrentLocation) {
        if (!isNavigating || directionsResult == null || userCurrentLocation == null) return@LaunchedEffect

        val steps = directionsResult!!.steps
        if (steps.isEmpty()) return@LaunchedEffect

        val currentStep = steps.getOrNull(currentStepIndex) ?: return@LaunchedEffect
        val lat1 = userCurrentLocation!!.latitude
        val lon1 = userCurrentLocation!!.longitude

        // Distance to the END of current step (= start of next step)
        val nextIndex = currentStepIndex + 1
        if (nextIndex < steps.size) {
            val nextStep = steps[nextIndex]
            val lat2 = nextStep.startLocation.latitude
            val lon2 = nextStep.startLocation.longitude
            val r = 6371
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val distanceToNext = (r * c) * 1000 // meters

            val cleanInstruction = currentStep.instruction.replace(Regex("<[^>]*>"), "")
            val activeTts = tts

            // ── Google Maps-style 3-announcement system ──────────────────
            // 1) "In 300 meters, turn right onto X"  (when 250–350m away)
            if (distanceToNext in 250.0..350.0 && !hasSpoken300m && activeTts != null) {
                hasSpoken300m = true
                activeTts.speak(
                    "In 300 meters, $cleanInstruction",
                    android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "nav_300"
                )
            }

            // 2) "Turn right onto X"  (when within 50m — do it NOW)
            if (distanceToNext < 50.0 && !hasSpokenNow && activeTts != null) {
                hasSpokenNow = true
                activeTts.speak(
                    cleanInstruction,
                    android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "nav_now"
                )
            }

            // 3) Auto-advance step when within 30 meters
            if (distanceToNext < 30) {
                currentStepIndex = nextIndex
                hasSpoken300m = false
                hasSpokenNow = false
                lastSpokenStepIndex = nextIndex

                // Re-center camera on user during navigation
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(userCurrentLocation!!, 17f),
                    durationMs = 500
                )
            }
        }
    }

    LaunchedEffect(isNavigating) {
        if (!isNavigating) return@LaunchedEffect
        while (isNavigating) {
            if (checkLocationPermission(context)) {
                getCurrentLocation(context) { location ->
                    userCurrentLocation = LatLng(location.latitude, location.longitude)
                }
            }
            kotlinx.coroutines.delay(3000) // Update every 3 seconds
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

    val drawerWidthPx = with(density) { 280.dp.toPx() }
    val drawerOffsetPx = remember { Animatable(-drawerWidthPx) }
    LaunchedEffect(isDrawerOpen) {
        drawerOffsetPx.animateTo(
            targetValue = if (isDrawerOpen) 0f else -drawerWidthPx,
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
        )
    }
    val scrimAlpha = if (isDrawerOpen) 0.5f else 0f

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
        val results = hydrants.mapNotNull { hydrant ->
            val lat = hydrant.latitude.toDoubleOrNull() ?: return@mapNotNull null
            val lng = hydrant.longitude.toDoubleOrNull() ?: return@mapNotNull null
            if (lat < -90 || lat > 90 || lng < -180 || lng > 180 || (lat == 0.0 && lng == 0.0)) return@mapNotNull null
            val distance = calculateDistance(userLat, userLng, lat, lng)
            hydrant to distance
        }.sortedBy { it.second }

        nearestHydrants = results.take(3)
        return if (results.isNotEmpty()) Pair(results.first().first, results.first().second) else Pair(null, null)
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

        // Close the pin-click hydrant details card if it's open
        showHydrantDetailsCard = false
        selectedHydrantForCard = null
        selectedHydrantMarker = null

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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.emergency_alerts),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        colorFilter = ColorFilter.tint(Color(0xFFD32F2F))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Report a Fire Incident", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                }
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
    if (showReportFireDialog) {
        var incidentLocation by remember { mutableStateOf("") }
        var currentLocation by remember { mutableStateOf("") }
        var incidentDescription by remember { mutableStateOf("") }
        var isSubmitting by remember { mutableStateOf(false) }
        var isGettingLocation by remember { mutableStateOf(false) }
        var reporterName by remember { mutableStateOf("") }
        var reporterContact by remember { mutableStateOf("") }
        var locationError by remember { mutableStateOf(false) }
        var locationDenied by remember { mutableStateOf(false) }
        var detectedMunicipality by remember { mutableStateOf<String?>(null) }
        var currentLatLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }
        var triggerLocationFetch by remember { mutableStateOf(false) }

        val locationSettingsLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                triggerLocationFetch = true
            } else {
                // "No thanks" pressed — close fire report and go back to emergency dialog
                isGettingLocation = false
                showReportFireDialog = false
                showEmergencyDialog = true
            }
        }

        // Fetch location when triggered after user enables location from dialog
        LaunchedEffect(triggerLocationFetch) {
            if (triggerLocationFetch) {
                triggerLocationFetch = false
                isGettingLocation = true
                locationError = false
                locationDenied = false
                getCurrentLocationWithTimeout(
                    context = context,
                    timeoutMs = 10000L,
                    onSuccess = { location ->
                        currentLocation = "Lat: %.6f, Lng: %.6f"
                            .format(location.latitude, location.longitude)
                        isGettingLocation = false
                    },
                    onTimeout = {
                        isGettingLocation = false
                        locationError = true
                        android.widget.Toast.makeText(
                            context,
                            "Location timed out. Please try again.",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }

        LaunchedEffect(Unit) {
            reporterName = FirebaseAuth.getInstance().currentUser?.displayName ?: ""

            if (checkLocationPermission(context)) {
                val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                    priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)

                val settingsClient = com.google.android.gms.location.LocationServices.getSettingsClient(context)
                val task = settingsClient.checkLocationSettings(builder.build())

                task.addOnSuccessListener {
                    isGettingLocation = true
                    locationError = false
                    getCurrentLocationWithTimeout(
                        context = context,
                        timeoutMs = 10000L,
                        onSuccess = { location ->
                            currentLocation = "Lat: %.6f, Lng: %.6f"
                                .format(location.latitude, location.longitude)
                            isGettingLocation = false
                        },
                        onTimeout = {
                            isGettingLocation = false
                            locationError = true
                        }
                    )
                }

                task.addOnFailureListener { exception ->
                    if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                        try {
                            val intentSenderRequest = androidx.activity.result.IntentSenderRequest.Builder(
                                exception.resolution.intentSender
                            ).build()
                            locationSettingsLauncher.launch(intentSenderRequest)
                        } catch (sendEx: android.content.IntentSender.SendIntentException) {
                            locationError = true
                            isGettingLocation = false
                        }
                    } else {
                        locationError = true
                        isGettingLocation = false
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
        }

        // Auto-detect municipality from location
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
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.emergency_alerts),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(Color(0xFFD32F2F))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Report Fire Incident", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                }
            },
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

                    // Contact Number
                    OutlinedTextField(
                        value = reporterContact,
                        onValueChange = { newValue ->
                            val digitsOnly = newValue.filter { it.isDigit() }
                            if (digitsOnly.length <= 10) reporterContact = digitsOnly
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

                    // Current Location Section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Your Current Location", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        Text(" *", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }

                    if (isGettingLocation) {
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(Modifier.size(20.dp), color = Color(0xFF1976D2), strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Getting your location...", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1976D2))
                            }
                        }
                    }

                    if (currentLocation.isNotEmpty()) {
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📍", fontSize = 20.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("Your Location:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                        Text(currentLocation, style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
                                        if (detectedMunicipality != null) {
                                            Spacer(Modifier.height(4.dp))
                                            Surface(color = Color(0xFF4CAF50), shape = RoundedCornerShape(4.dp)) {
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
                                }
                            }
                        }
                    } else if (locationDenied) {
                        // Show this when user pressed "No thanks" on the location dialog
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🚫", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Location access is required", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                    Text("You must enable location to submit a fire report", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                                }
                            }
                        }
                    } else if (locationError) {
                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
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
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.warning),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("For immediate emergencies, please call 911 or 160 directly!", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            reporterName.isBlank() -> android.widget.Toast.makeText(context, "Please enter your name", android.widget.Toast.LENGTH_SHORT).show()
                            reporterContact.isBlank() -> android.widget.Toast.makeText(context, "Please enter your contact number", android.widget.Toast.LENGTH_SHORT).show()
                            reporterContact.length != 10 -> android.widget.Toast.makeText(context, "Contact number must be 10 digits", android.widget.Toast.LENGTH_SHORT).show()
                            locationDenied -> android.widget.Toast.makeText(context, "Location access is required to submit a report", android.widget.Toast.LENGTH_LONG).show()
                            currentLocation.isBlank() -> {
                                locationError = true
                                android.widget.Toast.makeText(context, "Please enable location to submit a fire report", android.widget.Toast.LENGTH_SHORT).show()
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
                                    municipality = detectedMunicipality!!,
                                    onSuccess = {
                                        isSubmitting = false
                                        showReportFireDialog = false
                                        android.widget.Toast.makeText(context, "Fire incident reported successfully!", android.widget.Toast.LENGTH_LONG).show()
                                    },
                                    onFailure = { error ->
                                        isSubmitting = false
                                        android.widget.Toast.makeText(context, "Failed: $error", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        }
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Submitting...")
                    } else {
                        Text("Submit Report")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportFireDialog = false }, enabled = !isSubmitting) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    // Occupy / Release confirmation dialog
    if (showOccupyConfirmDialog && selectedHydrantForCard != null) {
        val hydrant = selectedHydrantForCard!!
        val isOccupied = hydrant.serviceStatus == "Occupied"
        AlertDialog(
            onDismissRequest = { showOccupyConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning, null,
                        tint = if (isOccupied) Color(0xFF757575) else Color(0xFFFF9800),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isOccupied) "Release Hydrant" else "Occupy Hydrant",
                        fontWeight = FontWeight.Bold,
                        color = if (isOccupied) Color(0xFF757575) else Color(0xFFFF9800)
                    )
                }
            },
            text = {
                Text(
                    if (isOccupied)
                        "Mark \"${hydrant.hydrantName}\" as In Service (released)?"
                    else
                        "Mark \"${hydrant.hydrantName}\" as Occupied? This hydrant will be shown as in use on the map."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOccupyConfirmDialog = false
                        val newStatus = if (isOccupied) "In Service" else "Occupied"
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val performedBy = currentUser?.displayName?.takeIf { it.isNotBlank() }
                            ?: currentUser?.email ?: "Unknown"
                        val rtdb = com.google.firebase.database.FirebaseDatabase
                            .getInstance("https://firegrid-58bc3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        val sdf = java.text.SimpleDateFormat(
                            "MMMM dd, yyyy 'at' h:mm:ss a 'UTC+8'", java.util.Locale.ENGLISH
                        )
                        sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Manila")
                        val timestamp = sdf.format(java.util.Date())
                        rtdb.getReference("fire_hydrants")
                            .child(hydrant.municipality)
                            .child(hydrant.id)
                            .updateChildren(
                                if (newStatus == "Occupied") {
                                    mapOf(
                                        "ServiceStatus" to newStatus,
                                        "LastUpdated" to timestamp,
                                        "OccupiedBy" to performedBy,
                                        "OccupiedAt" to timestamp
                                    )
                                } else {
                                    mapOf(
                                        "ServiceStatus" to newStatus,
                                        "LastUpdated" to timestamp,
                                        "OccupiedBy" to "",
                                        "OccupiedAt" to ""
                                    )
                                }
                            )
                            .addOnSuccessListener {
                                logHydrantChange(
                                    context = context,
                                    changeType = "updated",
                                    hydrantId = hydrant.id,
                                    hydrantName = hydrant.hydrantName,
                                    exactLocation = hydrant.exactLocation,
                                    latitude = hydrant.latitude,
                                    longitude = hydrant.longitude,
                                    typeColor = hydrant.typeColor,
                                    newStatus = newStatus,
                                    remarks = hydrant.remarks,
                                    municipality = hydrant.municipality,
                                    performedBy = performedBy,
                                    changedFields = mapOf("ServiceStatus" to Pair(hydrant.serviceStatus, newStatus))
                                )

                                if (newStatus == "Occupied") {
                                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                                    val notifId = (hydrant.id + hydrant.municipality).hashCode()

                                    val scheduleIntent = Intent(context, HydrantOccupiedReceiver::class.java).apply {
                                        action = "ACTION_FIRE_NOTIFICATION"
                                        putExtra("hydrantId", hydrant.id)
                                        putExtra("hydrantName", hydrant.hydrantName)
                                        putExtra("municipality", hydrant.municipality)
                                        putExtra("occupiedBy", performedBy)
                                        putExtra("notifId", notifId)
                                    }
                                    val pi = PendingIntent.getBroadcast(
                                        context, notifId + 10, scheduleIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                    )
                                    val triggerAt = System.currentTimeMillis() + 60 * 60 * 1000L // 1 hour
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
                                    } else {
                                        alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
                                    }

                                    val autoReleaseIntent = Intent(context, HydrantOccupiedReceiver::class.java).apply {
                                        action = "ACTION_AUTO_RELEASE"
                                        putExtra("hydrantId", hydrant.id)
                                        putExtra("hydrantName", hydrant.hydrantName)
                                        putExtra("municipality", hydrant.municipality)
                                        putExtra("notifId", notifId)
                                    }
                                    val autoPi = PendingIntent.getBroadcast(
                                        context, notifId + 20, autoReleaseIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                    )
                                    val autoTriggerAt = System.currentTimeMillis() + (1 + 10) * 60 * 1000L // 11 minutes
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, autoTriggerAt, autoPi)
                                    } else {
                                        alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, autoTriggerAt, autoPi)
                                    }
                                }

                                selectedHydrantForCard = hydrant.copy(serviceStatus = newStatus)
                                hydrantViewModel.updateLocalHydrantStatus(hydrant.id, newStatus)
                                Toast.makeText(
                                    context,
                                    if (newStatus == "Occupied") "🟠 Hydrant marked as Occupied"
                                    else "🟢 Hydrant released (In Service)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                            }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOccupied) Color(0xFF757575) else Color(0xFFFF9800)
                    )
                ) {
                    Text(if (isOccupied) "Release" else "Occupy", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOccupyConfirmDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Stable objects — only recreate MapProperties when isMyLocationEnabled actually changes
        val mapProperties = remember(isMyLocationEnabled) {
            MapProperties(isMyLocationEnabled = isMyLocationEnabled)
        }
        val mapUiSettings = remember {
            MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = false
            )
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
        ) {
            if (markersReady && greenMarkerIcon != null && redMarkerIcon != null) {
                // Pre-compute icon per hydrant — composite key avoids id collisions across municipalities
                val markerIconMap = remember(nearestHydrant, greenMarkerIcon, redMarkerIcon, blueMarkerIcon, orangeMarkerIcon, hydrantUiState.allHydrants) {
                    hydrantsWithValidCoords.associate { h ->
                        val isNearest = nearestHydrant?.let { it.id == h.id && it.municipality == h.municipality } ?: false
                        "${h.id}_${h.municipality}" to when {
                            isNearest -> blueMarkerIcon!!
                            h.serviceStatus == "In Service" -> greenMarkerIcon!!
                            h.serviceStatus == "Occupied" -> orangeMarkerIcon ?: greenMarkerIcon!!
                            else -> redMarkerIcon!!
                        }
                    }
                }

                hydrantsWithValidCoords.forEach { hydrant ->
                    val lat = hydrant.latitude.toDoubleOrNull() ?: return@forEach
                    val lng = hydrant.longitude.toDoubleOrNull() ?: return@forEach
                    val markerIcon = markerIconMap["${hydrant.id}_${hydrant.municipality}"]
                        ?: when {
                            hydrant.serviceStatus == "In Service" -> greenMarkerIcon!!
                            hydrant.serviceStatus == "Occupied" -> orangeMarkerIcon ?: greenMarkerIcon!!
                            else -> redMarkerIcon!!
                        }
                    val markerState = remember(hydrant.id, hydrant.municipality) { com.google.maps.android.compose.MarkerState(position = LatLng(lat, lng)) }
                    com.google.maps.android.compose.Marker(
                        state = markerState,
                        title = "${hydrant.municipality} - ${hydrant.hydrantName}",
                        icon = markerIcon,
                        onClick = { marker ->
                            // Close search hydrant card if open
                            if (selectedSearchHydrant != null) {
                                selectedSearchHydrant = null
                                nearestHydrant = null
                                directionsResult = null
                                selectedSearchHydrantDistance = null
                            }

                            val isNearestOrSearchHydrantShowing = nearestHydrant != null || selectedSearchHydrant != null
                            val isClickingSameHydrant = (nearestHydrant?.id == hydrant.id && nearestHydrant?.municipality == hydrant.municipality) ||
                                    (selectedSearchHydrant?.id == hydrant.id && selectedSearchHydrant?.municipality == hydrant.municipality)

                            if (isNearestOrSearchHydrantShowing && isClickingSameHydrant) {
                                // If clicking the SAME hydrant that's showing in the card, ONLY show info window (no card)
                                selectedHydrantMarker = hydrant
                                selectedHydrantForCard = null
                                showHydrantDetailsCard = false
                                isCardVisible = false
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
                                isCardVisible = true
                            } else {
                                // Normal behavior - no cards showing, so show both info window and card
                                selectedHydrantMarker = hydrant
                                selectedHydrantForCard = hydrant
                                showHydrantDetailsCard = true
                                isCardVisible = true
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
                    com.google.maps.android.compose.Marker(
                        state = com.google.maps.android.compose.MarkerState(position = incidentMarkerLocation!!),
                        title = "🔥 FIRE INCIDENT",
                        snippet = "Reporter's Location",
                        icon = fireIncidentMarkerIcon
                    )
                }
            }

            // Directions polyline
            directionsResult?.let { result ->
                val displayPoints = remember(result.polylinePoints, userCurrentLocation) {
                    if (userCurrentLocation == null) {
                        result.polylinePoints
                    } else {
                        // Find the closest point on the polyline to the user's current location
                        var closestIndex = 0
                        var minDistance = Double.MAX_VALUE

                        result.polylinePoints.forEachIndexed { index, point ->
                            val r = 6371
                            val dLat = Math.toRadians(point.latitude - userCurrentLocation!!.latitude)
                            val dLon = Math.toRadians(point.longitude - userCurrentLocation!!.longitude)
                            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                    Math.cos(Math.toRadians(userCurrentLocation!!.latitude)) *
                                    Math.cos(Math.toRadians(point.latitude)) *
                                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
                            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                            val distance = r * c * 1000 // meters

                            if (distance < minDistance) {
                                minDistance = distance
                                closestIndex = index
                            }
                        }

                        // Return only points from closest index onward
                        result.polylinePoints.drop(closestIndex)
                    }
                }

                // Remaining route (blue)
                Polyline(
                    points = displayPoints,
                    color = Color(0xFF2196F3),
                    width = 12f
                )

                // Already traveled portion (gray) - optional, shows where you've been
                if (userCurrentLocation != null && displayPoints.size < result.polylinePoints.size) {
                    val traveledPoints = result.polylinePoints.take(
                        result.polylinePoints.size - displayPoints.size + 1
                    )
                    Polyline(
                        points = traveledPoints,
                        color = Color(0xFFBDBDBD),
                        width = 8f
                    )
                }
            }

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

                    val municipalityCounts = remember(hydrantsWithValidCoords) {
                        availableMunicipalities.associateWith { m -> hydrantsWithValidCoords.count { it.municipality == m } }
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

                        // Dropdown Menu — no animation, instant open
                        if (showMunicipalityDropdown) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp),
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 4.dp,
                                color = Color.White
                            ) {
                                LazyColumn {
                                    item {
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Image(
                                                        painter = painterResource(id = R.drawable.laguna_logo),
                                                        contentDescription = "Laguna logo",
                                                        modifier = Modifier.size(28.dp).clip(CircleShape),
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
                                    }
                                    items(availableMunicipalities) { municipality ->
                                        val count = municipalityCounts[municipality] ?: 0
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Image(
                                                        painter = painterResource(id = getMunicipalityLogo(municipality)),
                                                        contentDescription = "$municipality logo",
                                                        modifier = Modifier.size(28.dp).clip(CircleShape),
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
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // Search Suggestions
                    if (searchQuery.isNotEmpty() || selectedMunicipality != null || searchSuggestions.isNotEmpty()) {
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

                                // Show "showing X of Y" + Load More button
                                if (totalFilteredCount > searchSuggestions.size) {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "Showing ${searchSuggestions.size} of $totalFilteredCount results",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                            Spacer(Modifier.height(8.dp))
                                            OutlinedButton(
                                                onClick = { displayLimit += 50 },
                                                shape = RoundedCornerShape(50),
                                                border = BorderStroke(1.dp, Color(0xFFFF6B35)),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = Color(0xFFFF6B35)
                                                )
                                            ) {
                                                Text("Load More")
                                            }
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

        // Fire Incident Info Card (Top)
        if (showIncidentInfoCard && incidentMarkerLocation != null && !isSearchOpen) {
            var isAdmin by remember { mutableStateOf(false) }
            var isCheckingAdmin by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                isCheckingAdmin = true
                val auth = FirebaseAuth.getInstance()
                val currentUserEmail = auth.currentUser?.email
                if (currentUserEmail != null) {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("admins")
                        .document(currentUserEmail.lowercase())
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val adminStatus = document.getBoolean("isAdmin")
                                isAdmin = adminStatus == true
                            } else {
                                isAdmin = false
                            }
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

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
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
                                hydrantDirectionsFetched = false
                                fireDirectionsFetched = false
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
                                            else nearestHydrantDistance?.let {
                                                if (it < 1) "%.0f meters".format(it * 1000) else "%.2f km".format(it)
                                            } ?: "Calculating...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF1976D2),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Direction Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isCheckingAdmin) {
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

                            // ── TO HYDRANT / NAVIGATE TO HYDRANT BUTTON ──
                            if (nearestHydrant != null) {
                                Button(
                                    onClick = {
                                        if (hydrantDirectionsFetched && directionsResult != null) {
                                            // Already have directions → start navigation
                                            navigationHydrant = nearestHydrant
                                            currentStepIndex = 0
                                            isNavigating = true
                                            lastSpokenStepIndex = -1
                                            hasSpoken300m = false
                                            hasSpokenNow = false
                                            showIncidentInfoCard = false
                                            scope.launch {
                                                userCurrentLocation?.let { loc ->
                                                    val bearing9 = run {
                                                        val s1 = directionsResult?.steps?.firstOrNull()?.startLocation
                                                        val s2 = directionsResult?.steps?.getOrNull(1)?.startLocation
                                                        if (s1 != null && s2 != null) {
                                                            val lat1 = Math.toRadians(s1.latitude); val lat2 = Math.toRadians(s2.latitude)
                                                            val dLng = Math.toRadians(s2.longitude - s1.longitude)
                                                            ((Math.toDegrees(Math.atan2(Math.sin(dLng) * Math.cos(lat2), Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng))) + 360) % 360).toFloat()
                                                        } else 0f
                                                    }
                                                    cameraPositionState.animate(
                                                        CameraUpdateFactory.newCameraPosition(
                                                            CameraPosition.Builder().target(loc).zoom(18f).tilt(60f).bearing(bearing9).build()
                                                        ), durationMs = 1000
                                                    )
                                                }
                                            }
                                        } else {
                                            // Fetch directions to hydrant
                                            val hydrantLat = nearestHydrant!!.latitude.toDoubleOrNull()
                                            val hydrantLng = nearestHydrant!!.longitude.toDoubleOrNull()
                                            if (hydrantLat != null && hydrantLng != null) {
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
                                                    onSuccess = {},
                                                    onFailure = {}
                                                )
                                                if (checkLocationPermission(context)) {
                                                    if (locationSettingsOk) {
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
                                                                    hydrantDirectionsFetched = true
                                                                    fireDirectionsFetched = false
                                                                    cameraPositionState.animate(
                                                                        CameraUpdateFactory.newLatLngZoom(
                                                                            LatLng(hydrantLat, hydrantLng), 15f
                                                                        )
                                                                    )
                                                                } else {
                                                                    android.widget.Toast.makeText(context, "Could not fetch directions", android.widget.Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        cachedSettingsClient.checkLocationSettings(cachedLocationSettingsRequest)
                                                            .addOnSuccessListener {
                                                                locationSettingsOk = true
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
                                                                            hydrantDirectionsFetched = true
                                                                            fireDirectionsFetched = false
                                                                            cameraPositionState.animate(
                                                                                CameraUpdateFactory.newLatLngZoom(
                                                                                    LatLng(hydrantLat, hydrantLng), 15f
                                                                                )
                                                                            )
                                                                        } else {
                                                                            android.widget.Toast.makeText(context, "Could not fetch directions", android.widget.Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            .addOnFailureListener { exception ->
                                                                if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                                                                    try {
                                                                        pendingLocationAction = "directions"
                                                                        mapLocationSettingsLauncher.launch(
                                                                            androidx.activity.result.IntentSenderRequest.Builder(
                                                                                exception.resolution.intentSender
                                                                            ).build()
                                                                        )
                                                                    } catch (e: android.content.IntentSender.SendIntentException) {
                                                                        android.widget.Toast.makeText(context, "Unable to enable location", android.widget.Toast.LENGTH_SHORT).show()
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
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (hydrantDirectionsFetched) Color(0xFF1A73E8) else Color(0xFF4CAF50)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    enabled = !isLoadingDirections
                                ) {
                                    if (isLoadingDirections && !fireDirectionsFetched) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else if (hydrantDirectionsFetched) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.icon_navigation),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.White
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
                                        if (hydrantDirectionsFetched) "Navigate" else "To Hydrant",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            // ── TO FIRE / NAVIGATE TO FIRE BUTTON ──
                            Button(
                                onClick = {
                                    if (fireDirectionsFetched && directionsResult != null) {
                                        // Already have directions → start navigation to fire
                                        currentStepIndex = 0
                                        isNavigating = true
                                        lastSpokenStepIndex = -1
                                        hasSpoken300m = false
                                        hasSpokenNow = false
                                        showIncidentInfoCard = false
                                        scope.launch {
                                            userCurrentLocation?.let { loc ->
                                                val bearing10 = run {
                                                    val s1 = directionsResult?.steps?.firstOrNull()?.startLocation
                                                    val s2 = directionsResult?.steps?.getOrNull(1)?.startLocation
                                                    if (s1 != null && s2 != null) {
                                                        val lat1 = Math.toRadians(s1.latitude); val lat2 = Math.toRadians(s2.latitude)
                                                        val dLng = Math.toRadians(s2.longitude - s1.longitude)
                                                        ((Math.toDegrees(Math.atan2(Math.sin(dLng) * Math.cos(lat2), Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng))) + 360) % 360).toFloat()
                                                    } else 0f
                                                }
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newCameraPosition(
                                                        CameraPosition.Builder().target(loc).zoom(18f).tilt(60f).bearing(bearing10).build()
                                                    ), durationMs = 1000
                                                )
                                            }
                                        }
                                    } else {
                                        // Fetch directions to fire location
                                        if (checkLocationPermission(context)) {
                                            if (locationSettingsOk) {
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
                                                            fireDirectionsFetched = true
                                                            hydrantDirectionsFetched = false
                                                            cameraPositionState.animate(
                                                                CameraUpdateFactory.newLatLngZoom(incidentMarkerLocation!!, 15f)
                                                            )
                                                        } else {
                                                            android.widget.Toast.makeText(context, "Could not fetch directions", android.widget.Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            } else {
                                                cachedSettingsClient.checkLocationSettings(cachedLocationSettingsRequest)
                                                    .addOnSuccessListener {
                                                        locationSettingsOk = true
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
                                                                    fireDirectionsFetched = true
                                                                    hydrantDirectionsFetched = false
                                                                    cameraPositionState.animate(
                                                                        CameraUpdateFactory.newLatLngZoom(incidentMarkerLocation!!, 15f)
                                                                    )
                                                                } else {
                                                                    android.widget.Toast.makeText(context, "Could not fetch directions", android.widget.Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        }
                                                    }
                                                    .addOnFailureListener { exception ->
                                                        if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                                                            try {
                                                                pendingLocationAction = "directions"
                                                                mapLocationSettingsLauncher.launch(
                                                                    androidx.activity.result.IntentSenderRequest.Builder(
                                                                        exception.resolution.intentSender
                                                                    ).build()
                                                                )
                                                            } catch (e: android.content.IntentSender.SendIntentException) {
                                                                android.widget.Toast.makeText(context, "Unable to enable location", android.widget.Toast.LENGTH_SHORT).show()
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
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (fireDirectionsFetched) Color(0xFF1A73E8) else Color(0xFFD32F2F)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                enabled = !isLoadingDirections
                            ) {
                                if (isLoadingDirections && !hydrantDirectionsFetched) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else if (fireDirectionsFetched) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_navigation),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
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
                                    if (fireDirectionsFetched) "Navigate" else "To Fire",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }

                        } else {
                            // REGULAR USER BUTTON: Show Firefighter Locations
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
        if (selectedHydrantForCard != null && !isSearchOpen) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .graphicsLayer { translationY = cardTranslationY },
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
                        verticalAlignment = Alignment.CenterVertically  // Changed from Top to CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween  // This pushes items to opposite ends
                        ) {
                            Text(
                                text = selectedHydrantForCard!!.hydrantName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )

                            Surface(
                                color = when (selectedHydrantForCard!!.serviceStatus) {
                                    "In Service" -> Color(0xFF4CAF50)
                                    "Occupied"   -> Color(0xFFFF9800)
                                    else         -> Color(0xFFEF5350)
                                },
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
                                isCardVisible = false
                                showHydrantDetailsCard = false
                                selectedHydrantMarker = null
                                scope.launch {
                                    kotlinx.coroutines.delay(320)
                                    selectedHydrantForCard = null
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

// Two buttons in a row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Navigation Button (Left)
                        Button(
                            onClick = {
                                if (checkLocationPermission(context)) {
                                    if (locationSettingsOk) {
                                        triggerNavigate = true
                                    } else {
                                        cachedSettingsClient.checkLocationSettings(cachedLocationSettingsRequest)
                                            .addOnSuccessListener {
                                                locationSettingsOk = true
                                                triggerNavigate = true
                                            }
                                            .addOnFailureListener { exception ->
                                                if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                                                    try {
                                                        pendingLocationAction = "navigate"
                                                        mapLocationSettingsLauncher.launch(
                                                            androidx.activity.result.IntentSenderRequest.Builder(
                                                                exception.resolution.intentSender
                                                            ).build()
                                                        )
                                                    } catch (e: android.content.IntentSender.SendIntentException) {
                                                        triggerNavigate = true
                                                    }
                                                } else {
                                                    triggerNavigate = true
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
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_navigation),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Navigate",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }

                        // Directions Button (Right)
                        Button(
                            onClick = {
                                if (checkLocationPermission(context)) {
                                    if (locationSettingsOk) {
                                        triggerDirections = true
                                    } else {
                                        cachedSettingsClient.checkLocationSettings(cachedLocationSettingsRequest)
                                            .addOnSuccessListener {
                                                locationSettingsOk = true
                                                triggerDirections = true
                                            }
                                            .addOnFailureListener { exception ->
                                                if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                                                    try {
                                                        pendingLocationAction = "directions"
                                                        mapLocationSettingsLauncher.launch(
                                                            androidx.activity.result.IntentSenderRequest.Builder(
                                                                exception.resolution.intentSender
                                                            ).build()
                                                        )
                                                    } catch (e: android.content.IntentSender.SendIntentException) {
                                                        triggerDirections = true
                                                    }
                                                } else {
                                                    triggerDirections = true
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
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A73E8)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isLoadingDirections,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            if (isLoadingDirections) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Loading...",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            } else {
                                Icon(
                                    Icons.Default.Directions,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Directions",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Occupy / Release button — admin only
                    if (isCardAdmin && (selectedHydrantForCard!!.serviceStatus == "In Service" || selectedHydrantForCard!!.serviceStatus == "Occupied")) {
                        Spacer(Modifier.height(8.dp))
                        val isOccupied = selectedHydrantForCard!!.serviceStatus == "Occupied"
                        Button(
                            onClick = { showOccupyConfirmDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOccupied) Color(0xFF757575) else Color(0xFFFF9800)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (isOccupied) "Release Hydrant" else "Occupy Hydrant",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
        if (!isSearchOpen && !showIncidentInfoCard && !isNavigating) {
            FloatingActionButton(
                onClick = { isDrawerOpen = true },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 12.dp)
                    .size(40.dp),
                containerColor = Color.White,
                contentColor = Color(0xFF5F6368),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "≡",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF5F6368),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Emergency, Mail and Search Button Row (hide when search is open)
        // After
        if (!isSearchOpen && !showIncidentInfoCard && !isNavigating) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 12.dp),
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
                            if (locationSettingsOk) {
                                triggerGoToMyLocation = true
                            } else {
                                cachedSettingsClient.checkLocationSettings(cachedLocationSettingsRequest)
                                    .addOnSuccessListener {
                                        locationSettingsOk = true
                                        triggerGoToMyLocation = true
                                    }
                                    .addOnFailureListener { exception ->
                                        if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                                            try {
                                                pendingLocationAction = "goToLocation"
                                                mapLocationSettingsLauncher.launch(
                                                    androidx.activity.result.IntentSenderRequest
                                                        .Builder(exception.resolution.intentSender).build()
                                                )
                                            } catch (sendEx: android.content.IntentSender.SendIntentException) {
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "Unable to get current location",
                                                    android.widget.Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        } else {
                                            android.widget.Toast.makeText(
                                                context,
                                                "Unable to get current location",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
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

        if (nearestHydrant != null && !showIncidentInfoCard && !isSearchOpen && !isNavigating) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    // Header Row
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Name + dropdown button as one unit
                        Box(modifier = Modifier.weight(1f)) {
                            if (selectedSearchHydrant != null || directionsResult != null) {
                                // Search selection OR directions from pin: plain non-clickable name, no dropdown
                                Text(
                                    nearestHydrant!!.hydrantName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 0.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                )
                            } else {
                                // Find Nearest: clickable Surface with dropdown arrow
                                Surface(
                                    onClick = { showNearestDropdown = !showNearestDropdown },
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            nearestHydrant!!.hydrantName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF212121),
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.arrow_drop_down),
                                            contentDescription = "Show more",
                                            tint = Color(0xFF5F6368),
                                            modifier = Modifier
                                                .size(24.dp)
                                                .rotate(if (showNearestDropdown) 180f else 0f)
                                        )
                                    }
                                }

                                // Dropdown for 2nd and 3rd nearest
                                DropdownMenu(
                                    expanded = showNearestDropdown && nearestHydrants.size > 1,
                                    onDismissRequest = { showNearestDropdown = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .background(Color.White)
                                ) {
                                    nearestHydrants.forEachIndexed { originalRank, (hydrant, distance) ->
                                        val isCurrentlyDisplayed = hydrant.id == nearestHydrant!!.id &&
                                                hydrant.municipality == nearestHydrant!!.municipality
                                        if (isCurrentlyDisplayed) return@forEachIndexed
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        color = Color(0xFFFF6B35),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            "${originalRank + 1}",
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    Spacer(Modifier.width(10.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            hydrant.hydrantName,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.SemiBold,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            hydrant.exactLocation,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = Color.Gray,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(
                                                        if (distance < 1) "%.0fm".format(distance * 1000)
                                                        else "%.2fkm".format(distance),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color(0xFF1976D2),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            },
                                            onClick = {
                                                nearestHydrant = hydrant
                                                nearestHydrantDistance = distance
                                                showNearestDropdown = false
                                                directionsResult = null

                                                val lat = hydrant.latitude.toDoubleOrNull()
                                                val lng = hydrant.longitude.toDoubleOrNull()
                                                if (lat != null && lng != null) {
                                                    scope.launch {
                                                        cameraPositionState.animate(
                                                            CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 16f),
                                                            durationMs = 800
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                        if (originalRank < nearestHydrants.size - 1) {
                                            HorizontalDivider(color = Color(0xFFEEEEEE))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        // Status badge
                        Surface(
                            color = if (nearestHydrant!!.serviceStatus == "In Service") Color(0xFF4CAF50) else Color(0xFFEF5350),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                nearestHydrant!!.serviceStatus,
                                Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        // Close button
                        IconButton(
                            onClick = {
                                nearestHydrant = null
                                nearestHydrants = emptyList()
                                directionsResult = null
                                selectedSearchHydrant = null
                                selectedSearchHydrantDistance = null
                                showNearestDropdown = false
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, "Close", tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(nearestHydrant!!.exactLocation, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF757575))
                    Text("Municipality: ${nearestHydrant!!.municipality}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF757575))
                    Spacer(Modifier.height(12.dp))

                    // Distance + Directions row
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(20.dp)) {
                            Row(
                                Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                if (directionsResult != null) {
                                    Column {
                                        Text(
                                            text = directionsResult!!.distance,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF1976D2),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = directionsResult!!.duration
                                                .replace("hours", "hrs")
                                                .replace("hour", "hr")
                                                .replace("mins", "min")
                                                .replace("minutes", "min"),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF1976D2),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    val distanceToShow = if (selectedSearchHydrant != null) selectedSearchHydrantDistance else nearestHydrantDistance
                                    Text(
                                        text = distanceToShow?.let {
                                            if (it < 1) "%.0f meters away".format(it * 1000)
                                            else "%.2f km away".format(it)
                                        } ?: "Calculating...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF1976D2),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (directionsResult != null) {
                                    // Directions already loaded — start navigation
                                    val hydrantLat = nearestHydrant!!.latitude.toDoubleOrNull()
                                    val hydrantLng = nearestHydrant!!.longitude.toDoubleOrNull()
                                    if (hydrantLat != null && hydrantLng != null && userCurrentLocation != null) {
                                        navigationHydrant = nearestHydrant
                                        currentStepIndex = 0
                                        isNavigating = true
                                        lastSpokenStepIndex = -1
                                        hasSpoken300m = false
                                        hasSpokenNow = false
                                        scope.launch {
                                            val bearing11 = run {
                                                val s1 = directionsResult?.steps?.firstOrNull()?.startLocation
                                                val s2 = directionsResult?.steps?.getOrNull(1)?.startLocation
                                                if (s1 != null && s2 != null) {
                                                    val lat1 = Math.toRadians(s1.latitude); val lat2 = Math.toRadians(s2.latitude)
                                                    val dLng = Math.toRadians(s2.longitude - s1.longitude)
                                                    ((Math.toDegrees(Math.atan2(Math.sin(dLng) * Math.cos(lat2), Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng))) + 360) % 360).toFloat()
                                                } else 0f
                                            }
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newCameraPosition(
                                                    CameraPosition.Builder().target(userCurrentLocation!!).zoom(18f).tilt(60f).bearing(bearing11).build()
                                                ), durationMs = 1000
                                            )
                                        }
                                    }
                                } else {
                                    // Fetch directions first
                                    val hydrantLat = nearestHydrant!!.latitude.toDoubleOrNull()
                                    val hydrantLng = nearestHydrant!!.longitude.toDoubleOrNull()
                                    if (hydrantLat != null && hydrantLng != null) {
                                        isLoadingDirections = true
                                        getCurrentLocationWithTimeout(
                                            context = context,
                                            timeoutMs = 8000L,
                                            onSuccess = { location ->
                                                userCurrentLocation = LatLng(location.latitude, location.longitude)
                                                scope.launch {
                                                    directionsResult = fetchDirections(
                                                        origin = LatLng(location.latitude, location.longitude),
                                                        destination = LatLng(hydrantLat, hydrantLng)
                                                    )
                                                    isLoadingDirections = false
                                                    if (directionsResult == null)
                                                        android.widget.Toast.makeText(context, "Could not fetch directions", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            onTimeout = {
                                                isLoadingDirections = false
                                                android.widget.Toast.makeText(context, "Could not get location. Try again.", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            },
                            enabled = !isLoadingDirections,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (directionsResult != null) Color(0xFF1A73E8) else Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            if (isLoadingDirections) {
                                CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(Modifier.width(6.dp))
                                Text("Loading...", color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            } else if (directionsResult != null) {
                                Icon(painter = painterResource(id = R.drawable.icon_navigation), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Navigate", color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            } else {
                                Icon(Icons.Default.Directions, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Directions", color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Turn-by-Turn Navigation Bar (Google Maps style)
        if (isNavigating && directionsResult != null && !isSearchOpen) {
            val steps = directionsResult!!.steps  // List<DirectionStep> — see note below
            val currentStep = steps.getOrNull(currentStepIndex)

            // Speak step-start announcement: "In 500 meters, turn right onto X"
            LaunchedEffect(currentStepIndex, tts) {
                val activeTts = tts ?: return@LaunchedEffect
                if (currentStep != null && currentStepIndex != lastSpokenStepIndex) {
                    lastSpokenStepIndex = currentStepIndex
                    hasSpoken300m = false
                    hasSpokenNow = false
                    val cleanInstruction = currentStep.instruction.replace(Regex("<[^>]*>"), "")
                    val distStr = currentStep.distance.trim()
                    // Opening announcement like Google Maps: "In X, [instruction]"
                    val speechText = "In $distStr, $cleanInstruction"
                    activeTts.speak(speechText, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "nav_start")
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {

                // Top instruction banner (green, like Google Maps)
                if (currentStep != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp),
                        color = Color(0xFF2E7D32),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 12.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Dynamic direction icon based on maneuver type
                            val maneuverRotation = when {
                                currentStep.maneuver.contains("turn-left") -> -90f
                                currentStep.maneuver.contains("turn-right") -> 90f
                                currentStep.maneuver.contains("uturn") -> 180f
                                currentStep.maneuver.contains("straight") -> 0f
                                currentStep.maneuver.contains("fork-left") || currentStep.maneuver.contains("keep-left") -> -45f
                                currentStep.maneuver.contains("fork-right") || currentStep.maneuver.contains("keep-right") -> 45f
                                currentStep.maneuver.contains("ramp-left") -> -45f
                                currentStep.maneuver.contains("ramp-right") -> 45f
                                else -> 0f
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = currentStep.maneuver,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(52.dp)
                                    .rotate(maneuverRotation + 90f)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentStep.instruction
                                        .replace(Regex("<[^>]*>"), ""), // Strip HTML tags
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 22.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                                Text(
                                    text = run {
                                        val distStr = currentStep.distance.trim()
                                        val kmRegex = Regex("""^([\d.]+)\s*km$""")
                                        val match = kmRegex.find(distStr)
                                        if (match != null) {
                                            val km = match.groupValues[1].toDoubleOrNull()
                                            if (km != null && km < 1.0) {
                                                "${(km * 1000).toInt()} m"
                                            } else distStr
                                        } else distStr
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }

                // Bottom bar: distance + time + Exit
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = directionsResult!!.duration,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "${directionsResult!!.distance} • ${
                                    java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                                        .format(java.util.Date())
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Previous step
                            if (currentStepIndex > 0) {
                                OutlinedButton(
                                    onClick = { currentStepIndex-- },
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.size(48.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    border = BorderStroke(1.5.dp, Color(0xFF5F6368))
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_left),
                                        contentDescription = "Previous step",
                                        tint = Color(0xFF5F6368),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Next step
                            if (steps != null && currentStepIndex < steps.size - 1) {
                                OutlinedButton(
                                    onClick = { currentStepIndex++ },
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.size(48.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    border = BorderStroke(1.5.dp, Color(0xFF5F6368))
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_right),
                                        contentDescription = "Next step",
                                        tint = Color(0xFF5F6368),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Exit button
                            Button(
                                onClick = {
                                    isNavigating = false
                                    currentStepIndex = 0
                                    navigationHydrant = null
                                    hydrantDirectionsFetched = false
                                    fireDirectionsFetched = false
                                    userCurrentLocation = null

                                    // Restore the fire incident card to its original state
                                    if (incidentMarkerLocation != null) {
                                        showIncidentInfoCard = true
                                        showIncidentMarker = true  // ← restores the fire marker on map

                                        // Re-calculate nearest hydrant
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
                                            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                                    Math.cos(Math.toRadians(lat)) *
                                                    Math.cos(Math.toRadians(hydrantLat)) *
                                                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
                                            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                                            val distance = r * c

                                            if (distance < minDistance) {
                                                minDistance = distance
                                                nearest = hydrant
                                            }
                                        }

                                        if (nearest != null) {
                                            nearestHydrant = nearest
                                            nearestHydrantDistance = minDistance

                                            // ← Re-fetch directions from incident to hydrant
                                            // This restores the blue polyline like the first appearance
                                            val hydrantLat = nearest.latitude.toDoubleOrNull()
                                            val hydrantLng = nearest.longitude.toDoubleOrNull()
                                            if (hydrantLat != null && hydrantLng != null) {
                                                isLoadingDirections = true
                                                scope.launch {
                                                    directionsResult = fetchDirections(
                                                        origin = incidentMarkerLocation!!,
                                                        destination = LatLng(hydrantLat, hydrantLng)
                                                    )
                                                    isLoadingDirections = false

                                                    // Zoom back to show both the fire and hydrant
                                                    cameraPositionState.animate(
                                                        CameraUpdateFactory.newLatLngZoom(incidentMarkerLocation!!, 14f),
                                                        durationMs = 800
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        directionsResult = null
                                        nearestHydrant = null
                                        nearestHydrantDistance = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF444444)
                                ),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(48.dp)
                                    .border(1.5.dp, Color(0xFFCCCCCC), CircleShape),
                                contentPadding = PaddingValues(0.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.close_button),
                                    contentDescription = "Exit navigation",
                                    tint = Color(0xFF444444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
            }

                // ── Overview / Normal View toggle button (bottom-right) ──
                var isOverviewMode by remember { mutableStateOf(false) }
                FloatingActionButton(
                    onClick = {
                        isOverviewMode = !isOverviewMode
                        scope.launch {
                            if (!isOverviewMode) {
                                // Go back to navigation tilt view (re-center on user)
                                val loc = userCurrentLocation
                                if (loc != null) {
                                    val bearing = run {
                                        val s1 = directionsResult?.steps?.getOrNull(currentStepIndex)?.startLocation
                                        val s2 = directionsResult?.steps?.getOrNull(currentStepIndex + 1)?.startLocation
                                        if (s1 != null && s2 != null) {
                                            val lat1 = Math.toRadians(s1.latitude)
                                            val lat2 = Math.toRadians(s2.latitude)
                                            val dLng = Math.toRadians(s2.longitude - s1.longitude)
                                            val y = Math.sin(dLng) * Math.cos(lat2)
                                            val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng)
                                            ((Math.toDegrees(Math.atan2(y, x)) + 360) % 360).toFloat()
                                        } else 0f
                                    }
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newCameraPosition(
                                            CameraPosition.Builder()
                                                .target(loc)
                                                .zoom(18f)
                                                .tilt(60f)
                                                .bearing(bearing)
                                                .build()
                                        ),
                                        durationMs = 800
                                    )
                                }
                            } else {
                                // Overview mode: fit the entire route using LatLngBounds (like Google Maps)
                                val routePoints = directionsResult?.polylinePoints
                                if (!routePoints.isNullOrEmpty()) {
                                    val boundsBuilder = com.google.android.gms.maps.model.LatLngBounds.Builder()
                                    routePoints.forEach { boundsBuilder.include(it) }
                                    // Also include user location so the blue dot is visible
                                    userCurrentLocation?.let { boundsBuilder.include(it) }
                                    val bounds = boundsBuilder.build()
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngBounds(
                                            bounds,
                                            120 // padding in pixels around the route edges
                                        ),
                                        durationMs = 900
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 90.dp),
                    containerColor = Color.White,
                    contentColor = Color(0xFF1B5E20),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = if (isOverviewMode) R.drawable.icon_navigation else R.drawable.expand),
                        contentDescription = if (isOverviewMode) "Back to navigation" else "Overview",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Loading
        if (hydrantUiState.isLoading || isSearchingNearestHydrant) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF6B35))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (isSearchingNearestHydrant) "Finding nearest hydrant..."
                            else "Loading hydrants..."
                        )
                    }
                }
            }
        }

        // Scrim (hide when search is open since search has its own overlay)
        if (isDrawerOpen && !isSearchOpen) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { isDrawerOpen = false }
            )
        }

// Drawer (hide when search is open)
        if (!isSearchOpen) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .align(Alignment.CenterStart)
                    .graphicsLayer { translationX = drawerOffsetPx.value },
                color = Color.White,
                shadowElevation = 0.dp
            ) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
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
                            .padding(20.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White)) {
                                            append("Fire")
                                        }
                                        withStyle(SpanStyle(color = Color(0xFFFFE0B2))) {
                                            append("Grid")
                                        }
                                    },
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Map Options",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Hydrants: ${hydrantsWithValidCoords.size}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            " ${hydrantsWithValidCoords.count { it.serviceStatus == "In Service" }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Icon(
                                            Icons.Default.LocationOn,
                                            null,
                                            tint = Color(0xFFEF5350),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            " ${hydrantsWithValidCoords.count { it.serviceStatus != "In Service" }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    // Find Nearest
                    Surface(
                        onClick = {
                            isDrawerOpen = false
                            directionsResult = null
                            selectedSearchHydrant = null
                            showHydrantDetailsCard = false
                            selectedHydrantForCard = null

                            if (checkLocationPermission(context)) {
                                if (locationSettingsOk) {
                                    triggerFindNearestHydrant = true
                                } else {
                                    cachedSettingsClient.checkLocationSettings(cachedLocationSettingsRequest)
                                        .addOnSuccessListener {
                                            locationSettingsOk = true
                                            triggerFindNearestHydrant = true
                                        }
                                        .addOnFailureListener { exception ->
                                            if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                                                try {
                                                    pendingLocationAction = "findNearest"
                                                    mapLocationSettingsLauncher.launch(
                                                        androidx.activity.result.IntentSenderRequest
                                                            .Builder(exception.resolution.intentSender).build()
                                                    )
                                                } catch (sendEx: android.content.IntentSender.SendIntentException) {
                                                    android.widget.Toast.makeText(
                                                        context,
                                                        "Unable to get current location",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            } else {
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "Unable to get current location",
                                                    android.widget.Toast.LENGTH_LONG
                                                ).show()
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
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "Find Nearest Hydrant",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = Color(0xFFE0E0E0))

                    // Reset
                    Surface(
                        onClick = {
                            isDrawerOpen = false
                            nearestHydrant = null
                            showIncidentMarker = false
                            showIncidentInfoCard = false
                            incidentMarkerLocation = null
                            directionsResult = null
                            selectedSearchHydrant = null
                            scope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newCameraPosition(
                                        CameraPosition.Builder()
                                            .target(LatLng(14.3500, 121.2500))  // Laguna Lake
                                            .zoom(10f)
                                            .bearing(0f)  // ⭐ Reset rotation to North
                                            .tilt(0f)     // ⭐ Reset tilt to flat
                                            .build()
                                    ),
                                    durationMs = 1000
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                null,
                                tint = Color(0xFFFF6B35),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text("Reset to Laguna View", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    // My Location
                    Surface(
                        onClick = {
                            isDrawerOpen = false
                            nearestHydrant = null
                            selectedSearchHydrant = null
                            if (checkLocationPermission(context)) {
                                if (locationSettingsOk) {
                                    triggerGoToMyLocation = true
                                } else {
                                    cachedSettingsClient.checkLocationSettings(cachedLocationSettingsRequest)
                                        .addOnSuccessListener {
                                            locationSettingsOk = true
                                            triggerGoToMyLocation = true
                                        }
                                        .addOnFailureListener { exception ->
                                            if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                                                try {
                                                    pendingLocationAction = "goToLocation"
                                                    mapLocationSettingsLauncher.launch(
                                                        androidx.activity.result.IntentSenderRequest
                                                            .Builder(exception.resolution.intentSender).build()
                                                    )
                                                } catch (sendEx: android.content.IntentSender.SendIntentException) {
                                                    android.widget.Toast.makeText(
                                                        context,
                                                        "Unable to get current location",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            } else {
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "Unable to get current location",
                                                    android.widget.Toast.LENGTH_LONG
                                                ).show()
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
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = Color(0xFF5F6368),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = "Go to My Location",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = Color(0xFFE0E0E0))

// Occupied Hydrants
                    Surface(
                        onClick = {
                            isDrawerOpen = false
                            showOccupiedHydrantsDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Occupied Hydrants",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    "${hydrantsWithValidCoords.count { it.serviceStatus == "Occupied" }} occupied",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }

                    Text(
                        "Map Legend",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("In Service Hydrant")
                    }
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = Color(0xFFEF5350), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Out of Service Hydrant")
                    }
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Nearest Hydrant")
                    }
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = Color(0xFFFF5722), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Fire Incident Location")
                    }
                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = Color(0xFFE0E0E0))
                    Text(
                        "FireGrid v1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }

    // Occupied Hydrants Dialog
    if (showOccupiedHydrantsDialog) {
        val occupiedList = hydrantsWithValidCoords.filter { it.serviceStatus == "Occupied" }

        // Live timer — ticks every second
        var now by remember { mutableStateOf(System.currentTimeMillis()) }
        LaunchedEffect(Unit) {
            while (true) {
                kotlinx.coroutines.delay(1000L)
                now = System.currentTimeMillis()
            }
        }

        fun formatElapsed(occupiedAtStr: String): String {
            return try {
                val sdf = java.text.SimpleDateFormat("MMMM dd, yyyy 'at' h:mm:ss a 'UTC+8'", java.util.Locale.ENGLISH)
                sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Manila")
                val occupiedTime = sdf.parse(occupiedAtStr)?.time ?: return "Unknown"
                val elapsed = now - occupiedTime
                val h = elapsed / 3600000
                val m = (elapsed % 3600000) / 60000
                val s = (elapsed % 60000) / 1000
                when {
                    h > 0 -> "${h}h ${m}m ${s}s"
                    m > 0 -> "${m}m ${s}s"
                    else  -> "${s}s"
                }
            } catch (e: Exception) { "Unknown" }
        }

        AlertDialog(
            onDismissRequest = { showOccupiedHydrantsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Occupied Hydrants", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFFF9800),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "${occupiedList.size}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            },
            text = {
                if (occupiedList.isEmpty()) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No occupied hydrants at this time.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                        items(occupiedList) { hydrant ->
                            Card(
                                onClick = {
                                    val lat = hydrant.latitude.toDoubleOrNull()
                                    val lng = hydrant.longitude.toDoubleOrNull()
                                    if (lat != null && lng != null) {
                                        showOccupiedHydrantsDialog = false
                                        selectedHydrantForCard = hydrant
                                        showHydrantDetailsCard = true
                                        isCardVisible = true
                                        scope.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 17f),
                                                durationMs = 800
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFCC80))
                            ) {
                                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                    // Hydrant name + live timer badge
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Icon(Icons.Default.LocationOn, null, tint = Color(0xFFFF9800), modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                hydrant.hydrantName,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        // Live elapsed timer
                                        if (hydrant.occupiedAt.isNotEmpty()) {
                                            Surface(
                                                color = Color(0xFFEF6C00),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    "⏱ ${formatElapsed(hydrant.occupiedAt)}",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    // Location & municipality
                                    Text(hydrant.exactLocation, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text(hydrant.municipality, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFF9800))
                                    Spacer(Modifier.height(6.dp))
                                    HorizontalDivider(color = Color(0xFFFFCC80))
                                    Spacer(Modifier.height(6.dp))
                                    // Who occupied
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccountBox, null, tint = Color(0xFF5F6368), modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "Occupied by: ${if (hydrant.occupiedBy.isNotEmpty()) hydrant.occupiedBy else "Unknown"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF5F6368)
                                        )
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    // When occupied
                                    if (hydrant.occupiedAt.isNotEmpty()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Info, null, tint = Color(0xFF5F6368), modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                "Since: ${hydrant.occupiedAt}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF9E9E9E)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showOccupiedHydrantsDialog = false }) {
                    Text("Close")
                }
            },
            containerColor = Color.White
        )
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
                .document("chief_admin")
                .collection("all")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { chiefDoc ->
                    if (chiefDoc.exists()) {
                        isAdmin = true
                        isChiefAdmin = true
                        adminMunicipality = null
                    } else {
                        val tasks = lagunaMunicipalities.map { mun ->
                            firestore.collection("admins")
                                .document("municipality_admin")
                                .collection(mun)
                                .document(userEmail.lowercase())
                                .get()
                        }
                        com.google.android.gms.tasks.Tasks.whenAllSuccess<com.google.firebase.firestore.DocumentSnapshot>(tasks)
                            .addOnSuccessListener { results ->
                                val munDoc = results.firstOrNull { it.exists() }
                                if (munDoc != null) {
                                    isAdmin = true
                                    isChiefAdmin = false
                                    adminMunicipality = munDoc.getString("municipality")
                                }
                            }
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
        }
        // silently do nothing if location is null
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
                .padding(top = 8.dp)  // Add just a small custom top padding
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
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(68.dp)
                        )

                        Column {
                            Text(
                                text = "FireGrid",
                                style = MaterialTheme.typography.headlineMedium,
                                fontSize = 28.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
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
            .document("chief_admin")
            .collection("all")
            .document(userEmail.lowercase())
            .get()
            .addOnSuccessListener { chiefDoc ->
                if (chiefDoc.exists()) {
                    adminType = "CHIEF_ADMINISTRATOR"
                    municipalityName = null
                    isCheckingAdmin = false
                } else {
                    val tasks = lagunaMunicipalities.map { mun ->
                        firestore.collection("admins")
                            .document("municipality_admin")
                            .collection(mun)
                            .document(userEmail.lowercase())
                            .get()
                    }
                    com.google.android.gms.tasks.Tasks.whenAllSuccess<com.google.firebase.firestore.DocumentSnapshot>(tasks)
                        .addOnSuccessListener { results ->
                            val found = results.firstOrNull { it.exists() }
                            if (found != null) {
                                adminType = "MUNICIPALITY_ADMIN"
                                municipalityName = found.getString("municipality")
                            }
                            isCheckingAdmin = false
                        }
                        .addOnFailureListener {
                            adminType = null
                            isCheckingAdmin = false
                        }
                }
            }
            .addOnFailureListener {
                adminType = null
                isCheckingAdmin = false
            }
    }

    val nameParts = userName.trim().split(" ").filter { it.isNotBlank() }
    val initials = when {
        nameParts.size >= 2 -> "${nameParts.first().first()}${nameParts.last().first()}"
        nameParts.size == 1 -> nameParts.first().first().toString()
        else -> ""
    }.uppercase()

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
                    .padding(top = 8.dp, bottom = 12.dp),  // Changed: removed statusBarsPadding(), added small top padding
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
                .document("chief_admin")
                .collection("all")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { chiefDoc ->
                    if (chiefDoc.exists()) {
                        isAdmin = true
                        isCheckingAdmin = false
                    } else {
                        val tasks = lagunaMunicipalities.map { mun ->
                            firestore.collection("admins")
                                .document("municipality_admin")
                                .collection(mun)
                                .document(userEmail.lowercase())
                                .get()
                        }
                        com.google.android.gms.tasks.Tasks.whenAllSuccess<com.google.firebase.firestore.DocumentSnapshot>(tasks)
                            .addOnSuccessListener { results ->
                                isAdmin = results.any { it.exists() }
                                isCheckingAdmin = false
                            }
                            .addOnFailureListener {
                                isAdmin = false
                                isCheckingAdmin = false
                            }
                    }
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.warning),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete Account",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.notif), contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notification Settings", fontWeight = FontWeight.Bold, color = Color(0xFFFF6B35))
                }
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(id = R.drawable.emergency_alerts), contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Emergency Alerts", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            }
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(id = R.drawable.fire_hydrant), contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Hydrant Updates", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            }
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
                                Image(painter = painterResource(id = R.drawable.change_password), contentDescription = null, modifier = Modifier.size(24.dp))
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
                                Image(painter = painterResource(id = R.drawable.delete_account), contentDescription = null, modifier = Modifier.size(24.dp))
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
                                    Image(painter = painterResource(id = R.drawable.add_admin), contentDescription = null, modifier = Modifier.size(24.dp))
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
                                    Image(painter = painterResource(id = R.drawable.remove_admin), contentDescription = null, modifier = Modifier.size(24.dp))
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
                                Image(painter = painterResource(id = R.drawable.notif), contentDescription = null, modifier = Modifier.size(24.dp))
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
                .document("chief_admin")
                .collection("all")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        currentUserRole = AdminRole.CHIEF_ADMINISTRATOR
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
                            Image(
                                painter = painterResource(id = R.drawable.add_admin),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
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
                        Image(
                            painter = painterResource(id = R.drawable.add_admin),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
                                                val roleFolder = if (selectedRole == AdminRole.CHIEF_ADMINISTRATOR) "chief_admin" else "municipality_admin"
                                                val municipalityFolder = if (selectedRole == AdminRole.MUNICIPALITY_ADMIN) selectedMunicipality!! else "all"

                                                firestore.collection("admins")
                                                    .document(roleFolder)
                                                    .collection(municipalityFolder)
                                                    .document(emailToSave)
                                                    .get()
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
                                                            val roleFolder = if (selectedRole == AdminRole.CHIEF_ADMINISTRATOR) "chief_admin" else "municipality_admin"
                                                            val municipalityFolder = if (selectedRole == AdminRole.MUNICIPALITY_ADMIN) selectedMunicipality!! else "all"

                                                            firestore.collection("admins")
                                                                .document(roleFolder)
                                                                .collection(municipalityFolder)
                                                                .document(emailToSave)
                                                                .set(adminData.toMap())
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
                                                            val roleFolder = if (selectedRole == AdminRole.CHIEF_ADMINISTRATOR) "chief_admin" else "municipality_admin"
                                                            val municipalityFolder = if (selectedRole == AdminRole.MUNICIPALITY_ADMIN) selectedMunicipality!! else "all"

                                                            firestore.collection("admins")
                                                                .document(roleFolder)
                                                                .collection(municipalityFolder)
                                                                .document(emailToSave)
                                                                .get()
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
                                                                        val roleFolder = if (selectedRole == AdminRole.CHIEF_ADMINISTRATOR) "chief_admin" else "municipality_admin"
                                                                        val municipalityFolder = if (selectedRole == AdminRole.MUNICIPALITY_ADMIN) selectedMunicipality!! else "all"

                                                                        firestore.collection("admins")
                                                                            .document(roleFolder)
                                                                            .collection(municipalityFolder)
                                                                            .document(emailToSave)
                                                                            .set(adminData.toMap())
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
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedRoleFilter by remember { mutableStateOf<AdminRole?>(null) }
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
                .document("chief_admin")
                .collection("all")
                .document(userEmail.lowercase())
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        currentUserRole = AdminRole.CHIEF_ADMINISTRATOR
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
        errorMessage = ""
        val result = mutableListOf<AdminUser>()

        firestore.collection("admins")
            .document("chief_admin")
            .collection("all")
            .get()
            .addOnSuccessListener { chiefDocs ->
                result.addAll(chiefDocs.documents.mapNotNull { AdminUser.fromDocument(it) })

                val municipalities = lagunaMunicipalities
                val tasks = municipalities.map { mun ->
                    firestore.collection("admins")
                        .document("municipality_admin")
                        .collection(mun)
                        .get()
                }

                com.google.android.gms.tasks.Tasks.whenAllSuccess<com.google.firebase.firestore.QuerySnapshot>(tasks)
                    .addOnSuccessListener { results ->
                        val munAdmins = results.flatMap { snapshot ->
                            snapshot.documents.mapNotNull { AdminUser.fromDocument(it) }
                        }
                        adminsList = (result + munAdmins).sortedBy { it.email }
                        isLoadingAdmins = false
                    }
                    .addOnFailureListener { e ->
                        isLoadingAdmins = false
                        errorMessage = "Failed to load admins: ${e.message}"
                    }
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
                            Image(
                                painter = painterResource(id = R.drawable.remove_admin),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
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

    // Filter Dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = {
                Text("Filter Admins", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select which admins to display:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    // ALL option
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedRoleFilter = null
                            showFilterDialog = false
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedRoleFilter == null) Color(0xFFFFCDD2) else Color(0xFFF5F5F5)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("👥", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("All Admins", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text("Show all administrators", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            if (selectedRoleFilter == null) Icon(Icons.Default.Check, null, tint = Color(0xFFEF5350))
                        }
                    }

                    // CHIEF ADMIN option
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedRoleFilter = AdminRole.CHIEF_ADMINISTRATOR
                            showFilterDialog = false
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedRoleFilter == AdminRole.CHIEF_ADMINISTRATOR) Color(0xFFFFF3E0) else Color(0xFFF5F5F5)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Chief Admin", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = Color(0xFFE65100))
                                Text("Full access administrators", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            if (selectedRoleFilter == AdminRole.CHIEF_ADMINISTRATOR) Icon(Icons.Default.Check, null, tint = Color(0xFFE65100))
                        }
                    }

                    // MUNICIPALITY ADMIN option
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedRoleFilter = AdminRole.MUNICIPALITY_ADMIN
                            showFilterDialog = false
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedRoleFilter == AdminRole.MUNICIPALITY_ADMIN) Color(0xFFFFCDD2) else Color(0xFFF5F5F5)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🏛️", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Municipality Admin", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = Color(0xFFD32F2F))
                                Text("Municipality-level administrators", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            if (selectedRoleFilter == AdminRole.MUNICIPALITY_ADMIN) Icon(Icons.Default.Check, null, tint = Color(0xFFD32F2F))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Close", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
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
                                val roleFolder = if (selectedAdmin!!.role == AdminRole.CHIEF_ADMINISTRATOR) "chief_admin" else "municipality_admin"
                                val municipalityFolder = selectedAdmin!!.municipality ?: "all"

                                firestore.collection("admins")
                                    .document(roleFolder)
                                    .collection(municipalityFolder)
                                    .document(adminEmail)
                                    .delete()
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
                        Image(
                            painter = painterResource(id = R.drawable.remove_admin),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
            Card(
                modifier = Modifier.fillMaxWidth().clickable { showFilterDialog = true },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Remove Admin Privileges", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFEF5350))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(
                            when (selectedRoleFilter) {
                                AdminRole.CHIEF_ADMINISTRATOR -> "Showing: Chief Admins"
                                AdminRole.MUNICIPALITY_ADMIN -> "Showing: Municipality Admins"
                                else -> "Showing: All Admins"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFEF5350)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Filter", tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
                    }
                    Text(
                        if (isLoadingAdmins) "Loading admins..."
                        else {
                            val count = adminsList.count { selectedRoleFilter == null || it.role == selectedRoleFilter }
                            "$count admin${if (count != 1) "s" else ""} found"
                        },
                        style = MaterialTheme.typography.bodySmall,
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
                adminsList.filter { selectedRoleFilter == null || it.role == selectedRoleFilter }.forEach { admin ->
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
    var isNewPasswordFocused by remember { mutableStateOf(false) }

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
                        val slashProgress = remember { Animatable(if (currentPasswordVisible) 0f else 1f) }
                        LaunchedEffect(currentPasswordVisible) {
                            slashProgress.animateTo(
                                targetValue = if (currentPasswordVisible) 0f else 1f,
                                animationSpec = tween(durationMillis = 150, easing = LinearEasing)
                            )
                        }
                        IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.eye_on),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Canvas(modifier = Modifier.size(24.dp)) {
                                    val p = slashProgress.value
                                    if (p > 0f) {
                                        val startX = size.width * 0.15f
                                        val startY = size.height * 0.05f
                                        val endX = size.width * 0.85f
                                        val endY = size.height * 0.95f
                                        val animEndX = startX + (endX - startX) * p
                                        val animEndY = startY + (endY - startY) * p
                                        drawLine(
                                            color = Color.White,
                                            start = Offset(startX, startY),
                                            end = Offset(animEndX, animEndY),
                                            strokeWidth = 5.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                        drawLine(
                                            color = Color(0xFF444444),
                                            start = Offset(startX, startY),
                                            end = Offset(animEndX, animEndY),
                                            strokeWidth = 2.5.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }
                            }
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
                        newPassword = it.filter { char -> !char.isWhitespace() }
                        errorMessage = ""
                    },
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val slashProgress = remember { Animatable(if (newPasswordVisible) 0f else 1f) }
                        LaunchedEffect(newPasswordVisible) {
                            slashProgress.animateTo(
                                targetValue = if (newPasswordVisible) 0f else 1f,
                                animationSpec = tween(durationMillis = 150, easing = LinearEasing)
                            )
                        }
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.eye_on),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Canvas(modifier = Modifier.size(24.dp)) {
                                    val p = slashProgress.value
                                    if (p > 0f) {
                                        val startX = size.width * 0.15f
                                        val startY = size.height * 0.05f
                                        val endX = size.width * 0.85f
                                        val endY = size.height * 0.95f
                                        val animEndX = startX + (endX - startX) * p
                                        val animEndY = startY + (endY - startY) * p
                                        drawLine(color = Color.White, start = Offset(startX, startY), end = Offset(animEndX, animEndY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                                        drawLine(color = Color(0xFF444444), start = Offset(startX, startY), end = Offset(animEndX, animEndY), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isNewPasswordFocused = focusState.isFocused
                        },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B35),
                        focusedLabelColor = Color(0xFFFF6B35)
                    )
                )

// Password requirements hint - only show when focused
                if (isNewPasswordFocused) {
                    Text(
                        text = "Must have: 12 chars, 1 uppercase, 1 number, 1 special char",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

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
                        val slashProgress = remember { Animatable(if (confirmPasswordVisible) 0f else 1f) }
                        LaunchedEffect(confirmPasswordVisible) {
                            slashProgress.animateTo(
                                targetValue = if (confirmPasswordVisible) 0f else 1f,
                                animationSpec = tween(durationMillis = 150, easing = LinearEasing)
                            )
                        }
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.eye_on),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Canvas(modifier = Modifier.size(24.dp)) {
                                    val p = slashProgress.value
                                    if (p > 0f) {
                                        val startX = size.width * 0.15f
                                        val startY = size.height * 0.05f
                                        val endX = size.width * 0.85f
                                        val endY = size.height * 0.95f
                                        val animEndX = startX + (endX - startX) * p
                                        val animEndY = startY + (endY - startY) * p
                                        drawLine(color = Color.White, start = Offset(startX, startY), end = Offset(animEndX, animEndY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                                        drawLine(color = Color(0xFF444444), start = Offset(startX, startY), end = Offset(animEndX, animEndY), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
                                    }
                                }
                            }
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

class HydrantOccupiedReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val hydrantId = intent.getStringExtra("hydrantId") ?: return
        val hydrantName = intent.getStringExtra("hydrantName") ?: ""
        val municipality = intent.getStringExtra("municipality") ?: return
        val occupiedBy = intent.getStringExtra("occupiedBy") ?: ""
        val notifId = intent.getIntExtra("notifId", 0)

        // Dismiss the notification
        NotificationManagerCompat.from(context).cancel(notifId)

        val rtdb = com.google.firebase.database.FirebaseDatabase
            .getInstance("https://firegrid-58bc3-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val ref = rtdb.getReference("fire_hydrants").child(municipality).child(hydrantId)

        when (intent.action) {
            "ACTION_ACKNOWLEDGE" -> {
                // Schedule another notification in 1 hour
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                val scheduleIntent = Intent(context, HydrantOccupiedReceiver::class.java).apply {
                    action = "ACTION_FIRE_NOTIFICATION"
                    putExtra("hydrantId", hydrantId)
                    putExtra("hydrantName", hydrantName)
                    putExtra("municipality", municipality)
                    putExtra("occupiedBy", occupiedBy)
                    putExtra("notifId", notifId)
                }
                val pi = PendingIntent.getBroadcast(
                    context, notifId + 10, scheduleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val triggerAt = System.currentTimeMillis() + 60 * 60 * 1000L // 1 hour
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
                } else {
                    alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
                }

                // Also schedule auto-release if ignored (10 mins after the next notif fires)
                // i.e., 1hr + 10min from now
                scheduleAutoRelease(context, hydrantId, hydrantName, municipality, notifId,
                    delayMs = (60 + 10) * 60 * 1000L) // 1hr 10min (1hr until re-notif + 10min grace)
            }

            "ACTION_RELEASE", "ACTION_AUTO_RELEASE" -> {
                // Cancel any pending alarms
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                val cancelAutoRelease = PendingIntent.getBroadcast(
                    context, notifId + 20,
                    Intent(context, HydrantOccupiedReceiver::class.java).apply { action = "ACTION_AUTO_RELEASE" },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val cancelRenotif = PendingIntent.getBroadcast(
                    context, notifId + 10,
                    Intent(context, HydrantOccupiedReceiver::class.java).apply { action = "ACTION_FIRE_NOTIFICATION" },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(cancelAutoRelease)
                alarmManager.cancel(cancelRenotif)

                // Release the hydrant in Firebase
                val sdf = java.text.SimpleDateFormat(
                    "MMMM dd, yyyy 'at' h:mm:ss a 'UTC+8'", java.util.Locale.ENGLISH
                )
                sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Manila")
                val timestamp = sdf.format(java.util.Date())
                ref.updateChildren(mapOf(
                    "ServiceStatus" to "In Service",
                    "LastUpdated" to timestamp,
                    "OccupiedBy" to "",
                    "OccupiedAt" to ""
                ))
                if (intent.action == "ACTION_AUTO_RELEASE") {
                    // Show a silent info notification that it was auto-released
                    val autoNotif = NotificationCompat.Builder(context, "hydrant_updates_channel")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Hydrant Auto-Released")
                        .setContentText("$hydrantName in $municipality was automatically released.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build()
                    try { NotificationManagerCompat.from(context).notify(notifId + 99, autoNotif) }
                    catch (e: SecurityException) { e.printStackTrace() }
                }
            }

            "ACTION_FIRE_NOTIFICATION" -> {
                // Re-fire the occupied notification after 1hr acknowledge
                showHydrantOccupiedNotification(context, hydrantId, hydrantName, municipality, occupiedBy, "")
                // Schedule auto-release 10 mins after this notification
                scheduleAutoRelease(context, hydrantId, hydrantName, municipality, notifId,
                    delayMs = 10 * 60 * 1000L) // 10 minutes after notification fires
            }
        }
    }

    private fun scheduleAutoRelease(
        context: Context,
        hydrantId: String,
        hydrantName: String,
        municipality: String,
        notifId: Int,
        delayMs: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val autoReleaseIntent = Intent(context, HydrantOccupiedReceiver::class.java).apply {
            action = "ACTION_AUTO_RELEASE"
            putExtra("hydrantId", hydrantId)
            putExtra("hydrantName", hydrantName)
            putExtra("municipality", municipality)
            putExtra("notifId", notifId)
        }
        val pi = PendingIntent.getBroadcast(
            context, notifId + 20, autoReleaseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = System.currentTimeMillis() + delayMs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
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