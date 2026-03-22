package com.example.iamsbe.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AssetCategory {
    LAPTOP,
    SMARTPHONE,
    ACCESSORY,
    OTHER;

    @JsonCreator
    public static AssetCategory fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String cleanedValue = value.trim().toUpperCase();
        for (AssetCategory category : AssetCategory.values()) {
            if (category.name().equals(cleanedValue)) {
                return category;
            }
        }
        // Trả về một giá trị mặc định hoặc null thay vì để nó nổ exception
        return OTHER;
    }
}