package com.agroworld.backend.controller;

import com.agroworld.backend.dto.ApiResponse;
import com.agroworld.backend.entity.ProductEntity;
import com.agroworld.backend.entity.ProductOrderEntity;
import com.agroworld.backend.entity.ProductOrderItemEntity;
import com.agroworld.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductEntity>>> getAllProducts(@RequestParam(required = false) String category) {
        List<ProductEntity> products = (category != null && !category.isEmpty())
                ? productService.getProductsByCategory(category)
                : productService.getAllActiveProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductEntity>> addProduct(Authentication auth, @RequestBody ProductEntity product) {
        String sellerId = (String) auth.getPrincipal();
        if (product.getPrice() == null || product.getPrice() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Price must be greater than zero."));
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Stock quantity cannot be negative."));
        }
        ProductEntity created = productService.addProduct(sellerId, product);
        return ResponseEntity.ok(ApiResponse.success("Product listed successfully", created));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<ProductOrderEntity>> placeOrder(
            Authentication auth,
            @RequestBody Map<String, Object> orderPayload) {
        String farmerId = (String) auth.getPrincipal();
        String sellerId = (String) orderPayload.get("sellerId");
        String deliveryAddress = (String) orderPayload.get("deliveryAddress");

        if (sellerId != null && sellerId.equals(farmerId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Sellers cannot purchase their own products."));
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) orderPayload.get("items");
        if (rawItems == null || rawItems.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Order must contain at least one item."));
        }

        List<ProductOrderItemEntity> items = rawItems.stream().map(itemMap -> {
            ProductOrderItemEntity item = new ProductOrderItemEntity();
            item.setProductId((String) itemMap.get("productId"));
            item.setQuantity(((Number) itemMap.get("quantity")).doubleValue());
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive.");
            }
            return item;
        }).toList();

        try {
            ProductOrderEntity order = productService.createOrder(farmerId, sellerId, items, deliveryAddress);
            return ResponseEntity.ok(ApiResponse.success("Order placed successfully", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/orders/farmer")
    public ResponseEntity<ApiResponse<List<ProductOrderEntity>>> getFarmerOrders(Authentication auth) {
        String farmerId = (String) auth.getPrincipal();
        List<ProductOrderEntity> orders = productService.getOrdersByFarmer(farmerId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/orders/seller")
    public ResponseEntity<ApiResponse<List<ProductOrderEntity>>> getSellerOrders(Authentication auth) {
        String sellerId = (String) auth.getPrincipal();
        List<ProductOrderEntity> orders = productService.getOrdersBySeller(sellerId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}
