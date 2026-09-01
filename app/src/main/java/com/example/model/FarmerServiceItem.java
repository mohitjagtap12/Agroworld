package com.example.model;

import java.io.Serializable;

/**
 * Model representing a service action card on the Farmer Dashboard.
 */
public class FarmerServiceItem implements Serializable {
    public static final String ACTION_MY_CROPS = "action_my_crops";
    public static final String ACTION_AI_DISEASE = "action_ai_disease";
    public static final String ACTION_HIRE_LABOUR = "action_hire_labour";
    public static final String ACTION_BUY_PRODUCTS = "action_buy_products";
    public static final String ACTION_CONTRACT_FARMING = "action_contract_farming";
    public static final String ACTION_LIST_AGRI_WASTE = "action_list_agri_waste";
    public static final String ACTION_BROKER_TRADING = "action_broker_trading";
    public static final String ACTION_SELL_PRODUCE = "action_sell_produce";
    public static final String ACTION_DELIVERY_PORTAL = "action_delivery_portal";
    public static final String ACTION_MY_ACTIVITIES = "action_my_activities";

    private String id;
    private String iconEmoji;
    private String title;
    private String description;
    private String actionKey;
    private String badgeText;

    public FarmerServiceItem() {}

    public FarmerServiceItem(String id, String iconEmoji, String title, String description, String actionKey, String badgeText) {
        this.id = id;
        this.iconEmoji = iconEmoji;
        this.title = title;
        this.description = description;
        this.actionKey = actionKey;
        this.badgeText = badgeText;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIconEmoji() { return iconEmoji; }
    public void setIconEmoji(String iconEmoji) { this.iconEmoji = iconEmoji; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getActionKey() { return actionKey; }
    public void setActionKey(String actionKey) { this.actionKey = actionKey; }

    public String getBadgeText() { return badgeText; }
    public void setBadgeText(String badgeText) { this.badgeText = badgeText; }
}
