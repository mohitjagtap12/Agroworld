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

    val listings: SnapshotStateList<AgriWasteItem> = mutableStateListOf(
        AgriWasteItem(
            id = "AW-LST-101",
            farmerId = "f_ramesh",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            wasteType = "Wheat Straw",
            wasteName = "Golden Dry Wheat Straw (गव्हाचा भुसा)",
            category = "Straw",
            quantity = 500.0,
            initialQuantity = 500.0,
            unit = "kg",
            price = 4.0,
            priceUnit = "₹/kg",
            availableDate = "5 September 2026",
            village = "Baramati Rural",
            taluka = "Baramati",
            district = "Pune",
            distanceKm = 3.8,
            description = "Dry wheat straw suitable for agricultural reuse, cattle fodder, or mushroom beds. Threshed cleanly with moisture below 8%.",
            imageEmoji = "🌾",
            pickupPreference = "Both Supported",
            status = "Available",
            createdAt = "25 Aug 2026"
        ),
        AgriWasteItem(
            id = "AW-LST-102",
            farmerId = "f_suresh",
            farmerName = "Suresh Shinde",
            farmerPhone = "+91 94220 88712",
            wasteType = "Sugarcane Residue",
            wasteName = "Sugarcane Trash / पाचट (Co 86032)",
            category = "Sugarcane Residue",
            quantity = 15.0,
            initialQuantity = 15.0,
            unit = "ton",
            price = 1400.0,
            priceUnit = "₹/ton",
            availableDate = "Immediate / Ready",
            village = "Alephata",
            taluka = "Junnar",
            district = "Pune",
            distanceKm = 8.2,
            description = "Sun-dried sugarcane dry leaves gathered in windrows. Excellent calorific value for biomass boilers or farm mulching.",
            imageEmoji = "🎋",
            pickupPreference = "Buyer Pickup",
            status = "Available",
            createdAt = "24 Aug 2026"
        ),
        AgriWasteItem(
            id = "AW-LST-103",
            farmerId = "f_santosh",
            farmerName = "Santosh Gaikwad",
            farmerPhone = "+91 97654 32109",
            wasteType = "Rice Straw",
            wasteName = "Indrayani Long Golden Paddy Straw",
            category = "Straw",
            quantity = 4.0,
            initialQuantity = 6.0,
            unit = "ton",
            price = 2200.0,
            priceUnit = "₹/ton",
            availableDate = "02 Sept 2026",
            village = "Khed Shivapur",
            taluka = "Haveli",
            district = "Pune",
            distanceKm = 12.5,
            description = "Uncut clean golden rice straw bundles. Completely weed-free, ideal for dairy bedding and oyster mushroom growing.",
            imageEmoji = "🌾",
            pickupPreference = "Both Supported",
            status = "Available",
            createdAt = "23 Aug 2026"
        ),
        AgriWasteItem(
            id = "AW-LST-104",
            farmerId = "f_anand",
            farmerName = "Anand Deshmukh",
            farmerPhone = "+91 98901 23456",
            wasteType = "Maize Stalk",
            wasteName = "Chopped Green Corn Stalks (मका धांडा)",
            category = "Crop Stalks",
            quantity = 8.0,
            initialQuantity = 8.0,
            unit = "ton",
            price = 1600.0,
            priceUnit = "₹/ton",
            availableDate = "Ready for pickup",
            village = "Manchar",
            taluka = "Ambegaon",
            district = "Pune",
            distanceKm = 10.0,
            description = "Sweet corn and maize stalks with high sugar content, chopped and ready for cattle silage preparation.",
            imageEmoji = "🌽",
            pickupPreference = "Delivery Partner",
            status = "Available",
            createdAt = "22 Aug 2026"
        ),
        AgriWasteItem(
            id = "AW-LST-105",
            farmerId = "f_vijay",
            farmerName = "Vijay Bhosale",
            farmerPhone = "+91 94211 44556",
            wasteType = "Cotton Stalk",
            wasteName = "Dry Cotton Woody Stalks (कापूस पराटी)",
            category = "Crop Stalks",
            quantity = 12.0,
            initialQuantity = 12.0,
            unit = "ton",
            price = 1200.0,
            priceUnit = "₹/ton",
            availableDate = "10 Sept 2026",
            village = "Indapur",
            taluka = "Indapur",
            district = "Pune",
            distanceKm = 24.0,
            description = "Dry thick cotton stems with high thermal calorific value. Ideal for briquette plants and industrial furnaces.",
            imageEmoji = "🌱",
            pickupPreference = "Both Supported",
            status = "Available",
            createdAt = "21 Aug 2026"
        ),
        AgriWasteItem(
            id = "AW-LST-106",
            farmerId = "f_ramesh",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            wasteType = "Soybean Residue",
            wasteName = "Soybean Pod Shells & Stems (सोयाबीन कुटार)",
            category = "Other Crop Residue",
            quantity = 25.0,
            initialQuantity = 25.0,
            unit = "quintal",
            price = 650.0,
            priceUnit = "₹/quintal",
            availableDate = "Ready for pickup",
            village = "Narayangaon",
            taluka = "Junnar",
            district = "Pune",
            distanceKm = 4.2,
            description = "Threshed soybean pods and stems, very high protein fodder supplement for milch cattle.",
            imageEmoji = "🫘",
            pickupPreference = "Both Supported",
            status = "Available",
            createdAt = "20 Aug 2026"
        ),
        AgriWasteItem(
            id = "AW-LST-107",
            farmerId = "f_ganesh",
            farmerName = "Ganesh Jadhav",
            farmerPhone = "+91 98233 77112",
            wasteType = "Coconut Husk",
            wasteName = "Raw Coconut Husks & Fibers (नारळ शेंड्या)",
            category = "Husk / Shell",
            quantity = 3000.0,
            initialQuantity = 3000.0,
            unit = "bundle",
            price = 3.5,
            priceUnit = "₹/bundle",
            availableDate = "Ready for pickup",
            village = "Shirwal",
            taluka = "Khandala",
            district = "Satara / Pune",
            distanceKm = 18.5,
            description = "Clean fibrous coconut husks for coir extraction, coco-peat blocks, and plant nursery soil conditioning.",
            imageEmoji = "🥥",
            pickupPreference = "Buyer Pickup",
            status = "Available",
            createdAt = "19 Aug 2026"
        ),
        AgriWasteItem(
            id = "AW-LST-108",
            farmerId = "f_ramesh",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            wasteType = "Groundnut Shell",
            wasteName = "Dry Groundnut Shells (भुईमूग टरफले)",
            category = "Husk / Shell",
            quantity = 0.0,
            initialQuantity = 15.0,
            unit = "quintal",
            price = 450.0,
            priceUnit = "₹/quintal",
            availableDate = "Sold 15 Aug 2026",
            village = "Narayangaon",
            taluka = "Junnar",
            district = "Pune",
            distanceKm = 4.2,
            description = "Batch sold out to Bio-Pellet Plant.",
            imageEmoji = "🥜",
            pickupPreference = "Buyer Pickup",
            status = "Sold Out",
            createdAt = "10 Aug 2026"
        )
    )

    val orders: SnapshotStateList<AgriWasteOrder> = mutableStateListOf(
        AgriWasteOrder(
            id = "WO-8921",
            wasteId = "AW-LST-101",
            farmerId = "f_ramesh",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            buyerId = "b_dairy_shree",
            buyerName = "Shree Krishna Dairy",
            buyerPhone = "+91 98501 44332",
            buyerType = "Dairy Farm",
            wasteName = "Golden Dry Wheat Straw (गव्हाचा भुसा)",
            wasteType = "Wheat Straw",
            quantity = 200.0,
            unit = "kg",
            agreedPrice = 4.0,
            priceUnit = "₹/kg",
            totalAmount = 800.0,
            pickupMethod = "Buyer Pickup",
            deliveryAddress = "Self Pickup from Farmer Field, Baramati",
            village = "Baramati Rural",
            taluka = "Baramati",
            district = "Pune",
            status = "Accepted",
            orderDate = "25 Aug 2026",
            pickupDate = "5 September 2026",
            notes = "Will send our Tata Ace pickup on 5th September morning."
        ),
        AgriWasteOrder(
            id = "WO-8922",
            wasteId = "AW-LST-101",
            farmerId = "f_ramesh",
            farmerName = "Ramesh Patil",
            farmerPhone = "+91 98220 14589",
            buyerId = "b_greenbio",
            buyerName = "Sahyadri Bio-Pellets Ltd.",
            buyerPhone = "+91 98221 66554",
            buyerType = "Biomass Plant",
            wasteName = "Golden Dry Wheat Straw (गव्हाचा भुसा)",
            wasteType = "Wheat Straw",
            quantity = 200.0,
            unit = "kg",
            agreedPrice = 4.0,
            priceUnit = "₹/kg",
            totalAmount = 800.0,
            pickupMethod = "Delivery Partner",
            deliveryAddress = "Chakan MIDC Gate 2, Pune",
            village = "Chakan",
            taluka = "Khed",
            district = "Pune",
            deliveryPartnerId = "del_01",
            deliveryPartnerName = "Kisan Express Transport (Pawan)",
            deliveryPartnerPhone = "+91 98900 33445",
            status = "Waiting for Farmer",
            orderDate = "26 Aug 2026",
            pickupDate = "02 Sept 2026",
            notes = "Require prompt delivery by AgroWorld logistics."
        ),
        AgriWasteOrder(
            id = "WO-8854",
            wasteId = "AW-LST-102",
            farmerId = "f_suresh",
            farmerName = "Suresh Shinde",
            farmerPhone = "+91 94220 88712",
            buyerId = "b_greenenergy",
            buyerName = "Sahyadri Bio-Pellets Ltd.",
            buyerPhone = "+91 98221 66554",
            buyerType = "Biomass Plant",
            wasteName = "Sugarcane Trash / पाचट (Co 86032)",
            wasteType = "Sugarcane Residue",
            quantity = 5.0,
            unit = "ton",
            agreedPrice = 1400.0,
            priceUnit = "₹/ton",
            totalAmount = 7000.0,
            pickupMethod = "Delivery Partner",
            deliveryAddress = "Chakan MIDC Phase 2, Pune",
            village = "Alephata",
            taluka = "Junnar",
            district = "Pune",
            deliveryPartnerId = "del_01",
            deliveryPartnerName = "Kisan Express Transport (Pawan)",
            deliveryPartnerPhone = "+91 98900 33445",
            status = "Picked Up",
            orderDate = "20 Aug 2026",
            pickupDate = "24 Aug 2026",
            notes = "10-ton tipper truck dispatched."
        ),
        AgriWasteOrder(
            id = "WO-8712",
            wasteId = "AW-LST-103",
            farmerId = "f_santosh",
            farmerName = "Santosh Gaikwad",
            farmerPhone = "+91 97654 32109",
            buyerId = "b_mushroom",
            buyerName = "Pune Agro Oyster Mushroom Farm",
            buyerPhone = "+91 99223 88123",
            buyerType = "Mushroom Cultivator",
            wasteName = "Indrayani Long Golden Paddy Straw",
            wasteType = "Rice Straw",
            quantity = 2.0,
            unit = "ton",
            agreedPrice = 2200.0,
            priceUnit = "₹/ton",
            totalAmount = 4400.0,
            pickupMethod = "Delivery Partner",
            deliveryAddress = "Plot 9, Hadapsar Agro Estate",
            village = "Khed Shivapur",
            taluka = "Haveli",
            district = "Pune",
            deliveryPartnerId = "del_02",
            deliveryPartnerName = "AgroWorld Express Fleet",
            deliveryPartnerPhone = "+91 98224 55667",
            status = "Completed",
            orderDate = "15 Aug 2026",
            pickupDate = "18 Aug 2026",
            completedDate = "19 Aug 2026",
            notes = "Paddy straw bundles verified clean."
        )
    )

    val notifications: SnapshotStateList<AgriWasteNotificationItem> = mutableStateListOf(
        AgriWasteNotificationItem(
            id = "notif_w1",
            title = "New Waste Purchase Request ♻️",
            message = "Sahyadri Bio-Pellets Ltd. requested 200 kg Wheat Straw at ₹4/kg.",
            timestamp = "Just now",
            type = "Request",
            recipientRole = "farmer",
            isRead = false
        ),
        AgriWasteNotificationItem(
            id = "notif_w2",
            title = "Order Confirmed by Farmer! ✅",
            message = "Ramesh Patil accepted your order for 200 kg Wheat Straw. Pickup on 5 Sept.",
            timestamp = "2 hours ago",
            type = "Accepted",
            recipientRole = "buyer",
            isRead = false
        ),
        AgriWasteNotificationItem(
            id = "notif_w3",
            title = "Agri-Waste Pickup Assigned 🚛",
            message = "New Biomass Delivery #WO-8922 assigned for 200 kg Wheat Straw in Baramati.",
            timestamp = "10 mins ago",
            type = "Delivery",
            recipientRole = "delivery",
            isRead = false
        )
    )

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
