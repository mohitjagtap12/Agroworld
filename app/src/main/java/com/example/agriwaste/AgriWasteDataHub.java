package com.example.agriwaste;

import com.example.model.AgriWasteItem;
import com.example.model.AgriWasteOrder;
import com.example.model.AgriWastePurchaseRequest;
import com.example.model.WasteTypeMetadata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe Central Data Hub for the Agri Waste Marketplace.
 * Enforces strict role boundaries:
 * - Farmer: Only Lists, Edits, Deletes, Views Own Listings, and Accepts/Rejects buyer requests. Cannot buy.
 * - Buyer: Browses, Searches, Filters, Views Details, and Sends Purchase Requests / Orders.
 */
public class AgriWasteDataHub {

    private static volatile AgriWasteDataHub instance;

    private final CopyOnWriteArrayList<AgriWasteItem> listings = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<AgriWasteOrder> orders = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<AgriWastePurchaseRequest> purchaseRequests = new CopyOnWriteArrayList<>();
    private final List<WasteTypeMetadata> wasteTypes = new ArrayList<>();

    private AgriWasteDataHub() {
        initWasteTypes();
        seedInitialListings();
    }

    public static AgriWasteDataHub getInstance() {
        if (instance == null) {
            synchronized (AgriWasteDataHub.class) {
                if (instance == null) {
                    instance = new AgriWasteDataHub();
                }
            }
        }
        return instance;
    }

    private void initWasteTypes() {
        wasteTypes.add(new WasteTypeMetadata(
                "Wheat Straw", "🌾", "Straw", "kg", "₹/kg",
                Arrays.asList("Animal fodder & cattle feed", "Mushroom cultivation substrate", "Organic vermicompost", "Biomass briquettes"),
                "High demand from dairy clusters & biomass plants"
        ));
        wasteTypes.add(new WasteTypeMetadata(
                "Rice Straw", "🌾", "Straw", "ton", "₹/ton",
                Arrays.asList("Cattle bedding & feed", "Paddy mushroom growing", "Bio-energy power plants", "Eco-packaging boards"),
                "Crucial for stubble burning prevention & bio-energy"
        ));
        wasteTypes.add(new WasteTypeMetadata(
                "Sugarcane Residue", "🎋", "Sugarcane Residue", "ton", "₹/ton",
                Arrays.asList("Soil mulching & moisture conservation", "Sugar mill cogeneration boilers", "Compost & vermiwash", "Eco-paper pulp"),
                "Very high calorific value for industrial boilers"
        ));
        wasteTypes.add(new WasteTypeMetadata(
                "Maize Stalk", "🌽", "Crop Stalks", "ton", "₹/ton",
                Arrays.asList("Nutritious silage & green fodder", "Industrial boiler fuel", "Field mulch cover", "Bio-char conversion"),
                "Essential for dairy silage in summer"
        ));
        wasteTypes.add(new WasteTypeMetadata(
                "Cotton Stalk", "🌱", "Crop Stalks", "ton", "₹/ton",
                Arrays.asList("High-density biomass briquettes", "Particle board manufacturing", "Industrial boiler fuel", "Activated carbon"),
                "Preferred wood substitute for biomass fuel"
        ));
        wasteTypes.add(new WasteTypeMetadata(
                "Soybean Residue", "🫘", "Other Crop Residue", "quintal", "₹/quintal",
                Arrays.asList("High protein milch cattle fodder", "Soil nitrogen fixation compost", "Animal bedding"),
                "Rapidly purchased by local dairy farmers"
        ));
        wasteTypes.add(new WasteTypeMetadata(
                "Groundnut Shell", "🥜", "Husk / Shell", "quintal", "₹/quintal",
                Arrays.asList("Biofuel pellets & briquettes", "Poultry farm bedding & litter", "Activated carbon filtration"),
                "High heating value for heating plants"
        ));
        wasteTypes.add(new WasteTypeMetadata(
                "Coconut Husk", "🥥", "Husk / Shell", "bundle", "₹/bundle",
                Arrays.asList("Coir fiber rope & geotextiles", "Coco-peat for plant nurseries & hydroponics", "Soil water-retention mulching"),
                "Huge export & domestic nursery demand"
        ));
        wasteTypes.add(new WasteTypeMetadata(
                "Other", "♻️", "Other Crop Residue", "kg", "₹/kg",
                Arrays.asList("General farm composting", "Bio-char generation", "Soil mulching", "Local biomass fuel"),
                "Eco-friendly zero-waste farm management"
        ));
    }

    private void seedInitialListings() {
        listings.add(new AgriWasteItem(
                "AW-LST-101", "FARMER_MH_01", "Rameshwar Patil", "+91 98220 12345",
                "Wheat Straw", "Clean Golden Wheat Straw", "Straw", 15.0, 15.0,
                "ton", 1800.0, "₹/ton", "30 Aug 2026", "Narayangaon", "Junnar", "Pune",
                3.5, "Dry, well packed golden wheat straw. Ideal for biomass pellets & animal feed.",
                "🌾", "Both Supported", "Available", "26 Aug 2026", "26 Aug 2026"
        ));
        listings.add(new AgriWasteItem(
                "AW-LST-102", "FARMER_MH_01", "Rameshwar Patil", "+91 98220 12345",
                "Sugarcane Residue", "Shredded Sugarcane Bagasse & Tops", "Sugarcane Residue", 25.0, 25.0,
                "ton", 1400.0, "₹/ton", "05 Sept 2026", "Narayangaon", "Junnar", "Pune",
                4.2, "High calorific value sugarcane dry residue and bagasse for cogeneration plants.",
                "🎋", "Delivery Partner", "Available", "26 Aug 2026", "26 Aug 2026"
        ));
        listings.add(new AgriWasteItem(
                "AW-LST-103", "f_anand", "Anand Kadam", "+91 94225 99887",
                "Rice Straw", "High Quality Paddy Straw Bundles", "Straw", 10.0, 10.0,
                "ton", 1600.0, "₹/ton", "02 Sept 2026", "Ozar", "Junnar", "Pune",
                6.0, "Well tied paddy straw bales for mushroom cultivation and dairy bedding.",
                "🌾", "Both Supported", "Available", "25 Aug 2026", "25 Aug 2026"
        ));

        // Sample Buyer Purchase Request to Farmer
        purchaseRequests.add(new AgriWastePurchaseRequest(
                "REQ-AW-501", "AW-LST-101", "FARMER_MH_01", "Rameshwar Patil",
                "BUYER_01", "GreenBio Energy Solutions", "+91 98900 77112", "Biomass Plant",
                "Clean Golden Wheat Straw", 10.0, "ton", 1750.0, "₹/ton", 17500.0,
                "02 Sept 2026", "MIDC Chakan Industrial Area, Pune", "Pending",
                "26 Aug 2026", "Need bulk dispatch for our biomass briquetting unit."
        ));
    }

    public List<WasteTypeMetadata> getWasteTypes() {
        return new ArrayList<>(wasteTypes);
    }

    // --- Farmer Scope: Listing Management ---
    public List<AgriWasteItem> getFarmerListings(String farmerId) {
        List<AgriWasteItem> result = new ArrayList<>();
        for (AgriWasteItem item : listings) {
            if (item.getFarmerId().equals(farmerId)) {
                result.add(item);
            }
        }
        return result;
    }

    public synchronized void addListing(AgriWasteItem item) {
        if (item != null) {
            listings.add(0, item);
        }
    }

    public synchronized boolean updateListing(AgriWasteItem updatedItem) {
        if (updatedItem == null) return false;
        for (int i = 0; i < listings.size(); i++) {
            if (listings.get(i).getId().equals(updatedItem.getId())) {
                listings.set(i, updatedItem);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteListing(String listingId) {
        return listings.removeIf(item -> item.getId().equals(listingId));
    }

    // --- Buyer Scope: Browsing & Ordering ---
    public List<AgriWasteItem> getAllAvailableListings() {
        List<AgriWasteItem> result = new ArrayList<>();
        for (AgriWasteItem item : listings) {
            if ("Available".equalsIgnoreCase(item.getStatus())) {
                result.add(item);
            }
        }
        return result;
    }

    public AgriWasteItem getListingById(String id) {
        for (AgriWasteItem item : listings) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    // --- Purchase Requests Workflow ---
    public List<AgriWastePurchaseRequest> getRequestsForFarmer(String farmerId) {
        List<AgriWastePurchaseRequest> result = new ArrayList<>();
        for (AgriWastePurchaseRequest req : purchaseRequests) {
            if (req.getFarmerId().equals(farmerId)) {
                result.add(req);
            }
        }
        return result;
    }

    public synchronized void submitPurchaseRequest(AgriWastePurchaseRequest request) {
        if (request != null) {
            purchaseRequests.add(0, request);
        }
    }

    public synchronized boolean respondToPurchaseRequest(String requestId, boolean accept) {
        for (AgriWastePurchaseRequest req : purchaseRequests) {
            if (req.getId().equals(requestId)) {
                req.setStatus(accept ? "Accepted" : "Rejected");
                if (accept) {
                    // Create an order in the pipeline
                    String orderId = "ORD-AW-" + System.currentTimeMillis();
                    AgriWasteOrder order = new AgriWasteOrder(
                            orderId, req.getListingId(), req.getFarmerId(), req.getFarmerName(), "",
                            req.getBuyerId(), req.getBuyerName(), req.getBuyerPhone(), req.getBuyerType(),
                            req.getWasteName(), "Straw", req.getRequestedQuantity(), req.getUnit(),
                            req.getOfferedPrice(), req.getPriceUnit(), req.getTotalAmount(),
                            "Delivery Partner", req.getDeliveryAddress(), "Narayangaon", "Junnar", "Pune",
                            null, null, null, "Accepted",
                            new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(new Date()),
                            req.getProposedPickupDate(), null, req.getNotes()
                    );
                    orders.add(0, order);
                }
                return true;
            }
        }
        return false;
    }

    // --- Orders Management ---
    public List<AgriWasteOrder> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public List<AgriWasteOrder> getOrdersForFarmer(String farmerId) {
        List<AgriWasteOrder> result = new ArrayList<>();
        for (AgriWasteOrder order : orders) {
            if (order.getFarmerId().equals(farmerId)) {
                result.add(order);
            }
        }
        return result;
    }

    public AgriWasteOrder getOrderById(String orderId) {
        if (orderId == null) return null;
        for (AgriWasteOrder order : orders) {
            if (order.getId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public synchronized boolean updateOrderStatus(String orderId, String newStatus) {
        for (AgriWasteOrder order : orders) {
            if (order.getId().equals(orderId)) {
                order.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }
}
