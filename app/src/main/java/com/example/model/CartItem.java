package com.example.model;

import java.io.Serializable;

/**
 * Model representing an item in the Farmer's shopping cart.
 */
public class CartItem implements Serializable {
    private String productId;
    private SellerProduct product;
    private int quantity;
    private double subtotal;

    public CartItem() {}

    public CartItem(SellerProduct product, int quantity) {
        this.product = product;
        this.productId = product != null ? product.getId() : "";
        this.quantity = quantity;
        this.subtotal = product != null ? (product.getPrice() * quantity) : 0.0;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public SellerProduct getProduct() { return product; }
    public void setProduct(SellerProduct product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
            this.subtotal = product.getPrice() * this.quantity;
        }
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (product != null) {
            this.subtotal = product.getPrice() * this.quantity;
        }
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
