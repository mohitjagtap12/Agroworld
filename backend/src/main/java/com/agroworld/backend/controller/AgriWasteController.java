package com.agroworld.backend.controller;

import com.agroworld.backend.dto.ApiResponse;
import com.agroworld.backend.entity.AgriWasteListingEntity;
import com.agroworld.backend.entity.AgriWasteOrderEntity;
import com.agroworld.backend.service.AgriWasteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/waste")
public class AgriWasteController {

    @Autowired
    private AgriWasteService wasteService;

    @GetMapping("/marketplace")
    public ResponseEntity<ApiResponse<List<AgriWasteListingEntity>>> getMarketplaceListings() {
        return ResponseEntity.ok(ApiResponse.success(wasteService.getMarketplaceListings()));
    }

    @GetMapping("/farmer")
    public ResponseEntity<ApiResponse<List<AgriWasteListingEntity>>> getFarmerListings(Authentication auth) {
        String farmerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(wasteService.getFarmerListings(farmerId)));
    }

    @PostMapping("/farmer")
    public ResponseEntity<ApiResponse<AgriWasteListingEntity>> createListing(
            Authentication auth,
            @RequestBody AgriWasteListingEntity listing) {
        String farmerId = (String) auth.getPrincipal();
        if (listing.getQuantity() == null || listing.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Quantity must be greater than zero."));
        }
        if (listing.getPrice() == null || listing.getPrice() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Price must be greater than zero."));
        }
        AgriWasteListingEntity created = wasteService.createListing(farmerId, listing);
        return ResponseEntity.ok(ApiResponse.success("Agri-waste listing published", created));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<AgriWasteOrderEntity>> placeWasteOrder(
            Authentication auth,
            @RequestBody Map<String, Object> orderPayload) {
        String buyerId = (String) auth.getPrincipal();
        String listingId = (String) orderPayload.get("listingId");
        Double quantity = ((Number) orderPayload.get("quantity")).doubleValue();
        boolean deliveryRequired = Boolean.TRUE.equals(orderPayload.get("deliveryRequired"));

        if (quantity <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Requested quantity must be greater than zero."));
        }

        try {
            AgriWasteOrderEntity order = wasteService.placeWasteOrder(buyerId, listingId, quantity, deliveryRequired);
            return ResponseEntity.ok(ApiResponse.success("Agri-waste purchase order confirmed", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
