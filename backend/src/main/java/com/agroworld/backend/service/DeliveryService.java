package com.agroworld.backend.service;

import com.agroworld.backend.entity.DeliveryJobEntity;
import com.agroworld.backend.entity.DeliveryStatusHistoryEntity;
import com.agroworld.backend.repository.DeliveryJobRepository;
import com.agroworld.backend.repository.DeliveryStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryJobRepository deliveryJobRepository;

    @Autowired
    private DeliveryStatusHistoryRepository historyRepository;

    public List<DeliveryJobEntity> getAvailableJobs() {
        return deliveryJobRepository.findByStatus("AVAILABLE");
    }

    public List<DeliveryJobEntity> getJobsByPartner(String partnerId) {
        return deliveryJobRepository.findByAssignedPartnerId(partnerId);
    }

    public List<DeliveryJobEntity> getJobsForUser(String userId) {
        return deliveryJobRepository.findBySourceUserIdOrDestinationUserId(userId, userId);
    }

    @Transactional
    public DeliveryJobEntity acceptDeliveryJob(String partnerId, String jobId) {
        DeliveryJobEntity job = deliveryJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery job not found: " + jobId));

        if (!"AVAILABLE".equals(job.getStatus())) {
            throw new IllegalStateException("Job is already assigned or completed.");
        }

        job.setAssignedPartnerId(partnerId);
        job.setStatus("ASSIGNED");
        job.setUpdatedAt(LocalDateTime.now());
        job = deliveryJobRepository.save(job);

        // Record history
        recordStatusHistory(jobId, "ASSIGNED", partnerId, "Partner accepted the delivery route.");
        return job;
    }

    @Transactional
    public DeliveryJobEntity updateStatus(String userId, String jobId, String newStatus, String remarks) {
        DeliveryJobEntity job = deliveryJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery job not found: " + jobId));

        job.setStatus(newStatus);
        job.setUpdatedAt(LocalDateTime.now());
        if ("DELIVERED".equals(newStatus) || "COMPLETED".equals(newStatus)) {
            job.setCompletedAt(LocalDateTime.now());
        }
        job = deliveryJobRepository.save(job);

        recordStatusHistory(jobId, newStatus, userId, remarks);
        return job;
    }

    private void recordStatusHistory(String jobId, String status, String updatedBy, String remarks) {
        String historyId = "dhist_" + UUID.randomUUID().toString().substring(0, 8);
        DeliveryStatusHistoryEntity history = new DeliveryStatusHistoryEntity(
                historyId, jobId, status, updatedBy, remarks
        );
        historyRepository.save(history);
    }
}
