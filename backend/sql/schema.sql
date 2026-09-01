-- ============================================================================
-- AGROWORLD - DATABASE SCHEMA DEFINITION (MySQL 8.0+)
-- Phase 11: Production Normalized Relational Database
-- ============================================================================

CREATE DATABASE IF NOT EXISTS agroworld_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agroworld_db;

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- 1. USERS & ROLES
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(128) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('farmer', 'seller', 'labour', 'company', 'broker', 'customer', 'delivery') NOT NULL,
    profile_image VARCHAR(512),
    village VARCHAR(128),
    taluka VARCHAR(128),
    district VARCHAR(128),
    state VARCHAR(128) DEFAULT 'Maharashtra',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_phone (phone),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 2. ROLE PROFILES
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS farmer_profiles;
CREATE TABLE farmer_profiles (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    total_land_acres DECIMAL(8,2) DEFAULT 0.0,
    soil_type VARCHAR(64),
    irrigation_source VARCHAR(64),
    experience_years INT DEFAULT 0,
    kisan_credit_card_no VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS seller_profiles;
CREATE TABLE seller_profiles (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    shop_name VARCHAR(128) NOT NULL,
    gst_number VARCHAR(32),
    license_number VARCHAR(64),
    shop_address TEXT,
    rating DECIMAL(3,2) DEFAULT 4.5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS labour_profiles;
CREATE TABLE labour_profiles (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    skill_level ENUM('SKILLED', 'SEMI_SKILLED', 'UNSKILLED') DEFAULT 'SEMI_SKILLED',
    experience_years INT DEFAULT 0,
    primary_skills VARCHAR(255),
    daily_wage_rate DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    rating DECIMAL(3,2) DEFAULT 4.8,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS company_profiles;
CREATE TABLE company_profiles (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    company_name VARCHAR(128) NOT NULL,
    cin_number VARCHAR(32),
    industry_type VARCHAR(64),
    headquarters VARCHAR(128),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS broker_profiles;
CREATE TABLE broker_profiles (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    firm_name VARCHAR(128) NOT NULL,
    mandi_license_no VARCHAR(64),
    operating_mandi VARCHAR(128),
    rating DECIMAL(3,2) DEFAULT 4.7,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS customer_profiles;
CREATE TABLE customer_profiles (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    delivery_address TEXT,
    preferred_language VARCHAR(32) DEFAULT 'English',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS delivery_partner_profiles;
CREATE TABLE delivery_partner_profiles (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    vehicle_type ENUM('PICKUP_TRUCK', 'TEMPO_TATA_ACE', 'TRACTOR_TROLLEY', 'MINI_TRUCK', 'HEAVY_TRUCK') NOT NULL,
    vehicle_number VARCHAR(32) NOT NULL,
    driving_license_no VARCHAR(64) NOT NULL,
    max_capacity_kg DECIMAL(10,2) NOT NULL,
    service_radius_km INT DEFAULT 50,
    is_online BOOLEAN DEFAULT TRUE,
    rating DECIMAL(3,2) DEFAULT 4.9,
    total_deliveries INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 3. FARMER CROPS
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS farmer_crops;
CREATE TABLE farmer_crops (
    id VARCHAR(64) PRIMARY KEY,
    farmer_id VARCHAR(64) NOT NULL,
    crop_name VARCHAR(128) NOT NULL,
    variety VARCHAR(128),
    land_area DECIMAL(8,2) NOT NULL,
    land_unit VARCHAR(32) DEFAULT 'Acres',
    sowing_date DATE,
    expected_harvest_date DATE,
    location VARCHAR(255),
    status ENUM('SOWN', 'GROWING', 'VEGETATIVE', 'FLOWERING', 'READY_FOR_HARVEST', 'HARVESTED') DEFAULT 'GROWING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_crops_farmer (farmer_id),
    INDEX idx_crops_name (crop_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 4. LABOUR MODULE
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS labour_requirements;
CREATE TABLE labour_requirements (
    id VARCHAR(64) PRIMARY KEY,
    farmer_id VARCHAR(64) NOT NULL,
    crop_id VARCHAR(64),
    work_type VARCHAR(128) NOT NULL,
    description TEXT,
    workers_required INT NOT NULL DEFAULT 1,
    skill_level ENUM('SKILLED', 'SEMI_SKILLED', 'UNSKILLED') DEFAULT 'SEMI_SKILLED',
    experience_required INT DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE,
    start_time VARCHAR(32) DEFAULT '08:00 AM',
    working_hours DECIMAL(4,1) DEFAULT 8.0,
    wage_type ENUM('DAILY', 'PIECE_RATE', 'HOURLY') DEFAULT 'DAILY',
    wage_amount DECIMAL(10,2) NOT NULL,
    village VARCHAR(128),
    taluka VARCHAR(128),
    district VARCHAR(128),
    status ENUM('OPEN', 'FILLED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (crop_id) REFERENCES farmer_crops(id) ON DELETE SET NULL,
    INDEX idx_labour_farmer (farmer_id),
    INDEX idx_labour_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS labour_applications;
CREATE TABLE labour_applications (
    id VARCHAR(64) PRIMARY KEY,
    requirement_id VARCHAR(64) NOT NULL,
    worker_id VARCHAR(64) NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP NULL,
    FOREIGN KEY (requirement_id) REFERENCES labour_requirements(id) ON DELETE CASCADE,
    FOREIGN KEY (worker_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_req_worker (requirement_id, worker_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS labour_jobs;
CREATE TABLE labour_jobs (
    id VARCHAR(64) PRIMARY KEY,
    requirement_id VARCHAR(64) NOT NULL,
    farmer_id VARCHAR(64) NOT NULL,
    worker_id VARCHAR(64) NOT NULL,
    start_date DATE NOT NULL,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'DISPUTED') DEFAULT 'SCHEDULED',
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requirement_id) REFERENCES labour_requirements(id) ON DELETE CASCADE,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (worker_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 5. AGRI WASTE MARKETPLACE
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS agri_waste_listings;
CREATE TABLE agri_waste_listings (
    id VARCHAR(64) PRIMARY KEY,
    farmer_id VARCHAR(64) NOT NULL,
    waste_type VARCHAR(128) NOT NULL,
    waste_name VARCHAR(128) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(32) DEFAULT 'Tons',
    price DECIMAL(10,2) NOT NULL,
    price_unit VARCHAR(32) DEFAULT 'per Ton',
    available_date DATE,
    village VARCHAR(128),
    taluka VARCHAR(128),
    district VARCHAR(128),
    description TEXT,
    image VARCHAR(512),
    status ENUM('AVAILABLE', 'PARTIALLY_SOLD', 'SOLD_OUT', 'CANCELLED') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_waste_farmer (farmer_id),
    INDEX idx_waste_type (waste_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS agri_waste_orders;
CREATE TABLE agri_waste_orders (
    id VARCHAR(64) PRIMARY KEY,
    listing_id VARCHAR(64) NOT NULL,
    farmer_id VARCHAR(64) NOT NULL,
    buyer_id VARCHAR(64) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(32) DEFAULT 'Tons',
    price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    delivery_required BOOLEAN DEFAULT TRUE,
    status ENUM('PENDING', 'ACCEPTED', 'DISPATCHED', 'DELIVERED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (listing_id) REFERENCES agri_waste_listings(id) ON DELETE CASCADE,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_waste_orders_farmer (farmer_id),
    INDEX idx_waste_orders_buyer (buyer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 6. SELLER PRODUCTS & STORE
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS products;
CREATE TABLE products (
    id VARCHAR(64) PRIMARY KEY,
    seller_id VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    category ENUM('SEEDS', 'FERTILIZERS', 'PESTICIDES', 'EQUIPMENT', 'IRRIGATION', 'FEED') NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    unit VARCHAR(32) DEFAULT 'Kg',
    stock_quantity DECIMAL(10,2) NOT NULL DEFAULT 0.0,
    image VARCHAR(512),
    location VARCHAR(255),
    status ENUM('ACTIVE', 'OUT_OF_STOCK', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_products_seller (seller_id),
    INDEX idx_products_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS product_orders;
CREATE TABLE product_orders (
    id VARCHAR(64) PRIMARY KEY,
    farmer_id VARCHAR(64) NOT NULL,
    seller_id VARCHAR(64) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    delivery_address TEXT NOT NULL,
    status ENUM('PLACED', 'CONFIRMED', 'PROCESSING', 'READY_FOR_DELIVERY', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED') DEFAULT 'PLACED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_product_orders_farmer (farmer_id),
    INDEX idx_product_orders_seller (seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS product_order_items;
CREATE TABLE product_order_items (
    id VARCHAR(64) PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES product_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 7. CONTRACT FARMING
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS contracts;
CREATE TABLE contracts (
    id VARCHAR(64) PRIMARY KEY,
    company_id VARCHAR(64) NOT NULL,
    crop VARCHAR(128) NOT NULL,
    required_quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(32) DEFAULT 'Tons',
    offered_price DECIMAL(10,2) NOT NULL,
    price_unit VARCHAR(32) DEFAULT 'per Quintal',
    quality_requirements TEXT,
    harvest_period VARCHAR(128),
    delivery_location VARCHAR(255),
    application_deadline DATE,
    contract_duration VARCHAR(64),
    payment_terms VARCHAR(255),
    additional_conditions TEXT,
    status ENUM('ACTIVE', 'APPLICATIONS_CLOSED', 'ALLOCATED', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_contracts_company (company_id),
    INDEX idx_contracts_crop (crop)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS contract_applications;
CREATE TABLE contract_applications (
    id VARCHAR(64) PRIMARY KEY,
    contract_id VARCHAR(64) NOT NULL,
    farmer_id VARCHAR(64) NOT NULL,
    crop_id VARCHAR(64),
    land_area DECIMAL(8,2) NOT NULL,
    expected_quantity DECIMAL(10,2) NOT NULL,
    expected_harvest_date DATE,
    message TEXT,
    status ENUM('SUBMITTED', 'UNDER_REVIEW', 'ACCEPTED', 'REJECTED') DEFAULT 'SUBMITTED',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (crop_id) REFERENCES farmer_crops(id) ON DELETE SET NULL,
    UNIQUE KEY uk_contract_farmer (contract_id, farmer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 8. BROKER & MANDI TRADING
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS broker_requirements;
CREATE TABLE broker_requirements (
    id VARCHAR(64) PRIMARY KEY,
    broker_id VARCHAR(64) NOT NULL,
    crop VARCHAR(128) NOT NULL,
    required_quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(32) DEFAULT 'Quintals',
    offered_price DECIMAL(10,2) NOT NULL,
    price_unit VARCHAR(32) DEFAULT 'per Quintal',
    quality_requirement TEXT,
    required_date DATE,
    pickup_location VARCHAR(255),
    payment_terms VARCHAR(255),
    status ENUM('OPEN', 'IN_NEGOTIATION', 'DEAL_CLOSED', 'EXPIRED') DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (broker_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_broker_req_crop (crop),
    INDEX idx_broker_req_broker (broker_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS broker_offers;
CREATE TABLE broker_offers (
    id VARCHAR(64) PRIMARY KEY,
    requirement_id VARCHAR(64) NOT NULL,
    farmer_id VARCHAR(64) NOT NULL,
    crop_id VARCHAR(64),
    available_quantity DECIMAL(10,2) NOT NULL,
    expected_price DECIMAL(10,2) NOT NULL,
    available_date DATE,
    quality_details TEXT,
    message TEXT,
    status ENUM('OFFERED', 'COUNTERED', 'ACCEPTED', 'REJECTED') DEFAULT 'OFFERED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requirement_id) REFERENCES broker_requirements(id) ON DELETE CASCADE,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (crop_id) REFERENCES farmer_crops(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS broker_negotiations;
CREATE TABLE broker_negotiations (
    id VARCHAR(64) PRIMARY KEY,
    offer_id VARCHAR(64) NOT NULL,
    sender_id VARCHAR(64) NOT NULL,
    proposed_price DECIMAL(10,2) NOT NULL,
    proposed_quantity DECIMAL(10,2) NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (offer_id) REFERENCES broker_offers(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS broker_deals;
CREATE TABLE broker_deals (
    id VARCHAR(64) PRIMARY KEY,
    requirement_id VARCHAR(64) NOT NULL,
    offer_id VARCHAR(64) NOT NULL,
    broker_id VARCHAR(64) NOT NULL,
    farmer_id VARCHAR(64) NOT NULL,
    crop VARCHAR(128) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(32) DEFAULT 'Quintals',
    agreed_price DECIMAL(10,2) NOT NULL,
    price_unit VARCHAR(32) DEFAULT 'per Quintal',
    total_value DECIMAL(12,2) NOT NULL,
    pickup_date DATE,
    pickup_location VARCHAR(255),
    status ENUM('CONFIRMED', 'PICKUP_SCHEDULED', 'IN_TRANSIT', 'COMPLETED', 'DISPUTED') DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (requirement_id) REFERENCES broker_requirements(id) ON DELETE CASCADE,
    FOREIGN KEY (offer_id) REFERENCES broker_offers(id) ON DELETE CASCADE,
    FOREIGN KEY (broker_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_deals_broker (broker_id),
    INDEX idx_deals_farmer (farmer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 9. DIRECT FARM PRODUCE SALES
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS produce_listings;
CREATE TABLE produce_listings (
    id VARCHAR(64) PRIMARY KEY,
    farmer_id VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    category ENUM('VEGETABLES', 'FRUITS', 'GRAINS', 'PULSES', 'SPICES', 'ORGANIC') NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(32) DEFAULT 'Kg',
    price DECIMAL(10,2) NOT NULL,
    price_unit VARCHAR(32) DEFAULT 'per Kg',
    quality_grade VARCHAR(32) DEFAULT 'Grade A',
    harvest_date DATE,
    available_from DATE,
    available_until DATE,
    location VARCHAR(255),
    description TEXT,
    image VARCHAR(512),
    status ENUM('AVAILABLE', 'LOW_STOCK', 'SOLD_OUT', 'INACTIVE') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_produce_farmer (farmer_id),
    INDEX idx_produce_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS produce_orders;
CREATE TABLE produce_orders (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    farmer_id VARCHAR(64) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    delivery_address TEXT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'PACKED', 'OUT_FOR_DELIVERY', 'DELIVERED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_produce_orders_customer (customer_id),
    INDEX idx_produce_orders_farmer (farmer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS produce_order_items;
CREATE TABLE produce_order_items (
    id VARCHAR(64) PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL,
    listing_id VARCHAR(64) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(32) DEFAULT 'Kg',
    price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES produce_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (listing_id) REFERENCES produce_listings(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 10. COMMON DELIVERY LOGISTICS
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS delivery_jobs;
CREATE TABLE delivery_jobs (
    id VARCHAR(64) PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL,
    order_type ENUM('SELLER_PRODUCT', 'FARM_PRODUCE', 'AGRI_WASTE', 'BROKER_DEAL') NOT NULL,
    source_user_id VARCHAR(64) NOT NULL,
    destination_user_id VARCHAR(64) NOT NULL,
    pickup_location VARCHAR(255) NOT NULL,
    destination_location VARCHAR(255) NOT NULL,
    items_summary VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(32) DEFAULT 'Kg',
    pickup_date DATE,
    delivery_date DATE,
    delivery_fee DECIMAL(10,2) NOT NULL,
    assigned_partner_id VARCHAR(64),
    status ENUM('AVAILABLE', 'ASSIGNED', 'PICKUP_SCHEDULED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'COMPLETED', 'CANCELLED') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (source_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (destination_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_partner_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_delivery_partner (assigned_partner_id),
    INDEX idx_delivery_status (status),
    INDEX idx_delivery_order (order_id, order_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS delivery_status_history;
CREATE TABLE delivery_status_history (
    id VARCHAR(64) PRIMARY KEY,
    delivery_job_id VARCHAR(64) NOT NULL,
    status VARCHAR(64) NOT NULL,
    updated_by VARCHAR(64) NOT NULL,
    remarks TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (delivery_job_id) REFERENCES delivery_jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 11. AI DISEASE DETECTION HISTORY
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS disease_scans;
CREATE TABLE disease_scans (
    id VARCHAR(64) PRIMARY KEY,
    farmer_id VARCHAR(64) NOT NULL,
    crop_id VARCHAR(64),
    image_url VARCHAR(512) NOT NULL,
    disease_name VARCHAR(128) NOT NULL,
    confidence DECIMAL(5,2) NOT NULL,
    symptoms TEXT,
    prevention TEXT,
    treatment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (crop_id) REFERENCES farmer_crops(id) ON DELETE SET NULL,
    INDEX idx_scans_farmer (farmer_id),
    INDEX idx_scans_crop (crop_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 12. NOTIFICATIONS
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS notifications;
CREATE TABLE notifications (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    type VARCHAR(64) NOT NULL,
    title VARCHAR(128) NOT NULL,
    message TEXT NOT NULL,
    related_entity_id VARCHAR(64),
    related_entity_type VARCHAR(64),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_user (user_id),
    INDEX idx_notif_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 13. ACTIVITY RECORDS (UNIFIED FEED)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS activity_records;
CREATE TABLE activity_records (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    activity_type ENUM('LABOUR', 'WASTE', 'PRODUCT_ORDERS', 'CONTRACTS', 'BROKER_DEALS', 'PRODUCE_SALES', 'DELIVERY', 'DISEASE_SCAN') NOT NULL,
    related_entity_id VARCHAR(64),
    title VARCHAR(128) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_act_user (user_id),
    INDEX idx_act_type (activity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
