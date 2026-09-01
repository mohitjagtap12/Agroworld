package com.example.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.model.BrokerDeal;
import com.example.model.BrokerRequirement;
import com.example.model.FarmerBrokerOffer;
import com.example.model.FarmerCrop;
import com.example.repository.CommerceRepository;
import com.example.repository.FarmerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for Broker Wholesale Trading Module and Farmer Bulk Selling experience.
 */
public class BrokerViewModel extends AndroidViewModel {

    private final CommerceRepository commerceRepository;
    private final FarmerRepository farmerRepository;
    private final ExecutorService executorService;

    // Farmer view LiveData
    private final MutableLiveData<List<BrokerRequirement>> requirementsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FarmerBrokerOffer>> farmerOffersLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<BrokerDeal>> farmerDealsLiveData = new MutableLiveData<>(new ArrayList<>());

    // Broker view LiveData
    private final MutableLiveData<List<BrokerRequirement>> brokerMyRequirementsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FarmerBrokerOffer>> brokerIncomingOffersLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<BrokerDeal>> brokerDealsLiveData = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> currentQueryLiveData = new MutableLiveData<>("");
    private final MutableLiveData<String> currentFilterLiveData = new MutableLiveData<>("All");
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public BrokerViewModel(@NonNull Application application) {
        super(application);
        this.commerceRepository = CommerceRepository.getInstance();
        this.farmerRepository = FarmerRepository.getInstance();
        this.executorService = Executors.newFixedThreadPool(2);

        refreshData();
    }

    public void refreshData() {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            String farmerId = farmerRepository.getFarmerId();
            List<FarmerCrop> farmerCrops = farmerRepository.getAllCrops();
            String query = currentQueryLiveData.getValue();
            String filter = currentFilterLiveData.getValue();

            // 1. Requirements for Farmer
            List<BrokerRequirement> filteredReqs = commerceRepository.searchAndFilterRequirements(query, filter, farmerCrops);
            // 2. Offers for Farmer
            List<FarmerBrokerOffer> farmerOffers = commerceRepository.getOffersForFarmer(farmerId);
            // 3. Deals for Farmer
            List<BrokerDeal> farmerDeals = commerceRepository.getDealsForFarmer(farmerId);

            // 4. Broker side data
            List<BrokerRequirement> myReqs = commerceRepository.getAllBrokerRequirements();
            List<FarmerBrokerOffer> allOffers = commerceRepository.getAllFarmerBrokerOffers();
            List<BrokerDeal> allDeals = commerceRepository.getAllBrokerDeals();

            requirementsLiveData.postValue(filteredReqs);
            farmerOffersLiveData.postValue(farmerOffers);
            farmerDealsLiveData.postValue(farmerDeals);

            brokerMyRequirementsLiveData.postValue(myReqs);
            brokerIncomingOffersLiveData.postValue(allOffers);
            brokerDealsLiveData.postValue(allDeals);

            isLoadingLiveData.postValue(false);
        });
    }

    public LiveData<List<BrokerRequirement>> getRequirementsLiveData() {
        return requirementsLiveData;
    }

    public LiveData<List<FarmerBrokerOffer>> getFarmerOffersLiveData() {
        return farmerOffersLiveData;
    }

    public LiveData<List<BrokerDeal>> getFarmerDealsLiveData() {
        return farmerDealsLiveData;
    }

    public LiveData<List<BrokerRequirement>> getBrokerMyRequirementsLiveData() {
        return brokerMyRequirementsLiveData;
    }

    public LiveData<List<FarmerBrokerOffer>> getBrokerIncomingOffersLiveData() {
        return brokerIncomingOffersLiveData;
    }

    public LiveData<List<BrokerDeal>> getBrokerDealsLiveData() {
        return brokerDealsLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public LiveData<String> getCurrentFilterLiveData() {
        return currentFilterLiveData;
    }

    public void setSearchQuery(String query) {
        currentQueryLiveData.setValue(query);
        refreshData();
    }

    public void setCategoryFilter(String filter) {
        currentFilterLiveData.setValue(filter);
        refreshData();
    }

    // --- Farmer Actions ---

    public void submitFarmerOffer(FarmerBrokerOffer offer, Runnable onSuccess) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.submitFarmerBrokerOffer(offer);
            if (success) {
                messageLiveData.postValue("Bulk offer submitted to " + offer.getCropName() + " buyer!");
                refreshData();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else {
                messageLiveData.postValue("You already have an active offer for this requirement.");
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void farmerCounterOffer(String offerId, double counterPrice, String note, Runnable onSuccess) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.farmerCounterOffer(offerId, counterPrice, note);
            if (success) {
                messageLiveData.postValue("Counter-offer of ₹" + counterPrice + " sent to broker!");
                refreshData();
                if (onSuccess != null) onSuccess.run();
            } else {
                messageLiveData.postValue("Failed to submit counter-offer.");
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void farmerAcceptCounterOffer(String offerId, Runnable onSuccess) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.farmerAcceptCounterOffer(offerId);
            if (success) {
                messageLiveData.postValue("Counter offer accepted! Deal confirmed 🎉");
                refreshData();
                if (onSuccess != null) onSuccess.run();
            } else {
                messageLiveData.postValue("Failed to confirm deal.");
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void farmerCancelOffer(String offerId, Runnable onSuccess) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.farmerRejectOffer(offerId);
            if (success) {
                messageLiveData.postValue("Offer withdrawn.");
                refreshData();
                if (onSuccess != null) onSuccess.run();
            } else {
                messageLiveData.postValue("Failed to withdraw offer.");
                isLoadingLiveData.postValue(false);
            }
        });
    }

    // --- Broker Actions ---

    public void brokerCounterOffer(String offerId, double counterPrice, String note, Runnable onSuccess) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.brokerCounterOffer(offerId, counterPrice, note);
            if (success) {
                messageLiveData.postValue("Counter-offer sent to farmer!");
                refreshData();
                if (onSuccess != null) onSuccess.run();
            } else {
                messageLiveData.postValue("Failed to send counter-offer.");
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void brokerAcceptOffer(String offerId, String pickupDate, String pickupLocation, Runnable onSuccess) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.brokerAcceptOffer(offerId, pickupDate, pickupLocation);
            if (success) {
                messageLiveData.postValue("Farmer offer accepted & Deal Confirmed! 🤝");
                refreshData();
                if (onSuccess != null) onSuccess.run();
            } else {
                messageLiveData.postValue("Failed to accept offer.");
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void brokerRejectOffer(String offerId, String reason, Runnable onSuccess) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.brokerRejectOffer(offerId, reason);
            if (success) {
                messageLiveData.postValue("Offer declined.");
                refreshData();
                if (onSuccess != null) onSuccess.run();
            } else {
                messageLiveData.postValue("Failed to decline offer.");
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void createBrokerRequirement(BrokerRequirement req, Runnable onSuccess) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            commerceRepository.addBrokerRequirement(req);
            messageLiveData.postValue("Wholesale buying requirement published successfully!");
            refreshData();
            if (onSuccess != null) onSuccess.run();
        });
    }

    public void closeBrokerRequirement(String reqId) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.closeBrokerRequirement(reqId);
            if (success) {
                messageLiveData.postValue("Requirement closed.");
                refreshData();
            }
        });
    }

    public void updateDealStatus(String dealId, String newStatus, Runnable onSuccess) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.updateBrokerDealStatus(dealId, newStatus);
            if (success) {
                messageLiveData.postValue("Deal status updated to: " + newStatus);
                refreshData();
                if (onSuccess != null) onSuccess.run();
            } else {
                messageLiveData.postValue("Failed to update deal status.");
                isLoadingLiveData.postValue(false);
            }
        });
    }
}
