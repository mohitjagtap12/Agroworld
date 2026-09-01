package com.example.repository;

import com.example.agriwaste.AgriWasteDataHub;
import com.example.model.AgriWasteOrder;
import com.example.model.BrokerDeal;
import com.example.model.DeliveryJob;
import com.example.model.FarmerActivityItem;
import com.example.model.FarmerDirectOrder;
import com.example.model.ProductOrder;
import com.example.seller.SellerDataHub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe Central Repository for Common Delivery Partner Operations across:
 * - 🏪 Seller Product Orders
 * - 🌾 Farmer Produce Orders
 * - ♻️ Agri Waste Orders
 * - 📈 Broker Wholesale Deals
 *
 * Enforces strict status transition validation and real-time order synchronization.
 */
public class DeliveryRepository {

    private static volatile DeliveryRepository instance;

    private final CopyOnWriteArrayList<DeliveryJob> deliveryJobs = new CopyOnWriteArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(100);

    // Current Logged-in Delivery Partner Profile
    private String partnerId = "DEL_PARTNER_01";
    private String partnerName = "Sanjay More";
    private String partnerPhone = "+91 94220 12345";
    private String vehicleType = "Pickup Truck (Mahindra Bolero Maxi)";
    private String vehicleNumber = "MH-14-GH-4589";
    private double vehicleCapacityKg = 1500.0;
    private String serviceArea = "Pune, Junnar, Ambegaon, Haveli & Baramati Clusters";
    private boolean isAvailable = true;
    private double rating = 4.9;

    private DeliveryRepository() {
        seedInitialDeliveryJobs();
    }

    public static DeliveryRepository getInstance() {
        if (instance == null) {
            synchronized (DeliveryRepository.class) {
                if (instance == null) {
                    instance = new DeliveryRepository();
                }
            }
        }
        return instance;
    }

    private void seedInitialDeliveryJobs() {
        String today = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

        // 1. 🏪 NEW/AVAILABLE JOB: Seller Product Order
        deliveryJobs.add(new DeliveryJob(
                "DEL-2026-000101",
                "AW-ORD-8091",
                DeliveryJob.TYPE_SELLER_PRODUCT,
                "🧪",
                "Water Soluble NPK 19:19:19 (5 Bags - 125 kg)",
                125.0,
                "kg",
                "SELLER_01",
                "Kisan Agri Mart & Seed Center",
                "Shop 14, APMC Market Yard Complex, Pune",
                "+91 98220 54321",
                "FARMER_MH_01",
                "Rameshwar Patil (Farmer)",
                "Gat 112, Narayangaon, Junnar Taluka, Pune",
                "+91 98220 12345",
                "Today, 02:30 PM",
                "Today, 06:00 PM",
                280.0,
                42.0,
                DeliveryJob.STATUS_AVAILABLE,
                null,
                null,
                null,
                "Pickup Truck",
                "Fragile packaging. Customer requested evening delivery after 5 PM.",
                today
        ));

        // 2. 🌾 ASSIGNED JOB: Farmer Direct Produce Order
        DeliveryJob farmJob = new DeliveryJob(
                "DEL-2026-000102",
                "DIR-ORD-501",
                DeliveryJob.TYPE_FARM_PRODUCE,
                "🍅",
                "Fresh Hybrid Red Tomato (50 kg)",
                50.0,
                "kg",
                "farmer_1",
                "Vitthal Deshmukh (Farmer)",
                "Farm Gate Shed #2, Rui Village, Baramati, Pune",
                "+91 98221 67890",
                "cust_01",
                "Aniket Joshi (Customer)",
                "Flat 402, Kothrud Heights, Pune - 411038",
                "+91 98765 43210",
                "Today, 01:00 PM",
                "Today, 04:30 PM",
                320.0,
                68.0,
                DeliveryJob.STATUS_ASSIGNED,
                partnerId,
                partnerName,
                partnerPhone,
                "Pickup Truck",
                "Perishable fresh tomatoes. Ventilated crates loaded.",
                today
        );
        deliveryJobs.add(farmJob);

        // 3. 🚚 IN_TRANSIT JOB: Agri Waste Biomass Order
        DeliveryJob wasteJob = new DeliveryJob(
                "DEL-2026-000103",
                "WST-ORD-201",
                DeliveryJob.TYPE_AGRI_WASTE,
                "🌾",
                "Wheat Straw Biomass Bales (500 kg)",
                500.0,
                "kg",
                "farmer_1",
                "Rameshwar Patil (Farmer)",
                "Shrikrishna Farm, Narayangaon, Junnar, Pune",
                "+91 98220 12345",
                "buyer_01",
                "GreenBio Energy Plant",
                "Plot D-14, Baramati MIDC, Pune - 413133",
                "+91 2112 255678",
                "Today, 10:00 AM",
                "Today, 03:00 PM",
                550.0,
                85.0,
                DeliveryJob.STATUS_IN_TRANSIT,
                partnerId,
                partnerName,
                partnerPhone,
                "Mini Truck",
                "Tightly strapped biomass bales. Weighbridge slip attached.",
                today
        );
        deliveryJobs.add(wasteJob);

        // 4. 📈 PICKUP_SCHEDULED JOB: Broker Wholesale Deal
        DeliveryJob brokerJob = new DeliveryJob(
                "DEL-2026-000104",
                "BRK-DEAL-01",
                DeliveryJob.TYPE_BROKER_DEAL,
                "🧅",
                "Pune Red Onions Bulk (1.2 Tons)",
                1200.0,
                "kg",
                "FARMER_MH_01",
                "Rameshwar Patil (Farmer)",
                "Field Storage Godown, Narayangaon, Junnar",
                "+91 98220 12345",
                "brk_01",
                "Shri Ganesh APMC Commission Agents",
                "Gala No. 42, Gultekdi APMC Yard, Pune",
                "+91 20 2426 7788",
                "Tomorrow, 08:00 AM",
                "Tomorrow, 12:00 PM",
                1250.0,
                78.0,
                DeliveryJob.STATUS_PICKUP_SCHEDULED,
                partnerId,
                partnerName,
                partnerPhone,
                "Heavy Truck",
                "Commercial APMC wholesale delivery. Check sorting quality at farm gate.",
                today
        );
        deliveryJobs.add(brokerJob);

        // 5. ✅ COMPLETED JOB 1: Seller Seeds
        DeliveryJob comp1 = new DeliveryJob(
                "DEL-2026-000098",
                "AW-ORD-8094",
                DeliveryJob.TYPE_SELLER_PRODUCT,
                "🌱",
                "Wheat Certified Seeds (HD-2967) - 40 kg",
                40.0,
                "kg",
                "SELLER_01",
                "Kisan Agri Mart",
                "APMC Market Yard, Pune",
                "+91 98220 54321",
                "FARMER_MH_02",
                "Vilasrao Gade (Farmer)",
                "Chakan Road, Rajgurunagar (Khed), Pune",
                "+91 90110 32415",
                "Yesterday, 11:00 AM",
                "Yesterday, 02:45 PM",
                210.0,
                38.0,
                DeliveryJob.STATUS_COMPLETED,
                partnerId,
                partnerName,
                partnerPhone,
                "Pickup Truck",
                "Delivered on time in good condition.",
                "Yesterday"
        );
        comp1.setCompletedAt("Yesterday, 02:45 PM");
        comp1.setRecipientName("Vilasrao Gade");
        comp1.setConfirmationMethod("OTP Verified");
        deliveryJobs.add(comp1);

        // 6. ✅ COMPLETED JOB 2: Agri Waste Vermicompost
        DeliveryJob comp2 = new DeliveryJob(
                "DEL-2026-000099",
                "WST-ORD-204",
                DeliveryJob.TYPE_AGRI_WASTE,
                "🪴",
                "Organic Vermicompost Bags (200 kg)",
                200.0,
                "kg",
                "farmer_2",
                "Baramati Organic Unit",
                "Rui Village, Baramati, Pune",
                "+91 91580 44556",
                "buyer_02",
                "Sahyadri Agro Nursery",
                "Manchar Bypass, Ambegaon, Pune",
                "+91 98223 99887",
                "2 Days Ago",
                "2 Days Ago",
                380.0,
                62.0,
                DeliveryJob.STATUS_COMPLETED,
                partnerId,
                partnerName,
                partnerPhone,
                "Pickup Truck",
                "Delivered to main gate supervisor.",
                "2 Days Ago"
        );
        comp2.setCompletedAt("2 Days Ago, 05:15 PM");
        comp2.setRecipientName("Sunil Shinde (Supervisor)");
        comp2.setConfirmationMethod("Recipient Handover");
        deliveryJobs.add(comp2);
    }

    // ==========================================
    // GETTERS & FILTERING
    // ==========================================

    public List<DeliveryJob> getAllJobs() {
        return new ArrayList<>(deliveryJobs);
    }

    public DeliveryJob getDeliveryJobById(String id) {
        if (id == null) return null;
        for (DeliveryJob job : deliveryJobs) {
            if (id.equalsIgnoreCase(job.getId()) || id.equalsIgnoreCase(job.getOrderId())) {
                return job;
            }
        }
        return null;
    }

    public List<DeliveryJob> getAvailableJobs() {
        List<DeliveryJob> result = new ArrayList<>();
        for (DeliveryJob job : deliveryJobs) {
            if (DeliveryJob.STATUS_AVAILABLE.equalsIgnoreCase(job.getStatus()) ||
                DeliveryJob.STATUS_CREATED.equalsIgnoreCase(job.getStatus())) {
                result.add(job);
            }
        }
        return result;
    }

    public List<DeliveryJob> getAssignedJobs() {
        List<DeliveryJob> result = new ArrayList<>();
        for (DeliveryJob job : deliveryJobs) {
            if (DeliveryJob.STATUS_ASSIGNED.equalsIgnoreCase(job.getStatus())) {
                result.add(job);
            }
        }
        return result;
    }

    public List<DeliveryJob> getPickupScheduledJobs() {
        List<DeliveryJob> result = new ArrayList<>();
        for (DeliveryJob job : deliveryJobs) {
            if (DeliveryJob.STATUS_PICKUP_SCHEDULED.equalsIgnoreCase(job.getStatus())) {
                result.add(job);
            }
        }
        return result;
    }

    public List<DeliveryJob> getActiveDeliveries() {
        List<DeliveryJob> result = new ArrayList<>();
        for (DeliveryJob job : deliveryJobs) {
            if (DeliveryJob.STATUS_ASSIGNED.equalsIgnoreCase(job.getStatus()) ||
                DeliveryJob.STATUS_PICKUP_SCHEDULED.equalsIgnoreCase(job.getStatus()) ||
                DeliveryJob.STATUS_PICKED_UP.equalsIgnoreCase(job.getStatus()) ||
                DeliveryJob.STATUS_IN_TRANSIT.equalsIgnoreCase(job.getStatus())) {
                result.add(job);
            }
        }
        return result;
    }

    public List<DeliveryJob> getCompletedDeliveries() {
        List<DeliveryJob> result = new ArrayList<>();
        for (DeliveryJob job : deliveryJobs) {
            if (DeliveryJob.STATUS_COMPLETED.equalsIgnoreCase(job.getStatus()) ||
                DeliveryJob.STATUS_DELIVERED.equalsIgnoreCase(job.getStatus())) {
                result.add(job);
            }
        }
        return result;
    }

    public int getTodayJobsCount() {
        return deliveryJobs.size();
    }

    public int getPendingRequestsCount() {
        return getAvailableJobs().size();
    }

    public int getActiveDeliveriesCount() {
        return getActiveDeliveries().size();
    }

    public int getCompletedDeliveriesCount() {
        return getCompletedDeliveries().size();
    }

    public double getTotalEarnings() {
        double total = 0;
        for (DeliveryJob job : deliveryJobs) {
            if (DeliveryJob.STATUS_COMPLETED.equalsIgnoreCase(job.getStatus()) ||
                DeliveryJob.STATUS_DELIVERED.equalsIgnoreCase(job.getStatus())) {
                total += job.getDeliveryFee();
            }
        }
        return total;
    }

    // ==========================================
    // DELIVERY JOB CREATION (From Confirmed Orders)
    // ==========================================

    public DeliveryJob createDeliveryJob(String orderId, String orderType, String itemEmoji,
                                         String itemsSummary, double quantity, String unit,
                                         String sourceUserId, String sourceName, String sourceLocation, String sourcePhone,
                                         String destinationUserId, String destinationName, String destinationLocation, String destinationPhone,
                                         String pickupDate, String deliveryDate, double deliveryFee, double distanceKm,
                                         String vehicleRequired, String deliveryNotes) {
        String uniqueId = "DEL-2026-000" + idCounter.incrementAndGet();
        String today = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

        DeliveryJob newJob = new DeliveryJob(
                uniqueId, orderId, orderType, itemEmoji, itemsSummary, quantity, unit,
                sourceUserId, sourceName, sourceLocation, sourcePhone,
                destinationUserId, destinationName, destinationLocation, destinationPhone,
                pickupDate, deliveryDate, deliveryFee, distanceKm,
                DeliveryJob.STATUS_AVAILABLE, null, null, null,
                vehicleRequired, deliveryNotes, today
        );

        deliveryJobs.add(0, newJob);
        return newJob;
    }

    // ==========================================
    // LIFECYCLE & STATUS TRANSITION VALIDATION
    // ==========================================

    /**
     * Accept a delivery job by a delivery partner.
     * Valid transition: AVAILABLE / CREATED -> ASSIGNED
     */
    public synchronized boolean acceptDeliveryJob(String jobId) {
        if (!isAvailable) {
            return false; // Partner is marked unavailable
        }

        DeliveryJob job = getDeliveryJobById(jobId);
        if (job == null) return false;

        String currentStatus = job.getStatus();
        if (!DeliveryJob.STATUS_AVAILABLE.equalsIgnoreCase(currentStatus) &&
            !DeliveryJob.STATUS_CREATED.equalsIgnoreCase(currentStatus)) {
            return false; // Invalid transition
        }

        job.setStatus(DeliveryJob.STATUS_ASSIGNED);
        job.setAssignedPartnerId(partnerId);
        job.setAssignedPartnerName(partnerName);
        job.setAssignedPartnerPhone(partnerPhone);
        job.setUpdatedAt(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date()));

        // Sync with underlying orders
        syncOrderStatus(job);
        return true;
    }

    /**
     * Reject a delivery job.
     */
    public synchronized boolean rejectDeliveryJob(String jobId) {
        DeliveryJob job = getDeliveryJobById(jobId);
        if (job == null) return false;

        if (DeliveryJob.STATUS_ASSIGNED.equalsIgnoreCase(job.getStatus())) {
            // Revert back to AVAILABLE so other partners can accept
            job.setStatus(DeliveryJob.STATUS_AVAILABLE);
            job.setAssignedPartnerId(null);
            job.setAssignedPartnerName(null);
            job.setAssignedPartnerPhone(null);
            job.setUpdatedAt(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date()));
            return true;
        } else if (DeliveryJob.STATUS_AVAILABLE.equalsIgnoreCase(job.getStatus())) {
            job.setStatus(DeliveryJob.STATUS_REJECTED);
            job.setUpdatedAt(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date()));
            return true;
        }
        return false;
    }

    /**
     * Schedule a specific pickup time.
     * Valid transition: ASSIGNED -> PICKUP_SCHEDULED
     */
    public synchronized boolean schedulePickup(String jobId, String scheduledTime) {
        DeliveryJob job = getDeliveryJobById(jobId);
        if (job == null) return false;

        if (!DeliveryJob.STATUS_ASSIGNED.equalsIgnoreCase(job.getStatus())) {
            return false; // Invalid transition
        }

        job.setStatus(DeliveryJob.STATUS_PICKUP_SCHEDULED);
        if (scheduledTime != null && !scheduledTime.trim().isEmpty()) {
            job.setPickupDate(scheduledTime);
        }
        job.setUpdatedAt(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date()));
        syncOrderStatus(job);
        return true;
    }

    /**
     * Mark goods picked up from the source.
     * Valid transition: ASSIGNED or PICKUP_SCHEDULED -> PICKED_UP
     */
    public synchronized boolean markPickedUp(String jobId) {
        DeliveryJob job = getDeliveryJobById(jobId);
        if (job == null) return false;

        String currentStatus = job.getStatus();
        if (!DeliveryJob.STATUS_ASSIGNED.equalsIgnoreCase(currentStatus) &&
            !DeliveryJob.STATUS_PICKUP_SCHEDULED.equalsIgnoreCase(currentStatus)) {
            return false; // Invalid transition
        }

        job.setStatus(DeliveryJob.STATUS_PICKED_UP);
        job.setUpdatedAt(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date()));
        syncOrderStatus(job);
        return true;
    }

    /**
     * Mark goods in transit to destination.
     * Valid transition: PICKED_UP -> IN_TRANSIT
     */
    public synchronized boolean markInTransit(String jobId) {
        DeliveryJob job = getDeliveryJobById(jobId);
        if (job == null) return false;

        String currentStatus = job.getStatus();
        if (!DeliveryJob.STATUS_PICKED_UP.equalsIgnoreCase(currentStatus) &&
            !DeliveryJob.STATUS_PICKUP_SCHEDULED.equalsIgnoreCase(currentStatus) &&
            !DeliveryJob.STATUS_ASSIGNED.equalsIgnoreCase(currentStatus)) {
            return false; // Invalid transition
        }

        job.setStatus(DeliveryJob.STATUS_IN_TRANSIT);
        job.setUpdatedAt(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date()));
        syncOrderStatus(job);
        return true;
    }

    /**
     * Confirm delivery at destination.
     * Valid transition: IN_TRANSIT or PICKED_UP -> DELIVERED
     */
    public synchronized boolean markDelivered(String jobId, String recipientName, String confirmationMethod, String notes) {
        DeliveryJob job = getDeliveryJobById(jobId);
        if (job == null) return false;

        String currentStatus = job.getStatus();
        if (!DeliveryJob.STATUS_IN_TRANSIT.equalsIgnoreCase(currentStatus) &&
            !DeliveryJob.STATUS_PICKED_UP.equalsIgnoreCase(currentStatus)) {
            return false; // Invalid transition
        }

        job.setStatus(DeliveryJob.STATUS_DELIVERED);
        job.setRecipientName(recipientName != null && !recipientName.trim().isEmpty() ? recipientName : job.getDestinationName());
        job.setConfirmationMethod(confirmationMethod != null ? confirmationMethod : "Recipient Handover");
        job.setConfirmationNotes(notes);
        String completedTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
        job.setCompletedAt(completedTime);
        job.setUpdatedAt(completedTime);

        syncOrderStatus(job);
        return true;
    }

    /**
     * Complete and close delivery job.
     * Valid transition: DELIVERED -> COMPLETED
     */
    public synchronized boolean completeDelivery(String jobId) {
        DeliveryJob job = getDeliveryJobById(jobId);
        if (job == null) return false;

        if (!DeliveryJob.STATUS_DELIVERED.equalsIgnoreCase(job.getStatus()) &&
            !DeliveryJob.STATUS_IN_TRANSIT.equalsIgnoreCase(job.getStatus())) {
            return false;
        }

        job.setStatus(DeliveryJob.STATUS_COMPLETED);
        if (job.getCompletedAt() == null || job.getCompletedAt().isEmpty()) {
            job.setCompletedAt(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date()));
        }
        job.setUpdatedAt(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date()));
        syncOrderStatus(job);
        return true;
    }

    // ==========================================
    // CROSS-MODULE STATUS SYNCHRONIZATION
    // ==========================================

    private void syncOrderStatus(DeliveryJob job) {
        String deliveryStatus = job.getStatus();
        String orderId = job.getOrderId();
        String orderType = job.getOrderType();

        try {
            // 1. 🏪 SELLER PRODUCT ORDERS
            if (DeliveryJob.TYPE_SELLER_PRODUCT.equalsIgnoreCase(orderType)) {
                SellerDataHub sellerHub = SellerDataHub.getInstance();
                ProductOrder order = sellerHub.getOrderById(orderId);
                if (order != null) {
                    if (DeliveryJob.STATUS_PICKED_UP.equalsIgnoreCase(deliveryStatus) ||
                        DeliveryJob.STATUS_IN_TRANSIT.equalsIgnoreCase(deliveryStatus)) {
                        order.setStatus(ProductOrder.STATUS_OUT_FOR_DELIVERY);
                    } else if (DeliveryJob.STATUS_DELIVERED.equalsIgnoreCase(deliveryStatus) ||
                               DeliveryJob.STATUS_COMPLETED.equalsIgnoreCase(deliveryStatus)) {
                        order.setStatus(ProductOrder.STATUS_DELIVERED);
                    }
                }
            }

            // 2. 🌾 FARMER DIRECT PRODUCE ORDERS
            else if (DeliveryJob.TYPE_FARM_PRODUCE.equalsIgnoreCase(orderType)) {
                FarmerRepository farmerRepo = FarmerRepository.getInstance();
                FarmerDirectOrder directOrder = farmerRepo.getDirectOrderById(orderId);
                if (directOrder != null) {
                    if (DeliveryJob.STATUS_PICKED_UP.equalsIgnoreCase(deliveryStatus) ||
                        DeliveryJob.STATUS_IN_TRANSIT.equalsIgnoreCase(deliveryStatus)) {
                        directOrder.setStatus("Out for Delivery");
                    } else if (DeliveryJob.STATUS_DELIVERED.equalsIgnoreCase(deliveryStatus) ||
                               DeliveryJob.STATUS_COMPLETED.equalsIgnoreCase(deliveryStatus)) {
                        directOrder.setStatus("Delivered");
                    }
                }
            }

            // 3. ♻️ AGRI WASTE BIOMASS ORDERS
            else if (DeliveryJob.TYPE_AGRI_WASTE.equalsIgnoreCase(orderType)) {
                AgriWasteDataHub wasteHub = AgriWasteDataHub.getInstance();
                AgriWasteOrder wasteOrder = wasteHub.getOrderById(orderId);
                if (wasteOrder != null) {
                    if (DeliveryJob.STATUS_PICKED_UP.equalsIgnoreCase(deliveryStatus)) {
                        wasteOrder.setStatus("Picked Up");
                    } else if (DeliveryJob.STATUS_IN_TRANSIT.equalsIgnoreCase(deliveryStatus)) {
                        wasteOrder.setStatus("Out for Delivery");
                    } else if (DeliveryJob.STATUS_DELIVERED.equalsIgnoreCase(deliveryStatus) ||
                               DeliveryJob.STATUS_COMPLETED.equalsIgnoreCase(deliveryStatus)) {
                        wasteOrder.setStatus("Delivered");
                    }
                }
            }

            // 4. 📈 BROKER WHOLESALE DEALS
            else if (DeliveryJob.TYPE_BROKER_DEAL.equalsIgnoreCase(orderType)) {
                CommerceRepository commerceRepo = CommerceRepository.getInstance();
                BrokerDeal deal = commerceRepo.getDealById(orderId);
                if (deal != null) {
                    if (DeliveryJob.STATUS_PICKED_UP.equalsIgnoreCase(deliveryStatus) ||
                        DeliveryJob.STATUS_IN_TRANSIT.equalsIgnoreCase(deliveryStatus)) {
                        deal.setDealStatus("Crop Handed Over");
                    } else if (DeliveryJob.STATUS_DELIVERED.equalsIgnoreCase(deliveryStatus) ||
                               DeliveryJob.STATUS_COMPLETED.equalsIgnoreCase(deliveryStatus)) {
                        deal.setDealStatus("Completed");
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle any synchronization exception to prevent crashes
        }
    }

    // ==========================================
    // PARTNER PROFILE & AVAILABILITY
    // ==========================================

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerPhone() {
        return partnerPhone;
    }

    public void setPartnerPhone(String partnerPhone) {
        this.partnerPhone = partnerPhone;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public double getVehicleCapacityKg() {
        return vehicleCapacityKg;
    }

    public void setVehicleCapacityKg(double vehicleCapacityKg) {
        this.vehicleCapacityKg = vehicleCapacityKg;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
