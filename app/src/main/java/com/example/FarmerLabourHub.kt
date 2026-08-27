package com.example

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Complete Nearby Labour Hiring Module for Farmer
 * Implements the Requirement-First matching & hiring workflow
 */
@Composable
fun FarmerLabourHubScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Sub-Hub Navigation Tabs: "post_requirement", "my_requests", "confirmed_jobs"
    var currentTab by remember { mutableStateOf("post_requirement") }

    // Multi-Step Requirement Form State
    var formStep by remember { mutableStateOf(1) } // 1: Work Details, 2: Labour Req, 3: Date/Time, 4: Payment, 5: Location, 6: Additional, 7: Preview, 8: Match & Select, 9: Confirmation

    // Form fields
    var workType by remember { mutableStateOf("Sugarcane Harvesting") }
    var customWorkType by remember { mutableStateOf("") }
    var crop by remember { mutableStateOf("Sugarcane (Co-86032)") }
    var description by remember { mutableStateOf("Need skilled labour squad for sugarcane harvesting and field clearing. 2 acres plot.") }

    var workersRequired by remember { mutableStateOf(8) }
    var skillLevel by remember { mutableStateOf("Skilled") }
    var experienceRequired by remember { mutableStateOf("2+ years") }
    val selectedSkills = remember { mutableStateListOf("Sugarcane Harvesting", "Cutting", "Loading") }

    var startDate by remember { mutableStateOf("5 September") }
    var endDate by remember { mutableStateOf("6 September") }
    var startTime by remember { mutableStateOf("7:00 AM") }
    var workingHoursPerDay by remember { mutableStateOf(8) }

    var wageType by remember { mutableStateOf("Per Day") }
    var wageAmount by remember { mutableStateOf("500") }
    var paymentTerms by remember { mutableStateOf("Daily Cash") }

    var village by remember { mutableStateOf("Narayangaon") }
    var taluka by remember { mutableStateOf("Junnar") }
    var district by remember { mutableStateOf("Pune") }
    var farmLocation by remember { mutableStateOf("Plot No. 4, Canal Road") }
    var searchRadiusKm by remember { mutableStateOf(10) }

    var specialInstructions by remember { mutableStateOf("Experienced sugarcane harvesting workers preferred. Sharp koyta/sickles required.") }
    var requiredEquipment by remember { mutableStateOf("Sickles (Koyta), Gloves") }
    var foodProvided by remember { mutableStateOf(true) }
    var transportProvided by remember { mutableStateOf(false) }
    var otherRequirements by remember { mutableStateOf("Morning tea and lunch will be served in the farm.") }

    // Match & Selection State
    var activeCreatedRequirement by remember { mutableStateOf<LabourRequirement?>(null) }
    val selectedWorkerIds = remember { mutableStateListOf<String>() }

    // Rating modal state
    var ratingRequirementId by remember { mutableStateOf<String?>(null) }
    var ratingWorkerName by remember { mutableStateOf("") }
    var ratingValue by remember { mutableStateOf(5) }
    var ratingComment by remember { mutableStateOf("") }

    val requirementsList = AgroWorldLabourRepository.requirements
    val activeRequirements = requirementsList.filter { it.status != "Completed" && it.status != "Cancelled" }
    val confirmedRequirements = requirementsList.filter { it.status == "Confirmed" || it.status == "Scheduled" || it.status == "Work Started" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FarmerBackground)
    ) {
        // TOP HEADER
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = FarmerPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Nearby Labour Hiring",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextPrimary
                        )
                        Text(
                            text = "Find & hire skilled agricultural workers nearby",
                            fontSize = 12.sp,
                            color = FarmerTextSecondary
                        )
                    }
                }

                // TAB SWITCHER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentTab == "post_requirement",
                        onClick = { currentTab = "post_requirement" },
                        label = { Text("+ Post Requirement", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("tab_post_req")
                    )
                    FilterChip(
                        selected = currentTab == "my_requests",
                        onClick = { currentTab = "my_requests" },
                        label = {
                            Text(
                                "My Requests (${requirementsList.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("tab_my_reqs")
                    )
                    FilterChip(
                        selected = currentTab == "confirmed_jobs",
                        onClick = { currentTab = "confirmed_jobs" },
                        label = {
                            Text(
                                "Confirmed (${confirmedRequirements.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("tab_confirmed_jobs")
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // MAIN CONTENT ROUTER
        Box(modifier = Modifier.weight(1f)) {
            when (currentTab) {
                "post_requirement" -> {
                    when (formStep) {
                        1 -> Step1WorkDetails(
                            workType = workType,
                            onWorkTypeChange = { workType = it },
                            customWorkType = customWorkType,
                            onCustomWorkTypeChange = { customWorkType = it },
                            crop = crop,
                            onCropChange = { crop = it },
                            description = description,
                            onDescriptionChange = { description = it },
                            onNext = { formStep = 2 }
                        )
                        2 -> Step2LabourRequirement(
                            workersRequired = workersRequired,
                            onWorkersChange = { workersRequired = it },
                            skillLevel = skillLevel,
                            onSkillLevelChange = { skillLevel = it },
                            experienceRequired = experienceRequired,
                            onExperienceChange = { experienceRequired = it },
                            selectedSkills = selectedSkills,
                            onNext = { formStep = 3 },
                            onPrevious = { formStep = 1 }
                        )
                        3 -> Step3DateTime(
                            startDate = startDate,
                            onStartDateChange = { startDate = it },
                            endDate = endDate,
                            onEndDateChange = { endDate = it },
                            startTime = startTime,
                            onStartTimeChange = { startTime = it },
                            workingHours = workingHoursPerDay,
                            onWorkingHoursChange = { workingHoursPerDay = it },
                            onNext = { formStep = 4 },
                            onPrevious = { formStep = 2 }
                        )
                        4 -> Step4Payment(
                            wageType = wageType,
                            onWageTypeChange = { wageType = it },
                            wageAmount = wageAmount,
                            onWageAmountChange = { wageAmount = it },
                            paymentTerms = paymentTerms,
                            onPaymentTermsChange = { paymentTerms = it },
                            onNext = { formStep = 5 },
                            onPrevious = { formStep = 3 }
                        )
                        5 -> Step5Location(
                            village = village,
                            onVillageChange = { village = it },
                            taluka = taluka,
                            onTalukaChange = { taluka = it },
                            district = district,
                            farmLocation = farmLocation,
                            onFarmLocationChange = { farmLocation = it },
                            searchRadiusKm = searchRadiusKm,
                            onRadiusChange = { searchRadiusKm = it },
                            onNext = { formStep = 6 },
                            onPrevious = { formStep = 4 }
                        )
                        6 -> Step6Additional(
                            specialInstructions = specialInstructions,
                            onSpecialChange = { specialInstructions = it },
                            requiredEquipment = requiredEquipment,
                            onEquipmentChange = { requiredEquipment = it },
                            foodProvided = foodProvided,
                            onFoodChange = { foodProvided = it },
                            transportProvided = transportProvided,
                            onTransportChange = { transportProvided = it },
                            otherRequirements = otherRequirements,
                            onOtherChange = { otherRequirements = it },
                            onNext = { formStep = 7 },
                            onPrevious = { formStep = 5 }
                        )
                        7 -> Step7Preview(
                            workType = if (workType == "Other") customWorkType.ifBlank { "Farm Work" } else workType,
                            crop = crop,
                            description = description,
                            workersRequired = workersRequired,
                            skillLevel = skillLevel,
                            experienceRequired = experienceRequired,
                            startDate = startDate,
                            endDate = endDate,
                            startTime = startTime,
                            workingHours = workingHoursPerDay,
                            wageType = wageType,
                            wageAmount = wageAmount.toDoubleOrNull() ?: 500.0,
                            paymentTerms = paymentTerms,
                            village = village,
                            taluka = taluka,
                            farmLocation = farmLocation,
                            searchRadiusKm = searchRadiusKm,
                            foodProvided = foodProvided,
                            transportProvided = transportProvided,
                            specialInstructions = specialInstructions,
                            onEdit = { formStep = 1 },
                            onPostRequirement = {
                                val newReq = LabourRequirement(
                                    id = "req_" + (100..999).random(),
                                    farmerName = "Ramesh Patil",
                                    farmerPhone = "+91 98220 14589",
                                    workType = if (workType == "Other") customWorkType.ifBlank { "General Work" } else workType,
                                    crop = crop,
                                    description = description,
                                    workersRequired = workersRequired,
                                    skillLevel = skillLevel,
                                    experienceRequired = experienceRequired,
                                    requiredSkills = selectedSkills.toList(),
                                    startDate = startDate,
                                    endDate = endDate,
                                    startTime = startTime,
                                    workingHoursPerDay = workingHoursPerDay,
                                    wageType = wageType,
                                    wageAmount = wageAmount.toDoubleOrNull() ?: 500.0,
                                    paymentTerms = paymentTerms,
                                    village = village,
                                    taluka = taluka,
                                    district = district,
                                    farmLocation = farmLocation,
                                    searchRadiusKm = searchRadiusKm,
                                    specialInstructions = specialInstructions,
                                    requiredEquipment = requiredEquipment,
                                    foodProvided = foodProvided,
                                    transportProvided = transportProvided,
                                    otherRequirements = otherRequirements,
                                    status = "Finding Labour"
                                )
                                AgroWorldLabourRepository.postRequirement(newReq)
                                activeCreatedRequirement = newReq

                                // Pre-select matching workers
                                val matched = AgroWorldLabourRepository.findMatchingWorkers(newReq)
                                selectedWorkerIds.clear()
                                matched.take(workersRequired).forEach { selectedWorkerIds.add(it.id) }

                                formStep = 8
                                Toast.makeText(context, "Requirement posted! Finding suitable nearby labour...", Toast.LENGTH_SHORT).show()
                            }
                        )
                        8 -> Step8MatchAndSelectLabour(
                            requirement = activeCreatedRequirement ?: requirementsList.first(),
                            selectedWorkerIds = selectedWorkerIds,
                            onToggleWorker = { workerId ->
                                if (selectedWorkerIds.contains(workerId)) {
                                    selectedWorkerIds.remove(workerId)
                                } else {
                                    selectedWorkerIds.add(workerId)
                                }
                            },
                            onSendRequests = { reqId, workerIds ->
                                AgroWorldLabourRepository.sendRequestsToWorkers(reqId, workerIds)
                                formStep = 9
                                Toast.makeText(context, "Hiring requests sent to ${workerIds.size} workers! 📩", Toast.LENGTH_LONG).show()
                            },
                            onBackToForm = { formStep = 7 }
                        )
                        9 -> Step9RequestSentSuccess(
                            workersCount = selectedWorkerIds.size,
                            onViewMyRequests = {
                                currentTab = "my_requests"
                                formStep = 1
                            },
                            onNewRequirement = {
                                formStep = 1
                            }
                        )
                    }
                }
                "my_requests" -> FarmerMyLabourRequestsView(
                    requirements = requirementsList,
                    onSelectRequirement = { req ->
                        activeCreatedRequirement = req
                    },
                    onConfirmLabour = { reqId ->
                        AgroWorldLabourRepository.farmerConfirmWorkers(reqId)
                        Toast.makeText(context, "Workers Confirmed! Job scheduled. ✅", Toast.LENGTH_SHORT).show()
                    },
                    onFindReplacements = { req ->
                        activeCreatedRequirement = req
                        selectedWorkerIds.clear()
                        val matched = AgroWorldLabourRepository.findMatchingWorkers(req)
                        matched.filter { !req.workerIdsRejected.contains(it.id) && !req.workerIdsAccepted.contains(it.id) }
                            .take(req.workersRequired - req.workerIdsAccepted.size)
                            .forEach { selectedWorkerIds.add(it.id) }
                        currentTab = "post_requirement"
                        formStep = 8
                    },
                    onConfirmJobDone = { reqId ->
                        AgroWorldLabourRepository.farmerConfirmCompletion(reqId)
                        Toast.makeText(context, "Work completed and closed! 🎉", Toast.LENGTH_SHORT).show()
                    },
                    onRateLabour = { reqId, workerName ->
                        ratingRequirementId = reqId
                        ratingWorkerName = workerName
                    }
                )
                "confirmed_jobs" -> FarmerConfirmedJobsView(
                    requirements = confirmedRequirements,
                    onOpenDirections = { loc ->
                        val gmmIntentUri = Uri.parse("geo:0,0?q=$loc, Narayangaon, Junnar")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Opening farm map location: $loc", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCallWorker = { phoneNo ->
                        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNo"))
                        try {
                            context.startActivity(callIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Dialing $phoneNo", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    // RATING DIALOG
    if (ratingRequirementId != null) {
        AlertDialog(
            onDismissRequest = { ratingRequirementId = null },
            title = { Text("Rate & Review Labour Squad", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("How was the quality of work done by $ratingWorkerName?", fontSize = 12.sp, color = FarmerTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { ratingValue = i }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "$i Stars",
                                    tint = if (i <= ratingValue) FarmerAccent else Color(0xFFE2E8F0),
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = ratingComment,
                        onValueChange = { ratingComment = it },
                        label = { Text("Review & Feedback") },
                        placeholder = { Text("e.g. Excellent harvesting speed, clean field cutting.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        ratingRequirementId?.let { id ->
                            AgroWorldLabourRepository.addReview(
                                jobId = id,
                                fromRole = "farmer",
                                toRole = "labour",
                                fromName = "Ramesh Patil (Farmer)",
                                toName = ratingWorkerName,
                                rating = ratingValue.toDouble(),
                                comment = ratingComment.ifBlank { "Great work done on farm." }
                            )
                            val req = AgroWorldLabourRepository.requirements.find { it.id == id }
                            req?.farmerRating = ratingValue.toDouble()
                            req?.farmerReview = ratingComment
                            Toast.makeText(context, "Rating submitted! ⭐ Thank you.", Toast.LENGTH_SHORT).show()
                        }
                        ratingRequirementId = null
                        ratingComment = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit Rating", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { ratingRequirementId = null }) {
                    Text("Cancel", color = FarmerTextSecondary)
                }
            }
        )
    }
}

// ------------------ STEP 1: WORK DETAILS ------------------
@Composable
fun Step1WorkDetails(
    workType: String,
    onWorkTypeChange: (String) -> Unit,
    customWorkType: String,
    onCustomWorkTypeChange: (String) -> Unit,
    crop: String,
    onCropChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val workTypes = listOf(
        "Sugarcane Harvesting (ऊस तोडणी)",
        "Weeding (खुरपणी)",
        "Sowing (पेरणी)",
        "Planting (लागवड)",
        "Harvesting (कापणी)",
        "Pesticide Spraying (फवारणी)",
        "Fertilizer Application (खत टाकणे)",
        "Irrigation (पाणी देणे)",
        "Fruit Picking (फळ तोडणी)",
        "Other"
    )

    val popularCrops = listOf("Sugarcane", "Pune Red Onions", "Soybean", "Tomato", "Rice", "Cotton", "Wheat", "Grapes", "Pomegranate")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            FormStepIndicator(currentStep = 1, totalSteps = 6, stepTitle = "Step 1 – Work Details")
        }

        item {
            Text("Select Farm Work Activity *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                workTypes.forEach { type ->
                    val isSelected = workType == type || (type == "Other" && workType == "Other")
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) FarmerLightBg else Color.White),
                        border = BorderStroke(1.dp, if (isSelected) FarmerPrimary else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onWorkTypeChange(type) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(type, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) FarmerPrimary else FarmerTextPrimary)
                            RadioButton(
                                selected = isSelected,
                                onClick = { onWorkTypeChange(type) },
                                colors = RadioButtonDefaults.colors(selectedColor = FarmerPrimary)
                            )
                        }
                    }
                }
            }
        }

        if (workType == "Other") {
            item {
                OutlinedTextField(
                    value = customWorkType,
                    onValueChange = onCustomWorkTypeChange,
                    label = { Text("Specify Work Type *") },
                    placeholder = { Text("e.g. Drip line layout, Trellising") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Text("Select Crop *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                items(popularCrops) { cr ->
                    FilterChip(
                        selected = crop.contains(cr),
                        onClick = { onCropChange(cr) },
                        label = { Text(cr, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            OutlinedTextField(
                value = crop,
                onValueChange = onCropChange,
                label = { Text("Crop & Variety") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Work Description & Scope") },
                placeholder = { Text("Describe specific farm tasks, plot conditions, etc.") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Next: Labour Requirement ➔", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }
        }
    }
}

// ------------------ STEP 2: LABOUR REQUIREMENT ------------------
@Composable
fun Step2LabourRequirement(
    workersRequired: Int,
    onWorkersChange: (Int) -> Unit,
    skillLevel: String,
    onSkillLevelChange: (String) -> Unit,
    experienceRequired: String,
    onExperienceChange: (String) -> Unit,
    selectedSkills: MutableList<String>,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val skillsOptions = listOf("Sugarcane Harvesting", "Cutting", "Loading", "Weeding", "Planting", "Spraying", "Tractor Operation", "Sorting", "Grading")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            FormStepIndicator(currentStep = 2, totalSteps = 6, stepTitle = "Step 2 – Labour Requirement")
        }

        // NUMBER OF WORKERS COUNTER
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Number of Workers Required *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (workersRequired > 1) onWorkersChange(workersRequired - 1) },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FarmerLightBg)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Minus", tint = FarmerPrimary)
                        }
                        Text(
                            "$workersRequired Workers",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerPrimary
                        )
                        IconButton(
                            onClick = { onWorkersChange(workersRequired + 1) },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FarmerLightBg)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Plus", tint = FarmerPrimary)
                        }
                    }
                }
            }
        }

        // SKILL LEVEL
        item {
            Text("Skill Level Required", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                listOf("Skilled", "Semi-skilled", "Unskilled", "Any").forEach { lvl ->
                    FilterChip(
                        selected = skillLevel == lvl,
                        onClick = { onSkillLevelChange(lvl) },
                        label = { Text(lvl, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // EXPERIENCE REQUIRED
        item {
            Text("Experience Required", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                listOf("No experience", "1+ year", "2+ years", "3+ years").forEach { exp ->
                    FilterChip(
                        selected = experienceRequired == exp,
                        onClick = { onExperienceChange(exp) },
                        label = { Text(exp, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // REQUIRED SKILLS TAGS
        item {
            Text("Required Specific Skills (Select all that apply)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                items(skillsOptions) { sk ->
                    val isSel = selectedSkills.contains(sk)
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            if (isSel) selectedSkills.remove(sk) else selectedSkills.add(sk)
                        },
                        label = { Text(sk, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onPrevious,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Back", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.5f).height(50.dp)
                ) {
                    Text("Next: Date & Time ➔", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ------------------ STEP 3: DATE & TIME ------------------
@Composable
fun Step3DateTime(
    startDate: String,
    onStartDateChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    workingHours: Int,
    onWorkingHoursChange: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            FormStepIndicator(currentStep = 3, totalSteps = 6, stepTitle = "Step 3 – Date and Time")
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = onStartDateChange,
                    label = { Text("Start Date *") },
                    placeholder = { Text("e.g. 5 September") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = onEndDateChange,
                    label = { Text("End Date *") },
                    placeholder = { Text("e.g. 6 September") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            OutlinedTextField(
                value = startTime,
                onValueChange = onStartTimeChange,
                label = { Text("Start Time *") },
                placeholder = { Text("e.g. 7:00 AM") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Text("Expected Working Hours Per Day", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                listOf(6, 7, 8, 9, 10).forEach { hrs ->
                    FilterChip(
                        selected = workingHours == hrs,
                        onClick = { onWorkingHoursChange(hrs) },
                        label = { Text("$hrs Hours/day", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onPrevious,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Back", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.5f).height(50.dp)
                ) {
                    Text("Next: Payment ➔", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ------------------ STEP 4: PAYMENT ------------------
@Composable
fun Step4Payment(
    wageType: String,
    onWageTypeChange: (String) -> Unit,
    wageAmount: String,
    onWageAmountChange: (String) -> Unit,
    paymentTerms: String,
    onPaymentTermsChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            FormStepIndicator(currentStep = 4, totalSteps = 6, stepTitle = "Step 4 – Payment & Wage")
        }

        item {
            Text("Wage Type *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                listOf("Per Day", "Per Hour", "Fixed Job Amount").forEach { type ->
                    FilterChip(
                        selected = wageType == type,
                        onClick = { onWageTypeChange(type) },
                        label = { Text(type, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = wageAmount,
                onValueChange = onWageAmountChange,
                label = { Text("Wage Amount (₹ per worker) *") },
                placeholder = { Text("e.g. 500") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Text("Payment Terms", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                listOf("Daily Cash", "End of Job", "UPI / Direct Transfer").forEach { term ->
                    FilterChip(
                        selected = paymentTerms == term,
                        onClick = { onPaymentTermsChange(term) },
                        label = { Text(term, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onPrevious,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Back", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.5f).height(50.dp)
                ) {
                    Text("Next: Location ➔", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ------------------ STEP 5: LOCATION ------------------
@Composable
fun Step5Location(
    village: String,
    onVillageChange: (String) -> Unit,
    taluka: String,
    onTalukaChange: (String) -> Unit,
    district: String,
    farmLocation: String,
    onFarmLocationChange: (String) -> Unit,
    searchRadiusKm: Int,
    onRadiusChange: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            FormStepIndicator(currentStep = 5, totalSteps = 6, stepTitle = "Step 5 – Farm Location")
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = village,
                    onValueChange = onVillageChange,
                    label = { Text("Village *") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = taluka,
                    onValueChange = onTalukaChange,
                    label = { Text("Taluka *") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            OutlinedTextField(
                value = farmLocation,
                onValueChange = onFarmLocationChange,
                label = { Text("Farm Location / Survey Landmark *") },
                placeholder = { Text("e.g. Plot No. 4, Canal Road, Near Primary School") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Text("Labour Search Radius from Farm", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                listOf(5, 10, 15, 25).forEach { radius ->
                    FilterChip(
                        selected = searchRadiusKm == radius,
                        onClick = { onRadiusChange(radius) },
                        label = { Text("$radius km", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onPrevious,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Back", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.5f).height(50.dp)
                ) {
                    Text("Next: Additional ➔", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ------------------ STEP 6: ADDITIONAL REQUIREMENTS ------------------
@Composable
fun Step6Additional(
    specialInstructions: String,
    onSpecialChange: (String) -> Unit,
    requiredEquipment: String,
    onEquipmentChange: (String) -> Unit,
    foodProvided: Boolean,
    onFoodChange: (Boolean) -> Unit,
    transportProvided: Boolean,
    onTransportChange: (Boolean) -> Unit,
    otherRequirements: String,
    onOtherChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            FormStepIndicator(currentStep = 6, totalSteps = 6, stepTitle = "Step 6 – Additional Requirements")
        }

        item {
            OutlinedTextField(
                value = specialInstructions,
                onValueChange = onSpecialChange,
                label = { Text("Special Instructions") },
                placeholder = { Text("e.g. Experienced sugarcane harvesting workers preferred") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = requiredEquipment,
                onValueChange = onEquipmentChange,
                label = { Text("Required Equipment / Tools") },
                placeholder = { Text("e.g. Sickles (Koyta), Gloves, Battery Sprayer") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // FOOD & TRANSPORT SWITCHES
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🍱 Food Provided", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Lunch / Snacks provided by farmer", fontSize = 11.sp, color = FarmerTextSecondary)
                        }
                        Switch(
                            checked = foodProvided,
                            onCheckedChange = onFoodChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FarmerPrimary)
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🚜 Transportation Provided", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Pickup from ST Stand / Village center", fontSize = 11.sp, color = FarmerTextSecondary)
                        }
                        Switch(
                            checked = transportProvided,
                            onCheckedChange = onTransportChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FarmerPrimary)
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = otherRequirements,
                onValueChange = onOtherChange,
                label = { Text("Other Requirements / Notes") },
                placeholder = { Text("e.g. Morning tea and lunch will be served in the farm.") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onPrevious,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Back", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.5f).height(50.dp)
                ) {
                    Text("Review Requirement ➔", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ------------------ STEP 7: PREVIEW BEFORE POSTING ------------------
@Composable
fun Step7Preview(
    workType: String,
    crop: String,
    description: String,
    workersRequired: Int,
    skillLevel: String,
    experienceRequired: String,
    startDate: String,
    endDate: String,
    startTime: String,
    workingHours: Int,
    wageType: String,
    wageAmount: Double,
    paymentTerms: String,
    village: String,
    taluka: String,
    farmLocation: String,
    searchRadiusKm: Int,
    foodProvided: Boolean,
    transportProvided: Boolean,
    specialInstructions: String,
    onEdit: () -> Unit,
    onPostRequirement: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            Text("Preview Labour Requirement", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Text("Please review all details before searching for nearby labour:", fontSize = 12.sp, color = FarmerTextSecondary)
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🌾 $workType", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            Text("Crop: $crop", fontSize = 13.sp, color = FarmerTextSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FarmerLightBg)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("$workersRequired Workers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                        }
                    }

                    Divider(color = Color(0xFFF1F5F9))

                    Text("📅 Dates: $startDate - $endDate", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FarmerTextPrimary)
                    Text("⏰ Time: $startTime ($workingHours hrs/day)", fontSize = 13.sp, color = FarmerTextSecondary)
                    Text("💰 Wage: ₹${wageAmount.toInt()} / worker ($wageType) • $paymentTerms", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                    Text("📍 Farm: $farmLocation, $village, $taluka (Radius: $searchRadiusKm km)", fontSize = 13.sp, color = FarmerTextSecondary)
                    Text("👨‍🌾 Required Skill: $skillLevel • Exp: $experienceRequired", fontSize = 12.sp, color = FarmerTextSecondary)

                    if (foodProvided || transportProvided) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (foodProvided) Text("🍱 Food Provided", fontSize = 11.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                            if (transportProvided) Text("🚜 Transport Provided", fontSize = 11.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (specialInstructions.isNotBlank()) {
                        Text("📝 Note: $specialInstructions", fontSize = 12.sp, color = FarmerTextSecondary)
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Form", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onPostRequirement,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.6f).height(52.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Find Suitable Labour", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ------------------ STEP 8: FINDING & SELECTING SUITABLE LABOUR ------------------
@Composable
fun Step8MatchAndSelectLabour(
    requirement: LabourRequirement,
    selectedWorkerIds: MutableList<String>,
    onToggleWorker: (String) -> Unit,
    onSendRequests: (String, List<String>) -> Unit,
    onBackToForm: () -> Unit
) {
    val matchedWorkers = remember(requirement) {
        AgroWorldLabourRepository.findMatchingWorkers(requirement)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        // MATCH HEADER BANNER
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FarmerLightBg),
                border = BorderStroke(1.dp, FarmerSecondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "✨ ${matchedWorkers.size} Suitable Workers Found Near Your Farm",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmerPrimary
                    )
                    Text(
                        text = "Matched within ${requirement.searchRadiusKm} km radius based on skill (${requirement.workType}), experience & availability.",
                        fontSize = 12.sp,
                        color = FarmerTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Required: ${requirement.workersRequired} | Selected: ${selectedWorkerIds.size} / ${requirement.workersRequired}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmerTextPrimary
                        )

                        if (selectedWorkerIds.size < requirement.workersRequired) {
                            Text(
                                text = "Select ${requirement.workersRequired - selectedWorkerIds.size} more",
                                fontSize = 11.sp,
                                color = Color(0xFFC62828),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // IF FEWER WORKERS FOUND
        if (matchedWorkers.size < requirement.workersRequired) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ Notice: Only ${matchedWorkers.size} of ${requirement.workersRequired} workers are currently available in your immediate radius. You can send requests to available workers now and find remaining workers later.",
                        fontSize = 12.sp,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // LIST OF MATCHED WORKERS
        items(matchedWorkers) { worker ->
            val isSelected = selectedWorkerIds.contains(worker.id)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFF1F8E9) else Color.White),
                border = BorderStroke(1.dp, if (isSelected) FarmerPrimary else Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleWorker(worker.id) }
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(FarmerPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(worker.avatarEmoji, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(worker.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                                Text(worker.squadName, fontSize = 12.sp, color = FarmerTextSecondary)
                            }
                        }

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleWorker(worker.id) },
                            colors = CheckboxDefaults.colors(checkedColor = FarmerPrimary)
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("⭐ ${worker.rating} (${worker.totalReviews} reviews)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerAccent)
                        Text("📍 ${worker.distanceKm} km • ${worker.village}", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.SemiBold)
                    }

                    Text("Skills: ${worker.skills.joinToString(", ")}", fontSize = 12.sp, color = FarmerTextSecondary)
                    Text("Experience: ${worker.experienceYears} Years • Level: ${worker.skillLevel}", fontSize = 12.sp, color = FarmerTextSecondary)
                    Text("Availability: ${worker.availableDates} • Status: Available", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.Bold)
                    Text("Wage: ₹${worker.dailyWage.toInt()} / worker / day", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                }
            }
        }

        // ACTION BUTTONS (REVIEW & SEND REQUESTS)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        onSendRequests(requirement.id, selectedWorkerIds.toList())
                    },
                    enabled = selectedWorkerIds.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "SEND REQUESTS (${selectedWorkerIds.size} WORKERS)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onBackToForm,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Requirement Details", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ------------------ STEP 9: REQUEST SENT CONFIRMATION ------------------
@Composable
fun Step9RequestSentSuccess(
    workersCount: Int,
    onViewMyRequests: () -> Unit,
    onNewRequirement: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(FarmerLightBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = FarmerPrimary, modifier = Modifier.size(54.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Hiring Requests Sent! 🎉", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Hiring requests have been sent to $workersCount selected workers.\n\nYou will receive a notification as soon as each worker accepts or responds.",
            fontSize = 14.sp,
            color = FarmerTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onViewMyRequests,
            colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Track in My Labour Requests ➔", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNewRequirement) {
            Text("Post Another Requirement", color = FarmerPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

// ------------------ MY LABOUR REQUESTS (STEP 11, 12, 13) ------------------
@Composable
fun FarmerMyLabourRequestsView(
    requirements: List<LabourRequirement>,
    onSelectRequirement: (LabourRequirement) -> Unit,
    onConfirmLabour: (String) -> Unit,
    onFindReplacements: (LabourRequirement) -> Unit,
    onConfirmJobDone: (String) -> Unit,
    onRateLabour: (String, String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredList = when (selectedFilter) {
        "Active" -> requirements.filter { it.status == "Request Sent" || it.status == "Finding Labour" || it.status == "Accepted" }
        "Confirmed" -> requirements.filter { it.status == "Confirmed" || it.status == "Scheduled" || it.status == "Work Started" }
        "Completed" -> requirements.filter { it.status == "Completed" || it.status == "Work Completed" }
        else -> requirements
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            Text("My Posted Labour Requirements (${requirements.size})", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Text("Track worker responses, confirmations, live field progress and ratings:", fontSize = 12.sp, color = FarmerTextSecondary)
        }

        // FILTER TABS
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("All", "Active", "Confirmed", "Completed").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FarmerPrimary,
                            selectedLabelColor = Color.White
                        )
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
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("👨‍🌾", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Requirements in '$selectedFilter'", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    }
                }
            }
        } else {
            items(filteredList) { req ->
                FarmerRequirementCard(
                    requirement = req,
                    onConfirmLabour = { onConfirmLabour(req.id) },
                    onFindReplacements = { onFindReplacements(req) },
                    onConfirmJobDone = { onConfirmJobDone(req.id) },
                    onRateLabour = { onRateLabour(req.id, "Maruti Farm Labour Squad") }
                )
            }
        }
    }
}

@Composable
fun FarmerRequirementCard(
    requirement: LabourRequirement,
    onConfirmLabour: () -> Unit,
    onFindReplacements: () -> Unit,
    onConfirmJobDone: () -> Unit,
    onRateLabour: () -> Unit
) {
    val confirmedCount = requirement.workerIdsConfirmed.size
    val acceptedCount = requirement.workerIdsAccepted.size
    val totalRequired = requirement.workersRequired

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // HEADER & STATUS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("🌾 ${requirement.workType}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    Text("Crop: ${requirement.crop} • Posted: ${requirement.createdAt}", fontSize = 12.sp, color = FarmerTextSecondary)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (requirement.status) {
                                "Accepted" -> Color(0xFFE8F5E9)
                                "Confirmed", "Scheduled" -> Color(0xFFE0F2FE)
                                "Work Started" -> Color(0xFFFFF3E0)
                                "Completed" -> Color(0xFFE8F5E9)
                                else -> Color(0xFFF1F5F9)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        requirement.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (requirement.status) {
                            "Accepted" -> FarmerPrimary
                            "Confirmed", "Scheduled" -> Color(0xFF0284C7)
                            "Work Started" -> Color(0xFFE65100)
                            "Completed" -> FarmerPrimary
                            else -> FarmerTextSecondary
                        }
                    )
                }
            }

            // 9-STAGE LIFECYCLE TRACKER
            RequirementLifecycleStepper(status = requirement.status)

            // WORKER PROGRESS INDICATOR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Workers Progress", fontSize = 11.sp, color = FarmerTextSecondary)
                    Text(
                        "$acceptedCount / $totalRequired Workers Accepted",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (acceptedCount >= totalRequired) FarmerPrimary else Color(0xFFE65100)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("💰 ₹${requirement.wageAmount.toInt()} / worker / day", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    Text("📍 ${requirement.village} (${requirement.farmLocation})", fontSize = 11.sp, color = FarmerTextSecondary)
                }
            }

            // WORKER RESPONSES BREAKDOWN
            if (requirement.workerIdsRequested.isNotEmpty()) {
                Text("Worker Response Status:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    requirement.workerIdsRequested.forEach { workerId ->
                        val worker = AgroWorldLabourRepository.availableWorkers.find { it.id == workerId }
                        val isAccepted = requirement.workerIdsAccepted.contains(workerId)
                        val isRejected = requirement.workerIdsRejected.contains(workerId)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isAccepted) Color(0xFFE8F5E9) else if (isRejected) Color(0xFFFFEBEE) else Color(0xFFF8FAFC))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("👨‍🌾 ${worker?.name ?: "Worker"} (${worker?.phone ?: ""})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (isAccepted) "Accepted ✅" else if (isRejected) "Rejected ❌" else "Pending ⏳",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAccepted) FarmerPrimary else if (isRejected) Color(0xFFC62828) else Color(0xFFE65100)
                            )
                        }
                    }
                }
            }

            // ACTION BUTTONS BASED ON STAGE
            when (requirement.status) {
                "Accepted" -> {
                    Button(
                        onClick = onConfirmLabour,
                        colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Confirm", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("GIVE FINAL CONFIRMATION (नक्की करा)", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                "Request Sent" -> {
                    if (requirement.workerIdsRejected.isNotEmpty()) {
                        OutlinedButton(
                            onClick = onFindReplacements,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE65100)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Find Replacement Workers 🔍", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "Work Completed" -> {
                    Button(
                        onClick = onConfirmJobDone,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CONFIRM JOB COMPLETION & CLOSE", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                "Completed" -> {
                    OutlinedButton(
                        onClick = onRateLabour,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmerAccent),
                        border = BorderStroke(1.dp, FarmerAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Rate", tint = FarmerAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rate & Review Labour Squad ⭐", fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                    }
                }
            }
        }
    }
}

// ------------------ 9-STAGE LIFECYCLE STEPPER ------------------
@Composable
fun RequirementLifecycleStepper(status: String) {
    val stages = listOf("Posted", "Finding", "Sent", "Accepted", "Confirmed", "Scheduled", "Started", "Done", "Completed")

    val currentIdx = when (status) {
        "Requirement Posted" -> 0
        "Finding Labour" -> 1
        "Request Sent" -> 2
        "Accepted" -> 3
        "Confirmed" -> 4
        "Scheduled" -> 5
        "Work Started" -> 6
        "Work Completed" -> 7
        "Completed" -> 8
        else -> 2
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 5) {
                val isReached = currentIdx >= i * 2
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isReached) FarmerPrimary else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isReached) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                    }
                }
                if (i < 4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (currentIdx > i * 2) FarmerPrimary else Color(0xFFE2E8F0))
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Posted", fontSize = 9.sp, color = FarmerTextSecondary)
            Text("Requests", fontSize = 9.sp, color = FarmerTextSecondary)
            Text("Confirmed", fontSize = 9.sp, color = FarmerTextSecondary)
            Text("Working", fontSize = 9.sp, color = FarmerTextSecondary)
            Text("Done", fontSize = 9.sp, color = FarmerTextSecondary)
        }
    }
}

// ------------------ CONFIRMED JOBS VIEW ------------------
@Composable
fun FarmerConfirmedJobsView(
    requirements: List<LabourRequirement>,
    onOpenDirections: (String) -> Unit,
    onCallWorker: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            Text("Confirmed & Scheduled Labour Jobs (${requirements.size})", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Text("Scheduled squads ready for upcoming field operations:", fontSize = 12.sp, color = FarmerTextSecondary)
        }

        if (requirements.isEmpty()) {
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
                        Text("📅", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No confirmed jobs scheduled yet", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                        Text("Once you confirm accepted worker requests, they appear here.", fontSize = 12.sp, color = FarmerTextSecondary, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(requirements) { req ->
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
                            Text("🌾 ${req.workType} (${req.crop})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FarmerLightBg)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Scheduled ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
                            }
                        }

                        Text("📅 Date: ${req.startDate} - ${req.endDate} • ⏰ ${req.startTime}", fontSize = 12.sp, color = FarmerPrimary, fontWeight = FontWeight.SemiBold)
                        Text("📍 Farm: ${req.farmLocation}, ${req.village}", fontSize = 12.sp, color = FarmerTextSecondary)
                        Text("👥 Squad Size: ${req.workerIdsConfirmed.size} Workers • Wage: ₹${req.wageAmount.toInt()} / day", fontSize = 12.sp, color = FarmerTextPrimary)

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { onOpenDirections(req.farmLocation) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = "Map", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Farm Map", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onCallWorker("+91 98221 44556") },
                                colors = ButtonDefaults.buttonColors(containerColor = FarmerPrimary),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Squad", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------ STEP INDICATOR COMPONENT ------------------
@Composable
fun FormStepIndicator(currentStep: Int, totalSteps: Int, stepTitle: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stepTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FarmerTextPrimary)
            Text("$currentStep of $totalSteps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmerPrimary)
        }

        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = FarmerPrimary,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
