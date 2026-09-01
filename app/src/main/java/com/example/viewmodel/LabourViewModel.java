package com.example.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labour.LabourDataHub;
import com.example.model.LabourApplication;
import com.example.model.LabourRequirement;
import com.example.model.LabourWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel managing state and operations for Farmer Labour Hiring and Labour Portal.
 */
public class LabourViewModel extends ViewModel {

    private final LabourDataHub dataHub = LabourDataHub.getInstance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<LabourRequirement>> requirementsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<LabourWorker>> matchedWorkersLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<LabourApplication>> jobApplicationsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<LabourWorker> currentWorkerProfileLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccessLiveData = new MutableLiveData<>();

    public LabourViewModel() {
        refreshAll();
    }

    public LiveData<List<LabourRequirement>> getRequirementsLiveData() {
        return requirementsLiveData;
    }

    public LiveData<List<LabourWorker>> getMatchedWorkersLiveData() {
        return matchedWorkersLiveData;
    }

    public LiveData<List<LabourApplication>> getJobApplicationsLiveData() {
        return jobApplicationsLiveData;
    }

    public LiveData<LabourWorker> getCurrentWorkerProfileLiveData() {
        return currentWorkerProfileLiveData;
    }

    public LiveData<Boolean> getOperationSuccessLiveData() {
        return operationSuccessLiveData;
    }

    public void refreshAll() {
        executor.execute(() -> {
            List<LabourRequirement> reqs = dataHub.getAllRequirements();
            requirementsLiveData.postValue(reqs);

            LabourWorker worker = dataHub.getWorkerById("lab_1");
            if (worker != null) {
                currentWorkerProfileLiveData.postValue(worker);
                List<LabourApplication> apps = dataHub.getApplicationsForLabour("lab_1");
                jobApplicationsLiveData.postValue(apps);
            }
        });
    }

    public void searchMatchingWorkers(String workType, String skillLevel, double maxDistanceKm) {
        executor.execute(() -> {
            List<LabourWorker> matched = dataHub.findMatchingWorkers(workType, skillLevel, maxDistanceKm);
            matchedWorkersLiveData.postValue(matched);
        });
    }

    public void postRequirement(LabourRequirement req, List<LabourWorker> selectedWorkers) {
        executor.execute(() -> {
            dataHub.postRequirement(req);
            if (selectedWorkers != null && !selectedWorkers.isEmpty()) {
                for (LabourWorker worker : selectedWorkers) {
                    dataHub.sendJobRequest(req, worker);
                }
            }
            refreshAll();
            operationSuccessLiveData.postValue(true);
        });
    }

    public void sendRequests(LabourRequirement req, List<LabourWorker> selectedWorkers) {
        executor.execute(() -> {
            if (req != null && selectedWorkers != null) {
                for (LabourWorker worker : selectedWorkers) {
                    dataHub.sendJobRequest(req, worker);
                }
            }
            refreshAll();
            operationSuccessLiveData.postValue(true);
        });
    }

    public void confirmWorker(String reqId, String workerId) {
        executor.execute(() -> {
            boolean success = dataHub.confirmWorkerBooking(reqId, workerId);
            refreshAll();
            operationSuccessLiveData.postValue(success);
        });
    }

    public void respondToJobRequest(String applicationId, boolean accept) {
        executor.execute(() -> {
            boolean success = dataHub.respondToJobRequest(applicationId, accept);
            refreshAll();
            operationSuccessLiveData.postValue(success);
        });
    }

    public void updateWorkerAvailability(String workerId, boolean isAvailable, String availableDates) {
        executor.execute(() -> {
            LabourWorker worker = dataHub.getWorkerById(workerId);
            if (worker != null) {
                worker.setAvailable(isAvailable);
                if (availableDates != null && !availableDates.isEmpty()) {
                    worker.setAvailableDates(availableDates);
                }
                currentWorkerProfileLiveData.postValue(worker);
            }
            refreshAll();
        });
    }

    public void completeJob(String applicationId, double rating, String review) {
        executor.execute(() -> {
            boolean success = dataHub.completeJob(applicationId, rating, review);
            refreshAll();
            operationSuccessLiveData.postValue(success);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
