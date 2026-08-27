package com.example

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ------------------ LABOUR THEME PALETTE ------------------
val LabourPrimary = Color(0xFF2E7D32)       // Forest Green
val LabourSecondary = Color(0xFF66BB6A)     // Light Green
val LabourAccent = Color(0xFFF9A825)        // Amber/Gold
val LabourBackground = Color(0xFFF8FBF7)    // Soft agricultural background
val LabourCardBg = Color(0xFFFFFFFF)        // White Card
val LabourTextPrimary = Color(0xFF212121)   // Dark Charcoal
val LabourTextSecondary = Color(0xFF616161) // Medium Grey
val LabourLightBg = Color(0xFFE8F5E9)       // Mint tint

// ------------------ MASTER LABOUR PORTAL ------------------
@Composable
fun LabourPortalScreen(navController: NavController) {
    val context = LocalContext.current

    // Bottom Nav items: "home", "requests", "my_jobs", "profile"
    var currentTab by remember { mutableStateOf("home") }

    // Active Labour Profile State (Logged in as Ramesh Ghadge / Maruti Squad)
    val currentWorkerId = "lab_1"
    var leaderName by remember { mutableStateOf("Ramesh Ghadge (Mukkadam)") }
    var squadName by remember { mutableStateOf("Maruti Farm Labour Squad") }
    var phone by remember { mutableStateOf("+91 98221 44556") }
    var village by remember { mutableStateOf("Narayangaon") }
    var taluka by remember { mutableStateOf("Junnar") }
    var district by remember { mutableStateOf("Pune") }
    var experienceYears by remember { mutableStateOf("5 Years") }
    var teamSize by remember { mutableStateOf("8 Workers") }
    var dailyWage by remember { mutableStateOf("₹500 / worker / day") }
    var isAvailable by remember { mutableStateOf(true) }
    var workingRadiusKm by remember { mutableStateOf(15) }
    var availableDates by remember { mutableStateOf("All Days (Available)") }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAvailabilityDialog by remember { mutableStateOf(false) }

    // Rating modal state
    var ratingJobId by remember { mutableStateOf<String?>(null) }
    var ratingFarmerName by remember { mutableStateOf("") }
    var ratingValue by remember { mutableStateOf(5) }
    var ratingComment by remember { mutableStateOf("") }

    // Sync requests from shared repository
    val allJobRequests = AgroWorldLabourRepository.jobRequests
    val myJobRequests = allJobRequests.filter { it.labourId == currentWorkerId || it.labourId == "all" }

    val pendingRequests = myJobRequests.filter { it.status == "Pending" }
    val activeJobs = myJobRequests.filter { it.status == "Confirmed" || it.status == "Scheduled" || it.status == "In Progress" }
    val completedJobs = myJobRequests.filter { it.status == "Completed" }

    Scaffold(
        topBar = {
            Surface(
                color = LabourCardBg,
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
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LabourPrimary)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "AgroWorld Labour Portal",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LabourPrimary
                                )
                                Text(
                                    text = "👨‍🌾 $squadName ($village)",
                                    fontSize = 12.sp,
                                    color = LabourTextSecondary
                                )
                            }
                        }

                        // Availability Badge Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                                .clickable {
                                    showAvailabilityDialog = true
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAvailable) "Available" else "Busy",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
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
                    selected = currentTab == "home",
                    onClick = { currentTab = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = LabourPrimary,
                        indicatorColor = LabourPrimary,
                        unselectedIconColor = LabourTextSecondary,
                        unselectedTextColor = LabourTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_labour_home")
                )
                NavigationBarItem(
                    selected = currentTab == "requests",
                    onClick = { currentTab = "requests" },
                    icon = {
                        BadgedBox(badge = {
                            if (pendingRequests.isNotEmpty()) {
                                Badge(containerColor = Color(0xFFD32F2F)) {
                                    Text(pendingRequests.size.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }) {
                            Icon(Icons.Default.Mail, contentDescription = "Job Requests")
                        }
                    },
                    label = { Text("Job Requests", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = LabourPrimary,
                        indicatorColor = LabourPrimary,
                        unselectedIconColor = LabourTextSecondary,
                        unselectedTextColor = LabourTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_labour_requests")
                )
                NavigationBarItem(
                    selected = currentTab == "my_jobs",
                    onClick = { currentTab = "my_jobs" },
                    icon = {
                        BadgedBox(badge = {
                            if (activeJobs.isNotEmpty()) {
                                Badge(containerColor = LabourAccent) {
                                    Text(activeJobs.size.toString(), color = LabourTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }) {
                            Icon(Icons.Default.Work, contentDescription = "My Jobs")
                        }
                    },
                    label = { Text("My Jobs", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = LabourPrimary,
                        indicatorColor = LabourPrimary,
                        unselectedIconColor = LabourTextSecondary,
                        unselectedTextColor = LabourTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_labour_jobs")
                )
                NavigationBarItem(
                    selected = currentTab == "profile",
                    onClick = { currentTab = "profile" },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = LabourPrimary,
                        indicatorColor = LabourPrimary,
                        unselectedIconColor = LabourTextSecondary,
                        unselectedTextColor = LabourTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_labour_profile")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LabourBackground)
        ) {
            when (currentTab) {
                "home" -> LabourHomeDashboard(
                    squadName = squadName,
                    leaderName = leaderName,
                    village = village,
                    taluka = taluka,
                    isAvailable = isAvailable,
                    pendingCount = pendingRequests.size,
                    activeCount = activeJobs.size,
                    completedCount = completedJobs.size,
                    pendingRequests = pendingRequests,
                    activeJobs = activeJobs,
                    onNavigateTab = { currentTab = it },
                    onAcceptJob = { jobId ->
                        AgroWorldLabourRepository.labourAcceptJobRequest(jobId)
                        Toast.makeText(context, "Job Request Accepted! Farmer will confirm. ✅", Toast.LENGTH_SHORT).show()
                    },
                    onRejectJob = { jobId ->
                        AgroWorldLabourRepository.labourRejectJobRequest(jobId)
                        Toast.makeText(context, "Job Request Declined.", Toast.LENGTH_SHORT).show()
                    },
                    onOpenAvailability = { showAvailabilityDialog = true }
                )
                "requests" -> LabourJobRequestsScreen(
                    jobRequests = myJobRequests,
                    onAccept = { jobId ->
                        AgroWorldLabourRepository.labourAcceptJobRequest(jobId)
                        Toast.makeText(context, "Job Request Accepted! Farmer notified. ✅", Toast.LENGTH_SHORT).show()
                    },
                    onReject = { jobId ->
                        AgroWorldLabourRepository.labourRejectJobRequest(jobId)
                        Toast.makeText(context, "Job Request Declined.", Toast.LENGTH_SHORT).show()
                    }
                )
                "my_jobs" -> LabourMyJobsScreen(
                    jobRequests = myJobRequests,
                    onStartJob = { jobId ->
                        AgroWorldLabourRepository.labourStartJob(jobId)
                        Toast.makeText(context, "Job Started! 🌾 Work status updated.", Toast.LENGTH_SHORT).show()
                    },
                    onCompleteJob = { jobId ->
                        AgroWorldLabourRepository.labourCompleteJob(jobId)
                        Toast.makeText(context, "Job Completed! Marked for Farmer confirmation. 🎉", Toast.LENGTH_SHORT).show()
                    },
                    onRateFarmer = { jobId, farmerName ->
                        ratingJobId = jobId
                        ratingFarmerName = farmerName
                    }
                )
                "profile" -> LabourProfileScreen(
                    leaderName = leaderName,
                    squadName = squadName,
                    phone = phone,
                    village = village,
                    taluka = taluka,
                    district = district,
                    experienceYears = experienceYears,
                    teamSize = teamSize,
                    dailyWage = dailyWage,
                    isAvailable = isAvailable,
                    workingRadiusKm = workingRadiusKm,
                    availableDates = availableDates,
                    completedJobsCount = completedJobs.size + 42,
                    onEditClick = { showEditProfileDialog = true },
                    onAvailabilityClick = { showAvailabilityDialog = true },
                    onLogout = {
                        navController.navigate("role_selection") {
                            popUpTo("role_selection") { inclusive = true }
                        }
                    }
                )
            }
        }
    }

    // AVAILABILITY SETTINGS DIALOG
    if (showAvailabilityDialog) {
        AlertDialog(
            onDismissRequest = { showAvailabilityDialog = false },
            title = { Text("Labour Availability Settings", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Set your squad availability for matching with nearby farmers:", fontSize = 12.sp, color = LabourTextSecondary)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                if (isAvailable) "Available for Work" else "Busy / Unavailable",
                                fontWeight = FontWeight.Bold,
                                color = if (isAvailable) LabourPrimary else Color(0xFFC62828)
                            )
                            Text(
                                if (isAvailable) "Visible to nearby farmers in search" else "Hidden from new requirement searches",
                                fontSize = 11.sp,
                                color = LabourTextSecondary
                            )
                        }
                        Switch(
                            checked = isAvailable,
                            onCheckedChange = {
                                isAvailable = it
                                AgroWorldLabourRepository.availableWorkers.find { w -> w.id == currentWorkerId }?.isAvailable = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = LabourPrimary
                            )
                        )
                    }

                    OutlinedTextField(
                        value = availableDates,
                        onValueChange = { availableDates = it },
                        label = { Text("Available Dates") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = "$workingRadiusKm km radius",
                        onValueChange = {
                            workingRadiusKm = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 15
                        },
                        label = { Text("Working Search Radius") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAvailabilityDialog = false
                        Toast.makeText(context, "Availability settings saved! ✅", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LabourPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Settings", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAvailabilityDialog = false }) {
                    Text("Cancel", color = LabourTextSecondary)
                }
            }
        )
    }

    // EDIT PROFILE DIALOG
    if (showEditProfileDialog) {
        var tempSquad by remember { mutableStateOf(squadName) }
        var tempLeader by remember { mutableStateOf(leaderName) }
        var tempPhone by remember { mutableStateOf(phone) }
        var tempWage by remember { mutableStateOf(dailyWage) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Labour Profile", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = tempSquad,
                        onValueChange = { tempSquad = it },
                        label = { Text("Squad / Toli Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = tempLeader,
                        onValueChange = { tempLeader = it },
                        label = { Text("Leader / Mukkadam Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("Contact Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = tempWage,
                        onValueChange = { tempWage = it },
                        label = { Text("Standard Daily Wage") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        squadName = tempSquad
                        leaderName = tempLeader
                        phone = tempPhone
                        dailyWage = tempWage
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profile updated successfully! ✅", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LabourPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = LabourTextSecondary)
                }
            }
        )
    }

    // RATING DIALOG FOR LABOUR TO RATE FARMER
    if (ratingJobId != null) {
        AlertDialog(
            onDismissRequest = { ratingJobId = null },
            title = { Text("Rate Farmer: $ratingFarmerName", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("How was your experience working on this farm?", fontSize = 12.sp, color = LabourTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { ratingValue = i }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "$i Stars",
                                    tint = if (i <= ratingValue) LabourAccent else Color(0xFFE2E8F0),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = ratingComment,
                        onValueChange = { ratingComment = it },
                        label = { Text("Optional Review / Feedback") },
                        placeholder = { Text("e.g. Prompt cash payment, good food and clear instructions.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        ratingJobId?.let { id ->
                            AgroWorldLabourRepository.addReview(
                                jobId = id,
                                fromRole = "labour",
                                toRole = "farmer",
                                fromName = squadName,
                                toName = ratingFarmerName,
                                rating = ratingValue.toDouble(),
                                comment = ratingComment.ifBlank { "Great experience working with this farmer." }
                            )
                            Toast.makeText(context, "Review submitted! Thank you. ⭐", Toast.LENGTH_SHORT).show()
                        }
                        ratingJobId = null
                        ratingComment = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LabourPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit Rating", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { ratingJobId = null }) {
                    Text("Cancel", color = LabourTextSecondary)
                }
            }
        )
    }
}

// ------------------ 1. LABOUR HOME DASHBOARD ------------------
@Composable
fun LabourHomeDashboard(
    squadName: String,
    leaderName: String,
    village: String,
    taluka: String,
    isAvailable: Boolean,
    pendingCount: Int,
    activeCount: Int,
    completedCount: Int,
    pendingRequests: List<LabourJobItem>,
    activeJobs: List<LabourJobItem>,
    onNavigateTab: (String) -> Unit,
    onAcceptJob: (String) -> Unit,
    onRejectJob: (String) -> Unit,
    onOpenAvailability: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        // WELCOME BANNER
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LabourPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Namaskar, $leaderName 🙏", fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
                            Text(squadName, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("📍 $village, $taluka (Search Radius: 15 km)", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👨‍🌾", fontSize = 26.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Status: ", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                            Text(
                                if (isAvailable) "Ready for Hiring ✅" else "Busy / In-field ⏸️",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LabourAccent
                            )
                        }
                        TextButton(
                            onClick = onOpenAvailability,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("Change Status ⚙️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // METRICS ROW (Available Jobs, Pending Requests, Confirmed Jobs, Completed Jobs)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pending Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, if (pendingCount > 0) Color(0xFFEF5350) else Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTab("requests") }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Requests", fontSize = 12.sp, color = LabourTextSecondary)
                            Text("📩", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$pendingCount", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = if (pendingCount > 0) Color(0xFFD32F2F) else LabourTextPrimary)
                        Text("New Job Offers", fontSize = 10.sp, color = LabourTextSecondary)
                    }
                }

                // Active Jobs Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTab("my_jobs") }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Active", fontSize = 12.sp, color = LabourTextSecondary)
                            Text("🌾", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$activeCount", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LabourPrimary)
                        Text("Confirmed Jobs", fontSize = 10.sp, color = LabourTextSecondary)
                    }
                }

                // Completed Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTab("my_jobs") }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Done", fontSize = 12.sp, color = LabourTextSecondary)
                            Text("✅", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${completedCount + 42}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                        Text("Completed", fontSize = 10.sp, color = LabourTextSecondary)
                    }
                }
            }
        }

        // PENDING JOB OFFERS PREVIEW
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Incoming Job Requests (${pendingRequests.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                if (pendingRequests.isNotEmpty()) {
                    TextButton(onClick = { onNavigateTab("requests") }) {
                        Text("View All", color = LabourPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            if (pendingRequests.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✨", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("No pending requests right now", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                        Text("When a nearby farmer posts a requirement and selects you, it appears here.", fontSize = 12.sp, color = LabourTextSecondary, textAlign = TextAlign.Center)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                    pendingRequests.take(2).forEach { job ->
                        LabourJobRequestCard(
                            job = job,
                            onAccept = { onAcceptJob(job.id) },
                            onReject = { onRejectJob(job.id) }
                        )
                    }
                }
            }
        }

        // CONFIRMED / SCHEDULED JOBS PREVIEW
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Confirmed & Upcoming Jobs (${activeJobs.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                if (activeJobs.isNotEmpty()) {
                    TextButton(onClick = { onNavigateTab("my_jobs") }) {
                        Text("Manage Jobs", color = LabourPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            if (activeJobs.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📅", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("No scheduled jobs upcoming", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LabourTextSecondary)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                    activeJobs.take(2).forEach { job ->
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
                                    Text("🌾 ${job.workType}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(LabourPrimary.copy(alpha = 0.12f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(job.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LabourPrimary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Farmer: ${job.farmerName} (${job.farmerPhone})", fontSize = 12.sp, color = LabourTextSecondary)
                                Text("📅 Date: ${job.startDate} • ⏰ ${job.startTime}", fontSize = 12.sp, color = LabourPrimary, fontWeight = FontWeight.SemiBold)
                                Text("💰 Wage: ₹${job.wage.toInt()} / day • 📍 ${job.village} (${job.distanceKm} km)", fontSize = 12.sp, color = LabourTextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ 2. JOB REQUESTS SCREEN ------------------
@Composable
fun LabourJobRequestsScreen(
    jobRequests: List<LabourJobItem>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit
) {
    val pending = jobRequests.filter { it.status == "Pending" }
    val other = jobRequests.filter { it.status != "Pending" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            Text(
                "Incoming Farm Job Requests (${pending.size})",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = LabourTextPrimary
            )
            Text(
                "Farmers who posted labour requirements and matched with your squad profile:",
                fontSize = 12.sp,
                color = LabourTextSecondary
            )
        }

        if (pending.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Inbox, contentDescription = "Empty", tint = LabourTextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Pending Job Requests", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                        Text("Keep your status set to 'Available' to receive new requests from nearby farmers.", fontSize = 12.sp, color = LabourTextSecondary, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(pending) { job ->
                LabourJobRequestCard(job = job, onAccept = { onAccept(job.id) }, onReject = { onReject(job.id) })
            }
        }

        if (other.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Past Requests History (${other.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
            }

            items(other) { job ->
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
                            Text("🌾 ${job.workType} (${job.crop})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                            Text("Farmer: ${job.farmerName} • ${job.startDate}", fontSize = 12.sp, color = LabourTextSecondary)
                            Text("Wage: ₹${job.wage.toInt()} / worker / day", fontSize = 12.sp, color = LabourPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (job.status) {
                                        "Accepted", "Confirmed" -> Color(0xFFE8F5E9)
                                        "Completed" -> Color(0xFFE0F2FE)
                                        "Rejected" -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFF1F5F9)
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                job.status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (job.status) {
                                    "Accepted", "Confirmed" -> LabourPrimary
                                    "Completed" -> Color(0xFF0284C7)
                                    "Rejected" -> Color(0xFFC62828)
                                    else -> LabourTextSecondary
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LabourJobRequestCard(
    job: LabourJobItem,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LabourPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌾", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(job.workType, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                        Text("Crop: ${job.crop}", fontSize = 12.sp, color = LabourTextSecondary)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF3E0))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("New Request", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                }
            }

            Divider(color = Color(0xFFF1F5F9))

            // FARMER & LOCATION INFO
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("👨‍🌾 Farmer: ${job.farmerName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                    Text("📞 ${job.farmerPhone}", fontSize = 11.sp, color = LabourTextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("📍 ${job.village}, ${job.taluka}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LabourPrimary)
                    Text("Approx. ${job.distanceKm} km away", fontSize = 11.sp, color = LabourTextSecondary)
                }
            }

            // DATE, TIME & WAGE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("📅 Dates", fontSize = 10.sp, color = LabourTextSecondary)
                    Text("${job.startDate} - ${job.endDate}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                    Text("⏰ ${job.startTime} (${job.workingHours} hrs/day)", fontSize = 11.sp, color = LabourTextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("💰 Offered Wage", fontSize = 10.sp, color = LabourTextSecondary)
                    Text("₹${job.wage.toInt()} / day", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LabourPrimary)
                    Text(if (job.foodProvided) "🍱 Food Provided" else "No Food", fontSize = 11.sp, color = if (job.foodProvided) LabourPrimary else LabourTextSecondary)
                }
            }

            if (job.specialInstructions.isNotBlank()) {
                Text("📝 Note: ${job.specialInstructions}", fontSize = 11.sp, color = LabourTextSecondary)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ACTION BUTTONS (ACCEPT / REJECT)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Decline", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = LabourPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Accept", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Accept Job", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}

// ------------------ 3. MY JOBS SCREEN ------------------
@Composable
fun LabourMyJobsScreen(
    jobRequests: List<LabourJobItem>,
    onStartJob: (String) -> Unit,
    onCompleteJob: (String) -> Unit,
    onRateFarmer: (String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("upcoming") }

    val upcomingJobs = jobRequests.filter { it.status == "Accepted" || it.status == "Confirmed" || it.status == "Scheduled" }
    val activeJobs = jobRequests.filter { it.status == "In Progress" }
    val completedJobs = jobRequests.filter { it.status == "Completed" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LabourBackground)
    ) {
        // TABS: Upcoming, Active, Completed
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTab == "upcoming",
                onClick = { selectedTab = "upcoming" },
                label = { Text("Upcoming (${upcomingJobs.size})") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = LabourPrimary, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = selectedTab == "active",
                onClick = { selectedTab = "active" },
                label = { Text("Active (${activeJobs.size})") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = LabourPrimary, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = selectedTab == "completed",
                onClick = { selectedTab = "completed" },
                label = { Text("Completed (${completedJobs.size})") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = LabourPrimary, selectedLabelColor = Color.White)
            )
        }

        val displayList = when (selectedTab) {
            "upcoming" -> upcomingJobs
            "active" -> activeJobs
            else -> completedJobs
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            if (displayList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🌾", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                when (selectedTab) {
                                    "upcoming" -> "No Upcoming Jobs"
                                    "active" -> "No Currently Active Jobs"
                                    else -> "No Completed Jobs Yet"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = LabourTextPrimary
                            )
                        }
                    }
                }
            } else {
                items(displayList) { job ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
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
                                Text("🌾 ${job.workType}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (job.status) {
                                                "In Progress" -> Color(0xFFFFF3E0)
                                                "Completed" -> Color(0xFFE8F5E9)
                                                else -> Color(0xFFE0F2FE)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        if (job.status == "In Progress") "Work In Progress" else job.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (job.status) {
                                            "In Progress" -> Color(0xFFE65100)
                                            "Completed" -> LabourPrimary
                                            else -> Color(0xFF0284C7)
                                        }
                                    )
                                }
                            }

                            Text("Farmer: ${job.farmerName} • 📞 ${job.farmerPhone}", fontSize = 12.sp, color = LabourTextSecondary)
                            Text("📍 Farm: ${job.village}, ${job.taluka} (${job.distanceKm} km)", fontSize = 12.sp, color = LabourPrimary, fontWeight = FontWeight.SemiBold)
                            Text("📅 Date: ${job.startDate} - ${job.endDate} • ⏰ ${job.startTime} (${job.workingHours} hrs)", fontSize = 12.sp, color = LabourTextSecondary)
                            Text("💰 Wage: ₹${job.wage.toInt()} / worker / day", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)

                            Spacer(modifier = Modifier.height(6.dp))

                            // ACTION BUTTONS BASED ON STATE
                            when (job.status) {
                                "Confirmed", "Scheduled", "Accepted" -> {
                                    Button(
                                        onClick = { onStartJob(job.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = LabourPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("START JOB (काम सुरू करा)", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                                "In Progress" -> {
                                    Button(
                                        onClick = { onCompleteJob(job.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Complete", modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("MARK AS COMPLETED (काम पूर्ण झाले)", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                                "Completed" -> {
                                    OutlinedButton(
                                        onClick = { onRateFarmer(job.id, job.farmerName) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LabourAccent),
                                        border = BorderStroke(1.dp, LabourAccent),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = "Rate", tint = LabourAccent, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Rate & Review Farmer ⭐", fontWeight = FontWeight.Bold, color = LabourTextPrimary)
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

// ------------------ 4. LABOUR PROFILE SCREEN ------------------
@Composable
fun LabourProfileScreen(
    leaderName: String,
    squadName: String,
    phone: String,
    village: String,
    taluka: String,
    district: String,
    experienceYears: String,
    teamSize: String,
    dailyWage: String,
    isAvailable: Boolean,
    workingRadiusKm: Int,
    availableDates: String,
    completedJobsCount: Int,
    onEditClick: () -> Unit,
    onAvailabilityClick: () -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        // PROFILE HEADER CARD
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(LabourPrimary.copy(alpha = 0.15f))
                            .border(2.dp, LabourPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍🌾", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(squadName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                    Text("Mukkadam: $leaderName", fontSize = 13.sp, color = LabourTextSecondary)
                    Text("📞 $phone", fontSize = 12.sp, color = LabourTextSecondary)
                    Text("📍 $village, $taluka ($district)", fontSize = 12.sp, color = LabourPrimary, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐ 4.8", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LabourAccent)
                            Text("Rating", fontSize = 11.sp, color = LabourTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$completedJobsCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LabourPrimary)
                            Text("Jobs Done", fontSize = 11.sp, color = LabourTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(teamSize, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)
                            Text("Squad Size", fontSize = 11.sp, color = LabourTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onAvailabilityClick,
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = LabourPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Availability", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // SKILLS & CAPABILITIES
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Squad Agricultural Skills", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LabourTextPrimary)

                    val skills = listOf(
                        "Sugarcane Harvesting (ऊस तोडणी)",
                        "Onion Digging & Grading (कांदा काढणी)",
                        "Pesticide Spraying (फवारणी)",
                        "Weeding & Khurpani (खुरपणी)",
                        "Tractor & Heavy Trailer Loading"
                    )

                    skills.forEach { sk ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Check", tint = LabourPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(sk, fontSize = 13.sp, color = LabourTextPrimary)
                        }
                    }
                }
            }
        }

        // LOGOUT BUTTON
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
