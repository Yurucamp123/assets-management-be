package com.example.iamsbe.utils;

import com.example.iamsbe.models.enums.AssetCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AssetCategoryConverter implements AttributeConverter<AssetCategory, String> {
    @Override
    public String convertToDatabaseColumn(AssetCategory attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public AssetCategory convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return AssetCategory.valueOf(dbData.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return AssetCategory.OTHER; // Trả về mặc định nếu là "Laptop" thay vì "LAPTOP"
        }
    }
}