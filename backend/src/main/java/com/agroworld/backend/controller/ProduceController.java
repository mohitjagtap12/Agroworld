package com.agroworld.backend.controller;

import com.agroworld.backend.dto.ApiResponse;
import com.agroworld.backend.entity.ProduceListingEntity;
import com.agroworld.backend.entity.ProduceOrderEntity;
import com.agroworld.backend.entity.ProduceOrderItemEntity;
import com.agroworld.backend.service.ProduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produce")
public class ProduceController {

    @Autowired
    private ProduceService produceService;

    @GetMapping("/marketplace")
    public ResponseEntity<ApiResponse<List<ProduceListingEntity>>> getMarketplaceProduce() {
        return ResponseEntity.ok(ApiResponse.success(produceService.getAllProduceListings()));
    }

    @GetMapping("/farmer")
    public ResponseEntity<ApiResponse<List<ProduceListingEntity>>> getFarmerProduce(Authentication auth) {
        String farmerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(produceService.getFarmerProduceListings(farmerId)));
    }

    @PostMapping("/farmer")
    public ResponseEntity<ApiResponse<ProduceListingEntity>> createProduceListing(
            Authentication auth,
            @RequestBody ProduceListingEntity listing) {
        String farmerId = (String) auth.getPrincipal();
        if (listing.getQuantity() == null || listing.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Harvest quantity must be greater than zero."));
        }
        if (listing.getPrice() == null || listing.getPrice() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Price per kg must be greater than zero."));
        }
        ProduceListingEntity created = produceService.createListing(farmerId, listing);
        return ResponseEntity.ok(ApiResponse.success("Direct farm produce listed", created));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<ProduceOrderEntity>> placeProduceOrder(
            Authentication auth,
            @RequestBody Map<String, Object> payload) {
        String customerId = (String) auth.getPrincipal();
        String farmerId = (String) payload.get("farmerId");
        String deliveryAddress = (String) payload.get("deliveryAddress");

        if (customerId.equals(farmerId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Farmers cannot purchase their own produce."));
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) payload.get("items");
        if (rawItems == null || rawItems.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Produce order must contain items."));
        }

        List<ProduceOrderItemEntity> items = rawItems.stream().map(itemMap -> {
            ProduceOrderItemEntity item = new ProduceOrderItemEntity();
            item.setListingId((String) itemMap.get("listingId"));
            item.setQuantity(((Number) itemMap.get("quantity")).doubleValue());
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero.");
            }
            return item;
        }).toList();

        try {
            ProduceOrderEntity order = produceService.placeProduceOrder(customerId, farmerId, items, deliveryAddress);
            return ResponseEntity.ok(ApiResponse.success("Produce order placed successfully", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
