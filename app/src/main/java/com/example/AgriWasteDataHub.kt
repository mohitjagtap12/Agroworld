package com.example

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

// ------------------ DATA MODELS ------------------

data class WasteTypeMetadata(
    val name: String,
    val emoji: String,
    val category: String,
    val defaultUnit: String,
    val defaultPriceUnit: String,
    val typicalUses: List<String>,
    val industrialDemand: String
)

val AGRI_WASTE_TYPES = listOf(
    WasteTypeMetadata(
        name = "Wheat Straw",
        emoji = "🌾",
        category = "Straw",
        defaultUnit = "kg",
        defaultPriceUnit = "₹/kg",
        typicalUses = listOf("Animal fodder & cattle feed", "Mushroom cultivation substrate", "Organic vermicompost", "Biomass briquette manufacturing"),
        industrialDemand = "High demand from dairy clusters & biomass plants"
    ),
    WasteTypeMetadata(
        name = "Rice Straw",
        emoji = "🌾",
        category = "Straw",
        defaultUnit = "ton",
        defaultPriceUnit = "₹/ton",
        typicalUses = listOf("Cattle bedding & feed", "Paddy straw mushroom growing", "Bio-energy power plants", "Eco-packaging boards"),
        industrialDemand = "Crucial for stubble burning prevention & bio-energy"
    ),
    WasteTypeMetadata(
        name = "Sugarcane Residue",
        emoji = "🎋",
        category = "Sugarcane Residue",
        defaultUnit = "ton",
        defaultPriceUnit = "₹/ton",
        typicalUses = listOf("Soil mulching & moisture conservation", "Sugar mill cogeneration boilers", "Compost & vermiwash", "Eco-paper pulp"),
        industrialDemand = "Very high calorific value for industrial boilers"
    ),
    WasteTypeMetadata(
        name = "Maize Stalk",
        emoji = "🌽",
        category = "Crop Stalks",
        defaultUnit = "ton",
        defaultPriceUnit = "₹/ton",
        typicalUses = listOf("Nutritious silage & green fodder", "Industrial boiler fuel", "Field mulch cover", "Bio-char conversion"),
        industrialDemand = "Essential for dairy silage in summer"
    ),
    WasteTypeMetadata(
        name = "Cotton Stalk",
        emoji = "🌱",
        category = "Crop Stalks",
        defaultUnit = "ton",
        defaultPriceUnit = "₹/ton",
        typicalUses = listOf("High-density biomass briquettes", "Particle board manufacturing", "Industrial boiler fuel", "Activated carbon"),
        industrialDemand = "Preferred wood substitute for biomass fuel"
    ),
    WasteTypeMetadata(
        name = "Soybean Residue",
        emoji = "🫘",
        category = "Other Crop Residue",
        defaultUnit = "quintal",
        defaultPriceUnit = "₹/quintal",
        typicalUses = listOf("High protein milch cattle fodder", "Soil nitrogen fixation compost", "Animal bedding"),
        industrialDemand = "Rapidly purchased by local dairy farmers"
    ),
    WasteTypeMetadata(
        name = "Groundnut Shell",
        emoji = "🥜",
        category = "Husk / Shell",
        defaultUnit = "quintal",
        defaultPriceUnit = "₹/quintal",
        typicalUses = listOf("Biofuel pellets & briquettes", "Poultry farm bedding & litter", "Activated carbon filtration"),
        industrialDemand = "High heating value for heating plants"
    ),
    WasteTypeMetadata(
        name = "Coconut Husk",
        emoji = "🥥",
        category = "Husk / Shell",
        defaultUnit = "bundle",
        defaultPriceUnit = "₹/bundle",
        typicalUses = listOf("Coir fiber rope & geotextiles", "Coco-peat for plant nurseries & hydroponics", "Soil water-retention mulching"),
        industrialDemand = "Huge export & domestic nursery demand"
    ),
    WasteTypeMetadata(
        name = "Other",
        emoji = "♻️",
        category = "Other Crop Residue",
        defaultUnit = "kg",
        defaultPriceUnit = "₹/kg",
        typicalUses = listOf("General farm composting", "Bio-char generation", "Soil mulching", "Local biomass fuel"),
        industrialDemand = "Eco-friendly zero-waste farm management"
    )
)

data class AgriWasteItem(
    val id: String,
    val farmerId: String,
    val farmerName: String,
    val farmerPhone: String,
    val wasteType: String, // from AGRI_WASTE_TYPES names
    val wasteName: String,
    val category: String, // "Straw", "Crop Stalks", "Sugarcane Residue", "Husk / Shell", "Other Crop Residue"
    var quantity: Double,
    val initialQuantity: Double,
    val unit: String, // "kg", "quintal", "ton", "bundle", "bags"
    val price: Double,
    val priceUnit: String, // "₹/kg", "₹/quintal", "₹/ton", "₹/bundle", "₹/bag"
    val availableDate: String,
    val village: String,
    val taluka: String,
    val district: String,
    val distanceKm: Double = 4.5,
    val description: String,
    val imageEmoji: String = "🌾",
    val pickupPreference: String = "Both Supported", // "Buyer Pickup", "Delivery Partner", "Both Supported"
    var status: String = "Available", // "Available", "Sold Out", "Cancelled"
    val createdAt: String = "26 Aug 2026",
    val updatedAt: String = "26 Aug 2026"
)

data class AgriWasteOrder(
    val id: String,
    val wasteId: String,
    val farmerId: String,
    val farmerName: String,
    val farmerPhone: String,
    val buyerId: String,
    val buyerName: String,
    val buyerPhone: String,
    val buyerType: String = "Biomass Buyer", // "Dairy Farm", "Biomass Plant", "Compost Unit", "Nursery", "Trader"
    val wasteName: String,
    val wasteType: String,
    val quantity: Double,
    val unit: String,
    val agreedPrice: Double,
    val priceUnit: String,
    val totalAmount: Double,
    val pickupMethod: String, // "Buyer Pickup", "Delivery Partner"
    val deliveryAddress: String,
    val village: String,
    val taluka: String,
    val district: String,
    var deliveryPartnerId: String? = null,
    var deliveryPartnerName: String? = null,
    var deliveryPartnerPhone: String? = null,
    var status: String = "Waiting for Farmer", // "Waiting for Farmer", "Accepted", "Pickup Scheduled", "Picked Up", "Out for Delivery", "Delivered", "Completed", "Rejected"
    val orderDate: String,
    val pickupDate: String,
    var completedDate: String? = null,
    val notes: String = ""
)

data class AgriWasteNotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String, // "Request", "Accepted", "Delivery", "Completed"
    val recipientRole: String, // "farmer", "buyer", "delivery"
    var isRead: Boolean = false
)

// ------------------ SINGLETON REPOSITORY ------------------

object AgriWasteDataHub {

    val listings: SnapshotStateList<AgriWasteItem> = mutableStateListOf()

    val orders: SnapshotStateList<AgriWasteOrder> = mutableStateListOf()

    val notifications: SnapshotStateList<AgriWasteNotificationItem> = mutableStateListOf()

    // Helper functions

    fun addListing(item: AgriWasteItem) {
        listings.add(0, item)
        notifications.add(0, AgriWasteNotificationItem(
            id = "notif_" + System.currentTimeMillis(),
            title = "Waste Listing Published 🌾",
            message = "Your listing for ${item.wasteName} (${item.quantity} ${item.unit}) is now visible to buyers.",
            timestamp = "Just now",
            type = "Accepted",
            recipientRole = "farmer"
        ))
    }

    fun updateListing(updated: AgriWasteItem) {
        val idx = listings.indexOfFirst { it.id == updated.id }
        if (idx != -1) {
            listings[idx] = updated
        }
    }

    fun deleteListing(id: String) {
        listings.removeAll { it.id == id }
    }

    fun placeOrder(order: AgriWasteOrder): String {
        orders.add(0, order)
        // Notify farmer
        notifications.add(0, AgriWasteNotificationItem(
            id = "notif_" + System.currentTimeMillis(),
            title = "♻️ Waste Purchase Request",
            message = "${order.buyerName} requested ${order.quantity} ${order.unit} of ${order.wasteName} (${order.pickupMethod}).",
            timestamp = "Just now",
            type = "Request",
            recipientRole = "farmer"
        ))
        return order.id
    }

    fun acceptOrder(orderId: String) {
        val idx = orders.indexOfFirst { it.id == orderId }
        if (idx != -1) {
            val ord = orders[idx]
            val nextStatus = if (ord.pickupMethod == "Buyer Pickup") "Accepted" else "Pickup Scheduled"
            orders[idx] = ord.copy(status = nextStatus)

            // Deduct available quantity from listing
            val listIdx = listings.indexOfFirst { it.id == ord.wasteId }
            if (listIdx != -1) {
                val current = listings[listIdx]
                val remaining = (current.quantity - ord.quantity).coerceAtLeast(0.0)
                val newStatus = if (remaining <= 0.0) "Sold Out" else current.status
                listings[listIdx] = current.copy(quantity = remaining, status = newStatus)
            }

            // Notify buyer
            notifications.add(0, AgriWasteNotificationItem(
                id = "notif_" + System.currentTimeMillis(),
                title = "Order Confirmed by Farmer ✅",
                message = "Farmer ${ord.farmerName} confirmed your order for ${ord.quantity} ${ord.unit} ${ord.wasteName}.",
                timestamp = "Just now",
                type = "Accepted",
                recipientRole = "buyer"
            ))
        }
    }

    fun rejectOrder(orderId: String) {
        val idx = orders.indexOfFirst { it.id == orderId }
        if (idx != -1) {
            val ord = orders[idx]
            orders[idx] = ord.copy(status = "Rejected")

            notifications.add(0, AgriWasteNotificationItem(
                id = "notif_" + System.currentTimeMillis(),
                title = "Order Request Declined ❌",
                message = "Farmer ${ord.farmerName} could not fulfill the request for ${ord.wasteName}.",
                timestamp = "Just now",
                type = "Request",
                recipientRole = "buyer"
            ))
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        val idx = orders.indexOfFirst { it.id == orderId }
        if (idx != -1) {
            val ord = orders[idx]
            val completedDate = if (newStatus == "Completed" || newStatus == "Delivered") "Today" else ord.completedDate
            orders[idx] = ord.copy(status = newStatus, completedDate = completedDate)

            // If order completed and was buyer pickup, mark completed
            notifications.add(0, AgriWasteNotificationItem(
                id = "notif_" + System.currentTimeMillis(),
                title = "Order Status Updated: $newStatus",
                message = "Order #${ord.id} (${ord.wasteName}) is now marked as $newStatus.",
                timestamp = "Just now",
                type = "Delivery",
                recipientRole = "buyer"
            ))
        }
    }
}
