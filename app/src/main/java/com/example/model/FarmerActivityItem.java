package com.example.model;

import java.io.Serializable;

/**
 * Unified model representing an activity card in the Farmer Activities timeline.
 */
public class FarmerActivityItem implements Serializable {
    private String id;
    private String category; // "Labour", "Agri Waste", "Direct Produce", "Store Order", "Contract", "Broker"
    private String typeEmoji;
    private String title;
    private String counterparty;
    private String date;
    private String status; // "Active", "Pending", "Completed", "Confirmed", "Rejected"
    private double amount;
    private String details;

    public FarmerActivityItem() {}

    public FarmerActivityItem(String id, String category, String typeEmoji, String title,
                              String counterparty, String date, String status, double amount, String details) {
        this.id = id;
        this.category = category;
        this.typeEmoji = typeEmoji;
        this.title = title;
        this.counterparty = counterparty;
        this.date = date;
        this.status = status;
        this.amount = amount;
        this.details = details;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTypeEmoji() { return typeEmoji; }
    public void setTypeEmoji(String typeEmoji) { this.typeEmoji = typeEmoji; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCounterparty() { return counterparty; }
    public void setCounterparty(String counterparty) { this.counterparty = counterparty; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
