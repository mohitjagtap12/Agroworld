-- ============================================================================
-- AGROWORLD - DATABASE SEED DATA (MySQL 8.0+)
-- ============================================================================

USE agroworld_db;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. USERS (BCrypt hashed passwords for demo 'password123': $2a$10$7EqJtq98hPqEX7fNZaFWoOhi5w7dD... )
INSERT INTO users (id, name, phone, email, password_hash, role, village, taluka, district, state) VALUES
('usr_farmer_01', 'Ramesh Patil', '9876543210', 'ramesh.patil@agroworld.in', '$2a$10$wK1q7eD9iU7c9Q2n3XmN6e6w1bH8qF2g7vY6zC8oT3kL9mJ1vB4wK', 'farmer', 'Baramati', 'Baramati', 'Pune', 'Maharashtra'),
('usr_seller_01', 'Kisan Seva Kendra', '9822012345', 'kisan.seva@agroworld.in', '$2a$10$wK1q7eD9iU7c9Q2n3XmN6e6w1bH8qF2g7vY6zC8oT3kL9mJ1vB4wK', 'seller', 'Hadapsar', 'Haveli', 'Pune', 'Maharashtra'),
('usr_labour_01', 'Suresh Shinde', '9850112233', 'suresh.labour@agroworld.in', '$2a$10$wK1q7eD9iU7c9Q2n3XmN6e6w1bH8qF2g7vY6zC8oT3kL9mJ1vB4wK', 'labour', 'Indapur', 'Indapur', 'Pune', 'Maharashtra'),
('usr_company_01', 'Sahyadri Agro Foods Ltd', '9823098765', 'procure@sahyadriagro.in', '$2a$10$wK1q7eD9iU7c9Q2n3XmN6e6w1bH8qF2g7vY6zC8oT3kL9mJ1vB4wK', 'company', 'Nashik MIDC', 'Nashik', 'Nashik', 'Maharashtra'),
('usr_broker_01', 'Mahesh APMC Trading', '9890123456', 'mahesh.apmc@agroworld.in', '$2a$10$wK1q7eD9iU7c9Q2n3XmN6e6w1bH8qF2g7vY6zC8oT3kL9mJ1vB4wK', 'broker', 'Gultekdi Mandi', 'Pune City', 'Pune', 'Maharashtra'),
('usr_customer_01', 'Priya Deshmukh', '9870099887', 'priya.deshmukh@gmail.com', '$2a$10$wK1q7eD9iU7c9Q2n3XmN6e6w1bH8qF2g7vY6zC8oT3kL9mJ1vB4wK', 'customer', 'Kothrud', 'Pune City', 'Pune', 'Maharashtra'),
('usr_delivery_01', 'Vijay Jadhav Logistics', '9860445566', 'vijay.logistics@agroworld.in', '$2a$10$wK1q7eD9iU7c9Q2n3XmN6e6w1bH8qF2g7vY6zC8oT3kL9mJ1vB4wK', 'delivery', 'Saswad', 'Purandar', 'Pune', 'Maharashtra');

-- 2. ROLE PROFILES
INSERT INTO farmer_profiles (id, user_id, total_land_acres, soil_type, irrigation_source, experience_years, kisan_credit_card_no) VALUES
('fp_01', 'usr_farmer_01', 7.5, 'Black Cotton', 'Drip & Well', 14, 'KCC-MAH-2024-8891');

INSERT INTO seller_profiles (id, user_id, shop_name, gst_number, license_number, shop_address, rating) VALUES
('sp_01', 'usr_seller_01', 'Kisan Seva Kendra & Agri Supplies', '27AABCK1234F1Z5', 'AGRI-RET-MH-9941', 'Shop #12, Market Yard Road, Hadapsar, Pune 411028', 4.8);

INSERT INTO labour_profiles (id, user_id, skill_level, experience_years, primary_skills, daily_wage_rate, is_available, rating) VALUES
('lp_01', 'usr_labour_01', 'SKILLED', 8, 'Sugarcane harvesting, Tractor driving, Drip installation', 650.00, TRUE, 4.9);

INSERT INTO company_profiles (id, user_id, company_name, cin_number, industry_type, headquarters) VALUES
('cp_01', 'usr_company_01', 'Sahyadri Agro Foods Ltd', 'U01111MH2012PLC123456', 'Food Processing & Exports', 'Nashik, Maharashtra');

INSERT INTO broker_profiles (id, user_id, firm_name, mandi_license_no, operating_mandi, rating) VALUES
('bp_01', 'usr_broker_01', 'Mahesh APMC Commission Agents', 'APMC-PUN-COMM-4421', 'Pune Gultekdi Market Yard', 4.7);

INSERT INTO customer_profiles (id, user_id, delivery_address, preferred_language) VALUES
('cup_01', 'usr_customer_01', 'Flat 402, Green Acres Residency, Paud Road, Kothrud, Pune - 411038', 'Marathi');

INSERT INTO delivery_partner_profiles (id, user_id, vehicle_type, vehicle_number, driving_license_no, max_capacity_kg, service_radius_km, is_online, rating, total_deliveries) VALUES
('dp_01', 'usr_delivery_01', 'TEMPO_TATA_ACE', 'MH-12-FC-4491', 'MH1220180099441', 1200.00, 60, TRUE, 4.9, 142);

-- 3. FARMER CROPS
INSERT INTO farmer_crops (id, farmer_id, crop_name, variety, land_area, land_unit, sowing_date, expected_harvest_date, location, status) VALUES
('crop_01', 'usr_farmer_01', 'Sugarcane', 'Co 86032', 3.5, 'Acres', '2025-10-15', '2026-10-20', 'North Plot - Well Area', 'GROWING'),
('crop_02', 'usr_farmer_01', 'Tomato', 'Abhinav Hybrid', 1.5, 'Acres', '2026-01-10', '2026-04-15', 'Polyhouse Unit 2', 'FLOWERING'),
('crop_03', 'usr_farmer_01', 'Wheat', 'Lokwan 147', 2.0, 'Acres', '2025-11-20', '2026-03-25', 'South Canal Plot', 'READY_FOR_HARVEST');

-- 4. LABOUR REQUIREMENTS
INSERT INTO labour_requirements (id, farmer_id, crop_id, work_type, description, workers_required, skill_level, experience_required, start_date, end_date, start_time, working_hours, wage_type, wage_amount, village, taluka, district, status) VALUES
('lab_req_01', 'usr_farmer_01', 'crop_01', 'Sugarcane Harvesting', 'Experienced team required for cutting, bundling and loading on tractor trolley', 4, 'SKILLED', 2, '2026-09-05', '2026-09-12', '07:00 AM', 8.0, 'DAILY', 650.00, 'Baramati', 'Baramati', 'Pune', 'OPEN');

-- 5. AGRI WASTE LISTINGS
INSERT INTO agri_waste_listings (id, farmer_id, waste_type, waste_name, quantity, unit, price, price_unit, available_date, village, taluka, district, description, status) VALUES
('waste_01', 'usr_farmer_01', 'Crop Residue', 'Dry Sugarcane Trash & Bagasse', 25.0, 'Tons', 1200.00, 'per Ton', '2026-09-10', 'Baramati', 'Baramati', 'Pune', 'Sun-dried clean sugarcane crop residue suitable for bio-briquettes and boiler fuel.', 'AVAILABLE'),
('waste_02', 'usr_farmer_01', 'Straw / Husk', 'Golden Wheat Straw Husk (Bhoosa)', 15.0, 'Tons', 1600.00, 'per Ton', '2026-09-05', 'Baramati', 'Baramati', 'Pune', 'Clean, chaff-free wheat straw, ideal for animal feed and packaging.', 'AVAILABLE');

-- 6. PRODUCTS (SELLER INVENTORY)
INSERT INTO products (id, seller_id, name, category, description, price, unit, stock_quantity, location, status) VALUES
('prod_01', 'usr_seller_01', 'Mahyco Hybrid Tomato Seeds (100g)', 'SEEDS', 'High-yield, disease resistant premium tomato seeds.', 450.00, 'Pack', 120.0, 'Hadapsar Warehouse', 'ACTIVE'),
('prod_02', 'usr_seller_01', 'IFFCO NPK 19:19:19 100% Water Soluble (25kg)', 'FERTILIZERS', 'Balanced water-soluble fertilizer for drip irrigation & foliar spray.', 1850.00, 'Bag', 85.0, 'Hadapsar Warehouse', 'ACTIVE'),
('prod_03', 'usr_seller_01', 'Neem Oil Bio-Pesticide 10000 PPM (1L)', 'PESTICIDES', 'Certified organic botanical insecticide and repellent.', 580.00, 'Bottle', 60.0, 'Hadapsar Warehouse', 'ACTIVE');

-- 7. CONTRACTS
INSERT INTO contracts (id, company_id, crop, required_quantity, unit, offered_price, price_unit, quality_requirements, harvest_period, delivery_location, application_deadline, contract_duration, payment_terms, status) VALUES
('contract_01', 'usr_company_01', 'Processing Tomato (Red Hybrid)', 150.0, 'Tons', 1450.00, 'per Quintal', 'Brix > 4.8, Deep red color, Firm texture, Free from fungal blemishes', 'Nov 2026 - Jan 2027', 'Sahyadri Food Processing Hub, Nashik', '2026-09-30', '4 Months', '50% on harvest loading, 50% within 48h of QC pass at gate', 'ACTIVE');

-- 8. BROKER REQUIREMENTS
INSERT INTO broker_requirements (id, broker_id, crop, required_quantity, unit, offered_price, price_unit, quality_requirement, required_date, pickup_location, payment_terms, status) VALUES
('brk_req_01', 'usr_broker_01', 'Wheat (Lokwan 147)', 80.0, 'Quintals', 2525.00, 'per Quintal', 'Moisture < 11%, Grain luster bright, Foreign matter < 1%', '2026-09-15', 'Pune APMC Yard Gate 2', 'Immediate RTGS / UPI on digital weighing voucher', 'OPEN');

-- 9. PRODUCE LISTINGS (FARMER DIRECT HARVEST)
INSERT INTO produce_listings (id, farmer_id, name, category, quantity, unit, price, price_unit, quality_grade, harvest_date, available_from, available_until, location, description, status) VALUES
('prod_list_01', 'usr_farmer_01', 'Farm Fresh Desi Tomatoes', 'VEGETABLES', 500.0, 'Kg', 32.00, 'per Kg', 'Grade A', '2026-09-01', '2026-09-02', '2026-09-10', 'Baramati Farm Yard', 'Vine-ripened, organic compost-grown juicy red tomatoes.', 'AVAILABLE'),
('prod_list_02', 'usr_farmer_01', 'Sweet Royal Guavas', 'FRUITS', 250.0, 'Kg', 65.00, 'per Kg', 'Grade A+', '2026-09-02', '2026-09-03', '2026-09-12', 'Baramati Farm Yard', 'Crisp, aromatic white-flesh sweet guavas.', 'AVAILABLE');

-- 10. DELIVERY JOBS
INSERT INTO delivery_jobs (id, order_id, order_type, source_user_id, destination_user_id, pickup_location, destination_location, items_summary, quantity, unit, pickup_date, delivery_date, delivery_fee, assigned_partner_id, status) VALUES
('del_job_01', 'ord_sample_01', 'SELLER_PRODUCT', 'usr_seller_01', 'usr_farmer_01', 'Hadapsar Agri Mart, Pune', 'Baramati Farm House, Pune', '2x NPK Fertilizer Bags (50kg)', 50.0, 'Kg', '2026-09-02', '2026-09-02', 320.00, 'usr_delivery_01', 'ASSIGNED');

-- 11. NOTIFICATIONS
INSERT INTO notifications (id, user_id, type, title, message, related_entity_id, related_entity_type, is_read) VALUES
('notif_01', 'usr_farmer_01', 'LABOUR_ALERT', 'Worker Inquiry Received', 'Suresh Shinde applied for your Sugarcane Harvesting job post.', 'lab_req_01', 'labour_requirement', FALSE),
('notif_02', 'usr_farmer_01', 'SYSTEM_UPDATE', 'Logistics Partner Assigned', 'Vijay Jadhav Logistics accepted your farm input supply freight.', 'del_job_01', 'delivery_job', FALSE);

-- 12. ACTIVITY RECORDS
INSERT INTO activity_records (id, user_id, activity_type, related_entity_id, title, description, status) VALUES
('act_01', 'usr_farmer_01', 'LABOUR', 'lab_req_01', 'Labour Requirement Posted', 'Created labour demand for 4 workers for Sugarcane Harvesting at ₹650/day.', 'OPEN'),
('act_02', 'usr_farmer_01', 'WASTE', 'waste_01', 'Agri Waste Listed', 'Listed 25 Tons of Sugarcane Trash at ₹1,200/Ton.', 'AVAILABLE');

SET FOREIGN_KEY_CHECKS = 1;
