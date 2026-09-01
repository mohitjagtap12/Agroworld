package com.example.labour;

import com.example.model.LabourApplication;
import com.example.model.LabourRequirement;
import com.example.model.LabourWorker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe Central Data Hub for the Labour Hiring module.
 * Preserves the full end-to-end workflow:
 * Post Requirement -> Match Workers -> Send Requests -> Accept/Reject -> Farmer Confirm -> Scheduled -> Completed -> Review.
 */
public class LabourDataHub {

    private static volatile LabourDataHub instance;

    private final CopyOnWriteArrayList<LabourWorker> workers = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<LabourRequirement> requirements = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<LabourApplication> jobApplications = new CopyOnWriteArrayList<>();

    private LabourDataHub() {
        seedLabourData();
    }

    public static LabourDataHub getInstance() {
        if (instance == null) {
            synchronized (LabourDataHub.class) {
                if (instance == null) {
                    instance = new LabourDataHub();
                }
            }
        }
        return instance;
    }

    private void seedLabourData() {
        // Seed Available Workers
        workers.add(new LabourWorker(
                "lab_1", "Ramesh Ghadge", "👨‍🌾", "+91 98221 44556",
                "Maruti Farm Labour Squad", "Narayangaon", "Junnar", "Pune", 3.2,
                "Sugarcane Harvesting", Arrays.asList("Sugarcane Harvesting", "Sugarcane Cutting", "Loading", "Heavy Field Work"),
                5, "Skilled", 4.8, 38, 64, 500.0, true, "5 Sept - 10 Sept",
                Arrays.asList("Sugarcane Harvesting", "Sugarcane Cutting", "Loading"), 15
        ));
        workers.add(new LabourWorker(
                "lab_2", "Suresh Mandhare", "👨‍🌾", "+91 94220 12345",
                "Jai Kisan Shramik Group", "Ozar", "Junnar", "Pune", 5.1,
                "Sugarcane Harvesting", Arrays.asList("Sugarcane Harvesting", "Onion Harvesting", "Weeding", "Sorting"),
                3, "Semi-skilled", 4.5, 21, 35, 480.0, true, "5 Sept - 12 Sept",
                Arrays.asList("Sugarcane Harvesting", "Weeding", "Sowing"), 12
        ));
        workers.add(new LabourWorker(
                "lab_3", "Mahesh Patil", "👨‍🌾", "+91 98901 23456",
                "Sahyadri Majur Mandal", "Alephata", "Junnar", "Pune", 6.4,
                "Sugarcane Harvesting", Arrays.asList("Sugarcane Harvesting", "Pesticide Spraying", "Tractor Operation", "Harvesting"),
                6, "Skilled", 4.9, 52, 88, 500.0, true, "5 Sept - 8 Sept",
                Arrays.asList("Sugarcane Harvesting", "Spraying", "Tractor Operation"), 20
        ));
        workers.add(new LabourWorker(
                "lab_4", "Dnyaneshwar Kale", "👨‍🌾", "+91 97665 88990",
                "Shivaji Farm Labourers", "Manchar", "Ambegaon", "Pune", 8.5,
                "Sugarcane Harvesting", Arrays.asList("Sugarcane Harvesting", "Sugarcane Cutting", "Weeding", "Planting"),
                4, "Skilled", 4.7, 29, 45, 490.0, true, "5 Sept - 15 Sept",
                Arrays.asList("Sugarcane Harvesting", "Planting"), 15
        ));
        workers.add(new LabourWorker(
                "lab_5", "Ankush Thorat", "👨‍🌾", "+91 94210 33441",
                "Malhar Agro Workers", "Baramati", "Baramati", "Pune", 4.2,
                "Sugarcane Harvesting", Arrays.asList("Sugarcane Harvesting", "Sugarcane Cutting", "Drip Laying", "Fertilizer Application"),
                5, "Skilled", 4.8, 41, 72, 500.0, true, "5 Sept - 10 Sept",
                Arrays.asList("Sugarcane Harvesting", "Drip Laying"), 18
        ));
        workers.add(new LabourWorker(
                "lab_6", "Pandurang Shinde", "👨‍🌾", "+91 91588 77665",
                "Gramin Shramik Sangh", "Alephata", "Junnar", "Pune", 7.0,
                "Sugarcane Harvesting", Arrays.asList("Sugarcane Harvesting", "Sugarcane Cutting", "Field Cleaning", "Loading"),
                4, "Semi-skilled", 4.6, 25, 39, 480.0, true, "6 Sept - 14 Sept",
                Arrays.asList("Sugarcane Harvesting", "Field Cleaning"), 15
        ));

        // Seed Sample Requirement
        LabourRequirement req = new LabourRequirement();
        req.setId("req_demo_01");
        req.setFarmerId("FARMER_MH_01");
        req.setFarmerName("Rameshwar Patil");
        req.setFarmerPhone("+91 98220 12345");
        req.setWorkType("Sugarcane Harvesting");
        req.setCrop("Sugarcane");
        req.setDescription("Need 6 skilled sugarcane cutters and loaders for 3.5 acres farm.");
        req.setWorkersRequired(6);
        req.setSkillLevel("Skilled");
        req.setStartDate("05 Sept 2026");
        req.setEndDate("08 Sept 2026");
        req.setStartTime("07:00 AM");
        req.setWorkingHoursPerDay(8);
        req.setWageType("Per Day");
        req.setWageAmount(500.0);
        req.setPaymentTerms("Daily Cash");
        req.setVillage("Narayangaon");
        req.setTaluka("Junnar");
        req.setFarmLocation("Near Narayangaon Bypass Canal, Survey No 142");
        req.setStatus("Scheduled");
        req.setWorkerIdsRequested(new ArrayList<>(Arrays.asList("lab_1", "lab_2", "lab_3", "lab_4", "lab_5", "lab_6")));
        req.setWorkerIdsAccepted(new ArrayList<>(Arrays.asList("lab_1", "lab_3", "lab_5")));
        req.setWorkerIdsConfirmed(new ArrayList<>(Arrays.asList("lab_1", "lab_3", "lab_5")));
        requirements.add(req);

        // Seed Job Applications for Workers
        jobApplications.add(new LabourApplication(
                "job_101", "req_demo_01", "FARMER_MH_01", "Rameshwar Patil", "+91 98220 12345",
                "lab_1", "Ramesh Ghadge", "+91 98221 44556", "Sugarcane Harvesting", "Sugarcane",
                "05 Sept 2026", "08 Sept 2026", "07:00 AM", 8, 500.0, "Per Day",
                "Narayangaon", "Junnar", 3.2, true, false, "Bring sickle cutting tools", "Confirmed"
        ));
        jobApplications.add(new LabourApplication(
                "job_102", "req_demo_01", "FARMER_MH_01", "Rameshwar Patil", "+91 98220 12345",
                "lab_3", "Mahesh Patil", "+91 98901 23456", "Sugarcane Harvesting", "Sugarcane",
                "05 Sept 2026", "08 Sept 2026", "07:00 AM", 8, 500.0, "Per Day",
                "Narayangaon", "Junnar", 6.4, true, false, "Bring sickle cutting tools", "Confirmed"
        ));
    }

    // --- Worker Queries ---
    public List<LabourWorker> getAllWorkers() {
        return new ArrayList<>(workers);
    }

    public LabourWorker getWorkerById(String workerId) {
        for (LabourWorker worker : workers) {
            if (worker.getId().equals(workerId)) {
                return worker;
            }
        }
        return null;
    }

    public List<LabourWorker> findMatchingWorkers(String workType, String skillLevel, double maxDistanceKm) {
        List<LabourWorker> matched = new ArrayList<>();
        for (LabourWorker worker : workers) {
            if (!worker.isAvailable()) continue;
            boolean skillMatches = workType == null || workType.isEmpty() ||
                    worker.getPrimarySkill().toLowerCase().contains(workType.toLowerCase()) ||
                    worker.getSkills().stream().anyMatch(s -> s.toLowerCase().contains(workType.toLowerCase()));
            boolean distMatches = maxDistanceKm <= 0 || worker.getDistanceKm() <= maxDistanceKm;
            if (skillMatches && distMatches) {
                matched.add(worker);
            }
        }
        if (matched.isEmpty()) {
            return new ArrayList<>(workers);
        }
        return matched;
    }

    // --- Requirements Management ---
    public List<LabourRequirement> getAllRequirements() {
        return new ArrayList<>(requirements);
    }

    public LabourRequirement getRequirementById(String reqId) {
        for (LabourRequirement req : requirements) {
            if (req.getId().equals(reqId)) {
                return req;
            }
        }
        return null;
    }

    public synchronized void postRequirement(LabourRequirement requirement) {
        if (requirement != null) {
            requirements.add(0, requirement);
        }
    }

    public synchronized boolean updateRequirementStatus(String reqId, String status) {
        LabourRequirement req = getRequirementById(reqId);
        if (req != null) {
            req.setStatus(status);
            return true;
        }
        return false;
    }

    // --- Job Application Workflow ---
    public List<LabourApplication> getApplicationsForLabour(String labourId) {
        List<LabourApplication> result = new ArrayList<>();
        for (LabourApplication app : jobApplications) {
            if (app.getLabourId().equals(labourId)) {
                result.add(app);
            }
        }
        return result;
    }

    public List<LabourApplication> getApplicationsForRequirement(String reqId) {
        List<LabourApplication> result = new ArrayList<>();
        for (LabourApplication app : jobApplications) {
            if (app.getRequirementId().equals(reqId)) {
                result.add(app);
            }
        }
        return result;
    }

    public synchronized void sendJobRequest(LabourRequirement req, LabourWorker worker) {
        String jobId = "job_" + System.currentTimeMillis() + "_" + worker.getId();
        LabourApplication app = new LabourApplication(
                jobId, req.getId(), req.getFarmerId(), req.getFarmerName(), req.getFarmerPhone(),
                worker.getId(), worker.getName(), worker.getPhone(), req.getWorkType(), req.getCrop(),
                req.getStartDate(), req.getEndDate(), req.getStartTime(), req.getWorkingHoursPerDay(),
                req.getWageAmount(), req.getWageType(), req.getVillage(), req.getTaluka(),
                worker.getDistanceKm(), req.isFoodProvided(), req.isTransportProvided(),
                req.getSpecialInstructions(), "Pending"
        );
        jobApplications.add(0, app);
        if (!req.getWorkerIdsRequested().contains(worker.getId())) {
            req.getWorkerIdsRequested().add(worker.getId());
        }
        req.setStatus("Request Sent");
    }

    public synchronized boolean respondToJobRequest(String applicationId, boolean accept) {
        for (LabourApplication app : jobApplications) {
            if (app.getId().equals(applicationId)) {
                app.setStatus(accept ? "Accepted" : "Rejected");
                app.setRespondedAt(new SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(new Date()));

                LabourRequirement req = getRequirementById(app.getRequirementId());
                if (req != null) {
                    if (accept) {
                        if (!req.getWorkerIdsAccepted().contains(app.getLabourId())) {
                            req.getWorkerIdsAccepted().add(app.getLabourId());
                        }
                        req.setStatus("Accepted");
                    } else {
                        if (!req.getWorkerIdsRejected().contains(app.getLabourId())) {
                            req.getWorkerIdsRejected().add(app.getLabourId());
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public synchronized boolean confirmWorkerBooking(String reqId, String labourId) {
        LabourRequirement req = getRequirementById(reqId);
        if (req != null) {
            if (!req.getWorkerIdsConfirmed().contains(labourId)) {
                req.getWorkerIdsConfirmed().add(labourId);
            }
            req.setStatus("Scheduled");
            for (LabourApplication app : jobApplications) {
                if (app.getRequirementId().equals(reqId) && app.getLabourId().equals(labourId)) {
                    app.setStatus("Confirmed");
                }
            }
            return true;
        }
        return false;
    }

    public synchronized boolean completeJob(String applicationId, double rating, String review) {
        for (LabourApplication app : jobApplications) {
            if (app.getId().equals(applicationId)) {
                app.setStatus("Completed");
                app.setCompletedAt(new SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(new Date()));
                app.setFarmerRating(rating);
                app.setFarmerReview(review);
                return true;
            }
        }
        return false;
    }
}
