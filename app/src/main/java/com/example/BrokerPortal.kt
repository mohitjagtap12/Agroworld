package com.example

import android.widget.Toast
import com.example.network.SessionManager
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

// ------------------ BROKER COLOR PALETTE ------------------
val BrokerPrimary = Color(0xFF2E7D32)       // Forest Green
val BrokerSecondary = Color(0xFF66BB6A)     // Soft Green
val BrokerAccent = Color(0xFFF9A825)        // Gold/Amber Accent
val BrokerBackground = Color(0xFFF8FBF7)    // Soft agricultural background
val BrokerCardBg = Color(0xFFFFFFFF)        // Card White
val BrokerTextPrimary = Color(0xFF212121)   // Deep charcoal
val BrokerTextSecondary = Color(0xFF616161) // Soft charcoal
val BrokerLightBg = Color(0xFFE8F5E9)       // Very light pastel green

// ------------------ DATA MODELS ------------------
data class BrokerCropListing(
    val id: String,
    val name: String,
    val farmerName: String,
    val village: String,
    val taluka: String, // "Haveli", "Baramati", "Junnar", "Khed", "Maval"
    val price: Double, // Price per unit in ₹
    val unit: String, // "Kg", "Quintal", "Ton"
    val qtyAvailable: Double,
    val description: String,
    val imageEmoji: String,
    val farmerRating: Double = 4.8
)

data class BrokerCustomerRequest(
    val id: String,
    val customerName: String,
    val requiredCrop: String,
    val qtyNeeded: Double,
    val unit: String, // "Quintal", "Ton", "Kg"
    val preferredTaluka: String,
    val requestedPrice: Double,
    val matchedFarmerName: String? = null
)

data class BrokerDeal(
    val id: String,
    val farmerName: String,
    val customerName: String,
    val cropName: String,
    val quantity: Double,
    val unit: String,
    val agreedPrice: Double,
    val totalAmount: Double,
    val status: String, // "Pending", "Ongoing", "Completed"
    val date: String,
    val farmerVillage: String,
    val farmerTaluka: String
)

data class BrokerBroadcastRequest(
    val id: String,
    val cropName: String,
    val category: String,
    val requiredQty: Double,
    val unit: String,
    val expectedPrice: Double,
    val requiredBefore: String,
    val preferredTaluka: String,
    val additionalNotes: String,
    val status: String = "Active", // "Active", "Closed"
    val responsesCount: Int = 0,
    val interestedFarmers: List<String> = emptyList()
)

data class BrokerChatMessage(
    val sender: String, // "Me", "Farmer:Ramesh Patil", "Customer:Abhishek"
    val text: String,
    val timestamp: String,
    val hasImage: Boolean = false
)

data class BrokerChatSession(
    val id: String,
    val partnerName: String,
    val partnerRole: String, // "Farmer", "Customer"
    val lastMsg: String,
    val timestamp: String,
    val unread: Boolean,
    val cropInterest: String,
    val messages: List<BrokerChatMessage>
)

// ------------------ MASTER STATE CONTROLLER ------------------
@Composable
fun BrokerPortalScreen(navController: NavController) {
    val context = LocalContext.current

    // Inner navigation screen state
    // "dashboard", "crop_listings", "crop_details", "customer_requests", "my_deals", "deal_details", "chat_list", "chat_detail", "profile", "broadcast_requirement", "farmer_broker_requests_sim"
    var currentScreen by remember { mutableStateOf("dashboard") }

    // Dynamic Database lists in memory
    val cropListings = remember { mutableStateListOf<BrokerCropListing>() }

    val customerRequests = remember { mutableStateListOf<BrokerCustomerRequest>() }

    val brokerDeals = remember { mutableStateListOf<BrokerDeal>() }

    val broadcastRequests = remember { mutableStateListOf<BrokerBroadcastRequest>() }

    val chatSessions = remember { mutableStateListOf<BrokerChatSession>() }

    // Portal Stage: "otp_verification", "registration", "success", "dashboard"
    var currentPortalStage by remember { mutableStateOf("otp_verification") }

    // OTP / Verification States
    var enteredPhone by remember { mutableStateOf("") }
    var enteredOtpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    // Registration Form Inputs
    var regFullName by remember { mutableStateOf("") }
    var regBusinessName by remember { mutableStateOf("") }
    var regBrokerType by remember { mutableStateOf("Individual Broker") }
    val regSelectedTalukas = remember { mutableStateListOf<String>() }
    val regSelectedCropCategories = remember { mutableStateListOf<String>() }
    var regPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Dynamic Profile Information for Broker
    var brokerName by remember { mutableStateOf(SessionManager.getInstance(context).userName.ifEmpty { "Agri Broker" }) }
    var brokerPhone by remember { mutableStateOf(SessionManager.getInstance(context).userPhone.ifEmpty { "+91 ----------" }) }
    var brokerFirmName by remember { mutableStateOf("AgroWorld Trading Agency") }
    var brokerType by remember { mutableStateOf("Commission Agent") }
    var brokerServiceArea by remember { mutableStateOf(SessionManager.getInstance(context).userDistrict.ifEmpty { "Trading Market" }) }
    var brokerPrimaryTrade by remember { mutableStateOf("Grains, Red Onions, Turmeric & Fruits") }
    var brokerPhotoUriState by remember { mutableStateOf<Uri?>(null) }

    // Interactive details variables
    var selectedCropListingId by remember { mutableStateOf("") }
    var selectedDealId by remember { mutableStateOf("") }
    var selectedChatSessionId by remember { mutableStateOf("") }

    // Navigation and layout
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
                        selectedTextColor = BrokerPrimary,
                        indicatorColor = BrokerPrimary,
                        unselectedIconColor = BrokerTextSecondary,
                        unselectedTextColor = BrokerTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "crop_listings" || currentScreen == "crop_details",
                    onClick = { currentScreen = "crop_listings" },
                    icon = { Icon(Icons.Default.Agriculture, contentDescription = "Crops") },
                    label = { Text("Listings", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = BrokerPrimary,
                        indicatorColor = BrokerPrimary,
                        unselectedIconColor = BrokerTextSecondary,
                        unselectedTextColor = BrokerTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "my_deals" || currentScreen == "deal_details",
                    onClick = { currentScreen = "my_deals" },
                    icon = { Icon(Icons.Default.Handshake, contentDescription = "Deals") },
                    label = { Text("My Deals", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = BrokerPrimary,
                        indicatorColor = BrokerPrimary,
                        unselectedIconColor = BrokerTextSecondary,
                        unselectedTextColor = BrokerTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "chat_list" || currentScreen == "chat_detail",
                    onClick = { currentScreen = "chat_list" },
                    icon = {
                        BadgedBox(badge = {
                            val count = chatSessions.count { it.unread }
                            if (count > 0) {
                                Badge(containerColor = BrokerAccent) {
                                    Text(count.toString(), color = BrokerTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }) {
                            Icon(Icons.Default.Chat, contentDescription = "Chat")
                        }
                    },
                    label = { Text("Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = BrokerPrimary,
                        indicatorColor = BrokerPrimary,
                        unselectedIconColor = BrokerTextSecondary,
                        unselectedTextColor = BrokerTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "profile",
                    onClick = { currentScreen = "profile" },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = BrokerPrimary,
                        indicatorColor = BrokerPrimary,
                        unselectedIconColor = BrokerTextSecondary,
                        unselectedTextColor = BrokerTextSecondary
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
                .background(BrokerBackground)
                .padding(if (currentPortalStage == "dashboard") paddingValues else PaddingValues(0.dp))
        ) {
            AnimatedContent(
                targetState = currentPortalStage,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                },
                label = "broker_portal_stage_transition"
            ) { stage ->
                when (stage) {
                    "otp_verification" -> {
                        BrokerOtpVerificationView(
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
                                    popUpTo("dashboard/broker") { inclusive = true }
                                }
                            }
                        )
                    }
                    "registration" -> {
                        BrokerRegistrationFormView(
                            fullName = regFullName,
                            onFullNameChange = { regFullName = it },
                            verifiedPhone = enteredPhone.ifEmpty { "9876543210" },
                            businessName = regBusinessName,
                            onBusinessNameChange = { regBusinessName = it },
                            selectedBrokerType = regBrokerType,
                            onBrokerTypeChange = { regBrokerType = it },
                            selectedTalukas = regSelectedTalukas,
                            selectedCropCategories = regSelectedCropCategories,
                            selectedPhotoUri = regPhotoUri,
                            onPhotoUriChange = { regPhotoUri = it },
                            onCreateAccount = {
                                if (regFullName.isBlank()) {
                                    Toast.makeText(context, "Please enter your Full Name", Toast.LENGTH_SHORT).show()
                                } else {
                                    brokerName = regFullName
                                    brokerPhone = "+91 $enteredPhone"
                                    brokerFirmName = regBusinessName.ifBlank { "Personal Brokerage" }
                                    brokerType = regBrokerType
                                    brokerServiceArea = if (regSelectedTalukas.isEmpty()) "Pune District (All)" else regSelectedTalukas.joinToString(", ")
                                    brokerPrimaryTrade = if (regSelectedCropCategories.isEmpty()) "All Crops" else regSelectedCropCategories.joinToString(", ")
                                    brokerPhotoUriState = regPhotoUri
                                    currentPortalStage = "success"
                                }
                            }
                        )
                    }
                    "success" -> {
                        BrokerRegistrationSuccessView(
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
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            label = "broker_flow_transition"
                        ) { target ->
                            when (target) {
                    "dashboard" -> BrokerDashboardView(
                        cropListings = cropListings,
                        customerRequests = customerRequests,
                        deals = brokerDeals,
                        broadcasts = broadcastRequests,
                        onNavigate = { currentScreen = it },
                        onSelectCrop = { id ->
                            selectedCropListingId = id
                            currentScreen = "crop_details"
                        },
                        onSelectDeal = { id ->
                            selectedDealId = id
                            currentScreen = "deal_details"
                        },
                        onCloseBroadcast = { id ->
                            val index = broadcastRequests.indexOfFirst { it.id == id }
                            if (index != -1) {
                                broadcastRequests[index] = broadcastRequests[index].copy(status = "Closed")
                                Toast.makeText(context, "Broadcast requirement closed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    "crop_listings" -> BrokerCropListingsView(
                        crops = cropListings,
                        onBack = { currentScreen = "dashboard" },
                        onSelectCrop = { id ->
                            selectedCropListingId = id
                            currentScreen = "crop_details"
                        }
                    )
                    "crop_details" -> {
                        val cropObj = cropListings.find { it.id == selectedCropListingId } ?: cropListings.first()
                        BrokerCropDetailsView(
                            crop = cropObj,
                            onBack = { currentScreen = "crop_listings" },
                            onContactFarmer = {
                                val sId = "chat_farmer_" + cropObj.farmerName.replace(" ", "_")
                                if (chatSessions.none { it.id == sId }) {
                                    chatSessions.add(0, BrokerChatSession(
                                        id = sId,
                                        partnerName = cropObj.farmerName,
                                        partnerRole = "Farmer",
                                        lastMsg = "Inquiring about crop: ${cropObj.name}",
                                        timestamp = "Just Now",
                                        unread = false,
                                        cropInterest = cropObj.name,
                                        messages = listOf(
                                            BrokerChatMessage("Me", "Ram Ram Patil Saheb, is the lot of ${cropObj.name} in ${cropObj.village} still available?", "Just Now")
                                        )
                                    ))
                                }
                                selectedChatSessionId = sId
                                currentScreen = "chat_detail"
                            },
                            onCreateDeal = {
                                // Dynamic creation of a deal
                                val newId = "deal_" + (104..999).random()
                                val clientName = customerRequests.firstOrNull { it.requiredCrop == cropObj.name }?.customerName ?: "General Buyer"
                                brokerDeals.add(0, BrokerDeal(
                                    id = newId,
                                    farmerName = cropObj.farmerName,
                                    customerName = clientName,
                                    cropName = cropObj.name,
                                    quantity = cropObj.qtyAvailable,
                                    unit = cropObj.unit,
                                    agreedPrice = cropObj.price,
                                    totalAmount = cropObj.qtyAvailable * cropObj.price,
                                    status = "Pending",
                                    date = "18 July 2026",
                                    farmerVillage = cropObj.village,
                                    farmerTaluka = cropObj.taluka
                                ))
                                selectedDealId = newId
                                Toast.makeText(context, "Deal Initiated successfully!", Toast.LENGTH_LONG).show()
                                currentScreen = "deal_details"
                            }
                        )
                    }
                    "customer_requests" -> BrokerCustomerRequestsView(
                        requests = customerRequests,
                        farmers = cropListings,
                        onBack = { currentScreen = "dashboard" },
                        onMatchFarmer = { reqId, cropName, taluka ->
                            // Find matching farmer
                            val match = cropListings.find { it.name == cropName && it.taluka == taluka }
                            if (match != null) {
                                val newId = "deal_" + (104..999).random()
                                val reqObj = customerRequests.find { it.id == reqId }!!
                                brokerDeals.add(0, BrokerDeal(
                                    id = newId,
                                    farmerName = match.farmerName,
                                    customerName = reqObj.customerName,
                                    cropName = cropName,
                                    quantity = reqObj.qtyNeeded,
                                    unit = reqObj.unit,
                                    agreedPrice = match.price,
                                    totalAmount = reqObj.qtyNeeded * match.price,
                                    status = "Ongoing",
                                    date = "18 July 2026",
                                    farmerVillage = match.village,
                                    farmerTaluka = match.taluka
                                ))
                                // Update request state
                                val reqIndex = customerRequests.indexOfFirst { it.id == reqId }
                                if (reqIndex != -1) {
                                    customerRequests[reqIndex] = reqObj.copy(matchedFarmerName = match.farmerName)
                                }
                                selectedDealId = newId
                                Toast.makeText(context, "Matched! Deal created with ${match.farmerName}", Toast.LENGTH_LONG).show()
                                currentScreen = "deal_details"
                            } else {
                                Toast.makeText(context, "No local farmer currently listing $cropName in $taluka Taluka.", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                    "my_deals" -> BrokerMyDealsView(
                        deals = brokerDeals,
                        onBack = { currentScreen = "dashboard" },
                        onSelectDeal = { id ->
                            selectedDealId = id
                            currentScreen = "deal_details"
                        }
                    )
                    "deal_details" -> {
                        val dealObj = brokerDeals.find { it.id == selectedDealId } ?: brokerDeals.first()
                        BrokerDealDetailsView(
                            deal = dealObj,
                            onBack = { currentScreen = "my_deals" },
                            onUpdateStatus = { newStatus ->
                                val index = brokerDeals.indexOfFirst { it.id == dealObj.id }
                                if (index != -1) {
                                    brokerDeals[index] = brokerDeals[index].copy(status = newStatus)
                                    Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onContactFarmer = {
                                val sId = "chat_farmer_" + dealObj.farmerName.replace(" ", "_")
                                if (chatSessions.none { it.id == sId }) {
                                    chatSessions.add(0, BrokerChatSession(
                                        id = sId,
                                        partnerName = dealObj.farmerName,
                                        partnerRole = "Farmer",
                                        lastMsg = "Regarding Deal ${dealObj.id}",
                                        timestamp = "Just Now",
                                        unread = false,
                                        cropInterest = dealObj.cropName,
                                        messages = listOf(
                                            BrokerChatMessage("Me", "Namaskar, regarding the deal for ${dealObj.cropName} of total amount ₹${dealObj.totalAmount}. Is everything ok with delivery?", "Just Now")
                                        )
                                    ))
                                }
                                selectedChatSessionId = sId
                                currentScreen = "chat_detail"
                            },
                            onContactCustomer = {
                                val sId = "chat_cust_" + dealObj.customerName.replace(" ", "_")
                                if (chatSessions.none { it.id == sId }) {
                                    chatSessions.add(0, BrokerChatSession(
                                        id = sId,
                                        partnerName = dealObj.customerName,
                                        partnerRole = "Customer",
                                        lastMsg = "Regarding Deal ${dealObj.id}",
                                        timestamp = "Just Now",
                                        unread = false,
                                        cropInterest = dealObj.cropName,
                                        messages = listOf(
                                            BrokerChatMessage("Me", "Hello, we have confirmed the deal from the farmer's side. The quantity is ${dealObj.quantity} ${dealObj.unit}.", "Just Now")
                                        )
                                    ))
                                }
                                selectedChatSessionId = sId
                                currentScreen = "chat_detail"
                            }
                        )
                    }
                    "chat_list" -> BrokerChatListView(
                        sessions = chatSessions,
                        onBack = { currentScreen = "dashboard" },
                        onSelectSession = { sid ->
                            selectedChatSessionId = sid
                            // Mark unread as false
                            val index = chatSessions.indexOfFirst { it.id == sid }
                            if (index != -1) {
                                chatSessions[index] = chatSessions[index].copy(unread = false)
                            }
                            currentScreen = "chat_detail"
                        }
                    )
                    "chat_detail" -> {
                        BrokerChatDetailView(
                            sessionId = selectedChatSessionId,
                            sessions = chatSessions,
                            onBack = { currentScreen = "chat_list" }
                        )
                    }
                    "broadcast_requirement" -> BrokerBroadcastRequirementView(
                        onBack = { currentScreen = "dashboard" },
                        onBroadcastSubmit = { name, cat, qty, unit, price, date, taluka, notes ->
                            val newId = "br_" + (103..999).random()
                            broadcastRequests.add(0, BrokerBroadcastRequest(
                                id = newId,
                                cropName = name,
                                category = cat,
                                requiredQty = qty,
                                unit = unit,
                                expectedPrice = price,
                                requiredBefore = date,
                                preferredTaluka = taluka,
                                additionalNotes = notes
                            ))
                            Toast.makeText(context, "Requirement Broadcasted successfully to Pune Farmers! 📣", Toast.LENGTH_LONG).show()
                            currentScreen = "dashboard"
                        }
                    )
                    "farmer_broker_requests_sim" -> FarmerBrokerRequestsSimView(
                        broadcasts = broadcastRequests,
                        onBack = { currentScreen = "dashboard" },
                        onInterestSubmitted = { brokerReqName, farmerName ->
                            // Add farmer to interested list and increase responses count
                            val index = broadcastRequests.indexOfFirst { it.cropName == brokerReqName }
                            if (index != -1) {
                                val currentReq = broadcastRequests[index]
                                if (!currentReq.interestedFarmers.contains(farmerName)) {
                                    val newList = currentReq.interestedFarmers + farmerName
                                    broadcastRequests[index] = currentReq.copy(
                                        responsesCount = currentReq.responsesCount + 1,
                                        interestedFarmers = newList
                                    )
                                }
                            }
                        }
                    )
                                "profile" -> BrokerProfileView(
                                    brokerName = brokerName,
                                    brokerPhone = brokerPhone,
                                    brokerFirmName = brokerFirmName,
                                    brokerType = brokerType,
                                    brokerServiceArea = brokerServiceArea,
                                    brokerPrimaryTrade = brokerPrimaryTrade,
                                    brokerPhotoUri = brokerPhotoUriState,
                                    onBack = { currentScreen = "dashboard" },
                                    onLogout = {
                                        Toast.makeText(context, "Logging out of AgroWorld Broker Portal...", Toast.LENGTH_SHORT).show()
                                        navController.navigate("role_selection") {
                                            popUpTo("dashboard/broker") { inclusive = true }
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

// ------------------ 1. BROKER DASHBOARD ------------------
@Composable
fun BrokerDashboardView(
    cropListings: List<BrokerCropListing>,
    customerRequests: List<BrokerCustomerRequest>,
    deals: List<BrokerDeal>,
    broadcasts: List<BrokerBroadcastRequest>,
    onNavigate: (String) -> Unit,
    onSelectCrop: (String) -> Unit,
    onSelectDeal: (String) -> Unit,
    onCloseBroadcast: (String) -> Unit
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
        // TOP APP BAR
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
                            .background(BrokerPrimary.copy(alpha = 0.12f))
                            .border(2.dp, BrokerPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "D",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrokerPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ram Ram, Deshmukh Saheb 🤝",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrokerTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Active Market Agent",
                                tint = BrokerPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Licensed Pune Broker • Wagholi Hub",
                                fontSize = 12.sp,
                                color = BrokerTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { Toast.makeText(context, "Market rates are steady today.", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = BrokerPrimary
                    )
                }
            }
        }

        // SIMULATION CONTROLLER BANNER
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrokerAccent.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, BrokerAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🌾 TEST SIMULATION MODE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD84315)
                        )
                        Text(
                            text = "Switch to simulated Farmer View to see and answer broadcasted requirements.",
                            fontSize = 11.sp,
                            color = BrokerTextSecondary
                        )
                    }
                    Button(
                        onClick = { onNavigate("farmer_broker_requests_sim") },
                        colors = ButtonDefaults.buttonColors(containerColor = BrokerAccent, contentColor = BrokerTextPrimary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Farmer View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // STATISTICS SECTION
        item {
            val activeDeals = deals.count { it.status == "Ongoing" || it.status == "Pending" }
            val completedDeals = deals.count { it.status == "Completed" }
            val openReqs = customerRequests.count { it.matchedFarmerName == null }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCardBlock("Active Deals", activeDeals.toString(), "In progress", BrokerPrimary, Modifier.weight(1f))
                StatCardBlock("Pending Req", openReqs.toString(), "To match", BrokerAccent, Modifier.weight(1f))
                StatCardBlock("Done Deals", completedDeals.toString(), "Successfully trade", Color(0xFF1565C0), Modifier.weight(1f))
            }
        }

        // BROADCAST CROP REQUIREMENT BUTTON
        item {
            Button(
                onClick = { onNavigate("broadcast_requirement") },
                colors = ButtonDefaults.buttonColors(containerColor = BrokerPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(1.dp, RoundedCornerShape(16.dp))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = "Broadcast Icon", tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Broadcast Bulk Crop Requirement 📣",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // QUICK ACTION GRID
        item {
            Text(
                text = "Quick Action Board",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )
        }

        item {
            val actions = listOf(
                Triple("Browse Crops", Icons.Default.Agriculture, "crop_listings"),
                Triple("Customer Requests", Icons.Default.ShoppingCart, "customer_requests"),
                Triple("All Broker Deals", Icons.Default.Handshake, "my_deals"),
                Triple("Broadcast Screen", Icons.Default.Campaign, "broadcast_requirement"),
                Triple("Internal Chats", Icons.Default.Chat, "chat_list")
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
                            .width(120.dp)
                            .clickable { onNavigate(route) }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BrokerPrimary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = label, tint = BrokerPrimary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrokerTextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // MY BROADCAST REQUESTS SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Broadcast Requests",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrokerTextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrokerPrimary.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Notice Board",
                        color = BrokerPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (broadcasts.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "You haven't broadcasted any crop requirements yet. Click above to broadcast.",
                        fontSize = 12.sp,
                        color = BrokerTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        } else {
            items(broadcasts) { request ->
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
                                Text("📢", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = request.cropName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = BrokerTextPrimary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (request.status == "Active") BrokerPrimary.copy(alpha = 0.15f)
                                        else Color.LightGray.copy(alpha = 0.3f)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = request.status,
                                    color = if (request.status == "Active") BrokerPrimary else BrokerTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Quantity Needed", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text("${request.requiredQty} ${request.unit}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Expected Price", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text("₹${request.expectedPrice} / ${request.unit}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                            }
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text("Preferred Taluka", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text(request.preferredTaluka, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                            }
                        }

                        if (request.additionalNotes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Notes: ${request.additionalNotes}",
                                fontSize = 11.sp,
                                color = BrokerTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Divider(color = Color(0xFFF1F5F9))

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "⚡ Farmer Responses",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrokerAccent
                                )
                                Text(
                                    text = if (request.responsesCount == 0) "Waiting for farmers..."
                                    else "${request.responsesCount} Farmer(s) interested: ${request.interestedFarmers.joinToString()}",
                                    fontSize = 11.sp,
                                    color = BrokerTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (request.status == "Active") {
                                TextButton(
                                    onClick = { onCloseBroadcast(request.id) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Close Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // RECENT DEAL ACTIVITY
        item {
            Text(
                text = "Recent Deal Activity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(deals.take(3)) { deal ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDeal(deal.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrokerLightBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤝", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${deal.cropName} Trade",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = BrokerTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (deal.status) {
                                            "Completed" -> Color(0xFFE8F5E9)
                                            "Ongoing" -> Color(0xFFFFF3E0)
                                            else -> Color(0xFFECEFF1)
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = deal.status,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (deal.status) {
                                        "Completed" -> BrokerPrimary
                                        "Ongoing" -> BrokerAccent
                                        else -> BrokerTextSecondary
                                    }
                                )
                            }
                        }
                        Text(
                            text = "Farmer: ${deal.farmerName} ➔ Customer: ${deal.customerName}",
                            fontSize = 11.sp,
                            color = BrokerTextSecondary,
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
                                text = "Vol: ${deal.quantity} ${deal.unit} @ ₹${deal.agreedPrice}/${deal.unit}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = BrokerTextPrimary
                            )
                            Text(
                                text = "₹${deal.totalAmount}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrokerPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCardBlock(
    title: String,
    count: String,
    subtitle: String,
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
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, fontSize = 11.sp, color = BrokerTextSecondary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(subtitle, fontSize = 9.sp, color = BrokerTextSecondary)
        }
    }
}

// ------------------ 2. BROWSE CROP LISTINGS ------------------
@Composable
fun BrokerCropListingsView(
    crops: List<BrokerCropListing>,
    onBack: () -> Unit,
    onSelectCrop: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTalukaFilter by remember { mutableStateOf("All") }
    var selectedCropFilter by remember { mutableStateOf("All") }

    val talukas = listOf("All", "Haveli", "Baramati", "Junnar", "Khed", "Maval", "Shirur")
    val cropTypes = listOf("All", "Onions", "Mangoes", "Paddy", "Turmeric", "Wheat")

    val filteredCrops = crops.filter { crop ->
        val matchesSearch = crop.name.contains(searchQuery, ignoreCase = true) ||
                crop.farmerName.contains(searchQuery, ignoreCase = true) ||
                crop.village.contains(searchQuery, ignoreCase = true)
        val matchesTaluka = selectedTalukaFilter == "All" || crop.taluka.lowercase() == selectedTalukaFilter.lowercase()
        val matchesCrop = selectedCropFilter == "All" || crop.name.contains(selectedCropFilter, ignoreCase = true)
        matchesSearch && matchesTaluka && matchesCrop
    }

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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Browse Crop Listings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by crop, farmer, village...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, tint = BrokerPrimary, contentDescription = "Search input") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrokerPrimary,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // FILTER CHIPS FOR TALUKAS
        Text("Taluka Filter:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            talukas.forEach { taluka ->
                val isSelected = selectedTalukaFilter == taluka
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTalukaFilter = taluka },
                    label = { Text(taluka, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrokerPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = BrokerTextSecondary
                    ),
                    border = BorderStroke(1.dp, if (isSelected) BrokerPrimary else Color(0xFFE2E8F0))
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // FILTER CHIPS FOR CROPS
        Text("Crop Filter:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            cropTypes.forEach { type ->
                val isSelected = selectedCropFilter == type
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCropFilter = type },
                    label = { Text(type, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrokerAccent,
                        selectedLabelColor = BrokerTextPrimary,
                        containerColor = Color.White,
                        labelColor = BrokerTextSecondary
                    ),
                    border = BorderStroke(1.dp, if (isSelected) BrokerAccent else Color(0xFFE2E8F0))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredCrops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌾", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No matching crop listings found.", color = BrokerTextSecondary, fontWeight = FontWeight.SemiBold)
                    Text("Try resetting your filter chips.", fontSize = 12.sp, color = BrokerTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredCrops) { crop ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCrop(crop.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BrokerLightBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(crop.imageEmoji, fontSize = 36.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = crop.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = BrokerTextPrimary
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = BrokerAccent, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(crop.farmerRating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = "Farmer: ${crop.farmerName} • ${crop.village} (${crop.taluka})",
                                    fontSize = 12.sp,
                                    color = BrokerTextSecondary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Available Quantity", fontSize = 9.sp, color = BrokerTextSecondary)
                                        Text("${crop.qtyAvailable} ${crop.unit}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Price / ${crop.unit}", fontSize = 9.sp, color = BrokerTextSecondary)
                                        Text("₹${crop.price}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
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

// ------------------ 3. CROP DETAILS ------------------
@Composable
fun BrokerCropDetailsView(
    crop: BrokerCropListing,
    onBack: () -> Unit,
    onContactFarmer: () -> Unit,
    onCreateDeal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(BrokerLightBg, BrokerBackground)
                    )
                )
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }

            Text(
                text = crop.imageEmoji,
                fontSize = 100.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrokerPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(crop.taluka + " Taluka", color = BrokerPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = BrokerAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Farmer Rating: ${crop.farmerRating}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = crop.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "₹${crop.price} per ${crop.unit}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // FARMER CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BrokerPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(crop.farmerName.first().toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Farmer Information", fontSize = 11.sp, color = BrokerTextSecondary)
                        Text(crop.farmerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                        Text("Village: ${crop.village}, Pune", fontSize = 12.sp, color = BrokerTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Crop Specifications", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = crop.description,
                fontSize = 13.sp,
                color = BrokerTextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFECEFF1), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Stock Available", fontSize = 11.sp, color = BrokerTextSecondary)
                    Text("${crop.qtyAvailable} ${crop.unit}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Estimated Trade Value", fontSize = 11.sp, color = BrokerTextSecondary)
                    Text("₹${crop.qtyAvailable * crop.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onContactFarmer,
                    colors = ButtonDefaults.buttonColors(containerColor = BrokerSecondary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Contact")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Contact Farmer", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onCreateDeal,
                    colors = ButtonDefaults.buttonColors(containerColor = BrokerPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Handshake, contentDescription = "Deal")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Deal 🤝", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ------------------ 4. CUSTOMER REQUESTS ------------------
@Composable
fun BrokerCustomerRequestsView(
    requests: List<BrokerCustomerRequest>,
    farmers: List<BrokerCropListing>,
    onBack: () -> Unit,
    onMatchFarmer: (String, String, String) -> Unit
) {
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Customer Requirements",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )
        }

        Text(
            text = "Bulk demands and wholesale requests placed by premium corporate retail/wholesale buyers in Pune.",
            fontSize = 12.sp,
            color = BrokerTextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(requests) { request ->
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
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8EAF6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Business, contentDescription = "Client", tint = Color(0xFF3F51B5), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(request.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrokerTextPrimary)
                                    Text("Corporate Buyer", fontSize = 10.sp, color = BrokerTextSecondary)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (request.matchedFarmerName != null) Color(0xFFE8F5E9)
                                        else Color(0xFFFFFDE7)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (request.matchedFarmerName != null) "Matched ✓" else "Active",
                                    color = if (request.matchedFarmerName != null) BrokerPrimary else BrokerAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrokerBackground, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Required Crop", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text(request.requiredCrop, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Quantity Needed", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text("${request.qtyNeeded} ${request.unit}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Preferred Origin Taluka", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text(request.preferredTaluka, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Target Price / ${request.unit}", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text("₹${request.requestedPrice}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (request.matchedFarmerName != null) {
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Matched with farmer: ${request.matchedFarmerName}",
                                fontSize = 12.sp,
                                color = BrokerPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Button(
                                onClick = { onMatchFarmer(request.id, request.requiredCrop, request.preferredTaluka) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrokerPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Handshake, contentDescription = "Match", tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Match Pune Farmer ➔", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 5. MY DEALS ------------------
@Composable
fun BrokerMyDealsView(
    deals: List<BrokerDeal>,
    onBack: () -> Unit,
    onSelectDeal: (String) -> Unit
) {
    var selectedTabState by remember { mutableStateOf(0) }
    val tabLabels = listOf("Pending", "Ongoing", "Completed")

    val filteredDeals = deals.filter { deal ->
        when (selectedTabState) {
            0 -> deal.status == "Pending"
            1 -> deal.status == "Ongoing"
            else -> deal.status == "Completed"
        }
    }

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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My Wholesale Deals",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )
        }

        // TABS
        TabRow(
            selectedTabIndex = selectedTabState,
            containerColor = Color.Transparent,
            contentColor = BrokerPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabState]),
                    color = BrokerPrimary
                )
            },
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            tabLabels.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTabState == index,
                    onClick = { selectedTabState = index },
                    text = { Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        }

        if (filteredDeals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤝", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No ${tabLabels[selectedTabState]} deals at this moment.", color = BrokerTextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredDeals) { deal ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectDeal(deal.id) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📦", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = deal.cropName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = BrokerTextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (deal.status) {
                                                "Completed" -> Color(0xFFE8F5E9)
                                                "Ongoing" -> Color(0xFFFFF3E0)
                                                else -> Color(0xFFECEFF1)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = deal.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (deal.status) {
                                            "Completed" -> BrokerPrimary
                                            "Ongoing" -> BrokerAccent
                                            else -> BrokerTextSecondary
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Producer (Farmer)", fontSize = 10.sp, color = BrokerTextSecondary)
                                    Text(deal.farmerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                                    Text("${deal.farmerVillage} (${deal.farmerTaluka})", fontSize = 11.sp, color = BrokerTextSecondary)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Consumer (Buyer)", fontSize = 10.sp, color = BrokerTextSecondary)
                                    Text(deal.customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                                    Text("Pune HQ", fontSize = 11.sp, color = BrokerTextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Volume: ${deal.quantity} ${deal.unit} @ ₹${deal.agreedPrice}",
                                    fontSize = 12.sp,
                                    color = BrokerTextSecondary
                                )

                                Text(
                                    text = "₹${deal.totalAmount}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrokerPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onSelectDeal(deal.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrokerPrimary.copy(alpha = 0.12f), contentColor = BrokerPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("View Deal Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 6. DEAL DETAILS ------------------
@Composable
fun BrokerDealDetailsView(
    deal: BrokerDeal,
    onBack: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onContactFarmer: () -> Unit,
    onContactCustomer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Deal Contract #${deal.id}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )
        }

        // AGRIBUSINESS HEADER
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Commodity Deal Details", fontSize = 12.sp, color = BrokerTextSecondary, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrokerPrimary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(deal.status, color = BrokerPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = deal.cropName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrokerTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Total Valuation: ₹${deal.totalAmount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrokerPrimary
                )

                Text(
                    text = "Created on: ${deal.date} • Brokerage: 1.5% fixed",
                    fontSize = 11.sp,
                    color = BrokerTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PARTIES INVOLVED CARDS
        Text("Parties Involved", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        // FARMER
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BrokerLightBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧑‍🌾", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("PRODUCER (FARMER)", fontSize = 9.sp, color = BrokerTextSecondary, fontWeight = FontWeight.Bold)
                        Text(deal.farmerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                        Text("${deal.farmerVillage}, Taluka ${deal.farmerTaluka}", fontSize = 11.sp, color = BrokerTextSecondary)
                    }
                }

                IconButton(onClick = onContactFarmer) {
                    Icon(Icons.Default.Chat, contentDescription = "Chat with farmer", tint = BrokerPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CUSTOMER
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8EAF6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏢", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("CUSTOMER (BUYER)", fontSize = 9.sp, color = BrokerTextSecondary, fontWeight = FontWeight.Bold)
                        Text(deal.customerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                        Text("Pune Warehouse Logistics", fontSize = 11.sp, color = BrokerTextSecondary)
                    }
                }

                IconButton(onClick = onContactCustomer) {
                    Icon(Icons.Default.Chat, contentDescription = "Chat with customer", tint = Color(0xFF3F51B5))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TRANSACTION DETAILS
        Text("Transaction Particulars", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Gross Volume", fontSize = 13.sp, color = BrokerTextSecondary)
                    Text("${deal.quantity} ${deal.unit}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rate / ${deal.unit}", fontSize = 13.sp, color = BrokerTextSecondary)
                    Text("₹${deal.agreedPrice}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Quality Standard", fontSize = 13.sp, color = BrokerTextSecondary)
                    Text("A-Grade Double Filtered", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery Mode", fontSize = 13.sp, color = BrokerTextSecondary)
                    Text("Self Pick Up by Buyer", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // STATUS UPDATER ACTIONS
        Text("Contract Status Controls", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onUpdateStatus("Ongoing") },
                colors = ButtonDefaults.buttonColors(containerColor = BrokerAccent, contentColor = BrokerTextPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Mark Ongoing", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onUpdateStatus("Completed") },
                colors = ButtonDefaults.buttonColors(containerColor = BrokerPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1.2f)
            ) {
                Text("Mark Completed ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ------------------ 7. CHAT (CONVERSATION LIST & DETAIL) ------------------
@Composable
fun BrokerChatListView(
    sessions: List<BrokerChatSession>,
    onBack: () -> Unit,
    onSelectSession: (String) -> Unit
) {
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My Trades Chat Hub",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )
        }

        Text(
            text = "Active multi-party conversations with registered Pune farmers & direct buyers.",
            fontSize = 12.sp,
            color = BrokerTextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sessions) { session ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(
                        width = if (session.unread) 2.dp else 1.dp,
                        color = if (session.unread) BrokerPrimary else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectSession(session.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (session.partnerRole == "Farmer") BrokerPrimary.copy(alpha = 0.12f)
                                    else Color(0xFF3F51B5).copy(alpha = 0.12f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (session.partnerRole == "Farmer") "🧑‍🌾" else "🏢",
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = session.partnerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = BrokerTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (session.partnerRole == "Farmer") BrokerPrimary.copy(alpha = 0.15f)
                                                else Color(0xFFE8EAF6)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = session.partnerRole,
                                            fontSize = 8.sp,
                                            color = if (session.partnerRole == "Farmer") BrokerPrimary else Color(0xFF3F51B5),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = session.timestamp,
                                    fontSize = 11.sp,
                                    color = BrokerTextSecondary
                                )
                            }

                            Text(
                                text = "Interest: ${session.cropInterest}",
                                fontSize = 11.sp,
                                color = BrokerPrimary,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = session.lastMsg,
                                fontSize = 12.sp,
                                color = BrokerTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (session.unread) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(BrokerAccent)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrokerChatDetailView(
    sessionId: String,
    sessions: List<BrokerChatSession>,
    onBack: () -> Unit
) {
    val session = sessions.find { it.id == sessionId } ?: sessions.first()
    var typedText by remember { mutableStateOf("") }
    val localMessages = remember { mutableStateListOf<BrokerChatMessage>().apply { addAll(session.messages) } }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // CHAT HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 10.dp, horizontal = 12.dp)
                .drawBehind {
                    drawLine(
                        color = Color(0xFFECEFF1),
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BrokerPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (session.partnerRole == "Farmer") "🧑‍🌾" else "🏢", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.partnerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BrokerTextPrimary
                )
                Text(
                    text = "${session.partnerRole} • Local trading inquiry for ${session.cropInterest}",
                    fontSize = 11.sp,
                    color = BrokerTextSecondary
                )
            }

            IconButton(
                onClick = { Toast.makeText(context, "Initiating high-quality voice call...", Toast.LENGTH_SHORT).show() }
            ) {
                Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = BrokerPrimary)
            }
        }

        // CHAT MESSAGES PANEL
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            items(localMessages) { msg ->
                val isMe = msg.sender == "Me"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 0.dp,
                            bottomEnd = if (isMe) 0.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) BrokerPrimary else Color.White
                        ),
                        border = BorderStroke(1.dp, if (isMe) BrokerPrimary else Color(0xFFECEFF1)),
                        modifier = Modifier.fillMaxWidth(0.82f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (!isMe) {
                                Text(
                                    text = msg.sender,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrokerSecondary,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = if (isMe) Color.White else BrokerTextPrimary,
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.timestamp,
                                fontSize = 9.sp,
                                color = if (isMe) Color.White.copy(alpha = 0.7f) else BrokerTextSecondary,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // TEXT EDITOR BOTTOM INPUT PANEL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { Toast.makeText(context, "Attach crop image or grade lab report...", Toast.LENGTH_SHORT).show() }
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add image", tint = BrokerPrimary)
            }

            OutlinedTextField(
                value = typedText,
                onValueChange = { typedText = it },
                placeholder = { Text("Type wholesale message...", fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrokerPrimary,
                    unfocusedBorderColor = Color(0xFFECEFF1),
                    focusedContainerColor = Color(0xFFF8FBF7),
                    unfocusedContainerColor = Color(0xFFF8FBF7)
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
            )

            IconButton(
                onClick = {
                    if (typedText.isNotBlank()) {
                        localMessages.add(BrokerChatMessage("Me", typedText, "12:16 PM"))
                        typedText = ""
                    }
                },
                enabled = typedText.isNotBlank(),
                modifier = Modifier
                    .background(if (typedText.isNotBlank()) BrokerPrimary else Color.LightGray, CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send message", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ------------------ 9. BROADCAST CROP REQUIREMENT SCREEN (UNIQUE FEATURE) ------------------
@Composable
fun BrokerBroadcastRequirementView(
    onBack: () -> Unit,
    onBroadcastSubmit: (String, String, Double, String, Double, String, String, String) -> Unit
) {
    var cropName by remember { mutableStateOf("") }
    var cropCategory by remember { mutableStateOf("Vegetables") }
    var requiredQty by remember { mutableStateOf("") }
    var quantityUnit by remember { mutableStateOf("Quintal") }
    var expectedPrice by remember { mutableStateOf("") }
    var requiredBeforeDate by remember { mutableStateOf("25 July 2026") }
    var preferredTaluka by remember { mutableStateOf("Haveli") }
    var additionalNotes by remember { mutableStateOf("") }

    val categories = listOf("Vegetables", "Fruits", "Grains", "Pulses", "Spices", "Cash Crop")
    val units = listOf("Quintal", "Ton", "Kg")
    val talukas = listOf("Haveli", "Baramati", "Junnar", "Khed", "Maval", "Shirur", "Indapur")

    var categoryExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }
    var talukaExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Broadcast Bulk Requirement",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )
        }

        Text(
            text = "Post a wholesale agricultural notice. This will instantly show up on the notice boards of registered Pune farmers.",
            fontSize = 12.sp,
            color = BrokerTextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Crop Name
                Text("Crop / Commodity Name *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
                OutlinedTextField(
                    value = cropName,
                    onValueChange = { cropName = it },
                    placeholder = { Text("e.g. Pune Red Onions, Indrayani Paddy") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrokerPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp)
                )

                // Category Dropdown
                Text("Crop Category *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
                Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp)) {
                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrokerTextPrimary)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cropCategory)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand category selection")
                        }
                    }
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    cropCategory = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Required Quantity & Unit in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("Required Quantity *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
                        OutlinedTextField(
                            value = requiredQty,
                            onValueChange = { requiredQty = it },
                            placeholder = { Text("e.g. 150") },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrokerPrimary),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                            OutlinedButton(
                                onClick = { unitExpanded = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrokerTextPrimary)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(quantityUnit)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand unit selection")
                                }
                            }
                            DropdownMenu(
                                expanded = unitExpanded,
                                onDismissRequest = { unitExpanded = false }
                            ) {
                                units.forEach { un ->
                                    DropdownMenuItem(
                                        text = { Text(un) },
                                        onClick = {
                                            quantityUnit = un
                                            unitExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Expected Price
                Text("Expected Buy Price (₹) per $quantityUnit *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
                OutlinedTextField(
                    value = expectedPrice,
                    onValueChange = { expectedPrice = it },
                    placeholder = { Text("e.g. 1850") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrokerPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp)
                )

                // Required Before Date
                Text("Deliver Required Before (Date) *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
                OutlinedTextField(
                    value = requiredBeforeDate,
                    onValueChange = { requiredBeforeDate = it },
                    placeholder = { Text("e.g. 25 July 2026") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrokerPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp)
                )

                // Preferred Taluka
                Text("Preferred Origin Taluka *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
                Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp)) {
                    OutlinedButton(
                        onClick = { talukaExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrokerTextPrimary)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(preferredTaluka)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand taluka selection")
                        }
                    }
                    DropdownMenu(
                        expanded = talukaExpanded,
                        onDismissRequest = { talukaExpanded = false }
                    ) {
                        talukas.forEach { tk ->
                            DropdownMenuItem(
                                text = { Text(tk) },
                                onClick = {
                                    preferredTaluka = tk
                                    talukaExpanded = false
                                }
                            )
                        }
                    }
                }

                // Additional Notes
                Text("Additional Specifications / Notes", fontSize = 12.sp, color = BrokerTextSecondary)
                OutlinedTextField(
                    value = additionalNotes,
                    onValueChange = { additionalNotes = it },
                    placeholder = { Text("Provide details e.g., moisture under 12%, double bagged, organic compost only...") },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrokerPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(top = 4.dp, bottom = 16.dp)
                )

                // SUBMIT
                Button(
                    onClick = {
                        val parsedQty = requiredQty.toDoubleOrNull()
                        val parsedPrice = expectedPrice.toDoubleOrNull()
                        if (cropName.isBlank() || parsedQty == null || parsedPrice == null) {
                            Toast.makeText(context, "Please enter all valid requested fields marked with *", Toast.LENGTH_SHORT).show()
                        } else {
                            onBroadcastSubmit(
                                cropName,
                                cropCategory,
                                parsedQty,
                                quantityUnit,
                                parsedPrice,
                                requiredBeforeDate,
                                preferredTaluka,
                                additionalNotes
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrokerPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = "Broadcast", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Broadcast Wholesale Request 📣", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

// ------------------ SIMULATOR: FARMER VIEW OF BROKER REQUESTS ------------------
@Composable
fun FarmerBrokerRequestsSimView(
    broadcasts: List<BrokerBroadcastRequest>,
    onBack: () -> Unit,
    onInterestSubmitted: (String, String) -> Unit
) {
    var interestedFarmerName by remember { mutableStateOf("Patil Ramesh") }
    var responseLogs = remember { mutableStateListOf<String>() }

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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Farmer View (Broker Demands)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )
        }

        // Explanation card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BrokerLightBg),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "This screen simulates how a Farmer registered on AgroWorld views your posted requirements. You can test clicking 'I'm Interested' below to see the count change on the Broker Dashboard!",
                    fontSize = 11.sp,
                    color = BrokerPrimary,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Test Farmer Identity:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = interestedFarmerName,
                        onValueChange = { interestedFarmerName = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrokerPrimary),
                        modifier = Modifier.height(38.dp).width(160.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(broadcasts) { req ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("BROKER REQUEST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrokerAccent)
                                Text("Deshmukh Trading Co.", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BrokerPrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(req.status, color = BrokerPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Required Crop", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text(req.cropName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Expected Price", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text("₹${req.expectedPrice} / ${req.unit}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Quantity Required", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text("${req.requiredQty} ${req.unit}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BrokerTextPrimary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Preferred Taluka", fontSize = 10.sp, color = BrokerTextSecondary)
                                Text(req.preferredTaluka, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BrokerTextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Deadline: Required before ${req.requiredBefore}", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)

                        if (req.additionalNotes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Broker Note: ${req.additionalNotes}", fontSize = 11.sp, color = BrokerTextSecondary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onInterestSubmitted(req.cropName, interestedFarmerName)
                                    responseLogs.add("Expressed interest in ${req.cropName} as $interestedFarmerName")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrokerPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text("I'm Interested 🙋‍♂️", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    responseLogs.add("Contacting Broker regarding ${req.cropName} as $interestedFarmerName")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrokerSecondary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text("Contact Broker", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (responseLogs.isNotEmpty()) {
            Text("Activity Log:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
            LazyColumn(
                modifier = Modifier.height(100.dp).fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).border(1.dp, Color(0xFFECEFF1)).padding(8.dp)
            ) {
                items(responseLogs.reversed()) { log ->
                    Text("• $log", fontSize = 11.sp, color = BrokerTextPrimary, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ------------------ 8. PROFILE ------------------
@Composable
fun BrokerProfileView(
    brokerName: String,
    brokerPhone: String,
    brokerFirmName: String,
    brokerType: String,
    brokerServiceArea: String,
    brokerPrimaryTrade: String,
    brokerPhotoUri: Uri?,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrokerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Broker Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(BrokerPrimary.copy(alpha = 0.12f))
                    .border(3.dp, BrokerPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (brokerPhotoUri != null) {
                    Text("📸", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                } else {
                    val initial = if (brokerName.isNotEmpty()) brokerName.substring(0, 1).uppercase() else "B"
                    Text(initial, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = brokerName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrokerTextPrimary
            )

            Text(
                text = brokerType,
                fontSize = 12.sp,
                color = BrokerTextSecondary
            )

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BrokerPrimary)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text("License ID: MH-PUN-BRK-9281", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Business Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                ProfileRowItem("Firm Name", brokerFirmName)
                Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))
                ProfileRowItem("Service Area", brokerServiceArea)
                Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))
                ProfileRowItem("Mobile Number", brokerPhone)
                Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))
                ProfileRowItem("Primary Trade", brokerPrimaryTrade)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("App Preferences", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                ProfileRowItem("Language", "English (Default)")
                Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))
                ProfileRowItem("Wholesale Alerts", "Instant Notifications Active")
                Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))
                ProfileRowItem("Help & Support", "support@agroworld.org")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84315)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout from Broker Account", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ProfileRowItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = BrokerTextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
    }
}

// ------------------ 9. BROKER REGISTRATION STAGE VIEWS ------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrokerOtpVerificationView(
    phone: String,
    onPhoneChange: (String) -> Unit,
    otpCode: String,
    onOtpChange: (String) -> Unit,
    isOtpSent: Boolean,
    onSendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokerBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("broker_otp_verification_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // AgroWorld Brand Header
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BrokerPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Broker Security",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Broker Portal Verification",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BrokerTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Verify your mobile number to unlock wholesale trading, secure crop bidding, and direct farmer matching.",
            fontSize = 14.sp,
            color = BrokerTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Mobile Number",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrokerTextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            onPhoneChange(it)
                        }
                    },
                    placeholder = { Text("Enter 10-digit mobile number") },
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        ) {
                            Text("+91 ", fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Divider(
                                color = Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .height(20.dp)
                                    .width(1.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    enabled = !isOtpSent,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrokerPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("broker_phone_otp_input")
                )

                if (isOtpSent) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Enter 4-Digit OTP Code",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrokerTextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                onOtpChange(it)
                            }
                        },
                        placeholder = { Text("Enter OTP (e.g. 1234)") },
                        leadingIcon = {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = "OTP Icon", tint = BrokerPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrokerPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("broker_otp_digit_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrokerPrimary.copy(alpha = 0.08f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 Simulation Mode: Use OTP code \"1234\" to verify.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrokerPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (!isOtpSent) {
                    onSendOtp()
                } else {
                    onVerifyOtp()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrokerPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag(if (!isOtpSent) "broker_send_otp_btn" else "broker_verify_otp_btn")
        ) {
            Text(
                text = if (!isOtpSent) "Send OTP Verification" else "Verify OTP & Continue",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onCancel,
            border = BorderStroke(1.dp, BrokerPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Back to Role Selection",
                fontSize = 14.sp,
                color = BrokerPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BrokerRegistrationFormView(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    verifiedPhone: String,
    businessName: String,
    onBusinessNameChange: (String) -> Unit,
    selectedBrokerType: String,
    onBrokerTypeChange: (String) -> Unit,
    selectedTalukas: SnapshotStateList<String>,
    selectedCropCategories: SnapshotStateList<String>,
    selectedPhotoUri: Uri?,
    onPhotoUriChange: (Uri?) -> Unit,
    onCreateAccount: () -> Unit
) {
    val context = LocalContext.current
    var nameError by remember { mutableStateOf(false) }

    // Taluka state definitions
    val puneTalukas = listOf(
        "Haveli", "Khed", "Maval", "Junnar", "Baramati", "Shirur", 
        "Indapur", "Daund", "Ambegaon", "Purandar", "Bhor", "Velhe", "Mulshi"
    )
    var talukaQuery by remember { mutableStateOf("") }
    var isTalukaExpanded by remember { mutableStateOf(false) }

    val cropCategories = listOf(
        Pair("Vegetables", "🥦"),
        Pair("Fruits", "🍎"),
        Pair("Grains", "🌾"),
        Pair("Pulses", "🫘"),
        Pair("Spices", "🌶️"),
        Pair("Flowers", "🌸"),
        Pair("Sugarcane", "🎋"),
        Pair("Other", "📦")
    )

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoUriChange(uri)
            Toast.makeText(context, "Custom profile picture loaded!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokerBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Simple Top Sticky Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .drawBehind {
                    drawLine(
                        color = Color(0xFFECEFF1),
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrokerPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SupportAgent,
                    contentDescription = "Broker Portal",
                    tint = BrokerPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "AgroWorld Broker Registration",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrokerTextPrimary
                )
                Text(
                    text = "Step 2 of 2: Create Professional Profile",
                    fontSize = 11.sp,
                    color = BrokerTextSecondary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 20.dp)
                .testTag("broker_registration_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Profile Photo Row
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape)
                                .background(BrokerPrimary.copy(alpha = 0.08f))
                                .border(2.dp, BrokerPrimary, CircleShape)
                                .clickable { imageLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedPhotoUri != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📸", fontSize = 24.sp)
                                    Text("Selected", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = BrokerPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Optional", fontSize = 9.sp, color = BrokerTextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Profile Picture",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrokerTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the circular box to upload a professional business card or headshot photo from your device.",
                                fontSize = 11.sp,
                                color = BrokerTextSecondary
                            )
                        }
                    }
                }
            }

            item {
                // Personal Information Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrokerPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Full Name
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Full Name", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                            Text(" *", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                onFullNameChange(it)
                                nameError = it.isBlank()
                            },
                            placeholder = { Text("Enter your full registration name") },
                            isError = nameError,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrokerPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("broker_reg_full_name_input")
                        )
                        if (nameError) {
                            Text(
                                "Full Name is required to register as an AgroWorld Broker.",
                                color = Color.Red,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Verified Mobile Number (Read Only)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Verified Mobile Number", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BrokerPrimary.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("OTP Verified", color = BrokerPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = "+91 $verifiedPhone",
                            onValueChange = {},
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE2E8F0),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFF1F5F9),
                                unfocusedContainerColor = Color(0xFFF1F5F9)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                // Business Information Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Business Information",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrokerPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Business Name
                        Text("Business Name (Optional)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = businessName,
                            onValueChange = onBusinessNameChange,
                            placeholder = { Text("e.g. Deshmukh Trade Corp") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrokerPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("broker_reg_business_name_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Broker Type Selectors
                        Text("Broker Type Selection", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))

                        val brokerTypes = listOf("Individual Broker", "Commission Agent", "Trader", "Wholesale Buyer")
                        brokerTypes.forEach { type ->
                            val isSelected = selectedBrokerType == type
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) BrokerPrimary.copy(alpha = 0.08f) else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) BrokerPrimary else Color(0xFFE2E8F0),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onBrokerTypeChange(type) }
                                    .padding(12.dp)
                                    .testTag("broker_type_option_$type"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onBrokerTypeChange(type) },
                                    colors = RadioButtonDefaults.colors(selectedColor = BrokerPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(type, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrokerTextPrimary)
                                    val desc = when(type) {
                                        "Individual Broker" -> "Independent agricultural broker coordinating custom crop lots."
                                        "Commission Agent" -> "Licensed APMC Market Yard trader coordinating transactions for a commission fee."
                                        "Trader" -> "Direct buyer and seller transacting bulk agricultural volumes with cash settlement."
                                        else -> "Representing bulk industries, food processors, retail chains or direct food brands."
                                    }
                                    Text(desc, fontSize = 10.sp, color = BrokerTextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Service Area (Pune Talukas Multi-select dropdown)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Service Area Coverage",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrokerPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "Select one or multiple Pune District talukas where you primarily trade and service.",
                            fontSize = 11.sp,
                            color = BrokerTextSecondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Trigger Field
                        OutlinedCard(
                            onClick = { isTalukaExpanded = !isTalukaExpanded },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isTalukaExpanded) BrokerPrimary else Color(0xFFCBD5E1)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBF7)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedTalukas.isEmpty()) "Select Operating Talukas" else "${selectedTalukas.size} Talukas Selected",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedTalukas.isEmpty()) BrokerTextSecondary else BrokerPrimary
                                )
                                Icon(
                                    imageVector = if (isTalukaExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Expand",
                                    tint = BrokerPrimary
                                )
                            }
                        }

                        // Search and select custom inline view
                        AnimatedVisibility(visible = isTalukaExpanded) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    // Search Bar
                                    OutlinedTextField(
                                        value = talukaQuery,
                                        onValueChange = { talukaQuery = it },
                                        placeholder = { Text("Search Talukas (e.g. Haveli, Maval)") },
                                        singleLine = true,
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BrokerPrimary) },
                                        trailingIcon = {
                                            if (talukaQuery.isNotEmpty()) {
                                                IconButton(onClick = { talukaQuery = "" }) {
                                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = BrokerTextSecondary)
                                                }
                                            }
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrokerPrimary,
                                            unfocusedBorderColor = Color(0xFFCBD5E1)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Shortcut helpers
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Text(
                                            text = "+ Select All",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrokerPrimary,
                                            modifier = Modifier
                                                .clickable {
                                                    selectedTalukas.clear()
                                                    selectedTalukas.addAll(puneTalukas)
                                                }
                                                .background(Color.White, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )

                                        Text(
                                            text = "✕ Clear All",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFD84315),
                                            modifier = Modifier
                                                .clickable { selectedTalukas.clear() }
                                                .background(Color.White, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Divider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))

                                    // Search results list
                                    val filteredTalukas = puneTalukas.filter {
                                        it.contains(talukaQuery, ignoreCase = true)
                                    }

                                    Column(
                                        modifier = Modifier.heightIn(max = 180.dp).verticalScroll(rememberScrollState())
                                    ) {
                                        if (filteredTalukas.isEmpty()) {
                                            Text(
                                                "No talukas found matching search.",
                                                fontSize = 12.sp,
                                                color = BrokerTextSecondary,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        } else {
                                            filteredTalukas.forEach { talukaName ->
                                                val isChecked = selectedTalukas.contains(talukaName)
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            if (isChecked) {
                                                                selectedTalukas.remove(talukaName)
                                                            } else {
                                                                selectedTalukas.add(talukaName)
                                                            }
                                                        }
                                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Checkbox(
                                                        checked = isChecked,
                                                        onCheckedChange = { checked ->
                                                            if (checked) {
                                                                selectedTalukas.add(talukaName)
                                                            } else {
                                                                selectedTalukas.remove(talukaName)
                                                            }
                                                        },
                                                        colors = CheckboxDefaults.colors(checkedColor = BrokerPrimary)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(talukaName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BrokerTextPrimary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Selected Talukas Chips
                        if (selectedTalukas.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                selectedTalukas.forEach { talukaName ->
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = 3.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BrokerPrimary.copy(alpha = 0.1f))
                                            .border(1.dp, BrokerPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .clickable { selectedTalukas.remove(talukaName) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .testTag("broker_taluka_chip_$talukaName")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(talukaName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrokerPrimary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = BrokerPrimary,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Interested Crop Categories Chips
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Primary Crop Trading Interests",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrokerPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "Choose crop categories you handle for sourcing, trading or wholesale buying.",
                            fontSize = 11.sp,
                            color = BrokerTextSecondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            cropCategories.forEach { pair ->
                                val catName = pair.first
                                val emoji = pair.second
                                val isChecked = selectedCropCategories.contains(catName)

                                FilterChip(
                                    selected = isChecked,
                                    onClick = {
                                        if (isChecked) {
                                            selectedCropCategories.remove(catName)
                                        } else {
                                            selectedCropCategories.add(catName)
                                        }
                                    },
                                    label = { Text("$emoji $catName", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrokerPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFFF1F5F9),
                                        labelColor = BrokerTextPrimary
                                    ),
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .testTag("broker_crop_chip_$catName")
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Floating Footer with validation messages and Create Button
        Card(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                val isFormValid = fullName.isNotBlank()

                if (!isFormValid) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Warning", tint = Color(0xFFD84315), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Please enter your Full Name to create your Broker account.",
                            color = Color(0xFFD84315),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = {
                        if (fullName.isBlank()) {
                            nameError = true
                            Toast.makeText(context, "Full Name is required!", Toast.LENGTH_SHORT).show()
                        } else {
                            onCreateAccount()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) BrokerPrimary else Color(0xFF9E9E9E)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("create_broker_account_button")
                ) {
                    Text(
                        text = "Create Broker Account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun BrokerRegistrationSuccessView(
    fullName: String,
    onProceedToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokerBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("broker_success_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // High quality Success Illustration drawn on Canvas
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background soft green glowing radial circle
                drawCircle(
                    color = BrokerPrimary.copy(alpha = 0.15f),
                    radius = size.minDimension / 2f
                )
                // Center success badge
                drawCircle(
                    color = BrokerPrimary,
                    radius = size.minDimension / 3.2f
                )
            }
            // Overlapping Checkmark Icon
            Icon(
                Icons.Default.Check,
                contentDescription = "Success checkmark",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to AgroWorld!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = BrokerPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your Broker account has been created successfully.",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrokerTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hello $fullName, you can now start exploring daily farmers' crops, broadcasting wholesale purchase requirements, and negotiating direct deals.",
            fontSize = 13.sp,
            color = BrokerTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onProceedToDashboard,
            colors = ButtonDefaults.buttonColors(containerColor = BrokerPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("broker_go_to_dashboard_button")
        ) {
            Text(
                text = "Go to Dashboard",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
