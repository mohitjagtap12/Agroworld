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
public class BrokerService {

    @Autowired
    private BrokerRequirementRepository reqRepository;

    @Autowired
    private BrokerOfferRepository offerRepository;

    @Autowired
    private BrokerDealRepository dealRepository;

    @Autowired
    private DeliveryJobRepository deliveryJobRepository;

    public List<BrokerRequirementEntity> getOpenRequirements() {
        return reqRepository.findByStatus("OPEN");
    }

    public List<BrokerRequirementEntity> getBrokerRequirements(String brokerId) {
        return reqRepository.findByBrokerId(brokerId);
    }

    @Transactional
    public BrokerRequirementEntity createRequirement(String brokerId, BrokerRequirementEntity req) {
        if (req.getId() == null) req.setId("breq_" + UUID.randomUUID().toString().substring(0, 8));
        req.setBrokerId(brokerId);
        req.setStatus("OPEN");
        req.setCreatedAt(LocalDateTime.now());
        req.setUpdatedAt(LocalDateTime.now());
        return reqRepository.save(req);
    }

    @Transactional
    public BrokerOfferEntity submitOffer(String farmerId, String requirementId, BrokerOfferEntity offer) {
        if (offer.getId() == null) offer.setId("boff_" + UUID.randomUUID().toString().substring(0, 8));
        offer.setRequirementId(requirementId);
        offer.setFarmerId(farmerId);
        offer.setStatus("OFFERED");
        offer.setCreatedAt(LocalDateTime.now());
        offer.setUpdatedAt(LocalDateTime.now());
        return offerRepository.save(offer);
    }

    @Transactional
    public BrokerDealEntity finalizeDeal(String brokerId, String requirementId, String offerId, Double agreedPrice, Double quantity, LocalDate pickupDate, String pickupLocation) {
        BrokerRequirementEntity req = reqRepository.findById(requirementId)
                .orElseThrow(() -> new IllegalArgumentException("Requirement not found"));
        BrokerOfferEntity offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        // Server-side calculation: Total Deal Value = quantity * agreedPrice
        double totalValue = quantity * agreedPrice;

        String dealId = "deal_" + UUID.randomUUID().toString().substring(0, 8);
        BrokerDealEntity deal = new BrokerDealEntity();
        deal.setId(dealId);
        deal.setRequirementId(requirementId);
        deal.setOfferId(offerId);
        deal.setBrokerId(brokerId);
        deal.setFarmerId(offer.getFarmerId());
        deal.setCrop(req.getCrop());
        deal.setQuantity(quantity);
        deal.setUnit("Quintals");
        deal.setAgreedPrice(agreedPrice);
        deal.setPriceUnit("per Quintal");
        deal.setTotalValue(totalValue);
        deal.setPickupDate(pickupDate != null ? pickupDate : LocalDate.now().plusDays(2));
        deal.setPickupLocation(pickupLocation != null ? pickupLocation : "Farmer Yard / Mandi Gate");
        deal.setStatus("CONFIRMED");
        deal.setCreatedAt(LocalDateTime.now());
        deal = dealRepository.save(deal);

        // Update requirement & offer statuses
        req.setStatus("DEAL_CLOSED");
        reqRepository.save(req);
        offer.setStatus("ACCEPTED");
        offerRepository.save(offer);

        // Unified Delivery Partner integration for Broker Mandi Trading
        DeliveryJobEntity deliveryJob = new DeliveryJobEntity();
        deliveryJob.setId("del_" + UUID.randomUUID().toString().substring(0, 8));
        deliveryJob.setOrderId(dealId);
        deliveryJob.setOrderType("BROKER_DEAL");
        deliveryJob.setSourceUserId(offer.getFarmerId());
        deliveryJob.setDestinationUserId(brokerId);
        deliveryJob.setPickupLocation(deal.getPickupLocation());
        deliveryJob.setDestinationLocation("APMC Mandi Yard Warehouse");
        deliveryJob.setItemsSummary(quantity + " Quintals of " + req.getCrop());
        deliveryJob.setQuantity(quantity * 100.0); // 1 Quintal = 100 kg
        deliveryJob.setUnit("Kg");
        deliveryJob.setPickupDate(deal.getPickupDate());
        deliveryJob.setDeliveryDate(deal.getPickupDate());
        deliveryJob.setDeliveryFee(800.0);
        deliveryJob.setStatus("AVAILABLE");
        deliveryJobRepository.save(deliveryJob);

        return deal;
    }

    public List<BrokerDealEntity> getDealsForFarmer(String farmerId) {
        return dealRepository.findByFarmerId(farmerId);
    }
}
