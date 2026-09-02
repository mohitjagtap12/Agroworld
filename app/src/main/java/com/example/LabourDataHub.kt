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
    val availableWorkers = mutableStateListOf<LabourWorker>()

    // Requirements Database
    val requirements = mutableStateListOf<LabourRequirement>()

    // Job Requests sent to Labourers
    val jobRequests = mutableStateListOf<LabourJobItem>()

    // History Reviews
    val reviews = mutableStateListOf<LabourReview>()

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
