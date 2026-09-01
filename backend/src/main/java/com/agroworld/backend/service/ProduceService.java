package com.agroworld.backend.service;

import com.agroworld.backend.entity.*;
import com.agroworld.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProduceService {

    @Autowired
    private ProduceListingRepository listingRepository;

    @Autowired
    private ProduceOrderRepository orderRepository;

    @Autowired
    private ProduceOrderItemRepository orderItemRepository;

    @Autowired
    private DeliveryJobRepository deliveryJobRepository;

    public List<ProduceListingEntity> getAllProduceListings() {
        return listingRepository.findByStatus("AVAILABLE");
    }

    public List<ProduceListingEntity> getFarmerProduceListings(String farmerId) {
        return listingRepository.findByFarmerId(farmerId);
    }

    @Transactional
    public ProduceListingEntity createListing(String farmerId, ProduceListingEntity listing) {
        if (listing.getId() == null) listing.setId("prod_list_" + UUID.randomUUID().toString().substring(0, 8));
        listing.setFarmerId(farmerId);
        listing.setStatus("AVAILABLE");
        listing.setCreatedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());
        return listingRepository.save(listing);
    }

    @Transactional
    public ProduceOrderEntity placeProduceOrder(String customerId, String farmerId, List<ProduceOrderItemEntity> items, String deliveryAddress) {
        double total = 0.0;
        double totalWeightKg = 0.0;

        for (ProduceOrderItemEntity item : items) {
            ProduceListingEntity produce = listingRepository.findById(item.getListingId())
                    .orElseThrow(() -> new IllegalArgumentException("Produce not found: " + item.getListingId()));

            if (produce.getQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient harvest stock for: " + produce.getName());
            }

            produce.setQuantity(produce.getQuantity() - item.getQuantity());
            if (produce.getQuantity() <= 0) {
                produce.setStatus("SOLD_OUT");
            }
            listingRepository.save(produce);

            item.setPrice(produce.getPrice());
            item.setSubtotal(produce.getPrice() * item.getQuantity());
            total += item.getSubtotal();
            totalWeightKg += item.getQuantity();
        }

        String orderId = "pr_ord_" + UUID.randomUUID().toString().substring(0, 8);
        ProduceOrderEntity order = new ProduceOrderEntity();
        order.setId(orderId);
        order.setCustomerId(customerId);
        order.setFarmerId(farmerId);
        order.setTotalAmount(total);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        for (ProduceOrderItemEntity item : items) {
            item.setId("pr_item_" + UUID.randomUUID().toString().substring(0, 8));
            item.setOrderId(orderId);
            orderItemRepository.save(item);
        }

        // Auto-create Delivery Job for fresh farm produce direct to customer
        DeliveryJobEntity deliveryJob = new DeliveryJobEntity();
        deliveryJob.setId("del_" + UUID.randomUUID().toString().substring(0, 8));
        deliveryJob.setOrderId(orderId);
        deliveryJob.setOrderType("FARM_PRODUCE");
        deliveryJob.setSourceUserId(farmerId);
        deliveryJob.setDestinationUserId(customerId);
        deliveryJob.setPickupLocation("Farmer Harvest Yard");
        deliveryJob.setDestinationLocation(deliveryAddress);
        deliveryJob.setItemsSummary(items.size() + " farm fresh produce baskets");
        deliveryJob.setQuantity(totalWeightKg);
        deliveryJob.setUnit("Kg");
        deliveryJob.setPickupDate(LocalDate.now());
        deliveryJob.setDeliveryDate(LocalDate.now());
        deliveryJob.setDeliveryFee(120.0);
        deliveryJob.setStatus("AVAILABLE");
        deliveryJobRepository.save(deliveryJob);

        return order;
    }

    public List<ProduceOrderEntity> getOrdersForFarmer(String farmerId) {
        return orderRepository.findByFarmerId(farmerId);
    }
}
