package com.example.repository;

import com.example.model.CropRequirement;
import com.example.model.FarmerCrop;
import com.example.model.FarmerDirectOrder;
import com.example.model.FarmerProduceListing;
import com.example.model.SavedDiseaseScan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe Central Repository for Farmer business operations, profile, crops, produce listings, and activities.
 */
public class FarmerRepository {

    private static volatile FarmerRepository instance;

    // In-memory thread-safe data stores
    private final CopyOnWriteArrayList<FarmerCrop> crops = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<CropRequirement> requirements = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<FarmerProduceListing> produceListings = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<FarmerDirectOrder> directOrders = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SavedDiseaseScan> diseaseScans = new CopyOnWriteArrayList<>();

    // Farmer Profile
    private String farmerId = "FARMER_MH_01";
    private String farmerName = "Rameshwar Patil";
    private String farmerPhone = "+91 98220 12345";
    private String village = "Narayangaon";
    private String taluka = "Junnar";
    private String district = "Pune";
    private String totalLandAcres = "8.5 Acres";
    private String kisanCreditCardNo = "KCC-MH-789021";

    private FarmerRepository() {
        seedInitialData();
    }

    public static FarmerRepository getInstance() {
        if (instance == null) {
            synchronized (FarmerRepository.class) {
                if (instance == null) {
                    instance = new FarmerRepository();
                }
            }
        }
        return instance;
    }

    private void seedInitialData() {
        // Seed Initial Crops
        crops.add(new FarmerCrop(
                "crop_1", "Pune Red Onions", "Gavran Fursungi", "Cash Crop",
                "3.5", "Acres", "15 June 2026", "10 Oct 2026",
                "Drip Irrigation", 350.0, 22.5, "High grade winter onion batch with excellent pungency.",
                "Growing", "🧅"
        ));
        crops.add(new FarmerCrop(
                "crop_2", "Alphonso Mango", "Hapus Ratnagiri Clone", "Horticulture",
                "2.0", "Acres", "Annual Orchard", "15 May 2027",
                "Micro Sprinkler", 120.0, 850.0, "Export quality Alphonso orchard, GI tag certified.",
                "Growing", "🥭"
        ));
        crops.add(new FarmerCrop(
                "crop_3", "Indrayani Rice", "Aromatic Basmati Cross", "Food Grain",
                "3.0", "Acres", "05 July 2026", "25 Nov 2026",
                "Canal Irrigation", 180.0, 45.0, "Fragrant Indrayani rice crop in peak flowering phase.",
                "Growing", "🌾"
        ));

        // Seed Produce Listings
        produceListings.add(new FarmerProduceListing(
                "prod_1", farmerId, farmerName, "+91 98220 12345", "Fresh Hybrid Red Tomato",
                "Vegetables", 500.0, "kg", 30.0, "kg", "Grade A Export",
                "3 September 2026", "Today", "25 September 2026",
                "Baramati", "Baramati", "Pune",
                "Direct farm-harvested glossy firm red tomatoes, ideal for salads and long shelf life.",
                "Available", "🍅"
        ));
        produceListings.add(new FarmerProduceListing(
                "prod_2", farmerId, farmerName, "+91 98220 12345", "Nashik Fresh Red Onion",
                "Vegetables", 1200.0, "kg", 25.0, "kg", "Grade A Export",
                "28 August 2026", "Today", "30 October 2026",
                "Wagholi", "Haveli", "Pune",
                "Sun-cured medium pungent red onions directly sorted from field sheds.",
                "Available", "🧅"
        ));
        produceListings.add(new FarmerProduceListing(
                "prod_3", farmerId, farmerName, "+91 98220 12345", "Aromatic Indrayani Rice",
                "Cereals", 800.0, "kg", 52.0, "kg", "Organic Grade 1",
                "20 August 2026", "Today", "15 December 2026",
                "Kamshet", "Maval", "Pune",
                "Fragrant unpolished traditional Indrayani rice with rich aroma.",
                "Available", "🌾"
        ));
        produceListings.add(new FarmerProduceListing(
                "prod_4", "farmer_2", "Vilasrao Deshmukh", "+91 98221 67890", "Ratnagiri Alphonso Mango",
                "Fruits", 150.0, "dozen", 450.0, "dozen", "Grade A Premium",
                "15 May 2026", "Available Now", "15 June 2027",
                "Junnar Hills", "Junnar", "Pune",
                "Naturally tree-ripened Alphonso mangoes, chemical-free and extremely sweet.",
                "Available", "🥭"
        ));
        produceListings.add(new FarmerProduceListing(
                "prod_5", "farmer_3", "Dnyaneshwar Hande", "+91 97654 32100", "Organic Turmeric Finger Roots",
                "Spices", 250.0, "kg", 180.0, "kg", "Curcumin 5%+",
                "10 August 2026", "Today", "30 November 2026",
                "Baramati Agro", "Baramati", "Pune",
                "Steam-washed raw yellow turmeric finger roots high in natural medicinal curcumin.",
                "Available", "🫚"
        ));
        produceListings.add(new FarmerProduceListing(
                "prod_6", "farmer_4", "Tanaji Rao", "+91 98811 22334", "Fresh Green Polyhouse Capsicum",
                "Vegetables", 300.0, "kg", 45.0, "kg", "Grade A",
                "01 September 2026", "Today", "20 September 2026",
                "Khed Shivapur", "Khed", "Pune",
                "Crunchy thick-walled green bell peppers grown under automated shade nets.",
                "Available", "🫑"
        ));
        produceListings.add(new FarmerProduceListing(
                "prod_7", farmerId, farmerName, "+91 98220 12345", "Golden Lokwan Wheat",
                "Cereals", 2000.0, "kg", 32.0, "kg", "Sharbati Grade",
                "15 July 2026", "Today", "31 December 2026",
                "Baramati", "Baramati", "Pune",
                "Heavy-grain stone cleaned Lokwan wheat suitable for soft chapatis.",
                "Available", "🌾"
        ));

        // Seed Direct Orders
        directOrders.add(new FarmerDirectOrder(
                "ord_101", "prod_1", farmerId, farmerName, "Fresh Hybrid Red Tomato", "Vegetables",
                "cust_1", "Amit Sharma", "+91 98901 11223", "Flat 402, Shivajinagar, Pune",
                "Shivajinagar", "Haveli", "Pune",
                10.0, "kg", 30.0, 300.0, "01 Sep 2026", "03 Sep 2026",
                "Order Placed", "Paid Online", "🍅"
        ));
        directOrders.add(new FarmerDirectOrder(
                "ord_102", "prod_2", farmerId, farmerName, "Nashik Fresh Red Onion", "Vegetables",
                "cust_2", "Priya Kulkarni", "+91 97664 33445", "Bungalow 7, Kothrud, Pune",
                "Kothrud", "Haveli", "Pune",
                25.0, "kg", 25.0, 625.0, "31 Aug 2026", "02 Sep 2026",
                "Accepted", "Cash on Delivery", "🧅"
        ));
        directOrders.add(new FarmerDirectOrder(
                "ord_103", "prod_3", farmerId, farmerName, "Aromatic Indrayani Rice", "Cereals",
                "cust_1", "Amit Sharma", "+91 98901 11223", "Flat 402, Shivajinagar, Pune",
                "Shivajinagar", "Haveli", "Pune",
                20.0, "kg", 52.0, 1040.0, "28 Aug 2026", "30 Aug 2026",
                "Delivered", "Paid Online", "🌾"
        ));
    }

    // --- Crops Management ---
    public List<FarmerCrop> getAllCrops() {
        return new ArrayList<>(crops);
    }

    public FarmerCrop getCropById(String cropId) {
        for (FarmerCrop crop : crops) {
            if (crop.getId().equals(cropId)) {
                return crop;
            }
        }
        return null;
    }

    public synchronized void addCrop(FarmerCrop crop) {
        if (crop != null) {
            crops.add(crop);
        }
    }

    public synchronized boolean updateCrop(FarmerCrop updatedCrop) {
        if (updatedCrop == null) return false;
        for (int i = 0; i < crops.size(); i++) {
            if (crops.get(i).getId().equals(updatedCrop.getId())) {
                crops.set(i, updatedCrop);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteCrop(String cropId) {
        return crops.removeIf(crop -> crop.getId().equals(cropId));
    }

    // --- Produce Listings ---
    public List<FarmerProduceListing> getAllProduceListings() {
        return new ArrayList<>(produceListings);
    }

    public List<FarmerProduceListing> getFarmerProduceListings(String targetFarmerId) {
        List<FarmerProduceListing> result = new ArrayList<>();
        for (FarmerProduceListing l : produceListings) {
            if (targetFarmerId == null || targetFarmerId.isEmpty() || l.getFarmerId().equalsIgnoreCase(targetFarmerId)) {
                result.add(l);
            }
        }
        return result;
    }

    public FarmerProduceListing getProduceListingById(String id) {
        if (id == null) return null;
        for (FarmerProduceListing l : produceListings) {
            if (l.getId().equalsIgnoreCase(id)) {
                return l;
            }
        }
        return null;
    }

    public synchronized void addProduceListing(FarmerProduceListing listing) {
        if (listing != null) {
            produceListings.add(0, listing);
        }
    }

    public synchronized boolean updateProduceListing(FarmerProduceListing listing) {
        if (listing == null) return false;
        for (int i = 0; i < produceListings.size(); i++) {
            if (produceListings.get(i).getId().equals(listing.getId())) {
                produceListings.set(i, listing);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean pauseOrResumeProduceListing(String listingId) {
        FarmerProduceListing listing = getProduceListingById(listingId);
        if (listing != null) {
            if ("Paused".equalsIgnoreCase(listing.getStatus())) {
                listing.setStatus(listing.getQuantityAvailable() > 0 ? "Available" : "Sold Out");
            } else {
                listing.setStatus("Paused");
            }
            return true;
        }
        return false;
    }

    public synchronized boolean decrementProduceInventory(String listingId, double quantityToDeduct) {
        FarmerProduceListing listing = getProduceListingById(listingId);
        if (listing != null && listing.getQuantityAvailable() >= quantityToDeduct) {
            listing.setQuantityAvailable(listing.getQuantityAvailable() - quantityToDeduct);
            return true;
        }
        return false;
    }

    public synchronized boolean deleteProduceListing(String listingId) {
        return produceListings.removeIf(l -> l.getId().equals(listingId));
    }

    // --- Direct Orders ---
    public List<FarmerDirectOrder> getAllDirectOrders() {
        return new ArrayList<>(directOrders);
    }

    public List<FarmerDirectOrder> getFarmerDirectOrders(String targetFarmerId) {
        List<FarmerDirectOrder> result = new ArrayList<>();
        for (FarmerDirectOrder o : directOrders) {
            if (targetFarmerId == null || targetFarmerId.isEmpty() || o.getFarmerId().equalsIgnoreCase(targetFarmerId)) {
                result.add(o);
            }
        }
        return result;
    }

    public List<FarmerDirectOrder> getCustomerDirectOrders(String customerId) {
        List<FarmerDirectOrder> result = new ArrayList<>();
        for (FarmerDirectOrder o : directOrders) {
            if (customerId == null || customerId.isEmpty() || o.getCustomerId().equalsIgnoreCase(customerId)) {
                result.add(o);
            }
        }
        return result;
    }

    public FarmerDirectOrder getDirectOrderById(String orderId) {
        if (orderId == null) return null;
        for (FarmerDirectOrder o : directOrders) {
            if (o.getId().equalsIgnoreCase(orderId)) {
                return o;
            }
        }
        return null;
    }

    public synchronized void addDirectOrder(FarmerDirectOrder order) {
        if (order != null) {
            directOrders.add(0, order);
        }
    }

    public synchronized boolean updateOrderStatus(String orderId, String newStatus) {
        for (FarmerDirectOrder order : directOrders) {
            if (order.getId().equals(orderId)) {
                order.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean cancelDirectOrder(String orderId) {
        FarmerDirectOrder order = getDirectOrderById(orderId);
        if (order != null) {
            order.setStatus("Cancelled");
            // Restore inventory to listing
            FarmerProduceListing listing = getProduceListingById(order.getListingId());
            if (listing != null) {
                listing.setQuantityAvailable(listing.getQuantityAvailable() + order.getQuantity());
            }
            return true;
        }
        return false;
    }

    // --- Disease Scans Archive ---
    public List<SavedDiseaseScan> getAllDiseaseScans() {
        List<SavedDiseaseScan> list = new ArrayList<>(diseaseScans);
        Collections.sort(list, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return list;
    }

    public synchronized void saveDiseaseScan(SavedDiseaseScan scan) {
        if (scan != null) {
            diseaseScans.add(0, scan);
            // Also update crop health status if crop is matched
            if (scan.getCropName() != null) {
                for (FarmerCrop crop : crops) {
                    if (crop.getName().equalsIgnoreCase(scan.getCropName()) ||
                            crop.getId().equals(scan.getCropId())) {
                        crop.setLatestHealthStatus(scan.isHealthy() ? "Healthy" : "Disease: " + scan.getDiseaseName());
                    }
                }
            }
        }
    }

    public synchronized boolean deleteDiseaseScan(String scanId) {
        return diseaseScans.removeIf(scan -> scan.getId().equals(scanId));
    }

    // --- Profile Getters & Setters ---
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getTotalLandAcres() { return totalLandAcres; }
    public void setTotalLandAcres(String totalLandAcres) { this.totalLandAcres = totalLandAcres; }

    public String getKisanCreditCardNo() { return kisanCreditCardNo; }
    public void setKisanCreditCardNo(String kisanCreditCardNo) { this.kisanCreditCardNo = kisanCreditCardNo; }
}
