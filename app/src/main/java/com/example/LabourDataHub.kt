package com.example

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ------------------ LABOUR SYSTEM DATA MODELS ------------------

/**
 * Worker / Squad Profile Model
 */
data class LabourWorker(
    val id: String,
    val name: String,
    val avatarEmoji: String = "👨‍🌾",
    val phone: String,
    val squadName: String,
    val village: String,
    val taluka: String,
    val district: String = "Pune",
    val distanceKm: Double,
    val primarySkill: String,
    val skills: List<String>,
    val experienceYears: Int,
    val skillLevel: String, // "Skilled", "Semi-skilled", "Unskilled"
    val rating: Double,
    val totalReviews: Int,
    val completedJobs: Int,
    val dailyWage: Double,
    var isAvailable: Boolean = true,
    var availableDates: String = "All Days (Available)",
    var preferredWork: List<String> = listOf("Sugarcane Harvesting", "Onion Harvesting", "Weeding", "Spraying"),
    var workingRadiusKm: Int = 15
)

/**
 * Farmer's Posted Labour Requirement
 */
data class LabourRequirement(
    val id: String,
    val farmerId: String = "FARMER_01",
    val farmerName: String,
    val farmerPhone: String,
    val workType: String,
    val customWorkType: String? = null,
    val crop: String,
    val description: String,
    val workersRequired: Int,
    val skillLevel: String, // "Skilled", "Semi-skilled", "Unskilled", "Any"
    val experienceRequired: String, // "No experience required", "1+ year", "2+ years", "3+ years", "Custom"
    val requiredSkills: List<String>,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val workingHoursPerDay: Int,
    val wageType: String, // "Per Day", "Per Hour", "Fixed Job Amount"
    val wageAmount: Double,
    val paymentTerms: String, // "Daily Cash", "End of Job", "UPI / Direct Transfer"
    val village: String,
    val taluka: String,
    val district: String = "Pune",
    val farmLocation: String,
    val searchRadiusKm: Int = 10,
    val specialInstructions: String = "",
    val requiredEquipment: String = "",
    val foodProvided: Boolean = true,
    val transportProvided: Boolean = false,
    val otherRequirements: String = "",
    var status: String = "Finding Labour", // "Requirement Posted", "Finding Labour", "Request Sent", "Accepted", "Confirmed", "Scheduled", "Work Started", "Work Completed", "Completed", "Cancelled"
    val workerIdsRequested: MutableList<String> = mutableListOf(),
    val workerIdsAccepted: MutableList<String> = mutableListOf(),
    val workerIdsRejected: MutableList<String> = mutableListOf(),
    val workerIdsConfirmed: MutableList<String> = mutableListOf(),
    val createdAt: String = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date()),
    var farmerRating: Double? = null,
    var farmerReview: String? = null
)

/**
 * Individual Job Request sent to a worker/squad
 */
data class LabourJobItem(
    val id: String,
    val requirementId: String,
    val farmerId: String,
    val farmerName: String,
    val farmerPhone: String,
    val labourId: String,
    val labourName: String,
    val labourPhone: String,
    val workType: String,
    val crop: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val workingHours: Int,
    val wage: Double,
    val wageType: String,
    val village: String,
    val taluka: String,
    val distanceKm: Double,
    val foodProvided: Boolean,
    val transportProvided: Boolean,
    val specialInstructions: String,
    var status: String = "Pending", // "Pending", "Accepted", "Rejected", "Confirmed", "Scheduled", "In Progress", "Completed", "Cancelled"
    val sentAt: String = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date()),
    var respondedAt: String? = null,
    var startedAt: String? = null,
    var completedAt: String? = null,
    var farmerRating: Double? = null,
    var labourRating: Double? = null,
    var farmerReview: String? = null,
    var labourReview: String? = null
)

/**
 * Labour Review Model
 */
data class LabourReview(
    val id: String,
    val jobId: String,
    val fromUserRole: String,
    val toUserRole: String,
    val fromName: String,
    val toName: String,
    val rating: Double,
    val reviewComment: String,
    val date: String = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date())
)

// ------------------ SINGLETON REPOSITORY ------------------
object AgroWorldLabourRepository {

    // Available Workers Database
    val availableWorkers = mutableStateListOf(
        LabourWorker(
            id = "lab_1",
            name = "Ramesh Ghadge",
            avatarEmoji = "👨‍🌾",
            phone = "+91 98221 44556",
            squadName = "Maruti Farm Labour Squad",
            village = "Narayangaon",
            taluka = "Junnar",
            distanceKm = 3.2,
            primarySkill = "Sugarcane Harvesting",
            skills = listOf("Sugarcane Harvesting", "Sugarcane Cutting", "Loading", "Heavy Field Work"),
            experienceYears = 5,
            skillLevel = "Skilled",
            rating = 4.8,
            totalReviews = 38,
            completedJobs = 64,
            dailyWage = 500.0,
            isAvailable = true,
            availableDates = "5 Sept - 10 Sept",
            preferredWork = listOf("Sugarcane Harvesting", "Sugarcane Cutting", "Loading"),
            workingRadiusKm = 15
        ),
        LabourWorker(
            id = "lab_2",
            name = "Suresh Mandhare",
            avatarEmoji = "👨‍🌾",
            phone = "+91 94220 12345",
            squadName = "Jai Kisan Shramik Group",
            village = "Ozar",
            taluka = "Junnar",
            distanceKm = 5.1,
            primarySkill = "Sugarcane Harvesting",
            skills = listOf("Sugarcane Harvesting", "Onion Harvesting", "Weeding", "Sorting"),
            experienceYears = 3,
            skillLevel = "Semi-skilled",
            rating = 4.5,
            totalReviews = 21,
            completedJobs = 35,
            dailyWage = 480.0,
            isAvailable = true,
            availableDates = "5 Sept - 12 Sept",
            preferredWork = listOf("Sugarcane Harvesting", "Weeding", "Sowing"),
            workingRadiusKm = 12
        ),
        LabourWorker(
            id = "lab_3",
            name = "Mahesh Patil",
            avatarEmoji = "👨‍🌾",
            phone = "+91 98901 23456",
            squadName = "Sahyadri Majur Mandal",
            village = "Alephata",
            taluka = "Junnar",
            distanceKm = 6.4,
            primarySkill = "Sugarcane Harvesting",
            skills = listOf("Sugarcane Harvesting", "Pesticide Spraying", "Tractor Operation", "Harvesting"),
            experienceYears = 6,
            skillLevel = "Skilled",
            rating = 4.9,
            totalReviews = 52,
            completedJobs = 88,
            dailyWage = 500.0,
            isAvailable = true,
            availableDates = "5 Sept - 8 Sept",
            preferredWork = listOf("Sugarcane Harvesting", "Spraying", "Tractor Operation"),
            workingRadiusKm = 20
        ),
        LabourWorker(
            id = "lab_4",
            name = "Dnyaneshwar Kale",
            avatarEmoji = "👨‍🌾",
            phone = "+91 97665 88990",
            squadName = "Shivaji Farm Labourers",
            village = "Manchar",
            taluka = "Ambegaon",
            distanceKm = 8.5,
            primarySkill = "Sugarcane Harvesting",
            skills = listOf("Sugarcane Harvesting", "Sugarcane Cutting", "Weeding", "Planting"),
            experienceYears = 4,
            skillLevel = "Skilled",
            rating = 4.7,
            totalReviews = 29,
            completedJobs = 45,
            dailyWage = 490.0,
            isAvailable = true,
            availableDates = "5 Sept - 15 Sept",
            preferredWork = listOf("Sugarcane Harvesting", "Planting"),
            workingRadiusKm = 15
        ),
        LabourWorker(
            id = "lab_5",
            name = "Ankush Thorat",
            avatarEmoji = "👨‍🌾",
            phone = "+91 94210 33441",
            squadName = "Malhar Agro Workers",
            village = "Baramati",
            taluka = "Baramati",
            distanceKm = 4.2,
            primarySkill = "Sugarcane Harvesting",
            skills = listOf("Sugarcane Harvesting", "Sugarcane Cutting", "Drip Laying", "Fertilizer Application"),
            experienceYears = 5,
            skillLevel = "Skilled",
            rating = 4.8,
            totalReviews = 41,
            completedJobs = 72,
            dailyWage = 500.0,
            isAvailable = true,
            availableDates = "5 Sept - 10 Sept",
            preferredWork = listOf("Sugarcane Harvesting", "Drip Laying"),
            workingRadiusKm = 18
        ),
        LabourWorker(
            id = "lab_6",
            name = "Pandurang Shinde",
            avatarEmoji = "👨‍🌾",
            phone = "+91 98223 99881",
            squadName = "Kranti Shetmajur Sangh",
            village = "Narayangaon",
            taluka = "Junnar",
            distanceKm = 2.8,
            primarySkill = "Sugarcane Harvesting",
            skills = listOf("Sugarcane Harvesting", "Weeding", "Fertilizer Application", "Irrigation"),
            experienceYears = 2,
            skillLevel = "Semi-skilled",
            rating = 4.4,
            totalReviews = 16,
            completedJobs = 28,
            dailyWage = 470.0,
            isAvailable = true,
            availableDates = "5 Sept - 7 Sept",
            preferredWork = listOf("Sugarcane Harvesting", "Weeding"),
            workingRadiusKm = 10
        ),
        LabourWorker(
            id = "lab_7",
            name = "Kisan Jadhav",
            avatarEmoji = "👨‍🌾",
            phone = "+91 98501 22334",
            squadName = "Jadhav Majur Toli",
            village = "Ale",
            taluka = "Junnar",
            distanceKm = 7.1,
            primarySkill = "Weeding & Planting",
            skills = listOf("Weeding", "Planting", "Onion Harvesting", "Vegetable Picking"),
            experienceYears = 3,
            skillLevel = "Semi-skilled",
            rating = 4.6,
            totalReviews = 19,
            completedJobs = 31,
            dailyWage = 450.0,
            isAvailable = true,
            availableDates = "Available Any Day",
            preferredWork = listOf("Weeding", "Planting", "Onion Harvesting"),
            workingRadiusKm = 12
        ),
        LabourWorker(
            id = "lab_8",
            name = "Ganesh Bhor",
            avatarEmoji = "👨‍🌾",
            phone = "+91 97632 11223",
            squadName = "Bhor Agro Service",
            village = "Ozar",
            taluka = "Junnar",
            distanceKm = 4.9,
            primarySkill = "Pesticide Spraying",
            skills = listOf("Pesticide Spraying", "Battery Sprayer", "Chemical Safety", "Fertilizer Application"),
            experienceYears = 5,
            skillLevel = "Skilled",
            rating = 4.9,
            totalReviews = 46,
            completedJobs = 80,
            dailyWage = 520.0,
            isAvailable = true,
            availableDates = "Available Today & Tomorrow",
            preferredWork = listOf("Pesticide Spraying", "Fertilizer Application"),
            workingRadiusKm = 15
        )
    )

    // Requirements Database
    val requirements = mutableStateListOf(
        LabourRequirement(
            id = "req_101",
            farmerId = "FARMER_01",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            workType = "Sugarcane Harvesting",
            customWorkType = null,
            crop = "Sugarcane",
            description = "Need experienced cane cutters for 2 acres Co-86032 variety. Clean cutting and trailer stacking required.",
            workersRequired = 8,
            skillLevel = "Skilled",
            experienceRequired = "2+ years",
            requiredSkills = listOf("Sugarcane Harvesting", "Cutting", "Loading"),
            startDate = "5 September",
            endDate = "6 September",
            startTime = "7:00 AM",
            workingHoursPerDay = 8,
            wageType = "Per Day",
            wageAmount = 500.0,
            paymentTerms = "Daily Cash",
            village = "Narayangaon",
            taluka = "Junnar",
            district = "Pune",
            farmLocation = "Plot No. 4, Canal Road",
            searchRadiusKm = 10,
            specialInstructions = "Experienced sugarcane harvesting workers preferred. Sharp koyta/sickles required.",
            requiredEquipment = "Sickles (Koyta), Gloves",
            foodProvided = true,
            transportProvided = false,
            otherRequirements = "Morning tea and lunch will be served in the farm.",
            status = "Accepted",
            workerIdsRequested = mutableListOf("lab_1", "lab_2", "lab_3", "lab_4", "lab_5", "lab_6"),
            workerIdsAccepted = mutableListOf("lab_1", "lab_2", "lab_3", "lab_4", "lab_5", "lab_6"),
            workerIdsRejected = mutableListOf(),
            workerIdsConfirmed = mutableListOf("lab_1", "lab_2", "lab_3", "lab_4", "lab_5", "lab_6"),
            createdAt = "Yesterday"
        ),
        LabourRequirement(
            id = "req_102",
            farmerId = "FARMER_01",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            workType = "Weeding",
            customWorkType = null,
            crop = "Pune Red Onions",
            description = "Manual hand weeding for 1.5 acres red onion nursery plot.",
            workersRequired = 4,
            skillLevel = "Semi-skilled",
            experienceRequired = "1+ year",
            requiredSkills = listOf("Weeding", "Khurpani"),
            startDate = "8 September",
            endDate = "8 September",
            startTime = "7:30 AM",
            workingHoursPerDay = 7,
            wageType = "Per Day",
            wageAmount = 450.0,
            paymentTerms = "Daily Cash",
            village = "Narayangaon",
            taluka = "Junnar",
            district = "Pune",
            farmLocation = "Survey No 88, Near Temple",
            searchRadiusKm = 10,
            specialInstructions = "Careful not to damage delicate onion bulb tops.",
            requiredEquipment = "Hand weeding tool (Khurpi)",
            foodProvided = true,
            transportProvided = true,
            otherRequirements = "Pickup available from Narayangaon ST Stand.",
            status = "Confirmed",
            workerIdsRequested = mutableListOf("lab_7", "lab_2"),
            workerIdsAccepted = mutableListOf("lab_7", "lab_2"),
            workerIdsRejected = mutableListOf(),
            workerIdsConfirmed = mutableListOf("lab_7", "lab_2"),
            createdAt = "2 days ago"
        )
    )

    // Job Requests sent to Labourers
    val jobRequests = mutableStateListOf(
        LabourJobItem(
            id = "job_req_101_1",
            requirementId = "req_101",
            farmerId = "FARMER_01",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            labourId = "lab_1",
            labourName = "Ramesh Ghadge",
            labourPhone = "+91 98221 44556",
            workType = "Sugarcane Harvesting",
            crop = "Sugarcane",
            startDate = "5 September",
            endDate = "6 September",
            startTime = "7:00 AM",
            workingHours = 8,
            wage = 500.0,
            wageType = "Per Day",
            village = "Narayangaon",
            taluka = "Junnar",
            distanceKm = 3.2,
            foodProvided = true,
            transportProvided = false,
            specialInstructions = "Experienced sugarcane harvesting workers preferred. Morning tea & lunch provided.",
            status = "Accepted",
            sentAt = "Yesterday",
            respondedAt = "1 hour ago"
        ),
        LabourJobItem(
            id = "job_req_101_2",
            requirementId = "req_101",
            farmerId = "FARMER_01",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            labourId = "lab_2",
            labourName = "Suresh Mandhare",
            labourPhone = "+91 94220 12345",
            workType = "Sugarcane Harvesting",
            crop = "Sugarcane",
            startDate = "5 September",
            endDate = "6 September",
            startTime = "7:00 AM",
            workingHours = 8,
            wage = 500.0,
            wageType = "Per Day",
            village = "Narayangaon",
            taluka = "Junnar",
            distanceKm = 5.1,
            foodProvided = true,
            transportProvided = false,
            specialInstructions = "Experienced sugarcane harvesting workers preferred. Morning tea & lunch provided.",
            status = "Accepted",
            sentAt = "Yesterday",
            respondedAt = "30 mins ago"
        ),
        LabourJobItem(
            id = "job_req_102_1",
            requirementId = "req_102",
            farmerId = "FARMER_01",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            labourId = "lab_7",
            labourName = "Kisan Jadhav",
            labourPhone = "+91 98501 22334",
            workType = "Weeding",
            crop = "Pune Red Onions",
            startDate = "8 September",
            endDate = "8 September",
            startTime = "7:30 AM",
            workingHours = 7,
            wage = 450.0,
            wageType = "Per Day",
            village = "Narayangaon",
            taluka = "Junnar",
            distanceKm = 7.1,
            foodProvided = true,
            transportProvided = true,
            specialInstructions = "Pickup available from ST stand. Hand weeding onion nursery.",
            status = "Confirmed",
            sentAt = "2 days ago",
            respondedAt = "1 day ago"
        )
    )

    // History Reviews
    val reviews = mutableStateListOf(
        LabourReview(
            id = "rev_1",
            jobId = "prev_job_1",
            fromUserRole = "farmer",
            toUserRole = "labour",
            fromName = "Ramesh Patil",
            toName = "Ramesh Ghadge",
            rating = 5.0,
            reviewComment = "Excellent work on sugarcane cutting. Punctual and very efficient team!",
            date = "12 Aug 2026"
        ),
        LabourReview(
            id = "rev_2",
            jobId = "prev_job_1",
            fromUserRole = "labour",
            toUserRole = "farmer",
            fromName = "Ramesh Ghadge",
            toName = "Ramesh Patil",
            rating = 5.0,
            reviewComment = "Prompt cash payment and great food provided on the farm. Highly recommended farmer!",
            date = "12 Aug 2026"
        )
    )

    // ------------------ BUSINESS LOGIC FUNCTIONS ------------------

    /**
     * Automatic Matching Engine: Matches workers based on Skill, Work Type, Availability, Distance, Experience, Rating
     */
    fun findMatchingWorkers(requirement: LabourRequirement): List<LabourWorker> {
        val targetWork = requirement.workType.lowercase()
        val radius = requirement.searchRadiusKm.toDouble()

        return availableWorkers.filter { worker ->
            // 1. Distance check
            val distanceOk = worker.distanceKm <= (radius + 5.0) // allow slight margin

            // 2. Skill & Work type match
            val matchesSkill = worker.skills.any { skill ->
                targetWork.contains(skill.lowercase()) ||
                skill.lowercase().contains(targetWork) ||
                requirement.requiredSkills.any { reqSkill -> skill.contains(reqSkill, ignoreCase = true) }
            } || targetWork.contains("other") || worker.skills.contains("General Farm Work")

            // 3. Availability check
            val isAvailable = worker.isAvailable

            distanceOk && (matchesSkill || worker.experienceYears >= 2) && isAvailable
        }.sortedWith(
            compareByDescending<LabourWorker> { it.rating }
                .thenBy { it.distanceKm }
                .thenByDescending { it.experienceYears }
        )
    }

    /**
     * Post a new requirement and return created requirement
     */
    fun postRequirement(req: LabourRequirement): LabourRequirement {
        requirements.add(0, req)
        return req
    }

    /**
     * Farmer sends hiring requests to selected workers
     */
    fun sendRequestsToWorkers(requirementId: String, selectedWorkerIds: List<String>) {
        val reqIndex = requirements.indexOfFirst { it.id == requirementId }
        if (reqIndex == -1) return

        val req = requirements[reqIndex]
        req.workerIdsRequested.clear()
        req.workerIdsRequested.addAll(selectedWorkerIds)
        req.status = "Request Sent"

        // Create individual job request entries for each worker
        selectedWorkerIds.forEach { workerId ->
            val worker = availableWorkers.find { it.id == workerId }
            if (worker != null) {
                val existingJob = jobRequests.find { it.requirementId == requirementId && it.labourId == workerId }
                if (existingJob == null) {
                    val job = LabourJobItem(
                        id = "job_${requirementId}_${workerId}",
                        requirementId = requirementId,
                        farmerId = req.farmerId,
                        farmerName = req.farmerName,
                        farmerPhone = req.farmerPhone,
                        labourId = worker.id,
                        labourName = worker.name,
                        labourPhone = worker.phone,
                        workType = req.workType,
                        crop = req.crop,
                        startDate = req.startDate,
                        endDate = req.endDate,
                        startTime = req.startTime,
                        workingHours = req.workingHoursPerDay,
                        wage = req.wageAmount,
                        wageType = req.wageType,
                        village = req.village,
                        taluka = req.taluka,
                        distanceKm = worker.distanceKm,
                        foodProvided = req.foodProvided,
                        transportProvided = req.transportProvided,
                        specialInstructions = req.specialInstructions,
                        status = "Pending",
                        sentAt = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date())
                    )
                    jobRequests.add(0, job)
                }
            }
        }
    }

    /**
     * Labour accepts incoming job request
     */
    fun labourAcceptJobRequest(jobId: String): Boolean {
        val jobIndex = jobRequests.indexOfFirst { it.id == jobId }
        if (jobIndex == -1) return false

        val job = jobRequests[jobIndex]
        job.status = "Accepted"
        job.respondedAt = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date())

        // Update corresponding requirement
        val reqIndex = requirements.indexOfFirst { it.id == job.requirementId }
        if (reqIndex != -1) {
            val req = requirements[reqIndex]
            if (!req.workerIdsAccepted.contains(job.labourId)) {
                req.workerIdsAccepted.add(job.labourId)
            }
            req.workerIdsRejected.remove(job.labourId)
            req.status = "Accepted"
        }
        return true
    }

    /**
     * Labour rejects incoming job request
     */
    fun labourRejectJobRequest(jobId: String): Boolean {
        val jobIndex = jobRequests.indexOfFirst { it.id == jobId }
        if (jobIndex == -1) return false

        val job = jobRequests[jobIndex]
        job.status = "Rejected"
        job.respondedAt = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date())

        // Update corresponding requirement
        val reqIndex = requirements.indexOfFirst { it.id == job.requirementId }
        if (reqIndex != -1) {
            val req = requirements[reqIndex]
            req.workerIdsAccepted.remove(job.labourId)
            if (!req.workerIdsRejected.contains(job.labourId)) {
                req.workerIdsRejected.add(job.labourId)
            }
        }
        return true
    }

    /**
     * Farmer gives Final Confirmation for accepted workers
     */
    fun farmerConfirmWorkers(requirementId: String) {
        val reqIndex = requirements.indexOfFirst { it.id == requirementId }
        if (reqIndex == -1) return

        val req = requirements[reqIndex]
        req.workerIdsConfirmed.clear()
        req.workerIdsConfirmed.addAll(req.workerIdsAccepted)
        req.status = "Confirmed"

        // Mark corresponding jobs as Confirmed / Scheduled
        jobRequests.forEachIndexed { index, job ->
            if (job.requirementId == requirementId && job.status == "Accepted") {
                jobRequests[index] = job.copy(status = "Confirmed")
            }
        }
    }

    /**
     * Labour starts work on the job
     */
    fun labourStartJob(jobId: String) {
        val jobIndex = jobRequests.indexOfFirst { it.id == jobId }
        if (jobIndex == -1) return

        val job = jobRequests[jobIndex]
        job.status = "In Progress"
        job.startedAt = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date())

        val reqIndex = requirements.indexOfFirst { it.id == job.requirementId }
        if (reqIndex != -1) {
            requirements[reqIndex].status = "Work Started"
        }
    }

    /**
     * Labour finishes work on the job
     */
    fun labourCompleteJob(jobId: String) {
        val jobIndex = jobRequests.indexOfFirst { it.id == jobId }
        if (jobIndex == -1) return

        val job = jobRequests[jobIndex]
        job.status = "Completed"
        job.completedAt = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date())

        val reqIndex = requirements.indexOfFirst { it.id == job.requirementId }
        if (reqIndex != -1) {
            requirements[reqIndex].status = "Work Completed"
        }
    }

    /**
     * Farmer confirms completion and closes requirement
     */
    fun farmerConfirmCompletion(requirementId: String) {
        val reqIndex = requirements.indexOfFirst { it.id == requirementId }
        if (reqIndex == -1) return

        val req = requirements[reqIndex]
        req.status = "Completed"

        jobRequests.forEachIndexed { index, job ->
            if (job.requirementId == requirementId) {
                jobRequests[index] = job.copy(status = "Completed")
            }
        }
    }

    /**
     * Add Rating & Review
     */
    fun addReview(
        jobId: String,
        fromRole: String,
        toRole: String,
        fromName: String,
        toName: String,
        rating: Double,
        comment: String
    ) {
        reviews.add(
            0,
            LabourReview(
                id = "rev_" + System.currentTimeMillis(),
                jobId = jobId,
                fromUserRole = fromRole,
                toUserRole = toRole,
                fromName = fromName,
                toName = toName,
                rating = rating,
                reviewComment = comment
            )
        )
    }
}
