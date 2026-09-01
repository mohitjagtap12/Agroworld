package com.example.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agriwaste.AgriWasteDataHub;
import com.example.model.AgriWasteItem;
import com.example.model.AgriWasteOrder;
import com.example.model.AgriWastePurchaseRequest;
import com.example.model.WasteTypeMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel managing state and operations for Farmer Agri Waste and Buyer Marketplace.
 */
public class AgriWasteViewModel extends ViewModel {

    private final AgriWasteDataHub dataHub = AgriWasteDataHub.getInstance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<AgriWasteItem>> farmerListingsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<AgriWastePurchaseRequest>> buyerRequestsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<AgriWasteOrder>> ordersLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<AgriWasteItem>> marketplaceListingsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<WasteTypeMetadata>> wasteTypesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> operationSuccessLiveData = new MutableLiveData<>();

    private final String currentFarmerId = "FARMER_MH_01";

    public AgriWasteViewModel() {
        refreshAll();
    }

    public LiveData<List<AgriWasteItem>> getFarmerListingsLiveData() {
        return farmerListingsLiveData;
    }

    public LiveData<List<AgriWastePurchaseRequest>> getBuyerRequestsLiveData() {
        return buyerRequestsLiveData;
    }

    public LiveData<List<AgriWasteOrder>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public LiveData<List<AgriWasteItem>> getMarketplaceListingsLiveData() {
        return marketplaceListingsLiveData;
    }

    public LiveData<List<WasteTypeMetadata>> getWasteTypesLiveData() {
        return wasteTypesLiveData;
    }

    public LiveData<Boolean> getOperationSuccessLiveData() {
        return operationSuccessLiveData;
    }

    public void refreshAll() {
        executor.execute(() -> {
            List<AgriWasteItem> myListings = dataHub.getFarmerListings(currentFarmerId);
            farmerListingsLiveData.postValue(myListings);

            List<AgriWastePurchaseRequest> requests = dataHub.getRequestsForFarmer(currentFarmerId);
            buyerRequestsLiveData.postValue(requests);

            List<AgriWasteOrder> orders = dataHub.getOrdersForFarmer(currentFarmerId);
            ordersLiveData.postValue(orders);

            List<AgriWasteItem> allAvailable = dataHub.getAllAvailableListings();
            marketplaceListingsLiveData.postValue(allAvailable);

            wasteTypesLiveData.postValue(dataHub.getWasteTypes());
        });
    }

    public void addListing(AgriWasteItem item) {
        executor.execute(() -> {
            dataHub.addListing(item);
            refreshAll();
            operationSuccessLiveData.postValue(true);
        });
    }

    public void updateListing(AgriWasteItem item) {
        executor.execute(() -> {
            boolean success = dataHub.updateListing(item);
            refreshAll();
            operationSuccessLiveData.postValue(success);
        });
    }

    public void deleteListing(String listingId) {
        executor.execute(() -> {
            boolean success = dataHub.deleteListing(listingId);
            refreshAll();
            operationSuccessLiveData.postValue(success);
        });
    }

    public void respondToBuyerRequest(String requestId, boolean accept) {
        executor.execute(() -> {
            boolean success = dataHub.respondToPurchaseRequest(requestId, accept);
            refreshAll();
            operationSuccessLiveData.postValue(success);
        });
    }

    public void submitPurchaseRequest(AgriWastePurchaseRequest request) {
        executor.execute(() -> {
            dataHub.submitPurchaseRequest(request);
            refreshAll();
            operationSuccessLiveData.postValue(true);
        });
    }

    public void filterMarketplace(String query, String category) {
        executor.execute(() -> {
            List<AgriWasteItem> all = dataHub.getAllAvailableListings();
            List<AgriWasteItem> filtered = new ArrayList<>();

            String lowerQuery = query != null ? query.trim().toLowerCase() : "";
            boolean hasCategoryFilter = category != null && !category.isEmpty() && !"All".equalsIgnoreCase(category);

            for (AgriWasteItem item : all) {
                boolean matchesCategory = !hasCategoryFilter ||
                        category.equalsIgnoreCase(item.getCategory()) ||
                        category.equalsIgnoreCase(item.getWasteType());

                boolean matchesQuery = lowerQuery.isEmpty() ||
                        item.getWasteName().toLowerCase().contains(lowerQuery) ||
                        item.getWasteType().toLowerCase().contains(lowerQuery) ||
                        item.getDescription().toLowerCase().contains(lowerQuery) ||
                        item.getVillage().toLowerCase().contains(lowerQuery) ||
                        item.getTaluka().toLowerCase().contains(lowerQuery);

                if (matchesCategory && matchesQuery) {
                    filtered.add(item);
                }
            }
            marketplaceListingsLiveData.postValue(filtered);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
