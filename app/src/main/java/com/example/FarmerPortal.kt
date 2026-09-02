package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.widget.Toast
import com.example.network.SessionManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import kotlinx.coroutines.launch

// ------------------ DESIGN SYSTEM COLOR PALETTE ------------------
val FarmerPrimary = Color(0xFF2E7D32)      // Forest Green
val FarmerSecondary = Color(0xFF66BB6A)    // Secondary Light Green
val FarmerAccent = Color(0xFFF9A825)       // Amber Gold
val FarmerBackground = Color(0xFFF8FBF7)   // Minty Soft Background
val FarmerLightBg = Color(0xFFE8F5E9)      // Soft Green Tint
val FarmerSurface = Color(0xFFFFFFFF)      // Pure White
val FarmerCardBg = Color(0xFFFFFFFF)       // White
val FarmerTextPrimary = Color(0xFF212121)  // Dark Charcoal
val FarmerTextSecondary = Color(0xFF616161)// Medium Grey
val FarmerMuted = Color(0xFFB0BEC5)        // Slate Gray
val FarmerSuccess = Color(0xFF4CAF50)      // Vivid Green

// ------------------ DATA MODELS ------------------
data class FarmerCrop(
    val id: String,
    val name: String,
    val variety: String,
    val category: String = "Cash Crop",
    val landArea: String,
    val unit: String = "Acres",
    val sowingDate: String,
    val harvestDate: String,
    val irrigationType: String = "Drip Irrigation",
    val quantity: Double = 10.0,
    val price: Double = 1950.0,
    val description: String = "",
    var status: String = "Growing", // "Planned", "Sown", "Growing", "Ready for Harvest", "Harvested"
    val imagePreset: String = "🌱"
)

data class UnifiedOrder(
    val id: String,
    val orderType: String, // "Product", "Produce", "Waste"
    val itemTitle: String,
    val category: String,
    val counterpartyName: String,
    val counterpartyPhone: String,
    val quantity: String,
    val totalPrice: Double,
    val date: String,
    val status: String, // "Pending", "Confirmed", "Picked Up", "Out for Delivery", "Delivered", "Rejected"
    val address: String
)

data class StoreProduct(
    val id: String,
    val name: String,
    val brand: String,
    val category: String, // "Seeds", "Fertilizers", "Pesticides", "Farming Equipment"
    val price: Double,
    val stock: Int,
    val rating: Double,
    val description: String,
    val imagePreset: String
)

data class LabourTeam(
    val id: String,
    val teamName: String,
    val mukadamName: String,
    val phone: String,
    val village: String,
    val distanceKm: Double,
    val skills: String,
    val totalWorkers: Int,
    val dailyWagePerWorker: Double,
    val rating: Double,
    var status: String // "Available", "Pending Request", "Request Accepted"
)

data class ContractFarming(
    val id: String,
    val companyName: String,
    val cropName: String,
    val variety: String,
    val requiredQuantity: String,
    val offeredPrice: Double,
    val qualitySpecs: String,
    val harvestPeriod: String,
    val location: String,
    var status: String // "Open", "Applied", "Approved"
)

data class BrokerDemand(
    val id: String,
    val brokerName: String,
    val companyName: String,
    val phone: String,
    val cropDemanded: String,
    val requiredQty: String,
    val offeredPricePerQuintal: Double,
    val location: String,
    val paymentTerms: String,
    var dealStatus: String // "Active Demand", "Negotiating", "Deal Agreed"
)

data class DirectProduceListing(
    val id: String,
    val produceName: String,
    val quantityAvailable: Double,
    val unit: String,
    val pricePerKg: Double,
    val qualityGrade: String,
    val harvestDate: String,
    val description: String,
    val status: String
)

data class FarmerNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val category: String, // "Labour", "Contract", "Broker", "Order", "AI Disease", "Delivery"
    var isRead: Boolean = false
)

data class DiseaseSample(
    val cropName: String,
    val diseaseName: String,
    val marathiName: String,
    val confidence: Int,
    val symptoms: String,
    val prevention: String,
    val treatment: String,
    val recommendedPesticide: String
)

// Preset Crops with visual emojis
val CROP_PRESETS = listOf(
    "Pune Red Onions" to "🧅",
    "Alphonso Mango" to "🥭",
    "Indrayani Rice" to "🌾",
    "Green Chillies" to "🌶️",
    "Sugarcane" to "🎋",
    "Organic Soybean" to "🌱",
    "Juicy Tomato" to "🍅"
)

// ------------------ MASTER FARMER PORTAL ------------------
@Composable
fun FarmerPortalScreen(navController: NavController) {
    val context = LocalContext.current

    // Portal Stage: "otp_verification", "registration", "success", "dashboard"
    var currentPortalStage by remember { mutableStateOf("dashboard") }

    // Dashboard Sub-screens:
    // "dashboard", "my_crops", "add_crop", "ai_disease", "agri_store", "hire_labour", "contract_farming", "agri_waste", "broker_trading", "direct_selling", "orders", "notifications", "profile", "weather", "gov_schemes"
    var currentSubScreen by remember { mutableStateOf("dashboard") }
    var aiDetectionCropTarget by remember { mutableStateOf("Tomato") }

    // Quick Add Sheet Modal State
    var showQuickAddModal by remember { mutableStateOf(false) }

    // Profile States
    var farmerName by remember { mutableStateOf(SessionManager.getInstance(context).userName.ifEmpty { "Registered Farmer" }) }
    var mobileNumber by remember { mutableStateOf(SessionManager.getInstance(context).userPhone.ifEmpty { "+91 ----------" }) }
    var village by remember { mutableStateOf(SessionManager.getInstance(context).userVillage.ifEmpty { "Village / Taluka" }) }
    var taluka by remember { mutableStateOf("") }
    var district by remember { mutableStateOf(SessionManager.getInstance(context).userDistrict.ifEmpty { "District" }) }
    var landArea by remember { mutableStateOf("") }
    var preferredCrops by remember { mutableStateOf("") }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Dynamic Memory Databases
    val cropsList = remember { mutableStateListOf<FarmerCrop>() }

    val ordersList = remember { mutableStateListOf<UnifiedOrder>() }

    val storeProducts = remember { mutableStateListOf<StoreProduct>() }

    val labourTeams = remember { mutableStateListOf<LabourTeam>() }

    val contractList = remember { mutableStateListOf<ContractFarming>() }

    val brokerDemands = remember { mutableStateListOf<BrokerDemand>() }

    val directProduceList = remember { mutableStateListOf<DirectProduceListing>() }

    val notificationsList = remember { mutableStateListOf<FarmerNotification>() }

    val savedDiseaseScans = remember { mutableStateListOf<SavedDiseaseScan>() }

    // Active Cart
    var cartCount by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            if (currentPortalStage == "dashboard") {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
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
                        selected = currentSubScreen == "dashboard",
                        onClick = { currentSubScreen = "dashboard" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = currentSubScreen == "my_crops" || currentSubScreen == "add_crop",
                        onClick = { currentSubScreen = "my_crops" },
                        icon = { Icon(Icons.Default.Eco, contentDescription = "My Crops") },
                        label = { Text("My Crops", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_my_crops")
                    )
                    // CENTER QUICK ADD BUTTON
                    NavigationBarItem(
                        selected = false,
                        onClick = { showQuickAddModal = true },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(FarmerAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Quick Add", tint = FarmerTextPrimary)
                            }
                        },
                        label = { Text("Add / List", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmerAccent) },
                        modifier = Modifier.testTag("nav_add_quick")
                    )
                    NavigationBarItem(
                        selected = currentSubScreen == "activities" || currentSubScreen == "orders",
                        onClick = { currentSubScreen = "activities" },
                        icon = {
                            val unconfirmed = ordersList.count { it.status == "Pending" } +
                                    AgriWasteDataHub.orders.count { it.status == "Waiting for Farmer" || it.status == "Order Placed" } +
                                    AgroWorldLabourRepository.requirements.count { it.status == "Pending Acceptance" || it.status == "Open" }
                            BadgedBox(badge = {
                                if (unconfirmed > 0) {
                                    Badge(containerColor = FarmerAccent) {
                                        Text(unconfirmed.toString(), color = FarmerTextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Inventory2, contentDescription = "Activities")
                            }
                        },
                        label = { Text("Activities", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_activities")
                    )
                    NavigationBarItem(
                        selected = currentSubScreen == "profile",
                        onClick = { currentSubScreen = "profile" },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_profile")
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
                .padding(if (currentPortalStage == "dashboard") paddingValues else PaddingValues(0.dp))
        ) {
            AnimatedContent(
                targetState = currentSubScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "farmer_subscreen_transition"
            ) { target ->
                when (target) {
                    "dashboard" -> FarmerDashboardMainView(
                        farmerName = farmerName,
                        village = village,
                        taluka = taluka,
                        cropsList = cropsList,
                        ordersList = ordersList,
                        notificationsList = notificationsList,
                        onNavigate = { currentSubScreen = it }
                    )
                    "profile" -> FarmerProfileView(
                        farmerName = farmerName,
                        mobileNumber = mobileNumber,
                        village = village,
                        taluka = taluka,
                        district = district,
                        landArea = landArea,
                        preferredCrops = preferredCrops,
                        profilePhotoUri = profilePhotoUri,
                        onUpdateProfile = { n, m, v, t, d, l, p ->
                            farmerName = n
                            mobileNumber = m
                            village = v
                            taluka = t
                            district = d
                            landArea = l
                            preferredCrops = p
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        },
                        onBack = { currentSubScreen = "dashboard" },
                        onLogout = {
                            navController.navigate("role_selection") {
                                popUpTo("dashboard/farmer") { inclusive = true }
                            }
                        }
                    )
                    "my_crops" -> MyCropsView(
                        cropsList = cropsList,
                        savedScans = savedDiseaseScans,
                        onCheckDisease = { cropName ->
                            aiDetectionCropTarget = cropName
                            currentSubScreen = "ai_disease"
                        },
                        onNavigateAdd = { currentSubScreen = "add_crop" },
                        onDeleteCrop = { id ->
                            cropsList.removeAll { it.id == id }
                            Toast.makeText(context, "Crop listing removed.", Toast.LENGTH_SHORT).show()
                        },
                        onBack = { currentSubScreen = "dashboard" }
                    )
                    "add_crop" -> AddCropView(
                        onAddCrop = { crop ->
                            cropsList.add(0, crop)
                            Toast.makeText(context, "${crop.name} added to My Crops!", Toast.LENGTH_LONG).show()
                            currentSubScreen = "my_crops"
                        },
                        onBack = { currentSubScreen = "my_crops" }
                    )
                    "ai_disease" -> AiCropDiseaseDetectionView(
                        initialCrop = aiDetectionCropTarget,
                        myCrops = cropsList,
                        savedScans = savedDiseaseScans,
                        onSaveScan = { scan ->
                            savedDiseaseScans.add(0, scan)
                            notificationsList.add(0, FarmerNotification(
                                id = "n_" + (10..999).random(),
                                title = "AI Crop Disease Scan Saved 🤖",
                                message = "${scan.diseaseName} on ${scan.cropName} saved to your health history.",
                                timestamp = "Just now",
                                category = "AI Disease"
                            ))
                        },
                        onNavigateStore = { pesticideName ->
                            currentSubScreen = "agri_store"
                            Toast.makeText(context, "Showing store remedies for $pesticideName", Toast.LENGTH_SHORT).show()
                        },
                        onNavigateMyCrops = {
                            currentSubScreen = "my_crops"
                        },
                        onBack = { currentSubScreen = "dashboard" }
                    )
                    "agri_store" -> AgriStoreView(
                        products = storeProducts,
                        cartCount = cartCount,
                        onAddToCart = {
                            cartCount++
                            Toast.makeText(context, "Product added to cart!", Toast.LENGTH_SHORT).show()
                        },
                        onCheckout = { title, total ->
                            ordersList.add(0, UnifiedOrder(
                                id = "ord_" + (10..999).random(),
                                orderType = "Product",
                                itemTitle = title,
                                category = "Agri Store",
                                counterpartyName = "Kisan Krushi Kendra",
                                counterpartyPhone = "+91 94220 11223",
                                quantity = "1 Unit",
                                totalPrice = total,
                                date = "Today",
                                status = "Confirmed",
                                address = "$village, $taluka, $district"
                            ))
                            Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_LONG).show()
                            currentSubScreen = "activities"
                        },
                        onBack = { currentSubScreen = "dashboard" }
                    )
                    "hire_labour" -> FarmerLabourHubScreen(
                        onBack = { currentSubScreen = "dashboard" }
                    )
                    "contract_farming" -> ContractFarmingView(
                        contracts = contractList,
                        onApply = { id ->
                            val index = contractList.indexOfFirst { it.id == id }
                            if (index != -1) {
                                contractList[index] = contractList[index].copy(status = "Applied")
                                Toast.makeText(context, "Contract application submitted!", Toast.LENGTH_LONG).show()
                            }
                        },
                        onBack = { currentSubScreen = "dashboard" }
                    )
                    "agri_waste" -> FarmerAgriWasteHubScreen(
                        onBack = { currentSubScreen = "dashboard" }
                    )
                    "broker_trading" -> BrokerTradingView(
                        brokers = brokerDemands,
                        onAcceptDeal = { id ->
                            val index = brokerDemands.indexOfFirst { it.id == id }
                            if (index != -1) {
                                brokerDemands[index] = brokerDemands[index].copy(dealStatus = "Deal Agreed")
                                Toast.makeText(context, "Deal agreed with APMC broker!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onBack = { currentSubScreen = "dashboard" }
                    )
                    "direct_selling" -> DirectCustomerSellingView(
                        listings = directProduceList,
                        onAddListing = { name, qty, price, grade, date, desc ->
                            directProduceList.add(0, DirectProduceListing(
                                id = "dp_" + (10..999).random(),
                                produceName = name,
                                quantityAvailable = qty,
                                unit = "Quintal",
                                pricePerKg = price,
                                qualityGrade = grade,
                                harvestDate = date,
                                description = desc,
                                status = "Active"
                            ))
                            Toast.makeText(context, "Produce listed for direct customer selling!", Toast.LENGTH_SHORT).show()
                        },
                        onBack = { currentSubScreen = "dashboard" }
                    )
                    "activities", "orders" -> FarmerActivitiesView(
                        orders = ordersList,
                        contracts = contractList,
                        brokerDemands = brokerDemands,
                        produceList = directProduceList,
                        onNavigate = { currentSubScreen = it },
                        onBack = { currentSubScreen = "dashboard" }
                    )
                    "notifications" -> NotificationsView(
                        notifications = notificationsList,
                        onBack = { currentSubScreen = "dashboard" }
                    )
                }
            }

            // QUICK ADD MODAL SHEET
            if (showQuickAddModal) {
                QuickAddSheetDialog(
                    onDismiss = { showQuickAddModal = false },
                    onSelectAddCrop = {
                        showQuickAddModal = false
                        currentSubScreen = "add_crop"
                    },
                    onSelectPostLabour = {
                        showQuickAddModal = false
                        currentSubScreen = "hire_labour"
                    },
                    onSelectAddWaste = {
                        showQuickAddModal = false
                        currentSubScreen = "agri_waste"
                    },
                    onSelectAddProduce = {
                        showQuickAddModal = false
                        currentSubScreen = "direct_selling"
                    }
                )
            }
        }
    }
}

// ------------------ 1. FARMER DASHBOARD / HOME ------------------
@Composable
fun FarmerDashboardMainView(
    farmerName: String,
    village: String,
    taluka: String,
    cropsList: List<FarmerCrop>,
    ordersList: List<UnifiedOrder>,
    notificationsList: List<FarmerNotification>,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val unreadNotifs = notificationsList.count { !it.isRead }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        // TOP HEADER BAR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "🌾 AgroWorld",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FarmerPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Good Morning, $farmerName",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmerTextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = FarmerSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$village, $taluka",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = FarmerTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = { onNavigate("notifications") },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                ) {
                    BadgedBox(badge = {
                        if (unreadNotifs > 0) {
                            Badge(containerColor = FarmerAccent) {
                                Text(unreadNotifs.toString(), color = FarmerTextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = FarmerPrimary)
                    }
                }
            }
        }

        // WEATHER & FARMING ALERTS BANNER
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                border = BorderStroke(1.dp, FarmerAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌤️", fontSize = 34.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Junnar Cluster Weather • 28°C", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        }
                        Text("Light rainfall expected in 2 days. Protect harvested crops!", fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("शेतकरी सल्ला: कांदा काढणी असल्यास सुकवून झाकून ठेवा.", fontSize = 11.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // CURRENT CROPS SUMMARY CARD
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FarmerPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("My Farm Crops Overview", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                        Text("${cropsList.size} Active Crops Listed", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Total Estimated Area: 5.5 Acres", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                    Button(
                        onClick = { onNavigate("my_crops") },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmerAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View All", color = FarmerTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // ACTIVE LABOUR REQUIREMENTS SUMMARY
        item {
            val reqs = AgroWorldLabourRepository.requirements
            val activeReq = reqs.firstOrNull { it.status != "Completed" && it.status != "Cancelled" }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("hire_labour") }
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👨‍🌾", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Active Labour Requirement", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        }
                        Text("Hire Labour ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                    }

                    if (activeReq != null) {
                        Text("${activeReq.workType} • ${activeReq.crop}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                        Text("${activeReq.workersRequired} Workers Required • ${activeReq.workerIdsAccepted.size} Accepted • Status: ${activeReq.status}", fontSize = 12.sp, color = FarmerTextSecondary)

                        LinearProgressIndicator(
                            progress = { (activeReq.workerIdsAccepted.size.toFloat() / activeReq.workersRequired.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = FarmerPrimary,
                            trackColor = Color(0xFFE2E8F0)
                        )
                    } else {
                        Text("No active requirement. Tap to post a new labour requirement.", fontSize = 12.sp, color = FarmerTextSecondary)
                    }
                }
            }
        }

        // MAIN SERVICES & MARKETPLACES (9 SERVICES)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Farming Services & Marketplaces",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )

                // 9 Action Cards Data
                val serviceCards = listOf(
                    FarmerServiceItem("🌱", "My Crops", "Manage crops and view crop information.", "my_crops", Color(0xFFE8F5E9)),
                    FarmerServiceItem("🤖", "AI Disease Detection", "Check your crop health using a photo", "ai_disease", Color(0xFFE0F2FE)),
                    FarmerServiceItem("👨‍🌾", "Hire Labour", "Post a farming work requirement and find nearby workers.", "hire_labour", Color(0xFFFFF3E0)),
                    FarmerServiceItem("🏪", "Buy Farming Products", "Buy seeds, fertilizers, pesticides and equipment.", "agri_store", Color(0xFFF3E5F5)),
                    FarmerServiceItem("🤝", "Contract Farming", "View and apply for farming contracts.", "contract_farming", Color(0xFFEDE7F6)),
                    FarmerServiceItem("♻️", "List Agri Waste", "Sell agricultural waste by creating a listing.", "agri_waste", Color(0xFFE0F7FA)),
                    FarmerServiceItem("📈", "Broker Trading", "View wholesale/bulk trading opportunities.", "broker_trading", Color(0xFFFFF8E1)),
                    FarmerServiceItem("🛒", "Sell Farm Produce", "List farm produce for direct customers.", "direct_selling", Color(0xFFFBE9E7)),
                    FarmerServiceItem("📦", "My Activities", "View labour jobs, orders, contracts, waste sales and other activities.", "activities", Color(0xFFF1F5F9))
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    serviceCards.forEach { srv ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(srv.route) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(srv.bgColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(srv.icon, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = srv.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FarmerTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = srv.description,
                                        fontSize = 12.sp,
                                        color = FarmerTextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Open ${srv.title}",
                                    tint = FarmerSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // RECENT ACTIVITIES & ORDERS RECAP
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activities & Orders",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerPrimary,
                    modifier = Modifier.clickable { onNavigate("activities") }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                ordersList.take(2).forEach { ord ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate("activities") }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ord.itemTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                Text("${ord.orderType} • ${ord.counterpartyName}", fontSize = 12.sp, color = FarmerTextSecondary)
                                Text("₹${ord.totalPrice.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (ord.status) {
                                            "Delivered", "Confirmed" -> FarmerPrimary.copy(alpha = 0.12f)
                                            "Pending" -> FarmerAccent.copy(alpha = 0.15f)
                                            else -> Color(0xFFE2E8F0)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = ord.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (ord.status) {
                                        "Delivered", "Confirmed" -> FarmerPrimary
                                        "Pending" -> Color(0xFFB78103)
                                        else -> FarmerTextPrimary
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

data class FarmerServiceItem(
    val icon: String,
    val title: String,
    val description: String,
    val route: String,
    val bgColor: Color
)

// ------------------ 2. FARMER PROFILE ------------------
@Composable
fun FarmerProfileView(
    farmerName: String,
    mobileNumber: String,
    village: String,
    taluka: String,
    district: String,
    landArea: String,
    preferredCrops: String,
    profilePhotoUri: Uri?,
    onUpdateProfile: (String, String, String, String, String, String, String) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var editName by remember { mutableStateOf(farmerName) }
    var editMobile by remember { mutableStateOf(mobileNumber) }
    var editVillage by remember { mutableStateOf(village) }
    var editTaluka by remember { mutableStateOf(taluka) }
    var editDistrict by remember { mutableStateOf(district) }
    var editLand by remember { mutableStateOf(landArea) }
    var editCrops by remember { mutableStateOf(preferredCrops) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("Farmer Profile", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
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
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(FarmerPrimary.copy(alpha = 0.15f))
                                .border(2.dp, FarmerPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (editName.isNotEmpty()) editName.first().toString() else "F",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmerPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(editName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        Text(editMobile, fontSize = 13.sp, color = FarmerTextSecondary)
                        Text("📍 $editVillage, $editTaluka ($editDistrict)", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Edit Farm & Personal Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)

                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Farmer Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = editMobile,
                            onValueChange = { editMobile = it },
                            label = { Text("Mobile Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editVillage,
                                onValueChange = { editVillage = it },
                                label = { Text("Village") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editTaluka,
                                onValueChange = { editTaluka = it },
                                label = { Text("Taluka") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editDistrict,
                                onValueChange = { editDistrict = it },
                                label = { Text("District") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editLand,
                                onValueChange = { editLand = it },
                                label = { Text("Land Area (Acres)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = editCrops,
                            onValueChange = { editCrops = it },
                            label = { Text("Preferred / Active Crops") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                onUpdateProfile(editName, editMobile, editVillage, editTaluka, editDistrict, editLand, editCrops)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Save Profile Changes", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = onLogout,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch Role / Logout", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ------------------ 3. MY CROPS ------------------
@Composable
fun MyCropsView(
    cropsList: MutableList<FarmerCrop>,
    savedScans: List<SavedDiseaseScan> = emptyList(),
    onCheckDisease: (String) -> Unit = {},
    onNavigateAdd: () -> Unit,
    onDeleteCrop: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedCropHistory by remember { mutableStateOf<Pair<String, List<SavedDiseaseScan>>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("My Farm Crops", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${cropsList.size} Crops Registered", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        Text("Track growth lifecycle & field management", fontSize = 12.sp, color = FarmerTextSecondary)
                    }
                    Button(
                        onClick = onNavigateAdd,
                        colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Crop", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(cropsList) { crop ->
                // Crop-specific scans matching this crop
                val cropScans = savedScans.filter {
                    it.cropName.contains(crop.name, ignoreCase = true) ||
                    crop.name.contains(it.cropName, ignoreCase = true) ||
                    (crop.name.contains("onion", ignoreCase = true) && it.cropName.contains("onion", ignoreCase = true)) ||
                    (crop.name.contains("tomato", ignoreCase = true) && it.cropName.contains("tomato", ignoreCase = true)) ||
                    (crop.name.contains("sugarcane", ignoreCase = true) && it.cropName.contains("sugarcane", ignoreCase = true)) ||
                    (crop.name.contains("rice", ignoreCase = true) && it.cropName.contains("rice", ignoreCase = true))
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(crop.imagePreset, fontSize = 36.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(crop.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                Text("Variety: ${crop.variety} • ${crop.category}", fontSize = 12.sp, color = FarmerTextSecondary)
                                Text("Area: ${crop.landArea} ${crop.unit} • ${crop.irrigationType}", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { onDeleteCrop(crop.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }

                        // CROP LIFECYCLE STAGE TRACKER (Planned -> Sown -> Growing -> Ready for Harvest -> Harvested)
                        CropStatusStepper(
                            currentStatus = crop.status,
                            onStatusChange = { newStatus ->
                                crop.status = newStatus
                                Toast.makeText(context, "Updated status: $newStatus", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // RECENT AI HEALTH BADGE IF SCANNED
                        if (cropScans.isNotEmpty()) {
                            val latestScan = cropScans.first()
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (latestScan.isHealthy) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(if (latestScan.isHealthy) "🌿" else "🤖", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Latest: ${latestScan.diseaseName}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (latestScan.isHealthy) Color(0xFF2E7D32) else Color(0xFFC62828),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text("${latestScan.confidencePercent}% conf", fontSize = 10.sp, color = FarmerTextSecondary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Divider(color = Color(0xFFF1F5F9))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🌱 Sown: ${crop.sowingDate}", fontSize = 12.sp, color = FarmerTextSecondary)
                            Text("🌾 Harvest: ${crop.harvestDate}", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                        }

                        if (crop.description.isNotBlank()) {
                            Text("📝 ${crop.description}", fontSize = 11.sp, color = FarmerTextSecondary)
                        }

                        // ACTIONS ROW: CHECK DISEASE & CROP-SPECIFIC HISTORY
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onCheckDisease(crop.name) },
                                colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.3f),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Check Disease", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Check Disease", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    val listToShow = if (cropScans.isNotEmpty()) cropScans else listOf(
                                        SavedDiseaseScan(
                                            id = "sample_hist_01",
                                            cropName = crop.name,
                                            diseaseName = "Early Crop Vigor Scan (Healthy)",
                                            confidence = "High",
                                            confidencePercent = 94,
                                            isHealthy = true,
                                            symptoms = listOf("Vibrant green leaves, robust venation"),
                                            prevention = listOf("Maintain drip irrigation and balanced fertilization"),
                                            recommendedAction = listOf("Routine visual checks once a week"),
                                            formattedDate = "20 Aug 2026",
                                            imageQuality = "good"
                                        )
                                    )
                                    selectedCropHistory = crop.name to listToShow
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmerSecondary),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.1f),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Disease History", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // CROP-SPECIFIC DISEASE HISTORY DIALOG (Section 12)
    selectedCropHistory?.let { (cropName, scans) ->
        AlertDialog(
            onDismissRequest = { selectedCropHistory = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("📋 $cropName Disease History", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${scans.size} previous diagnostic scans", fontSize = 11.sp, color = FarmerTextSecondary)
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(scans) { sc ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (sc.isHealthy) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)),
                            border = BorderStroke(1.dp, if (sc.isHealthy) Color(0xFFBBF7D0) else Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (sc.isHealthy) "🌿 ${sc.diseaseName}" else "🦠 ${sc.diseaseName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (sc.isHealthy) Color(0xFF15803D) else Color(0xFFB45309)
                                    )
                                    Text(
                                        text = "${sc.confidencePercent}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FarmerPrimary
                                    )
                                }
                                Text("📅 Date: ${sc.formattedDate}", fontSize = 11.sp, color = FarmerTextSecondary)
                                if (sc.symptoms.isNotEmpty()) {
                                    Text("🔍 Symptoms: ${sc.symptoms.first()}", fontSize = 11.sp, color = FarmerTextPrimary, maxLines = 2)
                                }
                                if (sc.recommendedAction.isNotEmpty()) {
                                    Text("💡 Action: ${sc.recommendedAction.first()}", fontSize = 11.sp, color = Color(0xFF1B5E20), maxLines = 2)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedCropHistory = null
                        onCheckDisease(cropName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Scan", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Scan for $cropName", fontSize = 12.sp, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCropHistory = null }) {
                    Text("Close", color = FarmerTextSecondary)
                }
            }
        )
    }
}

@Composable
fun CropStatusStepper(
    currentStatus: String,
    onStatusChange: (String) -> Unit
) {
    val statuses = listOf("Planned", "Sown", "Growing", "Ready for Harvest", "Harvested")
    val currentIdx = statuses.indexOf(currentStatus).let { if (it == -1) 2 else it }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Crop Status:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary)
            Text(currentStatus, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            statuses.forEachIndexed { index, st ->
                val isSelected = currentStatus == st
                val isPassed = index <= currentIdx
                FilterChip(
                    selected = isSelected,
                    onClick = { onStatusChange(st) },
                    label = { Text(st, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FarmerPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = if (isPassed) FarmerLightBg else Color(0xFFF8FAFC)
                    )
                )
            }
        }
    }
}

// ------------------ ADD CROP SCREEN ------------------
@Composable
fun AddCropView(
    onAddCrop: (FarmerCrop) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("Pune Red Onions") }
    var variety by remember { mutableStateOf("N-53") }
    var landArea by remember { mutableStateOf("2.0") }
    var unit by remember { mutableStateOf("Acres") }
    var sowingDate by remember { mutableStateOf("15 April 2026") }
    var harvestDate by remember { mutableStateOf("20 August 2026") }
    var irrigationType by remember { mutableStateOf("Drip Irrigation") }
    var cropStatus by remember { mutableStateOf("Growing") }
    var category by remember { mutableStateOf("Vegetables") }
    var price by remember { mutableStateOf("1950") }
    var desc by remember { mutableStateOf("Fresh organic harvest from Junnar cluster.") }
    var selectedEmoji by remember { mutableStateOf("🧅") }

    val cropEmojis = listOf("🧅", "🎋", "🌾", "🍅", "🌽", "🥔", "🍇", "🌱", "🌿")
    val unitsList = listOf("Acres", "Gunthas", "Hectares")
    val irrigationList = listOf("Drip Irrigation", "Sprinkler", "Flood Irrigation", "Canal", "Rainfed")
    val statusList = listOf("Planned", "Sown", "Growing", "Ready for Harvest", "Harvested")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("Add New Crop Listing", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text("Select Crop Icon", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    items(cropEmojis) { em ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedEmoji == em) FarmerLightBg else Color.White)
                                .border(1.dp, if (selectedEmoji == em) FarmerPrimary else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .clickable { selectedEmoji = em },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(em, fontSize = 22.sp)
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Crop Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = variety,
                    onValueChange = { variety = it },
                    label = { Text("Crop Variety *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = landArea,
                        onValueChange = { landArea = it },
                        label = { Text("Land Area *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            item {
                Text("Land Unit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 2.dp)) {
                    unitsList.forEach { u ->
                        FilterChip(
                            selected = unit == u,
                            onClick = { unit = u },
                            label = { Text(u, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FarmerPrimary, selectedLabelColor = Color.White)
                        )
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sowingDate,
                        onValueChange = { sowingDate = it },
                        label = { Text("Sowing Date") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = harvestDate,
                        onValueChange = { harvestDate = it },
                        label = { Text("Expected Harvest Date") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            item {
                Text("Irrigation Type", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 2.dp)) {
                    items(irrigationList) { irr ->
                        FilterChip(
                            selected = irrigationType == irr,
                            onClick = { irrigationType = irr },
                            label = { Text(irr, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FarmerPrimary, selectedLabelColor = Color.White)
                        )
                    }
                }
            }

            item {
                Text("Crop Status", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 2.dp)) {
                    items(statusList) { st ->
                        FilterChip(
                            selected = cropStatus == st,
                            onClick = { cropStatus = st },
                            label = { Text(st, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FarmerPrimary, selectedLabelColor = Color.White)
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Expected Price (₹/Quintal or Ton)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Crop Description & Soil Details") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val newC = FarmerCrop(
                            id = "c_" + (10..999).random(),
                            name = name,
                            variety = variety,
                            category = category,
                            landArea = landArea,
                            unit = unit,
                            sowingDate = sowingDate,
                            harvestDate = harvestDate,
                            irrigationType = irrigationType,
                            quantity = 10.0,
                            price = price.toDoubleOrNull() ?: 1800.0,
                            description = desc,
                            status = cropStatus,
                            imagePreset = selectedEmoji
                        )
                        onAddCrop(newC)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Publish Crop Listing", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ------------------ SAMPLE LEAF GENERATOR FOR TESTING ------------------
object SampleLeafGenerator {
    fun createSampleLeafBitmap(type: String): Bitmap {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (type) {
            "Tomato" -> {
                canvas.drawColor(android.graphics.Color.rgb(244, 248, 242))
                paint.color = android.graphics.Color.rgb(46, 125, 50)
                paint.strokeWidth = 12f
                paint.style = Paint.Style.STROKE
                val stemPath = Path().apply {
                    moveTo(200f, 380f)
                    quadTo(210f, 250f, 200f, 70f)
                }
                canvas.drawPath(stemPath, paint)

                paint.style = Paint.Style.FILL
                paint.color = android.graphics.Color.rgb(67, 160, 71)
                val leafPath = Path().apply {
                    moveTo(200f, 70f)
                    cubicTo(330f, 130f, 340f, 270f, 200f, 340f)
                    cubicTo(60f, 270f, 70f, 130f, 200f, 70f)
                }
                canvas.drawPath(leafPath, paint)

                // Early blight necrotic spots with chlorotic halo
                paint.color = android.graphics.Color.rgb(251, 192, 45) // Yellow halo
                canvas.drawCircle(165f, 175f, 36f, paint)
                canvas.drawCircle(235f, 235f, 28f, paint)
                paint.color = android.graphics.Color.rgb(62, 39, 35) // Brown center
                canvas.drawCircle(165f, 175f, 24f, paint)
                canvas.drawCircle(235f, 235f, 18f, paint)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                paint.color = android.graphics.Color.rgb(33, 33, 33)
                canvas.drawCircle(165f, 175f, 14f, paint)
            }
            "Onion" -> {
                canvas.drawColor(android.graphics.Color.rgb(245, 248, 243))
                paint.style = Paint.Style.FILL
                paint.color = android.graphics.Color.rgb(56, 142, 60)
                val leaf = Path().apply {
                    moveTo(185f, 380f)
                    lineTo(215f, 380f)
                    lineTo(205f, 50f)
                    lineTo(195f, 50f)
                    close()
                }
                canvas.drawPath(leaf, paint)

                // Purple blotch lesions
                paint.color = android.graphics.Color.rgb(123, 31, 162) // Purple
                canvas.drawOval(170f, 150f, 230f, 210f, paint)
                canvas.drawOval(175f, 240f, 225f, 280f, paint)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
                paint.color = android.graphics.Color.rgb(253, 216, 53)
                canvas.drawOval(167f, 147f, 233f, 213f, paint)
            }
            "Potato" -> {
                canvas.drawColor(android.graphics.Color.rgb(240, 245, 238))
                paint.style = Paint.Style.FILL
                paint.color = android.graphics.Color.rgb(46, 125, 50)
                val leaf = Path().apply {
                    moveTo(200f, 60f)
                    cubicTo(320f, 110f, 330f, 270f, 200f, 350f)
                    cubicTo(70f, 270f, 80f, 110f, 200f, 60f)
                }
                canvas.drawPath(leaf, paint)

                // Late blight lesion
                paint.color = android.graphics.Color.rgb(48, 28, 20)
                canvas.drawCircle(220f, 180f, 42f, paint)
                canvas.drawCircle(145f, 240f, 32f, paint)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 6f
                paint.color = android.graphics.Color.rgb(238, 238, 238) // Mildew ring
                canvas.drawCircle(220f, 180f, 44f, paint)
            }
            "Cotton" -> {
                canvas.drawColor(android.graphics.Color.rgb(248, 248, 242))
                paint.style = Paint.Style.FILL
                paint.color = android.graphics.Color.rgb(76, 175, 80)
                val leaf = Path().apply {
                    moveTo(200f, 60f)
                    lineTo(290f, 150f)
                    lineTo(260f, 230f)
                    lineTo(310f, 290f)
                    lineTo(200f, 350f)
                    lineTo(90f, 290f)
                    lineTo(140f, 230f)
                    lineTo(110f, 150f)
                    close()
                }
                canvas.drawPath(leaf, paint)
                // Angular vein leaf spots
                paint.color = android.graphics.Color.rgb(183, 28, 28)
                canvas.drawRect(175f, 160f, 215f, 195f, paint)
                canvas.drawRect(220f, 225f, 255f, 255f, paint)
                canvas.drawRect(140f, 210f, 170f, 240f, paint)
            }
            "Blurry" -> {
                // Out of focus test image
                canvas.drawColor(android.graphics.Color.rgb(210, 215, 210))
                paint.color = android.graphics.Color.rgb(180, 190, 180)
                canvas.drawCircle(200f, 200f, 130f, paint)
                paint.color = android.graphics.Color.rgb(160, 170, 160)
                canvas.drawCircle(180f, 190f, 80f, paint)
            }
            else -> {
                canvas.drawColor(android.graphics.Color.rgb(244, 248, 242))
                paint.style = Paint.Style.FILL
                paint.color = android.graphics.Color.rgb(56, 142, 60)
                val leaf = Path().apply {
                    moveTo(200f, 70f)
                    cubicTo(320f, 120f, 320f, 270f, 200f, 340f)
                    cubicTo(80f, 270f, 80f, 120f, 200f, 70f)
                }
                canvas.drawPath(leaf, paint)
            }
        }
        return bitmap
    }
}

// ------------------ 4. AI CROP DISEASE DETECTION ------------------
@Composable
fun AiCropDiseaseDetectionView(
    initialCrop: String = "Tomato",
    myCrops: List<FarmerCrop> = emptyList(),
    savedScans: SnapshotStateList<SavedDiseaseScan>,
    onSaveScan: (SavedDiseaseScan) -> Unit,
    onNavigateStore: (String) -> Unit,
    onNavigateMyCrops: () -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Screen Tabs: "scan" | "history"
    var activeTab by remember { mutableStateOf("scan") }

    // Supported Crops List
    val cropOptions = listOf(
        "Tomato" to "🍅",
        "Potato" to "🥔",
        "Cotton" to "☁️",
        "Wheat" to "🌾",
        "Rice" to "🌾",
        "Maize" to "🌽",
        "Pune Red Onion" to "🧅",
        "Sugarcane" to "🎋",
        "Soybean" to "🌱",
        "Chilli" to "🌶️",
        "Mango" to "🥭",
        "Grapes" to "🍇"
    )

    var selectedCrop by remember(initialCrop) { mutableStateOf(initialCrop.ifBlank { "Tomato" }) }
    var showCropDropdown by remember { mutableStateOf(false) }

    // Selected / Captured Image Bitmap
    var currentImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showImageSourceModal by remember { mutableStateOf(false) }
    var showKisanHelplineModal by remember { mutableStateOf(false) }

    // Analysis State
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<DiseaseAnalysisResult?>(null) }
    var analysisSaved by remember { mutableStateOf(false) }
    var selectedHistoryItem by remember { mutableStateOf<SavedDiseaseScan?>(null) }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            currentImageBitmap = bitmap
            analysisResult = null
            analysisSaved = false
        } else {
            Toast.makeText(context, "No photo captured", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(context, "Could not launch camera: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                context,
                "Camera permission required to capture crop leaf photos. You can also choose from Gallery.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun launchCameraSafely() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(context, "Camera unavailable: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = AiDiseaseService.uriToBitmap(context, uri)
            if (bitmap != null) {
                currentImageBitmap = bitmap
                analysisResult = null
                analysisSaved = false
            } else {
                Toast.makeText(context, "Failed to load image from gallery", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Animated Scan Bar Effect
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanOffset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("AI Crop Disease Detection 🤖", onBack)

        // Sub-Tab Switcher (New Scan vs History)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE8F5E9))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (activeTab == "scan") FarmerPrimary else Color.Transparent)
                    .clickable { activeTab = "scan" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Scan",
                        tint = if (activeTab == "scan") Color.White else FarmerPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Scan Crop Leaf",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeTab == "scan") Color.White else FarmerPrimary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (activeTab == "history") FarmerPrimary else Color.Transparent)
                    .clickable { activeTab = "history" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = "History",
                        tint = if (activeTab == "history") Color.White else FarmerPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Disease History (${savedScans.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeTab == "history") Color.White else FarmerPrimary
                    )
                }
            }
        }

        if (activeTab == "scan") {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. CROP SELECTION
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
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
                                Column {
                                    Text("1. Select Crop", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                    Text("Choose crop to calibrate AI analysis", fontSize = 12.sp, color = FarmerTextSecondary)
                                }

                                Box {
                                    FilledTonalButton(
                                        onClick = { showCropDropdown = true },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = FarmerPrimary.copy(alpha = 0.12f),
                                            contentColor = FarmerPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(selectedCrop, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                    }

                                    DropdownMenu(
                                        expanded = showCropDropdown,
                                        onDismissRequest = { showCropDropdown = false }
                                    ) {
                                        cropOptions.forEach { (crop, icon) ->
                                            DropdownMenuItem(
                                                text = { Text("$icon $crop", fontWeight = if (selectedCrop == crop) FontWeight.Bold else FontWeight.Normal) },
                                                onClick = {
                                                    selectedCrop = crop
                                                    showCropDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // My Farm Crops Quick Shortcuts (if farmer has listed crops)
                            if (myCrops.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("🌱 Your Farm Crops:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(myCrops) { farmCrop ->
                                        val isSelected = selectedCrop.equals(farmCrop.name, ignoreCase = true)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedCrop = farmCrop.name },
                                            label = { Text("🌾 ${farmCrop.name}", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = FarmerPrimary,
                                                selectedLabelColor = Color.White,
                                                containerColor = Color(0xFFF0FDF4),
                                                labelColor = FarmerPrimary
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                enabled = true,
                                                selected = isSelected,
                                                borderColor = FarmerPrimary
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Horizontal Quick Selection Chips
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(cropOptions) { (crop, icon) ->
                                    val isSelected = selectedCrop == crop
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCrop = crop },
                                        label = { Text("$icon $crop", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = FarmerPrimary,
                                            selectedLabelColor = Color.White,
                                            containerColor = Color(0xFFF1F8E9),
                                            labelColor = FarmerPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. QUICK TEST SAMPLE PHOTOS (For immediate testing)
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
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
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Samples", tint = FarmerPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("⚡ Quick Test Leaf Samples", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                }
                                Text("Tap to load sample", fontSize = 11.sp, color = FarmerMuted)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    OutlinedButton(
                                        onClick = {
                                            selectedCrop = "Tomato"
                                            currentImageBitmap = SampleLeafGenerator.createSampleLeafBitmap("Tomato")
                                            analysisResult = null
                                            analysisSaved = false
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        border = BorderStroke(1.dp, Color(0xFFEF5350))
                                    ) {
                                        Text("🍅 Tomato Early Blight", fontSize = 11.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                                    }
                                }
                                item {
                                    OutlinedButton(
                                        onClick = {
                                            selectedCrop = "Pune Red Onion"
                                            currentImageBitmap = SampleLeafGenerator.createSampleLeafBitmap("Onion")
                                            analysisResult = null
                                            analysisSaved = false
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        border = BorderStroke(1.dp, Color(0xFFAB47BC))
                                    ) {
                                        Text("🧅 Onion Purple Blotch", fontSize = 11.sp, color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)
                                    }
                                }
                                item {
                                    OutlinedButton(
                                        onClick = {
                                            selectedCrop = "Potato"
                                            currentImageBitmap = SampleLeafGenerator.createSampleLeafBitmap("Potato")
                                            analysisResult = null
                                            analysisSaved = false
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        border = BorderStroke(1.dp, Color(0xFF8D6E63))
                                    ) {
                                        Text("🥔 Potato Late Blight", fontSize = 11.sp, color = Color(0xFF4E342E), fontWeight = FontWeight.Bold)
                                    }
                                }
                                item {
                                    OutlinedButton(
                                        onClick = {
                                            selectedCrop = "Cotton"
                                            currentImageBitmap = SampleLeafGenerator.createSampleLeafBitmap("Cotton")
                                            analysisResult = null
                                            analysisSaved = false
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        border = BorderStroke(1.dp, Color(0xFF26A69A))
                                    ) {
                                        Text("☁️ Cotton Leaf Spot", fontSize = 11.sp, color = Color(0xFF00695C), fontWeight = FontWeight.Bold)
                                    }
                                }
                                item {
                                    OutlinedButton(
                                        onClick = {
                                            selectedCrop = "Tomato"
                                            currentImageBitmap = SampleLeafGenerator.createSampleLeafBitmap("Healthy")
                                            analysisResult = null
                                            analysisSaved = false
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        border = BorderStroke(1.dp, Color(0xFF66BB6A))
                                    ) {
                                        Text("🌿 Healthy Leaf", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                    }
                                }
                                item {
                                    OutlinedButton(
                                        onClick = {
                                            selectedCrop = "Tomato"
                                            currentImageBitmap = SampleLeafGenerator.createSampleLeafBitmap("Blurry")
                                            analysisResult = null
                                            analysisSaved = false
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFFA726))
                                    ) {
                                        Text("🌫️ Blurry / Low Quality", fontSize = 11.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. IMAGE UPLOAD & PREVIEW SECTION
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, if (currentImageBitmap != null) FarmerPrimary else Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("2. Upload or Capture Crop Photo", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            Text("Take a clear close-up photo of the affected leaf or crop part", fontSize = 12.sp, color = FarmerTextSecondary, textAlign = TextAlign.Center)

                            Spacer(modifier = Modifier.height(14.dp))

                            if (currentImageBitmap == null) {
                                // Upload prompt box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(FarmerPrimary.copy(alpha = 0.04f))
                                        .border(
                                            BorderStroke(
                                                1.5.dp,
                                                Brush.sweepGradient(listOf(FarmerPrimary, FarmerSecondary, FarmerAccent, FarmerPrimary))
                                            ),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { showImageSourceModal = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(CircleShape)
                                                .background(FarmerPrimary.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.AddAPhoto,
                                                contentDescription = "Upload",
                                                tint = FarmerPrimary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("Tap to Choose or Capture Photo", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                                        Text("Camera OR Gallery photo", fontSize = 11.sp, color = FarmerTextSecondary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Direct Choice Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { launchCameraSafely() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary)
                                    ) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(18.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Take Photo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    OutlinedButton(
                                        onClick = { galleryLauncher.launch("image/*") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmerPrimary),
                                        border = BorderStroke(1.5.dp, FarmerPrimary)
                                    ) {
                                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Choose Gallery", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                // Image Preview Section
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .border(2.dp, FarmerPrimary, RoundedCornerShape(16.dp))
                                ) {
                                    Image(
                                        bitmap = currentImageBitmap!!.asImageBitmap(),
                                        contentDescription = "Selected Crop Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )

                                    // Scanning laser overlay while analyzing
                                    if (isAnalyzing) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .align(Alignment.TopCenter)
                                                .offset(y = (scanLineOffset * 236).dp)
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(Color.Transparent, Color(0xFF00E676), Color.White, Color(0xFF00E676), Color.Transparent)
                                                    )
                                                )
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.55f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    color = Color(0xFF69F0AE),
                                                    strokeWidth = 3.5.dp,
                                                    modifier = Modifier.size(40.dp)
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text("🌿 AI is analyzing your crop...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Identifying leaf patterns and disease signatures", color = Color(0xFFE0E0E0), fontSize = 11.sp, textAlign = TextAlign.Center)
                                            }
                                        }
                                    }

                                    // Crop Tag in Preview
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.7f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Target: $selectedCrop", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Retake & Analyze Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { showImageSourceModal = true },
                                        enabled = !isAnalyzing,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmerTextSecondary),
                                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Retake", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Retake", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            if (currentImageBitmap != null && !isAnalyzing) {
                                                isAnalyzing = true
                                                analysisResult = null
                                                analysisSaved = false

                                                coroutineScope.launch {
                                                    val result = AiDiseaseService.analyzeCropImage(
                                                        context = context,
                                                        bitmap = currentImageBitmap!!,
                                                        selectedCrop = selectedCrop
                                                    )
                                                    isAnalyzing = false
                                                    analysisResult = result

                                                    // Auto-save valid scan to history
                                                    if (result.isSuccess && !result.isUnclearOrPoorQuality) {
                                                        val newScan = SavedDiseaseScan(
                                                            id = "scan_" + System.currentTimeMillis(),
                                                            cropName = result.crop,
                                                            diseaseName = result.disease,
                                                            confidence = result.confidence,
                                                            severity = result.severity,
                                                            symptoms = result.symptoms,
                                                            possibleCauses = result.possibleCauses,
                                                            recommendedAction = result.recommendedAction,
                                                            prevention = result.prevention,
                                                            imageQuality = result.imageQuality,
                                                            imageBitmap = currentImageBitmap
                                                        )
                                                        onSaveScan(newScan)
                                                        analysisSaved = true
                                                    }
                                                }
                                            }
                                        },
                                        enabled = !isAnalyzing,
                                        modifier = Modifier.weight(1.6f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = "Analyze", tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isAnalyzing) "Analyzing..." else "Analyze Disease", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. AI ANALYSIS RESULT DISPLAY
                analysisResult?.let { result ->
                    item {
                        if (!result.isSuccess) {
                            // ERROR CARD (API failure / Network Error)
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                border = BorderStroke(1.5.dp, Color(0xFFEF5350)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ErrorOutline, contentDescription = "Error", tint = Color(0xFFC62828), modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("AI Service Notice", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                    }

                                    Text(
                                        result.errorMessage ?: "AI disease analysis is temporarily unavailable. Please try again.",
                                        fontSize = 13.sp,
                                        color = Color(0xFF37474F),
                                        lineHeight = 18.sp
                                    )

                                    Button(
                                        onClick = {
                                            if (currentImageBitmap != null && !isAnalyzing) {
                                                isAnalyzing = true
                                                analysisResult = null
                                                coroutineScope.launch {
                                                    val res = AiDiseaseService.analyzeCropImage(
                                                        context = context,
                                                        bitmap = currentImageBitmap!!,
                                                        selectedCrop = selectedCrop
                                                    )
                                                    isAnalyzing = false
                                                    analysisResult = res
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Retry Analysis", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        } else if (result.isUnclearOrPoorQuality) {
                            // UNCLEAR / POOR IMAGE WARNING CARD
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                border = BorderStroke(1.5.dp, Color(0xFFFFA000)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.WarningAmber, contentDescription = "Warning", tint = Color(0xFFD84315), modifier = Modifier.size(26.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Image Unclear for Diagnosis", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
                                    }

                                    Text(
                                        "Unable to identify the crop problem reliably from this image.",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3E2723)
                                    )

                                    Text(
                                        "Please upload a clear image showing the affected leaf, stem, fruit, or crop area.",
                                        fontSize = 13.sp,
                                        color = Color(0xFF4E342E)
                                    )

                                    Divider(color = Color(0xFFFFE082))

                                    Text("Guidelines for sharp diagnosis:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                    Text("• Take a clear close-up shot under natural daylight.", fontSize = 11.sp, color = FarmerTextSecondary)
                                    Text("• Avoid dark shadows, strong back-glare, or motion blur.", fontSize = 11.sp, color = FarmerTextSecondary)
                                    Text("• Focus directly on the discolored spot or affected foliage.", fontSize = 11.sp, color = FarmerTextSecondary)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { launchCameraSafely() },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD84315)),
                                            border = BorderStroke(1.dp, Color(0xFFD84315)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Take Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { showImageSourceModal = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84315)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.AddAPhoto, contentDescription = "Upload Another", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Upload Another", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        } else if (result.isHealthy) {
                            // HEALTHY CROP SPECIAL CARD
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                border = BorderStroke(1.5.dp, Color(0xFF22C55E)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Healthy", tint = Color(0xFF16A34A), modifier = Modifier.size(28.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text("🌿 Crop Health: Healthy", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                                Text("Target: ${result.crop}", fontSize = 12.sp, color = Color(0xFF166534))
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFDCFCE7))
                                                .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("High Confidence", color = Color(0xFF15803D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Text(
                                        "No obvious disease was detected in this image.",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF14532D)
                                    )

                                    Text(
                                        "The foliage exhibits healthy green coloration, vigorous cell structure, and no visible fungal or bacterial lesions.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF166534),
                                        lineHeight = 17.sp
                                    )

                                    Divider(color = Color(0xFFBBF7D0))

                                    Text("🌱 Maintenance & Agronomy Tips:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                    result.prevention.forEach { tip ->
                                        Row(modifier = Modifier.padding(vertical = 1.dp), verticalAlignment = Alignment.Top) {
                                            Text("• ", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                                            Text(tip, fontSize = 12.sp, color = Color(0xFF14532D), lineHeight = 16.sp)
                                        }
                                    }

                                    // MANDATORY WARNING BANNER
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                    ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                            Icon(Icons.Default.Info, contentDescription = "Warning", tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "“AI-generated agricultural assessment. Continue periodic visual inspection of your field.”",
                                                fontSize = 11.sp,
                                                color = Color(0xFF92400E),
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = onNavigateMyCrops,
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmerPrimary),
                                            border = BorderStroke(1.dp, FarmerPrimary)
                                        ) {
                                            Text("View My Crops", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                currentImageBitmap = null
                                                analysisResult = null
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Scan Another Crop", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        } else {
                            // FULL STRUCTURED GEMINI DISEASE DIAGNOSIS CARD
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.5.dp, FarmerPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Header: Crop & Disease Name & Badges
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("🌿 ${result.crop}", fontSize = 13.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFFE8F5E9))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(result.modelName, fontSize = 9.sp, color = FarmerPrimary, fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                "🦠 ${result.disease}",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FarmerTextPrimary
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            // Confidence Badge
                                            val (confBg, confColor) = when (result.confidence.lowercase()) {
                                                "high" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                                                "moderate" -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
                                                else -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(confBg)
                                                    .border(1.dp, confColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    "📊 ${result.confidence} Confidence",
                                                    color = confColor,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            // Severity Badge
                                            if (result.severity.isNotBlank() && result.severity.lowercase() != "unknown") {
                                                val (sevBg, sevColor) = when (result.severity.lowercase()) {
                                                    "severe", "critical", "high" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                                                    "moderate" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
                                                    else -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(sevBg)
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        "⚠️ Severity: ${result.severity}",
                                                        color = sevColor,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Divider(color = Color(0xFFF1F5F9))

                                    // 🔍 SYMPTOMS
                                    if (result.symptoms.isNotEmpty()) {
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Search, contentDescription = "Symptoms", tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("🔍 Symptoms Identified", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                result.symptoms.forEach { symptom ->
                                                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                                        Text("• ", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                                                        Text(symptom, fontSize = 12.sp, color = FarmerTextSecondary, lineHeight = 17.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 🌾 POSSIBLE CAUSES
                                    if (result.possibleCauses.isNotEmpty()) {
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Coronavirus, contentDescription = "Causes", tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("🌾 Possible Causes / Pathogens", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                result.possibleCauses.forEach { cause ->
                                                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                                        Text("• ", color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                                                        Text(cause, fontSize = 12.sp, color = Color(0xFF78350F), lineHeight = 17.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 🧪 RECOMMENDED ACTION
                                    if (result.recommendedAction.isNotEmpty()) {
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                            border = BorderStroke(1.dp, Color(0xFFA5D6A7))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Healing, contentDescription = "Action", tint = FarmerPrimary, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("🧪 Recommended Treatment & Actions", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                result.recommendedAction.forEach { act ->
                                                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                                        Text("• ", color = FarmerPrimary, fontWeight = FontWeight.Bold)
                                                        Text(act, fontSize = 12.sp, color = Color(0xFF1B5E20), lineHeight = 17.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 🛡️ PREVENTION
                                    if (result.prevention.isNotEmpty()) {
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                            border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Shield, contentDescription = "Prevention", tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("🛡️ Prevention & Agronomy Best Practices", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                result.prevention.forEach { prev ->
                                                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                                        Text("• ", color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                                                        Text(prev, fontSize = 12.sp, color = Color(0xFF166534), lineHeight = 17.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // MANDATORY WARNING BANNER
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(Icons.Default.Info, contentDescription = "Warning", tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "“AI-generated agricultural assessment. Verify serious crop problems and pesticide decisions with a qualified agricultural expert.”",
                                                fontSize = 11.sp,
                                                color = Color(0xFF92400E),
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }

                                    // Primary Action Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                if (!analysisSaved) {
                                                    val scanToSave = SavedDiseaseScan(
                                                        id = "scan_" + System.currentTimeMillis(),
                                                        cropName = result.crop,
                                                        diseaseName = result.disease,
                                                        confidence = result.confidence,
                                                        severity = result.severity,
                                                        symptoms = result.symptoms,
                                                        possibleCauses = result.possibleCauses,
                                                        recommendedAction = result.recommendedAction,
                                                        prevention = result.prevention,
                                                        imageQuality = result.imageQuality,
                                                        imageBitmap = currentImageBitmap
                                                    )
                                                    onSaveScan(scanToSave)
                                                    analysisSaved = true
                                                    Toast.makeText(context, "Saved to Disease History! ✅", Toast.LENGTH_SHORT).show()
                                                }
                                                activeTab = "history"
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmerPrimary),
                                            border = BorderStroke(1.dp, FarmerPrimary)
                                        ) {
                                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Save", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (analysisSaved) "View in History" else "Save Result", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { onNavigateStore("Saaf Fungicide (Mancozeb + Carbendazim)") },
                                            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1.2f)
                                        ) {
                                            Icon(Icons.Default.ShoppingBag, contentDescription = "Buy", tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Agri Store Remedies", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }

                                    // Secondary Actions: Contact Helpline / View My Crops
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = onNavigateMyCrops,
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmerTextSecondary),
                                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                        ) {
                                            Icon(Icons.Default.Eco, contentDescription = "My Crops", modifier = Modifier.size(14.dp), tint = FarmerPrimary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("View My Crops", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = { showKisanHelplineModal = true },
                                            modifier = Modifier.weight(1.2f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF15803D)),
                                            border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(14.dp), tint = Color(0xFF15803D))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Kisan Helpline 📞", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // 4. DISEASE HISTORY TAB
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Previous Disease Scans (${savedScans.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextPrimary
                        )

                        TextButton(
                            onClick = { activeTab = "scan" },
                            colors = ButtonDefaults.textButtonColors(contentColor = FarmerPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Scan", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (savedScans.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.History, contentDescription = "Empty", tint = FarmerMuted, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("No disease scans recorded yet", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextSecondary)
                                Text("Scans you perform will automatically be archived here", fontSize = 12.sp, color = FarmerMuted, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { activeTab = "scan" },
                                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Scan First Crop", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(savedScans) { scan ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedHistoryItem = scan }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Thumbnail / Icon Box
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(FarmerPrimary.copy(alpha = 0.08f))
                                        .border(1.dp, FarmerPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (scan.imageBitmap != null) {
                                        Image(
                                            bitmap = scan.imageBitmap.asImageBitmap(),
                                            contentDescription = "Scan Image",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(Icons.Default.Eco, contentDescription = "Leaf", tint = FarmerPrimary, modifier = Modifier.size(30.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "🌿 ${scan.cropName}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FarmerTextPrimary
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(FarmerPrimary.copy(alpha = 0.12f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                "📊 ${scan.confidence}",
                                                color = FarmerPrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(
                                        "🦠 ${scan.diseaseName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (scan.isHealthy) Color(0xFF15803D) else Color(0xFFD32F2F),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (scan.severity.isNotBlank() && scan.severity != "Unknown" && !scan.isHealthy) {
                                        Text(
                                            "⚠️ Severity: ${scan.severity}",
                                            fontSize = 11.sp,
                                            color = FarmerTextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        "📅 ${scan.formattedDate}",
                                        fontSize = 10.sp,
                                        color = FarmerMuted
                                    )
                                }

                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Open",
                                    tint = FarmerMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // KISAN CALL CENTER / HELPLINE DIALOG
    if (showKisanHelplineModal) {
        AlertDialog(
            onDismissRequest = { showKisanHelplineModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SupportAgent, contentDescription = "Support", tint = FarmerPrimary, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kisan Call Center & Expert Support", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Government of India Kisan Call Center (KCC) provides free, expert agricultural guidance in your local language.",
                        fontSize = 12.sp,
                        color = FarmerTextSecondary,
                        lineHeight = 17.sp
                    )

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📞 Toll-Free Helpline:", fontSize = 11.sp, color = FarmerTextSecondary)
                            Text("1800-180-1551", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("⏰ Available: 6:00 AM to 10:00 PM (All 7 Days)", fontSize = 11.sp, color = FarmerTextSecondary)
                            Text("🗣️ Languages: Marathi, Hindi, English, Gujarati & Regional", fontSize = 11.sp, color = FarmerTextSecondary)
                        }
                    }

                    Button(
                        onClick = {
                            try {
                                val dialIntent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:18001801551")
                                }
                                context.startActivity(dialIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Dialer unavailable. Dial 1800-180-1551", Toast.LENGTH_LONG).show()
                            }
                            showKisanHelplineModal = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call Now", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call 1800-180-1551 Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showKisanHelplineModal = false }) {
                    Text("Close", color = FarmerTextSecondary)
                }
            }
        )
    }

    // IMAGE SOURCE SELECTION MODAL
    if (showImageSourceModal) {
        AlertDialog(
            onDismissRequest = { showImageSourceModal = false },
            title = { Text("Upload or Capture Crop Photo", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select how you want to provide the crop or leaf image:", fontSize = 12.sp, color = FarmerTextSecondary)

                    Button(
                        onClick = {
                            showImageSourceModal = false
                            launchCameraSafely()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📷 Take Photo with Camera", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showImageSourceModal = false
                            galleryLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmerPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🖼️ Choose from Gallery", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageSourceModal = false }) {
                    Text("Cancel", color = FarmerTextSecondary)
                }
            }
        )
    }

    // HISTORY ITEM DETAIL DIALOG
    selectedHistoryItem?.let { historyItem ->
        AlertDialog(
            onDismissRequest = { selectedHistoryItem = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("🌿 ${historyItem.cropName}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(historyItem.formattedDate, fontSize = 11.sp, color = FarmerMuted)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FarmerPrimary)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("📊 ${historyItem.confidence}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (historyItem.imageBitmap != null) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Image(
                                    bitmap = historyItem.imageBitmap.asImageBitmap(),
                                    contentDescription = "Scan Detail",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }

                    item {
                        Text("🦠 Disease: ${historyItem.diseaseName}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFD32F2F))
                        if (historyItem.severity.isNotBlank() && historyItem.severity != "Unknown") {
                            Text("⚠️ Severity: ${historyItem.severity}", fontSize = 12.sp, color = FarmerTextSecondary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (historyItem.symptoms.isNotEmpty()) {
                        item {
                            Text("🔍 Symptoms:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            historyItem.symptoms.forEach { symptom ->
                                Text("• $symptom", fontSize = 11.sp, color = FarmerTextSecondary)
                            }
                        }
                    }

                    if (historyItem.possibleCauses.isNotEmpty()) {
                        item {
                            Text("🌾 Possible Causes:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            historyItem.possibleCauses.forEach { cause ->
                                Text("• $cause", fontSize = 11.sp, color = Color(0xFF78350F))
                            }
                        }
                    }

                    if (historyItem.recommendedAction.isNotEmpty()) {
                        item {
                            Text("🧪 Recommended Treatment:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                            historyItem.recommendedAction.forEach { act ->
                                Text("• $act", fontSize = 11.sp, color = Color(0xFF1B5E20))
                            }
                        }
                    }

                    if (historyItem.prevention.isNotEmpty()) {
                        item {
                            Text("🛡️ Prevention:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                            historyItem.prevention.forEach { prev ->
                                Text("• $prev", fontSize = 11.sp, color = Color(0xFF166534))
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                selectedHistoryItem = null
                                onNavigateStore("Saaf Fungicide")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = "Buy")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buy Treatments in Agri Store", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedHistoryItem = null }) {
                    Text("Close", color = FarmerPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        savedScans.remove(historyItem)
                        selectedHistoryItem = null
                        Toast.makeText(context, "Scan removed from history", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            }
        )
    }
}

// ------------------ 5. BUY FARMING PRODUCTS (AGRI STORE) ------------------
@Composable
fun AgriStoreView(
    products: List<StoreProduct>,
    cartCount: Int,
    onAddToCart: () -> Unit,
    onCheckout: (String, Double) -> Unit,
    onBack: () -> Unit
) {
    var selectedCat by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("Agri Store (Seeds & Fertilizers)", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf("All", "Seeds", "Fertilizers", "Pesticides", "Farming Equipment").forEach { cat ->
                        FilterChip(
                            selected = selectedCat == cat,
                            onClick = { selectedCat = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FarmerPrimary, selectedLabelColor = Color.White)
                        )
                    }
                }
            }

            val filtered = if (selectedCat == "All") products else products.filter { it.category == selectedCat }

            items(filtered) { prod ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(prod.imagePreset, fontSize = 36.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            Text("${prod.brand} • ★ ${prod.rating}", fontSize = 12.sp, color = FarmerTextSecondary)
                            Text("₹${prod.price.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                        }
                        Button(
                            onClick = { onCheckout(prod.name, prod.price) },
                            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Buy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 6. NEARBY LABOUR HIRING ------------------
@Composable
fun HireLabourView(
    labourTeams: List<LabourTeam>,
    onRequestLabour: (String, String, Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedActivity by remember { mutableStateOf("Harvesting (कापणी)") }
    var workersNeeded by remember { mutableStateOf(8) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("Hire Nearby Farm Labour", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Select Farm Work Activity", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            listOf("Harvesting (कापणी)", "Sowing (पेरणी)", "Weeding (खुरपणी)", "Spraying (फवारणी)", "Tilling (नांगरणी)").forEach { act ->
                                FilterChip(
                                    selected = selectedActivity == act,
                                    onClick = { selectedActivity = act },
                                    label = { Text(act, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FarmerPrimary, selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }
                }
            }

            items(labourTeams) { team ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(team.teamName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            Text("★ ${team.rating}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerAccent)
                        }
                        Text("Mukkadam: ${team.mukadamName} (${team.phone})", fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("Location: ${team.village} • ${team.distanceKm} km away", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Skills: ${team.skills}", fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("Daily Wage: ₹${team.dailyWagePerWorker.toInt()} / worker / day", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { onRequestLabour(team.id, selectedActivity, workersNeeded) },
                            enabled = team.status == "Available",
                            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (team.status == "Available") "Send Hiring Request" else "Request Sent ✅", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 7. CONTRACT FARMING ------------------
@Composable
fun ContractFarmingView(
    contracts: List<ContractFarming>,
    onApply: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("Contract Farming Deals", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(contracts) { contract ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(contract.companyName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                        Text("Crop: ${contract.cropName} (${contract.variety})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        Text("Required Qty: ${contract.requiredQuantity}", fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("Offered Price: ₹${contract.offeredPrice.toInt()} / Quintal guaranteed", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerAccent)
                        Text("Quality Specs: ${contract.qualitySpecs}", fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("Harvest Period: ${contract.harvestPeriod} • ${contract.location}", fontSize = 12.sp, color = FarmerTextSecondary)

                        Button(
                            onClick = { onApply(contract.id) },
                            enabled = contract.status == "Open",
                            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (contract.status == "Open") "Apply for Contract" else "Application Submitted ✅", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 9. BROKER TRADING ------------------
@Composable
fun BrokerTradingView(
    brokers: List<BrokerDemand>,
    onAcceptDeal: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("APMC Broker Trading Board", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(brokers) { b ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(b.brokerName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                        Text(b.companyName, fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("Crop Demanded: ${b.cropDemanded}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        Text("Required Qty: ${b.requiredQty}", fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("Offered Price: ₹${b.offeredPricePerQuintal.toInt()} / Quintal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerAccent)
                        Text("Payment: ${b.paymentTerms} • ${b.location}", fontSize = 12.sp, color = FarmerTextSecondary)

                        Button(
                            onClick = { onAcceptDeal(b.id) },
                            enabled = b.dealStatus == "Active Demand",
                            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (b.dealStatus == "Active Demand") "Accept Bulk Deal" else "Deal Agreed ✅", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 10. DIRECT CUSTOMER SELLING ------------------
@Composable
fun DirectCustomerSellingView(
    listings: List<DirectProduceListing>,
    onAddListing: (String, Double, Double, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("Direct Customer Selling", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(listings) { item ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(item.produceName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        Text("Available Qty: ${item.quantityAvailable} ${item.unit} • Grade: ${item.qualityGrade}", fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("Price: ₹${item.pricePerKg.toInt()} / kg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                        Text(item.description, fontSize = 12.sp, color = FarmerTextSecondary)
                    }
                }
            }
        }
    }
}

// ------------------ 11. CENTRALIZED FARMER ACTIVITIES ------------------
@Composable
fun FarmerActivitiesView(
    orders: List<UnifiedOrder>,
    contracts: List<ContractFarming>,
    brokerDemands: List<BrokerDemand>,
    produceList: List<DirectProduceListing>,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedStatusTab by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }

    val statusTabs = listOf("All", "Active", "Pending", "Completed")
    val categoryChips = listOf(
        "All" to "All Categories",
        "Labour" to "👨‍🌾 Labour Jobs",
        "Waste" to "♻️ Waste Sales",
        "Produce" to "🛒 Produce Orders",
        "Products" to "🏪 Product Orders",
        "Contracts" to "🤝 Contracts",
        "Broker" to "📈 Broker Deals"
    )

    val labourReqs = AgroWorldLabourRepository.requirements
    val wasteOrders = AgriWasteDataHub.orders
    val wasteListings = AgriWasteDataHub.listings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("📦 My Activities", onBack)

        // STATUS TABS (All / Active / Pending / Completed)
        TabRow(
            selectedTabIndex = statusTabs.indexOf(selectedStatusTab),
            containerColor = Color.White,
            contentColor = FarmerPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            statusTabs.forEachIndexed { index, tabName ->
                Tab(
                    selected = selectedStatusTab == tabName,
                    onClick = { selectedStatusTab = tabName },
                    text = {
                        Text(
                            text = tabName,
                            fontWeight = if (selectedStatusTab == tabName) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // CATEGORY FILTER CHIPS
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categoryChips) { (key, label) ->
                FilterChip(
                    selected = selectedCategory == key,
                    onClick = { selectedCategory = key },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FarmerPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategory == key,
                        borderColor = if (selectedCategory == key) FarmerPrimary else Color(0xFFE2E8F0)
                    )
                )
            }
        }

        // ACTIVITY FEED
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. LABOUR JOBS
            if (selectedCategory == "All" || selectedCategory == "Labour") {
                val filteredLabour = labourReqs.filter { req ->
                    when (selectedStatusTab) {
                        "Active" -> req.status == "Active - Scheduled" || req.status == "In Progress" || req.status == "Open"
                        "Pending" -> req.status == "Pending Acceptance" || req.status == "Workers Assigned"
                        "Completed" -> req.status == "Completed"
                        else -> true
                    }
                }

                if (filteredLabour.isNotEmpty()) {
                    item {
                        Text("👨‍🌾 Labour Hiring Requirements", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    }
                    items(filteredLabour) { req ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${req.workType} • ${req.crop}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                    ActivityStatusPill(req.status)
                                }
                                Text("Requirement ID: ${req.id} • ${req.village}, ${req.taluka}", fontSize = 12.sp, color = FarmerTextSecondary)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Workers: ${req.workerIdsAccepted.size} / ${req.workersRequired} Filled", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = FarmerPrimary)
                                    Text("Wage: ₹${req.wageAmount.toInt()} (${req.wageType})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                }
                                Text("Work Period: ${req.startDate} to ${req.endDate} (${req.startTime})", fontSize = 11.sp, color = FarmerTextSecondary)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { onNavigate("hire_labour") },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Open Labour Hub ➔", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. AGRI WASTE SALES & LISTINGS
            if (selectedCategory == "All" || selectedCategory == "Waste") {
                val filteredWasteOrders = wasteOrders.filter { ord ->
                    when (selectedStatusTab) {
                        "Active" -> ord.status == "Farmer Accepted" || ord.status == "Order Placed" || ord.status == "Pickup Scheduled"
                        "Pending" -> ord.status == "Waiting for Farmer" || ord.status == "Purchase Request"
                        "Completed" -> ord.status == "Completed" || ord.status == "Delivered"
                        else -> true
                    }
                }

                if (filteredWasteOrders.isNotEmpty()) {
                    item {
                        Text("♻️ Agri Waste Purchase Requests & Orders", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    }
                    items(filteredWasteOrders) { ord ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ord.wasteName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                    ActivityStatusPill(ord.status)
                                }
                                Text("Buyer: ${ord.buyerName} (${ord.buyerPhone})", fontSize = 12.sp, color = FarmerTextSecondary)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Qty: ${ord.quantity} ${ord.unit}", fontSize = 12.sp, color = FarmerTextSecondary)
                                    Text("Total: ₹${ord.totalAmount.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                                }
                                Text("Collection: ${ord.pickupMethod} • Date: ${ord.orderDate}", fontSize = 11.sp, color = FarmerTextSecondary)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { onNavigate("agri_waste") },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Manage Waste Sales ➔", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. DIRECT CUSTOMER PRODUCE
            if (selectedCategory == "All" || selectedCategory == "Produce") {
                val filteredProduce = produceList.filter { p ->
                    when (selectedStatusTab) {
                        "Active" -> p.status == "Active"
                        "Pending" -> p.status == "Under Review"
                        "Completed" -> p.status == "Sold Out"
                        else -> true
                    }
                }

                if (filteredProduce.isNotEmpty()) {
                    item {
                        Text("🛒 Direct Customer Produce Listings", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    }
                    items(filteredProduce) { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.produceName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                    ActivityStatusPill(item.status)
                                }
                                Text("Available: ${item.quantityAvailable} ${item.unit} • Grade: ${item.qualityGrade}", fontSize = 12.sp, color = FarmerTextSecondary)
                                Text("Price: ₹${item.pricePerKg.toInt()}/kg • Harvest: ${item.harvestDate}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { onNavigate("direct_selling") },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Direct Selling Hub ➔", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. AGRI STORE PRODUCT PURCHASES
            if (selectedCategory == "All" || selectedCategory == "Products") {
                val storeOrders = orders.filter { it.orderType == "Product" }.filter { ord ->
                    when (selectedStatusTab) {
                        "Active" -> ord.status == "Confirmed" || ord.status == "Dispatched"
                        "Pending" -> ord.status == "Pending"
                        "Completed" -> ord.status == "Delivered"
                        else -> true
                    }
                }

                if (storeOrders.isNotEmpty()) {
                    item {
                        Text("🏪 Farming Products & Inputs Orders", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    }
                    items(storeOrders) { ord ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ord.itemTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                    ActivityStatusPill(ord.status)
                                }
                                Text("Seller: ${ord.counterpartyName} • ${ord.quantity}", fontSize = 12.sp, color = FarmerTextSecondary)
                                Text("Total: ₹${ord.totalPrice.toInt()} • Ordered on: ${ord.date}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                                Text("Delivery Address: ${ord.address}", fontSize = 11.sp, color = FarmerTextSecondary)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { onNavigate("agri_store") },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Agri Store ➔", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. CONTRACT FARMING APPLICATIONS
            if (selectedCategory == "All" || selectedCategory == "Contracts") {
                val filteredContracts = contracts.filter { c ->
                    when (selectedStatusTab) {
                        "Active" -> c.status == "Approved" || c.status == "Ongoing"
                        "Pending" -> c.status == "Applied" || c.status == "Open"
                        "Completed" -> c.status == "Fulfilled" || c.status == "Closed"
                        else -> true
                    }
                }

                if (filteredContracts.isNotEmpty()) {
                    item {
                        Text("🤝 Contract Farming Opportunities & Applications", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    }
                    items(filteredContracts) { c ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${c.companyName} • ${c.cropName}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                    ActivityStatusPill(c.status)
                                }
                                Text("Required Qty: ${c.requiredQuantity} • Cluster: ${c.location}", fontSize = 12.sp, color = FarmerTextSecondary)
                                Text("Offered Price: ₹${c.offeredPrice.toInt()}/Ton • Period: ${c.harvestPeriod}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { onNavigate("contract_farming") },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("View Contracts ➔", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. BROKER WHOLESALE DEALS
            if (selectedCategory == "All" || selectedCategory == "Broker") {
                val filteredBrokers = brokerDemands.filter { b ->
                    when (selectedStatusTab) {
                        "Active" -> b.dealStatus == "Deal Agreed" || b.dealStatus == "In Progress"
                        "Pending" -> b.dealStatus == "Active Demand"
                        "Completed" -> b.dealStatus == "Completed"
                        else -> true
                    }
                }

                if (filteredBrokers.isNotEmpty()) {
                    item {
                        Text("📈 Broker Wholesale Demands & Deals", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    }
                    items(filteredBrokers) { b ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${b.brokerName} (${b.companyName})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                    ActivityStatusPill(b.dealStatus)
                                }
                                Text("Crop Demanded: ${b.cropDemanded} • ${b.requiredQty}", fontSize = 12.sp, color = FarmerTextSecondary)
                                Text("Offered Price: ₹${b.offeredPricePerQuintal.toInt()}/Quintal • ${b.location}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { onNavigate("broker_trading") },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Broker Hub ➔", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
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

@Composable
fun ActivityStatusPill(status: String) {
    val (bgColor, textColor) = when (status) {
        "Confirmed", "Delivered", "Completed", "Approved", "Deal Agreed", "Active - Scheduled" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "Pending", "Waiting for Farmer", "Pending Acceptance", "Applied", "Active Demand", "Purchase Request", "Workers Assigned" -> Pair(Color(0xFFFFF8E1), Color(0xFFB78103))
        "Active", "In Progress", "Ongoing", "Open" -> Pair(Color(0xFFE0F2FE), Color(0xFF0284C7))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

// ------------------ 12. NOTIFICATIONS ------------------
@Composable
fun NotificationsView(
    notifications: List<FarmerNotification>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("Notifications & Alerts", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(notifications) { notif ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (!notif.isRead) Color(0xFFF0FDF4) else Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(notif.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            Text(notif.timestamp, fontSize = 11.sp, color = FarmerTextSecondary)
                        }
                        Text(notif.message, fontSize = 12.sp, color = FarmerTextSecondary)
                    }
                }
            }
        }
    }
}

// ------------------ HELPER COMPONENTS ------------------
@Composable
fun TopAppBarHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .background(Color.White, CircleShape)
                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = FarmerPrimary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
    }
}

@Composable
fun QuickAddSheetDialog(
    onDismiss: () -> Unit,
    onSelectAddCrop: () -> Unit,
    onSelectPostLabour: () -> Unit,
    onSelectAddWaste: () -> Unit,
    onSelectAddProduce: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("What would you like to add or post?", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onSelectAddCrop,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🌱 Add New Crop Listing", color = Color.White)
                }
                Button(
                    onClick = onSelectPostLabour,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("👨‍🌾 Post Labour Requirement", color = Color.White)
                }
                Button(
                    onClick = onSelectAddWaste,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("♻️ Sell Agri Waste / Straw", color = Color.White)
                }
                Button(
                    onClick = onSelectAddProduce,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🛒 Sell Farm Produce to Customer", color = Color.White)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
