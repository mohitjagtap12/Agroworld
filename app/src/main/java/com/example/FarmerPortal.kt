package com.example

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.navigation.NavController
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

// ------------------ COLOR PALETTE DEFINITION ------------------
val FarmerPrimary = Color(0xFF2E7D32)      // Forest Green
val FarmerSecondary = Color(0xFF66BB6A)    // Secondary Green
val FarmerAccent = Color(0xFFF9A825)       // Accent Amber Gold
val FarmerBackground = Color(0xFFF8FBF7)   // Minty Soft Background
val FarmerCardBg = Color(0xFFFFFFFF)       // White
val FarmerTextPrimary = Color(0xFF212121)  // Dark Grey Text
val FarmerTextSecondary = Color(0xFF616161)// Medium Grey Text
val FarmerMuted = Color(0xFFB0BEC5)        // Slate Gray Muted
val FarmerSuccess = Color(0xFF4CAF50)

// ------------------ MODELS ------------------
data class FarmerCrop(
    val id: String,
    val name: String,
    val category: String,
    val quantity: Double,
    val unit: String,
    val price: Double,
    val harvestDate: String,
    val description: String,
    val status: String, // "Available", "Sold"
    val imagePreset: String
)

data class FarmerOrder(
    val id: String,
    val customerName: String,
    val customerPhone: String,
    val cropName: String,
    val quantity: Double,
    val unit: String,
    val totalPrice: Double,
    val date: String,
    val status: String, // "Pending", "Accepted", "Completed", "Rejected"
    val deliveryAddress: String
)

data class StoreProduct(
    val id: String,
    val name: String,
    val brand: String,
    val category: String, // "Fertilizers", "Pesticides", "Seeds", "Organic", "Tools"
    val price: Double,
    val stock: Int,
    val rating: Double,
    val description: String,
    val imagePreset: String
)

data class GovScheme(
    val id: String,
    val name: String,
    val description: String,
    val eligibility: String,
    val benefit: String
)

data class ChatMessage(
    val sender: String, // "Me", "Partner"
    val text: String,
    val timestamp: String,
    val hasImage: Boolean = false
)

data class ChatSession(
    val id: String,
    val partnerName: String,
    val partnerRole: String, // "Broker", "Customer"
    val lastMsg: String,
    val timestamp: String,
    val messages: List<ChatMessage>
)

// Preset Crops with beautiful symbols for display
val CROP_PRESETS = listOf(
    "Pune Red Onions" to "🧅",
    "Alphonso Mango" to "🥭",
    "Indrayani Rice" to "🌾",
    "Pune Green Chillies" to "🌶️",
    "Sugar Cane" to "🎋",
    "Organic Soybean" to "🌱"
)

// ------------------ MASTER STATE CONTROLLER ------------------
@Composable
fun FarmerPortalScreen(navController: NavController) {
    val context = LocalContext.current

    // Navigation Screens within Farmer Module
    // "dashboard", "add_crop", "my_crops", "orders", "order_details", "agri_store", "gov_schemes", "weather", "chat_list", "chat_detail", "profile"
    var currentSubScreen by remember { mutableStateOf("dashboard") }

    // Dynamic Lists inside memory
    val cropsList = remember {
        mutableStateListOf(
            FarmerCrop("c1", "Pune Red Onions", "Vegetables", 12.0, "Quintal", 1850.0, "12 July 2026", "Grown with organic compost in Haveli taluka. Fully sorted and graded.", "Available", "🧅"),
            FarmerCrop("c2", "Alphonso Mango", "Fruits", 80.0, "Dozen", 450.0, "20 May 2026", "Premium Devgad clone Alphonso harvest from Pune hill orchards.", "Available", "🥭"),
            FarmerCrop("c3", "Indrayani Scented Rice", "Grains", 25.0, "Quintal", 4200.0, "05 June 2026", "Traditional Indrayani highly fragrant rice from Maval area.", "Sold", "🌾"),
            FarmerCrop("c4", "Sugar Cane", "Cash Crop", 4.0, "Ton", 3200.0, "18 July 2026", "High sugar-yield cane variety ready for crushing mills.", "Available", "🎋")
        )
    }

    val ordersList = remember {
        mutableStateListOf(
            FarmerOrder("ord1", "Suresh Mehta", "+91 98451 23091", "Pune Red Onions", 5.0, "Quintal", 9250.0, "17 July 2026", "Pending", "Wagholi Wholesale Center, Pune"),
            FarmerOrder("ord2", "Sunil Deshmukh (Broker)", "+91 94220 84521", "Alphonso Mango", 20.0, "Dozen", 9000.0, "15 July 2026", "Accepted", "Market Yard Gate No. 4, Pune"),
            FarmerOrder("ord3", "Aniket Phadke", "+91 91223 45678", "Indrayani Scented Rice", 10.0, "Quintal", 42000.0, "10 July 2026", "Completed", "Kothrud Organic Depot, Pune")
        )
    }

    // Agri Store mock database
    val storeProducts = remember {
        listOf(
            StoreProduct("p1", "Mahadhan 10:26:26 NPK Fertilizer", "Mahadhan", "Fertilizers", 1150.0, 45, 4.8, "High-efficiency balanced nutrients perfect for onion & sugarcane crops.", "🧪"),
            StoreProduct("p2", "Neem Bark Natural Pesticide 1L", "EcoShield", "Pesticides", 340.0, 12, 4.5, "100% cold-pressed organic leaf neem oil concentrate to control pests naturally.", "🍃"),
            StoreProduct("p3", "Premium Indrayani Seeds 5Kg", "MahaSeeds", "Seeds", 680.0, 80, 4.9, "High germination rate scented paddy seeds certified by MAHABEEJ.", "🌾"),
            StoreProduct("p4", "Modern Heavy Duty Sickle", "Tata Agrico", "Farming Tools", 250.0, 30, 4.2, "Ergonomic high-carbon steel farm cutting sickle with heavy-duty wooden grip.", "🛠️"),
            StoreProduct("p5", "Bayer Confidor Insecticide", "Bayer", "Pesticides", 490.0, 25, 4.7, "Powerful protection against sucking pests on vegetables and cash crops.", "🐛"),
            StoreProduct("p6", "Bio-Organic Vermicompost 10Kg", "Pune Bio-Compost", "Organic", 350.0, 150, 4.9, "Enriched with cow dung and microbial cultures for rich soil conditioning.", "🪱")
        )
    }

    // Active Cart
    var cartItemsCount by remember { mutableStateOf(0) }

    // Selected Order for Details page
    var selectedOrderId by remember { mutableStateOf("ord1") }

    // Active Chat Session
    var selectedChatPartnerId by remember { mutableStateOf("chat1") }

    val chatSessions = remember {
        mutableStateListOf(
            ChatSession(
                "chat1", "Sunil Deshmukh", "Broker", "I can accept the Basmati delivery tomorrow morning.", "10:30 AM",
                listOf(
                    ChatMessage("Partner", "Hello Patil Saheb, is the Alphonso lot ready?", "Yesterday"),
                    ChatMessage("Me", "Yes Sunil, 80 dozens are graded and packed.", "Yesterday"),
                    ChatMessage("Partner", "Great, let me dispatch a mini-truck. I can accept the delivery tomorrow morning.", "10:30 AM")
                )
            ),
            ChatSession(
                "chat2", "Suresh Mehta", "Customer", "Please share the weight bridge slip of the onions.", "Yesterday",
                listOf(
                    ChatMessage("Partner", "Sir, looking for fresh Red Onions.", "Yesterday"),
                    ChatMessage("Me", "Available at ₹1850 per quintal. Outstanding quality.", "Yesterday"),
                    ChatMessage("Partner", "Please share the weight bridge slip of the onions.", "Yesterday")
                )
            )
        )
    }

    // Government Schemes database
    val schemes = listOf(
        GovScheme("s1", "PM Kisan Samman Nidhi", "Direct income support of ₹6,000 per year in three equal installments to small landholding farmer families.", "Landholding farmers up to 2 hectares.", "₹6,000 yearly directly into bank accounts."),
        GovScheme("s2", "MahaDBT Farmer Equipment Subsidy", "State subsidy program offering up to 50% discount on tractors, rotavators, and micro-irrigation sets in Maharashtra.", "Farmers registered on MahaDBT portal with active land records.", "30% to 50% cash subsidy on agricultural equipment."),
        GovScheme("s3", "Pradhan Mantri Fasal Bima Yojana", "Comprehensive crop risk insurance cover against natural fire, lightning, dry spells, pests, and local calamities.", "All farmers growing notified crops in notified areas.", "Premium subsidized up to 90% by Central & State Gov.")
    )

    // Current Edit/Add Temporary variables
    var editCropId by remember { mutableStateOf<String?>(null) } // if not null, we are editing this crop

    // Portal Stage: "otp_verification", "registration", "success", "dashboard"
    var currentPortalStage by remember { mutableStateOf("otp_verification") }

    // OTP / Verification States
    var enteredPhone by remember { mutableStateOf("") }
    var enteredOtpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    // Registration Form Inputs
    var regFullName by remember { mutableStateOf("") }
    var regVillage by remember { mutableStateOf("") }
    var regTaluka by remember { mutableStateOf("Baramati") }
    var regOrganicFarmer by remember { mutableStateOf("Yes") } // "Yes" or "No"
    var regSelectedAvatar by remember { mutableStateOf("🧑‍🌾") }
    val regSelectedCategories = remember { mutableStateListOf<String>() }

    // Profile States
    var farmerName by remember { mutableStateOf("Ramesh Patil") }
    var farmLocation by remember { mutableStateOf("Baramati, Pune District") }
    var farmSize by remember { mutableStateOf("5.5 Acres") }
    var primaryCropType by remember { mutableStateOf("Onions & Sugarcane") }
    var appLanguage by remember { mutableStateOf("Marathi (मराठी)") }

    Scaffold(
        bottomBar = {
            // Only show Bottom Navigation once they have completed the registration flow and are in the main dashboard
            if (currentPortalStage == "dashboard") {
                NavigationBar(
                    containerColor = FarmerBackground,
                    tonalElevation = 8.dp,
                    modifier = Modifier.drawBehind {
                        drawLine(
                            color = Color(0xFFE0E0E0),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                ) {
                    NavigationBarItem(
                        selected = currentSubScreen == "dashboard",
                        onClick = { currentSubScreen = "dashboard" },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentSubScreen == "my_crops" || currentSubScreen == "add_crop",
                        onClick = { currentSubScreen = "my_crops" },
                        icon = { Icon(imageVector = Icons.Default.Eco, contentDescription = "My Crops") },
                        label = { Text("My Crops", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentSubScreen == "agri_store",
                        onClick = { currentSubScreen = "agri_store" },
                        icon = {
                            BadgedBox(badge = {
                                if (cartItemsCount > 0) {
                                    Badge(containerColor = FarmerAccent) {
                                        Text(cartItemsCount.toString(), color = FarmerTextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Storefront, contentDescription = "Agri Store")
                            }
                        },
                        label = { Text("Agri Store", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentSubScreen == "orders" || currentSubScreen == "order_details",
                        onClick = { currentSubScreen = "orders" },
                        icon = { Icon(imageVector = Icons.Default.ListAlt, contentDescription = "Orders") },
                        label = { Text("Orders", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentSubScreen == "profile",
                        onClick = { currentSubScreen = "profile" },
                        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        )
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FarmerBackground)
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentPortalStage,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "farmer_registration_stages_transition"
            ) { stage ->
                when (stage) {
                    "otp_verification" -> {
                        FarmerOtpVerificationView(
                            phone = enteredPhone,
                            onPhoneChange = { enteredPhone = it },
                            otpCode = enteredOtpCode,
                            onOtpChange = { enteredOtpCode = it },
                            isOtpSent = isOtpSent,
                            onSendOtp = {
                                if (enteredPhone.length == 10 && enteredPhone.all { it.isDigit() }) {
                                    isOtpSent = true
                                    Toast.makeText(context, "OTP code 1234 sent to +91 $enteredPhone successfully!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onVerifyOtp = {
                                if (enteredOtpCode == "1234" || enteredOtpCode == "123456" || enteredOtpCode == "0000") {
                                    currentPortalStage = "registration"
                                    Toast.makeText(context, "Mobile number verified successfully! ✓", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Invalid OTP code. Try \"1234\" for simulation.", Toast.LENGTH_LONG).show()
                                }
                            },
                            onCancel = {
                                navController.navigate("role_selection") {
                                    popUpTo("dashboard/farmer") { inclusive = true }
                                }
                            }
                        )
                    }
                    "registration" -> {
                        FarmerRegistrationFormView(
                            fullName = regFullName,
                            onFullNameChange = { regFullName = it },
                            verifiedPhone = enteredPhone.ifEmpty { "9876543210" },
                            village = regVillage,
                            onVillageChange = { regVillage = it },
                            selectedTaluka = regTaluka,
                            onTalukaChange = { regTaluka = it },
                            selectedCategories = regSelectedCategories,
                            isOrganic = regOrganicFarmer,
                            onOrganicChange = { regOrganicFarmer = it },
                            selectedAvatar = regSelectedAvatar,
                            onAvatarChange = { regSelectedAvatar = it },
                            onCreateAccount = {
                                if (regFullName.isBlank()) {
                                    Toast.makeText(context, "Please enter your Full Name", Toast.LENGTH_SHORT).show()
                                } else if (regVillage.isBlank()) {
                                    Toast.makeText(context, "Please enter your Village name", Toast.LENGTH_SHORT).show()
                                } else if (regSelectedCategories.isEmpty()) {
                                    Toast.makeText(context, "Please select at least one Crop Category", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Set actual profile states based on registered details
                                    farmerName = regFullName
                                    farmLocation = "$regVillage, $regTaluka"
                                    primaryCropType = regSelectedCategories.joinToString(", ")
                                    currentPortalStage = "success"
                                }
                            }
                        )
                    }
                    "success" -> {
                        FarmerRegistrationSuccessView(
                            fullName = regFullName,
                            onProceedToDashboard = {
                                currentPortalStage = "dashboard"
                            }
                        )
                    }
                    "dashboard" -> {
                        AnimatedContent(
                            targetState = currentSubScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            label = "farmer_screen_transition"
                        ) { subScreen ->
                            when (subScreen) {
                                "dashboard" -> FarmerDashboardView(
                                    farmerName = farmerName,
                                    farmLocation = farmLocation,
                                    cropsList = cropsList,
                                    ordersList = ordersList,
                                    onNavigate = { screen -> currentSubScreen = screen },
                                    onSelectOrder = { ordId ->
                                        selectedOrderId = ordId
                                        currentSubScreen = "order_details"
                                    }
                                )
                                "add_crop" -> AddCropView(
                                    editCropId = editCropId,
                                    cropsList = cropsList,
                                    onBack = {
                                        editCropId = null
                                        currentSubScreen = "my_crops"
                                    }
                                )
                                "my_crops" -> MyCropsView(
                                    cropsList = cropsList,
                                    onEditCrop = { id ->
                                        editCropId = id
                                        currentSubScreen = "add_crop"
                                    },
                                    onAddCrop = {
                                        editCropId = null
                                        currentSubScreen = "add_crop"
                                    }
                                )
                                "orders" -> OrdersView(
                                    ordersList = ordersList,
                                    onSelectOrder = { ordId ->
                                        selectedOrderId = ordId
                                        currentSubScreen = "order_details"
                                    }
                                )
                                "order_details" -> OrderDetailsView(
                                    orderId = selectedOrderId,
                                    ordersList = ordersList,
                                    onBack = { currentSubScreen = "orders" }
                                )
                                "agri_store" -> AgriStoreView(
                                    products = storeProducts,
                                    cartCount = cartItemsCount,
                                    onUpdateCart = { cartItemsCount = it }
                                )
                                "gov_schemes" -> GovSchemesView(
                                    schemes = schemes,
                                    onBack = { currentSubScreen = "dashboard" }
                                )
                                "weather" -> WeatherView(
                                    onBack = { currentSubScreen = "dashboard" }
                                )
                                "broker_requests" -> FarmerBrokerRequestsScreen(
                                    onBack = { currentSubScreen = "dashboard" },
                                    onContactBroker = { brokerName ->
                                        val sId = "chat_broker_" + brokerName.replace(" ", "_")
                                        if (chatSessions.none { it.id == sId }) {
                                            chatSessions.add(0, ChatSession(
                                                id = sId,
                                                partnerName = brokerName,
                                                partnerRole = "Broker",
                                                lastMsg = "Inquiry regarding bulk requirement on Board",
                                                timestamp = "Just Now",
                                                messages = listOf(
                                                    ChatMessage("Me", "Ram Ram, I saw your bulk requirement for crops on the AgroWorld Broker Requests Board. Is it still open?", "Just Now")
                                                )
                                            ))
                                        }
                                        selectedChatPartnerId = sId
                                        currentSubScreen = "chat_detail"
                                    }
                                )
                                "chat_list" -> ChatListView(
                                    chatSessions = chatSessions,
                                    onSelectChat = { id ->
                                        selectedChatPartnerId = id
                                        currentSubScreen = "chat_detail"
                                    },
                                    onBack = { currentSubScreen = "dashboard" }
                                )
                                "chat_detail" -> ChatDetailView(
                                    sessionId = selectedChatPartnerId,
                                    chatSessions = chatSessions,
                                    onBack = { currentSubScreen = "chat_list" }
                                )
                                "profile" -> ProfileView(
                                    name = farmerName,
                                    onNameChange = { farmerName = it },
                                    location = farmLocation,
                                    onLocationChange = { farmLocation = it },
                                    farmSize = farmSize,
                                    onFarmSizeChange = { farmSize = it },
                                    cropType = primaryCropType,
                                    onCropTypeChange = { primaryCropType = it },
                                    appLanguage = appLanguage,
                                    onLanguageChange = { appLanguage = it },
                                    onLogout = {
                                        Toast.makeText(context, "Logging out of AgroWorld...", Toast.LENGTH_SHORT).show()
                                        navController.navigate("role_selection") {
                                            popUpTo("dashboard/farmer") { inclusive = true }
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

// ------------------ 1. FARMER DASHBOARD SCREEN ------------------
@Composable
fun FarmerDashboardView(
    farmerName: String,
    farmLocation: String,
    cropsList: List<FarmerCrop>,
    ordersList: List<FarmerOrder>,
    onNavigate: (String) -> Unit,
    onSelectOrder: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // TOP APP BAR REPLACEMENT IN-BODY FOR VISUAL FREEDOM
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile picture avatar simulation with circular initials card
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(FarmerPrimary.copy(alpha = 0.15f))
                            .border(2.dp, FarmerPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (farmerName.isNotEmpty()) farmerName.first().toString() else "F",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Good Morning, $farmerName 👋",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = FarmerPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = farmLocation,
                                fontSize = 12.sp,
                                color = FarmerTextSecondary
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { onNavigate("chat_list") },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chats",
                            tint = FarmerPrimary
                        )
                    }
                    IconButton(
                        onClick = { Toast.makeText(context, "No new agricultural notifications.", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = FarmerPrimary
                        )
                    }
                }
            }
        }

        // SEARCH BAR
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search crops, fertilizers, pesticides...", color = FarmerTextSecondary) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, tint = FarmerPrimary, contentDescription = "Search icon") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, tint = FarmerTextSecondary, contentDescription = "Clear search")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FarmerPrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("farmer_search")
            )
        }

        // STATISTICS CARDS ROW
        item {
            val activeCount = cropsList.count { it.status == "Available" }
            val pendingOrders = ordersList.count { it.status == "Pending" }
            val completedOrders = ordersList.count { it.status == "Completed" }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(title = "My Crops", count = activeCount.toString(), subtitle = "Active Listings", color = FarmerPrimary, modifier = Modifier.weight(1f))
                StatCard(title = "Pending", count = pendingOrders.toString(), subtitle = "Incoming Orders", color = FarmerAccent, modifier = Modifier.weight(1f))
                StatCard(title = "Completed", count = completedOrders.toString(), subtitle = "Sales Done", color = FarmerSuccess, modifier = Modifier.weight(1f))
            }
        }

        // QUICK ACTION GRID
        item {
            Text(
                text = "Quick Actions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = FarmerTextPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            val actions = listOf(
                Triple("Add Crop", Icons.Default.Add, "add_crop"),
                Triple("My Crops", Icons.Default.Eco, "my_crops"),
                Triple("Orders", Icons.Default.ListAlt, "orders"),
                Triple("Agri Store", Icons.Default.Storefront, "agri_store"),
                Triple("Gov Schemes", Icons.Default.Gavel, "gov_schemes"),
                Triple("Weather", Icons.Default.Cloud, "weather")
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (row in 0 until 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (col in 0 until 3) {
                            val act = actions[row * 3 + col]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFECEFF1), RoundedCornerShape(20.dp))
                                    .clickable { onNavigate(act.third) }
                                    .padding(vertical = 16.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(FarmerPrimary.copy(alpha = 0.08f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = act.second,
                                            contentDescription = act.first,
                                            tint = FarmerPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = act.first,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FarmerTextPrimary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // WEATHER SUMMARY CARD
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                border = BorderStroke(1.dp, FarmerAccent.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("weather") }
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌤️", fontSize = 38.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Pune Weather", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            Text("28°C • Light Rain expected", fontSize = 12.sp, color = FarmerTextSecondary)
                            Text("Perfect humidity for onion sowing", fontSize = 11.sp, color = FarmerPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Details",
                        tint = FarmerPrimary
                    )
                }
            }
        }

        // BROKER BOARD BANNER
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, FarmerPrimary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("broker_requests") }
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("📣", fontSize = 38.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Broker Requests Board", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            Text("Wholesale demands from licensed Pune brokers", fontSize = 12.sp, color = FarmerTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(FarmerPrimary)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("New Bulk Live Demands", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Board",
                        tint = FarmerPrimary
                    )
                }
            }
        }

        // RECENT ORDERS SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Incoming Orders",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
                Text(
                    text = "View All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerPrimary,
                    modifier = Modifier.clickable { onNavigate("orders") }
                )
            }
        }

        val pending = ordersList.filter { it.status == "Pending" }.take(2)
        if (pending.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1))
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No pending crop orders today.", color = FarmerTextSecondary, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(pending) { ord ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectOrder(ord.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = ord.cropName, fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 15.sp)
                            Text(text = "Buyer: ${ord.customerName}", fontSize = 13.sp, color = FarmerTextSecondary)
                            Text(text = "Total: ₹${ord.totalPrice}", fontSize = 13.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(FarmerAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Pending", color = FarmerAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // GOVT SCHEMES PREVIEW
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Government Schemes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
                Text(
                    text = "View All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerPrimary,
                    modifier = Modifier.clickable { onNavigate("gov_schemes") }
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, FarmerPrimary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("gov_schemes") }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("🌾 PM-KISAN Yojana Scheme Active", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Eligible farmers are receiving active 17th installment transfer now. Verify your Aadhar seeding status.", fontSize = 12.sp, color = FarmerTextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Check Eligibility →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, count: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(count, fontSize = 24.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = FarmerTextSecondary)
        }
    }
}

// ------------------ 2. ADD CROP SCREEN ------------------
@Composable
fun AddCropView(
    editCropId: String?,
    cropsList: MutableList<FarmerCrop>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isEdit = editCropId != null
    val originalCrop = if (isEdit) cropsList.find { it.id == editCropId } else null

    var cropName by remember { mutableStateOf(originalCrop?.name ?: "") }
    var cropCategory by remember { mutableStateOf(originalCrop?.category ?: "Vegetables") }
    var cropQuantity by remember { mutableStateOf(originalCrop?.quantity?.toString() ?: "") }
    var cropUnit by remember { mutableStateOf(originalCrop?.unit ?: "Quintal") }
    var cropPrice by remember { mutableStateOf(originalCrop?.price?.toString() ?: "") }
    var harvestDate by remember { mutableStateOf(originalCrop?.harvestDate ?: "18 July 2026") }
    var description by remember { mutableStateOf(originalCrop?.description ?: "") }
    var selectedPresetImage by remember { mutableStateOf(originalCrop?.imagePreset ?: "🧅") }

    val categories = listOf("Vegetables", "Fruits", "Grains", "Cash Crop", "Spices")
    val units = listOf("Kg", "Quintal", "Ton", "Dozen")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = FarmerPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEdit) "Edit Crop Listing" else "Add New Crop",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
            }
        }

        // RECTANGULAR PRESET IMAGE SELECTOR (FARMER FRIENDLY IMAGE UPLOAD SIMULATOR)
        item {
            Column {
                Text("Select Crop Icon/Preset", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CROP_PRESETS.forEach { (name, emoji) ->
                        val isSelected = selectedPresetImage == emoji
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) FarmerPrimary.copy(alpha = 0.12f) else Color.White)
                                .border(
                                    2.dp,
                                    if (isSelected) FarmerPrimary else Color(0xFFE0E0E0),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    selectedPresetImage = emoji
                                    if (cropName.isEmpty() || CROP_PRESETS.any { it.first == cropName }) {
                                        cropName = name
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(emoji, fontSize = 26.sp)
                                Text(name.split(" ").last(), fontSize = 10.sp, color = FarmerTextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // CROP NAME
        item {
            Column {
                Text("Crop Name", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = cropName,
                    onValueChange = { cropName = it },
                    placeholder = { Text("e.g. Pune Red Onions", color = FarmerMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FarmerPrimary,
                        unfocusedBorderColor = Color(0xFFD0D0D0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // CATEGORY CHIPS
        item {
            Column {
                Text("Crop Category", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = cropCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { cropCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FarmerPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // QUANTITY AND UNIT
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Quantity", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = cropQuantity,
                        onValueChange = { cropQuantity = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("e.g. 15", color = FarmerMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmerPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Unit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        units.forEach { u ->
                            val isSelected = cropUnit == u
                            ElevatedFilterChip(
                                selected = isSelected,
                                onClick = { cropUnit = u },
                                label = { Text(u, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // PRICE
        item {
            Column {
                Text("Selling Price (₹ per unit)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = cropPrice,
                    onValueChange = { cropPrice = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("e.g. 1850", color = FarmerMuted) },
                    leadingIcon = { Text("₹", color = FarmerPrimary, fontWeight = FontWeight.Bold) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmerPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // HARVEST DATE
        item {
            Column {
                Text("Harvest Date", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = harvestDate,
                    onValueChange = { harvestDate = it },
                    placeholder = { Text("e.g. 18 July 2026", color = FarmerMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmerPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // DESCRIPTION
        item {
            Column {
                Text("Crop Description", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Tell buyers about organic pesticides used, sorting state, etc.", color = FarmerMuted) },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmerPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // PUBLISH BUTTON
        item {
            Button(
                onClick = {
                    val qty = cropQuantity.toDoubleOrNull() ?: 1.0
                    val prc = cropPrice.toDoubleOrNull() ?: 100.0

                    if (cropName.isEmpty()) {
                        Toast.makeText(context, "Please enter a crop name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (isEdit) {
                        val index = cropsList.indexOfFirst { it.id == editCropId }
                        if (index != -1) {
                            cropsList[index] = FarmerCrop(
                                editCropId!!, cropName, cropCategory, qty, cropUnit, prc, harvestDate, description, originalCrop?.status ?: "Available", selectedPresetImage
                            )
                        }
                        Toast.makeText(context, "Crop details updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        val newId = "crop_" + System.currentTimeMillis()
                        cropsList.add(
                            FarmerCrop(newId, cropName, cropCategory, qty, cropUnit, prc, harvestDate, description, "Available", selectedPresetImage)
                        )
                        Toast.makeText(context, "Crop listing published live to buyers!", Toast.LENGTH_SHORT).show()
                    }
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (isEdit) "Save Changes" else "Publish Crop",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ------------------ 3. MY CROPS SCREEN ------------------
@Composable
fun MyCropsView(
    cropsList: MutableList<FarmerCrop>,
    onEditCrop: (String) -> Unit,
    onAddCrop: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("All") }
    val context = LocalContext.current

    val filtered = cropsList.filter {
        (selectedCat == "All" || it.category == selectedCat) &&
        (it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCrop,
                containerColor = FarmerPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Crop")
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                Text(
                    text = "My Crops",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
                Text(
                    text = "Manage your listed products in Pune direct market",
                    fontSize = 12.sp,
                    color = FarmerTextSecondary
                )
            }

            // SEARCH AND FILTER CHIPS
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search your listed crops...", color = FarmerMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, tint = FarmerPrimary, contentDescription = "Search") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmerPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                val filterCats = listOf("All", "Vegetables", "Fruits", "Grains", "Cash Crop")
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterCats.forEach { cat ->
                        val isSelected = selectedCat == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCat = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FarmerPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌱", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No crops match your search/filter.", color = FarmerTextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                items(filtered) { crop ->
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(FarmerPrimary.copy(alpha = 0.08f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(crop.imagePreset, fontSize = 28.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = crop.name, fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 16.sp)
                                        Text(text = "Category: ${crop.category}", fontSize = 12.sp, color = FarmerTextSecondary)
                                    }
                                }

                                // Status Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (crop.status == "Available") FarmerSuccess.copy(alpha = 0.15f) else FarmerMuted.copy(alpha = 0.2f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = crop.status,
                                        color = if (crop.status == "Available") FarmerSuccess else FarmerTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("QUANTITY", fontSize = 10.sp, color = FarmerTextSecondary)
                                    Text("${crop.quantity} ${crop.unit}", fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 14.sp)
                                }
                                Column {
                                    Text("EST. PRICE", fontSize = 10.sp, color = FarmerTextSecondary)
                                    Text("₹${crop.price} / ${crop.unit}", fontWeight = FontWeight.Bold, color = FarmerPrimary, fontSize = 14.sp)
                                }
                                Column {
                                    Text("HARVEST", fontSize = 10.sp, color = FarmerTextSecondary)
                                    Text(crop.harvestDate, fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onEditCrop(crop.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, FarmerPrimary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = FarmerPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Edit", color = FarmerPrimary, fontSize = 12.sp)
                                }

                                if (crop.status == "Available") {
                                    Button(
                                        onClick = {
                                            val index = cropsList.indexOfFirst { it.id == crop.id }
                                            if (index != -1) {
                                                cropsList[index] = crop.copy(status = "Sold")
                                            }
                                            Toast.makeText(context, "${crop.name} marked as SOLD!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1.5f)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Sold", tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Mark Sold", color = Color.White, fontSize = 12.sp)
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        cropsList.remove(crop)
                                        Toast.makeText(context, "Crop listing deleted.", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .background(Color(0xFFFFEBEE), CircleShape)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 4. ORDERS TAB VIEW ------------------
@Composable
fun OrdersView(
    ordersList: List<FarmerOrder>,
    onSelectOrder: (String) -> Unit
) {
    var selectedTabState by remember { mutableStateOf("Pending") }
    val tabs = listOf("Pending", "Accepted", "Completed")

    val filtered = ordersList.filter { it.status == selectedTabState }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Buyer Orders",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = FarmerTextPrimary
        )
        Text(
            text = "Respond to wholesale and local purchase orders",
            fontSize = 12.sp,
            color = FarmerTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TAB BAR
        TabRow(
            selectedTabIndex = tabs.indexOf(selectedTabState),
            containerColor = Color.Transparent,
            contentColor = FarmerPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTabState)]),
                    color = FarmerPrimary
                )
            }
        ) {
            tabs.forEach { t ->
                Tab(
                    selected = selectedTabState == t,
                    onClick = { selectedTabState = t },
                    text = { Text(t, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No $selectedTabState orders found.", color = FarmerTextSecondary, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filtered) { ord ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectOrder(ord.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(ord.cropName, fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 16.sp)
                                    Text("Customer: ${ord.customerName}", fontSize = 13.sp, color = FarmerTextSecondary)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (ord.status) {
                                                "Pending" -> FarmerAccent.copy(alpha = 0.15f)
                                                "Accepted" -> FarmerPrimary.copy(alpha = 0.15f)
                                                else -> FarmerSuccess.copy(alpha = 0.15f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = ord.status,
                                        color = when (ord.status) {
                                            "Pending" -> FarmerAccent
                                            "Accepted" -> FarmerPrimary
                                            else -> FarmerSuccess
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF5F5F5))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("ORDERED QUANTITY", fontSize = 10.sp, color = FarmerTextSecondary)
                                    Text("${ord.quantity} ${ord.unit}", fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 13.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("TOTAL VALUE", fontSize = 10.sp, color = FarmerTextSecondary)
                                    Text("₹${ord.totalPrice}", fontWeight = FontWeight.Bold, color = FarmerPrimary, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Date: ${ord.date}", fontSize = 11.sp, color = FarmerTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 5. ORDER DETAILS SCREEN ------------------
@Composable
fun OrderDetailsView(
    orderId: String,
    ordersList: MutableList<FarmerOrder>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ordIndex = ordersList.indexOfFirst { it.id == orderId }
    if (ordIndex == -1) {
        onBack()
        return
    }
    val ord = ordersList[ordIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = FarmerPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Order Invoice Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
            }
        }

        // VISUAL INVOICE CONTAINER
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ORDER ID", fontSize = 10.sp, color = FarmerTextSecondary)
                            Text("#${ord.id.uppercase()}", fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FarmerPrimary.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(ord.status, color = FarmerPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("CUSTOMER INFORMATION", fontSize = 11.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(ord.customerName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    Text("Phone: ${ord.customerPhone}", fontSize = 14.sp, color = FarmerTextSecondary)
                    Text("Delivery Address: ${ord.deliveryAddress}", fontSize = 13.sp, color = FarmerTextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("CROP & PRICE SPECIFICATIONS", fontSize = 11.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(ord.cropName, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        Text("${ord.quantity} ${ord.unit}", fontWeight = FontWeight.Medium, color = FarmerTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL PAYABLE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        Text("₹${ord.totalPrice}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = FarmerPrimary)
                    }
                }
            }
        }

        // RESPONSIVE ACTION BUTTONS
        if (ord.status == "Pending") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            ordersList[ordIndex] = ord.copy(status = "Rejected")
                            Toast.makeText(context, "Order rejected.", Toast.LENGTH_SHORT).show()
                            onBack()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        Text("Reject Order", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            ordersList[ordIndex] = ord.copy(status = "Accepted")
                            Toast.makeText(context, "Order ACCEPTED! Initiating delivery dispatch.", Toast.LENGTH_SHORT).show()
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(54.dp)
                    ) {
                        Text("Accept Order", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        } else if (ord.status == "Accepted") {
            item {
                Button(
                    onClick = {
                        ordersList[ordIndex] = ord.copy(status = "Completed")
                        Toast.makeText(context, "Order marked as successfully COMPLETED!", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text("Mark as Completed / Delivered", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ------------------ 6. AGRI STORE SCREEN (FERTILIZERS & PESTICIDES) ------------------
@Composable
fun AgriStoreView(
    products: List<StoreProduct>,
    cartCount: Int,
    onUpdateCart: (Int) -> Unit
) {
    var storeQuery by remember { mutableStateOf("") }
    var storeCategory by remember { mutableStateOf("All") }
    val context = LocalContext.current

    val filtered = products.filter {
        (storeCategory == "All" || it.category == storeCategory) &&
        (it.name.contains(storeQuery, ignoreCase = true) || it.brand.contains(storeQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Pune Direct Agri Store",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = FarmerTextPrimary
            )
            Text(
                text = "Buy premium seeds, tools, and chemicals directly with free delivery",
                fontSize = 12.sp,
                color = FarmerTextSecondary
            )
        }

        // SEARCH BAR
        item {
            OutlinedTextField(
                value = storeQuery,
                onValueChange = { storeQuery = it },
                placeholder = { Text("Search seeds, fertilizers, chemical sprays...", color = FarmerMuted) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, tint = FarmerPrimary, contentDescription = "Search") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmerPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // PROMOTIONAL SEASONAL OFFERS BANNER
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, FarmerPrimary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎁", fontSize = 34.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Monsoon Special Discount!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                        Text("Get up to 20% instant cashback on Mahadhan Fertilizer NPK range. Delivered to your farm in Baramati/Shirur/Haveli.", fontSize = 11.sp, color = FarmerTextSecondary)
                    }
                }
            }
        }

        // CATEGORIES SELECTOR
        item {
            val storeCats = listOf("All", "Fertilizers", "Pesticides", "Seeds", "Organic", "Tools")
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                storeCats.forEach { cat ->
                    val isSelected = storeCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { storeCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // PRODUCTS LIST GRID
        items(filtered) { prod ->
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(FarmerPrimary.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(prod.imagePreset, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = prod.name, fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 15.sp)
                            Text(text = "Brand: ${prod.brand} • Rating: ⭐ ${prod.rating}", fontSize = 12.sp, color = FarmerTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "₹${prod.price}", fontWeight = FontWeight.Black, color = FarmerPrimary, fontSize = 16.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (prod.stock > 0) "In Stock" else "Out of Stock",
                            color = if (prod.stock > 0) FarmerSuccess else Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Button(
                            onClick = {
                                onUpdateCart(cartCount + 1)
                                Toast.makeText(context, "${prod.name} added to agricultural cart!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                            shape = RoundedCornerShape(12.dp),
                            enabled = prod.stock > 0,
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text("Add Cart", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 7. GOVERNMENT SCHEMES SCREEN ------------------
@Composable
fun GovSchemesView(
    schemes: List<GovScheme>,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = FarmerPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sarkari Yojana",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
            }
        }

        items(schemes) { sch ->
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FarmerPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Gavel, contentDescription = "Gov", tint = FarmerPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = sch.name, fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = sch.description, fontSize = 13.sp, color = FarmerTextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FarmerBackground)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("ELIGIBILITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                            Text(sch.eligibility, fontSize = 12.sp, color = FarmerTextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("BENEFITS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                            Text(sch.benefit, fontSize = 12.sp, color = FarmerTextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            Toast.makeText(context, "Redirecting to MahaDBT / Government Official Web Portal...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Now / Learn More", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ------------------ 8. WEATHER COMPOSABLE ------------------
@Composable
fun WeatherView(
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = FarmerPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pune Weather Radar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
            }
        }

        // CURRENT DETAILED WEATHER
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Haveli Taluka, Pune", fontSize = 14.sp, color = FarmerTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("28°C", fontSize = 48.sp, fontWeight = FontWeight.Black, color = FarmerPrimary)
                    Text("Light Rain Shower expected", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💧 HUMIDITY", fontSize = 10.sp, color = FarmerTextSecondary)
                            Text("88%", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💨 WIND SPEED", fontSize = 10.sp, color = FarmerTextSecondary)
                            Text("18 km/h", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌧️ PRECIPITATION", fontSize = 10.sp, color = FarmerTextSecondary)
                            Text("65% chance", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // 7-DAY FORECAST
        item {
            Text("7-Day Agricultural Forecast", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
        }

        val forecast = listOf(
            Triple("Today", "28°C", "⛈️ Light Rain"),
            Triple("Tomorrow", "29°C", "🌦️ Passing Shower"),
            Triple("Mon, 20 Jul", "30°C", "🌤️ Mainly Sunny"),
            Triple("Tue, 21 Jul", "30°C", "☁️ Heavy Overcast"),
            Triple("Wed, 22 Jul", "27°C", "🌧️ Moderate Monsoon Rain"),
            Triple("Thu, 23 Jul", "26°C", "🌧️ Wet Soil Advisory"),
            Triple("Fri, 24 Jul", "28°C", "🌦️ Scattered Drizzle")
        )

        items(forecast) { day ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(day.first, fontWeight = FontWeight.Bold, color = FarmerTextPrimary, modifier = Modifier.width(100.dp))
                    Text(day.third, fontSize = 13.sp, color = FarmerTextSecondary)
                    Text(day.second, fontWeight = FontWeight.Black, color = FarmerPrimary)
                }
            }
        }
    }
}

// ------------------ 9. CHAT SYSTEM COMPOSABLES ------------------
@Composable
fun ChatListView(
    chatSessions: List<ChatSession>,
    onSelectChat: (String) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = FarmerPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AgroWorld Messages",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
            }
        }

        items(chatSessions) { session ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectChat(session.id) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(FarmerPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(session.partnerName.first().toString(), fontWeight = FontWeight.Bold, color = FarmerPrimary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(session.partnerName, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${session.partnerRole}",
                                    fontSize = 11.sp,
                                    color = FarmerPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(session.timestamp, fontSize = 11.sp, color = FarmerTextSecondary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = session.lastMsg,
                            fontSize = 13.sp,
                            color = FarmerTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatDetailView(
    sessionId: String,
    chatSessions: MutableList<ChatSession>,
    onBack: () -> Unit
) {
    val sessionIndex = chatSessions.indexOfFirst { it.id == sessionId }
    if (sessionIndex == -1) {
        onBack()
        return
    }
    val session = chatSessions[sessionIndex]
    var replyText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = FarmerPrimary)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FarmerPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(session.partnerName.first().toString(), fontWeight = FontWeight.Bold, color = FarmerPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(session.partnerName, fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 15.sp)
                Text("Active Online • ${session.partnerRole}", fontSize = 11.sp, color = FarmerPrimary)
            }
        }

        HorizontalDivider()

        // MESSAGE LIST
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            items(session.messages) { msg ->
                val isMe = msg.sender == "Me"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) FarmerPrimary else Color.White
                        ),
                        border = if (isMe) null else BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = msg.text, color = if (isMe) Color.White else FarmerTextPrimary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.timestamp,
                                fontSize = 9.sp,
                                color = if (isMe) Color.White.copy(alpha = 0.7f) else FarmerTextSecondary,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // FOOTER TEXT INPUT FIELD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = replyText,
                onValueChange = { replyText = it },
                placeholder = { Text("Write your message here...", color = FarmerMuted) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmerPrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            )

            FloatingActionButton(
                onClick = {
                    if (replyText.isNotEmpty()) {
                        val updatedList = session.messages.toMutableList().apply {
                            add(ChatMessage("Me", replyText, "Just now"))
                        }
                        chatSessions[sessionIndex] = session.copy(
                            lastMsg = replyText,
                            timestamp = "Just now",
                            messages = updatedList
                        )
                        replyText = ""
                    }
                },
                containerColor = FarmerPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

// ------------------ 10. PROFILE VIEW SCREEN ------------------
@Composable
fun ProfileView(
    name: String,
    onNameChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    farmSize: String,
    onFarmSizeChange: (String) -> Unit,
    cropType: String,
    onCropTypeChange: (String) -> Unit,
    appLanguage: String,
    onLanguageChange: (String) -> Unit,
    onLogout: () -> Unit
) {
    var isEditingName by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "My Farm Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = FarmerTextPrimary
            )
            Text(
                text = "Keep your farm details and preferences updated",
                fontSize = 12.sp,
                color = FarmerTextSecondary
            )
        }

        // PROFILE CARD WITH INITIALS
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(FarmerPrimary.copy(alpha = 0.15f))
                            .border(3.dp, FarmerPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name.first().toString(), fontSize = 32.sp, fontWeight = FontWeight.Black, color = FarmerPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditingName) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = onNameChange,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmerPrimary),
                            modifier = Modifier.width(200.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Save",
                            color = FarmerPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isEditingName = false }
                        )
                    } else {
                        Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        Text(
                            text = "Edit Name",
                            fontSize = 12.sp,
                            color = FarmerPrimary,
                            modifier = Modifier.clickable { isEditingName = true }
                        )
                    }
                }
            }
        }

        // FARM INFORMATION DETAIL LIST
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("FARM & LOCATION DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileField(label = "Primary Location", value = location, onValueChange = onLocationChange)
                    HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(vertical = 8.dp))
                    ProfileField(label = "Total Landholding Area", value = farmSize, onValueChange = onFarmSizeChange)
                    HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(vertical = 8.dp))
                    ProfileField(label = "Major Crops Grown", value = cropType, onValueChange = onCropTypeChange)
                }
            }
        }

        // LANGUAGE AND INTERFACE SETTINGS
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("APP PREFERENCES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Application Language", fontSize = 11.sp, color = FarmerTextSecondary)
                            Text(appLanguage, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        val langs = listOf("Marathi (मराठी)", "English (English)", "Hindi (हिन्दी)")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            langs.forEach { l ->
                                val isSelected = appLanguage == l
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) FarmerPrimary else Color(0xFFECEFF1))
                                        .clickable { onLanguageChange(l) }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = l.split(" ").first(),
                                        color = if (isSelected) Color.White else FarmerTextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // HELP & SUPPORT
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .clickable { Toast.makeText(context, "Calling AgroWorld Farmer Helpline (Toll-Free)...", Toast.LENGTH_SHORT).show() }
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📞", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Farmer Help & Support Helpline", fontWeight = FontWeight.Bold, color = FarmerTextPrimary, fontSize = 14.sp)
                                Text("Toll-Free: 1800-420-1200", fontSize = 12.sp, color = FarmerTextSecondary)
                            }
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Call Help", tint = FarmerPrimary)
                    }
                }
            }
        }

        // LOGOUT
        item {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("Log Out from Account", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, color = FarmerTextSecondary)
            if (isEditing) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmerPrimary),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            } else {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FarmerTextPrimary)
            }
        }

        Text(
            text = if (isEditing) "Done" else "Change",
            color = FarmerPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier
                .clickable { isEditing = !isEditing }
                .padding(8.dp)
        )
    }
}

// ------------------ FARMER VIEW: BROKER REQUESTS BOARD ------------------
data class MockBrokerRequirement(
    val id: String,
    val brokerName: String,
    val requiredCrop: String,
    val quantityRequired: String,
    val expectedPrice: String,
    val preferredTaluka: String,
    val deadline: String,
    val additionalNotes: String
)

@Composable
fun FarmerBrokerRequestsScreen(
    onBack: () -> Unit,
    onContactBroker: (String) -> Unit
) {
    val context = LocalContext.current
    val mockRequirements = remember {
        listOf(
            MockBrokerRequirement("mbr_1", "Deshmukh Agro Commission Agent", "Pune Red Onions", "150 Quintal", "₹1,850 / Quintal", "Haveli", "25 July 2026", "Onions must be well cured, dried, and graded. Standard bags of 50kg needed."),
            MockBrokerRequirement("mbr_2", "Sunil Deshmukh (APMC Licensed)", "Indrayani Scented Paddy", "80 Quintal", "₹4,200 / Quintal", "Maval", "28 July 2026", "High aroma paddy required, moisture strictly under 13%."),
            MockBrokerRequirement("mbr_3", "Pune Wholesale Distributors", "Lokwan Durum Wheat", "200 Quintal", "₹2,800 / Quintal", "Shirur", "30 July 2026", "Direct delivery to Wagholi warehouse. Cash payment on scale weight."),
            MockBrokerRequirement("mbr_4", "Baramati Sugar Mills Broker", "Sugarcane", "100 Ton", "₹3,200 / Ton", "Baramati", "05 August 2026", "Freshly cut cane ready for direct transport, sugar recovery 11%+")
        )
    }

    var interestLogs = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = FarmerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Broker Requests Board",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FarmerTextPrimary
            )
        }

        Text(
            text = "Browse wholesale and bulk crop requirements posted by licensed APMC brokers in Pune District.",
            fontSize = 12.sp,
            color = FarmerTextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(mockRequirements) { req ->
                val hasExpressedInterest = interestLogs.contains(req.id)

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
                            Column {
                                Text(
                                    text = req.brokerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = FarmerTextPrimary
                                )
                                Text("APMC Commission Agent", fontSize = 10.sp, color = FarmerTextSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(FarmerPrimary.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Active",
                                    color = FarmerPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(FarmerBackground, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Required Crop", fontSize = 10.sp, color = FarmerTextSecondary)
                                Text(req.requiredCrop, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Quantity Required", fontSize = 10.sp, color = FarmerTextSecondary)
                                Text(req.quantityRequired, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Preferred Origin Taluka", fontSize = 10.sp, color = FarmerTextSecondary)
                                Text(req.preferredTaluka, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Expected Price Offered", fontSize = 10.sp, color = FarmerTextSecondary)
                                Text(req.expectedPrice, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Deliver Before: ${req.deadline}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD84315)
                        )

                        if (req.additionalNotes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Specifications: ${req.additionalNotes}",
                                fontSize = 11.sp,
                                color = FarmerTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!hasExpressedInterest) {
                                        interestLogs.add(req.id)
                                        Toast.makeText(context, "Interest registered! Broker has been notified.", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "You have already expressed interest in this requirement.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (hasExpressedInterest) Color(0xFFE8F5E9) else FarmerPrimary,
                                    contentColor = if (hasExpressedInterest) FarmerPrimary else Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Text(
                                    text = if (hasExpressedInterest) "Interested ✓" else "I'm Interested 🙋‍♂️",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { onContactBroker(req.brokerName) },
                                colors = ButtonDefaults.buttonColors(containerColor = FarmerSecondary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Contact Broker", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 12. OTP VERIFICATION SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerOtpVerificationView(
    phone: String,
    onPhoneChange: (String) -> Unit,
    otpCode: String,
    onOtpChange: (String) -> Unit,
    isOtpSent: Boolean,
    onSendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onCancel: () -> Unit
) {
    var timerSeconds by remember { mutableStateOf(30) }
    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            timerSeconds = 30
            while (timerSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                timerSeconds--
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        FarmerBackground,
                        Color(0xFFE8F5E9)
                    )
                )
            )
            .testTag("otp_verification_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = FarmerPrimary
                    )
                }
            }

            // Central Branding & Setup Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White)
                        .padding(12.dp)
                        .shadow(4.dp, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(FarmerPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockPerson,
                            contentDescription = "OTP Lock Icon",
                            tint = FarmerPrimary,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Verify Mobile Number",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A secure verification step is required before registering your farmer profile.",
                    fontSize = 14.sp,
                    color = FarmerTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Input card
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Phone input
                        Column {
                            Text(
                                text = "Enter Mobile Number",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmerPrimary,
                                modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() } && input.length <= 10) {
                                        onPhoneChange(input)
                                    }
                                },
                                leadingIcon = {
                                    Text("+91 ", fontWeight = FontWeight.Bold, color = FarmerPrimary, modifier = Modifier.padding(start = 12.dp))
                                },
                                trailingIcon = {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = FarmerPrimary.copy(alpha = 0.6f))
                                },
                                placeholder = { Text("10-digit mobile number", color = FarmerMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                readOnly = isOtpSent,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FarmerPrimary,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color(0xFFF8FBF7),
                                    unfocusedContainerColor = if (isOtpSent) Color(0xFFF1F5F9) else Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("phone_otp_input")
                            )
                        }

                        if (!isOtpSent) {
                            Button(
                                onClick = onSendOtp,
                                colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("send_otp_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("Send Verification OTP", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Send", modifier = Modifier.size(16.dp))
                                }
                            }
                        } else {
                            Divider(color = Color(0xFFF1F5F9))

                            // OTP input
                            Column {
                                Text(
                                    text = "Enter 4-Digit OTP Code",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmerPrimary,
                                    modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { input ->
                                        if (input.all { it.isDigit() } && input.length <= 6) {
                                            onOtpChange(input)
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.VpnKey, contentDescription = "OTP", tint = FarmerPrimary)
                                    },
                                    placeholder = { Text("Enter OTP code", color = FarmerMuted) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FarmerPrimary,
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedContainerColor = Color(0xFFF8FBF7)
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("otp_digit_input")
                                )
                            }

                            // Verify OTP Button
                            Button(
                                onClick = onVerifyOtp,
                                colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("verify_otp_btn")
                            ) {
                                Text("Verify & Register Profile", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }

                            // Resend timer text
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (timerSeconds > 0) "Resend OTP in ${timerSeconds}s" else "Didn't receive code?",
                                    fontSize = 12.sp,
                                    color = FarmerTextSecondary
                                )
                                if (timerSeconds == 0) {
                                    Text(
                                        text = "Resend OTP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FarmerPrimary,
                                        modifier = Modifier.clickable {
                                            onSendOtp()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Secure message
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Verified",
                    tint = FarmerPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Verified by AgroWorld Trust Network",
                    fontSize = 12.sp,
                    color = FarmerTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


// ==========================================
// 13. FARMER REGISTRATION FORM SCREEN
// ==========================================
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FarmerRegistrationFormView(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    verifiedPhone: String,
    village: String,
    onVillageChange: (String) -> Unit,
    selectedTaluka: String,
    onTalukaChange: (String) -> Unit,
    selectedCategories: SnapshotStateList<String>,
    isOrganic: String,
    onOrganicChange: (String) -> Unit,
    selectedAvatar: String,
    onAvatarChange: (String) -> Unit,
    onCreateAccount: () -> Unit
) {
    val context = LocalContext.current
    var isTalukaDropdownExpanded by remember { mutableStateOf(false) }

    val puneTalukas = remember {
        listOf(
            "Baramati", "Haveli", "Khed", "Junnar", "Ambegaon",
            "Shirur", "Maval", "Mulshi", "Velhe", "Bhor",
            "Purandar", "Indapur", "Daund"
        )
    }

    val availableCategories = remember {
        listOf(
            "Vegetables" to "🥦",
            "Fruits" to "🍎",
            "Grains" to "🌾",
            "Pulses" to "🫘",
            "Spices" to "🌶️",
            "Flowers" to "🌸",
            "Sugarcane" to "🎋",
            "Other" to "🚜"
        )
    }

    val avatars = listOf("🧑‍🌾", "🚜", "🌾", "🍎", "🧅", "🎋", "🍊", "🥦")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
            .testTag("farmer_registration_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Screen Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Farmer Profile Registration",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerPrimary
                )
                Text(
                    text = "Complete your AgroWorld farmer setup to start selling crops.",
                    fontSize = 13.sp,
                    color = FarmerTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Card 1: Profile Photo Picker
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose Farmer Avatar / Theme",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmerTextPrimary,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    // Big Circular Image Picker simulation
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(FarmerPrimary.copy(alpha = 0.1f))
                            .border(2.dp, FarmerPrimary, CircleShape)
                            .clickable {
                                Toast.makeText(context, "Tap on any icon below to update avatar", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedAvatar,
                            fontSize = 42.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Horizontal selectable avatars row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        avatars.forEach { avatar ->
                            val isSelected = selectedAvatar == avatar
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) FarmerPrimary.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) FarmerPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onAvatarChange(avatar) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = avatar, fontSize = 22.sp)
                            }
                        }
                    }
                }
            }

            // Card 2: Personal Information Form
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Personal Information",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmerPrimary
                    )

                    // Full Name
                    Column {
                        Text(
                            text = "Full Name (Required)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = onFullNameChange,
                            placeholder = { Text("Enter your first and last name", color = FarmerMuted) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = "Name", tint = FarmerPrimary)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FarmerPrimary,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("reg_full_name_input")
                        )
                        if (fullName.isBlank()) {
                            Text(
                                text = "Required field",
                                fontSize = 11.sp,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }

                    // Mobile Number
                    Column {
                        Text(
                            text = "Mobile Number (Verified by OTP - Read Only)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = "+91 $verifiedPhone",
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = "Verified Mobile", tint = FarmerPrimary)
                            },
                            trailingIcon = {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Verified ✓",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FarmerPrimary
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE2E8F0),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFF1F5F9),
                                unfocusedContainerColor = Color(0xFFF1F5F9)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Card 3: Location Details
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Farm Location Info",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmerPrimary
                    )

                    // Village
                    Column {
                        Text(
                            text = "Village / Town Name (Required)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = village,
                            onValueChange = onVillageChange,
                            placeholder = { Text("Enter your native village", color = FarmerMuted) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.HomeWork, contentDescription = "Village", tint = FarmerPrimary)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FarmerPrimary,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("reg_village_input")
                        )
                        if (village.isBlank()) {
                            Text(
                                text = "Required field",
                                fontSize = 11.sp,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }

                    // Taluka Dropdown
                    Column {
                        Text(
                            text = "Taluka (Pune District)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clickable { isTalukaDropdownExpanded = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "Taluka",
                                            tint = FarmerPrimary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "$selectedTaluka Taluka",
                                            fontSize = 15.sp,
                                            color = FarmerTextPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Open Taluka Dropdown",
                                        tint = FarmerTextSecondary
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isTalukaDropdownExpanded,
                                onDismissRequest = { isTalukaDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .background(Color.White)
                            ) {
                                puneTalukas.forEach { taluka ->
                                    DropdownMenuItem(
                                        text = { Text("$taluka Taluka", fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            onTalukaChange(taluka)
                                            isTalukaDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 4: Farming Information & Categories
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Farming Information",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmerPrimary
                    )

                    // Crop Categories Multiselect
                    Column {
                        Text(
                            text = "Crop Categories (Select all that apply)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextSecondary,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableCategories.forEach { (catName, catEmoji) ->
                                val isSelected = selectedCategories.contains(catName)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) FarmerPrimary else Color(0xFFF1F5F9))
                                        .border(
                                            width = if (isSelected) 0.dp else 1.dp,
                                            color = Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            if (isSelected) {
                                                selectedCategories.remove(catName)
                                            } else {
                                                selectedCategories.add(catName)
                                            }
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .testTag("crop_chip_$catName")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = catEmoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = catName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else FarmerTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))

                    // Organic Farmer selection
                    Column {
                        Text(
                            text = "Are you an Organic Farmer?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextSecondary,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf("Yes", "No").forEach { option ->
                                val isSelected = isOrganic == option
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .clickable { onOrganicChange(option) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) FarmerPrimary.copy(alpha = 0.1f) else Color(0xFFF8FAFC)
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) FarmerPrimary else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { onOrganicChange(option) },
                                                colors = RadioButtonDefaults.colors(selectedColor = FarmerPrimary)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (option == "Yes") "Yes, Organic 🌿" else "No, Standard 🚜",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) FarmerPrimary else FarmerTextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Create Account Button
            Button(
                onClick = onCreateAccount,
                colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .testTag("create_farmer_account_button")
            ) {
                Text(
                    text = "Create Farmer Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


// ==========================================
// 14. FARMER REGISTRATION SUCCESS SCREEN
// ==========================================
@Composable
fun FarmerRegistrationSuccessView(
    fullName: String,
    onProceedToDashboard: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("success_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Vector Leaf / Checkmark Graphics
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .drawBehind {
                        drawCircle(
                            color = FarmerPrimary.copy(alpha = 0.08f),
                            radius = size.minDimension / 2
                        )
                        drawCircle(
                            color = FarmerPrimary.copy(alpha = 0.15f),
                            radius = size.minDimension / 2.6f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(FarmerPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Welcome to AgroWorld!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = FarmerPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Congratulations $fullName! Your Farmer account has been created successfully.",
                fontSize = 16.sp,
                color = FarmerTextPrimary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You are now part of our trusted grower network. Let's list your first harvest and start selling crops!",
                fontSize = 13.sp,
                color = FarmerTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(56.dp))

            Button(
                onClick = onProceedToDashboard,
                colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .testTag("go_to_dashboard_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Go to Dashboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = "Agriculture",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


