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
import com.example.hydrant.model.FireHydrant
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
        super.onCreate(savedInstanceState)
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
    var isLoggedIn by rememberSaveable { mutableStateOf(auth.currentUser != null) }
    var showSignUp by rememberSaveable { mutableStateOf(false) }
    var signUpSuccessMessage by rememberSaveable { mutableStateOf<String?>(null) }  // NEW: Added this state

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(padding)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
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

                // Inline Error Message Display
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
            // Combine names: First Middle Last (middle name is optional)
            val fullName = if (middleName.isNotBlank()) {
                "${firstName.trim()} ${middleName.trim()} ${lastName.trim()}"
            } else {
                "${firstName.trim()} ${lastName.trim()}"
            }
            val profileUpdates = userProfileChangeRequest {
                displayName = fullName.trim()
            }
            user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                // Sign out after successful registration so user goes back to login
                FirebaseAuth.getInstance().signOut()
                viewModel.resetAuthState()
                onSignUpComplete("Account created successfully! Please login.")
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

                // Password
                item {
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
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

                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    Text(
                        text = "Password must have: 12 characters, 1 uppercase, 1 number, 1 special character",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // Confirm Password
                item {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
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

    // NEW: Add state for showing invalid coordinates filter
    var showInvalidCoordinatesOnly by rememberSaveable { mutableStateOf(false) }

    val favoritesScrollState = androidx.compose.foundation.lazy.rememberLazyListState()

    when {
        showHelpFaqScreen -> {
            HelpFaqScreen(onBack = { showHelpFaqScreen = false })
        }
        showContactSupportScreen -> {
            ContactSupportScreen(onBack = { showContactSupportScreen = false })
        }
        showReportProblemScreen -> {
            ReportProblemScreen(onBack = { showReportProblemScreen = false })
        }
        showAboutAppScreen -> {
            AboutAppScreen(onBack = { showAboutAppScreen = false })
        }
        showTermsPrivacyScreen -> {
            TermsPrivacyScreen(onBack = { showTermsPrivacyScreen = false })
        }
        selectedHydrantForView != null -> {
            ViewHydrantLocationScreen(
                hydrant = selectedHydrantForView!!,
                onBack = { selectedHydrantForView = null }
            )
        }
        selectedHydrantForEdit != null && selectedMunicipality != null -> {
            EditFireHydrantScreen(
                hydrant = selectedHydrantForEdit!!,
                onBack = { selectedHydrantForEdit = null },
                onSuccess = { selectedHydrantForEdit = null },
                onDelete = { selectedHydrantForEdit = null }
            )
        }
        showHydrantMapScreen && selectedMunicipality != null -> {
            FireHydrantMapScreen(
                municipalityName = selectedMunicipality!!,
                onBack = { showHydrantMapScreen = false },
                // NEW: Add callback for warning button
                onShowInvalidCoordinates = {
                    showInvalidCoordinatesOnly = true
                    showHydrantMapScreen = false
                    showHydrantListScreen = true
                }
            )
        }
        showHydrantListScreen && selectedMunicipality != null -> {
            FireHydrantListScreen(
                municipalityName = selectedMunicipality!!,
                onBack = {
                    showHydrantListScreen = false
                    showInvalidCoordinatesOnly = false  // Reset filter when going back
                },
                onEditHydrant = { hydrant ->
                    selectedHydrantForEdit = hydrant
                },
                onViewLocation = { hydrant ->
                    selectedHydrantForView = hydrant
                },
                // NEW: Pass the filter state
                showInvalidCoordinatesOnly = showInvalidCoordinatesOnly,
                onClearInvalidFilter = { showInvalidCoordinatesOnly = false }
            )
        }
        showAddHydrantScreen && selectedMunicipality != null -> {
            AddFireHydrantScreen(
                municipalityName = selectedMunicipality!!,
                onBack = { showAddHydrantScreen = false },
                onSuccess = { showAddHydrantScreen = false }
            )
        }
        selectedMunicipality != null -> {
            MunicipalityDetailScreen(
                municipalityName = selectedMunicipality!!,
                onBack = { selectedMunicipality = null },
                onAddHydrant = { showAddHydrantScreen = true },
                onShowHydrantList = { showHydrantListScreen = true },
                onShowHydrantMap = { showHydrantMapScreen = true }
            )
        }
        else -> {
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
                        AppDestinations.MAP -> MapScreen(modifier = Modifier.padding(innerPadding))
                        AppDestinations.PROFILE -> ProfileScreen(
                            modifier = Modifier.padding(innerPadding),
                            userName = userName,
                            onLogout = onLogout,
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

// Replace your existing FireHydrantListScreen function with this one:

// Replace your existing FireHydrantListScreen function with this one:

// Replace your existing FireHydrantListScreen function with this one:

// Replace your existing FireHydrantListScreen function with this one:

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
        val matchesSearch = searchQuery.isEmpty() ||
                hydrant.hydrantName.contains(searchQuery, ignoreCase = true) ||
                hydrant.exactLocation.contains(searchQuery, ignoreCase = true) ||
                hydrant.id.contains(searchQuery, ignoreCase = true)

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
    }

    // Reusable Filter Dropdown Menu content - NO heart on "All"
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
                        // For Invalid Coordinates with search active - show search field in title
                        if (showInvalidCoordinatesOnly && isSearchActive) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text("Search...", color = Color.Gray)
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
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        // Only show action buttons for Invalid Coordinates view when search is NOT active
                        if (showInvalidCoordinatesOnly && !isSearchActive) {
                            // Search icon button (white background, like Home screen)
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

                            // Filter button - arrow only, NO box/background
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
            // Search bar and Filter button BELOW header - Only for regular Fire Hydrant Lists
            if (!showInvalidCoordinatesOnly) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search TextField
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Search...", color = Color.Gray)
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

                    // Filter Button with Dropdown - shows "Filter" text with arrow
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
                            // Warning icon using text emoji
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

            // Show active filter indicator if not "All" (only for regular list)
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
                            showInvalidWarning = hasInvalidCoordinates(hydrant)
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
    showInvalidWarning: Boolean = false
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
                        // Warning icon using emoji - simple and reliable
                        Text(
                            text = "⚠️",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Invalid coordinates - Please update",
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

                // View Location Button - Single line
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
    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Form state - initialized with existing hydrant data
    var hydrantName by rememberSaveable { mutableStateOf(hydrant.hydrantName) }
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

    // Check if there are unsaved changes
    val hasChanges = hydrantName != hydrant.hydrantName ||
            exactLocation != hydrant.exactLocation ||
            latitude != hydrant.latitude ||
            longitude != hydrant.longitude ||
            typeColor != hydrant.typeColor ||
            serviceStatus != hydrant.serviceStatus ||
            remarks != hydrant.remarks

    // Handle update success
    LaunchedEffect(hydrantUiState.updateSuccess) {
        if (hydrantUiState.updateSuccess) {
            snackbarHostState.showSnackbar("Fire hydrant updated successfully!")
            hydrantViewModel.resetUpdateSuccess()
            onSuccess()
        }
    }

    // Handle delete success
    LaunchedEffect(hydrantUiState.deleteSuccess) {
        if (hydrantUiState.deleteSuccess) {
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
                        // Save changes
                        if (hydrantName.isNotBlank() && exactLocation.isNotBlank()) {
                            hydrantViewModel.updateFireHydrant(
                                hydrant.copy(
                                    hydrantName = hydrantName,
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
                        // Use the new deleteHydrantAndRenumber function
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
            // Hydrant Name
            Column {
                Text(
                    "Hydrant Name",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hydrantName,
                    onValueChange = {
                        hydrantName = it
                        validationError = ""
                    },
                    placeholder = { Text("Enter hydrant name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !hydrantUiState.isLoading,
                    shape = RoundedCornerShape(8.dp)
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
                            hydrantName.isBlank() -> validationError = "Hydrant name is required"
                            exactLocation.isBlank() -> validationError = "Exact location is required"
                            else -> {
                                hydrantViewModel.updateFireHydrant(
                                    hydrant.copy(
                                        hydrantName = hydrantName,
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
    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var hydrantName by rememberSaveable { mutableStateOf("") }
    var exactLocation by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var typeColor by rememberSaveable { mutableStateOf("") }
    var serviceStatus by rememberSaveable { mutableStateOf("In Service") }
    var remarks by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var validationError by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(hydrantUiState.addSuccess) {
        if (hydrantUiState.addSuccess) {
            snackbarHostState.showSnackbar("Fire hydrant added successfully!")
            hydrantViewModel.resetAddSuccess()
            onSuccess()
        }
    }

    LaunchedEffect(hydrantUiState.error) {
        hydrantUiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            hydrantViewModel.clearError()
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
            Column {
                Text(
                    "Hydrant Name",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hydrantName,
                    onValueChange = {
                        hydrantName = it
                        validationError = ""
                    },
                    placeholder = { Text("Enter hydrant name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !hydrantUiState.isLoading,
                    shape = RoundedCornerShape(8.dp)
                )
            }

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
                            hydrantName.isBlank() -> validationError = "Hydrant name is required"
                            exactLocation.isBlank() -> validationError = "Exact location is required"
                            latitude.isBlank() -> validationError = "Latitude is required"
                            longitude.isBlank() -> validationError = "Longitude is required"
                            typeColor.isBlank() -> validationError = "Type/Color is required"
                            else -> {
                                hydrantViewModel.addFireHydrant(
                                    hydrantName = hydrantName,
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
                        Text("Add Fire Hydrant")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
    var weatherData by remember { mutableStateOf<DailyWeather?>(null) }
    var isWeatherLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

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

    val outOfService = hydrantUiState.outOfServiceCount
    val inService = hydrantUiState.inServiceCount
    val totalHydrants = outOfService + inService

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
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
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
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFCDD2)
                        ),
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
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFC8E6C9)
                        ),
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

            // Total Hydrants Card - Compact white version
            // Total Hydrants Card - Compact faded blue version
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(56.dp),  // Same height as Add Fire Hydrant button
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFBBDEFB)  // Light faded blue (like the others)
                    ),
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
            // ===== END OF NEW Total Hydrants Card =====

            item {
                Button(
                    onClick = onAddHydrant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF81C784)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Add Fire Hydrant",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350)
                        ),
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350)
                        ),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewHydrantLocationScreen(
    hydrant: FireHydrant,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val lat = hydrant.latitude.toDoubleOrNull()
    val lng = hydrant.longitude.toDoubleOrNull()

    // Check if coordinates are valid
    val hasValidCoordinates = lat != null && lng != null &&
            lat >= -90 && lat <= 90 &&
            lng >= -180 && lng <= 180 &&
            lat != 0.0 && lng != 0.0

    val hydrantLocation = if (hasValidCoordinates) {
        LatLng(lat!!, lng!!)
    } else {
        getMunicipalityCoordinates(hydrant.municipality)
    }

    val cameraPositionState = rememberCameraPositionState {
        // Zoom level 17 for close-up view of the hydrant location
        position = CameraPosition.fromLatLngZoom(hydrantLocation, 17f)
    }

    // Create pin marker based on service status
    val markerIcon = remember(hydrant.serviceStatus) {
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

        val centerX = width / 2f
        val topRadius = 14f
        val bottomY = height - 4f

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

        com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap)
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
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                )
            ) {
                // Add marker for the hydrant
                if (hasValidCoordinates) {
                    com.google.maps.android.compose.Marker(
                        state = com.google.maps.android.compose.MarkerState(
                            position = hydrantLocation
                        ),
                        title = "Hydrant ${hydrant.id}",
                        snippet = "${hydrant.hydrantName} - ${hydrant.serviceStatus}",
                        icon = markerIcon
                    )
                }
            }

            // Custom zoom controls - same style as Home page
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
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(hydrantLocation, 17f)
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

                // Zoom out button
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(modifier: Modifier = Modifier) {
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
    val scope = rememberCoroutineScope()

    // Get hydrant ViewModel to load all hydrants
    val hydrantViewModel: FireHydrantViewModel = viewModel()
    val hydrantUiState by hydrantViewModel.uiState.collectAsState()

    // Load all hydrants when screen opens
    LaunchedEffect(Unit) {
        hydrantViewModel.loadAllHydrants()
    }

    // Filter hydrants with valid coordinates
    val hydrantsWithValidCoords = hydrantUiState.allHydrants.filter { hydrant ->
        val lat = hydrant.latitude.toDoubleOrNull()
        val lng = hydrant.longitude.toDoubleOrNull()
        lat != null && lng != null &&
                lat in -90.0..90.0 &&
                lng in -180.0..180.0 &&
                !(lat == 0.0 && lng == 0.0)
    }

    // Create marker icons
    var greenMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var redMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var blueMarkerIcon by remember { mutableStateOf<com.google.android.gms.maps.model.BitmapDescriptor?>(null) }
    var markersReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        try {
            val centerX = 24f
            val topRadius = 14f
            val bottomY = 44f

            // Create green marker (In Service)
            val greenBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val greenCanvas = android.graphics.Canvas(greenBitmap)
            val greenPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#4CAF50")
                style = android.graphics.Paint.Style.FILL
            }
            greenCanvas.drawCircle(centerX, topRadius + 4f, topRadius, greenPaint)
            val greenTriangle = android.graphics.Path()
            greenTriangle.moveTo(centerX, bottomY)
            greenTriangle.lineTo(centerX - 12f, topRadius + 12f)
            greenTriangle.lineTo(centerX + 12f, topRadius + 12f)
            greenTriangle.close()
            greenCanvas.drawPath(greenTriangle, greenPaint)
            greenPaint.color = android.graphics.Color.WHITE
            greenCanvas.drawCircle(centerX, topRadius + 4f, 6f, greenPaint)
            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(greenBitmap)

            // Create red marker (Out of Service)
            val redBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val redCanvas = android.graphics.Canvas(redBitmap)
            val redPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#EF5350")
                style = android.graphics.Paint.Style.FILL
            }
            redCanvas.drawCircle(centerX, topRadius + 4f, topRadius, redPaint)
            val redTriangle = android.graphics.Path()
            redTriangle.moveTo(centerX, bottomY)
            redTriangle.lineTo(centerX - 12f, topRadius + 12f)
            redTriangle.lineTo(centerX + 12f, topRadius + 12f)
            redTriangle.close()
            redCanvas.drawPath(redTriangle, redPaint)
            redPaint.color = android.graphics.Color.WHITE
            redCanvas.drawCircle(centerX, topRadius + 4f, 6f, redPaint)
            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(redBitmap)

            // Create blue marker (Nearest Hydrant)
            val blueBitmap = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
            val blueCanvas = android.graphics.Canvas(blueBitmap)
            val bluePaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#2196F3")
                style = android.graphics.Paint.Style.FILL
            }
            blueCanvas.drawCircle(centerX, topRadius + 4f, topRadius, bluePaint)
            val blueTriangle = android.graphics.Path()
            blueTriangle.moveTo(centerX, bottomY)
            blueTriangle.lineTo(centerX - 12f, topRadius + 12f)
            blueTriangle.lineTo(centerX + 12f, topRadius + 12f)
            blueTriangle.close()
            blueCanvas.drawPath(blueTriangle, bluePaint)
            bluePaint.color = android.graphics.Color.WHITE
            blueCanvas.drawCircle(centerX, topRadius + 4f, 6f, bluePaint)
            blueMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(blueBitmap)

            markersReady = true
        } catch (e: Exception) {
            greenMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
            )
            redMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
            )
            blueMarkerIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE
            )
            markersReady = true
        }
    }

    // Animation for drawer slide
    val drawerOffsetX by animateDpAsState(
        targetValue = if (isDrawerOpen) 0.dp else (-280).dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "drawerOffset"
    )

    // Animation for scrim alpha
    val scrimAlpha by animateFloatAsState(
        targetValue = if (isDrawerOpen) 0.5f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "scrimAlpha"
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
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
            }
            else -> {
                android.widget.Toast.makeText(
                    context,
                    "Location permission denied",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Function to calculate distance between two points (Haversine formula)
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Earth's radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c // Distance in kilometers
    }

    // Function to find nearest hydrant
    fun findNearestHydrant(userLat: Double, userLng: Double, hydrants: List<FireHydrant>): Pair<FireHydrant?, Double?> {
        var nearest: FireHydrant? = null
        var minDistance = Double.MAX_VALUE

        for (hydrant in hydrants) {
            val lat = hydrant.latitude.toDoubleOrNull() ?: continue
            val lng = hydrant.longitude.toDoubleOrNull() ?: continue

            // Skip invalid coordinates
            if (lat < -90 || lat > 90 || lng < -180 || lng > 180 || (lat == 0.0 && lng == 0.0)) continue

            val distance = calculateDistance(userLat, userLng, lat, lng)
            if (distance < minDistance) {
                minDistance = distance
                nearest = hydrant
            }
        }

        return if (nearest != null) Pair(nearest, minDistance) else Pair(null, null)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = isMyLocationEnabled
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        ) {
            // Show all hydrants on the map
            if (markersReady && greenMarkerIcon != null && redMarkerIcon != null) {
                hydrantsWithValidCoords.forEach { hydrant ->
                    val lat = hydrant.latitude.toDoubleOrNull() ?: return@forEach
                    val lng = hydrant.longitude.toDoubleOrNull() ?: return@forEach

                    // Check if this is the nearest hydrant
                    val isNearest = nearestHydrant?.let {
                        it.id == hydrant.id && it.municipality == hydrant.municipality
                    } ?: false

                    val markerIcon = when {
                        isNearest -> blueMarkerIcon!!
                        hydrant.serviceStatus == "In Service" -> greenMarkerIcon!!
                        else -> redMarkerIcon!!
                    }

                    com.google.maps.android.compose.Marker(
                        state = com.google.maps.android.compose.MarkerState(
                            position = LatLng(lat, lng)
                        ),
                        title = "${hydrant.municipality} - ${hydrant.hydrantName}",
                        snippet = "${hydrant.exactLocation} | ${hydrant.serviceStatus}",
                        icon = markerIcon
                    )
                }
            }
        }

        // Hydrant count info card - Top Left (below menu button)
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 64.dp)
                .statusBarsPadding(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Hydrants: ${hydrantsWithValidCoords.size}",
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
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = " ${hydrantsWithValidCoords.count { it.serviceStatus == "In Service" }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = " ${hydrantsWithValidCoords.count { it.serviceStatus != "In Service" }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Nearest Hydrant Info Card (shown when hydrant is found)
        nearestHydrant?.let { hydrant ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Nearest Hydrant",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                        }
                        IconButton(
                            onClick = { nearestHydrant = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = hydrant.hydrantName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = hydrant.exactLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Municipality: ${hydrant.municipality}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        nearestHydrantDistance?.let { distance ->
                            Surface(
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = if (distance < 1) {
                                        "📍 %.0f meters away".format(distance * 1000)
                                    } else {
                                        "📍 %.2f km away".format(distance)
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1976D2),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Surface(
                            color = if (hydrant.serviceStatus == "In Service")
                                Color(0xFF81C784) else Color(0xFFEF5350),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = hydrant.serviceStatus,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Hamburger Menu Button - Top Left
        FloatingActionButton(
            onClick = { isDrawerOpen = true },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 12.dp)
                .statusBarsPadding()
                .size(40.dp),
            containerColor = Color.White,
            contentColor = Color(0xFF5F6368),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 3.dp,
                pressedElevation = 5.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "≡",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF5F6368),
                fontWeight = FontWeight.Bold
            )
        }

        // Zoom and Location Controls - Bottom Right
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = if (nearestHydrant != null) 180.dp else 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
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
                    contentDescription = "My Location",
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF5F6368)
                )
            }

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

        // Loading indicator
        if (hydrantUiState.isLoading || isSearchingNearestHydrant) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF6B35))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isSearchingNearestHydrant) "Finding nearest hydrant..." else "Loading hydrants...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Dark scrim with animation - TAP TO CLOSE
        if (scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { isDrawerOpen = false }
            )
        }

        // Drawer Content with slide animation
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .offset(x = drawerOffsetX)
                .align(Alignment.CenterStart),
            color = Color.White,
            shadowElevation = if (isDrawerOpen) 16.dp else 0.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFF6B35))
                        .statusBarsPadding()
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.White)) {
                                    append("Fire")
                                }
                                withStyle(style = SpanStyle(color = Color(0xFFFFE0B2))) {
                                    append("Grid")
                                }
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Map Options",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Find Nearest Hydrant
                Surface(
                    onClick = {
                        isDrawerOpen = false
                        if (checkLocationPermission(context)) {
                            isSearchingNearestHydrant = true
                            isMyLocationEnabled = true

                            getCurrentLocation(context) { location ->
                                scope.launch {
                                    // Use already loaded hydrants
                                    val allHydrants = hydrantUiState.allHydrants
                                    if (allHydrants.isNotEmpty()) {
                                        val (nearest, distance) = findNearestHydrant(
                                            location.latitude,
                                            location.longitude,
                                            allHydrants
                                        )

                                        if (nearest != null) {
                                            nearestHydrant = nearest
                                            nearestHydrantDistance = distance

                                            val lat = nearest.latitude.toDoubleOrNull()
                                            val lng = nearest.longitude.toDoubleOrNull()
                                            if (lat != null && lng != null) {
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newLatLngZoom(
                                                        LatLng(lat, lng),
                                                        16f
                                                    )
                                                )
                                            }
                                        } else {
                                            android.widget.Toast.makeText(
                                                context,
                                                "No hydrants found nearby",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        android.widget.Toast.makeText(
                                            context,
                                            "Loading hydrants, please try again",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    isSearchingNearestHydrant = false
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
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Find Nearest Hydrant",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                // Reset to Laguna View
                Surface(
                    onClick = {
                        isDrawerOpen = false
                        nearestHydrant = null
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(lagunaLake, 10f)
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
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color(0xFFFF6B35),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Reset to Laguna View",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

// Go to My Location
                Surface(
                    onClick = {
                        isDrawerOpen = false
                        nearestHydrant = null
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
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF5F6368),  // Normal gray color
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Go to My Location",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                // Map Legend
                Text(
                    text = "Map Legend",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                // Legend Item - In Service
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "In Service Hydrant",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Legend Item - Out of Service
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Out of Service Hydrant",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Legend Item - Nearest Hydrant
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Nearest Hydrant",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Footer
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                Text(
                    text = "FireGrid v1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
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

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    scrollState: androidx.compose.foundation.lazy.LazyListState = androidx.compose.foundation.lazy.rememberLazyListState(),
    onMunicipalityClick: (String) -> Unit
) {
    val municipalities = listOf(
        "Alaminos", "Bay", "Biñan", "Cabuyao", "Calamba", "Calauan",
        "Cavinti", "Famy", "Kalayaan", "Liliw", "Los Baños", "Luisiana",
        "Lumban", "Mabitac", "Magdalena", "Majayjay", "Nagcarlan", "Paete",
        "Pagsanjan", "Pakil", "Pangil", "Rizal", "San Pablo", "San Pedro",
        "Santa Cruz", "Santa Maria", "Santa Rosa", "Siniloan", "Victoria"
    )

    // Search state
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    // Filter municipalities - only show those that START with the search query
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
        // Header with title and search button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6B35))
                .systemBarsPadding()
                .padding(16.dp)
        ) {
            if (isSearchActive) {
                // Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search...", color = Color.Gray)
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
                // Title with search button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Laguna Municipalities",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { isSearchActive = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFFF6B35)
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
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF212121),
                fontWeight = FontWeight.Medium
            )
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
    onLogout: () -> Unit,
    onHelpFaqClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onReportProblemClick: () -> Unit,
    onAboutAppClick: () -> Unit,
    onTermsPrivacyClick: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel()

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
        // Header with profile info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6B35))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .systemBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFFF6B35),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "Apply as Admin",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                ProfileMenuItem(
                    title = "Settings",
                    onClick = { }
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