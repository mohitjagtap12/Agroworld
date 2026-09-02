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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ------------------ COMPANY COLOR PALETTE ------------------
val CompanyPrimary = Color(0xFF2E7D32)       // Forest Green
val CompanySecondary = Color(0xFF66BB6A)     // Light Green
val CompanyAccent = Color(0xFFF9A825)        // Amber/Gold
val CompanyBackground = Color(0xFFF8FBF7)    // Soft agricultural background
val CompanyCardBg = Color(0xFFFFFFFF)        // Pure White Cards
val CompanyTextPrimary = Color(0xFF212121)   // Dark Charcoal
val CompanyTextSecondary = Color(0xFF616161) // Medium Grey
val CompanyLightBg = Color(0xFFE8F5E9)       // Mint tint

// ------------------ DATA MODELS ------------------
data class PublishedContract(
    val id: String,
    val companyName: String,
    val cropName: String,
    val variety: String,
    val requiredQuantityTons: Double,
    val offeredPricePerTon: Double,
    val harvestPeriod: String,
    val targetLocation: String,
    val qualityRequirements: String,
    val termsAndConditions: String,
    val advancePaymentPercent: Int,
    var status: String, // "Active", "Applications Closed", "Harvesting", "Completed"
    val datePublished: String
)

data class FarmerContractApplication(
    val id: String,
    val contractId: String,
    val cropName: String,
    val farmerName: String,
    val farmerPhone: String,
    val village: String,
    val committedAcreage: Double,
    val projectedYieldTons: Double,
    val appliedDate: String,
    var applicationStatus: String // "Pending Review", "Approved", "Rejected"
)

// ------------------ MASTER COMPANY PORTAL ------------------
@Composable
fun CompanyPortalScreen(navController: NavController) {
    val context = LocalContext.current

    // Navigation Sub-screens: "contracts", "publish", "applications", "profile"
    var currentSubScreen by remember { mutableStateOf("contracts") }

    // Company Profile
    var companyName by remember { mutableStateOf(SessionManager.getInstance(context).userName.ifEmpty { "Agri-Enterprise Co." }) }
    var businessType by remember { mutableStateOf("Agri-Export & Food Processing") }
    var contactPerson by remember { mutableStateOf(SessionManager.getInstance(context).userName.ifEmpty { "Procurement Lead" }) }
    var phone by remember { mutableStateOf(SessionManager.getInstance(context).userPhone.ifEmpty { "+91 ----------" }) }
    var location by remember { mutableStateOf(SessionManager.getInstance(context).userDistrict.ifEmpty { "Agro Cluster" }) }
    var fssaiNumber by remember { mutableStateOf("FSSAI Registered") }

    // Contracts in Memory
    val contractsList = remember { mutableStateListOf<PublishedContract>() }

    // Farmer Applications in Memory
    val farmerApplications = remember { mutableStateListOf<FarmerContractApplication>() }

    Scaffold(
        topBar = {
            Surface(
                color = CompanyCardBg,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    navController.navigate("role_selection") {
                                        popUpTo("role_selection") { inclusive = true }
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CompanyPrimary)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "AgroWorld Contract Portal",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CompanyPrimary
                                )
                                Text(
                                    text = "🏢 $companyName",
                                    fontSize = 12.sp,
                                    color = CompanyTextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Verified Buyer", color = CompanyPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        bottomBar = {
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
                    selected = currentSubScreen == "contracts",
                    onClick = { currentSubScreen = "contracts" },
                    icon = { Icon(Icons.Default.Handshake, contentDescription = "Contracts") },
                    label = { Text("Contracts", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CompanyPrimary,
                        indicatorColor = CompanyPrimary,
                        unselectedIconColor = CompanyTextSecondary,
                        unselectedTextColor = CompanyTextSecondary
                    )
                )

                NavigationBarItem(
                    selected = currentSubScreen == "publish",
                    onClick = { currentSubScreen = "publish" },
                    icon = { Icon(Icons.Default.AddCircleOutline, contentDescription = "Publish") },
                    label = { Text("Publish Contract", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CompanyPrimary,
                        indicatorColor = CompanyPrimary,
                        unselectedIconColor = CompanyTextSecondary,
                        unselectedTextColor = CompanyTextSecondary
                    )
                )

                NavigationBarItem(
                    selected = currentSubScreen == "applications",
                    onClick = { currentSubScreen = "applications" },
                    icon = {
                        val pendingCount = farmerApplications.count { it.applicationStatus == "Pending Review" }
                        BadgedBox(badge = {
                            if (pendingCount > 0) {
                                Badge(containerColor = CompanyAccent) {
                                    Text(pendingCount.toString(), color = CompanyTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }) {
                            Icon(Icons.Default.AssignmentInd, contentDescription = "Applications")
                        }
                    },
                    label = { Text("Farmer Applications", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CompanyPrimary,
                        indicatorColor = CompanyPrimary,
                        unselectedIconColor = CompanyTextSecondary,
                        unselectedTextColor = CompanyTextSecondary
                    )
                )

                NavigationBarItem(
                    selected = currentSubScreen == "profile",
                    onClick = { currentSubScreen = "profile" },
                    icon = { Icon(Icons.Default.Business, contentDescription = "Company Profile") },
                    label = { Text("Company Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CompanyPrimary,
                        indicatorColor = CompanyPrimary,
                        unselectedIconColor = CompanyTextSecondary,
                        unselectedTextColor = CompanyTextSecondary
                    )
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CompanyBackground)
                .padding(paddingValues)
        ) {
            when (currentSubScreen) {
                "contracts" -> CompanyContractsListView(
                    contracts = contractsList,
                    onPublishNewClick = { currentSubScreen = "publish" }
                )
                "publish" -> CompanyPublishContractFormView(
                    onContractPublished = { newContract ->
                        contractsList.add(0, newContract)
                        currentSubScreen = "contracts"
                        Toast.makeText(context, "Farming Contract published to AgroWorld farmers! 🤝", Toast.LENGTH_SHORT).show()
                    }
                )
                "applications" -> CompanyApplicationsView(
                    applications = farmerApplications,
                    onApprove = { app ->
                        app.applicationStatus = "Approved"
                        Toast.makeText(context, "Application for ${app.farmerName} approved! Contract established.", Toast.LENGTH_SHORT).show()
                    },
                    onReject = { app ->
                        app.applicationStatus = "Rejected"
                        Toast.makeText(context, "Application declined", Toast.LENGTH_SHORT).show()
                    }
                )
                "profile" -> CompanyProfileView(
                    companyName = companyName,
                    businessType = businessType,
                    contactPerson = contactPerson,
                    phone = phone,
                    location = location,
                    fssaiNumber = fssaiNumber,
                    totalContractsCount = contractsList.size,
                    totalApplicationsCount = farmerApplications.size
                )
            }
        }
    }
}

// ------------------ 1. CONTRACTS LIST VIEW ------------------
@Composable
fun CompanyContractsListView(
    contracts: List<PublishedContract>,
    onPublishNewClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Published Buyback Contracts", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CompanyTextPrimary)
                    Text("Active contract farming programs", fontSize = 12.sp, color = CompanyTextSecondary)
                }

                Button(
                    onClick = onPublishNewClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CompanyPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publish", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }
            }
        }

        items(contracts) { contract ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CompanyCardBg),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🌾 ${contract.cropName}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = CompanyTextPrimary)
                            Text("Variety: ${contract.variety}", fontSize = 12.sp, color = CompanyPrimary, fontWeight = FontWeight.SemiBold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(contract.status, color = CompanyPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = Color(0xFFF1F5F9))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Required Quantity:", fontSize = 11.sp, color = CompanyTextSecondary)
                            Text("${contract.requiredQuantityTons} Tons", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CompanyTextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Offered Buyback Price:", fontSize = 11.sp, color = CompanyTextSecondary)
                            Text("₹${contract.offeredPricePerTon.toInt()} / Ton", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CompanyPrimary)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Target Cluster:", fontSize = 11.sp, color = CompanyTextSecondary)
                            Text(contract.targetLocation, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = CompanyTextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Harvest Window:", fontSize = 11.sp, color = CompanyTextSecondary)
                            Text(contract.harvestPeriod, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = CompanyTextPrimary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🔍 Quality Specs: ${contract.qualityRequirements}", fontSize = 11.sp, color = CompanyTextSecondary)
                            Text("📜 Terms: ${contract.termsAndConditions} (${contract.advancePaymentPercent}% Advance)", fontSize = 11.sp, color = CompanyPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 2. PUBLISH CONTRACT VIEW ------------------
@Composable
fun CompanyPublishContractFormView(
    onContractPublished: (PublishedContract) -> Unit
) {
    val context = LocalContext.current

    var cropName by remember { mutableStateOf("Pune Red Onions") }
    var variety by remember { mutableStateOf("N-53 Grade A") }
    var quantityTons by remember { mutableStateOf("50") }
    var pricePerTon by remember { mutableStateOf("21500") }
    var harvestDate by remember { mutableStateOf("October 2026") }
    var location by remember { mutableStateOf("Junnar & Ambegaon Clusters") }
    var qualitySpecs by remember { mutableStateOf("Uniform bulb size 45-60mm, fully dried outer scales, zero rot.") }
    var contractTerms by remember { mutableStateOf("100% buyback guarantee with weighing at farm gate. 25% advance upon seedling planting.") }
    var advancePercent by remember { mutableStateOf("25") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Handshake, contentDescription = "Contract", tint = CompanyPrimary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Publish Buyback Farming Contract", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CompanyPrimary)
                        Text("Reach thousands of verified farmers in Maharashtra", fontSize = 12.sp, color = CompanyTextSecondary)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CompanyCardBg),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = cropName,
                        onValueChange = { cropName = it },
                        label = { Text("Crop Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = variety,
                        onValueChange = { variety = it },
                        label = { Text("Seed / Crop Variety") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = quantityTons,
                            onValueChange = { quantityTons = it },
                            label = { Text("Required Qty (Tons)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pricePerTon,
                            onValueChange = { pricePerTon = it },
                            label = { Text("Offered Price (₹/Ton)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = harvestDate,
                            onValueChange = { harvestDate = it },
                            label = { Text("Expected Harvest Date") },
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = advancePercent,
                            onValueChange = { advancePercent = it },
                            label = { Text("Advance %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Target Location / Cluster") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = qualitySpecs,
                        onValueChange = { qualitySpecs = it },
                        label = { Text("Quality Standards & Specifications") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = contractTerms,
                        onValueChange = { contractTerms = it },
                        label = { Text("Contract Terms & Payment Schedule") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (cropName.isBlank() || quantityTons.isBlank() || pricePerTon.isBlank()) {
                                Toast.makeText(context, "Please fill in all mandatory fields", Toast.LENGTH_SHORT).show()
                            } else {
                                val contract = PublishedContract(
                                    id = "cf_" + System.currentTimeMillis(),
                                    companyName = "Sahyadri Farmers Producer Co. Ltd.",
                                    cropName = cropName,
                                    variety = variety,
                                    requiredQuantityTons = quantityTons.toDoubleOrNull() ?: 50.0,
                                    offeredPricePerTon = pricePerTon.toDoubleOrNull() ?: 20000.0,
                                    harvestPeriod = harvestDate,
                                    targetLocation = location,
                                    qualityRequirements = qualitySpecs,
                                    termsAndConditions = contractTerms,
                                    advancePaymentPercent = advancePercent.toIntOrNull() ?: 20,
                                    status = "Active",
                                    datePublished = "Today"
                                )
                                onContractPublished(contract)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CompanyPrimary)
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = "Publish", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish Contract for Farmers", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ------------------ 3. APPLICATIONS VIEW ------------------
@Composable
fun CompanyApplicationsView(
    applications: List<FarmerContractApplication>,
    onApprove: (FarmerContractApplication) -> Unit,
    onReject: (FarmerContractApplication) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text("Farmer Contract Applications", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CompanyTextPrimary)
        }

        if (applications.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Text("No farmer applications received yet.", color = CompanyTextSecondary)
                }
            }
        } else {
            items(applications) { app ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CompanyCardBg),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(app.farmerName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CompanyTextPrimary)
                            val statusBg = when (app.applicationStatus) {
                                "Approved" -> Color(0xFFE8F5E9)
                                "Rejected" -> Color(0xFFFFEBEE)
                                else -> Color(0xFFFFF3E0)
                            }
                            val statusText = when (app.applicationStatus) {
                                "Approved" -> Color(0xFF2E7D32)
                                "Rejected" -> Color(0xFFC62828)
                                else -> Color(0xFFE65100)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(statusBg)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(app.applicationStatus, color = statusText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text("Crop: ${app.cropName} • Applied ${app.appliedDate}", fontSize = 12.sp, color = CompanyPrimary, fontWeight = FontWeight.SemiBold)
                        Text("📍 Location: ${app.village} • Phone: ${app.farmerPhone}", fontSize = 12.sp, color = CompanyTextSecondary)
                        Text("🌱 Committed Farm Acreage: ${app.committedAcreage} Acres", fontSize = 12.sp, color = CompanyTextPrimary, fontWeight = FontWeight.Medium)
                        Text("📦 Projected Harvest Yield: ${app.projectedYieldTons} Tons", fontSize = 13.sp, color = CompanyPrimary, fontWeight = FontWeight.Bold)

                        if (app.applicationStatus == "Pending Review") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onReject(app) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                                    border = BorderStroke(1.dp, Color(0xFFEF9A9A))
                                ) {
                                    Text("Decline", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { onApprove(app) },
                                    modifier = Modifier.weight(1.3f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CompanyPrimary)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Approve", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve & Contract", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 4. PROFILE VIEW ------------------
@Composable
fun CompanyProfileView(
    companyName: String,
    businessType: String,
    contactPerson: String,
    phone: String,
    location: String,
    fssaiNumber: String,
    totalContractsCount: Int,
    totalApplicationsCount: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CompanyCardBg),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(CompanyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏢", fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(companyName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CompanyTextPrimary)
                            Text(businessType, fontSize = 12.sp, color = CompanyPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Divider(color = Color(0xFFF1F5F9))

                    Text("Contact Person: $contactPerson", fontSize = 13.sp, color = CompanyTextPrimary)
                    Text("Phone: $phone", fontSize = 13.sp, color = CompanyTextPrimary)
                    Text("Location: $location", fontSize = 13.sp, color = CompanyTextPrimary)
                    Text("Certification: $fssaiNumber", fontSize = 13.sp, color = CompanyTextSecondary)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$totalContractsCount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CompanyPrimary)
                        Text("Active Programs", fontSize = 11.sp, color = CompanyTextSecondary)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$totalApplicationsCount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CompanyAccent)
                        Text("Farmer Partners", fontSize = 11.sp, color = CompanyTextSecondary)
                    }
                }
            }
        }
    }
}
