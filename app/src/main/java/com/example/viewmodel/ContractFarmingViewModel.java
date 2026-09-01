package com.example.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.model.CompanyProfile;
import com.example.model.ContractApplication;
import com.example.model.ContractFarmingDeal;
import com.example.model.FarmerCrop;
import com.example.repository.CommerceRepository;
import com.example.repository.FarmerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel governing Contract Farming for both Farmer (Application & Deal Tracking)
 * and Company (Deal Publishing & Application Management).
 */
public class ContractFarmingViewModel extends AndroidViewModel {

    private final CommerceRepository commerceRepository;
    private final FarmerRepository farmerRepository;
    private final ExecutorService executorService;

    // Farmer Side LiveData
    private final MutableLiveData<List<ContractFarmingDeal>> availableContractsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ContractApplication>> farmerApplicationsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ContractApplication>> activeContractsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ContractApplication>> completedContractsLiveData = new MutableLiveData<>(new ArrayList<>());

    // Company Side LiveData
    private final MutableLiveData<List<ContractFarmingDeal>> companyContractsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ContractApplication>> companyApplicationsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<CompanyProfile> companyProfileLiveData = new MutableLiveData<>();

    // Status & UI States
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> statusMessageLiveData = new MutableLiveData<>();

    // Current filter states
    private String currentQuery = "";
    private String currentCategory = "All";

    public ContractFarmingViewModel(@NonNull Application application) {
        super(application);
        this.commerceRepository = CommerceRepository.getInstance();
        this.farmerRepository = FarmerRepository.getInstance();
        this.executorService = Executors.newFixedThreadPool(2);

        refreshAllData();
    }

    public void refreshAllData() {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            String farmerId = farmerRepository.getFarmerId();
            List<FarmerCrop> farmerCrops = farmerRepository.getAllCrops();

            List<ContractFarmingDeal> filteredContracts = commerceRepository.searchAndFilter(currentQuery, currentCategory, farmerCrops);
            List<ContractApplication> allFarmerApps = commerceRepository.getApplicationsForFarmer(farmerId);

            List<ContractApplication> pendingApps = new ArrayList<>();
            List<ContractApplication> activeApps = new ArrayList<>();
            List<ContractApplication> completedApps = new ArrayList<>();

            for (ContractApplication app : allFarmerApps) {
                String status = app.getStatus();
                if ("Completed".equalsIgnoreCase(status)) {
                    completedApps.add(app);
                } else if ("Active".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status) ||
                           "Confirmed".equalsIgnoreCase(status) || "Harvest Ready".equalsIgnoreCase(status) ||
                           "Delivered".equalsIgnoreCase(status)) {
                    activeApps.add(app);
                } else {
                    pendingApps.add(app);
                }
            }

            // Company side data
            CompanyProfile profile = commerceRepository.getCompanyProfile();
            String compId = profile != null ? profile.getId() : "comp_01";
            List<ContractFarmingDeal> compContracts = commerceRepository.getContractsByCompany(compId);
            List<ContractApplication> compApps = commerceRepository.getApplicationsForCompany(compId);

            availableContractsLiveData.postValue(filteredContracts);
            farmerApplicationsLiveData.postValue(pendingApps);
            activeContractsLiveData.postValue(activeApps);
            completedContractsLiveData.postValue(completedApps);

            companyContractsLiveData.postValue(compContracts);
            companyApplicationsLiveData.postValue(compApps);
            companyProfileLiveData.postValue(profile);

            isLoadingLiveData.postValue(false);
        });
    }

    public void filterContracts(String query, String category) {
        this.currentQuery = query;
        this.currentCategory = category;
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            List<FarmerCrop> farmerCrops = farmerRepository.getAllCrops();
            List<ContractFarmingDeal> result = commerceRepository.searchAndFilter(query, category, farmerCrops);
            availableContractsLiveData.postValue(result);
            isLoadingLiveData.postValue(false);
        });
    }

    public void applyForContract(ContractApplication application) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean success = commerceRepository.submitApplication(application);
            if (success) {
                statusMessageLiveData.postValue("Application submitted successfully for " + application.getCropName() + "!");
                refreshAllData();
            } else {
                statusMessageLiveData.postValue("You have already submitted an active application for this contract.");
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void createCompanyContract(ContractFarmingDeal deal) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            commerceRepository.addContract(deal);
            statusMessageLiveData.postValue("Contract published for " + deal.getCropName() + " (" + deal.getRequiredQuantityTons() + " Tons)!");
            refreshAllData();
        });
    }

    public void closeContract(String contractId) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean closed = commerceRepository.closeContract(contractId);
            if (closed) {
                statusMessageLiveData.postValue("Contract closed for new applications.");
                refreshAllData();
            } else {
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void updateApplicationStatus(String appId, String status, String note) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean updated = commerceRepository.updateApplicationStatus(appId, status, note);
            if (updated) {
                statusMessageLiveData.postValue("Application status updated to: " + status);
                refreshAllData();
            } else {
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void updateMilestoneProgress(String appId, int progressPercent, String milestoneText) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            boolean updated = commerceRepository.updateApplicationMilestone(appId, progressPercent, milestoneText);
            if (updated) {
                statusMessageLiveData.postValue("Milestone updated: " + milestoneText + " (" + progressPercent + "%)");
                refreshAllData();
            } else {
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void updateCompanyProfile(CompanyProfile profile) {
        isLoadingLiveData.setValue(true);
        executorService.execute(() -> {
            commerceRepository.updateCompanyProfile(profile);
            companyProfileLiveData.postValue(profile);
            statusMessageLiveData.postValue("Company profile updated successfully!");
            isLoadingLiveData.postValue(false);
        });
    }

    // Getters for LiveData
    public LiveData<List<ContractFarmingDeal>> getAvailableContractsLiveData() { return availableContractsLiveData; }
    public LiveData<List<ContractApplication>> getFarmerApplicationsLiveData() { return farmerApplicationsLiveData; }
    public LiveData<List<ContractApplication>> getActiveContractsLiveData() { return activeContractsLiveData; }
    public LiveData<List<ContractApplication>> getCompletedContractsLiveData() { return completedContractsLiveData; }
    public LiveData<List<ContractFarmingDeal>> getCompanyContractsLiveData() { return companyContractsLiveData; }
    public LiveData<List<ContractApplication>> getCompanyApplicationsLiveData() { return companyApplicationsLiveData; }
    public LiveData<CompanyProfile> getCompanyProfileLiveData() { return companyProfileLiveData; }
    public LiveData<Boolean> getIsLoadingLiveData() { return isLoadingLiveData; }
    public LiveData<String> getStatusMessageLiveData() { return statusMessageLiveData; }
}
