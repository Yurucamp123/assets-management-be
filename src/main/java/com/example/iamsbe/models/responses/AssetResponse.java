package com.example.iamsbe.models.responses;

import lombok.Data;

@Data
public class AssetResponse {
    private Long id;
    private String assetName;
    private String assetTag;
    private String category;
    private String status;
}