package com.example.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.model.FarmerDirectOrder;
import com.example.model.FarmerProduceListing;
import com.example.repository.FarmerRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for the Customer Module to browse fresh produce, search, filter,
 * buy direct from farmers with instant inventory validation, and track orders.
 */
public class CustomerViewModel extends AndroidViewModel {

    private final FarmerRepository farmerRepository;
    private final ExecutorService executorService;

    private final MutableLiveData<List<FarmerProduceListing>> produceListingsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FarmerDirectOrder>> customerOrdersLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> orderPlacedSuccessLiveData = new MutableLiveData<>(false);

    // Filter states
    private String selectedCategory = "All";
    private String selectedTaluka = "All";
    private String searchQuery = "";
    private String currentCustomerId = "cust_1";
    private String currentCustomerName = "Abhishek Sharma";
    private String currentCustomerPhone = "+91 98765 43210";
    private String currentDeliveryAddress = "Flat 402, Building B, Kothrud Heights, Pune";
    private String currentVillage = "Kothrud";
    private String currentTaluka = "Haveli";
    private String currentDistrict = "Pune";

    public CustomerViewModel(@NonNull Application application) {
        super(application);
        this.farmerRepository = FarmerRepository.getInstance();
        this.executorService = Executors.newFixedThreadPool(2);
        loadProduceListings();
        loadCustomerOrders();
    }

    public LiveData<List<FarmerProduceListing>> getProduceListingsLiveData() {
        return produceListingsLiveData;
    }

    public LiveData<List<FarmerDirectOrder>> getCustomerOrdersLiveData() {
        return customerOrdersLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public LiveData<Boolean> getOrderPlacedSuccessLiveData() {
        return orderPlacedSuccessLiveData;
    }

    public void resetOrderSuccessFlag() {
        orderPlacedSuccessLiveData.setValue(false);
    }

    public void loadProduceListings() {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            List<FarmerProduceListing> all = farmerRepository.getAllProduceListings();
            List<FarmerProduceListing> filtered = new ArrayList<>();

            for (FarmerProduceListing item : all) {
                // Must be available or partially sold (not paused or cancelled, and quantity > 0)
                if ("Paused".equalsIgnoreCase(item.getStatus()) || "Cancelled".equalsIgnoreCase(item.getStatus())) {
                    continue;
                }

                // Category filter
                boolean categoryMatches = selectedCategory.equalsIgnoreCase("All") ||
                        (item.getCategory() != null && item.getCategory().equalsIgnoreCase(selectedCategory));

                // Taluka / Location filter
                boolean talukaMatches = selectedTaluka.equalsIgnoreCase("All") ||
                        (item.getTaluka() != null && item.getTaluka().equalsIgnoreCase(selectedTaluka)) ||
                        (item.getVillage() != null && item.getVillage().equalsIgnoreCase(selectedTaluka));

                // Search query
                boolean searchMatches = searchQuery.trim().isEmpty() ||
                        (item.getProduceName() != null && item.getProduceName().toLowerCase().contains(searchQuery.toLowerCase())) ||
                        (item.getFarmerName() != null && item.getFarmerName().toLowerCase().contains(searchQuery.toLowerCase())) ||
                        (item.getVillage() != null && item.getVillage().toLowerCase().contains(searchQuery.toLowerCase())) ||
                        (item.getTaluka() != null && item.getTaluka().toLowerCase().contains(searchQuery.toLowerCase()));

                if (categoryMatches && talukaMatches && searchMatches) {
                    filtered.add(item);
                }
            }

            produceListingsLiveData.postValue(filtered);
            isLoadingLiveData.postValue(false);
        });
    }

    public void setCategoryFilter(String category) {
        this.selectedCategory = category != null ? category : "All";
        loadProduceListings();
    }

    public void setTalukaFilter(String taluka) {
        this.selectedTaluka = taluka != null ? taluka : "All";
        loadProduceListings();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query : "";
        loadProduceListings();
    }

    public void loadCustomerOrders() {
        executorService.execute(() -> {
            List<FarmerDirectOrder> orders = farmerRepository.getCustomerDirectOrders(currentCustomerId);
            // Sort by most recent
            Collections.sort(orders, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
            customerOrdersLiveData.postValue(orders);
        });
    }

    public void placeProduceOrder(String listingId, double orderQty, String custName,
                                  String custPhone, String deliveryAddress, String village,
                                  String taluka, String district, String paymentMethod) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            FarmerProduceListing listing = farmerRepository.getProduceListingById(listingId);
            if (listing == null) {
                isLoadingLiveData.postValue(false);
                messageLiveData.postValue("Produce listing not found!");
                return;
            }

            if (listing.getQuantityAvailable() < orderQty) {
                isLoadingLiveData.postValue(false);
                messageLiveData.postValue("Sorry! Only " + listing.getQuantityAvailable() + " " + listing.getUnit() + " available.");
                return;
            }

            // Decrement inventory atomically
            boolean deducted = farmerRepository.decrementProduceInventory(listingId, orderQty);
            if (!deducted) {
                isLoadingLiveData.postValue(false);
                messageLiveData.postValue("Unable to reserve quantity. Please try again.");
                return;
            }

            double totalAmount = orderQty * listing.getPricePerKg();
            String orderId = "ORD-" + (1000 + (int)(Math.random() * 9000));
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
            String todayStr = sdf.format(new java.util.Date());

            FarmerDirectOrder order = new FarmerDirectOrder(
                    orderId,
                    listing.getId(),
                    listing.getFarmerId(),
                    listing.getFarmerName(),
                    listing.getProduceName(),
                    listing.getCategory(),
                    currentCustomerId,
                    custName != null && !custName.isEmpty() ? custName : currentCustomerName,
                    custPhone != null && !custPhone.isEmpty() ? custPhone : currentCustomerPhone,
                    deliveryAddress != null && !deliveryAddress.isEmpty() ? deliveryAddress : currentDeliveryAddress,
                    village != null && !village.isEmpty() ? village : currentVillage,
                    taluka != null && !taluka.isEmpty() ? taluka : currentTaluka,
                    district != null && !district.isEmpty() ? district : currentDistrict,
                    orderQty,
                    listing.getUnit(),
                    listing.getPricePerKg(),
                    totalAmount,
                    todayStr,
                    "In 2-3 Days",
                    "Order Placed",
                    paymentMethod != null ? paymentMethod : "Paid Online",
                    listing.getImageEmoji()
            );

            farmerRepository.addDirectOrder(order);

            // Refresh produce listings and customer orders
            loadProduceListings();
            loadCustomerOrders();

            isLoadingLiveData.postValue(false);
            orderPlacedSuccessLiveData.postValue(true);
            messageLiveData.postValue("Order " + orderId + " placed successfully!");
        });
    }

    public void cancelCustomerOrder(String orderId) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean cancelled = farmerRepository.cancelDirectOrder(orderId);
            if (cancelled) {
                loadProduceListings();
                loadCustomerOrders();
                isLoadingLiveData.postValue(false);
                messageLiveData.postValue("Order " + orderId + " cancelled. Stock restored.");
            } else {
                isLoadingLiveData.postValue(false);
                messageLiveData.postValue("Failed to cancel order.");
            }
        });
    }

    public String getCurrentCustomerId() { return currentCustomerId; }
    public String getCurrentCustomerName() { return currentCustomerName; }
    public String getCurrentCustomerPhone() { return currentCustomerPhone; }
    public String getCurrentDeliveryAddress() { return currentDeliveryAddress; }
    public String getCurrentVillage() { return currentVillage; }
    public String getCurrentTaluka() { return currentTaluka; }
    public String getCurrentDistrict() { return currentDistrict; }

    public void updateCustomerProfile(String name, String phone, String address, String village, String taluka) {
        this.currentCustomerName = name;
        this.currentCustomerPhone = phone;
        this.currentDeliveryAddress = address;
        this.currentVillage = village;
        this.currentTaluka = taluka;
        messageLiveData.postValue("Delivery profile updated.");
    }
}
