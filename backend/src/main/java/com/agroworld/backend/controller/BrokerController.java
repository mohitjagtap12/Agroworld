package com.agroworld.backend.controller;

import com.agroworld.backend.dto.ApiResponse;
import com.agroworld.backend.entity.BrokerDealEntity;
import com.agroworld.backend.entity.BrokerOfferEntity;
import com.agroworld.backend.entity.BrokerRequirementEntity;
import com.agroworld.backend.service.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/broker")
public class BrokerController {

    @Autowired
    private BrokerService brokerService;

    @GetMapping("/requirements")
    public ResponseEntity<ApiResponse<List<BrokerRequirementEntity>>> getOpenRequirements() {
        return ResponseEntity.ok(ApiResponse.success(brokerService.getOpenRequirements()));
    }

    @PostMapping("/requirements")
    public ResponseEntity<ApiResponse<BrokerRequirementEntity>> createRequirement(
            Authentication auth,
            @RequestBody BrokerRequirementEntity req) {
        String brokerId = (String) auth.getPrincipal();
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Quantity must be greater than zero."));
        }
        BrokerRequirementEntity created = brokerService.createRequirement(brokerId, req);
        return ResponseEntity.ok(ApiResponse.success("Broker crop requirement published", created));
    }

    @PostMapping("/requirements/{reqId}/offers")
    public ResponseEntity<ApiResponse<BrokerOfferEntity>> submitOffer(
            Authentication auth,
            @PathVariable String reqId,
            @RequestBody BrokerOfferEntity offer) {
        String farmerId = (String) auth.getPrincipal();
        if (offer.getOfferedQuantity() == null || offer.getOfferedQuantity() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Offered quantity must be positive."));
        }
        if (offer.getOfferedPrice() == null || offer.getOfferedPrice() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Offered price must be positive."));
        }
        BrokerOfferEntity created = brokerService.submitOffer(farmerId, reqId, offer);
        return ResponseEntity.ok(ApiResponse.success("Offer submitted to broker", created));
    }

    @PostMapping("/deals/finalize")
    public ResponseEntity<ApiResponse<BrokerDealEntity>> finalizeDeal(
            Authentication auth,
            @RequestBody Map<String, Object> payload) {
        String brokerId = (String) auth.getPrincipal();
        String reqId = (String) payload.get("requirementId");
        String offerId = (String) payload.get("offerId");
        Double agreedPrice = ((Number) payload.get("agreedPrice")).doubleValue();
        Double quantity = ((Number) payload.get("quantity")).doubleValue();
        String pickupLocation = (String) payload.get("pickupLocation");

        try {
            BrokerDealEntity deal = brokerService.finalizeDeal(
                    brokerId, reqId, offerId, agreedPrice, quantity, LocalDate.now().plusDays(2), pickupLocation
            );
            return ResponseEntity.ok(ApiResponse.success("Broker deal finalized and delivery dispatched", deal));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
