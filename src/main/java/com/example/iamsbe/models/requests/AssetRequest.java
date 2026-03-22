package com.example.iamsbe.models.requests;

import com.example.iamsbe.models.enums.AssetCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetRequest {
    @NotBlank(message = "ASSET_NAME_REQUIRED")
    private String assetName;

    @NotBlank(message = "ASSET_TAG_REQUIRED")
    private String assetTag;

    @NotNull(message = "CATEGORY_REQUIRED")
    private AssetCategory category;
    
    private String description;
}