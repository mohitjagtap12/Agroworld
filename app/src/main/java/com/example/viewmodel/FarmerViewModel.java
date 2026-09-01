package com.example.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.agriwaste.AgriWasteDataHub;
import com.example.labour.LabourDataHub;
import com.example.model.AgriWasteItem;
import com.example.model.AgriWasteOrder;
import com.example.model.AgriWastePurchaseRequest;
import com.example.model.FarmerActivityItem;
import com.example.model.FarmerCrop;
import com.example.model.FarmerDirectOrder;
import com.example.model.FarmerProduceListing;
import com.example.model.FarmerServiceItem;
import com.example.model.LabourApplication;
import com.example.model.LabourRequirement;
import com.example.model.ProductOrder;
import com.example.model.SavedDiseaseScan;
import com.example.repository.FarmerRepository;
import com.example.seller.SellerDataHub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for the Farmer Module that coordinates crops, services, activities, and profiles.
 */
public class FarmerViewModel extends AndroidViewModel {

    private final FarmerRepository farmerRepository;
    private final LabourDataHub labourDataHub;
    private final AgriWasteDataHub agriWasteDataHub;
    private final SellerDataHub sellerDataHub;
    private final ExecutorService executorService;

    private final MutableLiveData<List<FarmerCrop>> cropsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FarmerProduceListing>> produceListingsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FarmerDirectOrder>> directOrdersLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FarmerServiceItem>> servicesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FarmerActivityItem>> activitiesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<SavedDiseaseScan>> diseaseScansLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public FarmerViewModel(@NonNull Application application) {
        super(application);
        this.farmerRepository = FarmerRepository.getInstance();
        this.labourDataHub = LabourDataHub.getInstance();
        this.agriWasteDataHub = AgriWasteDataHub.getInstance();
        this.sellerDataHub = SellerDataHub.getInstance();
        this.executorService = Executors.newFixedThreadPool(2);

        initServices();
        refreshAllData();
    }

    private void initServices() {
        List<FarmerServiceItem> services = Arrays.asList(
                new FarmerServiceItem("s1", "🌱", "My Crops", "Track sown crops, growth stage & disease history", FarmerServiceItem.ACTION_MY_CROPS, null),
                new FarmerServiceItem("s2", "🤖", "AI Disease Detection", "Scan crop leaf photo for instant diagnosis & remedy", FarmerServiceItem.ACTION_AI_DISEASE, "AI Powered"),
                new FarmerServiceItem("s3", "👨‍🌾", "Hire Labour", "Find skilled farm workers & squads nearby", FarmerServiceItem.ACTION_HIRE_LABOUR, "Fast Match"),
                new FarmerServiceItem("s4", "🏪", "Buy Farming Products", "Seeds, fertilizers, pesticides & equipment", FarmerServiceItem.ACTION_BUY_PRODUCTS, null),
                new FarmerServiceItem("s5", "🤝", "Contract Farming", "Guaranteed corporate buyback with advance pricing", FarmerServiceItem.ACTION_CONTRACT_FARMING, "Verified Deals"),
                new FarmerServiceItem("s6", "♻️", "List Agri Waste", "Sell crop residue, husk & biomass to industry buyers", FarmerServiceItem.ACTION_LIST_AGRI_WASTE, "Eco Earnings"),
                new FarmerServiceItem("s7", "📈", "Broker Trading", "Sell crops in bulk to wholesale buyers & negotiate rates", FarmerServiceItem.ACTION_BROKER_TRADING, "Bulk Demand"),
                new FarmerServiceItem("s8", "🛒", "Sell Farm Produce", "List fresh harvest for direct bulk & retail sale", FarmerServiceItem.ACTION_SELL_PRODUCE, null),
                new FarmerServiceItem("s9", "🚚", "Delivery Partner", "Unified logistics hub for farm, store & waste freight", FarmerServiceItem.ACTION_DELIVERY_PORTAL, "Logistics"),
                new FarmerServiceItem("s10", "📦", "My Activities", "Track orders, labour bookings & deals in one place", FarmerServiceItem.ACTION_MY_ACTIVITIES, null)
        );
        servicesLiveData.setValue(services);
    }

    public void refreshAllData() {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            List<FarmerCrop> crops = farmerRepository.getAllCrops();
            List<SavedDiseaseScan> scans = farmerRepository.getAllDiseaseScans();
            List<FarmerActivityItem> activities = generateUnifiedActivities("All");

            cropsLiveData.postValue(crops);
            diseaseScansLiveData.postValue(scans);
            activitiesLiveData.postValue(activities);
            isLoadingLiveData.postValue(false);
        });
    }

    // --- Crops Management ---
    public LiveData<List<FarmerCrop>> getCropsLiveData() {
        return cropsLiveData;
    }

    public void addCrop(FarmerCrop crop) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            farmerRepository.addCrop(crop);
            cropsLiveData.postValue(farmerRepository.getAllCrops());
            activitiesLiveData.postValue(generateUnifiedActivities("All"));
            isLoadingLiveData.postValue(false);
            messageLiveData.postValue("Crop " + crop.getName() + " added successfully!");
        });
    }

    public void deleteCrop(String cropId) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            farmerRepository.deleteCrop(cropId);
            cropsLiveData.postValue(farmerRepository.getAllCrops());
            isLoadingLiveData.postValue(false);
            messageLiveData.postValue("Crop removed.");
        });
    }

    // --- Services ---
    public LiveData<List<FarmerServiceItem>> getServicesLiveData() {
        return servicesLiveData;
    }

    // --- Activities Management ---
    public LiveData<List<FarmerActivityItem>> getActivitiesLiveData() {
        return activitiesLiveData;
    }

    public void filterActivities(String filter) {
        executorService.execute(() -> {
            List<FarmerActivityItem> list = generateUnifiedActivities(filter);
            activitiesLiveData.postValue(list);
        });
    }

    private List<FarmerActivityItem> generateUnifiedActivities(String filter) {
        List<FarmerActivityItem> all = new ArrayList<>();
        String farmerId = farmerRepository.getFarmerId();

        // 1. Direct Produce Orders
        List<FarmerDirectOrder> directOrders = farmerRepository.getAllDirectOrders();
        for (FarmerDirectOrder order : directOrders) {
            all.add(new FarmerActivityItem(
                    order.getId(),
                    "Direct Produce",
                    "🛒",
                    order.getProduceName() + " (" + order.getQuantity() + " " + order.getUnit() + ")",
                    "Customer: " + order.getCustomerName(),
                    order.getOrderDate(),
                    order.getStatus(),
                    order.getTotalPrice(),
                    "Delivery: " + order.getDeliveryAddress()
            ));
        }

        // 2. Labour Requirements & Applications
        List<LabourRequirement> labourReqs = labourDataHub.getAllRequirements();
        for (LabourRequirement req : labourReqs) {
            all.add(new FarmerActivityItem(
                    req.getId(),
                    "Labour",
                    "👨‍🌾",
                    req.getWorkType() + " (" + req.getWorkersRequired() + " Workers)",
                    "Location: " + req.getVillage(),
                    req.getStartDate(),
                    req.getStatus(),
                    req.getWageAmount() * req.getWorkersRequired(),
                    req.getDescription()
            ));
        }

        // 3. Agri Waste Purchase Requests & Listings
        List<AgriWastePurchaseRequest> wasteReqs = agriWasteDataHub.getRequestsForFarmer(farmerId);
        for (AgriWastePurchaseRequest req : wasteReqs) {
            all.add(new FarmerActivityItem(
                    req.getId(),
                    "Agri Waste",
                    "♻️",
                    req.getWasteName() + " (" + req.getRequestedQuantity() + " " + req.getUnit() + ")",
                    "Buyer: " + req.getBuyerName() + " (" + req.getBuyerType() + ")",
                    req.getRequestDate(),
                    req.getStatus(),
                    req.getTotalAmount(),
                    "Offered Price: ₹" + req.getOfferedPrice() + "/" + req.getPriceUnit()
            ));
        }

        // 4. Farming Product Purchases (Seeds, Fertilizers, Equipment)
        List<ProductOrder> productOrders = sellerDataHub.getOrdersForFarmer(farmerId);
        for (ProductOrder pOrder : productOrders) {
            all.add(new FarmerActivityItem(
                    pOrder.getId(),
                    "Farming Inputs",
                    pOrder.getProductEmoji() != null ? pOrder.getProductEmoji() : "📦",
                    pOrder.getProductName() + " (" + pOrder.getQuantity() + " " + pOrder.getUnit() + ")",
                    "Seller: " + pOrder.getSellerName(),
                    pOrder.getOrderDate(),
                    pOrder.getStatus(),
                    pOrder.getTotalAmount(),
                    "Status: " + pOrder.getStatus() + " • " + pOrder.getPaymentMethod()
            ));
        }

        // 5. Corporate Contract Farming Deals & Applications
        List<com.example.model.ContractApplication> contractApps = com.example.repository.CommerceRepository.getInstance().getApplicationsForFarmer(farmerId);
        for (com.example.model.ContractApplication cApp : contractApps) {
            all.add(new FarmerActivityItem(
                    cApp.getId(),
                    "Contract Farming",
                    cApp.getCropEmoji() != null ? cApp.getCropEmoji() : "🤝",
                    cApp.getCropName() + " Contract (" + cApp.getExpectedQuantityTons() + " Tons)",
                    "Buyer: " + cApp.getCompanyName(),
                    cApp.getSubmittedDate(),
                    cApp.getStatus(),
                    cApp.calculateTotalContractValue(),
                    "Milestone: " + cApp.getCurrentMilestone() + " • Harvest: " + cApp.getExpectedHarvestDate() + " • Area: " + cApp.getLandAreaAcres() + " Acres"
            ));
        }

        // 6. Broker Bulk Trading Offers & Negotiations
        List<com.example.model.FarmerBrokerOffer> brokerOffers = com.example.repository.CommerceRepository.getInstance().getOffersForFarmer(farmerId);
        for (com.example.model.FarmerBrokerOffer offer : brokerOffers) {
            double effPrice = offer.getFinalAgreedPrice() > 0 ? offer.getFinalAgreedPrice() : (offer.getCounterPrice() > 0 ? offer.getCounterPrice() : offer.getExpectedPrice());
            all.add(new FarmerActivityItem(
                    offer.getId(),
                    "Broker",
                    offer.getCropEmoji() != null ? offer.getCropEmoji() : "📈",
                    offer.getCropName() + " Bulk Offer (" + offer.getAvailableQuantity() + " " + offer.getUnit() + ")",
                    "Status: " + offer.getStatus() + (offer.getNegotiationNote() != null ? " • " + offer.getNegotiationNote() : ""),
                    offer.getCreatedAt(),
                    offer.getStatus(),
                    offer.calculateTotalValue(effPrice),
                    "Expected: ₹" + offer.getExpectedPrice() + "/" + offer.getPriceUnit() + " • Date: " + offer.getAvailableDate()
            ));
        }

        // 7. Broker Bulk Trading Deals
        List<com.example.model.BrokerDeal> brokerDeals = com.example.repository.CommerceRepository.getInstance().getDealsForFarmer(farmerId);
        for (com.example.model.BrokerDeal deal : brokerDeals) {
            all.add(new FarmerActivityItem(
                    deal.getId(),
                    "Broker",
                    deal.getCropEmoji() != null ? deal.getCropEmoji() : "🤝",
                    deal.getCropDemanded() + " Bulk Deal (" + deal.getRequiredQty() + " " + deal.getUnit() + ")",
                    "Broker: " + deal.getBrokerName() + " (" + (deal.getCompanyName() != null ? deal.getCompanyName() : "Wholesale Trader") + ")",
                    deal.getCreatedAt() != null ? deal.getCreatedAt() : "Active",
                    deal.getDealStatus(),
                    deal.getTotalValue(),
                    "Agreed: ₹" + deal.getOfferedPricePerQuintal() + "/" + deal.getPriceUnit() + " • Pickup: " + deal.getLocation() + " (" + deal.getPickupDate() + ")"
            ));
        }

        // Apply Status Filter if not "All"
        if ("All".equalsIgnoreCase(filter)) {
            return all;
        }

        List<FarmerActivityItem> filtered = new ArrayList<>();
        for (FarmerActivityItem item : all) {
            if ("Active".equalsIgnoreCase(filter)) {
                if ("Active".equalsIgnoreCase(item.getStatus()) || "Growing".equalsIgnoreCase(item.getStatus()) || "Scheduled".equalsIgnoreCase(item.getStatus()) || "Confirmed".equalsIgnoreCase(item.getStatus()) || "Deal Confirmed".equalsIgnoreCase(item.getStatus()) || "Pickup Scheduled".equalsIgnoreCase(item.getStatus()) || "Negotiating".equalsIgnoreCase(item.getStatus())) {
                    filtered.add(item);
                }
            } else if ("Pending".equalsIgnoreCase(filter)) {
                if ("Pending".equalsIgnoreCase(item.getStatus()) || "Request Sent".equalsIgnoreCase(item.getStatus()) || "Open".equalsIgnoreCase(item.getStatus()) || "Price Agreed".equalsIgnoreCase(item.getStatus())) {
                    filtered.add(item);
                }
            } else if ("Completed".equalsIgnoreCase(filter)) {
                if ("Completed".equalsIgnoreCase(item.getStatus()) || "Delivered".equalsIgnoreCase(item.getStatus()) || "Accepted".equalsIgnoreCase(item.getStatus()) || "Crop Handed Over".equalsIgnoreCase(item.getStatus())) {
                    filtered.add(item);
                }
            }
        }
        return filtered;
    }

    // --- Disease Scans Management ---
    public LiveData<List<SavedDiseaseScan>> getDiseaseScansLiveData() {
        return diseaseScansLiveData;
    }

    public void saveDiseaseScan(SavedDiseaseScan scan) {
        executorService.execute(() -> {
            farmerRepository.saveDiseaseScan(scan);
            diseaseScansLiveData.postValue(farmerRepository.getAllDiseaseScans());
            cropsLiveData.postValue(farmerRepository.getAllCrops());
            messageLiveData.postValue("Diagnosis saved to health records!");
        });
    }

    public void loadDiseaseScansForCrop(String cropName) {
        executorService.execute(() -> {
            List<SavedDiseaseScan> all = farmerRepository.getAllDiseaseScans();
            if (cropName == null || cropName.trim().isEmpty() || "All".equalsIgnoreCase(cropName)) {
                diseaseScansLiveData.postValue(all);
                return;
            }
            List<SavedDiseaseScan> filtered = new ArrayList<>();
            for (SavedDiseaseScan scan : all) {
                if (scan.getCropName() != null && scan.getCropName().toLowerCase().contains(cropName.toLowerCase())) {
                    filtered.add(scan);
                }
            }
            diseaseScansLiveData.postValue(filtered);
        });
    }

    // --- Produce Listings & Direct Orders Management ---
    public LiveData<List<FarmerProduceListing>> getProduceListingsLiveData() {
        return produceListingsLiveData;
    }

    public LiveData<List<FarmerDirectOrder>> getDirectOrdersLiveData() {
        return directOrdersLiveData;
    }

    public void loadProduceListings() {
        executorService.execute(() -> {
            String farmerId = farmerRepository.getFarmerId();
            List<FarmerProduceListing> list = farmerRepository.getFarmerProduceListings(farmerId);
            produceListingsLiveData.postValue(list);
        });
    }

    public void loadDirectOrders() {
        executorService.execute(() -> {
            String farmerId = farmerRepository.getFarmerId();
            List<FarmerDirectOrder> orders = farmerRepository.getFarmerDirectOrders(farmerId);
            directOrdersLiveData.postValue(orders);
        });
    }

    public void addProduceListing(FarmerProduceListing listing) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            farmerRepository.addProduceListing(listing);
            String farmerId = farmerRepository.getFarmerId();
            produceListingsLiveData.postValue(farmerRepository.getFarmerProduceListings(farmerId));
            activitiesLiveData.postValue(generateUnifiedActivities("All"));
            isLoadingLiveData.postValue(false);
            messageLiveData.postValue("Produce " + listing.getProduceName() + " listed successfully!");
        });
    }

    public void updateProduceListing(FarmerProduceListing listing) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            farmerRepository.updateProduceListing(listing);
            String farmerId = farmerRepository.getFarmerId();
            produceListingsLiveData.postValue(farmerRepository.getFarmerProduceListings(farmerId));
            isLoadingLiveData.postValue(false);
            messageLiveData.postValue("Listing updated.");
        });
    }

    public void pauseOrResumeListing(String listingId) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            farmerRepository.pauseOrResumeProduceListing(listingId);
            String farmerId = farmerRepository.getFarmerId();
            produceListingsLiveData.postValue(farmerRepository.getFarmerProduceListings(farmerId));
            isLoadingLiveData.postValue(false);
            messageLiveData.postValue("Listing status updated.");
        });
    }

    public void deleteProduceListing(String listingId) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            farmerRepository.deleteProduceListing(listingId);
            String farmerId = farmerRepository.getFarmerId();
            produceListingsLiveData.postValue(farmerRepository.getFarmerProduceListings(farmerId));
            isLoadingLiveData.postValue(false);
            messageLiveData.postValue("Produce listing deleted.");
        });
    }

    public void updateDirectOrderStatus(String orderId, String newStatus) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            farmerRepository.updateOrderStatus(orderId, newStatus);
            String farmerId = farmerRepository.getFarmerId();
            directOrdersLiveData.postValue(farmerRepository.getFarmerDirectOrders(farmerId));
            activitiesLiveData.postValue(generateUnifiedActivities("All"));
            isLoadingLiveData.postValue(false);
            messageLiveData.postValue("Order " + orderId + " updated to " + newStatus);
        });
    }

    // --- Profile Management ---
    public FarmerRepository getFarmerRepository() {
        return farmerRepository;
    }

    public void updateProfile(String name, String phone, String village, String taluka, String district, String landArea) {
        farmerRepository.setFarmerName(name);
        farmerRepository.setFarmerPhone(phone);
        farmerRepository.setVillage(village);
        farmerRepository.setTaluka(taluka);
        farmerRepository.setDistrict(district);
        farmerRepository.setTotalLandAcres(landArea);
        messageLiveData.postValue("Profile updated successfully!");
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }
}
