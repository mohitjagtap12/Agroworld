package com.example

import android.widget.Toast
import com.example.network.SessionManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.snapshots.SnapshotStateList

// ------------------ DELIVERY COLOR PALETTE ------------------
val DeliveryPrimary = Color(0xFF2E7D32)       // Forest Green
val DeliverySecondary = Color(0xFF66BB6A)     // Soft Green
val DeliveryAccent = Color(0xFFF9A825)        // Amber/Gold
val DeliveryBackground = Color(0xFFF8FBF7)    // Light agriculture-themed backdrop
val DeliveryCardBg = Color(0xFFFFFFFF)        // Pure White Cards
val DeliveryTextPrimary = Color(0xFF212121)   // Charcoal
val DeliveryTextSecondary = Color(0xFF616161) // Soft Grey
val DeliveryLightBg = Color(0xFFE8F5E9)       // Very light green tint

// ------------------ DATA MODELS ------------------
data class DeliveryOrder(
    val id: String,
    val customerName: String,
    val customerContact: String,
    val sellerName: String,
    val sellerContact: String,
    val pickupVillage: String,
    val pickupAddress: String,
    val deliveryVillage: String,
    val deliveryAddress: String,
    val productDetails: String,
    val deliveryNotes: String,
    val deliveryFee: Double,
    val orderDate: String,
    var status: String // "Assigned", "Picked Up", "Out for Delivery", "Delivered", "Cancelled"
)

data class DeliveryNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String // "New", "Info", "Alert"
)

// ------------------ PORTAL MASTER SCREEN ------------------
@Composable
fun DeliveryPartnerPortalScreen(navController: NavController) {
    val context = LocalContext.current

    // Navigation Screens:
    // "dashboard" (Home), "deliveries" (Assigned Deliveries list), "delivery_details", "history" (Delivery History), "earnings", "notifications", "profile"
    var currentScreen by remember { mutableStateOf("dashboard") }

    // Global Interactive Deliveries State in Memory
    val deliveryOrders = remember { mutableStateListOf<DeliveryOrder>() }

    val notifications = remember { mutableStateListOf<DeliveryNotification>() }

    var selectedOrderId by remember { mutableStateOf("") }

    // Portal Stage: "otp_verification", "registration", "success", "dashboard"
    var currentPortalStage by remember { mutableStateOf("otp_verification") }

    // OTP / Verification States
    var enteredPhone by remember { mutableStateOf("") }
    var enteredOtpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    // Registration Form Inputs
    var regFullName by remember { mutableStateOf("") }
    var regVehicleType by remember { mutableStateOf("Two Wheeler") }
    val regSelectedTalukas = remember { mutableStateListOf<String>() }
    val regSelectedServices = remember { mutableStateListOf<String>() }
    var regPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Dynamic Profile Information for Delivery Partner
    var partnerName by remember { mutableStateOf(SessionManager.getInstance(context).userName.ifEmpty { "Delivery Partner" }) }
    var partnerPhone by remember { mutableStateOf(SessionManager.getInstance(context).userPhone.ifEmpty { "+91 ----------" }) }
    var partnerVehicleType by remember { mutableStateOf("Two Wheeler") }
    var partnerServiceArea by remember { mutableStateOf(SessionManager.getInstance(context).userDistrict.ifEmpty { "Assigned Region" }) }
    var partnerServices by remember { mutableStateOf("Fresh Fruits, Fresh Vegetables, Grains & Pulses, Fertilizers, Pesticides, Seeds") }
    var partnerPhotoUriState by remember { mutableStateOf<Uri?>(null) }

    Scaffold(
        bottomBar = {
            if (currentPortalStage == "dashboard") {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 10.dp,
                    modifier = Modifier.drawBehind {
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                ) {
                NavigationBarItem(
                    selected = currentScreen == "dashboard",
                    onClick = { currentScreen = "dashboard" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeliveryPrimary,
                        indicatorColor = DeliveryPrimary,
                        unselectedIconColor = DeliveryTextSecondary,
                        unselectedTextColor = DeliveryTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "deliveries" || currentScreen == "delivery_details",
                    onClick = { currentScreen = "deliveries" },
                    icon = {
                        val activeCount = deliveryOrders.count { it.status in listOf("Assigned", "Picked Up", "Out for Delivery") }
                        BadgedBox(badge = {
                            if (activeCount > 0) {
                                Badge(containerColor = DeliveryAccent) {
                                    Text(activeCount.toString(), color = DeliveryTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }) {
                            Icon(Icons.Default.LocalShipping, contentDescription = "Deliveries")
                        }
                    },
                    label = { Text("Deliveries", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeliveryPrimary,
                        indicatorColor = DeliveryPrimary,
                        unselectedIconColor = DeliveryTextSecondary,
                        unselectedTextColor = DeliveryTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "history",
                    onClick = { currentScreen = "history" },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeliveryPrimary,
                        indicatorColor = DeliveryPrimary,
                        unselectedIconColor = DeliveryTextSecondary,
                        unselectedTextColor = DeliveryTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "profile",
                    onClick = { currentScreen = "profile" },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeliveryPrimary,
                        indicatorColor = DeliveryPrimary,
                        unselectedIconColor = DeliveryTextSecondary,
                        unselectedTextColor = DeliveryTextSecondary
                    )
                )
            }
        }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val appliedModifier = if (currentPortalStage == "dashboard") {
            Modifier.fillMaxSize().padding(paddingValues)
        } else {
            Modifier.fillMaxSize()
        }

        Box(
            modifier = appliedModifier.background(DeliveryBackground)
        ) {
            AnimatedContent(
                targetState = currentPortalStage,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "portal_stage_transition"
            ) { stage ->
                when (stage) {
                    "otp_verification" -> {
                        DeliveryOtpVerificationView(
                            enteredPhone = enteredPhone,
                            onPhoneChange = { enteredPhone = it },
                            enteredOtpCode = enteredOtpCode,
                            onOtpChange = { enteredOtpCode = it },
                            isOtpSent = isOtpSent,
                            onSendOtp = {
                                isOtpSent = true
                                enteredOtpCode = ""
                                Toast.makeText(context, "Verification code sent successfully!", Toast.LENGTH_SHORT).show()
                            },
                            onVerifyOtp = {
                                if (enteredOtpCode.trim().isNotEmpty()) {
                                    Toast.makeText(context, "Mobile number verified successfully!", Toast.LENGTH_SHORT).show()
                                    partnerPhone = "+91 $enteredPhone"
                                    currentPortalStage = "registration"
                                } else {
                                    Toast.makeText(context, "Please enter verification code", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onBackToRoles = {
                                navController.navigateUp()
                            }
                        )
                    }
                    "registration" -> {
                        DeliveryRegistrationView(
                            fullName = regFullName,
                            onFullNameChange = { regFullName = it },
                            verifiedPhone = partnerPhone,
                            vehicleType = regVehicleType,
                            onVehicleTypeChange = { regVehicleType = it },
                            selectedTalukas = regSelectedTalukas,
                            selectedServices = regSelectedServices,
                            photoUri = regPhotoUri,
                            onPhotoUriChange = { regPhotoUri = it },
                            onSubmit = {
                                partnerName = regFullName
                                partnerVehicleType = regVehicleType
                                partnerServiceArea = if (regSelectedTalukas.isEmpty()) {
                                    "Pune District (All Talukas)"
                                } else {
                                    regSelectedTalukas.joinToString(", ")
                                }
                                partnerServices = if (regSelectedServices.isEmpty()) {
                                    "Fresh Fruits, Fresh Vegetables, Grains & Pulses"
                                } else {
                                    regSelectedServices.joinToString(", ")
                                }
                                partnerPhotoUriState = regPhotoUri
                                currentPortalStage = "success"
                            }
                        )
                    }
                    "success" -> {
                        DeliveryRegistrationSuccessView(
                            onGoToDashboard = {
                                currentPortalStage = "dashboard"
                            }
                        )
                    }
                    "dashboard" -> {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            label = "delivery_partner_portal_transition"
                        ) { target ->
                            when (target) {
                                "dashboard" -> DeliveryDashboardView(
                                    orders = deliveryOrders,
                                    notifications = notifications,
                                    partnerName = partnerName,
                                    partnerPhotoUri = partnerPhotoUriState,
                                    onNavigate = { currentScreen = it },
                                    onSelectOrder = { id ->
                                        selectedOrderId = id
                                        currentScreen = "delivery_details"
                                    }
                                )
                                "deliveries" -> DeliveryAssignedView(
                                    orders = deliveryOrders,
                                    onBack = { currentScreen = "dashboard" },
                                    onSelectOrder = { id ->
                                        selectedOrderId = id
                                        currentScreen = "delivery_details"
                                    }
                                )
                                "delivery_details" -> {
                                    val order = deliveryOrders.find { it.id == selectedOrderId } ?: deliveryOrders.first()
                                    DeliveryDetailsView(
                                        order = order,
                                        onBack = { currentScreen = "deliveries" },
                                        onStatusChange = { newStatus ->
                                            val index = deliveryOrders.indexOfFirst { it.id == order.id }
                                            if (index != -1) {
                                                deliveryOrders[index] = deliveryOrders[index].copy(status = newStatus)
                                                // Raise corresponding notification
                                                notifications.add(0, DeliveryNotification(
                                                    id = "n_" + (4..9999).random(),
                                                    title = "Delivery Status Updated",
                                                    message = "Order ${order.id} is now updated to: $newStatus",
                                                    timestamp = "Just Now",
                                                    type = "Info"
                                                ))
                                                Toast.makeText(context, "Status updated to: $newStatus", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }
                                "history" -> DeliveryHistoryView(
                                    orders = deliveryOrders,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "earnings" -> DeliveryEarningsView(
                                    orders = deliveryOrders,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "notifications" -> DeliveryNotificationsView(
                                    notifications = notifications,
                                    onBack = { currentScreen = "dashboard" },
                                    onClearAll = {
                                        notifications.clear()
                                        Toast.makeText(context, "All notifications cleared.", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                "profile" -> DeliveryProfileView(
                                    partnerName = partnerName,
                                    partnerPhone = partnerPhone,
                                    partnerVehicleType = partnerVehicleType,
                                    partnerServiceArea = partnerServiceArea,
                                    partnerServices = partnerServices,
                                    partnerPhotoUri = partnerPhotoUriState,
                                    onBack = { currentScreen = "dashboard" },
                                    onLogout = {
                                        Toast.makeText(context, "Logging out from AgroWorld Deliveries...", Toast.LENGTH_SHORT).show()
                                        navController.navigate("role_selection") {
                                            popUpTo("dashboard/delivery") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 1. DELIVERY DASHBOARD VIEW ------------------
@Composable
fun DeliveryDashboardView(
    orders: List<DeliveryOrder>,
    notifications: List<DeliveryNotification>,
    partnerName: String,
    partnerPhotoUri: Uri?,
    onNavigate: (String) -> Unit,
    onSelectOrder: (String) -> Unit
) {
    val context = LocalContext.current
    var isDutyOn by remember { mutableStateOf(true) }

    val activeOrders = orders.filter { it.status in listOf("Assigned", "Picked Up", "Out for Delivery") }
    val completedCount = orders.count { it.status == "Delivered" }
    val pendingCount = orders.count { it.status == "Assigned" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // TOP APP BAR / PROFILE GREETING
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(2.dp, DeliveryPrimary, CircleShape)
                            .background(DeliveryLightBg),
                        contentAlignment = Alignment.Center
                    ) {
                        if (partnerPhotoUri != null) {
                            Text("📸", fontSize = 28.sp)
                        } else {
                            Text("🧑‍✈️", fontSize = 32.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ram Ram, $partnerName! 👋",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeliveryTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isDutyOn) DeliveryPrimary else Color.Red)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isDutyOn) "Active Duty (Pune District)" else "Off-Duty (Offline)",
                                fontSize = 12.sp,
                                color = DeliveryTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Duty toggle switch
                    Switch(
                        checked = isDutyOn,
                        onCheckedChange = {
                            isDutyOn = it
                            Toast.makeText(
                                context,
                                if (it) "You are now online to receive delivery requests!" else "Offline. No new orders will be assigned.",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DeliveryPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.LightGray
                        ),
                        modifier = Modifier.scale(0.85f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { onNavigate("notifications") },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    ) {
                        BadgedBox(badge = {
                            if (notifications.isNotEmpty()) {
                                Badge(containerColor = DeliveryAccent) {
                                    Text(notifications.size.toString(), color = DeliveryTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "View Notifications",
                                tint = DeliveryPrimary
                            )
                        }
                    }
                }
            }
        }

        // STATS SUMMARY BANNER
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        DeliveryPrimary.copy(alpha = 0.05f),
                                        DeliverySecondary.copy(alpha = 0.15f)
                                    )
                                )
                            )
                        }
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("TODAY'S CASH & PAYOUT EARNINGS", fontSize = 11.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                                val todayEarnings = orders.filter { it.status == "Delivered" }.sumOf { it.deliveryFee }
                                Text("₹${"%,.2f".format(todayEarnings)}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = DeliveryPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DeliveryPrimary.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Pune Route 🛵", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeliveryPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Completed: $completedCount | Assigned: $pendingCount", fontSize = 12.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                            Text("Details ➔", fontSize = 12.sp, color = DeliveryPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigate("earnings") })
                        }
                    }
                }
            }
        }

        // STATS BLOCKS ROW
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DeliveryStatCard("Today's Runs", completedCount.toString(), "Delivered", DeliveryPrimary, Modifier.weight(1f))
                DeliveryStatCard("Pending", activeOrders.count { it.status == "Assigned" }.toString(), "Wait Pickup", DeliveryAccent, Modifier.weight(1f))
                DeliveryStatCard("In Transit", activeOrders.count { it.status in listOf("Picked Up", "Out for Delivery") }.toString(), "On Bike", Color(0xFF1565C0), Modifier.weight(1f))
            }
        }

        // QUICK ACTIONS SECTION
        item {
            Text(
                text = "Agro-World Delivery Actions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeliveryTextPrimary
            )
        }

        item {
            val actions = listOf(
                Triple("Assigned Run", Icons.Default.DirectionsRun, "deliveries"),
                Triple("Run History", Icons.Default.ReceiptLong, "history"),
                Triple("My Earnings", Icons.Default.CurrencyRupee, "earnings"),
                Triple("Help & Support", Icons.Default.SupportAgent, "profile")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                actions.forEach { (label, icon, route) ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .width(135.dp)
                            .clickable { onNavigate(route) }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(DeliveryPrimary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = label, tint = DeliveryPrimary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeliveryTextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // RECENT DELIVERIES ACTIVITY HEADER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Deliveries Pipeline",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeliveryTextPrimary
                )
                TextButton(
                    onClick = { onNavigate("deliveries") },
                    colors = ButtonDefaults.textButtonColors(contentColor = DeliveryPrimary)
                ) {
                    Text("Manage", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ACTIVE PIPELINE ITEMS
        if (activeOrders.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉 All clear! No pending deliveries.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeliveryPrimary)
                        Text("Toggle online switch to receive automatic assignments.", fontSize = 11.sp, color = DeliveryTextSecondary, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(activeOrders) { order ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectOrder(order.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon based on status
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when (order.status) {
                                        "Assigned" -> DeliveryAccent.copy(alpha = 0.15f)
                                        "Picked Up" -> Color(0xFFE3F2FD)
                                        "Out for Delivery" -> DeliveryLightBg
                                        else -> Color.LightGray.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (order.status) {
                                    "Assigned" -> "📥"
                                    "Picked Up" -> "📦"
                                    "Out for Delivery" -> "🛵"
                                    else -> "🚚"
                                },
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.id,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeliveryTextPrimary
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (order.status) {
                                                "Assigned" -> DeliveryAccent.copy(alpha = 0.15f)
                                                "Picked Up" -> Color(0xFFE3F2FD)
                                                "Out for Delivery" -> DeliveryLightBg
                                                else -> Color.LightGray
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = order.status.uppercase(),
                                        color = when (order.status) {
                                            "Assigned" -> Color(0xFFD84315)
                                            "Picked Up" -> Color(0xFF1565C0)
                                            "Out for Delivery" -> DeliveryPrimary
                                            else -> DeliveryTextSecondary
                                        },
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Pickup: ${order.sellerName} (${order.pickupVillage})",
                                fontSize = 11.sp,
                                color = DeliveryTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "Delivery: ${order.customerName} (${order.deliveryVillage})",
                                fontSize = 11.sp,
                                color = DeliveryTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.productDetails,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeliveryTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "₹${order.deliveryFee}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DeliveryPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryStatCard(
    title: String,
    value: String,
    sub: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = title, fontSize = 11.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sub, fontSize = 10.sp, color = DeliveryTextSecondary)
        }
    }
}

// ------------------ 2. ASSIGNED DELIVERIES VIEW ------------------
@Composable
fun DeliveryAssignedView(
    orders: List<DeliveryOrder>,
    onBack: () -> Unit,
    onSelectOrder: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Pending") } // "Pending", "Picked Up", "Out for Delivery"

    val filteredOrders = orders.filter { order ->
        val matchesSearch = order.id.contains(searchQuery, ignoreCase = true) ||
                order.customerName.contains(searchQuery, ignoreCase = true) ||
                order.pickupVillage.contains(searchQuery, ignoreCase = true) ||
                order.deliveryVillage.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Pending" -> order.status == "Assigned"
            "Picked Up" -> order.status == "Picked Up"
            "Out for Delivery" -> order.status == "Out for Delivery"
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeliveryBackground)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeliveryPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Assigned Runs",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeliveryTextPrimary
            )
        }

        // SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by Order ID, Client or Village...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = DeliveryTextSecondary) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = DeliveryPrimary,
                unfocusedBorderColor = Color(0xFFCBD5E1)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // FILTER CHIPS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Pending", "Picked Up", "Out for Delivery").forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DeliveryPrimary else Color.White)
                        .border(1.dp, if (isSelected) DeliveryPrimary else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else DeliveryTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // RESULTS LIST
        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛵", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No deliveries found in this list", color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                    Text("Try changing filters or searching another taluka.", fontSize = 11.sp, color = DeliveryTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                items(filteredOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Order: ", fontSize = 12.sp, color = DeliveryTextSecondary)
                                    Text(order.id, fontSize = 14.sp, fontWeight = FontWeight.Black, color = DeliveryPrimary)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (order.status) {
                                                "Assigned" -> DeliveryAccent.copy(alpha = 0.15f)
                                                "Picked Up" -> Color(0xFFE3F2FD)
                                                "Out for Delivery" -> DeliveryLightBg
                                                else -> Color.LightGray.copy(alpha = 0.3f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = order.status,
                                        color = when (order.status) {
                                            "Assigned" -> Color(0xFFD84315)
                                            "Picked Up" -> Color(0xFF1565C0)
                                            "Out for Delivery" -> DeliveryPrimary
                                            else -> DeliveryTextSecondary
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Route Timeline
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.dp)) {
                                    Text("🟢", fontSize = 10.sp)
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(28.dp)
                                            .background(Color.LightGray)
                                    )
                                    Text("📍", fontSize = 10.sp)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("PICKUP", fontSize = 9.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                                    Text(order.sellerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                                    Text(order.pickupVillage, fontSize = 11.sp, color = DeliveryTextSecondary)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text("DELIVERY TO", fontSize = 9.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                                    Text(order.customerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                                    Text(order.deliveryVillage, fontSize = 11.sp, color = DeliveryTextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ESTIMATED PAY", fontSize = 9.sp, color = DeliveryTextSecondary)
                                    Text("₹${order.deliveryFee}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DeliveryPrimary)
                                }

                                Button(
                                    onClick = { onSelectOrder(order.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeliveryPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("View Run Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 3. DELIVERY DETAILS VIEW ------------------
@Composable
fun DeliveryDetailsView(
    order: DeliveryOrder,
    onBack: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeliveryBackground)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeliveryPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Run Information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeliveryTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ORDER STATUS CARD
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Delivery Run: ${order.id}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeliveryAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(order.status, color = Color(0xFFE65100), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("PRODUCT SECURED", fontSize = 10.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                        Text(order.productDetails, fontSize = 16.sp, fontWeight = FontWeight.Black, color = DeliveryPrimary)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Assigned Date: ${order.orderDate}",
                            fontSize = 11.sp,
                            color = DeliveryTextSecondary
                        )
                    }
                }
            }

            // PICKUP DETAILS
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🟢 ", fontSize = 14.sp)
                                Text("Farmer / Seller Pickup", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                            }

                            IconButton(
                                onClick = { Toast.makeText(context, "Dialing ${order.sellerContact}...", Toast.LENGTH_SHORT).show() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DeliveryLightBg, CircleShape)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call Seller", tint = DeliveryPrimary, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = order.sellerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeliveryPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = order.pickupAddress,
                            fontSize = 12.sp,
                            color = DeliveryTextSecondary
                        )
                    }
                }
            }

            // DELIVERY DETAILS
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📍 ", fontSize = 14.sp)
                                Text("Customer Destination", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                            }

                            IconButton(
                                onClick = { Toast.makeText(context, "Dialing ${order.customerContact}...", Toast.LENGTH_SHORT).show() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DeliveryLightBg, CircleShape)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call Customer", tint = DeliveryPrimary, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = order.customerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeliveryPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = order.deliveryAddress,
                            fontSize = 12.sp,
                            color = DeliveryTextSecondary
                        )

                        if (order.deliveryNotes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFFDF2), RoundedCornerShape(10.dp))
                                    .border(1.dp, DeliveryAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row {
                                    Text("📝 ", fontSize = 14.sp)
                                    Column {
                                        Text("Driver Instruction Note:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeliveryAccent)
                                        Text(order.deliveryNotes, fontSize = 11.sp, color = DeliveryTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // DELIVERY ACTION WORKFLOW
            item {
                Text(
                    text = "Update Delivery Status",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeliveryTextPrimary
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Please execute each step in the delivery process below sequentially:",
                            fontSize = 11.sp,
                            color = DeliveryTextSecondary
                        )

                        // 1. Accept Delivery
                        Button(
                            onClick = { onStatusChange("Assigned") },
                            enabled = order.status == "Assigned",
                            colors = ButtonDefaults.buttonColors(containerColor = DeliveryAccent, contentColor = DeliveryTextPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("1. Confirm / Accept Job Assignment", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        // 2. Mark as Picked Up
                        Button(
                            onClick = { onStatusChange("Picked Up") },
                            enabled = order.status == "Assigned",
                            colors = ButtonDefaults.buttonColors(containerColor = DeliveryPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("2. Secure Sacks & Mark Picked Up", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        // 3. Mark as Out for Delivery
                        Button(
                            onClick = { onStatusChange("Out for Delivery") },
                            enabled = order.status == "Picked Up",
                            colors = ButtonDefaults.buttonColors(containerColor = DeliveryPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("3. Start Ride & Mark Out for Delivery 🛵", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        // 4. Mark as Delivered
                        Button(
                            onClick = { onStatusChange("Delivered") },
                            enabled = order.status == "Out for Delivery",
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text("4. Handover Sacks & Mark Delivered ✓", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Cancel Option
                        if (order.status != "Delivered" && order.status != "Cancelled") {
                            TextButton(
                                onClick = { onStatusChange("Cancelled") },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Reject / Cancel Run", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 4. DELIVERY HISTORY VIEW ------------------
@Composable
fun DeliveryHistoryView(
    orders: List<DeliveryOrder>,
    onBack: () -> Unit
) {
    var selectedTabState by remember { mutableStateOf(0) } // 0: Completed, 1: Cancelled
    val tabLabels = listOf("Completed Deliveries", "Cancelled Deliveries")

    val filteredOrders = orders.filter { order ->
        if (selectedTabState == 0) {
            order.status == "Delivered"
        } else {
            order.status == "Cancelled"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeliveryBackground)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeliveryPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Delivery History Logs",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeliveryTextPrimary
            )
        }

        // TABS
        TabRow(
            selectedTabIndex = selectedTabState,
            containerColor = Color.White,
            contentColor = DeliveryPrimary
        ) {
            tabLabels.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTabState == index,
                    onClick = { selectedTabState = index },
                    text = { Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // HISTORY LIST
        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No historic orders in this category.", color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                    Text("Complete assigned deliveries to populate history.", fontSize = 11.sp, color = DeliveryTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                items(filteredOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row {
                                    Text("Order ID: ", fontSize = 11.sp, color = DeliveryTextSecondary)
                                    Text(order.id, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selectedTabState == 0) DeliveryLightBg else Color(0xFFFFEBEE))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = order.status.uppercase(),
                                        color = if (selectedTabState == 0) DeliveryPrimary else Color(0xFFC62828),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Customer Name", fontSize = 9.sp, color = DeliveryTextSecondary)
                                    Text(order.customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                                }
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    Text("Delivery Date", fontSize = 9.sp, color = DeliveryTextSecondary)
                                    Text(order.orderDate, fontSize = 13.sp, color = DeliveryTextPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Cargo Particulars", fontSize = 9.sp, color = DeliveryTextSecondary)
                                    Text(order.productDetails, fontSize = 12.sp, color = DeliveryTextPrimary)
                                }
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    Text("Payout Settled", fontSize = 9.sp, color = DeliveryTextSecondary)
                                    Text("₹${order.deliveryFee}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DeliveryPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 5. EARNINGS SUMMARY VIEW ------------------
@Composable
fun DeliveryEarningsView(
    orders: List<DeliveryOrder>,
    onBack: () -> Unit
) {
    val completedDeliveries = orders.filter { it.status == "Delivered" }

    val todayEarnings = completedDeliveries.sumOf { it.deliveryFee }
    val weeklyEarnings = todayEarnings + 2450.0 // Adding previous runs in current weekly cycle
    val monthlyEarnings = weeklyEarnings + 11800.0 // Total monthly projection

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeliveryBackground)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeliveryPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Earnings Summary",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeliveryTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // THREE STATS CARDS BLOCK
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("PAYOUT MATRIX PERIOD", fontSize = 10.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Pune District Partner Account", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeliveryPrimary)

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Today
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TODAY", fontSize = 10.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${"%,.0f".format(todayEarnings)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = DeliveryPrimary)
                                Text("Disbursed", fontSize = 9.sp, color = DeliveryTextSecondary)
                            }
                            // Weekly
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("THIS WEEK", fontSize = 10.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${"%,.0f".format(weeklyEarnings)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = DeliveryAccent)
                                Text("Next Wednesday", fontSize = 9.sp, color = DeliveryTextSecondary)
                            }
                            // Monthly
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("THIS MONTH", fontSize = 10.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${"%,.0f".format(monthlyEarnings)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF1565C0))
                                Text("Estimated Payout", fontSize = 9.sp, color = DeliveryTextSecondary)
                            }
                        }
                    }
                }
            }

            // LIST TITLE
            item {
                Text(
                    text = "Delivery Fee Transactions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeliveryTextPrimary
                )
            }

            // TRANSACTIONS LIST
            if (completedDeliveries.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No successful runs completed today to list payouts.",
                            fontSize = 12.sp,
                            color = DeliveryTextSecondary,
                            modifier = Modifier.padding(18.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(completedDeliveries) { run ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(DeliveryLightBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Paid", tint = DeliveryPrimary, modifier = Modifier.size(18.dp))
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Trip Fee: ${run.id}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeliveryTextPrimary
                                    )
                                    Text(
                                        text = "${run.orderDate} • Route: ${run.deliveryVillage}",
                                        fontSize = 11.sp,
                                        color = DeliveryTextSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+ ₹${run.deliveryFee}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DeliveryPrimary
                                )
                                Text(
                                    text = "Credit Success",
                                    fontSize = 9.sp,
                                    color = DeliveryTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 6. NOTIFICATIONS SCREEN ------------------
@Composable
fun DeliveryNotificationsView(
    notifications: List<DeliveryNotification>,
    onBack: () -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeliveryBackground)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeliveryPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Notification Center",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeliveryTextPrimary
                )
            }

            if (notifications.isNotEmpty()) {
                TextButton(
                    onClick = onClearAll,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFC62828))
                ) {
                    Text("Clear All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // LIST
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your notification tray is empty.", color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
                    Text("We will notify you immediately when new runs are assigned.", fontSize = 11.sp, color = DeliveryTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                items(notifications) { notif ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(
                            1.dp,
                            when (notif.type) {
                                "New" -> DeliveryPrimary.copy(alpha = 0.3f)
                                "Alert" -> Color.Red.copy(alpha = 0.3f)
                                else -> Color(0xFFE2E8F0)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = when (notif.type) {
                                            "New" -> "🎉"
                                            "Alert" -> "⚠️"
                                            else -> "ℹ️"
                                        },
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = notif.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeliveryTextPrimary
                                    )
                                }

                                Text(
                                    text = notif.timestamp,
                                    fontSize = 10.sp,
                                    color = DeliveryTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = notif.message,
                                fontSize = 12.sp,
                                color = DeliveryTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 7. PROFILE VIEW ------------------
@Composable
fun DeliveryProfileView(
    partnerName: String,
    partnerPhone: String,
    partnerVehicleType: String,
    partnerServiceArea: String,
    partnerServices: String,
    partnerPhotoUri: Uri?,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeliveryBackground)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeliveryPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My Profile Hub",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeliveryTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // AVATAR & BASIC DETAILS
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(DeliveryLightBg)
                                .border(3.dp, DeliveryPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (partnerPhotoUri != null) {
                                Text("📸", fontSize = 48.sp)
                            } else {
                                Text("🧑‍✈️", fontSize = 48.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = partnerName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeliveryTextPrimary
                        )

                        Text(
                            text = "AgroWorld Registered Delivery Partner",
                            fontSize = 12.sp,
                            color = DeliveryPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF9C4))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("⭐ 5.0 Rating • OTP Verified", color = Color(0xFFF57F17), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // DETAILED SECTIONS
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Account Verification Details",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeliveryTextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        DeliveryProfileItemRow("Full Name", partnerName, Icons.Default.Person)
                        DeliveryProfileItemRow("Verified Mobile", partnerPhone, Icons.Default.Phone)
                        DeliveryProfileItemRow("Vehicle Type", partnerVehicleType, Icons.Default.LocalShipping)
                        DeliveryProfileItemRow("Service Area", partnerServiceArea, Icons.Default.Place)
                        DeliveryProfileItemRow("Delivery Services", partnerServices, Icons.Default.Eco)
                        DeliveryProfileItemRow("Identity Verified", "Aadhaar Card (OTP Confirmed)", Icons.Default.VerifiedUser)
                        DeliveryProfileItemRow("Status", "Active Duty", Icons.Default.CheckCircle)
                    }
                }
            }

            // HELP & SUPPORT
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "AgroWorld Partner Support",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeliveryTextPrimary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { Toast.makeText(context, "Dialing partner toll-free helpline...", Toast.LENGTH_SHORT).show() }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = "Helpline", tint = DeliveryPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Toll-Free Partner Helpline", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                                Text("1800-419-5555 (Direct Support)", fontSize = 11.sp, color = DeliveryTextSecondary)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { Toast.makeText(context, "Opening help articles & FAQ...", Toast.LENGTH_SHORT).show() }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.HelpOutline, contentDescription = "FAQ", tint = DeliveryPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Delivery Partner FAQs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                                Text("How to handle delayed cash payments, damaged goods", fontSize = 11.sp, color = DeliveryTextSecondary)
                            }
                        }
                    }
                }
            }

            // LOGOUT BUTTON
            item {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Log Out")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Log Out from Deliverer Account", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DeliveryProfileItemRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DeliveryLightBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = DeliveryPrimary, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = label, fontSize = 10.sp, color = DeliveryTextSecondary, fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 13.sp, color = DeliveryTextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}

// ------------------ NEW DELIVERY PARTNER REGISTRATION COMPONENTS ------------------

@Composable
fun DeliveryOtpVerificationView(
    enteredPhone: String,
    onPhoneChange: (String) -> Unit,
    enteredOtpCode: String,
    onOtpChange: (String) -> Unit,
    isOtpSent: Boolean,
    onSendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onBackToRoles: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFF1F8E9), Color(0xFFFFFFFF))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToRoles) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = DeliveryPrimary
                )
            }
            Text(
                text = "Partner Onboarding",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeliveryPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // HERO BRAND ICON
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, DeliveryPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocalShipping,
                    contentDescription = "Delivery Icon",
                    tint = DeliveryPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Join AgroWorld Logistics",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DeliveryTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Deliver fresh crops and critical agri-inputs to farmers & buyers in Pune District.",
                fontSize = 14.sp,
                color = DeliveryTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // MAIN INPUT CARD
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (!isOtpSent) {
                        Text(
                            text = "Verify Your Mobile",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeliveryTextPrimary
                        )

                        OutlinedTextField(
                            value = enteredPhone,
                            onValueChange = { if (it.length <= 10) onPhoneChange(it) },
                            label = { Text("10-Digit Mobile Number") },
                            placeholder = { Text("Enter mobile number") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = "Phone",
                                    tint = DeliveryPrimary
                                )
                            },
                            prefix = { Text("+91 ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeliveryPrimary,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("delivery_phone_input")
                        )

                        Button(
                            onClick = onSendOtp,
                            enabled = enteredPhone.length == 10,
                            colors = ButtonDefaults.buttonColors(containerColor = DeliveryPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("delivery_send_otp_button")
                        ) {
                            Text("Send Verification Code", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "Enter Verification Code",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeliveryTextPrimary
                        )

                        // DEMO OTP BANNER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = "Info", tint = DeliveryPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Demo Verification Code: 123456",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeliveryPrimary
                                )
                            }
                        }

                        OutlinedTextField(
                            value = enteredOtpCode,
                            onValueChange = { onOtpChange(it) },
                            label = { Text("6-Digit OTP Code") },
                            placeholder = { Text("Enter OTP code") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Lock",
                                    tint = DeliveryPrimary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeliveryPrimary,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("delivery_otp_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Didn't receive code?",
                                fontSize = 12.sp,
                                color = DeliveryTextSecondary
                            )
                            Text(
                                text = "Resend OTP",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeliveryPrimary,
                                modifier = Modifier.clickable {
                                    onOtpChange("")
                                    Toast.makeText(context, "OTP code resent!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        Button(
                            onClick = onVerifyOtp,
                            colors = ButtonDefaults.buttonColors(containerColor = DeliveryPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("delivery_verify_otp_button")
                        ) {
                            Text("Verify & Register", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun DeliveryRegistrationView(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    verifiedPhone: String,
    vehicleType: String,
    onVehicleTypeChange: (String) -> Unit,
    selectedTalukas: SnapshotStateList<String>,
    selectedServices: SnapshotStateList<String>,
    photoUri: Uri?,
    onPhotoUriChange: (Uri?) -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoUriChange(uri)
            Toast.makeText(context, "Profile photo attached!", Toast.LENGTH_SHORT).show()
        }
    }

    val availableTalukas = remember {
        listOf(
            "Junnar", "Ambegaon", "Khed", "Maval", "Mulshi", "Velhe",
            "Bhor", "Purandar", "Haveli", "Shirur", "Daund", "Indapur", "Baramati"
        )
    }

    val availableServices = remember {
        listOf(
            "Fresh Fruits", "Fresh Vegetables", "Grains & Pulses",
            "Flowers", "Fertilizers", "Pesticides", "Seeds"
        )
    }

    val isFormValid = fullName.trim().isNotEmpty() && selectedTalukas.isNotEmpty() && selectedServices.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeliveryBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Partner Registration",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeliveryPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // STEP CARD / PROGRESS
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DeliveryLightBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TaskAlt, contentDescription = "Done", tint = DeliveryPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Step 2 of 2: Profile Setup", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeliveryPrimary)
                            Text("Complete details to activate instant delivery duty.", fontSize = 11.sp, color = DeliveryTextSecondary)
                        }
                    }
                }
            }

            // PROFILE PHOTO SELECTION
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(3.dp, DeliveryPrimary, CircleShape)
                            .clickable { photoLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri != null) {
                            Text("📸", fontSize = 48.sp)
                        } else {
                            Text("👤", fontSize = 48.sp)
                        }

                        // FLOATING CAMERA ADD BADGE
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(DeliveryPrimary)
                                .align(Alignment.BottomEnd)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Add Photo", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (photoUri != null) "Profile Photo Attached (Tap to change)" else "Upload Profile Photo (Optional)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeliveryPrimary,
                        modifier = Modifier.clickable { photoLauncher.launch("image/*") }
                    )
                }
            }

            // PERSONAL INFO
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Personal Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = onFullNameChange,
                            label = { Text("Full Name *") },
                            placeholder = { Text("Enter your full name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = DeliveryPrimary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeliveryPrimary,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("delivery_fullname_input")
                        )

                        OutlinedTextField(
                            value = verifiedPhone,
                            onValueChange = {},
                            label = { Text("Mobile Number (Verified)") },
                            enabled = false,
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = DeliveryTextSecondary) },
                            trailingIcon = { Icon(Icons.Default.Verified, contentDescription = "Verified", tint = DeliveryPrimary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0xFFE2E8F0),
                                disabledTextColor = DeliveryTextSecondary,
                                disabledContainerColor = Color(0xFFF1F5F9)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // VEHICLE TYPE SELECTOR
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Vehicle Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Select the vehicle you will use for deliveries *", fontSize = 11.sp, color = DeliveryTextSecondary)

                        Spacer(modifier = Modifier.height(14.dp))

                        val vehicles = listOf(
                            "Two Wheeler" to Icons.Default.TwoWheeler,
                            "Three Wheeler" to Icons.Default.LocalTaxi,
                            "Pickup Van" to Icons.Default.LocalShipping,
                            "Mini Truck" to Icons.Default.LocalShipping,
                            "Tractor with Trolley" to Icons.Default.Agriculture
                        )

                        vehicles.forEach { (type, icon) ->
                            val isSelected = vehicleType == type
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) DeliveryLightBg else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) DeliveryPrimary else Color(0xFFE2E8F0),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onVehicleTypeChange(type) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) DeliveryPrimary else Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = type,
                                        tint = if (isSelected) Color.White else DeliveryTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Text(
                                    text = type,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) DeliveryPrimary else DeliveryTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onVehicleTypeChange(type) },
                                    colors = RadioButtonDefaults.colors(selectedColor = DeliveryPrimary)
                                )
                            }
                        }
                    }
                }
            }

            // SERVICE AREA / TALUKAS
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Service Area (Preferred Talukas)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Select Pune District talukas where you can deliver *", fontSize = 11.sp, color = DeliveryTextSecondary)

                        Spacer(modifier = Modifier.height(14.dp))

                        // Render them in toggleable Checkbox items
                        availableTalukas.forEach { taluka ->
                            val isChecked = selectedTalukas.contains(taluka)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedTalukas.remove(taluka) else selectedTalukas.add(taluka)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        if (isChecked) selectedTalukas.remove(taluka) else selectedTalukas.add(taluka)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = DeliveryPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "$taluka Taluka", fontSize = 14.sp, color = DeliveryTextPrimary)
                            }
                        }
                    }
                }
            }

            // DELIVERY SERVICES
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Delivery Services", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeliveryTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("What products can you handle and deliver? *", fontSize = 11.sp, color = DeliveryTextSecondary)

                        Spacer(modifier = Modifier.height(14.dp))

                        availableServices.forEach { service ->
                            val isChecked = selectedServices.contains(service)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isChecked) DeliveryPrimary.copy(alpha = 0.08f) else Color.Transparent)
                                    .clickable {
                                        if (isChecked) selectedServices.remove(service) else selectedServices.add(service)
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        if (isChecked) selectedServices.remove(service) else selectedServices.add(service)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = DeliveryPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = service,
                                    fontSize = 14.sp,
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isChecked) DeliveryPrimary else DeliveryTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // SUBMIT BUTTON & WARNINGS
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isFormValid) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Required", tint = Color(0xFF856404), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Please enter your name, and select at least 1 Taluka & 1 Delivery Service.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF856404)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onSubmit,
                        enabled = isFormValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeliveryPrimary,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("delivery_create_account_button")
                    ) {
                        Icon(Icons.Default.AssignmentInd, contentDescription = "Register")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Create Delivery Partner Account", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryRegistrationSuccessView(
    onGoToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // SUCCESS ILLUSTRATION (Canvas styled green circles + truck)
        Box(
            modifier = Modifier
                .size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ripple background circles using Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFFE8F5E9),
                    radius = size.maxDimension / 2
                )
                drawCircle(
                    color = Color(0xFFC8E6C9),
                    radius = size.maxDimension * 0.4f
                )
            }

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(DeliveryPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Welcome to AgroWorld!",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DeliveryPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your Delivery Partner account has been created successfully.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = DeliveryTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "You can now receive Crop Deliveries and Agri Store Deliveries to begin earning.",
            fontSize = 13.sp,
            color = DeliveryTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onGoToDashboard,
            colors = ButtonDefaults.buttonColors(containerColor = DeliveryPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(54.dp)
                .testTag("delivery_go_to_dashboard_button")
        ) {
            Text("Go to Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = "Forward")
        }
    }
}
