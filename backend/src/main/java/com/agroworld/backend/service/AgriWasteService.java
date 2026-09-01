package com.agroworld.backend.service;

import com.agroworld.backend.entity.AgriWasteListingEntity;
import com.agroworld.backend.entity.AgriWasteOrderEntity;
import com.agroworld.backend.entity.DeliveryJobEntity;
import com.agroworld.backend.repository.AgriWasteListingRepository;
import com.agroworld.backend.repository.AgriWasteOrderRepository;
import com.agroworld.backend.repository.DeliveryJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgriWasteService {

    @Autowired
    private AgriWasteListingRepository listingRepository;

    @Autowired
    private AgriWasteOrderRepository orderRepository;

    @Autowired
    private DeliveryJobRepository deliveryJobRepository;

    public List<AgriWasteListingEntity> getFarmerListings(String farmerId) {
        return listingRepository.findByFarmerId(farmerId);
    }

    public List<AgriWasteListingEntity> getMarketplaceListings() {
        return listingRepository.findByStatus("AVAILABLE");
    }

    @Transactional
    public AgriWasteListingEntity createListing(String farmerId, AgriWasteListingEntity listing) {
        if (listing.getId() == null) listing.setId("waste_" + UUID.randomUUID().toString().substring(0, 8));
        listing.setFarmerId(farmerId);
        listing.setStatus("AVAILABLE");
        listing.setCreatedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());
        return listingRepository.save(listing);
    }

    @Transactional
    public AgriWasteOrderEntity placeWasteOrder(String buyerId, String listingId, Double requestedQuantity, boolean deliveryRequired) {
        AgriWasteListingEntity listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found: " + listingId));

        if (listing.getQuantity() < requestedQuantity) {
            throw new IllegalStateException("Requested quantity exceeds available stock (" + listing.getQuantity() + " " + listing.getUnit() + ")");
        }

        // Deduct inventory
        listing.setQuantity(listing.getQuantity() - requestedQuantity);
        if (listing.getQuantity() <= 0) {
            listing.setStatus("SOLD_OUT");
        } else {
            listing.setStatus("PARTIALLY_SOLD");
        }
        listingRepository.save(listing);

        // Create Order
        String orderId = "word_" + UUID.randomUUID().toString().substring(0, 8);
        AgriWasteOrderEntity order = new AgriWasteOrderEntity();
        order.setId(orderId);
        order.setListingId(listingId);
        order.setFarmerId(listing.getFarmerId());
        order.setBuyerId(buyerId);
        order.setQuantity(requestedQuantity);
        order.setUnit(listing.getUnit());
        order.setPrice(listing.getPrice());
        order.setTotalAmount(requestedQuantity * listing.getPrice());
        order.setDeliveryRequired(deliveryRequired);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        // If delivery required, create unified Delivery Job
        if (deliveryRequired) {
            DeliveryJobEntity deliveryJob = new DeliveryJobEntity();
            deliveryJob.setId("del_" + UUID.randomUUID().toString().substring(0, 8));
            deliveryJob.setOrderId(orderId);
            deliveryJob.setOrderType("AGRI_WASTE");
            deliveryJob.setSourceUserId(listing.getFarmerId());
            deliveryJob.setDestinationUserId(buyerId);
            deliveryJob.setPickupLocation(listing.getVillage() + ", " + listing.getTaluka() + ", " + listing.getDistrict());
            deliveryJob.setDestinationLocation("Industrial Biofuel Plant / Buyer Location");
            deliveryJob.setItemsSummary(requestedQuantity + " " + listing.getUnit() + " of " + listing.getWasteName());
            deliveryJob.setQuantity(requestedQuantity * 1000.0); // Convert tons to kg for logistics
            deliveryJob.setUnit("Kg");
            deliveryJob.setPickupDate(LocalDate.now().plusDays(1));
            deliveryJob.setDeliveryDate(LocalDate.now().plusDays(2));
            deliveryJob.setDeliveryFee(450.0);
            deliveryJob.setStatus("AVAILABLE");
            deliveryJobRepository.save(deliveryJob);
        }

        return order;
    }

    public List<AgriWasteOrderEntity> getOrdersForFarmer(String farmerId) {
        return orderRepository.findByFarmerId(farmerId);
    }
}
