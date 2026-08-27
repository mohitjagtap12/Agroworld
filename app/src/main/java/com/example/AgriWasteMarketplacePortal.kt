package com.example

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

// ------------------ AGRI WASTE THEME PALETTE ------------------
val WastePrimary = Color(0xFF2E7D32)       // Forest Green
val WasteSecondary = Color(0xFF66BB6A)     // Soft Green
val WasteAccent = Color(0xFFF9A825)        // Accent Gold / Amber
val WasteBackground = Color(0xFFF8FBF7)    // Soft Eco Mint Background
val WasteCardBg = Color(0xFFFFFFFF)        // Pure White Cards
val WasteTextPrimary = Color(0xFF212121)   // Charcoal
val WasteTextSecondary = Color(0xFF616161) // Secondary Slate
val WasteLightGreen = Color(0xFFE8F5E9)    // Pastel Green
val WasteAmberLight = Color(0xFFFFF8E1)    // Pastel Amber
val WasteBorder = Color(0xFFE0E0E0)        // Border Light

// ==============================================================================
// 1. FARMER MODULE: AGRI WASTE LISTING & SALES MANAGEMENT ONLY
// ==============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerAgriWasteHubScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(0) } // 0: My Listings, 1: List Waste, 2: Buyer Requests & Orders

    // Modals
    var editingListing by remember { mutableStateOf<AgriWasteItem?>(null) }
    var viewingListingDetails by remember { mutableStateOf<AgriWasteItem?>(null) }
    var viewingOrderDetails by remember { mutableStateOf<AgriWasteOrder?>(null) }

    val farmerListings = AgriWasteDataHub.listings.filter { it.farmerId == "f_ramesh" || it.farmerName.contains("Ramesh") }
    val farmerOrders = AgriWasteDataHub.orders.filter { it.farmerId == "f_ramesh" || it.farmerName.contains("Ramesh") }
    val pendingRequestsCount = farmerOrders.count { it.status == "Waiting for Farmer" || it.status == "Order Placed" }

    Scaffold(
        containerColor = WasteBackground,
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(WasteLightGreen)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WastePrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "♻️ List Agri Waste",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WastePrimary
                                )
                                Text(
                                    "Farmer Waste Listing & Sales Hub",
                                    fontSize = 11.sp,
                                    color = WasteTextSecondary
                                )
                            }
                        }

                        // Badge showing pending buyer requests
                        if (pendingRequestsCount > 0) {
                            Badge(
                                containerColor = WasteAccent,
                                contentColor = Color.Black,
                                modifier = Modifier.clickable { activeTab = 2 }
                            ) {
                                Text(
                                    "$pendingRequestsCount New Requests",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Strict Farmer Navigation Tabs: My Listings | List New Waste | Buyer Requests
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = Color.White,
                        contentColor = WastePrimary
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            text = { Text("📋 My Listings (${farmerListings.size})", fontSize = 12.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = { Text("➕ List Waste", fontSize = 12.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = activeTab == 2,
                            onClick = { activeTab = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💬 Buyer Requests", fontSize = 12.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal)
                                    if (pendingRequestsCount > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(Color.Red),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("$pendingRequestsCount", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (activeTab) {
                0 -> FarmerMyListingsView(
                    listings = farmerListings,
                    onAddNew = { activeTab = 1 },
                    onView = { viewingListingDetails = it },
                    onEdit = { editingListing = it },
                    onDelete = {
                        AgriWasteDataHub.deleteListing(it.id)
                        Toast.makeText(context, "Listing deleted", Toast.LENGTH_SHORT).show()
                    }
                )
                1 -> FarmerAddWasteListingView(
                    onListingPublished = { newListing ->
                        AgriWasteDataHub.addListing(newListing)
                        Toast.makeText(context, "Waste listing published successfully!", Toast.LENGTH_LONG).show()
                        activeTab = 0
                    }
                )
                2 -> FarmerBuyerRequestsView(
                    orders = farmerOrders,
                    onAccept = { orderId ->
                        AgriWasteDataHub.acceptOrder(orderId)
                        Toast.makeText(context, "Purchase request accepted! Order confirmed.", Toast.LENGTH_SHORT).show()
                    },
                    onReject = { orderId ->
                        AgriWasteDataHub.rejectOrder(orderId)
                        Toast.makeText(context, "Request declined.", Toast.LENGTH_SHORT).show()
                    },
                    onUpdateStatus = { orderId, newStatus ->
                        AgriWasteDataHub.updateOrderStatus(orderId, newStatus)
                        Toast.makeText(context, "Status updated to: $newStatus", Toast.LENGTH_SHORT).show()
                    },
                    onViewOrder = { viewingOrderDetails = it }
                )
            }
        }
    }

    // Modal: View Listing Details
    viewingListingDetails?.let { listing ->
        Dialog(onDismissRequest = { viewingListingDetails = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${listing.imageEmoji} ${listing.wasteType}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                        IconButton(onClick = { viewingListingDetails = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Text(listing.wasteName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = WasteTextPrimary)

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    DetailRow(label = "Available Quantity", value = "${listing.quantity} ${listing.unit} (Initial: ${listing.initialQuantity} ${listing.unit})")
                    DetailRow(label = "Expected Price", value = "₹${listing.price} ${listing.priceUnit}")
                    DetailRow(label = "Location", value = "${listing.village}, ${listing.taluka}, ${listing.district}")
                    DetailRow(label = "Available Date", value = listing.availableDate)
                    DetailRow(label = "Pickup Preference", value = listing.pickupPreference)
                    DetailRow(label = "Listing Status", value = listing.status)

                    if (listing.description.isNotBlank()) {
                        Text("Description:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WasteTextSecondary)
                        Text(listing.description, fontSize = 12.sp, color = WasteTextPrimary)
                    }

                    Button(
                        onClick = { viewingListingDetails = null },
                        colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close Details")
                    }
                }
            }
        }
    }

    // Modal: Edit Listing
    editingListing?.let { listing ->
        var editName by remember { mutableStateOf(listing.wasteName) }
        var editQty by remember { mutableStateOf(listing.quantity.toString()) }
        var editPrice by remember { mutableStateOf(listing.price.toString()) }
        var editDesc by remember { mutableStateOf(listing.description) }

        Dialog(onDismissRequest = { editingListing = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Edit Waste Listing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WastePrimary)

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Waste Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editQty,
                            onValueChange = { editQty = it },
                            label = { Text("Quantity (${listing.unit})") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = editPrice,
                            onValueChange = { editPrice = it },
                            label = { Text("Price (${listing.priceUnit})") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { editingListing = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val updatedQty = editQty.toDoubleOrNull() ?: listing.quantity
                                val updatedStatus = if (updatedQty <= 0) "Sold Out" else "Available"
                                AgriWasteDataHub.updateListing(
                                    listing.copy(
                                        wasteName = editName,
                                        quantity = updatedQty,
                                        price = editPrice.toDoubleOrNull() ?: listing.price,
                                        description = editDesc,
                                        status = updatedStatus
                                    )
                                )
                                Toast.makeText(context, "Listing updated", Toast.LENGTH_SHORT).show()
                                editingListing = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 2. FARMER: ADD WASTE LISTING FORM ------------------

@Composable
fun FarmerAddWasteListingView(
    onListingPublished: (AgriWasteItem) -> Unit
) {
    val context = LocalContext.current

    // Waste Type Selection
    var selectedWasteType by remember { mutableStateOf(AGRI_WASTE_TYPES[0]) }
    var customWasteName by remember { mutableStateOf("Dry Wheat Straw") }
    var quantity by remember { mutableStateOf("500") }
    var unit by remember { mutableStateOf("kg") }
    var price by remember { mutableStateOf("4") }
    var priceUnit by remember { mutableStateOf("₹/kg") }
    var availableDate by remember { mutableStateOf("5 September 2026") }
    var village by remember { mutableStateOf("Baramati") }
    var taluka by remember { mutableStateOf("Baramati") }
    var district by remember { mutableStateOf("Pune") }
    var description by remember { mutableStateOf("Dry wheat straw suitable for agricultural reuse, dairy fodder, and compost.") }
    var pickupPreference by remember { mutableStateOf("Both Supported") } // "Buyer Pickup", "Delivery Partner", "Both Supported"
    var selectedEmoji by remember { mutableStateOf("🌾") }

    // Preview Step Toggle
    var showPreviewModal by remember { mutableStateOf(false) }

    val unitsList = listOf("kg", "quintal", "ton", "bundle", "bags")
    val priceUnitsList = listOf("₹/kg", "₹/quintal", "₹/ton", "₹/bundle", "₹/bag")
    val pickupOptions = listOf("Buyer Pickup", "Delivery Partner", "Both Supported")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WasteLightGreen),
                border = BorderStroke(1.dp, WasteSecondary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Sell Crop Residue Easily", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                        Text("List dry straw, stalks, bagasse or husks to connect with industrial biomass & dairy buyers.", fontSize = 11.sp, color = WasteTextSecondary)
                    }
                }
            }
        }

        // STEP 1: SELECT WASTE TYPE (9 Official Types)
        item {
            Text("1. Select Agricultural Waste Type *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AGRI_WASTE_TYPES) { wt ->
                    val isSelected = selectedWasteType.name == wt.name
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) WasteLightGreen else Color.White),
                        border = BorderStroke(1.5.dp, if (isSelected) WastePrimary else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .width(130.dp)
                            .clickable {
                                selectedWasteType = wt
                                selectedEmoji = wt.emoji
                                customWasteName = "${wt.name} Residue"
                                unit = wt.defaultUnit
                                priceUnit = wt.defaultPriceUnit
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(wt.emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                wt.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) WastePrimary else WasteTextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // STEP 2: DETAILS
        item {
            Text("2. Waste Details & Pricing *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = customWasteName,
                onValueChange = { customWasteName = it },
                label = { Text("Waste Listing Title *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WastePrimary)
            )
        }

        // Quantity & Unit
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity *") },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WastePrimary)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text("Unit", fontSize = 11.sp, color = WasteTextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        unitsList.forEach { u ->
                            FilterChip(
                                selected = unit == u,
                                onClick = { unit = u },
                                label = { Text(u, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WastePrimary, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }
        }

        // Price & Price Unit
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (₹) *") },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WastePrimary)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text("Price Unit", fontSize = 11.sp, color = WasteTextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        priceUnitsList.forEach { pu ->
                            FilterChip(
                                selected = priceUnit == pu,
                                onClick = { priceUnit = pu },
                                label = { Text(pu, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WastePrimary, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }
        }

        // Available Date
        item {
            OutlinedTextField(
                value = availableDate,
                onValueChange = { availableDate = it },
                label = { Text("Available Date / Ready For Pickup *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WastePrimary)
            )
        }

        // STEP 3: LOCATION & LOGISTICS
        item {
            Text("3. Location & Pickup Preferences *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = village,
                    onValueChange = { village = it },
                    label = { Text("Village *") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = taluka,
                    onValueChange = { taluka = it },
                    label = { Text("Taluka *") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text("District") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Text("Pickup Preference", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WasteTextPrimary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                pickupOptions.forEach { opt ->
                    FilterChip(
                        selected = pickupPreference == opt,
                        onClick = { pickupPreference = opt },
                        label = { Text(opt, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WastePrimary, selectedLabelColor = Color.White)
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Crop Origin *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // ACTION BUTTONS: PREVIEW & PUBLISH
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { showPreviewModal = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, WastePrimary)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "Preview", tint = WastePrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Preview", color = WastePrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val parsedQty = quantity.toDoubleOrNull() ?: 100.0
                        val parsedPrice = price.toDoubleOrNull() ?: 4.0
                        val newListing = AgriWasteItem(
                            id = "AW-LST-" + (100..999).random(),
                            farmerId = "f_ramesh",
                            farmerName = "Ramesh Patil",
                            farmerPhone = "+91 98220 14589",
                            wasteType = selectedWasteType.name,
                            wasteName = customWasteName.ifBlank { selectedWasteType.name },
                            category = selectedWasteType.category,
                            quantity = parsedQty,
                            initialQuantity = parsedQty,
                            unit = unit,
                            price = parsedPrice,
                            priceUnit = priceUnit,
                            availableDate = availableDate,
                            village = village,
                            taluka = taluka,
                            district = district,
                            description = description,
                            imageEmoji = selectedEmoji,
                            pickupPreference = pickupPreference,
                            status = "Available"
                        )
                        onListingPublished(newListing)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("publish_waste_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Publish, contentDescription = "Publish")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PUBLISH WASTE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    // PREVIEW DIALOG
    if (showPreviewModal) {
        Dialog(onDismissRequest = { showPreviewModal = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Listing Preview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                        IconButton(onClick = { showPreviewModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WasteLightGreen),
                        border = BorderStroke(1.dp, WasteSecondary)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(selectedEmoji, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(customWasteName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                                    Text("Type: ${selectedWasteType.name} • ${selectedWasteType.category}", fontSize = 12.sp, color = WasteTextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("📦 Available: $quantity $unit • 💰 ₹$price $priceUnit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                            Text("📍 Location: $village, $taluka, $district", fontSize = 12.sp, color = WasteTextSecondary)
                            Text("📅 Available: $availableDate", fontSize = 12.sp, color = WasteTextSecondary)
                            Text("🚚 Pickup: $pickupPreference", fontSize = 12.sp, color = WasteTextSecondary)
                        }
                    }

                    Text("Description: $description", fontSize = 12.sp, color = WasteTextSecondary)

                    Button(
                        onClick = {
                            showPreviewModal = false
                            val parsedQty = quantity.toDoubleOrNull() ?: 100.0
                            val parsedPrice = price.toDoubleOrNull() ?: 4.0
                            val newListing = AgriWasteItem(
                                id = "AW-LST-" + (100..999).random(),
                                farmerId = "f_ramesh",
                                farmerName = "Ramesh Patil",
                                farmerPhone = "+91 98220 14589",
                                wasteType = selectedWasteType.name,
                                wasteName = customWasteName.ifBlank { selectedWasteType.name },
                                category = selectedWasteType.category,
                                quantity = parsedQty,
                                initialQuantity = parsedQty,
                                unit = unit,
                                price = parsedPrice,
                                priceUnit = priceUnit,
                                availableDate = availableDate,
                                village = village,
                                taluka = taluka,
                                district = district,
                                description = description,
                                imageEmoji = selectedEmoji,
                                pickupPreference = pickupPreference,
                                status = "Available"
                            )
                            onListingPublished(newListing)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CONFIRM & PUBLISH NOW", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ------------------ 3. FARMER: MY WASTE LISTINGS VIEW ------------------

@Composable
fun FarmerMyListingsView(
    listings: List<AgriWasteItem>,
    onAddNew: () -> Unit,
    onView: (AgriWasteItem) -> Unit,
    onEdit: (AgriWasteItem) -> Unit,
    onDelete: (AgriWasteItem) -> Unit
) {
    var filterStatus by remember { mutableStateOf("All") } // "All", "Active", "Sold Out", "Cancelled"
    val filterTabs = listOf("All", "Active", "Sold Out", "Cancelled")

    val filteredList = when (filterStatus) {
        "Active" -> listings.filter { it.status == "Available" }
        "Sold Out" -> listings.filter { it.status == "Sold Out" }
        "Cancelled" -> listings.filter { it.status == "Cancelled" }
        else -> listings
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("📋 My Waste Listings", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                    Text("Manage active listings & sold inventory", fontSize = 12.sp, color = WasteTextSecondary)
                }
                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("List Waste", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Filter chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filterTabs.forEach { tab ->
                    FilterChip(
                        selected = filterStatus == tab,
                        onClick = { filterStatus = tab },
                        label = { Text(tab, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WastePrimary, selectedLabelColor = Color.White)
                    )
                }
            }
        }

        if (filteredList.isEmpty()) {
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🌾", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No listings in this tab", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                        Text("Publish your crop residues to start receiving buyer purchase requests.", fontSize = 12.sp, color = WasteTextSecondary, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onAddNew,
                            colors = ButtonDefaults.buttonColors(containerColor = WastePrimary)
                        ) {
                            Text("Publish New Waste")
                        }
                    }
                }
            }
        } else {
            items(filteredList) { item ->
                FarmerWasteListingCard(
                    listing = item,
                    onView = { onView(item) },
                    onEdit = { onEdit(item) },
                    onDelete = { onDelete(item) }
                )
            }
        }
    }
}

@Composable
fun FarmerWasteListingCard(
    listing: AgriWasteItem,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isSoldOut = listing.status == "Sold Out" || listing.quantity <= 0.0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isSoldOut) Color(0xFFCBD5E1) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(listing.imageEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(listing.wasteName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                        Text("Type: ${listing.wasteType} • ${listing.category}", fontSize = 11.sp, color = WasteTextSecondary)
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSoldOut) Color(0xFFFFEBEE) else WasteLightGreen
                ) {
                    Text(
                        text = if (isSoldOut) "SOLD OUT" else "AVAILABLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSoldOut) Color.Red else WastePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Available Quantity", fontSize = 10.sp, color = WasteTextSecondary)
                    Text("${listing.quantity} ${listing.unit}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                }
                Column {
                    Text("Unit Price", fontSize = 10.sp, color = WasteTextSecondary)
                    Text("₹${listing.price}/${listing.unit}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                }
                Column {
                    Text("Location", fontSize = 10.sp, color = WasteTextSecondary)
                    Text("📍 ${listing.village}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WasteTextPrimary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📅 ${listing.availableDate}", fontSize = 11.sp, color = WasteTextSecondary)
                Text("🚚 ${listing.pickupPreference}", fontSize = 11.sp, color = WasteTextSecondary)
            }

            // ACTIONS: VIEW, EDIT, DELETE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "View", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ------------------ 4. FARMER: BUYER REQUESTS & ORDERS VIEW ------------------

@Composable
fun FarmerBuyerRequestsView(
    orders: List<AgriWasteOrder>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onUpdateStatus: (String, String) -> Unit,
    onViewOrder: (AgriWasteOrder) -> Unit
) {
    var orderTab by remember { mutableStateOf("Pending") } // "Pending", "Active/Confirmed", "Completed"
    val tabs = listOf("Pending", "Active/Confirmed", "Completed")

    val filteredOrders = when (orderTab) {
        "Pending" -> orders.filter { it.status == "Waiting for Farmer" || it.status == "Order Placed" }
        "Active/Confirmed" -> orders.filter { it.status in listOf("Accepted", "Pickup Scheduled", "Picked Up", "Out for Delivery") }
        "Completed" -> orders.filter { it.status in listOf("Delivered", "Completed", "Rejected") }
        else -> orders
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text("💬 Buyer Purchase Requests & Orders", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
            Text("Review buyer purchase requests and manage order fulfillment", fontSize = 12.sp, color = WasteTextSecondary)
        }

        // Tabs
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tabs.forEach { tb ->
                    FilterChip(
                        selected = orderTab == tb,
                        onClick = { orderTab = tb },
                        label = { Text(tb, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WastePrimary, selectedLabelColor = Color.White)
                    )
                }
            }
        }

        if (filteredOrders.isEmpty()) {
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📭", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No orders in this category", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                        Text("Incoming purchase requests from biomass plants and dairy units will appear here.", fontSize = 12.sp, color = WasteTextSecondary, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(filteredOrders) { order ->
                FarmerBuyerRequestCard(
                    order = order,
                    onAccept = { onAccept(order.id) },
                    onReject = { onReject(order.id) },
                    onUpdateStatus = { newSt -> onUpdateStatus(order.id, newSt) },
                    onViewOrder = { onViewOrder(order) }
                )
            }
        }
    }
}

@Composable
fun FarmerBuyerRequestCard(
    order: AgriWasteOrder,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onViewOrder: () -> Unit
) {
    val isPending = order.status == "Waiting for Farmer" || order.status == "Order Placed"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isPending) WasteAccent else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("♻️ Waste Purchase Request", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                    Text("Order #${order.id} • ${order.orderDate}", fontSize = 10.sp, color = WasteTextSecondary)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (order.status) {
                        "Waiting for Farmer", "Order Placed" -> WasteAmberLight
                        "Accepted", "Pickup Scheduled" -> WasteLightGreen
                        "Delivered", "Completed" -> Color(0xFFE3F2FD)
                        "Rejected" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = order.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            "Waiting for Farmer", "Order Placed" -> Color(0xFFE65100)
                            "Accepted", "Pickup Scheduled" -> WastePrimary
                            "Delivered", "Completed" -> Color(0xFF1565C0)
                            "Rejected" -> Color.Red
                            else -> WasteTextPrimary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Details
            DetailRow(label = "Buyer", value = "${order.buyerName} (${order.buyerType})")
            DetailRow(label = "Waste", value = "${order.wasteName} (${order.wasteType})")
            DetailRow(label = "Requested Quantity", value = "${order.quantity} ${order.unit}")
            DetailRow(label = "Agreed Price", value = "₹${order.agreedPrice} ${order.priceUnit} (Total: ₹${order.totalAmount})")
            DetailRow(label = "Pickup / Logistics", value = "${order.pickupMethod} • Target: ${order.pickupDate}")

            if (order.pickupMethod == "Delivery Partner" && order.deliveryPartnerName != null) {
                DetailRow(label = "Delivery Partner", value = "${order.deliveryPartnerName} (${order.deliveryPartnerPhone ?: "Assigned"})")
            }

            if (order.notes.isNotBlank()) {
                Text("Notes: ${order.notes}", fontSize = 11.sp, color = WasteTextSecondary)
            }

            // ACTION BUTTONS
            if (isPending) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("REJECT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("ACCEPT REQUEST", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else if (order.status == "Accepted" || order.status == "Pickup Scheduled") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onUpdateStatus("Picked Up") },
                        colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Mark Picked Up / Handed Over", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (order.status == "Picked Up" || order.status == "Out for Delivery") {
                Button(
                    onClick = { onUpdateStatus("Completed") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirm Order Completed ✅", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==============================================================================
// 5. AGRI WASTE MARKETPLACE – BUYER ONLY
// ==============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriWasteMarketplaceScreen(
    navController: NavController? = null,
    initialMode: String = "buyer",
    onBack: () -> Unit = { navController?.popBackStack() }
) {
    val context = LocalContext.current

    // Buyer Navigation Tabs: 0: Browse Marketplace, 1: My Purchases & Tracking
    var buyerTab by remember { mutableStateOf(0) }

    // Search and Category Filter
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedWasteTypeFilter by remember { mutableStateOf("All") }
    var maxPriceFilter by remember { mutableStateOf(5000f) }

    // Dialog & Flow States
    var selectedListingForDetails by remember { mutableStateOf<AgriWasteItem?>(null) }
    var selectedListingForBuy by remember { mutableStateOf<AgriWasteItem?>(null) }
    var selectedOrderForTracking by remember { mutableStateOf<AgriWasteOrder?>(null) }

    val categories = listOf("All", "Straw", "Crop Stalks", "Sugarcane Residue", "Husk / Shell", "Other Crop Residue")

    // Filtered listings for Buyer
    val availableListings = AgriWasteDataHub.listings.filter { it.status == "Available" && it.quantity > 0.0 }
    val filteredListings = availableListings.filter { item ->
        val matchesSearch = searchQuery.isBlank() ||
                item.wasteName.contains(searchQuery, ignoreCase = true) ||
                item.wasteType.contains(searchQuery, ignoreCase = true) ||
                item.village.contains(searchQuery, ignoreCase = true) ||
                item.taluka.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
        val matchesType = selectedWasteTypeFilter == "All" || item.wasteType.equals(selectedWasteTypeFilter, ignoreCase = true)

        matchesSearch && matchesCategory && matchesType
    }

    // Buyer's purchases
    val buyerOrders = AgriWasteDataHub.orders.filter { it.buyerId == "b_buyer" || it.buyerName.contains("Bio") || it.buyerName.contains("Dairy") || it.buyerName.contains("Mushroom") }

    Scaffold(
        containerColor = WasteBackground,
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(WasteLightGreen)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WastePrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "♻️ Agri Waste Marketplace",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WastePrimary
                                )
                                Text(
                                    "Source Agricultural Biomass & Residue (Buyer Portal)",
                                    fontSize = 11.sp,
                                    color = WasteTextSecondary
                                )
                            }
                        }

                        // Order count badge
                        if (buyerOrders.isNotEmpty()) {
                            Badge(
                                containerColor = WasteLightGreen,
                                contentColor = WastePrimary,
                                modifier = Modifier.clickable { buyerTab = 1 }
                            ) {
                                Text(
                                    "Orders (${buyerOrders.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Buyer Tabs: 0: Browse Marketplace, 1: My Purchases & Tracking
                    TabRow(
                        selectedTabIndex = buyerTab,
                        containerColor = Color.White,
                        contentColor = WastePrimary
                    ) {
                        Tab(
                            selected = buyerTab == 0,
                            onClick = { buyerTab = 0 },
                            text = { Text("🔍 Browse Waste (${availableListings.size})", fontSize = 12.sp, fontWeight = if (buyerTab == 0) FontWeight.Bold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = buyerTab == 1,
                            onClick = { buyerTab = 1 },
                            text = { Text("📦 My Purchases & Tracking (${buyerOrders.size})", fontSize = 12.sp, fontWeight = if (buyerTab == 1) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (buyerTab) {
                0 -> BuyerMarketplaceHomeView(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it },
                    listings = filteredListings,
                    onViewDetails = { selectedListingForDetails = it },
                    onBuyWaste = { selectedListingForBuy = it }
                )
                1 -> BuyerPurchasesTrackingView(
                    orders = buyerOrders,
                    onViewTracking = { selectedOrderForTracking = it }
                )
            }
        }
    }

    // Modal: Waste Details (Buyer)
    selectedListingForDetails?.let { listing ->
        BuyerWasteDetailsDialog(
            listing = listing,
            onDismiss = { selectedListingForDetails = null },
            onBuy = {
                selectedListingForDetails = null
                selectedListingForBuy = listing
            }
        )
    }

    // Modal: Buy Waste Form (Buyer)
    selectedListingForBuy?.let { listing ->
        BuyWasteDialog(
            listing = listing,
            onDismiss = { selectedListingForBuy = null },
            onOrderPlaced = { newOrder ->
                AgriWasteDataHub.placeOrder(newOrder)
                selectedListingForBuy = null
                buyerTab = 1
                Toast.makeText(context, "Purchase request sent to farmer! Track order in My Purchases.", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Modal: Order Tracking Detail (Buyer)
    selectedOrderForTracking?.let { order ->
        BuyerOrderTrackingDialog(
            order = order,
            onDismiss = { selectedOrderForTracking = null }
        )
    }
}

// ------------------ 6 & 7. BUYER: MARKETPLACE HOME, SEARCH & CATEGORIES ------------------

@Composable
fun BuyerMarketplaceHomeView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    listings: List<AgriWasteItem>,
    onViewDetails: (AgriWasteItem) -> Unit,
    onBuyWaste: (AgriWasteItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // SEARCH INPUT BAR
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search straw, sugarcane trash, maize stalks, husk...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = WastePrimary) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_agri_waste_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = WastePrimary
                )
            )
        }

        // CATEGORIES (🌾 Straw, 🌱 Crop Stalks, 🎋 Sugarcane Residue, 🥥 Husk / Shell, ♻️ Other Crop Residue)
        item {
            Text("Categories", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    val emoji = when (cat) {
                        "Straw" -> "🌾"
                        "Crop Stalks" -> "🌱"
                        "Sugarcane Residue" -> "🎋"
                        "Husk / Shell" -> "🥥"
                        "Other Crop Residue" -> "♻️"
                        else -> "🌿"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(cat) },
                        leadingIcon = { Text(emoji, fontSize = 14.sp) },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WastePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White
                        )
                    )
                }
            }
        }

        // SECTION 1: NEARBY WASTE (< 10 KM)
        val nearbyList = listings.filter { it.distanceKm <= 10.0 }
        if (nearbyList.isNotEmpty() && searchQuery.isBlank() && selectedCategory == "All") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍 Nearby Waste Listings", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("(Within 10 km)", fontSize = 11.sp, color = WasteTextSecondary)
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(nearbyList) { item ->
                        BuyerNearbyWasteCard(
                            listing = item,
                            onView = { onViewDetails(item) },
                            onBuy = { onBuyWaste(item) }
                        )
                    }
                }
            }
        }

        // SECTION 2: ALL AVAILABLE WASTE LISTINGS / RESULTS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (searchQuery.isNotBlank()) "Search Results (${listings.size})" else "All Available Waste (${listings.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = WasteTextPrimary
                )
                Text(
                    "Direct from Verified Farmers",
                    fontSize = 11.sp,
                    color = WastePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (listings.isEmpty()) {
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
                        Text("🔍", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No waste listings match your search", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                        Text("Try searching for wheat straw, sugarcane trash, or selecting 'All' category.", fontSize = 12.sp, color = WasteTextSecondary, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(listings) { item ->
                BuyerWasteListingRowCard(
                    listing = item,
                    onView = { onViewDetails(item) },
                    onBuy = { onBuyWaste(item) }
                )
            }
        }
    }
}

@Composable
fun BuyerNearbyWasteCard(
    listing: AgriWasteItem,
    onView: () -> Unit,
    onBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .width(240.dp)
            .clickable { onView() }
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(listing.imageEmoji, fontSize = 28.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WasteLightGreen)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("${listing.distanceKm} km away", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                }
            }

            Text(listing.wasteName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("📍 ${listing.village}, ${listing.taluka}", fontSize = 11.sp, color = WasteTextSecondary)
            Text("📦 ${listing.quantity} ${listing.unit} • ₹${listing.price}/${listing.unit}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WastePrimary)

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    Text("VIEW", fontSize = 11.sp)
                }
                Button(
                    onClick = onBuy,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    Text("BUY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BuyerWasteListingRowCard(
    listing: AgriWasteItem,
    onView: () -> Unit,
    onBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView() }
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(listing.imageEmoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(listing.wasteName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                        Text("Category: ${listing.category} • Type: ${listing.wasteType}", fontSize = 11.sp, color = WasteTextSecondary)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WasteLightGreen)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("📍 ${listing.distanceKm} km", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                }
            }

            Text(listing.description, fontSize = 12.sp, color = WasteTextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)

            HorizontalDivider(color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Available Quantity", fontSize = 10.sp, color = WasteTextSecondary)
                    Text("${listing.quantity} ${listing.unit}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                }

                Column {
                    Text("Unit Price", fontSize = 10.sp, color = WasteTextSecondary)
                    Text("₹${listing.price}/${listing.unit}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Farmer", fontSize = 10.sp, color = WasteTextSecondary)
                    Text("👨‍🌾 ${listing.farmerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WasteTextPrimary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🚚 ${listing.pickupPreference}", fontSize = 11.sp, color = WasteTextSecondary)
                Text("📅 ${listing.availableDate}", fontSize = 11.sp, color = WasteTextSecondary)
            }

            // ACTIONS: VIEW & BUY
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, WastePrimary)
                ) {
                    Text("VIEW DETAILS", fontSize = 12.sp, color = WastePrimary, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onBuy,
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Buy", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("BUY WASTE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ------------------ 8. BUYER: WASTE DETAILS DIALOG ------------------

@Composable
fun BuyerWasteDetailsDialog(
    listing: AgriWasteItem,
    onDismiss: () -> Unit,
    onBuy: () -> Unit
) {
    val meta = AGRI_WASTE_TYPES.firstOrNull { it.name == listing.wasteType }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(listing.imageEmoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(listing.wasteName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                            Text("Category: ${listing.category}", fontSize = 11.sp, color = WasteTextSecondary)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Price & Quantity Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WasteLightGreen),
                    border = BorderStroke(1.dp, WasteSecondary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Available Quantity", fontSize = 11.sp, color = WasteTextSecondary)
                            Text("${listing.quantity} ${listing.unit}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Price Rate", fontSize = 11.sp, color = WasteTextSecondary)
                            Text("₹${listing.price} ${listing.priceUnit}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                        }
                    }
                }

                DetailRow(label = "Farmer / Seller", value = "👨‍🌾 ${listing.farmerName} (${listing.farmerPhone})")
                DetailRow(label = "Location", value = "📍 ${listing.village}, ${listing.taluka}, ${listing.district} (~${listing.distanceKm} km)")
                DetailRow(label = "Available Date", value = "📅 ${listing.availableDate}")
                DetailRow(label = "Pickup Options", value = "🚚 ${listing.pickupPreference}")

                Text("Description:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WasteTextSecondary)
                Text(listing.description, fontSize = 12.sp, color = WasteTextPrimary)

                if (meta != null && meta.typicalUses.isNotEmpty()) {
                    Text("💡 Recommended Industrial & Farm Uses:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        meta.typicalUses.forEach { use ->
                            Text("• $use", fontSize = 11.sp, color = WasteTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onBuy,
                    colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Buy")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("BUY WASTE NOW", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ------------------ 9. BUYER: BUY WASTE MODAL & QUANTITY SELECTOR ------------------

@Composable
fun BuyWasteDialog(
    listing: AgriWasteItem,
    onDismiss: () -> Unit,
    onOrderPlaced: (AgriWasteOrder) -> Unit
) {
    var quantityRequired by remember { mutableStateOf((listing.quantity.coerceAtMost(200.0)).toString()) }
    var pickupMethod by remember { mutableStateOf("Buyer Pickup") } // "Buyer Pickup" vs "Delivery Partner"
    var buyerName by remember { mutableStateOf("Sahyadri Bio-Pellets Ltd.") }
    var buyerPhone by remember { mutableStateOf("+91 98221 66554") }
    var buyerType by remember { mutableStateOf("Biomass Plant") }
    var deliveryAddress by remember { mutableStateOf("Chakan MIDC Phase 2, Pune, MH - 410501") }
    var notes by remember { mutableStateOf("We require clean dry loading on agreed date.") }

    val parsedQty = quantityRequired.toDoubleOrNull() ?: 0.0
    val totalEstimatedPrice = parsedQty * listing.price

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Buy Agricultural Waste", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                        Text(listing.wasteName, fontSize = 12.sp, color = WasteTextSecondary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Available info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Available: ${listing.quantity} ${listing.unit}", fontSize = 12.sp, color = WasteTextSecondary)
                    Text("Price: ₹${listing.price}/${listing.unit}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                }

                // Quantity Input
                OutlinedTextField(
                    value = quantityRequired,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.toDoubleOrNull() != null) {
                            quantityRequired = input
                        }
                    },
                    label = { Text("Quantity Required (${listing.unit}) *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WastePrimary)
                )

                // Real-time Estimated Total
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WasteAmberLight),
                    border = BorderStroke(1.dp, WasteAccent.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Estimated Total Price", fontSize = 11.sp, color = Color(0xFFE65100))
                            Text("$parsedQty ${listing.unit} × ₹${listing.price}/${listing.unit}", fontSize = 11.sp, color = WasteTextSecondary)
                        }
                        Text("₹${String.format("%.2f", totalEstimatedPrice)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }
                }

                // Pickup or Delivery Partner Selection
                Text("Select Pickup / Transport Option *", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = pickupMethod == "Buyer Pickup",
                        onClick = { pickupMethod = "Buyer Pickup" },
                        leadingIcon = { Text("🚜") },
                        label = { Text("Buyer Pickup", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WastePrimary, selectedLabelColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = pickupMethod == "Delivery Partner",
                        onClick = { pickupMethod = "Delivery Partner" },
                        leadingIcon = { Text("🚚") },
                        label = { Text("Delivery Partner", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WastePrimary, selectedLabelColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (pickupMethod == "Delivery Partner") {
                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        label = { Text("Delivery Destination Address *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                    ) {
                        Text(
                            "Self-pickup from farmer's location: ${listing.village}, ${listing.taluka}, ${listing.district}.",
                            fontSize = 11.sp,
                            color = WasteTextSecondary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Instructions for Farmer") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // PLACE ORDER BUTTON
                Button(
                    onClick = {
                        val validQty = if (parsedQty > 0) parsedQty.coerceAtMost(listing.quantity) else 100.0
                        val newOrder = AgriWasteOrder(
                            id = "WO-" + (1000..9999).random(),
                            wasteId = listing.id,
                            farmerId = listing.farmerId,
                            farmerName = listing.farmerName,
                            farmerPhone = listing.farmerPhone,
                            buyerId = "b_buyer",
                            buyerName = buyerName,
                            buyerPhone = buyerPhone,
                            buyerType = buyerType,
                            wasteName = listing.wasteName,
                            wasteType = listing.wasteType,
                            quantity = validQty,
                            unit = listing.unit,
                            agreedPrice = listing.price,
                            priceUnit = listing.priceUnit,
                            totalAmount = validQty * listing.price,
                            pickupMethod = pickupMethod,
                            deliveryAddress = if (pickupMethod == "Delivery Partner") deliveryAddress else "Self pickup at ${listing.village}",
                            village = listing.village,
                            taluka = listing.taluka,
                            district = listing.district,
                            deliveryPartnerName = if (pickupMethod == "Delivery Partner") "Kisan Express Transport" else null,
                            deliveryPartnerPhone = if (pickupMethod == "Delivery Partner") "+91 98900 33445" else null,
                            status = "Waiting for Farmer",
                            orderDate = "Today",
                            pickupDate = listing.availableDate,
                            notes = notes
                        )
                        onOrderPlaced(newOrder)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("place_waste_order_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Place Order")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PLACE ORDER (₹${String.format("%.0f", totalEstimatedPrice)})", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ------------------ 10 & 11. BUYER: MY PURCHASES & ORDER TRACKING ------------------

@Composable
fun BuyerPurchasesTrackingView(
    orders: List<AgriWasteOrder>,
    onViewTracking: (AgriWasteOrder) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text("📦 My Waste Purchases & Deliveries", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
            Text("Track purchase requests, pickup schedules, and delivery progress", fontSize = 12.sp, color = WasteTextSecondary)
        }

        if (orders.isEmpty()) {
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
                        Text("🛒", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No waste purchases yet", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                        Text("Browse the Agri Waste Marketplace to source straw, stalks, or husks directly from farmers.", fontSize = 12.sp, color = WasteTextSecondary, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(orders) { order ->
                BuyerOrderCard(
                    order = order,
                    onViewTracking = { onViewTracking(order) }
                )
            }
        }
    }
}

@Composable
fun BuyerOrderCard(
    order: AgriWasteOrder,
    onViewTracking: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewTracking() }
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.wasteName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                    Text("Order #${order.id} • ${order.orderDate}", fontSize = 10.sp, color = WasteTextSecondary)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (order.status) {
                        "Waiting for Farmer", "Order Placed" -> WasteAmberLight
                        "Accepted", "Pickup Scheduled" -> WasteLightGreen
                        "Delivered", "Completed" -> Color(0xFFE3F2FD)
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = order.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            "Waiting for Farmer", "Order Placed" -> Color(0xFFE65100)
                            "Accepted", "Pickup Scheduled" -> WastePrimary
                            "Delivered", "Completed" -> Color(0xFF1565C0)
                            else -> WasteTextPrimary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Quantity", fontSize = 10.sp, color = WasteTextSecondary)
                    Text("${order.quantity} ${order.unit}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)
                }
                Column {
                    Text("Total Price", fontSize = 10.sp, color = WasteTextSecondary)
                    Text("₹${order.totalAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Farmer", fontSize = 10.sp, color = WasteTextSecondary)
                    Text("👨‍🌾 ${order.farmerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WasteTextPrimary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🚚 ${order.pickupMethod}", fontSize = 11.sp, color = WasteTextSecondary)
                Text("Target: ${order.pickupDate}", fontSize = 11.sp, color = WasteTextSecondary)
            }

            Button(
                onClick = onViewTracking,
                colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Timeline, contentDescription = "Track", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Track Order Progress", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ------------------ 11. ORDER STATUS PROGRESS DIALOG ------------------

@Composable
fun BuyerOrderTrackingDialog(
    order: AgriWasteOrder,
    onDismiss: () -> Unit
) {
    val steps = if (order.pickupMethod == "Buyer Pickup") {
        listOf("Order Placed", "Waiting for Farmer", "Accepted", "Pickup Scheduled", "Picked Up by Buyer", "Completed")
    } else {
        listOf("Order Placed", "Waiting for Farmer", "Accepted", "Pickup Scheduled", "Picked Up", "Out for Delivery", "Delivered", "Completed")
    }

    val currentStatusIdx = steps.indexOfFirst {
        it.equals(order.status, ignoreCase = true) ||
                (order.status == "Picked Up" && it.contains("Picked Up")) ||
                (order.status == "Completed" && it == "Completed")
    }.let { if (it == -1) 1 else it }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Order Lifecycle Tracking", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WastePrimary)
                        Text("Order #${order.id}", fontSize = 11.sp, color = WasteTextSecondary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Summary
                DetailRow(label = "Waste Item", value = order.wasteName)
                DetailRow(label = "Quantity & Total", value = "${order.quantity} ${order.unit} (₹${order.totalAmount})")
                DetailRow(label = "Farmer", value = "${order.farmerName} (${order.farmerPhone})")
                DetailRow(label = "Pickup Method", value = order.pickupMethod)
                DetailRow(label = "Delivery Address", value = order.deliveryAddress)

                if (order.deliveryPartnerName != null) {
                    DetailRow(label = "Assigned Delivery Partner", value = "${order.deliveryPartnerName} (${order.deliveryPartnerPhone})")
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Order Status Timeline:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WasteTextPrimary)

                // Timeline Stepper
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    steps.forEachIndexed { index, stepName ->
                        val isDone = index <= currentStatusIdx
                        val isCurrent = index == currentStatusIdx

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) WastePrimary else Color(0xFFE2E8F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(14.dp))
                                } else {
                                    Text("${index + 1}", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stepName,
                                fontSize = 12.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) WastePrimary else if (isDone) WasteTextPrimary else Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = WastePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Tracking")
                }
            }
        }
    }
}

// ------------------ REUSABLE HELPER UI ------------------

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = WasteTextSecondary)
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = WasteTextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(max = 200.dp)
        )
    }
}
