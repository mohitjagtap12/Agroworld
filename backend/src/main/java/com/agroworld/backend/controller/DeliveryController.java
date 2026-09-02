package com.agroworld.backend.controller;

import com.agroworld.backend.dto.ApiResponse;
import com.agroworld.backend.entity.DeliveryJobEntity;
import com.agroworld.backend.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DeliveryJobEntity>>> getAvailableJobs() {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.getAvailableJobs()));
    }

    @GetMapping("/my-jobs")
    public ResponseEntity<ApiResponse<List<DeliveryJobEntity>>> getMyJobs(Authentication auth) {
        String partnerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(deliveryService.getJobsByPartner(partnerId)));
    }

    @PostMapping("/jobs/{jobId}/accept")
    public ResponseEntity<ApiResponse<DeliveryJobEntity>> acceptJob(
            Authentication auth,
            @PathVariable String jobId) {
        String partnerId = (String) auth.getPrincipal();
        try {
            DeliveryJobEntity job = deliveryService.acceptDeliveryJob(partnerId, jobId);
            return ResponseEntity.ok(ApiResponse.success("Delivery route accepted", job));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/jobs/{jobId}/status")
    public ResponseEntity<ApiResponse<DeliveryJobEntity>> updateJobStatus(
            Authentication auth,
            @PathVariable String jobId,
            @RequestBody Map<String, String> statusPayload) {
        String userId = (String) auth.getPrincipal();
        String newStatus = statusPayload.get("status");
        String remarks = statusPayload.getOrDefault("remarks", "Status updated by partner");

        // Validate status transitions
        if (!List.of("ASSIGNED", "PICKED_UP", "IN_TRANSIT", "DELIVERED", "COMPLETED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid delivery status: " + newStatus));
        }

        try {
            DeliveryJobEntity updated = deliveryService.updateStatus(userId, jobId, newStatus, remarks);
            return ResponseEntity.ok(ApiResponse.success("Status updated to " + newStatus, updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
