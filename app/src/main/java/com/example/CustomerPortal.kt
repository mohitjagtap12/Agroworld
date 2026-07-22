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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
import androidx.navigation.NavController
import androidx.compose.runtime.snapshots.SnapshotStateList

// ------------------ CUSTOMER COLOR PALETTE ------------------
val CustPrimary = Color(0xFF2E7D32)       // Forest Green
val CustSecondary = Color(0xFF66BB6A)     // Soft Green
val CustAccent = Color(0xFFF9A825)        // Gold/Amber Accent
val CustBackground = Color(0xFFF8FBF7)    // Soft agricultural off-white/mint background
val CustCardBg = Color(0xFFFFFFFF)        // Card White
val CustTextPrimary = Color(0xFF212121)   // Deep charcoal
val CustTextSecondary = Color(0xFF616161) // Soft charcoal
val CustLightBg = Color(0xFFE8F5E9)       // Very light pastel green

// ------------------ DATA MODELS ------------------
data class CustomerCropItem(
    val id: String,
    val name: String,
    val farmerName: String,
    val village: String,
    val taluka: String, // "Haveli", "Baramati", "Junnar", "Khed", "Maval"
    val category: String, // "Vegetables", "Fruits", "Grains", "Pulses", "Spices"
    val price: Double, // price in ₹
    val unit: String, // "Kg", "Quintal", "Dozen"
    val availableQty: Double,
    val rating: Double,
    val description: String,
    val imageEmoji: String,
    val isOrganic: Boolean = true
)

data class CustomerCartItem(
    val cropItem: CustomerCropItem,
    var selectedQty: Double
)

data class CustomerOrderItem(
    val orderId: String,
    val cropName: String,
    val imageEmoji: String,
    val farmerName: String,
    val quantity: Double,
    val unit: String,
    val totalAmount: Double,
    val status: String, // "Pending", "Confirmed", "Delivered"
    val date: String,
    val deliveryAddress: String,
    val otpCode: String = "4820"
)

data class CustChatMessage(
    val sender: String, // "Me", "Farmer"
    val text: String,
    val timestamp: String,
    val hasImage: Boolean = false
)

data class CustChatSession(
    val id: String,
    val farmerName: String,
    val farmerLocation: String,
    val cropInterest: String,
    val lastMsg: String,
    val timestamp: String,
    val unread: Boolean,
    val messages: List<CustChatMessage>
)

// ------------------ MASTER STATE CONTROLLER ------------------
@Composable
fun CustomerPortalScreen(navController: NavController) {
    val context = LocalContext.current

    // Inner navigation screen state
    // "dashboard", "categories", "product_listing", "product_details", "cart", "orders", "order_details", "chat_list", "chat_detail", "profile"
    var currentScreen by remember { mutableStateOf("dashboard") }

    // State for filtering product listings
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedTalukaFilter by remember { mutableStateOf("All") }
    var maxPriceFilter by remember { mutableStateOf(5000.0) }
    var searchKeyword by remember { mutableStateOf("") }

    // Crop database
    val cropsList = remember {
        mutableStateListOf(
            CustomerCropItem("crop1", "Pune Red Onions", "Ramesh Patil", "Wagholi", "Haveli", "Vegetables", 1850.0, "Quintal", 15.0, 4.8, "Perfectly graded sun-dried premium red onions from our fields in Wagholi. No chemical coloring used.", "🧅"),
            CustomerCropItem("crop2", "Alphonso Mangoes", "Vilasrao Deshmukh", "Junnar Hills", "Junnar", "Fruits", 450.0, "Dozen", 80.0, 4.9, "Extremely sweet, aromatic devgad-clone Alphonso mangoes, hand-picked with care.", "🥭"),
            CustomerCropItem("crop3", "Indrayani Scented Rice", "Sanjay Gawade", "Kamshet", "Maval", "Grains", 4200.0, "Quintal", 25.0, 4.7, "Highly aromatic traditional Indrayani scented paddy from Western Ghat streams.", "🌾"),
            CustomerCropItem("crop4", "Spicy Guntur Chillies", "Babasaheb Kudale", "Talegaon", "Khed", "Spices", 250.0, "Kg", 120.0, 4.6, "Super spicy, sun-dried red chillies with rich capsicum oil content.", "🌶️"),
            CustomerCropItem("crop5", "Organic Turmeric Pods", "Dnyaneshwar Hande", "Baramati Agro", "Baramati", "Spices", 180.0, "Kg", 50.0, 4.9, "Pure steam-distilled yellow turmeric finger roots high in curcumin content.", "🫚"),
            CustomerCropItem("crop6", "Fresh Green Broccoli", "Tanaji Rao", "Khed Shivapur", "Khed", "Vegetables", 120.0, "Kg", 40.0, 4.5, "Crisp exotic Italian broccoli grown under modern polyhouse shade nets.", "🥦"),
            CustomerCropItem("crop7", "Golden Wheat Lokwan", "Santosh Kadam", "Shirur", "Shirur", "Grains", 2900.0, "Quintal", 18.0, 4.8, "Heavy premium heavy-density Lokwan wheat, stone-cleaned & ready for rotis.", "🌾"),
            CustomerCropItem("crop8", "Suryamukhi Sunflower Seeds", "Vitthal Shelar", "Indapur", "Indapur", "Grains", 80.0, "Kg", 300.0, 4.4, "High oil yield dry organic sunflower seeds directly harvested.", "🌻")
        )
    }

    // Selected Crop for details view
    var selectedCropId by remember { mutableStateOf("crop1") }

    // Active Cart List
    val cartList = remember { mutableStateListOf<CustomerCartItem>() }

    // My Orders List
    val ordersList = remember {
        mutableStateListOf(
            CustomerOrderItem("ORD-9821", "Pune Red Onions", "🧅", "Ramesh Patil", 2.0, "Quintal", 3700.0, "Pending", "17 July 2026", "Kothrud Heights, Flat 402, Pune"),
            CustomerOrderItem("ORD-5412", "Alphonso Mangoes", "🥭", "Vilasrao Deshmukh", 5.0, "Dozen", 2250.0, "Confirmed", "15 July 2026", "Kothrud Heights, Flat 402, Pune"),
            CustomerOrderItem("ORD-1092", "Indrayani Scented Rice", "🌾", "Sanjay Gawade", 1.0, "Quintal", 4200.0, "Delivered", "10 July 2026", "Kothrud Heights, Flat 402, Pune")
        )
    }
    var selectedOrderId by remember { mutableStateOf("ORD-9821") }

    // Dynamic Chat database
    val chatSessions = remember {
        mutableStateListOf(
            CustChatSession(
                "chat_f1", "Ramesh Patil", "Wagholi, Pune", "Pune Red Onions", "Alright, I will keep 2 Quintals aside for you.", "11:45 AM", true,
                listOf(
                    CustChatMessage("Farmer", "Ram Ram! Yes, the red onions are freshly sorted.", "Yesterday"),
                    CustChatMessage("Me", "Can I get 2 Quintals delivered tomorrow?", "Yesterday"),
                    CustChatMessage("Farmer", "Sure, I am sending a loading pickup to Pune market yard.", "11:42 AM"),
                    CustChatMessage("Me", "Perfect. Let me make the payment on the app.", "11:44 AM"),
                    CustChatMessage("Farmer", "Alright, I will keep 2 Quintals aside for you.", "11:45 AM")
                )
            ),
            CustChatSession(
                "chat_f2", "Vilasrao Deshmukh", "Junnar Hills", "Alphonso Mangoes", "Yes, they are naturally ripened without carbide.", "Yesterday", false,
                listOf(
                    CustChatMessage("Me", "Are these Junnar mangoes naturally ripened?", "Yesterday"),
                    CustChatMessage("Farmer", "Yes, they are naturally ripened without carbide. 100% safe for kids.", "Yesterday")
                )
            )
        )
    }
    var selectedChatPartnerId by remember { mutableStateOf("chat_f1") }

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
    var regCustomerType by remember { mutableStateOf("Individual Customer") }
    var regSelectedAvatar by remember { mutableStateOf("🧑‍💻") }
    val regSelectedCategories = remember { mutableStateListOf<String>() }

    // User Profile Information
    var customerName by remember { mutableStateOf("Abhishek Sharma") }
    var customerPhone by remember { mutableStateOf("+91 98765 43210") }
    var savedAddress by remember { mutableStateOf("Flat 402, Building B, Kothrud Heights, Pune - 411038") }
    var userLanguage by remember { mutableStateOf("English (Default)") }

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
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CustPrimary,
                            indicatorColor = CustPrimary,
                            unselectedIconColor = CustTextSecondary,
                            unselectedTextColor = CustTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "categories" || currentScreen == "product_listing",
                        onClick = {
                            selectedCategoryFilter = "All"
                            currentScreen = "categories"
                        },
                        icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
                        label = { Text("Categories", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CustPrimary,
                            indicatorColor = CustPrimary,
                            unselectedIconColor = CustTextSecondary,
                            unselectedTextColor = CustTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "cart",
                        onClick = { currentScreen = "cart" },
                        icon = {
                            BadgedBox(badge = {
                                if (cartList.isNotEmpty()) {
                                    Badge(containerColor = CustAccent) {
                                        Text(
                                            text = cartList.size.toString(),
                                            color = CustTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        },
                        label = { Text("Cart", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CustPrimary,
                            indicatorColor = CustPrimary,
                            unselectedIconColor = CustTextSecondary,
                            unselectedTextColor = CustTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "orders" || currentScreen == "order_details",
                        onClick = { currentScreen = "orders" },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
                        label = { Text("Orders", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CustPrimary,
                            indicatorColor = CustPrimary,
                            unselectedIconColor = CustTextSecondary,
                            unselectedTextColor = CustTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "profile",
                        onClick = { currentScreen = "profile" },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CustPrimary,
                            indicatorColor = CustPrimary,
                            unselectedIconColor = CustTextSecondary,
                            unselectedTextColor = CustTextSecondary
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
                .background(CustBackground)
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentPortalStage,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                },
                label = "customer_portal_stage_transition"
            ) { stage ->
                when (stage) {
                    "otp_verification" -> {
                        CustomerOtpVerificationView(
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
                                    popUpTo("dashboard/customer") { inclusive = true }
                                }
                            }
                        )
                    }
                    "registration" -> {
                        CustomerRegistrationFormView(
                            fullName = regFullName,
                            onFullNameChange = { regFullName = it },
                            verifiedPhone = enteredPhone.ifEmpty { "9876543210" },
                            village = regVillage,
                            onVillageChange = { regVillage = it },
                            selectedTaluka = regTaluka,
                            onTalukaChange = { regTaluka = it },
                            selectedCustomerType = regCustomerType,
                            onCustomerTypeChange = { regCustomerType = it },
                            selectedCategories = regSelectedCategories,
                            selectedAvatar = regSelectedAvatar,
                            onAvatarChange = { regSelectedAvatar = it },
                            onCreateAccount = {
                                if (regFullName.isBlank()) {
                                    Toast.makeText(context, "Please enter your Full Name", Toast.LENGTH_SHORT).show()
                                } else if (regVillage.isBlank()) {
                                    Toast.makeText(context, "Please enter your Village name", Toast.LENGTH_SHORT).show()
                                } else if (regSelectedCategories.isEmpty()) {
                                    Toast.makeText(context, "Please select at least one interested Category", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Populate profile details
                                    customerName = regFullName
                                    customerPhone = "+91 $enteredPhone"
                                    savedAddress = "$regVillage, $regTaluka"
                                    currentPortalStage = "success"
                                }
                            }
                        )
                    }
                    "success" -> {
                        CustomerRegistrationSuccessView(
                            fullName = regFullName,
                            onProceedToDashboard = {
                                currentPortalStage = "dashboard"
                            }
                        )
                    }
                    "dashboard" -> {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                            },
                            label = "customer_flow_transition"
                        ) { target ->
                            when (target) {
                    "dashboard" -> CustDashboardView(
                        customerName = customerName,
                        crops = cropsList,
                        onSearch = { query ->
                            searchKeyword = query
                            selectedCategoryFilter = "All"
                            currentScreen = "product_listing"
                        },
                        onSelectCategory = { category ->
                            selectedCategoryFilter = category
                            currentScreen = "product_listing"
                        },
                        onSelectCrop = { cropId ->
                            selectedCropId = cropId
                            currentScreen = "product_details"
                        },
                        onAddToCart = { crop ->
                            val exists = cartList.find { it.cropItem.id == crop.id }
                            if (exists != null) {
                                exists.selectedQty += 1.0
                                Toast.makeText(context, "${crop.name} quantity increased in Cart", Toast.LENGTH_SHORT).show()
                            } else {
                                cartList.add(CustomerCartItem(crop, 1.0))
                                Toast.makeText(context, "Added ${crop.name} to Cart", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onNavigateToChat = { currentScreen = "chat_list" }
                    )
                    "categories" -> CustCategoriesView(
                        onSelectCategory = { category ->
                            selectedCategoryFilter = category
                            currentScreen = "product_listing"
                        }
                    )
                    "product_listing" -> CustProductListingView(
                        crops = cropsList,
                        initialCategory = selectedCategoryFilter,
                        initialTaluka = selectedTalukaFilter,
                        initialKeyword = searchKeyword,
                        onBack = { currentScreen = "dashboard" },
                        onSelectCrop = { cropId ->
                            selectedCropId = cropId
                            currentScreen = "product_details"
                        },
                        onAddToCart = { crop ->
                            val exists = cartList.find { it.cropItem.id == crop.id }
                            if (exists != null) {
                                exists.selectedQty += 1.0
                            } else {
                                cartList.add(CustomerCartItem(crop, 1.0))
                            }
                            Toast.makeText(context, "Added ${crop.name} to Cart", Toast.LENGTH_SHORT).show()
                        }
                    )
                    "product_details" -> {
                        val cropObj = cropsList.find { it.id == selectedCropId } ?: cropsList.first()
                        CustProductDetailsView(
                            crop = cropObj,
                            onBack = { currentScreen = "product_listing" },
                            onAddToCart = { qty ->
                                val exists = cartList.find { it.cropItem.id == cropObj.id }
                                if (exists != null) {
                                    exists.selectedQty += qty
                                } else {
                                    cartList.add(CustomerCartItem(cropObj, qty))
                                }
                                Toast.makeText(context, "Added $qty ${cropObj.unit} to Cart", Toast.LENGTH_SHORT).show()
                                currentScreen = "cart"
                            },
                            onChatWithFarmer = {
                                // Find or create a chat session
                                val partnerName = cropObj.farmerName
                                val existing = chatSessions.find { it.farmerName == partnerName }
                                if (existing != null) {
                                    selectedChatPartnerId = existing.id
                                } else {
                                    val newSessionId = "chat_" + System.currentTimeMillis()
                                    chatSessions.add(0, CustChatSession(
                                        id = newSessionId,
                                        farmerName = partnerName,
                                        farmerLocation = "${cropObj.village}, ${cropObj.taluka}",
                                        cropInterest = cropObj.name,
                                        lastMsg = "Inquiry about ${cropObj.name}",
                                        timestamp = "Just Now",
                                        unread = false,
                                        messages = listOf(
                                            CustChatMessage("Me", "Hello Patil Saheb, is the ${cropObj.name} available?", "Just Now")
                                        )
                                    ))
                                    selectedChatPartnerId = newSessionId
                                }
                                currentScreen = "chat_detail"
                            }
                        )
                    }
                    "cart" -> CustCartView(
                        cartItems = cartList,
                        deliveryAddress = savedAddress,
                        onCheckout = { address ->
                            if (cartList.isEmpty()) {
                                Toast.makeText(context, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                                return@CustCartView
                            }
                            // Create actual orders
                            cartList.forEach { cartItem ->
                                val newId = "ORD-" + (1000..9999).random()
                                ordersList.add(0, CustomerOrderItem(
                                    orderId = newId,
                                    cropName = cartItem.cropItem.name,
                                    imageEmoji = cartItem.cropItem.imageEmoji,
                                    farmerName = cartItem.cropItem.farmerName,
                                    quantity = cartItem.selectedQty,
                                    unit = cartItem.cropItem.unit,
                                    totalAmount = cartItem.selectedQty * cartItem.cropItem.price,
                                    status = "Pending",
                                    date = "18 July 2026",
                                    deliveryAddress = address
                                ))
                            }
                            cartList.clear()
                            Toast.makeText(context, "Order Placed Successfully! 🎉", Toast.LENGTH_LONG).show()
                            currentScreen = "orders"
                        }
                    )
                    "orders" -> CustOrdersView(
                        orders = ordersList,
                        onSelectOrder = { ordId ->
                            selectedOrderId = ordId
                            currentScreen = "order_details"
                        }
                    )
                    "order_details" -> {
                        val orderObj = ordersList.find { it.orderId == selectedOrderId } ?: ordersList.first()
                        CustOrderDetailsView(
                            order = orderObj,
                            onBack = { currentScreen = "orders" }
                        )
                    }
                    "chat_list" -> CustChatListView(
                        sessions = chatSessions,
                        onSelectSession = { sid ->
                            selectedChatPartnerId = sid
                            currentScreen = "chat_detail"
                        },
                        onBack = { currentScreen = "dashboard" }
                    )
                    "chat_detail" -> {
                        CustChatDetailView(
                            sessionId = selectedChatPartnerId,
                            chatSessions = chatSessions,
                            onBack = { currentScreen = "chat_list" }
                        )
                    }
                    "profile" -> CustProfileView(
                        name = customerName,
                        phone = customerPhone,
                        address = savedAddress,
                        language = userLanguage,
                        onSave = { n, p, a, l ->
                            customerName = n
                            customerPhone = p
                            savedAddress = a
                            userLanguage = l
                            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                        },
                        onLogout = {
                            Toast.makeText(context, "Logging out of AgroWorld...", Toast.LENGTH_SHORT).show()
                            navController.navigate("role_selection") {
                                popUpTo("dashboard/customer") { inclusive = true }
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

// ------------------ 1. CUSTOMER DASHBOARD SCREEN ------------------
@Composable
fun CustDashboardView(
    customerName: String,
    crops: List<CustomerCropItem>,
    onSearch: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onSelectCrop: (String) -> Unit,
    onAddToCart: (CustomerCropItem) -> Unit,
    onNavigateToChat: () -> Unit
) {
    var queryState by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Offers carousel data
    val offers = listOf(
        Pair("Direct from Pune Farms", "Get 100% Organic Onions & Mangoes with Free Kothrud delivery!"),
        Pair("Monsoon Rice Fest", "Scented Indrayani fresh harvest direct from Kamshet hills."),
        Pair("Local Farmers Pride", "Supporting 150+ smallholders in Junnar, Baramati & Haveli.")
    )
    var currentOfferIndex by remember { mutableStateOf(0) }

    // Auto rotate offer index
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(4000)
            currentOfferIndex = (currentOfferIndex + 1) % offers.size
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // TOP APP BAR REPLACEMENT
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
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(CustPrimary.copy(alpha = 0.12f))
                            .border(2.dp, CustPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (customerName.isNotEmpty()) customerName.first().toString() else "C",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hello, $customerName 👋",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Pune District Location Indicator",
                                tint = CustPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Pune District, MH",
                                fontSize = 12.sp,
                                color = CustTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onNavigateToChat,
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chats with Farmers",
                            tint = CustPrimary
                        )
                    }
                    IconButton(
                        onClick = { Toast.makeText(context, "No new marketplace updates.", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications bell icon",
                            tint = CustPrimary
                        )
                    }
                }
            }
        }

        // SEARCH BAR
        item {
            OutlinedTextField(
                value = queryState,
                onValueChange = { queryState = it },
                placeholder = { Text("Search fresh crops, farmers, categories...", color = CustTextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, tint = CustPrimary, contentDescription = "Search input field") },
                trailingIcon = {
                    if (queryState.isNotEmpty()) {
                        IconButton(onClick = { queryState = "" }) {
                            Icon(Icons.Default.Close, tint = CustTextSecondary, contentDescription = "Clear search")
                        }
                    } else {
                        IconButton(onClick = { onSearch(queryState) }) {
                            Icon(Icons.Default.ArrowForward, tint = CustPrimary, contentDescription = "Perform search")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CustPrimary,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cust_dashboard_search")
            )
        }

        // IMAGE CAROUSEL (SEASONAL OFFERS)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CustPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .shadow(2.dp, RoundedCornerShape(24.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(CustPrimary, CustSecondary)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.7f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CustAccent)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "SEASONAL OFFER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CustTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = offers[currentOfferIndex].first,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = offers[currentOfferIndex].second,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Large graphic visual absolute right
                    Text(
                        text = "🌾",
                        fontSize = 72.sp,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                    )
                }
            }
        }

        // CATEGORIES GRID HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore Categories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustTextPrimary
                )
                Text(
                    text = "See All",
                    fontSize = 13.sp,
                    color = CustPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSelectCategory("All") }
                )
            }
        }

        // CATEGORIES ROW
        item {
            val categories = listOf(
                Pair("Vegetables", "🧅"),
                Pair("Fruits", "🥭"),
                Pair("Grains", "🌾"),
                Pair("Pulses", "🫛"),
                Pair("Spices", "🌶️")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { (catName, emoji) ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                        modifier = Modifier
                            .width(82.dp)
                            .clickable { onSelectCategory(catName) }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(CustLightBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = catName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CustTextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // FEATURED CROPS HEADER
        item {
            Text(
                text = "Featured Fresh Harvest",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CustTextPrimary
            )
        }

        // TWO VERTICAL FEATURED CARDS SIDE BY SIDE
        item {
            val featuredCrops = crops.take(4)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (i in 0 until featuredCrops.size step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (j in 0 until 2) {
                            if (i + j < featuredCrops.size) {
                                val cropObj = featuredCrops[i + j]
                                FeaturedCropCard(
                                    crop = cropObj,
                                    onSelect = { onSelectCrop(cropObj.id) },
                                    onAdd = { onAddToCart(cropObj) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // NEARBY FARMERS SECTION
        item {
            Text(
                text = "Nearby Verified Farmers (Pune Dist.)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CustTextPrimary
            )
        }

        item {
            val farmers = listOf(
                Triple("Ramesh Patil", "Wagholi, Haveli Taluka", "★ 4.8 • Onions Expert"),
                Triple("Vilasrao Deshmukh", "Junnar Hills, Junnar", "★ 4.9 • Alphonso Orchardist"),
                Triple("Sanjay Gawade", "Kamshet Valley, Maval", "★ 4.7 • Rice Producer"),
                Triple("Dnyaneshwar Hande", "Baramati Agro, Baramati", "★ 4.9 • Turmeric Guru")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                farmers.forEach { (fName, loc, rating) ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.width(190.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CustSecondary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fName.first().toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = CustPrimary,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = fName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = CustTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = loc,
                                    fontSize = 10.sp,
                                    color = CustTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = rating,
                                    fontSize = 10.sp,
                                    color = CustAccent,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // RECENTLY ADDED CROPS HEADER
        item {
            Text(
                text = "Recently Added Produce",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CustTextPrimary
            )
        }

        // LISTING RECENTLY ADDED
        items(crops.takeLast(3)) { cropObj ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectCrop(cropObj.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CustLightBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cropObj.imageEmoji, fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = cropObj.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = CustTextPrimary
                            )
                            if (cropObj.isOrganic) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CustPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("Organic", fontSize = 8.sp, color = CustPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = "By ${cropObj.farmerName} • ${cropObj.village}",
                            fontSize = 11.sp,
                            color = CustTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "₹${cropObj.price} / ${cropObj.unit}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CustPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = "Star Rating icon", tint = CustAccent, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = cropObj.rating.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CustTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Sub-card used inside Grid list
@Composable
fun FeaturedCropCard(
    crop: CustomerCropItem,
    onSelect: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
        modifier = modifier
            .shadow(1.dp, RoundedCornerShape(20.dp))
            .clickable { onSelect() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(CustLightBg),
                contentAlignment = Alignment.Center
            ) {
                Text(crop.imageEmoji, fontSize = 52.sp)

                // Organic tag on top right
                if (crop.isOrganic) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CustPrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("100% Organic", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = crop.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CustTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${crop.farmerName} (${crop.taluka})",
                    fontSize = 11.sp,
                    color = CustTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${crop.price}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustPrimary
                        )
                        Text(
                            text = "per ${crop.unit}",
                            fontSize = 9.sp,
                            color = CustTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onAdd,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = CustPrimary),
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add crop to cart",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ------------------ 2. CATEGORIES VIEW ------------------
@Composable
fun CustCategoriesView(onSelectCategory: (String) -> Unit) {
    val allCategories = listOf(
        Triple("Vegetables", "🧅", "Fresh green leafy, roots, and organic onions"),
        Triple("Fruits", "🥭", "Fresh mangoes, papayas, and seasonal local treats"),
        Triple("Grains", "🌾", "Fragrant Indrayani rice, premium wheat, and millets"),
        Triple("Pulses", "🫛", "Rich protein tur dal, moong, and organic chana"),
        Triple("Spices", "🌶️", "Pure turmeric, red hot chili, and herbal masalas")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Browse Categories",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = CustTextPrimary,
            modifier = Modifier.padding(bottom = 4.dp, top = 8.dp)
        )
        Text(
            text = "Select an agricultural produce category to find direct listings from Pune farmers.",
            fontSize = 13.sp,
            color = CustTextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(allCategories) { (cat, emoji, desc) ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clickable { onSelectCategory(cat) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(CustLightBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 30.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = cat,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            fontSize = 10.sp,
                            color = CustTextSecondary,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Entire Catalog Quick button
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CustPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clickable { onSelectCategory("All") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AllInbox,
                            contentDescription = "Show All Catalog Products",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "All Produce",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Browse entire marketplace catalog.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ------------------ 3. PRODUCT LISTING VIEW (WITH FILTERING) ------------------
@Composable
fun CustProductListingView(
    crops: List<CustomerCropItem>,
    initialCategory: String,
    initialTaluka: String,
    initialKeyword: String,
    onBack: () -> Unit,
    onSelectCrop: (String) -> Unit,
    onAddToCart: (CustomerCropItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialKeyword) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedTaluka by remember { mutableStateOf(initialTaluka) }
    var maxPriceLimit by remember { mutableStateOf(5000.0) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val categories = listOf("All", "Vegetables", "Fruits", "Grains", "Pulses", "Spices")
    val talukas = listOf("All", "Haveli", "Baramati", "Junnar", "Khed", "Maval", "Shirur")

    // Filter logic
    val filteredCrops = crops.filter { crop ->
        val matchKeyword = searchQuery.isEmpty() || crop.name.contains(searchQuery, ignoreCase = true) || crop.farmerName.contains(searchQuery, ignoreCase = true)
        val matchCategory = selectedCategory == "All" || crop.category == selectedCategory
        val matchTaluka = selectedTaluka == "All" || crop.taluka == selectedTaluka
        val matchPrice = crop.price <= maxPriceLimit
        matchKeyword && matchCategory && matchTaluka && matchPrice
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP HEADER SEARCH BAR COMBINED
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CustPrimary)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search crops, villages...", fontSize = 13.sp, color = CustTextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, tint = CustPrimary, modifier = Modifier.size(18.dp), contentDescription = "Search text") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, tint = CustTextSecondary, modifier = Modifier.size(18.dp), contentDescription = "Clear search text")
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CustPrimary,
                    unfocusedBorderColor = Color(0xFFECEFF1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { showFilterSheet = !showFilterSheet },
                modifier = Modifier
                    .background(if (showFilterSheet) CustPrimary else Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Toggle filtering panel",
                    tint = if (showFilterSheet) Color.White else CustPrimary
                )
            }
        }

        // EXPANDABLE FILTER SHEET (BUILT NATIVE COMPOSE FOR ULTRA-RELIABILITY)
        AnimatedVisibility(
            visible = showFilterSheet,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                shape = RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Filter Listings", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CustTextPrimary)

                    // CATEGORY FILTER
                    Column {
                        Text("Category", fontSize = 12.sp, color = CustTextSecondary, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CustPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // TALUKA FILTER
                    Column {
                        Text("Taluka (Pune Region)", fontSize = 12.sp, color = CustTextSecondary, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            talukas.forEach { tal ->
                                val isSelected = selectedTaluka == tal
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedTaluka = tal },
                                    label = { Text(tal, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CustPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // PRICE SLIDER
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Max Price Limit", fontSize = 12.sp, color = CustTextSecondary, fontWeight = FontWeight.Bold)
                            Text("₹${maxPriceLimit.toInt()}", fontSize = 12.sp, color = CustPrimary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = maxPriceLimit.toFloat(),
                            onValueChange = { maxPriceLimit = it.toDouble() },
                            valueRange = 100f..5000f,
                            colors = SliderDefaults.colors(
                                thumbColor = CustPrimary,
                                activeTrackColor = CustPrimary,
                                inactiveTrackColor = CustLightBg
                            )
                        )
                    }

                    // RESET BUTTONS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                selectedCategory = "All"
                                selectedTaluka = "All"
                                maxPriceLimit = 5000.0
                                searchQuery = ""
                            }
                        ) {
                            Text("Reset All", color = CustTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { showFilterSheet = false },
                            colors = ButtonDefaults.buttonColors(containerColor = CustPrimary)
                        ) {
                            Text("Apply Filters")
                        }
                    }
                }
            }
        }

        // QUICK STATUS CHIPS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CustSecondary.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Showing ${filteredCrops.size} crops available",
                    fontSize = 11.sp,
                    color = CustPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // PRODUCT CARDS GRID/LIST
        if (filteredCrops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No direct crops found matching criteria.", fontWeight = FontWeight.Bold, color = CustTextPrimary)
                    Text("Try removing search keywords or filters.", fontSize = 12.sp, color = CustTextSecondary)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredCrops) { crop ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCrop(crop.id) }
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                                    .background(CustLightBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(crop.imageEmoji, fontSize = 56.sp)

                                if (crop.isOrganic) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CustPrimary)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("Organic", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    crop.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = CustTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "Farmer: ${crop.farmerName}",
                                    fontSize = 11.sp,
                                    color = CustTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "Village: ${crop.village} (${crop.taluka})",
                                    fontSize = 10.sp,
                                    color = CustTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "₹${crop.price}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CustPrimary
                                        )
                                        Text(
                                            "per ${crop.unit}",
                                            fontSize = 9.sp,
                                            color = CustTextSecondary
                                        )
                                    }

                                    IconButton(
                                        onClick = { onAddToCart(crop) },
                                        colors = IconButtonDefaults.iconButtonColors(containerColor = CustPrimary),
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add crop item directly to checkout cart",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 4. PRODUCT DETAILS VIEW ------------------
@Composable
fun CustProductDetailsView(
    crop: CustomerCropItem,
    onBack: () -> Unit,
    onAddToCart: (Double) -> Unit,
    onChatWithFarmer: () -> Unit
) {
    var selectedQtyState by remember { mutableStateOf(1.0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP BACK ACTION BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFECEFF1), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Return back button", tint = CustPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Crop Harvest Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CustTextPrimary)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LARGE CROP GRAPHIC PRESET
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CustLightBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(crop.imageEmoji, fontSize = 110.sp)

                        if (crop.isOrganic) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CustPrimary)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("100% Certified Organic", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // PRIMARY DETAIL TITLE BLOCK
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(crop.name, fontSize = 24.sp, fontWeight = FontWeight.Black, color = CustTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Category: ${crop.category} • Harvested in Pune District",
                                fontSize = 12.sp,
                                color = CustTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Rating Star Card
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CustAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Customer feedback star icon", tint = CustAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(crop.rating.toString(), fontWeight = FontWeight.Bold, color = CustTextPrimary, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price tag
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "₹${crop.price}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = CustPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "per ${crop.unit}",
                            fontSize = 14.sp,
                            color = CustTextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            // FARMER CONTACT INFO BLOCK
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(CustPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(crop.farmerName.first().toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CustPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(crop.farmerName, fontWeight = FontWeight.Bold, color = CustTextPrimary, fontSize = 15.sp)
                                Text("Village: ${crop.village}, ${crop.taluka} Taluka", fontSize = 12.sp, color = CustTextSecondary)
                                Text("Verified Active Seller • AgroWorld Certified", fontSize = 10.sp, color = CustPrimary, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(
                            onClick = onChatWithFarmer,
                            colors = IconButtonDefaults.iconButtonColors(containerColor = CustLightBg)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Chat with Local Farmer", tint = CustPrimary)
                        }
                    }
                }
            }

            // DESCRIPTION BOX
            item {
                Column {
                    Text("Crop Description", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CustTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = crop.description,
                        fontSize = 13.sp,
                        color = CustTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // DETAILS PARAMETER CARD
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Stock Available", fontSize = 12.sp, color = CustTextSecondary)
                            Text("${crop.availableQty} ${crop.unit}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CustTextPrimary)
                        }
                        Divider(color = Color(0xFFF1F5F9))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("District", fontSize = 12.sp, color = CustTextSecondary)
                            Text("Pune (Maharashtra)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CustTextPrimary)
                        }
                        Divider(color = Color(0xFFF1F5F9))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment Mode", fontSize = 12.sp, color = CustTextSecondary)
                            Text("AgroWorld Secure / COD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CustPrimary)
                        }
                    }
                }
            }

            // QUANTITY ADJUSTER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Select Order Quantity", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CustTextPrimary)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        IconButton(
                            onClick = { if (selectedQtyState > 1.0) selectedQtyState -= 1.0 },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease order volume")
                        }

                        Text(
                            text = "${selectedQtyState.toInt()} ${crop.unit}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustTextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        IconButton(
                            onClick = { if (selectedQtyState < crop.availableQty) selectedQtyState += 1.0 },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase order volume")
                        }
                    }
                }
            }

            // SPACER BOTTOM
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // BOTTOM MAIN ACTION BUTTONS
        Surface(
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Chat button secondary
                OutlinedButton(
                    onClick = onChatWithFarmer,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CustPrimary),
                    border = BorderStroke(1.5.dp, CustPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Chat icon")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat with Farmer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Add to Cart Primary
                Button(
                    onClick = { onAddToCart(selectedQtyState) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CustPrimary),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(54.dp)
                        .testTag("add_to_cart_detail_btn")
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart icon")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Cart", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ------------------ 5. CART VIEW ------------------
@Composable
fun CustCartView(
    cartItems: MutableList<CustomerCartItem>,
    deliveryAddress: String,
    onCheckout: (String) -> Unit
) {
    var deliveryAddressState by remember { mutableStateOf(deliveryAddress) }
    val totalPrice = cartItems.sumOf { it.cropItem.price * it.selectedQty }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "My Shopping Cart",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = CustTextPrimary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp)
        )
        Text(
            text = "Direct delivery of farm fresh crops from Pune Farmers.",
            fontSize = 12.sp,
            color = CustTextSecondary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Text("🛒", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your cart is empty!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CustTextPrimary)
                    Text(
                        "Explore crops inside the agricultural marketplace to place orders.",
                        color = CustTextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CustLightBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.cropItem.imageEmoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.cropItem.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = CustTextPrimary
                                )
                                Text(
                                    "Farmer: ${item.cropItem.farmerName}",
                                    fontSize = 11.sp,
                                    color = CustTextSecondary
                                )
                                Text(
                                    "Price: ₹${item.cropItem.price} / ${item.cropItem.unit}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CustPrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(
                                    onClick = { cartItems.remove(item) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove crop from checkout cart", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CustLightBg)
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (item.selectedQty > 1.0) {
                                                item.selectedQty -= 1.0
                                                // trigger list refresh by hacking item ref
                                                val idx = cartItems.indexOf(item)
                                                if (idx != -1) {
                                                    cartItems[idx] = item.copy(selectedQty = item.selectedQty)
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, modifier = Modifier.size(12.dp), contentDescription = "Decrement quantity")
                                    }

                                    Text(
                                        text = "${item.selectedQty.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CustTextPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            if (item.selectedQty < item.cropItem.availableQty) {
                                                item.selectedQty += 1.0
                                                val idx = cartItems.indexOf(item)
                                                if (idx != -1) {
                                                    cartItems[idx] = item.copy(selectedQty = item.selectedQty)
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Add, modifier = Modifier.size(12.dp), contentDescription = "Increment quantity")
                                    }
                                }
                            }
                        }
                    }
                }

                // DELIVERY DETAILS SECTION
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text("Delivery Address (Pune District)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CustTextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = deliveryAddressState,
                            onValueChange = { deliveryAddressState = it },
                            placeholder = { Text("Enter your detailed shipping address in Pune...") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CustPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // BREAKDOWN SUM CARD
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Produce Total Value", fontSize = 12.sp, color = CustTextSecondary)
                                Text("₹$totalPrice", fontSize = 12.sp, color = CustTextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("District Delivery Charge", fontSize = 12.sp, color = CustTextSecondary)
                                Text("FREE", fontSize = 12.sp, color = CustPrimary, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = Color(0xFFF1F5F9))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Amount Payable", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CustTextPrimary)
                                Text("₹$totalPrice", fontSize = 16.sp, fontWeight = FontWeight.Black, color = CustPrimary)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // CHEKOUT ACTION BAR
        if (cartItems.isNotEmpty()) {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { onCheckout(deliveryAddressState) },
                        colors = ButtonDefaults.buttonColors(containerColor = CustPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("checkout_place_order_btn")
                    ) {
                        Icon(Icons.Default.ShoppingCartCheckout, contentDescription = "Checkout cart and create actual orders")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Place Order • ₹$totalPrice",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ------------------ 6. MY ORDERS VIEW ------------------
@Composable
fun CustOrdersView(
    orders: List<CustomerOrderItem>,
    onSelectOrder: (String) -> Unit
) {
    var selectedTabState by remember { mutableStateOf(0) } // 0 = Pending, 1 = Confirmed, 2 = Delivered
    val tabs = listOf("Pending", "Confirmed", "Delivered")

    val filteredOrders = orders.filter { ord ->
        ord.status.equals(tabs[selectedTabState], ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "My Direct Orders",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = CustTextPrimary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp)
        )
        Text(
            text = "Track status of your orders bought from local Pune farmers.",
            fontSize = 12.sp,
            color = CustTextSecondary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )

        // TAB BAR FOR ORDERS
        TabRow(
            selectedTabIndex = selectedTabState,
            containerColor = Color.White,
            contentColor = CustPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabState]),
                    color = CustPrimary
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabState == index,
                    onClick = { selectedTabState = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    },
                    selectedContentColor = CustPrimary,
                    unselectedContentColor = CustTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No orders in this status category.", fontWeight = FontWeight.Bold, color = CustTextPrimary)
                    Text("Go to the catalog to buy premium crops.", fontSize = 11.sp, color = CustTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredOrders) { ord ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectOrder(ord.orderId) }
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
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CustLightBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(ord.imageEmoji, fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(ord.cropName, fontWeight = FontWeight.Bold, color = CustTextPrimary, fontSize = 15.sp)
                                        Text("ID: ${ord.orderId} • Date: ${ord.date}", fontSize = 11.sp, color = CustTextSecondary)
                                    }
                                }

                                // Custom status tag
                                val statusColor = when (ord.status) {
                                    "Pending" -> CustAccent
                                    "Confirmed" -> CustSecondary
                                    else -> CustPrimary
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(statusColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = ord.status,
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text("Farmer: ${ord.farmerName}", fontSize = 12.sp, color = CustTextSecondary)
                                    Text("Order Vol: ${ord.quantity.toInt()} ${ord.unit}", fontSize = 12.sp, color = CustTextSecondary)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Cost", fontSize = 10.sp, color = CustTextSecondary)
                                    Text("₹${ord.totalAmount}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = CustPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onSelectOrder(ord.orderId) },
                                colors = ButtonDefaults.buttonColors(containerColor = CustLightBg),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                            ) {
                                Text("View Receipt Details", color = CustPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 7. ORDER DETAILS VIEW ------------------
@Composable
fun CustOrderDetailsView(
    order: CustomerOrderItem,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = CustPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Order Invoice Receipt", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CustTextPrimary)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ORDER STATUS CARD WITH LARGE SYMBOLS
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, CustPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Order is ${order.status}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = CustPrimary
                        )
                        Text(
                            text = "Order reference ID: ${order.orderId}",
                            fontSize = 12.sp,
                            color = CustTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // OTP for pickup verification with farmer
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CustLightBg)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Verification PIN for Farmer: ", fontSize = 12.sp, color = CustTextSecondary)
                                Text(order.otpCode, fontWeight = FontWeight.Black, fontSize = 14.sp, color = CustPrimary)
                            }
                        }
                    }
                }
            }

            // DETAILED PRODUCT SPECIFICS
            item {
                Column {
                    Text("Product Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CustTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CustLightBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(order.imageEmoji, fontSize = 26.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(order.cropName, fontWeight = FontWeight.Bold, color = CustTextPrimary, fontSize = 15.sp)
                                Text("Sold by: ${order.farmerName}", fontSize = 12.sp, color = CustTextSecondary)
                                Text("Quantity ordered: ${order.quantity.toInt()} ${order.unit}", fontSize = 12.sp, color = CustTextSecondary)
                            }
                        }
                    }
                }
            }

            // BILLING BREAKDOWN RECEIPT
            item {
                Column {
                    Text("Payment Receipt Information", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CustTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Ordered Produce Rate", fontSize = 12.sp, color = CustTextSecondary)
                                Text("₹${order.totalAmount / order.quantity} / ${order.unit}", fontSize = 12.sp, color = CustTextPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Quantity", fontSize = 12.sp, color = CustTextSecondary)
                                Text("${order.quantity.toInt()} ${order.unit}", fontSize = 12.sp, color = CustTextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("AgroWorld Service Fee", fontSize = 12.sp, color = CustTextSecondary)
                                Text("FREE", fontSize = 12.sp, color = CustPrimary, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = Color(0xFFF1F5F9))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Amount Paid", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CustTextPrimary)
                                Text("₹${order.totalAmount}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = CustPrimary)
                            }
                        }
                    }
                }
            }

            // DELIVERY ADDRESS RECEIPT
            item {
                Column {
                    Text("Delivery Shipping Address", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CustTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Shipping destination pin", tint = CustPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Shipping Destination", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CustTextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = order.deliveryAddress,
                                    fontSize = 12.sp,
                                    color = CustTextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 8. CHAT FLOW VIEWS ------------------
@Composable
fun CustChatListView(
    sessions: List<CustChatSession>,
    onSelectSession: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CustPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Farmer Inquiries", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CustTextPrimary)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sessions) { sess ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectSession(sess.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CustPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sess.farmerName.first().toString(),
                                fontWeight = FontWeight.Bold,
                                color = CustPrimary,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    sess.farmerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = CustTextPrimary
                                )
                                Text(
                                    sess.timestamp,
                                    fontSize = 10.sp,
                                    color = CustTextSecondary
                                )
                            }
                            Text(
                                "Interested in: ${sess.cropInterest} • ${sess.farmerLocation}",
                                fontSize = 11.sp,
                                color = CustPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sess.lastMsg,
                                fontSize = 12.sp,
                                color = CustTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Unread indicator dot
                        if (sess.unread) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CustAccent)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustChatDetailView(
    sessionId: String,
    chatSessions: MutableList<CustChatSession>,
    onBack: () -> Unit
) {
    val currentSession = chatSessions.find { it.id == sessionId } ?: chatSessions.first()
    var typedMessageText by remember { mutableStateOf("") }
    val localContext = LocalContext.current

    // Chat messages list
    val currentMessages = remember(sessionId) {
        mutableStateListOf<CustChatMessage>().apply {
            addAll(currentSession.messages)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP CONTACT HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Return back", tint = CustPrimary)
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CustPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentSession.farmerName.first().toString(),
                    fontWeight = FontWeight.Bold,
                    color = CustPrimary,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    currentSession.farmerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CustTextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CustSecondary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Farmer Online • Pune District", fontSize = 10.sp, color = CustTextSecondary)
                }
            }

            IconButton(onClick = { Toast.makeText(localContext, "Calling ${currentSession.farmerName} directly...", Toast.LENGTH_SHORT).show() }) {
                Icon(Icons.Default.Call, tint = CustPrimary, contentDescription = "Call farmer directly")
            }
        }

        // MESSAGES FEED
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(currentMessages) { msg ->
                val isMe = msg.sender == "Me"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) CustPrimary else Color.White
                        ),
                        border = if (!isMe) BorderStroke(1.dp, Color(0xFFECEFF1)) else null,
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = if (isMe) Color.White else CustTextPrimary,
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.timestamp,
                                fontSize = 9.sp,
                                color = if (isMe) Color.White.copy(alpha = 0.7f) else CustTextSecondary,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // BOTTOM SEND CONTROL BAR
        Surface(
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Image sharing simulation button
                IconButton(
                    onClick = {
                        Toast.makeText(localContext, "Simulated: Harvest Photo attached!", Toast.LENGTH_SHORT).show()
                        // append a dummy image message
                        val newMsg = CustChatMessage("Me", "📸 Shared sample weight receipt slip", "Just Now", hasImage = true)
                        currentMessages.add(newMsg)

                        // Update back session object
                        val sessionIdx = chatSessions.indexOfFirst { it.id == sessionId }
                        if (sessionIdx != -1) {
                            val updatedMsgs = chatSessions[sessionIdx].messages.toMutableList()
                            updatedMsgs.add(newMsg)
                            chatSessions[sessionIdx] = chatSessions[sessionIdx].copy(
                                messages = updatedMsgs,
                                lastMsg = "📸 Shared weight slip",
                                timestamp = "Just Now"
                            )
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(CustLightBg, CircleShape)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Simulate image upload", tint = CustPrimary)
                }

                OutlinedTextField(
                    value = typedMessageText,
                    onValueChange = { typedMessageText = it },
                    placeholder = { Text("Ask about bulk price, pickup point...", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CustPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )

                IconButton(
                    onClick = {
                        if (typedMessageText.isNotEmpty()) {
                            val myMsg = CustChatMessage("Me", typedMessageText, "Just Now")
                            currentMessages.add(myMsg)

                            // Save original text to trigger auto-response
                            val savedTyped = typedMessageText
                            typedMessageText = ""

                            // Auto responses to make chat alive and engaging!
                            val index = chatSessions.indexOfFirst { it.id == sessionId }
                            if (index != -1) {
                                val updatedMsgs = chatSessions[index].messages.toMutableList()
                                updatedMsgs.add(myMsg)
                                chatSessions[index] = chatSessions[index].copy(
                                    messages = updatedMsgs,
                                    lastMsg = savedTyped,
                                    timestamp = "Just Now",
                                    unread = false
                                )
                            }

                            // Trigger simulated reply after a delay
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                val responseText = when {
                                    savedTyped.contains("price", ignoreCase = true) || savedTyped.contains("rate", ignoreCase = true) -> {
                                        "For bulk orders over 10 quintals, I can give ₹50 discount per unit."
                                    }
                                    savedTyped.contains("location", ignoreCase = true) || savedTyped.contains("where", ignoreCase = true) || savedTyped.contains("address", ignoreCase = true) -> {
                                        "My farm is located near Wagholi bypass. You are welcome to inspect anytime."
                                    }
                                    else -> {
                                        "Ram Ram! Yes, this sounds good. Please place the order on AgroWorld app for booking confirmation."
                                    }
                                }
                                val reply = CustChatMessage("Farmer", responseText, "Just Now")
                                currentMessages.add(reply)

                                if (index != -1) {
                                    val finalMsgs = chatSessions[index].messages.toMutableList()
                                    finalMsgs.add(reply)
                                    chatSessions[index] = chatSessions[index].copy(
                                        messages = finalMsgs,
                                        lastMsg = responseText,
                                        timestamp = "Just Now"
                                    )
                                }
                            }, 1500)
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = CustPrimary),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send text message", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ------------------ 9. PROFILE VIEW ------------------
@Composable
fun CustProfileView(
    name: String,
    phone: String,
    address: String,
    language: String,
    onSave: (String, String, String, String) -> Unit,
    onLogout: () -> Unit
) {
    var editName by remember { mutableStateOf(name) }
    var editPhone by remember { mutableStateOf(phone) }
    var editAddress by remember { mutableStateOf(address) }
    var editLanguage by remember { mutableStateOf(language) }

    var expandedLanguageMenu by remember { mutableStateOf(false) }
    val availableLanguages = listOf("English (Default)", "Marathi (मराठी)", "Hindi (हिन्दी)")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "My AgroWorld Profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CustTextPrimary
            )
            Text(
                text = "Configure your shipping addresses, personal profile, and display settings.",
                fontSize = 12.sp,
                color = CustTextSecondary
            )
        }

        // PROFILE PICTURE AVATAR
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(CustPrimary.copy(alpha = 0.1f))
                            .border(3.dp, CustPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (editName.isNotEmpty()) editName.first().toString() else "C",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = CustPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Verified Direct Buyer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CustLightBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // EDITABLE FIELDS
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Name Field
                Column {
                    Text("Full Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CustTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CustPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Phone Field
                Column {
                    Text("Mobile Number", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CustTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CustPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Shipping address Field
                Column {
                    Text("Shipping Address (Pune District)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CustTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CustPrimary),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Language Selection Field
                Column {
                    Text("Language Preference", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CustTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = editLanguage,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expandedLanguageMenu = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown languages")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CustPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedLanguageMenu = true }
                        )

                        DropdownMenu(
                            expanded = expandedLanguageMenu,
                            onDismissRequest = { expandedLanguageMenu = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            availableLanguages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang) },
                                    onClick = {
                                        editLanguage = lang
                                        expandedLanguageMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // SAVE BUTTON
        item {
            Button(
                onClick = { onSave(editName, editPhone, editAddress, editLanguage) },
                colors = ButtonDefaults.buttonColors(containerColor = CustPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Profile Changes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        // HELP & SUPPORT QUICK CARD
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Help & Support", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CustTextPrimary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupportAgent, contentDescription = "Support Agent", tint = CustPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("AgroWorld Pune Support Desk", fontSize = 12.sp, color = CustTextPrimary)
                        }
                        Text("Call", color = CustPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = "Legal Guidelines", tint = CustPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Direct Trading Rules & Policies", fontSize = 12.sp, color = CustTextPrimary)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Legal rules details", tint = CustTextSecondary)
                    }
                }
            }
        }

        // LOGOUT BUTTON
        item {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("cust_logout_btn")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out from Account", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// ==========================================
// 12. CUSTOMER OTP VERIFICATION SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOtpVerificationView(
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
                        CustBackground,
                        Color(0xFFE8F5E9)
                    )
                )
            )
            .testTag("customer_otp_verification_screen")
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
                        tint = CustPrimary
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
                            .background(CustPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "OTP Lock Icon",
                            tint = CustPrimary,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Verify Mobile Number",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A secure verification step is required before registering your customer profile.",
                    fontSize = 14.sp,
                    color = CustTextSecondary,
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
                                color = CustPrimary,
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
                                    Text("+91 ", fontWeight = FontWeight.Bold, color = CustPrimary, modifier = Modifier.padding(start = 12.dp))
                                },
                                trailingIcon = {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = CustPrimary.copy(alpha = 0.6f))
                                },
                                placeholder = { Text("10-digit mobile number", color = CustTextSecondary.copy(alpha = 0.5f)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                readOnly = isOtpSent,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CustPrimary,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color(0xFFF8FBF7),
                                    unfocusedContainerColor = if (isOtpSent) Color(0xFFF1F5F9) else Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("cust_phone_otp_input")
                            )
                        }

                        if (!isOtpSent) {
                            Button(
                                onClick = onSendOtp,
                                colors = ButtonDefaults.buttonColors(containerColor = CustPrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("cust_send_otp_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("Send Verification OTP", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Send", modifier = Modifier.size(16.dp), tint = Color.White)
                                }
                            }
                        } else {
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            // OTP input
                            Column {
                                Text(
                                    text = "Enter 4-Digit OTP Code",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CustPrimary,
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
                                        Icon(imageVector = Icons.Default.VpnKey, contentDescription = "OTP", tint = CustPrimary)
                                    },
                                    placeholder = { Text("Enter OTP code", color = CustTextSecondary.copy(alpha = 0.5f)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CustPrimary,
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedContainerColor = Color(0xFFF8FBF7)
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("cust_otp_digit_input")
                                )
                            }

                            // Verify OTP Button
                            Button(
                                onClick = onVerifyOtp,
                                colors = ButtonDefaults.buttonColors(containerColor = CustPrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("cust_verify_otp_btn")
                            ) {
                                Text("Verify & Register Profile", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                                    color = CustTextSecondary
                                )
                                if (timerSeconds == 0) {
                                    Text(
                                        text = "Resend OTP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CustPrimary,
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
                    tint = CustPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Verified by AgroWorld Trust Network",
                    fontSize = 12.sp,
                    color = CustTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


// ==========================================
// 13. CUSTOMER REGISTRATION FORM SCREEN
// ==========================================
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomerRegistrationFormView(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    verifiedPhone: String,
    village: String,
    onVillageChange: (String) -> Unit,
    selectedTaluka: String,
    onTalukaChange: (String) -> Unit,
    selectedCustomerType: String,
    onCustomerTypeChange: (String) -> Unit,
    selectedCategories: SnapshotStateList<String>,
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

    val customerTypes = remember {
        listOf("Individual Customer", "Retail Shop", "Hotel / Restaurant", "Wholesaler", "Vendor")
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
            "Organic Products" to "🌿"
        )
    }

    val avatars = listOf("🧑‍💻", "🛒", "🥗", "🍇", "🥑", "🥕", "🌾", "🏡")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustBackground)
            .testTag("customer_registration_screen")
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
                    text = "Customer Registration",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustPrimary
                )
                Text(
                    text = "Complete your customer profile to start purchasing direct from growers.",
                    fontSize = 13.sp,
                    color = CustTextSecondary,
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
                        text = "Choose Profile Avatar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustTextPrimary,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    // Big Circular Image Picker simulation
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(CustPrimary.copy(alpha = 0.1f))
                            .border(2.dp, CustPrimary, CircleShape)
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
                                    .background(if (isSelected) CustPrimary.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) CustPrimary else Color.Transparent,
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
                        color = CustPrimary
                    )

                    // Full Name
                    Column {
                        Text(
                            text = "Full Name (Required)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = onFullNameChange,
                            placeholder = { Text("Enter your first and last name", color = CustTextSecondary.copy(alpha = 0.5f)) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = "Name", tint = CustPrimary)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CustPrimary,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("cust_reg_full_name_input")
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
                            color = CustTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = "+91 $verifiedPhone",
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = "Verified Mobile", tint = CustPrimary)
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
                                        color = CustPrimary
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
                        text = "Location Information",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustPrimary
                    )

                    // Village
                    Column {
                        Text(
                            text = "Village / Town Name (Required)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = village,
                            onValueChange = onVillageChange,
                            placeholder = { Text("Enter your village or area name", color = CustTextSecondary.copy(alpha = 0.5f)) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.HomeWork, contentDescription = "Village", tint = CustPrimary)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CustPrimary,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("cust_reg_village_input")
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
                            color = CustTextSecondary,
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
                                            tint = CustPrimary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "$selectedTaluka Taluka",
                                            fontSize = 15.sp,
                                            color = CustTextPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Open Taluka Dropdown",
                                        tint = CustTextSecondary
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

            // Card 4: Customer Type & Interested Categories
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
                        text = "Customer Category Info",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustPrimary
                    )

                    // Customer Type selection
                    Column {
                        Text(
                            text = "Customer Type (Select One)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustTextSecondary,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            customerTypes.forEach { option ->
                                val isSelected = selectedCustomerType == option
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clickable { onCustomerTypeChange(option) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) CustPrimary.copy(alpha = 0.08f) else Color(0xFFF8FAFC)
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) CustPrimary else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { onCustomerTypeChange(option) },
                                            colors = RadioButtonDefaults.colors(selectedColor = CustPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = option,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) CustPrimary else CustTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))

                    // Crop Categories Multiselect
                    Column {
                        Text(
                            text = "Interested Crop Categories (Select all that apply)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustTextSecondary,
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
                                        .background(if (isSelected) CustPrimary else Color(0xFFF1F5F9))
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
                                        .testTag("cust_crop_chip_$catName")
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
                                            color = if (isSelected) Color.White else CustTextPrimary
                                        )
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
                colors = ButtonDefaults.buttonColors(containerColor = CustPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .testTag("create_customer_account_button")
            ) {
                Text(
                    text = "Create Customer Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


// ==========================================
// 14. CUSTOMER REGISTRATION SUCCESS SCREEN
// ==========================================
@Composable
fun CustomerRegistrationSuccessView(
    fullName: String,
    onProceedToDashboard: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("customer_success_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Animation simulation / Graphic
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .drawBehind {
                        drawCircle(
                            color = CustPrimary.copy(alpha = 0.08f),
                            radius = size.minDimension / 2
                        )
                        drawCircle(
                            color = CustPrimary.copy(alpha = 0.15f),
                            radius = size.minDimension / 2.6f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(CustPrimary),
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
                color = CustPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Congratulations $fullName! Your Customer account has been created successfully.",
                fontSize = 16.sp,
                color = CustTextPrimary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You are now part of our trusted buyer network. Let's explore fresh harvest and start buying premium agricultural products direct from farmers!",
                fontSize = 13.sp,
                color = CustTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(56.dp))

            Button(
                onClick = onProceedToDashboard,
                colors = ButtonDefaults.buttonColors(containerColor = CustPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .testTag("cust_go_to_dashboard_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Go to Dashboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

