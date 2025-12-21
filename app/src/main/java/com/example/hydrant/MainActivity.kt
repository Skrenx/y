package com.example.hydrant

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    // Using 5-day forecast API to get daily min/max temperatures
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
        // Get today's date to filter forecast items
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        // Extract all forecast items
        val listPattern = """"list":\[(.*?)\]""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val listMatch = listPattern.find(json) ?: return null
        val listContent = listMatch.groupValues[1]

        // Split by objects (each forecast entry)
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
            // Check if this entry is for today or tomorrow (to get full range)
            val dateRegex = """:\s*"([^"]+)"""".toRegex()
            val dateMatch = dateRegex.find(entry)
            val entryDate = dateMatch?.groupValues?.get(1) ?: continue

            // Only process today's and tomorrow's first few forecasts
            if (!entryDate.startsWith(today)) {
                if (count > 0) break // Stop after processing today's data
                continue
            }

            // Extract all temperature values from this forecast entry
            val tempRegex = """"temp":\s*([\d.]+)""".toRegex()
            val tempMatches = tempRegex.findAll(entry).toList()

            for (tempMatch in tempMatches) {
                val temp = tempMatch.groupValues[1].toDoubleOrNull() ?: continue
                minTemp = minOf(minTemp, temp)
                maxTemp = maxOf(maxTemp, temp)
                if (tempCount == 0) currentTemp = temp
                tempCount++
            }

            // Extract temp_min
            val tempMinRegex = """"temp_min":\s*([\d.]+)""".toRegex()
            tempMinRegex.findAll(entry).forEach { match ->
                val tMin = match.groupValues[1].toDoubleOrNull()
                if (tMin != null) minTemp = minOf(minTemp, tMin)
            }

            // Extract temp_max
            val tempMaxRegex = """"temp_max":\s*([\d.]+)""".toRegex()
            tempMaxRegex.findAll(entry).forEach { match ->
                val tMax = match.groupValues[1].toDoubleOrNull()
                if (tMax != null) maxTemp = maxOf(maxTemp, tMax)
            }

            // Extract humidity (first occurrence in this entry)
            val humidityRegex = """"humidity":\s*(\d+)""".toRegex()
            val humidity = humidityRegex.find(entry)?.groupValues?.get(1)?.toIntOrNull() ?: 0

            // Extract wind speed (first occurrence in this entry)
            val windRegex = """"speed":\s*([\d.]+)""".toRegex()
            val windSpeed = windRegex.find(entry)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0

            // Extract weather condition (only from first entry)
            if (count == 0) {
                val weatherRegex = """"main":\s*"([^"]+)"""".toRegex()
                condition = weatherRegex.find(entry)?.groupValues?.get(1) ?: "Clear"
            }

            avgHumidity += humidity
            avgWindSpeed += windSpeed
            count++
        }

        if (count == 0 || minTemp == Double.MAX_VALUE || maxTemp == Double.MIN_VALUE) {
            // Fallback: use first available forecast data
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

// Helper function to validate password
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
                isLoggedIn = true
            },
            onBackToLogin = { showSignUp = false }
        )
        else -> LoginScreen(
            onLoginSuccess = {
                isLoggedIn = true
            },
            onNavigateToSignUp = { showSignUp = true }
        )
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToSignUp: () -> Unit) {
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
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
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
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
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
                    enabled = !uiState.isLoading
                )

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
fun SignUpScreen(onSignUpSuccess: () -> Unit, onBackToLogin: () -> Unit) {
    val viewModel: AuthViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var localError by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            val user = FirebaseAuth.getInstance().currentUser
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                onSignUpSuccess()
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
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

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            localError = ""
                        },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            focusedLabelColor = Color(0xFFFF6B35)
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            autoCorrect = false
                        )
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

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
                        enabled = !uiState.isLoading
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

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
                        enabled = !uiState.isLoading
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
                        enabled = !uiState.isLoading
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
                                name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                    localError = "Please fill in all fields"
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
                                        viewModel.signUp(email, password)
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

    val favoritesScrollState = androidx.compose.foundation.lazy.rememberLazyListState()

    if (selectedMunicipality != null) {
        MunicipalityDetailScreen(
            municipalityName = selectedMunicipality!!,
            onBack = { selectedMunicipality = null }
        )
    } else {
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
                    AppDestinations.HOME -> MapScreen(modifier = Modifier.padding(innerPadding))
                    AppDestinations.FAVORITES -> FavoritesScreen(
                        modifier = Modifier.padding(innerPadding),
                        scrollState = favoritesScrollState,
                        onMunicipalityClick = { municipality ->
                            selectedMunicipality = municipality
                        }
                    )
                    AppDestinations.PROFILE -> ProfileScreen(
                        modifier = Modifier.padding(innerPadding),
                        userName = userName,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunicipalityDetailScreen(
    municipalityName: String,
    onBack: () -> Unit
) {
    var weatherData by remember { mutableStateOf<DailyWeather?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    var showAddHydrantDialog by remember { mutableStateOf(false) }
    var hydrantAdded by remember { mutableStateOf(false) }

    LaunchedEffect(municipalityName) {
        isLoading = true
        errorMessage = null
        scope.launch {
            val data = fetchWeatherData(municipalityName)
            if (data != null) {
                weatherData = data
            } else {
                errorMessage = "Unable to fetch weather data"
            }
            isLoading = false
        }
    }

    val temperature = weatherData?.temp?.toInt() ?: 65
    val condition = weatherData?.condition ?: "Loading..."
    val tempMin = weatherData?.tempMin?.toInt() ?: 63
    val tempMax = weatherData?.tempMax?.toInt() ?: 66
    // Show daily low/high forecast temperatures
    val lowHigh = "$tempMin° / $tempMax°"
    val humidity = "${weatherData?.humidity ?: 0}%"
    // Convert wind speed from m/s to km/h (multiply by 3.6)
    val windSpeedKmh = ((weatherData?.windSpeed ?: 0.0) * 3.6).toInt()
    val windSpeed = "$windSpeedKmh km/h"
    val outOfService = remember { 0 }
    val inService = remember { 0 }

    if (showAddHydrantDialog) {
        AddFireHydrantDialog(
            onDismiss = { showAddHydrantDialog = false },
            onConfirm = {
                hydrantAdded = true
                showAddHydrantDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
                ),
                modifier = Modifier.systemBarsPadding()
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

                        if (isLoading) {
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

            item {
                Button(
                    onClick = { showAddHydrantDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hydrantAdded) Color(0xFF4CAF50) else Color(0xFF81C784)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (hydrantAdded) "Added ✓" else "Add Fire Hydrant",
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
                        onClick = { },
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
                        onClick = { },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFireHydrantDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var hydrantName by remember { mutableStateOf("") }
    var exactLocation by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var typeColor by remember { mutableStateOf("") }
    var serviceStatus by remember { mutableStateOf("In Service") }
    var remarks by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add Fire Hydrant",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column {
                        Text(
                            "Hydrant Name",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = hydrantName,
                            onValueChange = { hydrantName = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter hydrant name") },
                            singleLine = true
                        )
                    }
                }

                item {
                    Column {
                        Text(
                            "Exact Location",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = exactLocation,
                            onValueChange = { exactLocation = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter exact location") },
                            singleLine = true
                        )
                    }
                }

                item {
                    Column {
                        Text(
                            "Coordinates",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = latitude,
                                onValueChange = { latitude = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Latitude") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                value = longitude,
                                onValueChange = { longitude = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Longitude") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            "Type/Color",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = typeColor,
                            onValueChange = { typeColor = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter type/color") },
                            singleLine = true
                        )
                    }
                }

                item {
                    Column {
                        Text(
                            "Service Status",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
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
                                }
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
                }

                item {
                    Column {
                        Text(
                            "Remarks",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter remarks (optional)") },
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF81C784)
                )
            ) {
                Text("Add Fire Hydrant")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lagunaLake = LatLng(14.3500, 121.2500)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lagunaLake, 10f)
    }

    var isMyLocationEnabled by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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

    Box(modifier = modifier.fillMaxSize()) {
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
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 16.dp),
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6B35))
                .systemBarsPadding()
                .padding(16.dp)
        ) {
            Text(
                text = "Laguna Municipalities",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(municipalities) { municipality ->
                MunicipalityButton(
                    name = municipality,
                    onClick = { onMunicipalityClick(municipality) }
                )
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

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, userName: String, onLogout: () -> Unit) {
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
    ) {
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
                    title = "Logout",
                    onClick = {
                        viewModel.signOut()
                        onLogout()
                    },
                    isLogout = true
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
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
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox)
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    HYDRANTTheme {
        LoginScreen(onLoginSuccess = {}, onNavigateToSignUp = {})
    }
}