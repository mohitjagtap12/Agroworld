package com.agroworld.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "produce_order_items")
public class ProduceOrderItemEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "listing_id", nullable = false, length = 64)
    private String listingId;

    @Column(nullable = false)
    private Double quantity;

    @Column(length = 32)
    private String unit = "Kg";

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double subtotal;

    public ProduceOrderItemEntity() {}

    public ProduceOrderItemEntity(String id, String orderId, String listingId, Double quantity, String unit, Double price, Double subtotal) {
        this.id = id;
        this.orderId = orderId;
        this.listingId = listingId;
        this.quantity = quantity;
        this.unit = unit != null ? unit : "Kg";
        this.price = price;
        this.subtotal = subtotal;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}
