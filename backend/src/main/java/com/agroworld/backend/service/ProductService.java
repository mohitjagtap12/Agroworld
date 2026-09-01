package com.agroworld.backend.service;

import com.agroworld.backend.entity.DeliveryJobEntity;
import com.agroworld.backend.entity.ProductEntity;
import com.agroworld.backend.entity.ProductOrderEntity;
import com.agroworld.backend.entity.ProductOrderItemEntity;
import com.agroworld.backend.repository.DeliveryJobRepository;
import com.agroworld.backend.repository.ProductOrderItemRepository;
import com.agroworld.backend.repository.ProductOrderRepository;
import com.agroworld.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private ProductOrderItemRepository orderItemRepository;

    @Autowired
    private DeliveryJobRepository deliveryJobRepository;

    public List<ProductEntity> getAllActiveProducts() {
        return productRepository.findByStatus("ACTIVE");
    }

    public List<ProductEntity> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<ProductEntity> getProductsBySeller(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    @Transactional
    public ProductEntity addProduct(String sellerId, ProductEntity product) {
        if (product.getId() == null) product.setId("prod_" + UUID.randomUUID().toString().substring(0, 8));
        product.setSellerId(sellerId);
        product.setStatus("ACTIVE");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public ProductOrderEntity createOrder(String farmerId, String sellerId, List<ProductOrderItemEntity> items, String deliveryAddress) {
        double total = 0.0;
        double totalQuantity = 0.0;

        // 1. Verify stock and calculate total server-side
        for (ProductOrderItemEntity item : items) {
            ProductEntity product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() + ", Requested: " + item.getQuantity());
            }

            // Decrement stock
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            if (product.getStockQuantity() <= 0) {
                product.setStatus("OUT_OF_STOCK");
            }
            productRepository.save(product);

            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice() * item.getQuantity());
            total += item.getSubtotal();
            totalQuantity += item.getQuantity();
        }

        // 2. Save Order
        String orderId = "pord_" + UUID.randomUUID().toString().substring(0, 8);
        ProductOrderEntity order = new ProductOrderEntity();
        order.setId(orderId);
        order.setFarmerId(farmerId);
        order.setSellerId(sellerId);
        order.setTotalAmount(total);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        // 3. Save Items
        for (ProductOrderItemEntity item : items) {
            item.setId("pitem_" + UUID.randomUUID().toString().substring(0, 8));
            item.setOrderId(orderId);
            orderItemRepository.save(item);
        }

        // 4. Create Unified Delivery Job
        DeliveryJobEntity deliveryJob = new DeliveryJobEntity();
        deliveryJob.setId("del_" + UUID.randomUUID().toString().substring(0, 8));
        deliveryJob.setOrderId(orderId);
        deliveryJob.setOrderType("SELLER_PRODUCT");
        deliveryJob.setSourceUserId(sellerId);
        deliveryJob.setDestinationUserId(farmerId);
        deliveryJob.setPickupLocation("Seller Warehouse, Hadapsar");
        deliveryJob.setDestinationLocation(deliveryAddress);
        deliveryJob.setItemsSummary(items.size() + " farm input products");
        deliveryJob.setQuantity(totalQuantity);
        deliveryJob.setUnit("Kg");
        deliveryJob.setPickupDate(LocalDate.now().plusDays(1));
        deliveryJob.setDeliveryDate(LocalDate.now().plusDays(2));
        deliveryJob.setDeliveryFee(250.0);
        deliveryJob.setStatus("AVAILABLE");
        deliveryJobRepository.save(deliveryJob);

        return order;
    }

    public List<ProductOrderEntity> getOrdersByFarmer(String farmerId) {
        return orderRepository.findByFarmerId(farmerId);
    }

    public List<ProductOrderEntity> getOrdersBySeller(String sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }
}
