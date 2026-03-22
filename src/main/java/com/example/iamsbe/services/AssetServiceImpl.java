package com.example.iamsbe.services;

import com.example.iamsbe.annotations.LogActivity;
import com.example.iamsbe.exceptions.AppException;
import com.example.iamsbe.exceptions.ErrorCode;
import com.example.iamsbe.models.entities.Asset;
import com.example.iamsbe.models.enums.AssetStatus;
import com.example.iamsbe.models.mapper.AssetMapper;
import com.example.iamsbe.models.requests.AssetRequest;
import com.example.iamsbe.models.responses.AssetResponse;
import com.example.iamsbe.repositories.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;

    @Override
    public Page<AssetResponse> getAllAssets(Pageable pageable) {
        return assetRepository.findAll(pageable).map(assetMapper::toResponse);
    }

    @Override
    @Transactional
    @LogActivity("CREATE_ASSET")
    public AssetResponse createAsset(AssetRequest request) {
        if (assetRepository.existsByAssetTag(request.getAssetTag())) {
            throw new AppException(ErrorCode.ASSET_TAG_DUPLICATE);
        }
        Asset asset = assetMapper.toEntity(request);
        asset.setStatus(AssetStatus.AVAILABLE);
        Asset saveAsset = assetRepository.save(asset);
        return assetMapper.toResponse(saveAsset);
    }

    @Override
    @Transactional
    @LogActivity("UPDATE_ASSET")
    public AssetResponse updateAsset(Long id, AssetRequest request) {
        Asset asset = assetRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.ASSET_NOT_FOUND));

        if (!request.getAssetTag().equals(asset.getAssetTag())
                && assetRepository.existsByAssetTag(request.getAssetTag())) {
            throw new AppException(ErrorCode.ASSET_TAG_DUPLICATE);
        }
        assetMapper.updateEntity(asset, request);
        Asset updatedAsset = assetRepository.save(asset);
        return assetMapper.toResponse(updatedAsset);
    }

    @Override
    public AssetResponse getAssetById(Long id) {
        Asset asset = assetRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.ASSET_NOT_FOUND));
        return assetMapper.toResponse(asset);
    }

    @Override
    @LogActivity("DELETE_ASSET")
    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.ASSET_NOT_FOUND));

        if ("ASSIGNED".equals(asset.getStatus().toString())) {
            throw new AppException(ErrorCode.ASSET_ALREADY_ASSIGNED);
        }
        assetRepository.delete(asset);
    }

    @Override
    public Page<AssetResponse> getAvailableAssets(Pageable pageable) {
        return assetRepository.findAllByStatus(AssetStatus.AVAILABLE, pageable)
                .map(assetMapper::toResponse);
    }

    @Override
    public long countAllAssets() {
        return assetRepository.count();
    }

    @Override
    public long countAssetsByStatus(String statusStr) {
        try {
            // Chuyển chuỗi "AVAILABLE" thành AssetStatus.AVAILABLE
            AssetStatus status = AssetStatus.valueOf(statusStr.toUpperCase());
            return assetRepository.countByStatus(status);
        } catch (IllegalArgumentException | NullPointerException e) {
            // Nếu truyền String sai (ví dụ: "ALREADY") thì trả về 0
            return 0;
        }
    }

    @Override
    @LogActivity("UPDATE_ASSET_STATUS")
    public Asset updateStatus(Long id, AssetStatus newStatus) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));

        if (asset.getStatus() == AssetStatus.BROKEN) {
            throw new RuntimeException("Thiết bị đã thanh lý/hỏng, không thể cập nhật.");
        }

        if (asset.getStatus() == AssetStatus.ASSIGNED) {
            throw new RuntimeException("Thiết bị đang được sử dụng, vui lòng thu hồi trước.");
        }

        asset.setStatus(newStatus);
        return assetRepository.save(asset);
    }
}
