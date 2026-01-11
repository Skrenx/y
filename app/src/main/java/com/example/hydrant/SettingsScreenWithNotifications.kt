package com.example.hydrant

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

/**
 * Updated SettingsScreen with WORKING Notifications
 * Replaces the existing SettingsScreen in MainActivity.kt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenWithNotifications(
    onBack: () -> Unit,
    onAddAdminClick: () -> Unit,
    onRemoveAdminClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: ""
    val firestore = Firebase.firestore
    val scope = rememberCoroutineScope()

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var isCheckingAdmin by remember { mutableStateOf(true) }

    // Notification states
    var notificationsEnabled by remember { mutableStateOf(true) }
    var emergencyAlertsEnabled by remember { mutableStateOf(true) }
    var hydrantUpdatesEnabled by remember { mutableStateOf(true) }
    var hasNotificationPermission by remember {
        mutableStateOf(NotificationHelper.hasNotificationPermission(context))
    }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }

    // Permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            notificationsEnabled = true
            // Initialize notifications when permission is granted
            NotificationHelper.initializeNotificationChannels(context)
            NotificationHelper.subscribeToEmergencyAlerts()
            NotificationHelper.subscribeToHydrantUpdates()

            // Show test notification
            NotificationHelper.showNotification(
                context = context,
                title = "🔔 Notifications Enabled!",
                message = "You will now receive FireGrid alerts and updates.",
                channelId = NotificationHelper.CHANNEL_GENERAL
            )
        }
    }

    // Load notification preferences
    LaunchedEffect(Unit) {
        val prefs = NotificationHelper.getNotificationPreferences(context)
        notificationsEnabled = prefs.notificationsEnabled
        emergencyAlertsEnabled = prefs.emergencyEnabled
        hydrantUpdatesEnabled = prefs.hydrantUpdatesEnabled

        // Initialize notification channels
        NotificationHelper.initializeNotificationChannels(context)
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

    // Notification Settings Dialog
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
                                    NotificationHelper.saveNotificationPreferences(
                                        context = context,
                                        notificationsEnabled = enabled,
                                        emergencyEnabled = emergencyAlertsEnabled,
                                        hydrantUpdatesEnabled = hydrantUpdatesEnabled
                                    )
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
                                if (enabled) {
                                    NotificationHelper.subscribeToEmergencyAlerts()
                                } else {
                                    NotificationHelper.unsubscribeFromTopic("emergency_alerts")
                                    NotificationHelper.unsubscribeFromTopic("fire_incidents")
                                }
                                NotificationHelper.saveNotificationPreferences(
                                    context = context,
                                    notificationsEnabled = notificationsEnabled,
                                    emergencyEnabled = enabled,
                                    hydrantUpdatesEnabled = hydrantUpdatesEnabled
                                )
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
                                if (enabled) {
                                    NotificationHelper.subscribeToHydrantUpdates()
                                } else {
                                    NotificationHelper.unsubscribeFromTopic("hydrant_updates")
                                }
                                NotificationHelper.saveNotificationPreferences(
                                    context = context,
                                    notificationsEnabled = notificationsEnabled,
                                    emergencyEnabled = emergencyAlertsEnabled,
                                    hydrantUpdatesEnabled = enabled
                                )
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
                                        // Open app settings
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
                            NotificationHelper.showNotification(
                                context = context,
                                title = "🧪 Test Notification",
                                message = "This is a test notification from FireGrid!",
                                channelId = NotificationHelper.CHANNEL_GENERAL
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
            // Account Settings Section
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 8.dp)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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

            // Admin Settings Section (only for admins)
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
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

            // App Settings Section
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                                        "Tap to enable",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (notificationsEnabled && hasNotificationPermission)
                                        Color(0xFF4CAF50)
                                    else
                                        Color.Gray
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                textAlign = TextAlign.Center
            )
        }
    }
}