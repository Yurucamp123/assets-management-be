package com.example.iamsbe.services;

import com.example.iamsbe.models.entities.Asset;
import com.example.iamsbe.models.enums.AssetStatus;
import com.example.iamsbe.models.requests.AssetRequest;
import com.example.iamsbe.models.responses.AssetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetService {
    Page<AssetResponse> getAllAssets(Pageable pageable);

    AssetResponse createAsset(AssetRequest request);

    AssetResponse updateAsset(Long id, AssetRequest request);

    AssetResponse getAssetById(Long id);

    void deleteAsset(Long id);

    Page<AssetResponse> getAvailableAssets(Pageable pageable);

    long countAllAssets();

    long countAssetsByStatus(String status);

    Asset updateStatus(Long id, AssetStatus newStatus);
}
