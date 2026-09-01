package com.example.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.model.DeliveryJob;
import com.example.repository.DeliveryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel managing Delivery Partner operations, dashboard stats, live job filters, and status transitions.
 */
public class DeliveryViewModel extends ViewModel {

    private final DeliveryRepository repository;

    private final MutableLiveData<List<DeliveryJob>> allJobsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<DeliveryJob>> filteredJobsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isAvailableLiveData = new MutableLiveData<>(true);
    private final MutableLiveData<DeliveryStats> statsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> actionMessageLiveData = new MutableLiveData<>();

    private String currentStatusFilter = "ALL"; // ALL, NEW, ASSIGNED, PICKUP, IN_TRANSIT, COMPLETED
    private String currentTypeFilter = "ALL";   // ALL, SELLER_PRODUCT, FARM_PRODUCE, AGRI_WASTE, BROKER_DEAL
    private String currentSearchQuery = "";

    public static class DeliveryStats {
        public final int todayJobs;
        public final int pendingRequests;
        public final int activeDeliveries;
        public final int completedDeliveries;
        public final double totalEarnings;

        public DeliveryStats(int todayJobs, int pendingRequests, int activeDeliveries, int completedDeliveries, double totalEarnings) {
            this.todayJobs = todayJobs;
            this.pendingRequests = pendingRequests;
            this.activeDeliveries = activeDeliveries;
            this.completedDeliveries = completedDeliveries;
            this.totalEarnings = totalEarnings;
        }
    }

    public DeliveryViewModel() {
        this.repository = DeliveryRepository.getInstance();
        refresh();
    }

    public LiveData<List<DeliveryJob>> getAllJobs() {
        return allJobsLiveData;
    }

    public LiveData<List<DeliveryJob>> getFilteredJobs() {
        return filteredJobsLiveData;
    }

    public LiveData<Boolean> getIsAvailable() {
        return isAvailableLiveData;
    }

    public LiveData<DeliveryStats> getStats() {
        return statsLiveData;
    }

    public LiveData<String> getActionMessage() {
        return actionMessageLiveData;
    }

    public void refresh() {
        List<DeliveryJob> jobs = repository.getAllJobs();
        allJobsLiveData.setValue(jobs);
        isAvailableLiveData.setValue(repository.isAvailable());

        DeliveryStats stats = new DeliveryStats(
                repository.getTodayJobsCount(),
                repository.getPendingRequestsCount(),
                repository.getActiveDeliveriesCount(),
                repository.getCompletedDeliveriesCount(),
                repository.getTotalEarnings()
        );
        statsLiveData.setValue(stats);

        applyFilters();
    }

    public void setStatusFilter(String filter) {
        this.currentStatusFilter = filter != null ? filter : "ALL";
        applyFilters();
    }

    public void setTypeFilter(String type) {
        this.currentTypeFilter = type != null ? type : "ALL";
        applyFilters();
    }

    public void setSearchQuery(String query) {
        this.currentSearchQuery = query != null ? query.trim().toLowerCase() : "";
        applyFilters();
    }

    private void applyFilters() {
        List<DeliveryJob> all = repository.getAllJobs();
        List<DeliveryJob> filtered = new ArrayList<>();

        for (DeliveryJob job : all) {
            // 1. Status Filter
            boolean matchesStatus = true;
            if ("NEW".equalsIgnoreCase(currentStatusFilter) || "AVAILABLE".equalsIgnoreCase(currentStatusFilter)) {
                matchesStatus = DeliveryJob.STATUS_AVAILABLE.equalsIgnoreCase(job.getStatus()) ||
                                DeliveryJob.STATUS_CREATED.equalsIgnoreCase(job.getStatus());
            } else if ("ASSIGNED".equalsIgnoreCase(currentStatusFilter)) {
                matchesStatus = DeliveryJob.STATUS_ASSIGNED.equalsIgnoreCase(job.getStatus());
            } else if ("PICKUP".equalsIgnoreCase(currentStatusFilter)) {
                matchesStatus = DeliveryJob.STATUS_PICKUP_SCHEDULED.equalsIgnoreCase(job.getStatus());
            } else if ("IN_TRANSIT".equalsIgnoreCase(currentStatusFilter)) {
                matchesStatus = DeliveryJob.STATUS_IN_TRANSIT.equalsIgnoreCase(job.getStatus()) ||
                                DeliveryJob.STATUS_PICKED_UP.equalsIgnoreCase(job.getStatus());
            } else if ("COMPLETED".equalsIgnoreCase(currentStatusFilter)) {
                matchesStatus = DeliveryJob.STATUS_COMPLETED.equalsIgnoreCase(job.getStatus()) ||
                                DeliveryJob.STATUS_DELIVERED.equalsIgnoreCase(job.getStatus());
            }

            // 2. Type Filter
            boolean matchesType = true;
            if (!"ALL".equalsIgnoreCase(currentTypeFilter)) {
                matchesType = currentTypeFilter.equalsIgnoreCase(job.getOrderType());
            }

            // 3. Search Query Filter
            boolean matchesSearch = true;
            if (!currentSearchQuery.isEmpty()) {
                String fullSearchable = (
                        (job.getId() != null ? job.getId() : "") + " " +
                        (job.getOrderId() != null ? job.getOrderId() : "") + " " +
                        (job.getItemsSummary() != null ? job.getItemsSummary() : "") + " " +
                        (job.getSourceName() != null ? job.getSourceName() : "") + " " +
                        (job.getSourceLocation() != null ? job.getSourceLocation() : "") + " " +
                        (job.getDestinationName() != null ? job.getDestinationName() : "") + " " +
                        (job.getDestinationLocation() != null ? job.getDestinationLocation() : "")
                ).toLowerCase();
                matchesSearch = fullSearchable.contains(currentSearchQuery);
            }

            if (matchesStatus && matchesType && matchesSearch) {
                filtered.add(job);
            }
        }

        filteredJobsLiveData.setValue(filtered);
    }

    public void toggleAvailability() {
        boolean current = repository.isAvailable();
        boolean newStatus = !current;
        repository.setAvailable(newStatus);
        isAvailableLiveData.setValue(newStatus);
        actionMessageLiveData.setValue(newStatus ? "Status updated: You are now ONLINE to receive deliveries" : "Status updated: You are now OFFLINE");
        refresh();
    }

    public void acceptJob(String jobId) {
        boolean success = repository.acceptDeliveryJob(jobId);
        if (success) {
            actionMessageLiveData.setValue("Delivery Job accepted! Proceed to pickup location.");
        } else {
            actionMessageLiveData.setValue("Cannot accept job: Partner is offline or job is no longer available.");
        }
        refresh();
    }

    public void rejectJob(String jobId) {
        boolean success = repository.rejectDeliveryJob(jobId);
        if (success) {
            actionMessageLiveData.setValue("Delivery Job declined.");
        } else {
            actionMessageLiveData.setValue("Cannot decline job.");
        }
        refresh();
    }

    public void schedulePickup(String jobId, String time) {
        boolean success = repository.schedulePickup(jobId, time);
        if (success) {
            actionMessageLiveData.setValue("Pickup scheduled for: " + time);
        } else {
            actionMessageLiveData.setValue("Failed to schedule pickup.");
        }
        refresh();
    }

    public void markPickedUp(String jobId) {
        boolean success = repository.markPickedUp(jobId);
        if (success) {
            actionMessageLiveData.setValue("Package marked as PICKED UP. Order status updated!");
        } else {
            actionMessageLiveData.setValue("Failed to update status to Picked Up.");
        }
        refresh();
    }

    public void markInTransit(String jobId) {
        boolean success = repository.markInTransit(jobId);
        if (success) {
            actionMessageLiveData.setValue("Delivery is now IN TRANSIT towards destination!");
        } else {
            actionMessageLiveData.setValue("Failed to update status to In Transit.");
        }
        refresh();
    }

    public void markDelivered(String jobId, String recipientName, String method, String notes) {
        boolean success = repository.markDelivered(jobId, recipientName, method, notes);
        if (success) {
            repository.completeDelivery(jobId);
            actionMessageLiveData.setValue("Delivery COMPLETED successfully! ₹" + getFeeForJob(jobId) + " credited.");
        } else {
            actionMessageLiveData.setValue("Failed to confirm delivery.");
        }
        refresh();
    }

    private double getFeeForJob(String jobId) {
        DeliveryJob job = repository.getDeliveryJobById(jobId);
        return job != null ? job.getDeliveryFee() : 0.0;
    }
}
