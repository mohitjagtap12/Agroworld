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

// ------------------ SELLER COLOR PALETTE ------------------
val SellerPrimary = Color(0xFF2E7D32)       // Forest Green
val SellerSecondary = Color(0xFF66BB6A)     // Soft Green
val SellerAccent = Color(0xFFF9A825)        // Gold/Amber Accent
val SellerBackground = Color(0xFFF8FBF7)    // Soft agricultural background
val SellerCardBg = Color(0xFFFFFFFF)        // Card White
val SellerTextPrimary = Color(0xFF212121)   // Deep charcoal
val SellerTextSecondary = Color(0xFF616161) // Soft charcoal
val SellerLightBg = Color(0xFFE8F5E9)       // Very light pastel green

// ------------------ DATA MODELS ------------------
data class SellerProduct(
    val id: String,
    val name: String,
    val category: String, // "Fertilizers", "Pesticides", "Seeds", "Organic Products", "Farming Tools"
    val brand: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val unit: String, // "Bag", "Bottle", "Packet", "Kg", "Litre"
    val mfgDate: String,
    val expDate: String,
    val imageEmoji: String,
    val isOfferActive: Boolean = false,
    val discountPercent: Int = 0
)

data class SellerOrder(
    val id: String,
    val farmerName: String,
    val productName: String,
    val quantity: Int,
    val unit: String,
    val pricePerUnit: Double,
    val totalAmount: Double,
    val orderDate: String,
    val address: String,
    val status: String, // "New", "Processing", "Completed", "Rejected"
    val farmerPhone: String = "+91 98765 43210"
)

data class SellerSaleRecord(
    val id: String,
    val date: String,
    val productName: String,
    val category: String,
    val quantity: Int,
    val totalAmount: Double,
    val buyerName: String
)

data class SellerOffer(
    val id: String,
    val title: String,
    val discountPercent: Int,
    val startDate: String,
    val endDate: String,
    val status: String = "Active"
)

// ------------------ SELLER PORTAL MASTER VIEW ------------------
@Composable
fun SellerPortalScreen(navController: NavController) {
    val context = LocalContext.current

    // Onboarding Stages: "otp_verification", "registration", "success", "dashboard"
    var currentPortalStage by remember { mutableStateOf("otp_verification") }

    // OTP verification fields
    var enteredPhone by remember { mutableStateOf("") }
    var enteredOtpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    // Registration Form states
    var regFullName by remember { mutableStateOf("") }
    var regShopLogoUri by remember { mutableStateOf<Uri?>(null) }
    var regShopName by remember { mutableStateOf("") }
    var regShopCategory by remember { mutableStateOf("Fertilizers") }
    var regVillage by remember { mutableStateOf("") }
    var regTaluka by remember { mutableStateOf("Haveli") }
    val regSelectedServices = remember { mutableStateListOf<String>("Fertilizers", "Pesticides") }

    // Dynamic Registered Seller State
    var ownerName by remember { mutableStateOf(SessionManager.getInstance(context).userName.ifEmpty { "Agri-Store Seller" }) }
    var verifiedPhone by remember { mutableStateOf(SessionManager.getInstance(context).userPhone.ifEmpty { "+91 ----------" }) }
    var shopName by remember { mutableStateOf("AgroWorld Verified Krushi Seva Kendra") }
    var shopCategory by remember { mutableStateOf("Fertilizers") }
    var shopVillage by remember { mutableStateOf(SessionManager.getInstance(context).userVillage.ifEmpty { "Market Area" }) }
    var shopTaluka by remember { mutableStateOf(SessionManager.getInstance(context).userDistrict.ifEmpty { "" }) }
    var servicesOffered by remember { mutableStateOf("Fertilizers, Pesticides, Seeds, Organic Products, Farming Tools") }
    var shopLogoUriState by remember { mutableStateOf<Uri?>(null) }

    // Current Screen Routing: 
    // "dashboard" (home), "add_product", "inventory", "orders", "order_details", "sales_history", "create_offer", "profile"
    var currentScreen by remember { mutableStateOf("dashboard") }

    // Dynamic Database lists in memory
    val sellerProducts = remember { mutableStateListOf<SellerProduct>() }

    val sellerOrders = remember { mutableStateListOf<SellerOrder>() }

    val sellerSalesHistory = remember { mutableStateListOf<SellerSaleRecord>() }

    val sellerOffers = remember { mutableStateListOf<SellerOffer>() }

    // Dynamic Selected Item States
    var selectedOrderId by remember { mutableStateOf("") }

    // Navigation and Layout Wrapper
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
                            selectedTextColor = SellerPrimary,
                            indicatorColor = SellerPrimary,
                            unselectedIconColor = SellerTextSecondary,
                            unselectedTextColor = SellerTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "inventory" || currentScreen == "add_product",
                        onClick = { currentScreen = "inventory" },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventory") },
                        label = { Text("Products", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = SellerPrimary,
                            indicatorColor = SellerPrimary,
                            unselectedIconColor = SellerTextSecondary,
                            unselectedTextColor = SellerTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "orders" || currentScreen == "order_details",
                        onClick = { currentScreen = "orders" },
                        icon = {
                            val count = sellerOrders.count { it.status == "New" }
                            BadgedBox(badge = {
                                if (count > 0) {
                                    Badge(containerColor = SellerAccent) {
                                        Text(count.toString(), color = SellerTextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = "Orders")
                            }
                        },
                        label = { Text("Orders", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = SellerPrimary,
                            indicatorColor = SellerPrimary,
                            unselectedIconColor = SellerTextSecondary,
                            unselectedTextColor = SellerTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "sales_history" || currentScreen == "create_offer",
                        onClick = { currentScreen = "sales_history" },
                        icon = { Icon(Icons.Default.Insights, contentDescription = "Sales") },
                        label = { Text("Sales", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = SellerPrimary,
                            indicatorColor = SellerPrimary,
                            unselectedIconColor = SellerTextSecondary,
                            unselectedTextColor = SellerTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "profile",
                        onClick = { currentScreen = "profile" },
                        icon = { Icon(Icons.Default.Store, contentDescription = "Profile") },
                        label = { Text("Shop Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = SellerPrimary,
                            indicatorColor = SellerPrimary,
                            unselectedIconColor = SellerTextSecondary,
                            unselectedTextColor = SellerTextSecondary
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
            modifier = appliedModifier.background(SellerBackground)
        ) {
            AnimatedContent(
                targetState = currentPortalStage,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "seller_portal_stage_transition"
            ) { stage ->
                when (stage) {
                    "otp_verification" -> {
                        SellerOtpVerificationView(
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
                                    verifiedPhone = "+91 $enteredPhone"
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
                        SellerRegistrationView(
                            fullName = regFullName,
                            onFullNameChange = { regFullName = it },
                            verifiedPhone = verifiedPhone,
                            shopName = regShopName,
                            onShopNameChange = { regShopName = it },
                            shopCategory = regShopCategory,
                            onShopCategoryChange = { regShopCategory = it },
                            village = regVillage,
                            onVillageChange = { regVillage = it },
                            taluka = regTaluka,
                            onTalukaChange = { regTaluka = it },
                            selectedServices = regSelectedServices,
                            shopLogoUri = regShopLogoUri,
                            onShopLogoUriChange = { regShopLogoUri = it },
                            onSubmit = {
                                ownerName = regFullName
                                shopName = regShopName
                                shopCategory = regShopCategory
                                shopVillage = regVillage
                                shopTaluka = regTaluka
                                servicesOffered = if (regSelectedServices.isEmpty()) {
                                    "Fertilizers, Pesticides, Seeds, Organic Products, Farming Tools"
                                } else {
                                    regSelectedServices.joinToString(", ")
                                }
                                shopLogoUriState = regShopLogoUri
                                currentPortalStage = "success"
                            }
                        )
                    }
                    "success" -> {
                        SellerRegistrationSuccessView(
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
                            label = "seller_portal_transition"
                        ) { target ->
                            when (target) {
                                "dashboard" -> SellerDashboardView(
                                    products = sellerProducts,
                                    orders = sellerOrders,
                                    sales = sellerSalesHistory,
                                    shopName = shopName,
                                    shopLogoUri = shopLogoUriState,
                                    onNavigate = { currentScreen = it },
                                    onSelectOrder = { id ->
                                        selectedOrderId = id
                                        currentScreen = "order_details"
                                    }
                                )
                                "add_product" -> SellerAddProductView(
                                    onBack = { currentScreen = "inventory" },
                                    onAddProduct = { product ->
                                        sellerProducts.add(0, product)
                                        Toast.makeText(context, "${product.name} published in store!", Toast.LENGTH_LONG).show()
                                        currentScreen = "inventory"
                                    }
                                )
                                "inventory" -> SellerInventoryView(
                                    products = sellerProducts,
                                    onBack = { currentScreen = "dashboard" },
                                    onNavigateAdd = { currentScreen = "add_product" },
                                    onUpdateStock = { id, newStock ->
                                        val index = sellerProducts.indexOfFirst { it.id == id }
                                        if (index != -1) {
                                            sellerProducts[index] = sellerProducts[index].copy(stock = newStock)
                                            Toast.makeText(context, "Stock updated successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onDeleteProduct = { id ->
                                        sellerProducts.removeAll { it.id == id }
                                        Toast.makeText(context, "Product removed from store.", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                "orders" -> SellerOrdersView(
                                    orders = sellerOrders,
                                    onBack = { currentScreen = "dashboard" },
                                    onSelectOrder = { id ->
                                        selectedOrderId = id
                                        currentScreen = "order_details"
                                    }
                                )
                                "order_details" -> {
                                    val order = sellerOrders.find { it.id == selectedOrderId } ?: sellerOrders.first()
                                    SellerOrderDetailsView(
                                        order = order,
                                        onBack = { currentScreen = "orders" },
                                        onAccept = {
                                            val index = sellerOrders.indexOfFirst { it.id == order.id }
                                            if (index != -1) {
                                                sellerOrders[index] = sellerOrders[index].copy(status = "Processing")
                                                Toast.makeText(context, "Order accepted. Prepare for packing.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onReject = {
                                            val index = sellerOrders.indexOfFirst { it.id == order.id }
                                            if (index != -1) {
                                                sellerOrders[index] = sellerOrders[index].copy(status = "Rejected")
                                                Toast.makeText(context, "Order rejected.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onMarkReady = {
                                            val index = sellerOrders.indexOfFirst { it.id == order.id }
                                            if (index != -1) {
                                                sellerOrders[index] = sellerOrders[index].copy(status = "Completed")
                                                // Add to sales history
                                                sellerSalesHistory.add(0, SellerSaleRecord(
                                                    id = "ssr_" + (6..9999).random(),
                                                    date = "18 July 2026",
                                                    productName = order.productName,
                                                    category = "Agri Store Item",
                                                    quantity = order.quantity,
                                                    totalAmount = order.totalAmount,
                                                    buyerName = order.farmerName
                                                ))
                                                Toast.makeText(context, "Order marked as completed & payment received!", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    )
                                }
                                "sales_history" -> SellerSalesHistoryView(
                                    sales = sellerSalesHistory,
                                    onBack = { currentScreen = "dashboard" },
                                    onNavigateOffers = { currentScreen = "create_offer" }
                                )
                                "create_offer" -> SellerCreateOfferView(
                                    offers = sellerOffers,
                                    products = sellerProducts,
                                    onBack = { currentScreen = "sales_history" },
                                    onPublishOffer = { title, percent, start, end, selectedProductIds ->
                                        val newOffer = SellerOffer("sof_" + (3..999).random(), title, percent, start, end)
                                        sellerOffers.add(0, newOffer)
                                        // Apply discount to products
                                        selectedProductIds.forEach { pid ->
                                            val index = sellerProducts.indexOfFirst { it.id == pid }
                                            if (index != -1) {
                                                sellerProducts[index] = sellerProducts[index].copy(
                                                    isOfferActive = true,
                                                    discountPercent = percent
                                                )
                                            }
                                        }
                                        Toast.makeText(context, "Offer '$title' is now live for Selected Products!", Toast.LENGTH_LONG).show()
                                        currentScreen = "sales_history"
                                    }
                                )
                                "profile" -> SellerProfileView(
                                    ownerName = ownerName,
                                    verifiedPhone = verifiedPhone,
                                    shopName = shopName,
                                    shopCategory = shopCategory,
                                    shopVillage = shopVillage,
                                    shopTaluka = shopTaluka,
                                    servicesOffered = servicesOffered,
                                    shopLogoUri = shopLogoUriState,
                                    onBack = { currentScreen = "dashboard" },
                                    onLogout = {
                                        Toast.makeText(context, "Logging out from AgroWorld Seller Portal...", Toast.LENGTH_SHORT).show()
                                        navController.navigate("role_selection") {
                                            popUpTo("dashboard/seller") { inclusive = true }
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

// ------------------ 1. SELLER DASHBOARD VIEW ------------------
@Composable
fun SellerDashboardView(
    products: List<SellerProduct>,
    orders: List<SellerOrder>,
    sales: List<SellerSaleRecord>,
    shopName: String,
    shopLogoUri: Uri?,
    onNavigate: (String) -> Unit,
    onSelectOrder: (String) -> Unit
) {
    val context = LocalContext.current
    val lowStockProducts = products.filter { it.stock <= 10 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // TOP APP BAR / SHOP HEADER
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
                            .background(SellerPrimary.copy(alpha = 0.12f))
                            .border(2.dp, SellerPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (shopLogoUri != null) {
                            Text(
                                text = "📸",
                                fontSize = 24.sp
                            )
                        } else {
                            Text(
                                text = "🚜",
                                fontSize = 24.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "$shopName 🌾",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Shop",
                                tint = SellerPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Licensed Pune Seed & Fertilizer Dealer",
                                fontSize = 12.sp,
                                color = SellerTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { Toast.makeText(context, "No new alerts. Your shop is sync'd.", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = SellerPrimary
                    )
                }
            }
        }

        // SHOP SALES BANNER SUMMARY
        item {
            val totalRevenue = sales.sumOf { it.totalAmount }
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
                                        SellerPrimary.copy(alpha = 0.05f),
                                        SellerSecondary.copy(alpha = 0.15f)
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
                                Text("TODAY'S BUSINESS REVENUE", fontSize = 11.sp, color = SellerTextSecondary, fontWeight = FontWeight.Bold)
                                Text("₹${"%,.2f".format(totalRevenue)}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = SellerPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SellerAccent.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("+18.4% 📈", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payout Schedule: Direct Bank Transfer", fontSize = 11.sp, color = SellerTextSecondary)
                            Text("Next Cycle: Tonight 10 PM", fontSize = 11.sp, color = SellerPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // STATISTICS CARDS
        item {
            val totalProducts = products.size
            val pendingOrders = orders.count { it.status == "New" || it.status == "Processing" }
            val completedOrders = orders.count { it.status == "Completed" }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SellerStatCardBlock("Total Stock", totalProducts.toString(), "Items listed", SellerPrimary, Modifier.weight(1f))
                SellerStatCardBlock("Pending Ord", pendingOrders.toString(), "Needs action", SellerAccent, Modifier.weight(1f))
                SellerStatCardBlock("Done Orders", completedOrders.toString(), "Dispatched", Color(0xFF1565C0), Modifier.weight(1f))
            }
        }

        // QUICK ACTION GRID Title
        item {
            Text(
                text = "Agri-Mall Operations Dashboard",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SellerTextPrimary
            )
        }

        // QUICK ACTION GRID
        item {
            val actions = listOf(
                Triple("Add Product", Icons.Default.AddBusiness, "add_product"),
                Triple("Inventory", Icons.Default.Inventory, "inventory"),
                Triple("Active Orders", Icons.Default.ListAlt, "orders"),
                Triple("Sales History", Icons.Default.Receipt, "sales_history"),
                Triple("Create Offers", Icons.Default.LocalOffer, "create_offer")
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
                            .width(130.dp)
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
                                    .background(SellerPrimary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = label, tint = SellerPrimary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SellerTextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // LOW STOCK ALERTS BLOCK
        if (lowStockProducts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🚨 Low Stock Alert",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD84315)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${lowStockProducts.size} Items Critical",
                            color = Color(0xFFC62828),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            items(lowStockProducts) { product ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F7)),
                    border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(product.imageEmoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SellerTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Brand: ${product.brand} | Category: ${product.category}",
                                    fontSize = 11.sp,
                                    color = SellerTextSecondary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Only ${product.stock} left",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { onNavigate("inventory") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84315)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Refill Stock", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // RECENT ORDERS BLOCK
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Incoming Orders",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerTextPrimary
                )
                TextButton(
                    onClick = { onNavigate("orders") },
                    colors = ButtonDefaults.textButtonColors(contentColor = SellerPrimary)
                ) {
                    Text("View All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        val recentOrders = orders.take(3)
        if (recentOrders.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No recent orders at this moment.",
                        fontSize = 12.sp,
                        color = SellerTextSecondary,
                        modifier = Modifier.padding(18.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(recentOrders) { order ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectOrder(order.id) }
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
                                .background(SellerLightBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📦", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.farmerName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SellerTextPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (order.status) {
                                                "New" -> SellerAccent.copy(alpha = 0.15f)
                                                "Processing" -> Color(0xFFE3F2FD)
                                                "Completed" -> SellerLightBg
                                                else -> Color.LightGray.copy(alpha = 0.3f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = order.status,
                                        color = when (order.status) {
                                            "New" -> Color(0xFFD84315)
                                            "Processing" -> Color(0xFF1565C0)
                                            "Completed" -> SellerPrimary
                                            else -> SellerTextSecondary
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${order.quantity} x ${order.productName}",
                                fontSize = 12.sp,
                                color = SellerTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order Date: ${order.orderDate}",
                                    fontSize = 11.sp,
                                    color = SellerTextSecondary
                                )
                                Text(
                                    text = "₹${order.totalAmount}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SellerPrimary
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
fun SellerStatCardBlock(
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
            Text(text = title, fontSize = 11.sp, color = SellerTextSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sub, fontSize = 10.sp, color = SellerTextSecondary)
        }
    }
}

// ------------------ 2. ADD PRODUCT VIEW ------------------
@Composable
fun SellerAddProductView(
    onBack: () -> Unit,
    onAddProduct: (SellerProduct) -> Unit
) {
    val context = LocalContext.current

    // Form inputs
    var pName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Fertilizers") }
    var pBrand by remember { mutableStateOf("") }
    var pDesc by remember { mutableStateOf("") }
    var pPrice by remember { mutableStateOf("") }
    var pStock by remember { mutableStateOf("") }
    var pUnit by remember { mutableStateOf("Bag") }
    var pMfgDate by remember { mutableStateOf("") }
    var pExpDate by remember { mutableStateOf("") }
    var pEmoji by remember { mutableStateOf("🌱") }

    val categories = listOf("Fertilizers", "Pesticides", "Seeds", "Organic Products", "Farming Tools")
    val units = listOf("Bag", "Bottle", "Packet", "Kg", "Litre", "Piece")
    val emojis = listOf("🌱", "🧴", "🌾", "🪵", "✂️", "📦", "🌽", "🪱")

    var categoryExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SellerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Publish New Product",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SellerTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // PRODUCT EMOJI SELECTOR AS PHOTO UPLOAD SIMULATION
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
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select Shop Representation Icon",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(SellerPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(pEmoji, fontSize = 42.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            emojis.forEach { emo ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (pEmoji == emo) SellerPrimary else Color(0xFFF1F5F9))
                                        .clickable { pEmoji = emo }
                                        .border(1.dp, if (pEmoji == emo) SellerPrimary else Color.Transparent, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emo, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "📸 Photo Upload Verified (Using high-contrast vector asset)",
                            fontSize = 11.sp,
                            color = SellerPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // FIELDS LIST
            item {
                OutlinedTextField(
                    value = pName,
                    onValueChange = { pName = it },
                    label = { Text("Product Name", color = SellerTextSecondary) },
                    placeholder = { Text("e.g. IFFCO Urea, Bayer Regent") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = SellerPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // CATEGORY SELECTOR (Dropdown)
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        label = { Text("Product Category", color = SellerTextSecondary) },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { categoryExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand Category")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SellerPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryExpanded = true }
                    )
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // BRAND
            item {
                OutlinedTextField(
                    value = pBrand,
                    onValueChange = { pBrand = it },
                    label = { Text("Brand Manufacturer", color = SellerTextSecondary) },
                    placeholder = { Text("e.g. Bayer, Tata, Godrej Agro") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = SellerPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // DESCRIPTION
            item {
                OutlinedTextField(
                    value = pDesc,
                    onValueChange = { pDesc = it },
                    label = { Text("Product Description / Usage Instructions", color = SellerTextSecondary) },
                    placeholder = { Text("Enter application dose, crop compatibility details...") },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = SellerPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // PRICE & STOCK ROW
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = pPrice,
                        onValueChange = { pPrice = it },
                        label = { Text("Retail Price (₹)", color = SellerTextSecondary) },
                        placeholder = { Text("e.g. 350") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SellerPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = pStock,
                        onValueChange = { pStock = it },
                        label = { Text("Initial Stock", color = SellerTextSecondary) },
                        placeholder = { Text("e.g. 50") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SellerPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // UNIT DROPDOWN
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = pUnit,
                        onValueChange = {},
                        label = { Text("Packaging Unit", color = SellerTextSecondary) },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { unitExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand Unit")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SellerPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { unitExpanded = true }
                    )
                    DropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        units.forEach { ut ->
                            DropdownMenuItem(
                                text = { Text(ut) },
                                onClick = {
                                    pUnit = ut
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // MFG & EXPIRY DATE ROW
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = pMfgDate,
                        onValueChange = { pMfgDate = it },
                        label = { Text("Mfg. Date", color = SellerTextSecondary) },
                        placeholder = { Text("MM/YYYY") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SellerPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = pExpDate,
                        onValueChange = { pExpDate = it },
                        label = { Text("Expiry Date", color = SellerTextSecondary) },
                        placeholder = { Text("MM/YYYY") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SellerPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // SUBMIT BUTTON
            item {
                Button(
                    onClick = {
                        val parsedPrice = pPrice.toDoubleOrNull()
                        val parsedStock = pStock.toIntOrNull()

                        if (pName.trim().isEmpty() || pBrand.trim().isEmpty() || parsedPrice == null || parsedStock == null) {
                            Toast.makeText(context, "Please enter valid product name, brand, price and stock levels.", Toast.LENGTH_LONG).show()
                        } else {
                            val product = SellerProduct(
                                id = "sp_" + (7..999).random(),
                                name = pName.trim(),
                                category = selectedCategory,
                                brand = pBrand.trim(),
                                description = if (pDesc.isEmpty()) "No explicit descriptions provided by Kisan Seva Mall." else pDesc.trim(),
                                price = parsedPrice,
                                stock = parsedStock,
                                unit = pUnit,
                                mfgDate = if (pMfgDate.isEmpty()) "07/2026" else pMfgDate,
                                expDate = if (pExpDate.isEmpty()) "N/A" else pExpDate,
                                imageEmoji = pEmoji
                            )
                            onAddProduct(product)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(top = 8.dp)
                ) {
                    Text("Publish Product to Farmer Agri Store 🚀", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ------------------ 3. PRODUCT INVENTORY VIEW ------------------
@Composable
fun SellerInventoryView(
    products: List<SellerProduct>,
    onBack: () -> Unit,
    onNavigateAdd: () -> Unit,
    onUpdateStock: (String, Int) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Fertilizers", "Pesticides", "Seeds", "Organic Products", "Farming Tools")

    // Update stock dialog states
    var showStockDialog by remember { mutableStateOf(false) }
    var stockTargetProductId by remember { mutableStateOf("") }
    var stockTargetProductName by remember { mutableStateOf("") }
    var stockInputValue by remember { mutableStateOf("") }

    val filteredProducts = products.filter {
        (selectedCategoryFilter == "All" || it.category == selectedCategoryFilter) &&
                (it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SellerPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inventory Manager",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerTextPrimary
                )
            }

            Button(
                onClick = onNavigateAdd,
                colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search brand, chemical, formula...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = SellerPrimary,
                unfocusedBorderColor = Color(0xFFCBD5E1)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // CATEGORY CHIPS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategoryFilter == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SellerPrimary else Color.White)
                        .clickable { selectedCategoryFilter = cat }
                        .border(1.dp, if (isSelected) SellerPrimary else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else SellerTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // INVENTORY LIST
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No products found matching filters.", color = SellerTextSecondary, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredProducts) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SellerLightBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(item.imageEmoji, fontSize = 28.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = item.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SellerTextPrimary
                                        )
                                        Text(
                                            text = "Brand: ${item.brand} | Exp: ${item.expDate}",
                                            fontSize = 11.sp,
                                            color = SellerTextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SellerPrimary.copy(alpha = 0.1f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(item.category, color = SellerPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${item.price} / ${item.unit}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SellerPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (item.stock > 10) SellerPrimary.copy(alpha = 0.12f)
                                                else Color(0xFFFFEBEE)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "Stock: ${item.stock}",
                                            color = if (item.stock > 10) SellerPrimary else Color(0xFFD84315),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (item.isOfferActive) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SellerAccent.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.LocalOffer, contentDescription = "Promo Offer Active", tint = SellerAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Active Promotion: ${item.discountPercent}% Off applied in store",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD84315)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        stockTargetProductId = item.id
                                        stockTargetProductName = item.name
                                        stockInputValue = item.stock.toString()
                                        showStockDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SellerSecondary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Stock Icon", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Update Stock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onDeleteProduct(item.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(0.7f)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Icon", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // UPDATE STOCK POPUP DIALOG
    if (showStockDialog) {
        AlertDialog(
            onDismissRequest = { showStockDialog = false },
            title = { Text("Update Available Stock", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stockTargetProductName, fontSize = 13.sp, color = SellerTextSecondary)
                    OutlinedTextField(
                        value = stockInputValue,
                        onValueChange = { if (it.all { char -> char.isDigit() }) stockInputValue = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("New Stock Quantity") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = stockInputValue.toIntOrNull()
                        if (parsed != null) {
                            onUpdateStock(stockTargetProductId, parsed)
                            showStockDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary)
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ------------------ 4. ORDERS LIST VIEW ------------------
@Composable
fun SellerOrdersView(
    orders: List<SellerOrder>,
    onBack: () -> Unit,
    onSelectOrder: (String) -> Unit
) {
    var selectedTabState by remember { mutableStateOf(0) } // 0: New, 1: Processing, 2: Completed
    val tabLabels = listOf("New Orders", "Processing", "Completed")

    val filteredOrders = orders.filter {
        when (selectedTabState) {
            0 -> it.status == "New"
            1 -> it.status == "Processing"
            2 -> it.status == "Completed"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SellerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Agri Store Orders",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SellerTextPrimary
            )
        }

        // TABS Row
        TabRow(
            selectedTabIndex = selectedTabState,
            containerColor = Color.White,
            contentColor = SellerPrimary
        ) {
            tabLabels.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTabState == index,
                    onClick = { selectedTabState = index },
                    text = {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTabState == index) SellerPrimary else SellerTextSecondary
                        )
                    }
                )
            }
        }

        // ORDER CARDS
        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📑", fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No ${tabLabels[selectedTabState]} orders at this moment.", color = SellerTextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                items(filteredOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectOrder(order.id) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Order ID: #${order.id}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = SellerTextSecondary
                                    )
                                    Text(
                                        text = order.farmerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = SellerTextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (order.status) {
                                                "New" -> SellerAccent.copy(alpha = 0.15f)
                                                "Processing" -> Color(0xFFE3F2FD)
                                                "Completed" -> SellerLightBg
                                                else -> Color.LightGray.copy(alpha = 0.3f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = order.status,
                                        color = when (order.status) {
                                            "New" -> Color(0xFFD84315)
                                            "Processing" -> Color(0xFF1565C0)
                                            "Completed" -> SellerPrimary
                                            else -> SellerTextSecondary
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Product Ordered", fontSize = 10.sp, color = SellerTextSecondary)
                                    Text(
                                        text = "${order.quantity} x ${order.productName}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SellerTextPrimary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Amount", fontSize = 10.sp, color = SellerTextSecondary)
                                    Text(
                                        text = "₹${order.totalAmount}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SellerPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Date: ${order.orderDate}",
                                    fontSize = 11.sp,
                                    color = SellerTextSecondary
                                )

                                Button(
                                    onClick = { onSelectOrder(order.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Review Order", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 5. ORDER DETAILS VIEW ------------------
@Composable
fun SellerOrderDetailsView(
    order: SellerOrder,
    onBack: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onMarkReady: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SellerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Order Details #${order.id}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SellerTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ORDER STATUS HEADER CARD
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
                            Column {
                                Text("CURRENT STATUS", fontSize = 10.sp, color = SellerTextSecondary, fontWeight = FontWeight.Bold)
                                Text(order.status, fontSize = 20.sp, fontWeight = FontWeight.Black, color = SellerPrimary)
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(SellerPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = "Shipping", tint = SellerPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Received Date: ${order.orderDate}", fontSize = 11.sp, color = SellerTextSecondary)
                            Text("Estimated dispatch: 24 Hours", fontSize = 11.sp, color = SellerPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // FARMER/BUYER INFO CARD
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Farmer & Delivery Information",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SellerPrimary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = "Farmer", tint = SellerPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(order.farmerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SellerTextPrimary)
                                Text(order.farmerPhone, fontSize = 12.sp, color = SellerTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("DELIVERY ADDRESS", fontSize = 10.sp, color = SellerTextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = order.address,
                            fontSize = 12.sp,
                            color = SellerTextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // PRODUCT DETAILS CARD
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Product List",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SellerLightBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🌱", fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(order.productName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SellerTextPrimary)
                                    Text("Quantity: ${order.quantity} x ₹${order.pricePerUnit}", fontSize = 12.sp, color = SellerTextSecondary)
                                }
                            }

                            Text(
                                text = "₹${order.totalAmount}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SellerTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", fontSize = 13.sp, color = SellerTextSecondary)
                            Text("₹${order.totalAmount}", fontSize = 13.sp, color = SellerTextPrimary)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Fee", fontSize = 13.sp, color = SellerTextSecondary)
                            Text("₹0.00 (Self Pick Up / Farm-drop)", fontSize = 13.sp, color = SellerPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("GRAND TOTAL AMOUNT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SellerTextPrimary)
                            Text("₹${order.totalAmount}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SellerPrimary)
                        }
                    }
                }
            }

            // ORDER ACTION PANEL
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (order.status == "New") {
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(50.dp)
                        ) {
                            Text("Accept Order ✓", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onReject,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            border = BorderStroke(1.dp, Color.Red),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("Reject Order", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (order.status == "Processing") {
                        Button(
                            onClick = onMarkReady,
                            colors = ButtonDefaults.buttonColors(containerColor = SellerAccent, contentColor = SellerTextPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Mark as Ready & Dispatch 🚚", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (order.status == "Completed") {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SellerLightBg),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.DoneAll, contentDescription = "Completed Order Icon", tint = SellerPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "This order was processed, delivered, and finalized.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SellerPrimary
                                )
                            }
                        }
                    } else if (order.status == "Rejected") {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = "Rejected Icon", tint = Color.Red)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "This order has been rejected.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 6. SALES HISTORY VIEW ------------------
@Composable
fun SellerSalesHistoryView(
    sales: List<SellerSaleRecord>,
    onBack: () -> Unit,
    onNavigateOffers: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SellerPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sales Revenue History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerTextPrimary
                )
            }

            IconButton(
                onClick = { Toast.makeText(context, "Downloading Sales Ledger Excel...", Toast.LENGTH_SHORT).show() }
            ) {
                Icon(Icons.Default.Download, contentDescription = "Download Ledger", tint = SellerPrimary)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // PROMO / OFFERS BANNER CARD
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SellerAccent.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, SellerAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("📢 Create Promotional Offers", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SellerTextPrimary)
                            Text("Publish discount banners on Farmer Agri Store to boost sales.", fontSize = 11.sp, color = SellerTextSecondary)
                        }
                        Button(
                            onClick = onNavigateOffers,
                            colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Manage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ANALYTICS STATS CARDS
            item {
                val sumAllSales = sales.sumOf { it.totalAmount }
                val countCompleted = sales.size
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("MONTHLY DISPATCHED SALES", fontSize = 11.sp, color = SellerTextSecondary, fontWeight = FontWeight.Bold)
                                Text("₹${"%,.2f".format(sumAllSales)}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = SellerPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("COMPLETED ORDERS", fontSize = 11.sp, color = SellerTextSecondary, fontWeight = FontWeight.Bold)
                                Text("$countCompleted Sales", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SellerPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = "Top Item", tint = SellerAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Top Selling Product: IFFCO Urea Fertilizer",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SellerTextPrimary
                            )
                        }
                    }
                }
            }

            // SALES LEDGER TITLE
            item {
                Text(
                    text = "Sales Ledger Records",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerTextPrimary
                )
            }

            // SALES LEDGER ITEMS
            if (sales.isEmpty()) {
                item {
                    Text(
                        text = "No sales history completed yet.",
                        fontSize = 12.sp,
                        color = SellerTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            } else {
                items(sales) { record ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(SellerPrimary.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Receipt, contentDescription = "Ledger Icon", tint = SellerPrimary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = record.productName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SellerTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Farmer: ${record.buyerName} | Date: ${record.date}",
                                        fontSize = 11.sp,
                                        color = SellerTextSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${record.totalAmount}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SellerPrimary
                                )
                                Text(
                                    text = "Qty: ${record.quantity}",
                                    fontSize = 11.sp,
                                    color = SellerTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 7. CREATE OFFERS VIEW ------------------
@Composable
fun SellerCreateOfferView(
    offers: List<SellerOffer>,
    products: List<SellerProduct>,
    onBack: () -> Unit,
    onPublishOffer: (String, Int, String, String, List<String>) -> Unit
) {
    val context = LocalContext.current

    // Inputs
    var offerTitle by remember { mutableStateOf("") }
    var offerPercent by remember { mutableStateOf("") }
    var offerStart by remember { mutableStateOf("18 July 2026") }
    var offerEnd by remember { mutableStateOf("31 August 2026") }

    val selectedProductIds = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SellerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Promotions & Offers Maker",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SellerTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // EXPLAINER BANNER
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SellerLightBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📊", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Promotional campaigns are rendered instantly in the AgroWorld Farmer Agri Store dashboard. Attract more bulk farmers by running periodic campaigns.",
                            fontSize = 11.sp,
                            color = SellerPrimary,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // CREATION CARD
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Create Promotion Campaign Banners",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        OutlinedTextField(
                            value = offerTitle,
                            onValueChange = { offerTitle = it },
                            label = { Text("Campaign Title", color = SellerTextSecondary) },
                            placeholder = { Text("e.g. Monsoon Organic Seeds Festival") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SellerPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = offerPercent,
                                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) offerPercent = it },
                                label = { Text("Discount (%)", color = SellerTextSecondary) },
                                placeholder = { Text("e.g. 15") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SellerPrimary,
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = offerEnd,
                                onValueChange = { offerEnd = it },
                                label = { Text("Ending Date", color = SellerTextSecondary) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SellerPrimary,
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Text(
                            text = "Choose Products Included in this Campaign",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextSecondary
                        )

                        // SELECTABLE LIST
                        products.forEach { prod ->
                            val isChecked = selectedProductIds.contains(prod.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChecked) SellerPrimary.copy(alpha = 0.05f) else Color.Transparent)
                                    .clickable {
                                        if (isChecked) selectedProductIds.remove(prod.id)
                                        else selectedProductIds.add(prod.id)
                                    }
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        if (isChecked) selectedProductIds.remove(prod.id)
                                        else selectedProductIds.add(prod.id)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = SellerPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(prod.imageEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(prod.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SellerTextPrimary)
                                    Text("Price: ₹${prod.price} | Brand: ${prod.brand}", fontSize = 11.sp, color = SellerTextSecondary)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val parsedPercent = offerPercent.toIntOrNull()
                                if (offerTitle.trim().isEmpty() || parsedPercent == null || selectedProductIds.isEmpty()) {
                                    Toast.makeText(context, "Please complete campaign title, discount percent and select at least one product.", Toast.LENGTH_LONG).show()
                                } else {
                                    onPublishOffer(offerTitle, parsedPercent, offerStart, offerEnd, selectedProductIds.toList())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(top = 8.dp)
                        ) {
                            Text("Publish Active Offer Banner 🚀", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ACTIVE CAMPAIGNS TITLE
            item {
                Text(
                    text = "Live Published Campaigns",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerTextPrimary
                )
            }

            if (offers.isEmpty()) {
                item {
                    Text(
                        text = "No live discount offers running currently.",
                        fontSize = 12.sp,
                        color = SellerTextSecondary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            } else {
                items(offers) { offer ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
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
                                    Text("🎉", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = offer.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SellerTextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SellerAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${offer.discountPercent}% OFF",
                                        color = Color(0xFFE65100),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Duration: ${offer.startDate} - ${offer.endDate}",
                                    fontSize = 11.sp,
                                    color = SellerTextSecondary
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SellerPrimary.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Active", color = SellerPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 8. SHOP PROFILE VIEW ------------------
@Composable
fun SellerProfileView(
    ownerName: String,
    verifiedPhone: String,
    shopName: String,
    shopCategory: String,
    shopVillage: String,
    shopTaluka: String,
    servicesOffered: String,
    shopLogoUri: Uri?,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SellerPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Agri Shop Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SellerTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // HERO SHOP LOGO CARD
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
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(SellerPrimary.copy(alpha = 0.12f))
                                .border(3.dp, SellerPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (shopLogoUri != null) {
                                Text("📸", fontSize = 48.sp)
                            } else {
                                Text("🚜", fontSize = 48.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = shopName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        Text(
                            text = "Primary Category: $shopCategory • Pune Rural Special Partner",
                            fontSize = 11.sp,
                            color = SellerTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SellerPrimary)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "VERIFIED MERCHANT SELLER",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // SHOP DETAILS SECTION
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Business Coordinates",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        SellerProfileItemRow("Owner Name", ownerName, Icons.Default.Person)
                        SellerProfileItemRow("Shop Address", "$shopVillage, $shopTaluka (Pune District)", Icons.Default.LocationOn)
                        SellerProfileItemRow("Language", "मराठी • English • Hindi", Icons.Default.Language)
                        SellerProfileItemRow("Services Offered", servicesOffered, Icons.Default.Eco)
                        SellerProfileItemRow("Phone Support", verifiedPhone, Icons.Default.Phone)
                    }
                }
            }

            // HELP & SUPPORT
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Help & Settings",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { Toast.makeText(context, "Directing to APMC Pune grievance desk...", Toast.LENGTH_SHORT).show() }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HelpOutline, contentDescription = "Help", tint = SellerPrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Grievance Support Desk", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SellerTextPrimary)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = SellerTextSecondary)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { Toast.makeText(context, "Syncing printer or scale devices...", Toast.LENGTH_SHORT).show() }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = SellerPrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Hardware Device Settings (Printer/Scale)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SellerTextPrimary)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = SellerTextSecondary)
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
                        .height(54.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Logout from AgroWorld Seller Portal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SellerProfileItemRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(SellerPrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = SellerPrimary, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(label, fontSize = 10.sp, color = SellerTextSecondary, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SellerTextPrimary)
        }
    }
}

// ------------------ NEW ONBOARDING SUBVIEWS ------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOtpVerificationView(
    enteredPhone: String,
    onPhoneChange: (String) -> Unit,
    enteredOtpCode: String,
    onOtpChange: (String) -> Unit,
    isOtpSent: Boolean,
    onSendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onBackToRoles: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
    ) {
        // Aesthetic Top Decorative Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SellerPrimary, SellerSecondary)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onBackToRoles,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .testTag("seller_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "AgroWorld Portal",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Fertilizer & Pesticide Seller Registration",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // OTP Card Panel
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 180.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Mobile Verification",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerTextPrimary
                )
                Text(
                    text = "We will verify your mobile number via an instant OTP to secure your merchant profile.",
                    fontSize = 12.sp,
                    color = SellerTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Phone Input
                OutlinedTextField(
                    value = enteredPhone,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= 10) {
                            onPhoneChange(it)
                        }
                    },
                    label = { Text("Mobile Number") },
                    placeholder = { Text("Enter 10-digit number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = SellerPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    enabled = !isOtpSent,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("seller_phone_input")
                )

                if (!isOtpSent) {
                    Button(
                        onClick = onSendOtp,
                        enabled = enteredPhone.length == 10,
                        colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("seller_send_otp_button")
                    ) {
                        Text("Send OTP Verification Code", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    // OTP Input
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "OTP Sent to +91 $enteredPhone",
                                fontSize = 11.sp,
                                color = SellerPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Edit Number",
                                fontSize = 11.sp,
                                color = SellerAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onPhoneChange("") }
                            )
                        }

                        OutlinedTextField(
                            value = enteredOtpCode,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() } && it.length <= 6) {
                                    onOtpChange(it)
                                }
                            },
                            label = { Text("Verification Code") },
                            placeholder = { Text("Enter 6-digit OTP") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "OTP", tint = SellerPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_otp_input")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = onVerifyOtp,
                            enabled = enteredOtpCode.length >= 4,
                            colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("seller_verify_otp_button")
                        ) {
                            Text("Verify & Continue", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerRegistrationView(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    verifiedPhone: String,
    shopName: String,
    onShopNameChange: (String) -> Unit,
    shopCategory: String,
    onShopCategoryChange: (String) -> Unit,
    village: String,
    onVillageChange: (String) -> Unit,
    taluka: String,
    onTalukaChange: (String) -> Unit,
    selectedServices: SnapshotStateList<String>,
    shopLogoUri: Uri?,
    onShopLogoUriChange: (Uri?) -> Unit,
    onSubmit: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onShopLogoUriChange(uri)
    }

    var talukaExpanded by remember { mutableStateOf(false) }
    val talukas = listOf("Haveli", "Baramati", "Khed", "Maval", "Junnar", "Shirur", "Indapur", "Purandar", "Bhor", "Daund", "Mulshi", "Ambegaon", "Velhe")
    val categories = listOf("Fertilizers", "Pesticides", "Seeds", "Organic Products", "Farming Tools")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Seller Registration",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SellerPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SellerPrimary.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Step 2 of 2",
                    fontSize = 11.sp,
                    color = SellerPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // SHOP LOGO CIRCLE PICKER
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
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Agri Shop Logo / Brand Photo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(SellerPrimary.copy(alpha = 0.08f))
                                .border(2.dp, SellerPrimary, CircleShape)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (shopLogoUri != null) {
                                Text("📸 Chosen", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SellerPrimary)
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Upload Logo",
                                        tint = SellerPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Tap to pick",
                                        fontSize = 9.sp,
                                        color = SellerTextSecondary
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (shopLogoUri != null) "Logo selected successfully!" else "Optional: Add your storefront logo/photo for dynamic styling",
                            fontSize = 11.sp,
                            color = SellerTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // PERSONAL INFO
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = onFullNameChange,
                            label = { Text("Full Name *") },
                            placeholder = { Text("Enter your full legal name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = SellerPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_fullname_input")
                        )

                        OutlinedTextField(
                            value = verifiedPhone,
                            onValueChange = {},
                            label = { Text("Mobile Number (Verified)") },
                            leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = "Verified Mobile", tint = SellerPrimary) },
                            trailingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = "Verified Status", tint = Color(0xFF2E7D32)) },
                            singleLine = true,
                            readOnly = true,
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // SHOP INFORMATION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Shop Information",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        OutlinedTextField(
                            value = shopName,
                            onValueChange = onShopNameChange,
                            label = { Text("Shop Name *") },
                            placeholder = { Text("e.g. Kisan Fertilizer Agency") },
                            leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = "Shop Name", tint = SellerPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_shopname_input")
                        )

                        Text(
                            text = "Primary Shop Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        // Scrollable row of Categories
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            categories.forEach { category ->
                                val selected = shopCategory == category
                                FilterChip(
                                    selected = selected,
                                    onClick = { onShopCategoryChange(category) },
                                    label = { Text(category) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SellerPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // SHOP LOCATION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Shop Location (Pune District)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        OutlinedTextField(
                            value = village,
                            onValueChange = onVillageChange,
                            label = { Text("Village *") },
                            placeholder = { Text("e.g. Narayangaon") },
                            leadingIcon = { Icon(Icons.Default.Map, contentDescription = "Village", tint = SellerPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_village_input")
                        )

                        // Taluka Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = taluka,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Taluka *") },
                                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = "Taluka", tint = SellerPrimary) },
                                trailingIcon = {
                                    IconButton(onClick = { talukaExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Taluka")
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { talukaExpanded = true }
                            )

                            DropdownMenu(
                                expanded = talukaExpanded,
                                onDismissRequest = { talukaExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                talukas.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            onTalukaChange(item)
                                            talukaExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SERVICES OFFERED
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Services / Products Offered (Multi-select)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SellerTextPrimary
                        )

                        categories.forEach { service ->
                            val selected = selectedServices.contains(service)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (selected) {
                                            selectedServices.remove(service)
                                        } else {
                                            selectedServices.add(service)
                                        }
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = { checked ->
                                        if (checked == true) {
                                            selectedServices.add(service)
                                        } else {
                                            selectedServices.remove(service)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = SellerPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = service,
                                    fontSize = 13.sp,
                                    color = SellerTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // SUBMIT BUTTON
            item {
                val isFormValid = fullName.trim().isNotEmpty() &&
                        shopName.trim().isNotEmpty() &&
                        village.trim().isNotEmpty()

                Button(
                    onClick = onSubmit,
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SellerPrimary,
                        disabledContainerColor = Color(0xFFCBD5E1)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("seller_create_account_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Submit", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Seller Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SellerRegistrationSuccessView(
    onGoToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SellerBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Soft Circle with Success Indicator
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(SellerPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.TaskAlt,
                contentDescription = "Success Icon",
                tint = SellerPrimary,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Congratulations!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SellerTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Welcome to AgroWorld! Your Seller account has been created successfully.",
            fontSize = 15.sp,
            color = SellerTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onGoToDashboard,
            colors = ButtonDefaults.buttonColors(containerColor = SellerPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("seller_go_to_dashboard_button")
        ) {
            Text(
                text = "Go to Dashboard",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
        }
    }
}
