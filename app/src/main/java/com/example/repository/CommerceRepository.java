package com.example.repository;

import com.example.model.BrokerDeal;
import com.example.model.BrokerRequirement;
import com.example.model.CompanyProfile;
import com.example.model.ContractApplication;
import com.example.model.ContractFarmingDeal;
import com.example.model.FarmerBrokerOffer;
import com.example.model.FarmerCrop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe Central Repository for Corporate Contract Farming deals,
 * corporate profiles, farmer applications, and Wholesale Broker bulk trading requirements/deals.
 */
public class CommerceRepository {

    private static volatile CommerceRepository instance;

    private final CopyOnWriteArrayList<ContractFarmingDeal> contracts = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ContractApplication> applications = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<BrokerRequirement> brokerRequirements = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<FarmerBrokerOffer> brokerOffers = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<BrokerDeal> brokerDeals = new CopyOnWriteArrayList<>();
    private CompanyProfile companyProfile;

    private CommerceRepository() {
        seedInitialContracts();
        seedInitialBrokerData();
    }

    public static CommerceRepository getInstance() {
        if (instance == null) {
            synchronized (CommerceRepository.class) {
                if (instance == null) {
                    instance = new CommerceRepository();
                }
            }
        }
        return instance;
    }

    private void seedInitialContracts() {
        // Seed Company Profile
        companyProfile = new CompanyProfile(
                "comp_01",
                "Sahyadri Farmers Producer Co. Ltd.",
                "Agri-Export & Food Processing Hub",
                "Vikram Thorat (Procurement Head)",
                "+91 20 2554 8899",
                "procure@sahyadrifarmers.com",
                "Pune Cluster & Nashik Highway",
                "FSSAI #11520038000142 / GSTIN #27AABCS1429K1Z4",
                true,
                4.9
        );

        // Seed Realistic Available Corporate Contracts
        contracts.add(new ContractFarmingDeal(
                "cf_01",
                "comp_01",
                "Sahyadri Farmers Producer Co. Ltd.",
                "+91 20 2554 8899",
                "Pune Red Onions",
                "🧅",
                "N-53 Grade A / Garwa",
                100.0,
                "Tons",
                22000.0,
                "Ton",
                "Sept - Oct 2026",
                "Junnar, Ambegaon & Khed Clusters",
                "Bulb diameter 45-65mm, tight pink skin, max 10% moisture, zero sprouting.",
                "25% advance payment on contract signing, 100% buyback guarantee at designated collection center.",
                25,
                "15 Sept 2026",
                "4 Months",
                "25% on agreement, 75% on batch gate delivery within 48 hours via RTGS.",
                "Free agronomy field inspection provided bi-weekly.",
                "Open",
                "10 Aug 2026"
        ));

        contracts.add(new ContractFarmingDeal(
                "cf_02",
                "comp_02",
                "PepsiCo India Processing Division",
                "+91 20 6677 8890",
                "Chip Processing Potatoes",
                "🥔",
                "FC-5 White Flesh Processing Grade",
                50.0,
                "Tons",
                18500.0,
                "Ton",
                "Nov 2026",
                "Manchar & Narayangaon Belt",
                "High specific gravity (>1.080), low reducing sugars (<0.1%), zero greening or hollow heart.",
                "Certified seed tubers supplied at 50% subsidized advance. Full harvest collected directly from farm gate.",
                30,
                "30 Sept 2026",
                "5 Months",
                "30% subsidized input credit, balance direct bank transfer upon quality grading.",
                "Mandatory drip irrigation and certified input protocol adherence.",
                "Open",
                "05 Aug 2026"
        ));

        contracts.add(new ContractFarmingDeal(
                "cf_03",
                "comp_03",
                "Tata Rallis & Agro Foods Ltd.",
                "+91 22 6789 4433",
                "Durum Wheat",
                "🌾",
                "Sharbati A-One Golden Grade",
                40.0,
                "Tons",
                25000.0,
                "Ton",
                "Nov - Dec 2026",
                "Shirur & Pune APMC Hub",
                "Protein >13%, moisture <11%, luster intact, foreign matter <0.5%.",
                "Premium procurement price with assured minimum floor rate. Quality testing done on-field.",
                20,
                "15 Sept 2026",
                "6 Months",
                "20% advance token, 80% on delivery slip generation.",
                "Farmer must maintain crop diary provided during onboarding.",
                "Open",
                "12 Aug 2026"
        ));

        contracts.add(new ContractFarmingDeal(
                "cf_04",
                "comp_04",
                "Mahyco Bio-Agri Corp",
                "+91 24 2334 1122",
                "Juicy Processing Tomato",
                "🍅",
                "Abhinav F1 Processing Hybrid",
                60.0,
                "Tons",
                16000.0,
                "Ton",
                "Oct 2026",
                "Junnar & Khed Clusters",
                "High TSS (Brix > 4.8), deep red color, firm flesh, free from blotch or sun scald.",
                "Crates provided at farm site. Cold-chain transport arranged by buyer.",
                25,
                "20 Sept 2026",
                "3 Months",
                "Weekly settlement against weighbridge slips.",
                "Integrated pest management (IPM) guidelines to be followed strictly.",
                "Open",
                "14 Aug 2026"
        ));

        contracts.add(new ContractFarmingDeal(
                "cf_05",
                "comp_05",
                "Baramati Agro Mills",
                "+91 21 1222 3344",
                "Sugarcane",
                "🎋",
                "Co 86032 High Brix",
                200.0,
                "Tons",
                3200.0,
                "Ton",
                "Dec 2026 - Jan 2027",
                "Pune & Baramati District Zone",
                "Average cane weight >1.2kg, Brix 19-21%, clean detrash without roots.",
                "Factory harvesting squad provided. Sugar recovery bonus linked payout.",
                15,
                "15 Oct 2026",
                "12 Months",
                "FRP rate + quality incentive credited within 14 days of cutting.",
                "Registered field survey and GIS tagging mandatory.",
                "Open",
                "01 Aug 2026"
        ));

        // Seed Existing Farmer Applications
        String farmerId = "FARMER_MH_01";
        String farmerName = "Rameshwar Patil";
        String farmerPhone = "+91 98220 12345";
        String village = "Narayangaon";
        String taluka = "Junnar";
        String district = "Pune";

        // Application 1: Under Review for Pune Red Onions
        applications.add(new ContractApplication(
                "app_101",
                "cf_01",
                "comp_01",
                "Sahyadri Farmers Producer Co. Ltd.",
                farmerId,
                farmerName,
                farmerPhone,
                village,
                taluka,
                district,
                "Pune Red Onions",
                "🧅",
                "N-53 Grade A",
                3.5,
                35.0,
                "Oct 2026",
                "Grade A export quality with drip irrigation and organic soil amendments.",
                "Ready to supply 35 tons meeting all size (45-65mm) and moisture specs.",
                22000.0,
                "Under Review",
                "12 Aug 2026",
                null,
                30,
                "Technical Inspection Scheduled"
        ));

        // Application 2: Active / Confirmed Deal for Sugarcane
        applications.add(new ContractApplication(
                "app_102",
                "cf_05",
                "comp_05",
                "Baramati Agro Mills",
                farmerId,
                farmerName,
                farmerPhone,
                village,
                taluka,
                district,
                "Sugarcane",
                "🎋",
                "Co 86032",
                2.0,
                80.0,
                "Dec 2026",
                "High sugar recovery drip irrigated cane.",
                "Contract signed, advance token credited.",
                3200.0,
                "Active",
                "02 Aug 2026",
                "05 Aug 2026",
                65,
                "Active Crop Growth & Cane Elongation"
        ));

        // Application 3: Another Farmer Application on company contract cf_01
        applications.add(new ContractApplication(
                "app_103",
                "cf_01",
                "comp_01",
                "Sahyadri Farmers Producer Co. Ltd.",
                "FARMER_MH_02",
                "Baburao Shinde",
                "+91 94220 55678",
                "Alephata",
                "Junnar",
                "Pune",
                "Pune Red Onions",
                "🧅",
                "Garwa Special",
                4.0,
                45.0,
                "Sept 2026",
                "Well cured stored onions ready for sorting.",
                "Previous year supplier to Sahyadri FPC.",
                22000.0,
                "Accepted",
                "11 Aug 2026",
                "13 Aug 2026",
                50,
                "Contract Signed & Agreement Exchanged"
        ));
    }

    // --- Contract Operations ---
    public List<ContractFarmingDeal> getAllContracts() {
        return new ArrayList<>(contracts);
    }

    public ContractFarmingDeal getContractById(String id) {
        if (id == null) return null;
        for (ContractFarmingDeal deal : contracts) {
            if (deal.getId().equals(id)) {
                return deal;
            }
        }
        return null;
    }

    public synchronized void addContract(ContractFarmingDeal deal) {
        if (deal != null) {
            if (deal.getId() == null || deal.getId().isEmpty()) {
                deal.setId("cf_" + System.currentTimeMillis());
            }
            if (deal.getDatePublished() == null || deal.getDatePublished().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                deal.setDatePublished(sdf.format(new Date()));
            }
            contracts.add(0, deal);
        }
    }

    public synchronized boolean updateContract(ContractFarmingDeal deal) {
        if (deal == null || deal.getId() == null) return false;
        for (int i = 0; i < contracts.size(); i++) {
            if (contracts.get(i).getId().equals(deal.getId())) {
                contracts.set(i, deal);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean closeContract(String contractId) {
        for (ContractFarmingDeal deal : contracts) {
            if (deal.getId().equals(contractId)) {
                deal.setStatus("Closed");
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteContract(String contractId) {
        return contracts.removeIf(deal -> deal.getId().equals(contractId));
    }

    public List<ContractFarmingDeal> getContractsByCompany(String companyId) {
        List<ContractFarmingDeal> result = new ArrayList<>();
        for (ContractFarmingDeal deal : contracts) {
            if (companyId == null || deal.getCompanyId().equals(companyId)) {
                result.add(deal);
            }
        }
        return result;
    }

    // --- Application Operations ---
    public List<ContractApplication> getAllApplications() {
        return new ArrayList<>(applications);
    }

    public List<ContractApplication> getApplicationsForFarmer(String farmerId) {
        List<ContractApplication> result = new ArrayList<>();
        for (ContractApplication app : applications) {
            if (farmerId == null || app.getFarmerId().equals(farmerId)) {
                result.add(app);
            }
        }
        return result;
    }

    public List<ContractApplication> getApplicationsForContract(String contractId) {
        List<ContractApplication> result = new ArrayList<>();
        for (ContractApplication app : applications) {
            if (app.getContractId().equals(contractId)) {
                result.add(app);
            }
        }
        return result;
    }

    public List<ContractApplication> getApplicationsForCompany(String companyId) {
        List<ContractApplication> result = new ArrayList<>();
        for (ContractApplication app : applications) {
            if (companyId == null || app.getCompanyId().equals(companyId)) {
                result.add(app);
            }
        }
        return result;
    }

    public ContractApplication getApplicationById(String appId) {
        if (appId == null) return null;
        for (ContractApplication app : applications) {
            if (app.getId().equals(appId)) {
                return app;
            }
        }
        return null;
    }

    public synchronized boolean submitApplication(ContractApplication app) {
        if (app == null) return false;
        // Check for existing pending application for this contract & farmer
        for (ContractApplication existing : applications) {
            if (existing.getContractId().equals(app.getContractId()) &&
                    existing.getFarmerId().equals(app.getFarmerId()) &&
                    !existing.getStatus().equalsIgnoreCase("Rejected")) {
                return false; // already applied
            }
        }
        if (app.getId() == null || app.getId().isEmpty()) {
            app.setId("app_" + System.currentTimeMillis());
        }
        if (app.getSubmittedDate() == null || app.getSubmittedDate().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            app.setSubmittedDate(sdf.format(new Date()));
        }
        applications.add(0, app);
        return true;
    }

    public synchronized boolean updateApplicationStatus(String appId, String newStatus, String currentMilestone) {
        for (ContractApplication app : applications) {
            if (app.getId().equals(appId)) {
                app.setStatus(newStatus);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                app.setReviewedDate(sdf.format(new Date()));

                if (currentMilestone != null && !currentMilestone.isEmpty()) {
                    app.setCurrentMilestone(currentMilestone);
                }

                if ("Accepted".equalsIgnoreCase(newStatus)) {
                    app.setMilestoneProgressPercent(50);
                    if (app.getCurrentMilestone() == null || app.getCurrentMilestone().startsWith("Application")) {
                        app.setCurrentMilestone("Application Accepted - Contract Confirmed");
                    }
                } else if ("Active".equalsIgnoreCase(newStatus)) {
                    app.setMilestoneProgressPercent(65);
                    app.setCurrentMilestone("Active Cultivation in Progress");
                } else if ("Harvest Ready".equalsIgnoreCase(newStatus)) {
                    app.setMilestoneProgressPercent(85);
                    app.setCurrentMilestone("Crop Ready for Inspection & Harvesting");
                } else if ("Delivered".equalsIgnoreCase(newStatus)) {
                    app.setMilestoneProgressPercent(95);
                    app.setCurrentMilestone("Produce Delivered to Company Warehouse");
                } else if ("Completed".equalsIgnoreCase(newStatus)) {
                    app.setMilestoneProgressPercent(100);
                    app.setCurrentMilestone("Full Contract Settled & Paid via Bank Transfer");
                } else if ("Rejected".equalsIgnoreCase(newStatus)) {
                    app.setMilestoneProgressPercent(0);
                    app.setCurrentMilestone("Application Rejected by Procurement Team");
                }
                return true;
            }
        }
        return false;
    }

    public synchronized boolean updateApplicationMilestone(String appId, int progressPercent, String milestoneDescription) {
        for (ContractApplication app : applications) {
            if (app.getId().equals(appId)) {
                app.setMilestoneProgressPercent(Math.max(0, Math.min(100, progressPercent)));
                if (milestoneDescription != null) {
                    app.setCurrentMilestone(milestoneDescription);
                }
                if (progressPercent >= 100) {
                    app.setStatus("Completed");
                }
                return true;
            }
        }
        return false;
    }

    // --- Search & Filter ---
    public List<ContractFarmingDeal> searchAndFilter(String query, String category, List<FarmerCrop> farmerCrops) {
        List<ContractFarmingDeal> result = new ArrayList<>();
        for (ContractFarmingDeal deal : contracts) {
            boolean matchesQuery = true;
            if (query != null && !query.trim().isEmpty()) {
                String q = query.toLowerCase().trim();
                matchesQuery = (deal.getCropName() != null && deal.getCropName().toLowerCase().contains(q)) ||
                               (deal.getCompanyName() != null && deal.getCompanyName().toLowerCase().contains(q)) ||
                               (deal.getLocation() != null && deal.getLocation().toLowerCase().contains(q)) ||
                               (deal.getVariety() != null && deal.getVariety().toLowerCase().contains(q));
            }

            boolean matchesCategory = true;
            if (category != null && !category.equalsIgnoreCase("All")) {
                if ("Matches My Crops".equalsIgnoreCase(category)) {
                    matchesCategory = deal.matchesFarmerCrops(farmerCrops);
                } else if ("Onions".equalsIgnoreCase(category)) {
                    matchesCategory = deal.getCropName() != null && deal.getCropName().toLowerCase().contains("onion");
                } else if ("Wheat".equalsIgnoreCase(category) || "Grains".equalsIgnoreCase(category)) {
                    matchesCategory = deal.getCropName() != null && (deal.getCropName().toLowerCase().contains("wheat") || deal.getCropName().toLowerCase().contains("rice"));
                } else if ("Vegetables".equalsIgnoreCase(category)) {
                    matchesCategory = deal.getCropName() != null && (deal.getCropName().toLowerCase().contains("tomato") || deal.getCropName().toLowerCase().contains("potato") || deal.getCropName().toLowerCase().contains("onion"));
                } else if ("Pune / Local".equalsIgnoreCase(category)) {
                    matchesCategory = deal.getLocation() != null && deal.getLocation().toLowerCase().contains("pune");
                } else if ("High Value".equalsIgnoreCase(category)) {
                    matchesCategory = deal.getOfferedPricePerTon() >= 20000;
                }
            }

            if (matchesQuery && matchesCategory) {
                result.add(deal);
            }
        }

        // Prioritize contracts matching farmer's crops to the top
        result.sort((a, b) -> {
            boolean aMatch = a.matchesFarmerCrops(farmerCrops);
            boolean bMatch = b.matchesFarmerCrops(farmerCrops);
            if (aMatch && !bMatch) return -1;
            if (!aMatch && bMatch) return 1;
            return 0;
        });

        return result;
    }

    public CompanyProfile getCompanyProfile() {
        return companyProfile;
    }

    public void updateCompanyProfile(CompanyProfile profile) {
        if (profile != null) {
            this.companyProfile = profile;
        }
    }

    // ==========================================
    // BROKER MODULE: SEED DATA & OPERATIONS
    // ==========================================

    private void seedInitialBrokerData() {
        // 1. Seed Realistic Broker Bulk Buying Requirements
        brokerRequirements.add(new BrokerRequirement(
                "br_01",
                "broker_01",
                "Dinesh Deshmukh",
                "Deshmukh Wholesale Trading Corp",
                "+91 98765 43210",
                "Wheat",
                "🌾",
                20.0,
                "Tons",
                2500.0,
                "quintal",
                "FAQ Grade A / Sharbati, Moisture <11%, High Protein (>13%)",
                "15 Nov 2026",
                "Shirur & Pune APMC Hub",
                "Immediate RTGS / NEFT on weighbridge receipt",
                "Looking for 5 to 10 tons lots from individual farmers or farmer groups.",
                2450.0,
                "Open",
                "10 Aug 2026",
                "14 Aug 2026"
        ));

        brokerRequirements.add(new BrokerRequirement(
                "br_02",
                "broker_02",
                "Sanjay Gawade",
                "Maha Agri Bulk Trading",
                "+91 98221 44556",
                "Pune Red Onions",
                "🧅",
                30.0,
                "Tons",
                1900.0,
                "quintal",
                "Grade A medium to large (45-65mm), dry shade cured, zero sprouting",
                "25 Oct 2026",
                "Wagholi & Narayangaon APMC Yard",
                "100% NEFT payment within 24 hours of gate delivery",
                "Direct farm gate pickup available for batches above 8 Tons.",
                1820.0,
                "Open",
                "12 Aug 2026",
                "14 Aug 2026"
        ));

        brokerRequirements.add(new BrokerRequirement(
                "br_03",
                "broker_03",
                "Vikram Thorat",
                "Sahyadri Commodity Traders",
                "+91 94220 88990",
                "Indrayani Rice",
                "🌾",
                15.0,
                "Tons",
                4100.0,
                "quintal",
                "Certified scented paddy, moisture <11%, clean dry bags",
                "30 Nov 2026",
                "Kamshet & Maval Hub",
                "Immediate cash voucher / RTGS",
                "Moisture testing done on-field with digital probe.",
                4020.0,
                "Open",
                "08 Aug 2026",
                "14 Aug 2026"
        ));

        brokerRequirements.add(new BrokerRequirement(
                "br_04",
                "broker_04",
                "Amit Shinde",
                "Kisan Commodity Brokers",
                "+91 97665 12345",
                "Organic Soybean",
                "🌱",
                25.0,
                "Tons",
                4650.0,
                "quintal",
                "Clean yellow grain, oil >18%, foreign matter <1%",
                "10 Nov 2026",
                "Baramati Agro Cluster",
                "Direct online payment to KCC bank account",
                "Gunny bags will be supplied by broker at pickup.",
                4550.0,
                "Open",
                "05 Aug 2026",
                "14 Aug 2026"
        ));

        brokerRequirements.add(new BrokerRequirement(
                "br_05",
                "broker_05",
                "Prakash Kadam",
                "Western Agro Brokers",
                "+91 98901 67890",
                "Sugarcane",
                "🎋",
                50.0,
                "Tons",
                3250.0,
                "ton",
                "High Brix (>19%), fresh cut within 24 hours, clean detrash",
                "20 Dec 2026",
                "Junnar & Baramati Zone",
                "Settlement within 3 days against mill weighbridge slip",
                "Transport fleet provided from farm site.",
                3150.0,
                "Open",
                "02 Aug 2026",
                "14 Aug 2026"
        ));

        brokerRequirements.add(new BrokerRequirement(
                "br_06",
                "broker_01",
                "Dinesh Deshmukh",
                "Deshmukh Wholesale Trading Corp",
                "+91 98765 43210",
                "Alphonso Mango",
                "🥭",
                10.0,
                "Tons",
                6500.0,
                "quintal",
                "Export Grade 1, uniform 250-300g fruit, zero fruit fly damage",
                "15 May 2027",
                "Junnar & Pune Cold Chain Facility",
                "30% advance token, balance on sorting & grading",
                "Corrugated export boxes supplied.",
                6200.0,
                "Open",
                "01 Aug 2026",
                "14 Aug 2026"
        ));

        // 2. Seed Initial Farmer Offers (with active negotiation and accepted offer)
        String farmerId = "FARMER_MH_01";
        String farmerName = "Rameshwar Patil";
        String farmerPhone = "+91 98220 12345";
        String farmerLocation = "Narayangaon, Junnar (Pune)";

        // Offer 1: Under Negotiation on Wheat requirement br_01
        brokerOffers.add(new FarmerBrokerOffer(
                "bo_101",
                "br_01",
                farmerId,
                farmerName,
                farmerPhone,
                farmerLocation,
                "crop_wheat_01",
                "Wheat",
                "🌾",
                8.0,
                "Tons",
                2550.0,
                "quintal",
                2500.0,
                2525.0,
                "10 Nov 2026",
                "High grade golden Sharbati wheat, cleaned and bagged, moisture 10.5%",
                "Good quality wheat available from recent harvest. Ready for pickup.",
                "Negotiating",
                "BROKER",
                "Broker countered with ₹2,500/quintal. Counter offer placed at ₹2,525/quintal.",
                "12 Aug 2026",
                "14 Aug 2026"
        ));

        // Offer 2: Accepted on Onions requirement br_02
        brokerOffers.add(new FarmerBrokerOffer(
                "bo_102",
                "br_02",
                farmerId,
                farmerName,
                farmerPhone,
                farmerLocation,
                "crop_1",
                "Pune Red Onions",
                "🧅",
                12.0,
                "Tons",
                1900.0,
                "quintal",
                0.0,
                1900.0,
                "20 Oct 2026",
                "Garwa special red onions, dry shade cured for 15 days",
                "Direct farm gate pickup available in Narayangaon.",
                "Accepted",
                "BROKER",
                "Offer accepted by broker at ₹1,900/quintal.",
                "11 Aug 2026",
                "13 Aug 2026"
        ));

        // 3. Seed Realistic Broker Deals
        // Deal 1: Confirmed Deal for 12 Tons Onions (12 Tons = 120 Quintals * 1900 = ₹2,28,000)
        brokerDeals.add(new BrokerDeal(
                "bd_201",
                "br_02",
                "bo_102",
                "broker_02",
                "Sanjay Gawade",
                "Maha Agri Bulk Trading",
                "+91 98221 44556",
                farmerId,
                farmerName,
                farmerPhone,
                "Pune Red Onions",
                "🧅",
                12.0,
                "Tons",
                1900.0,
                "quintal",
                228000.0,
                "Narayangaon Farm Gate, Junnar",
                "22 Oct 2026",
                "100% NEFT payment within 24 hours of gate delivery",
                "Pickup Scheduled",
                1820.0,
                "13 Aug 2026",
                null
        ));

        // Deal 2: Completed Deal for 5 Tons Indrayani Rice (5 Tons = 50 Quintals * 4100 = ₹2,05,000)
        brokerDeals.add(new BrokerDeal(
                "bd_202",
                "br_03",
                null,
                "broker_03",
                "Vikram Thorat",
                "Sahyadri Commodity Traders",
                "+91 94220 88990",
                farmerId,
                farmerName,
                farmerPhone,
                "Indrayani Rice",
                "🌾",
                5.0,
                "Tons",
                4100.0,
                "quintal",
                205000.0,
                "Kamshet Hub Weighbridge",
                "05 Aug 2026",
                "Immediate cash voucher / RTGS",
                "Completed",
                4020.0,
                "01 Aug 2026",
                "08 Aug 2026"
        ));
    }

    // --- Broker Requirements Operations ---

    public List<BrokerRequirement> getAllBrokerRequirements() {
        return new ArrayList<>(brokerRequirements);
    }

    public BrokerRequirement getBrokerRequirementById(String id) {
        if (id == null) return null;
        for (BrokerRequirement req : brokerRequirements) {
            if (req.getId().equals(id)) {
                return req;
            }
        }
        return null;
    }

    public synchronized void addBrokerRequirement(BrokerRequirement req) {
        if (req != null) {
            if (req.getId() == null || req.getId().isEmpty()) {
                req.setId("br_" + System.currentTimeMillis());
            }
            if (req.getCreatedAt() == null || req.getCreatedAt().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                req.setCreatedAt(sdf.format(new Date()));
                req.setUpdatedAt(sdf.format(new Date()));
            }
            brokerRequirements.add(0, req);
        }
    }

    public synchronized boolean updateBrokerRequirement(BrokerRequirement req) {
        if (req == null || req.getId() == null) return false;
        for (int i = 0; i < brokerRequirements.size(); i++) {
            if (brokerRequirements.get(i).getId().equals(req.getId())) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                req.setUpdatedAt(sdf.format(new Date()));
                brokerRequirements.set(i, req);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean closeBrokerRequirement(String reqId) {
        for (BrokerRequirement req : brokerRequirements) {
            if (req.getId().equals(reqId)) {
                req.setStatus("Closed");
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                req.setUpdatedAt(sdf.format(new Date()));
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteBrokerRequirement(String reqId) {
        return brokerRequirements.removeIf(req -> req.getId().equals(reqId));
    }

    public List<BrokerRequirement> getRequirementsByBroker(String brokerId) {
        List<BrokerRequirement> list = new ArrayList<>();
        for (BrokerRequirement req : brokerRequirements) {
            if (brokerId == null || req.getBrokerId().equals(brokerId)) {
                list.add(req);
            }
        }
        return list;
    }

    public List<BrokerRequirement> searchAndFilterRequirements(String query, String category, List<FarmerCrop> farmerCrops) {
        List<BrokerRequirement> result = new ArrayList<>();
        for (BrokerRequirement req : brokerRequirements) {
            boolean matchesQuery = true;
            if (query != null && !query.trim().isEmpty()) {
                String q = query.toLowerCase().trim();
                matchesQuery = (req.getCrop() != null && req.getCrop().toLowerCase().contains(q)) ||
                        (req.getBrokerName() != null && req.getBrokerName().toLowerCase().contains(q)) ||
                        (req.getBrokerFirmName() != null && req.getBrokerFirmName().toLowerCase().contains(q)) ||
                        (req.getPickupLocation() != null && req.getPickupLocation().toLowerCase().contains(q)) ||
                        (req.getQualityRequirement() != null && req.getQualityRequirement().toLowerCase().contains(q));
            }

            boolean matchesCategory = true;
            if (category != null && !category.equalsIgnoreCase("All")) {
                if ("Matches My Crops".equalsIgnoreCase(category) || "⭐ Matches My Crops".equalsIgnoreCase(category)) {
                    matchesCategory = req.matchesFarmerCrops(farmerCrops);
                } else if ("Wheat".equalsIgnoreCase(category) || "Grains".equalsIgnoreCase(category)) {
                    matchesCategory = req.getCrop() != null && (req.getCrop().toLowerCase().contains("wheat") || req.getCrop().toLowerCase().contains("rice") || req.getCrop().toLowerCase().contains("paddy"));
                } else if ("Onions".equalsIgnoreCase(category)) {
                    matchesCategory = req.getCrop() != null && req.getCrop().toLowerCase().contains("onion");
                } else if ("Rice".equalsIgnoreCase(category)) {
                    matchesCategory = req.getCrop() != null && (req.getCrop().toLowerCase().contains("rice") || req.getCrop().toLowerCase().contains("indrayani"));
                } else if ("High Price".equalsIgnoreCase(category)) {
                    matchesCategory = req.getOfferedPrice() >= 2500;
                } else if ("Pune / Local".equalsIgnoreCase(category)) {
                    matchesCategory = req.getPickupLocation() != null && req.getPickupLocation().toLowerCase().contains("pune");
                }
            }

            if (matchesQuery && matchesCategory) {
                result.add(req);
            }
        }

        // Prioritize requirements matching farmer's crops to the top
        result.sort((a, b) -> {
            boolean aMatch = a.matchesFarmerCrops(farmerCrops);
            boolean bMatch = b.matchesFarmerCrops(farmerCrops);
            if (aMatch && !bMatch) return -1;
            if (!aMatch && bMatch) return 1;
            return 0;
        });

        return result;
    }

    // --- Farmer Broker Offers & Negotiation Operations ---

    public List<FarmerBrokerOffer> getAllFarmerBrokerOffers() {
        return new ArrayList<>(brokerOffers);
    }

    public List<FarmerBrokerOffer> getOffersForFarmer(String farmerId) {
        List<FarmerBrokerOffer> list = new ArrayList<>();
        for (FarmerBrokerOffer offer : brokerOffers) {
            if (farmerId == null || offer.getFarmerId().equals(farmerId)) {
                list.add(offer);
            }
        }
        return list;
    }

    public List<FarmerBrokerOffer> getOffersForRequirement(String reqId) {
        List<FarmerBrokerOffer> list = new ArrayList<>();
        for (FarmerBrokerOffer offer : brokerOffers) {
            if (offer.getRequirementId().equals(reqId)) {
                list.add(offer);
            }
        }
        return list;
    }

    public FarmerBrokerOffer getOfferById(String offerId) {
        if (offerId == null) return null;
        for (FarmerBrokerOffer offer : brokerOffers) {
            if (offer.getId().equals(offerId)) {
                return offer;
            }
        }
        return null;
    }

    public synchronized boolean submitFarmerBrokerOffer(FarmerBrokerOffer offer) {
        if (offer == null) return false;
        // Prevent duplicate pending offers for the same requirement
        for (FarmerBrokerOffer existing : brokerOffers) {
            if (existing.getRequirementId().equals(offer.getRequirementId()) &&
                    existing.getFarmerId().equals(offer.getFarmerId()) &&
                    ("Pending".equalsIgnoreCase(existing.getStatus()) || "Negotiating".equalsIgnoreCase(existing.getStatus()))) {
                return false;
            }
        }
        if (offer.getId() == null || offer.getId().isEmpty()) {
            offer.setId("bo_" + System.currentTimeMillis());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String now = sdf.format(new Date());
        if (offer.getCreatedAt() == null || offer.getCreatedAt().isEmpty()) {
            offer.setCreatedAt(now);
        }
        offer.setUpdatedAt(now);
        brokerOffers.add(0, offer);
        return true;
    }

    public synchronized boolean brokerCounterOffer(String offerId, double counterPrice, String note) {
        for (FarmerBrokerOffer offer : brokerOffers) {
            if (offer.getId().equals(offerId)) {
                offer.setCounterPrice(counterPrice);
                offer.setFinalAgreedPrice(counterPrice);
                offer.setStatus("Negotiating");
                offer.setLastActorRole("BROKER");
                offer.setNegotiationNote(note != null ? note : "Broker placed counter price of ₹" + counterPrice + "/" + offer.getPriceUnit());
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                offer.setUpdatedAt(sdf.format(new Date()));
                return true;
            }
        }
        return false;
    }

    public synchronized boolean farmerCounterOffer(String offerId, double counterPrice, String note) {
        for (FarmerBrokerOffer offer : brokerOffers) {
            if (offer.getId().equals(offerId)) {
                offer.setExpectedPrice(counterPrice);
                offer.setFinalAgreedPrice(counterPrice);
                offer.setStatus("Negotiating");
                offer.setLastActorRole("FARMER");
                offer.setNegotiationNote(note != null ? note : "Farmer countered with ₹" + counterPrice + "/" + offer.getPriceUnit());
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                offer.setUpdatedAt(sdf.format(new Date()));
                return true;
            }
        }
        return false;
    }

    public synchronized boolean brokerAcceptOffer(String offerId, String pickupDate, String pickupLocation) {
        for (FarmerBrokerOffer offer : brokerOffers) {
            if (offer.getId().equals(offerId)) {
                offer.setStatus("Accepted");
                double agreedPrice = offer.getFinalAgreedPrice() > 0 ? offer.getFinalAgreedPrice() :
                        (offer.getCounterPrice() > 0 ? offer.getCounterPrice() : offer.getExpectedPrice());
                offer.setFinalAgreedPrice(agreedPrice);
                offer.setLastActorRole("BROKER");
                offer.setNegotiationNote("Offer accepted at agreed price ₹" + agreedPrice + "/" + offer.getPriceUnit());
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                offer.setUpdatedAt(sdf.format(new Date()));

                // Retrieve parent requirement
                BrokerRequirement req = getBrokerRequirementById(offer.getRequirementId());

                // Automatically generate Confirmed Broker Deal
                String dealId = "bd_" + System.currentTimeMillis();
                double total = BrokerDeal.calculateTotalValue(offer.getAvailableQuantity(), offer.getUnit(), agreedPrice, offer.getPriceUnit());
                String pDate = (pickupDate != null && !pickupDate.isEmpty()) ? pickupDate : offer.getAvailableDate();
                String pLoc = (pickupLocation != null && !pickupLocation.isEmpty()) ? pickupLocation :
                        (offer.getFarmerLocation() != null ? offer.getFarmerLocation() : (req != null ? req.getPickupLocation() : "Farmer Farm Gate"));

                BrokerDeal deal = new BrokerDeal(
                        dealId,
                        offer.getRequirementId(),
                        offer.getId(),
                        req != null ? req.getBrokerId() : "broker_01",
                        req != null ? req.getBrokerName() : "Broker",
                        req != null ? req.getBrokerFirmName() : "Wholesale Trading Corp",
                        req != null ? req.getBrokerPhone() : "+91 98765 43210",
                        offer.getFarmerId(),
                        offer.getFarmerName(),
                        offer.getFarmerPhone(),
                        offer.getCropName(),
                        offer.getCropEmoji(),
                        offer.getAvailableQuantity(),
                        offer.getUnit(),
                        agreedPrice,
                        offer.getPriceUnit(),
                        total,
                        pLoc,
                        pDate,
                        req != null ? req.getPaymentTerms() : "Immediate RTGS on delivery slip",
                        "Deal Confirmed",
                        req != null ? req.getSampleMandiPrice() : 0.0,
                        sdf.format(new Date()),
                        null
                );
                brokerDeals.add(0, deal);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean farmerAcceptCounterOffer(String offerId) {
        for (FarmerBrokerOffer offer : brokerOffers) {
            if (offer.getId().equals(offerId)) {
                offer.setStatus("Accepted");
                double agreedPrice = offer.getFinalAgreedPrice() > 0 ? offer.getFinalAgreedPrice() :
                        (offer.getCounterPrice() > 0 ? offer.getCounterPrice() : offer.getExpectedPrice());
                offer.setFinalAgreedPrice(agreedPrice);
                offer.setLastActorRole("FARMER");
                offer.setNegotiationNote("Farmer accepted counter price ₹" + agreedPrice + "/" + offer.getPriceUnit());
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                offer.setUpdatedAt(sdf.format(new Date()));

                // Retrieve requirement
                BrokerRequirement req = getBrokerRequirementById(offer.getRequirementId());

                // Generate Confirmed Deal
                String dealId = "bd_" + System.currentTimeMillis();
                double total = BrokerDeal.calculateTotalValue(offer.getAvailableQuantity(), offer.getUnit(), agreedPrice, offer.getPriceUnit());
                String pLoc = offer.getFarmerLocation() != null ? offer.getFarmerLocation() :
                        (req != null ? req.getPickupLocation() : "Farmer Farm Gate");

                BrokerDeal deal = new BrokerDeal(
                        dealId,
                        offer.getRequirementId(),
                        offer.getId(),
                        req != null ? req.getBrokerId() : "broker_01",
                        req != null ? req.getBrokerName() : "Broker",
                        req != null ? req.getBrokerFirmName() : "Wholesale Trading Corp",
                        req != null ? req.getBrokerPhone() : "+91 98765 43210",
                        offer.getFarmerId(),
                        offer.getFarmerName(),
                        offer.getFarmerPhone(),
                        offer.getCropName(),
                        offer.getCropEmoji(),
                        offer.getAvailableQuantity(),
                        offer.getUnit(),
                        agreedPrice,
                        offer.getPriceUnit(),
                        total,
                        pLoc,
                        offer.getAvailableDate(),
                        req != null ? req.getPaymentTerms() : "Immediate RTGS on delivery slip",
                        "Deal Confirmed",
                        req != null ? req.getSampleMandiPrice() : 0.0,
                        sdf.format(new Date()),
                        null
                );
                brokerDeals.add(0, deal);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean brokerRejectOffer(String offerId, String reason) {
        for (FarmerBrokerOffer offer : brokerOffers) {
            if (offer.getId().equals(offerId)) {
                offer.setStatus("Rejected");
                offer.setLastActorRole("BROKER");
                offer.setNegotiationNote(reason != null ? reason : "Offer rejected by broker.");
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                offer.setUpdatedAt(sdf.format(new Date()));
                return true;
            }
        }
        return false;
    }

    public synchronized boolean farmerRejectOffer(String offerId) {
        for (FarmerBrokerOffer offer : brokerOffers) {
            if (offer.getId().equals(offerId)) {
                offer.setStatus("Cancelled");
                offer.setLastActorRole("FARMER");
                offer.setNegotiationNote("Offer withdrawn by farmer.");
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                offer.setUpdatedAt(sdf.format(new Date()));
                return true;
            }
        }
        return false;
    }

    // --- Broker Deals Operations ---

    public List<BrokerDeal> getAllBrokerDeals() {
        return new ArrayList<>(brokerDeals);
    }

    public List<BrokerDeal> getDealsForFarmer(String farmerId) {
        List<BrokerDeal> list = new ArrayList<>();
        for (BrokerDeal deal : brokerDeals) {
            if (farmerId == null || (deal.getFarmerId() != null && deal.getFarmerId().equals(farmerId))) {
                list.add(deal);
            }
        }
        return list;
    }

    public List<BrokerDeal> getDealsForBroker(String brokerId) {
        List<BrokerDeal> list = new ArrayList<>();
        for (BrokerDeal deal : brokerDeals) {
            if (brokerId == null || (deal.getBrokerId() != null && deal.getBrokerId().equals(brokerId))) {
                list.add(deal);
            }
        }
        return list;
    }

    public BrokerDeal getDealById(String dealId) {
        if (dealId == null) return null;
        for (BrokerDeal deal : brokerDeals) {
            if (deal.getId().equals(dealId)) {
                return deal;
            }
        }
        return null;
    }

    public synchronized void createBrokerDeal(BrokerDeal deal) {
        if (deal != null) {
            if (deal.getId() == null || deal.getId().isEmpty()) {
                deal.setId("bd_" + System.currentTimeMillis());
            }
            if (deal.getCreatedAt() == null || deal.getCreatedAt().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                deal.setCreatedAt(sdf.format(new Date()));
            }
            brokerDeals.add(0, deal);
        }
    }

    public synchronized boolean updateBrokerDealStatus(String dealId, String newStatus) {
        for (BrokerDeal deal : brokerDeals) {
            if (deal.getId().equals(dealId)) {
                deal.setDealStatus(newStatus);
                if ("Completed".equalsIgnoreCase(newStatus)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    deal.setCompletedAt(sdf.format(new Date()));
                }
                return true;
            }
        }
        return false;
    }
}
