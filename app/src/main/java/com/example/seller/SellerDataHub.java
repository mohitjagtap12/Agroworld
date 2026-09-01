package com.example.seller;

import com.example.model.CartItem;
import com.example.model.ProductOrder;
import com.example.model.SellerProduct;
import com.example.model.SellerProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe Central Data Hub for the Seller Module and Farmer Farming-Products Shopping Marketplace.
 */
public class SellerDataHub {

    private static volatile SellerDataHub instance;

    public static final String DEFAULT_SELLER_ID = "SELLER_01";
    public static final String DEFAULT_SELLER_NAME = "Kisan Agri Mart & Seed Center";
    public static final String DEFAULT_SELLER_PHONE = "+91 98220 54321";

    private final CopyOnWriteArrayList<SellerProduct> products = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ProductOrder> orders = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<CartItem> cartItems = new CopyOnWriteArrayList<>();
    private final SellerProfile sellerProfile = new SellerProfile();

    private SellerDataHub() {
        seedInitialCatalog();
        seedInitialOrders();
    }

    public static SellerDataHub getInstance() {
        if (instance == null) {
            synchronized (SellerDataHub.class) {
                if (instance == null) {
                    instance = new SellerDataHub();
                }
            }
        }
        return instance;
    }

    private void seedInitialCatalog() {
        // --- 🌱 SEEDS ---
        products.add(new SellerProduct(
                "prod_seed_1", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Wheat Certified Seeds (HD-2967)", "Seeds", "Mahyco Agro",
                "High yielding certified wheat seeds with excellent rust tolerance and robust tillering.",
                120.0, "kg", 500, 4.8, "🌱", "Narayangaon, Junnar", "Active",
                "Wheat", "Follow local sowing spacing (20-22 cm). Treat seed with fungicide before sowing."
        ));

        products.add(new SellerProduct(
                "prod_seed_2", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Hybrid Tomato Seeds F1 (Abhinav)", "Seeds", "Syngenta India",
                "Indeterminate hybrid tomato seeds known for high fruit firmness and extended shelf life.",
                450.0, "packet", 120, 4.9, "🍅", "Narayangaon, Junnar", "Active",
                "Tomato", "Recommended nursery germination 25-30 days before field transplanting."
        ));

        products.add(new SellerProduct(
                "prod_seed_3", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Gavran Fursungi Onion Seeds", "Seeds", "Bejo Sheetal",
                "Premium quality winter/Rabi onion seeds with uniform dark red bulbs and superior storage life.",
                850.0, "kg", 80, 4.7, "🧅", "Narayangaon, Junnar", "Active",
                "Onion", "Sow 8-10 kg seed per hectare in well-drained raised beds."
        ));

        products.add(new SellerProduct(
                "prod_seed_4", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Bt Hybrid Cotton Seeds (Bollgard II)", "Seeds", "Rasi Seeds",
                "Certified Bollgard II hybrid cotton seeds with high boll retention and sucking pest resilience.",
                820.0, "packet", 150, 4.6, "☁️", "Narayangaon, Junnar", "Active",
                "Cotton", "Maintain refuge crop as per recommended agro-climatic package of practices."
        ));

        products.add(new SellerProduct(
                "prod_seed_5", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Aromatic Indrayani Rice Seed (Certified)", "Seeds", "Mahabeej Govt Seed",
                "Foundation certified aromatic rice seed batch suitable for lowland and canal irrigated tracts.",
                95.0, "kg", 400, 4.7, "🌾", "Narayangaon, Junnar", "Active",
                "Rice", "Standard seedling nursery preparation with 21-day transplanting."
        ));

        // --- 🧪 FERTILIZERS ---
        products.add(new SellerProduct(
                "prod_fert_1", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Water Soluble NPK 19:19:19", "Fertilizers", "IFFCO Fertilizer",
                "100% water-soluble balanced fertigation grade fertilizer for vegetative and flowering push.",
                160.0, "kg", 350, 4.8, "🧪", "Narayangaon, Junnar", "Active",
                "All Crops, Tomato, Onion, Rice, Wheat", "Apply via drip fertigation or foliar spray (5g/Litre water)."
        ));

        products.add(new SellerProduct(
                "prod_fert_2", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Organic Neem Cake Fertilizer (De-Oiled)", "Fertilizers", "Godrej Agrovet",
                "Pure neem seed meal rich in NPK, protects root zone against nematodes and soil-borne termites.",
                380.0, "bag", 100, 4.9, "🌿", "Narayangaon, Junnar", "Active",
                "All Crops, Tomato, Onion, Sugarcane", "Mix into soil during basal land preparation (200-250 kg/acre)."
        ));

        products.add(new SellerProduct(
                "prod_fert_3", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "DAP 18:46:0 Fertilizer Grade", "Fertilizers", "Coromandel International",
                "High grade Diammonium Phosphate for foundational root development and early vigor.",
                1350.0, "bag", 60, 4.7, "🌾", "Narayangaon, Junnar", "Active",
                "Wheat, Rice, Sugarcane, Onion", "Apply as basal dose during field sowing or planting."
        ));

        products.add(new SellerProduct(
                "prod_fert_4", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Bio-Potash & Micronutrient Mix (Liquid)", "Fertilizers", "Multiplex Bio",
                "Liquid chelated micronutrient booster ensuring vibrant fruit shine, weight, and pest resistance.",
                290.0, "bottle", 90, 4.6, "💧", "Narayangaon, Junnar", "Active",
                "Tomato, Onion, Chilli, Fruits", "Foliar application at 2ml/Litre during fruit set and bulbing stages."
        ));

        // --- 🛡️ CROP PROTECTION ---
        products.add(new SellerProduct(
                "prod_prot_1", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Bio-Fungicide Trichoderma Viride (1% WP)", "Crop Protection", "T.Stanes Bio",
                "Organic biological fungal inoculant for damping off, root rot, and Fusarium wilt management.",
                220.0, "kg", 120, 4.8, "🛡️", "Narayangaon, Junnar", "Active",
                "Tomato, Chilli, Cotton, Onion", "Product labeling advisory: Seed treatment 10g/kg or soil drenching 2.5 kg/acre. Follow safety guidelines."
        ));

        products.add(new SellerProduct(
                "prod_prot_2", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Organic Cold-Pressed Neem Oil (10,000 PPM)", "Crop Protection", "Ozone Biotech",
                "High azadirachtin formulation acting as repellent, antifeedant, and ovicide against sucking pests.",
                340.0, "bottle", 75, 4.9, "🍃", "Narayangaon, Junnar", "Active",
                "All Crops, Vegetables, Cotton", "Dissolve 3-5 ml/L with mild emulsifier. Follow label precautions."
        ));

        products.add(new SellerProduct(
                "prod_prot_3", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Yellow & Blue Sticky Traps (Pack of 25)", "Crop Protection", "PestShield Agro",
                "Weatherproof non-toxic sticky sheets for monitoring whiteflies, aphids, thrips, and leaf miners.",
                199.0, "packet", 200, 4.7, "🪤", "Narayangaon, Junnar", "Active",
                "Tomato, Chilli, Onion, Vegetables", "Hang 10-15 traps per acre at canopy height."
        ));

        products.add(new SellerProduct(
                "prod_prot_4", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Copper Oxychloride 50% WP", "Crop Protection", "Rallis India",
                "Broad spectrum protective contact fungicide for blight, leaf spot, and downy mildew.",
                420.0, "kg", 65, 4.6, "🔬", "Narayangaon, Junnar", "Active",
                "Tomato, Grapes, Mango, Potato", "Agricultural chemical advisory: Use 2.5-3g/L. Wear protective gloves and mask as per label instructions."
        ));

        // --- 🚜 FARM EQUIPMENT ---
        products.add(new SellerProduct(
                "prod_equip_1", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "16L Battery Operated Knapsack Sprayer", "Farm Equipment", "Neptune Agro",
                "Dual-switch 12V 12Ah battery sprayer with telescopic stainless steel lance and 4 brass nozzles.",
                2450.0, "piece", 30, 4.8, "🚜", "Narayangaon, Junnar", "Active",
                "All Crops", "Recharge fully before first use (8-10 hours). Wash tank with clean water after spraying."
        ));

        products.add(new SellerProduct(
                "prod_equip_2", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "7HP Petrol Power Weeder & Rotary Tiller", "Farm Equipment", "VST Tillers",
                "High torque petrol cultivator for de-weeding, soil aeration, and inter-row vegetable tilling.",
                38500.0, "piece", 5, 4.9, "⚙️", "Narayangaon, Junnar", "Active",
                "Vegetables, Cotton, Sugarcane", "Operate with clean 4-stroke engine oil. Comprehensive warranty included."
        ));

        products.add(new SellerProduct(
                "prod_equip_3", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Manual Multi-Crop Seed Drill Sowing Machine", "Farm Equipment", "KisanKraft",
                "Adjustable seed drum and depth regulator for fast planting of wheat, soyabean, and gram.",
                1850.0, "piece", 18, 4.5, "🌱", "Narayangaon, Junnar", "Active",
                "Wheat, Soyabean, Gram, Groundnut", "Calibrate seed slot size according to seed diameter before sowing."
        ));

        products.add(new SellerProduct(
                "prod_equip_4", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "4-Stroke Heavy Duty Brush Cutter Machine", "Farm Equipment", "Honda Power Agro",
                "35cc engine cutter with 3-tooth blade and nylon trimmer for weed clearing and harvesting.",
                14200.0, "piece", 8, 4.7, "🌾", "Narayangaon, Junnar", "Active",
                "Fodder, Rice, Wheat, Grass", "Always inspect safety guard and wear eye protection while operating."
        ));

        // --- 🛠️ TOOLS ---
        products.add(new SellerProduct(
                "prod_tool_1", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Stainless Steel Ergonomic Harvest Sickle", "Tools", "Falcon Garden",
                "Serrated edge tempered steel blade with comfortable non-slip grip for fast harvesting.",
                180.0, "piece", 150, 4.8, "🛠️", "Narayangaon, Junnar", "Active",
                "Rice, Wheat, Fodder, Grass", "Keep edge dry and wipe clean after harvesting operations."
        ));

        products.add(new SellerProduct(
                "prod_tool_2", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Bypass Pruning Secateurs Shears (8.5 Inch)", "Tools", "Bellota Tools",
                "Heavy-duty forged high carbon steel blade for clean tree pruning and fruit harvesting.",
                480.0, "piece", 60, 4.7, "✂️", "Narayangaon, Junnar", "Active",
                "Horticulture, Tomato, Orchard, Grapes", "Sharpen blade periodically and oil pivot screw."
        ));

        products.add(new SellerProduct(
                "prod_tool_3", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Carbon Steel Heavy-Duty Digging Fork", "Tools", "Tata Agrico",
                "4-prong heat-treated carbon steel head for bed loosening, root crop harvest, and manure mixing.",
                650.0, "piece", 40, 4.6, "⛏️", "Narayangaon, Junnar", "Active",
                "Soil Preparation, Potato, Onion", "Sturdy ash-wood handle with steel reinforcement sleeve."
        ));

        products.add(new SellerProduct(
                "prod_tool_4", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Hand Weeder & Cultivator Dual Hoe", "Tools", "Falcon Agro",
                "Combination hoe and 3-prong cultivator for weeding narrow rows and vegetable beds.",
                190.0, "piece", 80, 4.6, "🪴", "Narayangaon, Junnar", "Active",
                "All Crops, Vegetables", "Lightweight ergonomic hand tool."
        ));

        // --- 💧 IRRIGATION EQUIPMENT ---
        products.add(new SellerProduct(
                "prod_irri_1", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "16mm Inline Drip Lateral Pipe (400m Reel)", "Irrigation Equipment", "Jain Irrigation",
                "Class 2 UV stabilized inline drip tube with 40cm emitter spacing (4 LPH discharge rate).",
                2800.0, "roll", 25, 4.9, "💧", "Narayangaon, Junnar", "Active",
                "Tomato, Onion, Vegetables, Orchard", "Flush lateral lines with clean water before end-plugging."
        ));

        products.add(new SellerProduct(
                "prod_irri_2", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Micro Sprinkler Kit (50 Nozzles + Stand Pipe)", "Irrigation Equipment", "Netafim Agri",
                "Complete uniform overhead micro sprinkler package for nurseries and high-density vegetable plots.",
                1650.0, "packet", 35, 4.8, "💦", "Narayangaon, Junnar", "Active",
                "Vegetables, Nursery, Onion", "Operates efficiently at 1.5 - 2.5 kg/cm² water pressure."
        ));

        products.add(new SellerProduct(
                "prod_irri_3", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "2 Inch Venturi Fertilizer Injector Kit", "Irrigation Equipment", "Automat Sprinklers",
                "Durable chemical-resistant venturi with suction tube and flow control valve for fertigation.",
                850.0, "piece", 45, 4.6, "🚰", "Narayangaon, Junnar", "Active",
                "Drip Irrigation Systems", "Install on bypass line with pressure differential across ports."
        ));

        products.add(new SellerProduct(
                "prod_irri_4", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Disc Filter 2 Inch 120 Mesh (Hydrocyclone Grade)", "Irrigation Equipment", "Finolex Plastro",
                "Heavy-duty polypropylene disc filter preventing emitter clogging from algae, silt, and sand.",
                1400.0, "piece", 20, 4.7, "⚙️", "Narayangaon, Junnar", "Active",
                "Irrigation Systems", "Clean disc rings periodically using soft water spray."
        ));

        // --- 📦 OTHER AGRICULTURAL SUPPLIES ---
        products.add(new SellerProduct(
                "prod_sup_1", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "Silver-Black Mulching Film 25 Micron (400m)", "Other Agricultural Supplies", "Supreme Agro",
                "UV stabilized embossed mulch film for weed suppression, moisture retention, and root warmth.",
                1950.0, "roll", 40, 4.8, "📦", "Narayangaon, Junnar", "Active",
                "Tomato, Chilli, Watermelon", "Lay silver side facing upwards towards sunlight."
        ));

        products.add(new SellerProduct(
                "prod_sup_2", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "50% Green Agro Shade Net (3x50m)", "Other Agricultural Supplies", "VJ Agro Netting",
                "Virgin HDPE monofilament shade net protecting seedlings against excessive heat and heavy hail.",
                2200.0, "roll", 22, 4.7, "🟩", "Narayangaon, Junnar", "Active",
                "Nursery, Horticulture, Vegetables", "Anchor with UV-resistant tie cords to support frame."
        ));

        products.add(new SellerProduct(
                "prod_sup_3", DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_SELLER_PHONE,
                "HDPE Waterproof Tarpaulin Sheet (250 GSM 18x24ft)", "Other Agricultural Supplies", "Tufropes Agri",
                "Heavy multi-layer laminated tarp with reinforced aluminum eyelets for crop drying and storage cover.",
                1750.0, "piece", 30, 4.8, "⛺", "Narayangaon, Junnar", "Active",
                "Grain Storage, Crop Protection, Field Cover", "Tear-resistant and 100% waterproof."
        ));
    }

    private void seedInitialOrders() {
        orders.add(new ProductOrder(
                "ord_p_101", "ORD-AGRO-7821", "FARMER_MH_01", "Rameshwar Patil", "+91 98220 12345",
                DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, "prod_fert_1", "Water Soluble NPK 19:19:19",
                "🧪", "Fertilizers", 2, "kg", 160.0, 320.0,
                "Gat No. 42, Near Canal, Narayangaon", "Narayangaon", "Junnar", "Pune",
                ProductOrder.STATUS_CONFIRMED, "28 Aug 2026", "AgroWorld Delivery Partner", "Cash on Delivery",
                "Deliver after 4 PM"
        ));

        orders.add(new ProductOrder(
                "ord_p_102", "ORD-AGRO-7822", "FARMER_MH_01", "Rameshwar Patil", "+91 98220 12345",
                DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, "prod_seed_2", "Hybrid Tomato Seeds F1 (Abhinav)",
                "🍅", "Seeds", 3, "packet", 450.0, 1350.0,
                "Gat No. 42, Near Canal, Narayangaon", "Narayangaon", "Junnar", "Pune",
                ProductOrder.STATUS_PACKED, "29 Aug 2026", "AgroWorld Delivery Partner", "UPI Kisan Pay",
                "Packed in tamper-proof container"
        ));

        orders.add(new ProductOrder(
                "ord_p_103", "ORD-AGRO-7823", "FARMER_MH_01", "Rameshwar Patil", "+91 98220 12345",
                DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, "prod_prot_3", "Yellow & Blue Sticky Traps (Pack of 25)",
                "🪤", "Crop Protection", 1, "packet", 199.0, 199.0,
                "Gat No. 42, Near Canal, Narayangaon", "Narayangaon", "Junnar", "Pune",
                ProductOrder.STATUS_DELIVERED, "24 Aug 2026", "AgroWorld Delivery Partner", "Cash on Delivery",
                "Order delivered successfully"
        ));

        orders.add(new ProductOrder(
                "ord_p_104", "ORD-AGRO-7824", "FARMER_MH_02", "Dnyaneshwar Gorde", "+91 98221 98765",
                DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, "prod_equip_1", "16L Battery Operated Knapsack Sprayer",
                "🚜", "Farm Equipment", 1, "piece", 2450.0, 2450.0,
                "Wadgaon Sahani, Junnar", "Wadgaon", "Junnar", "Pune",
                ProductOrder.STATUS_ORDER_PLACED, "31 Aug 2026", "AgroWorld Delivery Partner", "Cash on Delivery",
                "Call before dispatch"
        ));
    }

    // =========================================================================
    // PRODUCT CATALOG OPERATIONS (SELLER + FARMER)
    // =========================================================================

    public List<SellerProduct> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<SellerProduct> getProductsForSeller(String sellerId) {
        String targetSeller = (sellerId != null && !sellerId.isEmpty()) ? sellerId : DEFAULT_SELLER_ID;
        List<SellerProduct> list = new ArrayList<>();
        for (SellerProduct p : products) {
            if (targetSeller.equalsIgnoreCase(p.getSellerId())) {
                list.add(p);
            }
        }
        return list;
    }

    public SellerProduct getProductById(String id) {
        if (id == null) return null;
        for (SellerProduct p : products) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public synchronized void addProduct(SellerProduct product) {
        if (product != null) {
            if (product.getId() == null || product.getId().isEmpty()) {
                product.setId("prod_" + UUID.randomUUID().toString().substring(0, 8));
            }
            if (product.getSellerId() == null || product.getSellerId().isEmpty()) {
                product.setSellerId(DEFAULT_SELLER_ID);
                product.setSellerName(DEFAULT_SELLER_NAME);
                product.setSellerPhone(DEFAULT_SELLER_PHONE);
            }
            products.add(0, product);
        }
    }

    public synchronized boolean updateProduct(SellerProduct updatedProduct, String actingSellerId) {
        if (updatedProduct == null) return false;
        String seller = (actingSellerId != null && !actingSellerId.isEmpty()) ? actingSellerId : DEFAULT_SELLER_ID;

        for (int i = 0; i < products.size(); i++) {
            SellerProduct current = products.get(i);
            if (current.getId().equals(updatedProduct.getId())) {
                // Enforce Seller Product Ownership
                if (!current.getSellerId().equalsIgnoreCase(seller)) {
                    return false;
                }
                products.set(i, updatedProduct);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteProduct(String productId, String actingSellerId) {
        if (productId == null) return false;
        String seller = (actingSellerId != null && !actingSellerId.isEmpty()) ? actingSellerId : DEFAULT_SELLER_ID;

        SellerProduct target = getProductById(productId);
        if (target != null && target.getSellerId().equalsIgnoreCase(seller)) {
            return products.remove(target);
        }
        return false;
    }

    public synchronized boolean updateStockAndPrice(String productId, int newStock, double newPrice, String actingSellerId) {
        if (productId == null) return false;
        String seller = (actingSellerId != null && !actingSellerId.isEmpty()) ? actingSellerId : DEFAULT_SELLER_ID;

        SellerProduct p = getProductById(productId);
        if (p != null && p.getSellerId().equalsIgnoreCase(seller)) {
            p.setStock(newStock);
            if (newPrice > 0) {
                p.setPrice(newPrice);
            }
            return true;
        }
        return false;
    }

    // =========================================================================
    // CART OPERATIONS (FARMER)
    // =========================================================================

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public synchronized boolean addToCart(SellerProduct product, int quantity) {
        if (product == null || quantity <= 0) return false;

        // Check if product is out of stock
        SellerProduct currentCatalogItem = getProductById(product.getId());
        if (currentCatalogItem == null || currentCatalogItem.isOutOfStock()) {
            return false;
        }

        int maxStock = currentCatalogItem.getStock();

        for (CartItem item : cartItems) {
            if (item.getProductId().equals(product.getId())) {
                int newQty = item.getQuantity() + quantity;
                if (newQty > maxStock) {
                    newQty = maxStock;
                }
                item.setQuantity(newQty);
                return true;
            }
        }

        int actualQty = Math.min(quantity, maxStock);
        if (actualQty > 0) {
            cartItems.add(new CartItem(currentCatalogItem, actualQty));
            return true;
        }
        return false;
    }

    public synchronized void updateCartItemQuantity(String productId, int quantity) {
        if (productId == null) return;
        SellerProduct product = getProductById(productId);
        int maxStock = product != null ? product.getStock() : Integer.MAX_VALUE;

        if (quantity <= 0) {
            removeFromCart(productId);
            return;
        }

        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(Math.min(quantity, maxStock));
                return;
            }
        }
    }

    public synchronized void removeFromCart(String productId) {
        if (productId == null) return;
        cartItems.removeIf(item -> item.getProductId().equals(productId));
    }

    public synchronized void clearCart() {
        cartItems.clear();
    }

    public double getCartTotal() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    // =========================================================================
    // ORDER MANAGEMENT & STOCK DECREMENT (FARMER + SELLER)
    // =========================================================================

    public synchronized List<ProductOrder> placeCartOrder(String farmerId, String farmerName, String farmerPhone,
                                                          String address, String village, String taluka,
                                                          String district, String paymentMethod, String notes) {
        List<ProductOrder> createdOrders = new ArrayList<>();
        if (cartItems.isEmpty()) return createdOrders;

        String dateStr = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(new Date());

        for (CartItem cartItem : cartItems) {
            SellerProduct p = getProductById(cartItem.getProductId());
            if (p == null) continue;

            int qtyToBuy = cartItem.getQuantity();
            if (qtyToBuy > p.getStock()) {
                qtyToBuy = p.getStock();
            }
            if (qtyToBuy <= 0) continue;

            // Decrement Stock
            int remainingStock = p.getStock() - qtyToBuy;
            p.setStock(remainingStock);

            String orderId = "ord_p_" + UUID.randomUUID().toString().substring(0, 8);
            String orderNum = "ORD-AGRO-" + (1000 + (int)(Math.random() * 9000));

            ProductOrder order = new ProductOrder(
                    orderId, orderNum, farmerId, farmerName, farmerPhone,
                    p.getSellerId(), p.getSellerName(), p.getId(), p.getName(),
                    p.getImageEmoji(), p.getCategory(), qtyToBuy, p.getUnit(),
                    p.getPrice(), qtyToBuy * p.getPrice(), address,
                    village, taluka, district, ProductOrder.STATUS_ORDER_PLACED,
                    dateStr, "AgroWorld Delivery Partner", paymentMethod, notes
            );

            orders.add(0, order);
            createdOrders.add(order);
        }

        clearCart();
        return createdOrders;
    }

    public synchronized ProductOrder placeDirectOrder(SellerProduct product, int quantity,
                                                      String farmerId, String farmerName, String farmerPhone,
                                                      String address, String village, String taluka,
                                                      String district, String paymentMethod, String notes) {
        if (product == null || quantity <= 0) return null;
        SellerProduct catalogItem = getProductById(product.getId());
        if (catalogItem == null || catalogItem.isOutOfStock()) return null;

        int qtyToBuy = Math.min(quantity, catalogItem.getStock());
        if (qtyToBuy <= 0) return null;

        // Decrement Stock
        int remainingStock = catalogItem.getStock() - qtyToBuy;
        catalogItem.setStock(remainingStock);

        String dateStr = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(new Date());
        String orderId = "ord_p_" + UUID.randomUUID().toString().substring(0, 8);
        String orderNum = "ORD-AGRO-" + (1000 + (int)(Math.random() * 9000));

        ProductOrder order = new ProductOrder(
                orderId, orderNum, farmerId, farmerName, farmerPhone,
                catalogItem.getSellerId(), catalogItem.getSellerName(), catalogItem.getId(), catalogItem.getName(),
                catalogItem.getImageEmoji(), catalogItem.getCategory(), qtyToBuy, catalogItem.getUnit(),
                catalogItem.getPrice(), qtyToBuy * catalogItem.getPrice(), address,
                village, taluka, district, ProductOrder.STATUS_ORDER_PLACED,
                dateStr, "AgroWorld Delivery Partner", paymentMethod, notes
        );

        orders.add(0, order);
        return order;
    }

    public List<ProductOrder> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public List<ProductOrder> getOrdersForFarmer(String farmerId) {
        String farmer = (farmerId != null && !farmerId.isEmpty()) ? farmerId : "FARMER_MH_01";
        List<ProductOrder> list = new ArrayList<>();
        for (ProductOrder o : orders) {
            if (farmer.equalsIgnoreCase(o.getFarmerId())) {
                list.add(o);
            }
        }
        return list;
    }

    public List<ProductOrder> getOrdersForSeller(String sellerId) {
        String seller = (sellerId != null && !sellerId.isEmpty()) ? sellerId : DEFAULT_SELLER_ID;
        List<ProductOrder> list = new ArrayList<>();
        for (ProductOrder o : orders) {
            if (seller.equalsIgnoreCase(o.getSellerId())) {
                list.add(o);
            }
        }
        return list;
    }

    public ProductOrder getOrderById(String orderId) {
        if (orderId == null) return null;
        for (ProductOrder o : orders) {
            if (o.getId().equals(orderId)) {
                return o;
            }
        }
        return null;
    }

    public synchronized boolean updateOrderStatus(String orderId, String newStatus, String actingSellerId) {
        if (orderId == null || newStatus == null) return false;
        String seller = (actingSellerId != null && !actingSellerId.isEmpty()) ? actingSellerId : DEFAULT_SELLER_ID;

        ProductOrder order = getOrderById(orderId);
        if (order != null && order.getSellerId().equalsIgnoreCase(seller)) {
            // If rejecting order, restore product stock!
            if (ProductOrder.STATUS_REJECTED.equalsIgnoreCase(newStatus) &&
                    !ProductOrder.STATUS_REJECTED.equalsIgnoreCase(order.getStatus())) {
                SellerProduct p = getProductById(order.getProductId());
                if (p != null) {
                    p.setStock(p.getStock() + order.getQuantity());
                }
            }

            order.setStatus(newStatus);
            return true;
        }
        return false;
    }

    // =========================================================================
    // CROP RECOMMENDATIONS HELPER
    // =========================================================================

    public List<SellerProduct> getProductsForCrop(String cropName) {
        if (cropName == null || cropName.trim().isEmpty() || "All".equalsIgnoreCase(cropName)) {
            return getAllProducts();
        }
        String query = cropName.toLowerCase().trim();
        List<SellerProduct> matches = new ArrayList<>();
        for (SellerProduct p : products) {
            if (p.getSuitableCrops() != null &&
                    (p.getSuitableCrops().toLowerCase().contains(query) || p.getSuitableCrops().toLowerCase().contains("all crops"))) {
                matches.add(p);
            } else if (p.getName().toLowerCase().contains(query) || p.getDescription().toLowerCase().contains(query)) {
                matches.add(p);
            }
        }
        return matches;
    }

    public SellerProfile getSellerProfile() {
        return sellerProfile;
    }
}
