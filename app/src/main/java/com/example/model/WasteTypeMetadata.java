package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Metadata for standard Agricultural Waste categories and recommended units/uses.
 */
public class WasteTypeMetadata implements Serializable {
    private String name;
    private String emoji;
    private String category;
    private String defaultUnit;
    private String defaultPriceUnit;
    private List<String> typicalUses;
    private String industrialDemand;

    public WasteTypeMetadata() {
        this.typicalUses = new ArrayList<>();
    }

    public WasteTypeMetadata(String name, String emoji, String category, String defaultUnit,
                             String defaultPriceUnit, List<String> typicalUses, String industrialDemand) {
        this.name = name;
        this.emoji = emoji;
        this.category = category;
        this.defaultUnit = defaultUnit;
        this.defaultPriceUnit = defaultPriceUnit;
        this.typicalUses = typicalUses != null ? new ArrayList<>(typicalUses) : new ArrayList<>();
        this.industrialDemand = industrialDemand;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDefaultUnit() { return defaultUnit; }
    public void setDefaultUnit(String defaultUnit) { this.defaultUnit = defaultUnit; }

    public String getDefaultPriceUnit() { return defaultPriceUnit; }
    public void setDefaultPriceUnit(String defaultPriceUnit) { this.defaultPriceUnit = defaultPriceUnit; }

    public List<String> getTypicalUses() { return typicalUses; }
    public void setTypicalUses(List<String> typicalUses) { this.typicalUses = typicalUses != null ? new ArrayList<>(typicalUses) : new ArrayList<>(); }

    public String getIndustrialDemand() { return industrialDemand; }
    public void setIndustrialDemand(String industrialDemand) { this.industrialDemand = industrialDemand; }

    @Override
    public String toString() {
        return "WasteTypeMetadata{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", defaultUnit='" + defaultUnit + '\'' +
                '}';
    }
}
