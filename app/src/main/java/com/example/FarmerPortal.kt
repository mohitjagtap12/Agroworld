package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.widget.Toast
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

    // Quick Add Sheet Modal State
    var showQuickAddModal by remember { mutableStateOf(false) }

    // Profile States
    var farmerName by remember { mutableStateOf("Ramesh Patil") }
    var mobileNumber by remember { mutableStateOf("+91 98220 14589") }
    var village by remember { mutableStateOf("Narayangaon") }
    var taluka by remember { mutableStateOf("Junnar") }
    var district by remember { mutableStateOf("Pune") }
    var landArea by remember { mutableStateOf("5.5 Acres") }
    var preferredCrops by remember { mutableStateOf("Pune Red Onions, Sugarcane, Soybean") }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Dynamic Memory Databases
    val cropsList = remember {
        mutableStateListOf(
            FarmerCrop("c1", "Pune Red Onions", "N-53 Grade A", "Vegetables", "2.5", "Acres", "15 April 2026", "20 August 2026", "Drip Irrigation", 12.0, 1950.0, "Organic compost grown in Junnar cluster.", "Growing", "🧅"),
            FarmerCrop("c2", "Sugarcane", "Co 86032", "Cash Crop", "2.0", "Acres", "10 Jan 2026", "15 Dec 2026", "Drip Irrigation", 80.0, 3100.0, "High sugar yield drip irrigated cane.", "Ready for Harvest", "🎋"),
            FarmerCrop("c3", "Indrayani Rice", "Scented Certified", "Grains", "1.0", "Acre", "05 June 2026", "10 Oct 2026", "Canal Irrigation", 25.0, 4200.0, "Fragrant Indrayani rice paddy.", "Growing", "🌾")
        )
    }

    val ordersList = remember {
        mutableStateListOf(
            UnifiedOrder("ord1", "Produce", "Pune Red Onions (5 Quintal)", "Crop Sales", "Suresh Mehta (Wholesaler)", "+91 98451 23091", "5 Quintal", 9750.0, "12 Aug 2026", "Pending", "Wagholi APMC, Pune"),
            UnifiedOrder("ord2", "Product", "Mahadhan NPK 10:26:26 (2 Bags)", "Agri Store", "Kisan Krushi Kendra Junnar", "+91 94220 11223", "2 Bags", 2300.0, "11 Aug 2026", "Out for Delivery", "Narayangaon Farm House"),
            UnifiedOrder("ord3", "Waste", "Sugarcane Residue / पाचट (3 Tons)", "Agri Waste", "BioPower Pellet Unit", "+91 91223 45678", "3 Tons", 4500.0, "08 Aug 2026", "Delivered", "Junnar Pellet Mill")
        )
    }

    val storeProducts = remember {
        listOf(
            StoreProduct("p1", "Mahadhan 10:26:26 NPK Fertilizer", "Mahadhan", "Fertilizers", 1150.0, 45, 4.8, "High-efficiency balanced fertilizer perfect for onions & sugarcane.", "🧪"),
            StoreProduct("p2", "Neem Bark Natural Pesticide 1L", "EcoShield", "Pesticides", 340.0, 18, 4.6, "100% cold-pressed organic leaf neem oil concentrate.", "🍃"),
            StoreProduct("p3", "Indrayani Scented Seeds 5Kg", "MahaSeeds", "Seeds", 680.0, 80, 4.9, "High germination rate certified by MAHABEEJ.", "🌾"),
            StoreProduct("p4", "Heavy Duty Agri Power Sprayer", "Stihl Tool", "Farming Equipment", 4200.0, 12, 4.7, "Battery operated 16L knapsack sprayer with brass nozzle.", "⚙️"),
            StoreProduct("p5", "Bayer Confidor Insecticide 250ml", "Bayer", "Pesticides", 520.0, 30, 4.8, "Fast protection against sucking pests in onions and tomatoes.", "🐛")
        )
    }

    val labourTeams = remember {
        mutableStateListOf(
            LabourTeam("l1", "Maruti Labour Squad", "Kisan Mukhya Mukkadam", "+91 98112 33445", "Narayangaon", 2.5, "Sugarcane & Onion harvesting experts", 8, 400.0, 4.9, "Available"),
            LabourTeam("l2", "Jay Malhar Shetmajur Tokoli", "Ganesh Mukkadam", "+91 97665 44332", "Ozar", 4.8, "Weeding, spraying & transplanting specialists", 6, 380.0, 4.7, "Available"),
            LabourTeam("l3", "Shree Ram Farm Labour Gang", "Prakash Thorat", "+91 94210 99887", "Alephata", 6.2, "Heavy tilling, loading & tractor work", 10, 450.0, 4.8, "Available")
        )
    }

    val contractList = remember {
        mutableStateListOf(
            ContractFarming("cf1", "Sahyadri Farmers Producer Co.", "Export Red Onions", "N-53 / Garwa", "100 Tons", 2200.0, "Grade A, Size 45-60mm, Max moisture 10%", "Sept - Oct 2026", "Junnar Cluster", "Open"),
            ContractFarming("cf2", "PepsiCo India Processing", "Chip Potato (FC-5)", "FC-5 White", "50 Tons", 1850.0, "High dry matter content, zero greening", "Nov 2026", "Pune District", "Open"),
            ContractFarming("cf3", "Tata Rallis Agri Corp", "Sweet Corn Harvest", "Sugar-75", "25 Tons", 2800.0, "Fresh green cobs with uniform kernel fill", "Aug - Sept 2026", "Ambegaon / Junnar", "Open")
        )
    }

    val brokerDemands = remember {
        mutableStateListOf(
            BrokerDemand("b1", "Sunil Deshmukh (APMC Trader)", "Deshmukh Agro Junnar", "+91 94220 84521", "Pune Red Onions", "200 Quintals", 2000.0, "Junnar Market Yard", "Instant UPI / Cash on Delivery", "Active Demand"),
            BrokerDemand("b2", "Suresh Traders & Exporters", "Suresh Global Pune", "+91 98901 12345", "Indrayani Scented Rice", "100 Quintals", 4350.0, "Market Yard Gate 4, Pune", "3-Day Direct Bank Transfer", "Active Demand")
        )
    }

    val directProduceList = remember {
        mutableStateListOf(
            DirectProduceListing("dp1", "Fresh Organic Red Onions", 15.0, "Quintal", 22.0, "Grade A Organic", "Ready Harvest", "Direct farm fresh onions, no chemical storage spray.", "Active")
        )
    }

    val notificationsList = remember {
        mutableStateListOf(
            FarmerNotification("n1", "Labour Request Accepted! 👨‍🌾", "Maruti Labour Squad (8 workers) confirmed for 15th August for Onion Harvesting.", "10 mins ago", "Labour", isRead = false),
            FarmerNotification("n2", "Contract Farming Application Update 🤝", "Sahyadri Farmers Producer Co. reviewed your 100 Ton Onion contract request.", "2 hours ago", "Contract", isRead = false),
            FarmerNotification("n3", "New Broker Demand Posted 📈", "Deshmukh Agro offered ₹2000/Quintal for Red Onions in Junnar Market Yard.", "Yesterday", "Broker", isRead = true),
            FarmerNotification("n4", "AI Crop Disease Scan Complete 🤖", "Purple Blotch detected on Onion sample. Remedy recommended in Agri Store.", "2 days ago", "AI Disease", isRead = true)
        )
    }

    val savedDiseaseScans = remember {
        mutableStateListOf(
            SavedDiseaseScan(
                id = "scan_01",
                cropName = "Tomato",
                diseaseName = "Tomato Early Blight",
                confidence = "High",
                severity = "Moderate",
                symptoms = listOf(
                    "Dark brown to black concentric ring spots on older lower leaves",
                    "Yellow chlorotic halo surrounding necrotic lesions",
                    "Premature leaf yellowing and defoliation"
                ),
                possibleCauses = listOf(
                    "Alternaria solani fungal pathogen",
                    "High humidity and prolonged leaf wetness",
                    "Overhead sprinkler irrigation splashing soil onto foliage"
                ),
                recommendedAction = listOf(
                    "Prune and safely destroy infected lower foliage",
                    "Apply Chlorothalonil 75% WP @ 2g/L or Mancozeb fungicide spray",
                    "Switch to drip irrigation to keep upper canopy dry"
                ),
                prevention = listOf(
                    "Practice 3-year crop rotation with non-solanaceous crops",
                    "Maintain 60cm row spacing for optimal air circulation",
                    "Stake vines to prevent contact with bare soil"
                ),
                imageQuality = "good",
                imageBitmap = SampleLeafGenerator.createSampleLeafBitmap("Tomato"),
                formattedDate = "14 Aug 2026, 10:30 AM"
            ),
            SavedDiseaseScan(
                id = "scan_02",
                cropName = "Pune Red Onion",
                diseaseName = "Purple Blotch (Alternaria porri)",
                confidence = "High",
                severity = "Moderate",
                symptoms = listOf(
                    "Small water-soaked lesions developing into elliptical purple-brown spots",
                    "Concentric light and dark zones with a distinct yellow halo",
                    "Leaves girdling and falling over prematurely"
                ),
                possibleCauses = listOf(
                    "Alternaria porri fungal spores",
                    "Warm temperatures (25-30°C) with relative humidity above 80%",
                    "Thrips injury providing entry points for spores"
                ),
                recommendedAction = listOf(
                    "Spray Mancozeb 75% WP @ 2.5g/L mixed with an agricultural sticker",
                    "Apply Azoxystrobin 23% SC @ 1ml/L in severe cases",
                    "Ensure adequate field drainage"
                ),
                prevention = listOf(
                    "Treat seeds with Thiram or Carbendazim before sowing",
                    "Avoid excessive nitrogen fertilization",
                    "Control thrips infestations early"
                ),
                imageQuality = "good",
                imageBitmap = SampleLeafGenerator.createSampleLeafBitmap("Onion"),
                formattedDate = "09 Aug 2026, 04:15 PM"
            )
        )
    }

    // Active Cart
    var cartCount by remember { mutableStateOf(1) }

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
                        selected = currentSubScreen == "orders",
                        onClick = { currentSubScreen = "orders" },
                        icon = {
                            val unconfirmed = ordersList.count { it.status == "Pending" }
                            BadgedBox(badge = {
                                if (unconfirmed > 0) {
                                    Badge(containerColor = FarmerAccent) {
                                        Text(unconfirmed.toString(), color = FarmerTextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = "Orders")
                            }
                        },
                        label = { Text("Orders", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = FarmerPrimary,
                            indicatorColor = FarmerPrimary,
                            unselectedIconColor = FarmerTextSecondary,
                            unselectedTextColor = FarmerTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_orders")
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
                            currentSubScreen = "orders"
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
                    "orders" -> UnifiedOrdersView(
                        orders = ordersList,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(FarmerPrimary.copy(alpha = 0.15f))
                            .border(2.dp, FarmerPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (farmerName.isNotEmpty()) farmerName.first().toString() else "F",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Namaskar, $farmerName 🙏",
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
                                text = "$village, $taluka (Pune)",
                                fontSize = 12.sp,
                                color = FarmerTextSecondary
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { onNavigate("notifications") },
                    modifier = Modifier
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
                    Text("🌤️", fontSize = 36.sp)
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
                    Column {
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
                        Text("Manage ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
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

        // QUICK ACTIONS GRID (8 MAJOR MODULES)
        item {
            Text(
                text = "Services & Marketplaces",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = FarmerTextPrimary
            )

            val actions = listOf(
                Triple("AI Crop Disease", "🤖 Check Health", "ai_disease"),
                Triple("My Crops", "🌱 Add & Manage", "my_crops"),
                Triple("Buy Products", "🏪 Seeds & Fertilizer", "agri_store"),
                Triple("Hire Labour", "👨‍🌾 Nearby Workers", "hire_labour"),
                Triple("Contract Farming", "🤝 Company Deals", "contract_farming"),
                Triple("List Agri Waste", "♻️ Sell Crop Residue", "agri_waste"),
                Triple("Broker Trading", "📈 APMC Wholesale", "broker_trading"),
                Triple("Sell to Customer", "🛒 Direct Selling", "direct_selling")
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                for (r in 0 until 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (c in 0 until 2) {
                            val act = actions[r * 2 + c]
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigate(act.third) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(act.second.substring(0, 2), fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(act.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(act.second.drop(2).trim(), fontSize = 10.sp, color = FarmerTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // RECENT ORDERS RECAP
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Orders & Deals",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerTextPrimary
                )
                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmerPrimary,
                    modifier = Modifier.clickable { onNavigate("orders") }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                ordersList.take(2).forEach { ord ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
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
    onNavigateAdd: () -> Unit,
    onDeleteCrop: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var editingCrop by remember { mutableStateOf<FarmerCrop?>(null) }

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
                    }
                }
            }
        }
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
    savedScans: SnapshotStateList<SavedDiseaseScan>,
    onSaveScan: (SavedDiseaseScan) -> Unit,
    onNavigateStore: (String) -> Unit,
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

    var selectedCrop by remember { mutableStateOf("Tomato") }
    var showCropDropdown by remember { mutableStateOf(false) }

    // Selected / Captured Image Bitmap
    var currentImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showImageSourceModal by remember { mutableStateOf(false) }

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

                // 2. IMAGE UPLOAD & PREVIEW SECTION
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
                                                Text("🌿 Gemini AI is analyzing your crop...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Sending image to Gemini Vision Model", color = Color(0xFFE0E0E0), fontSize = 11.sp, textAlign = TextAlign.Center)
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

                // 3. AI ANALYSIS RESULT DISPLAY
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

                                    Button(
                                        onClick = { showImageSourceModal = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84315)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.AddAPhoto, contentDescription = "Upload Another")
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Upload Another Photo", fontWeight = FontWeight.Bold, color = Color.White)
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
                                                    Text("gemini-3.5-flash", fontSize = 9.sp, color = FarmerPrimary, fontWeight = FontWeight.SemiBold)
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

                                    // Save / Action Row
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
                                        color = Color(0xFFD32F2F),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (scan.severity.isNotBlank() && scan.severity != "Unknown") {
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

// ------------------ 11. UNIFIED ORDERS ------------------
@Composable
fun UnifiedOrdersView(
    orders: List<UnifiedOrder>,
    onBack: () -> Unit
) {
    var filterCat by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        TopAppBarHeader("Orders & Deliveries", onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Product", "Produce", "Waste").forEach { cat ->
                        FilterChip(
                            selected = filterCat == cat,
                            onClick = { filterCat = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FarmerPrimary, selectedLabelColor = Color.White)
                        )
                    }
                }
            }

            val filtered = if (filterCat == "All") orders else orders.filter { it.orderType == filterCat }

            items(filtered) { ord ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(ord.itemTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            Text(ord.status, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                        }
                        Text("Type: ${ord.orderType} • ${ord.counterpartyName}", fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("Total: ₹${ord.totalPrice.toInt()} • Date: ${ord.date}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerAccent)
                        Text("Delivery Address: ${ord.address}", fontSize = 11.sp, color = FarmerTextSecondary)
                    }
                }
            }
        }
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
    onSelectAddWaste: () -> Unit,
    onSelectAddProduce: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("What would you like to list?", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
