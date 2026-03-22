package com.example.iamsbe.controllers;

import com.example.iamsbe.models.requests.AssetRequest;
import com.example.iamsbe.models.responses.ApiResponse;
import com.example.iamsbe.models.responses.AssetResponse;
import com.example.iamsbe.services.AssetService;
import com.example.iamsbe.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AssetResponse>>> getAllAssets(
            @ParameterObject Pageable pageable) {
        return ResponseUtils.ok(assetService.getAllAssets(pageable), "Lấy danh sách thiết bị thành công");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssetResponse>> create(@Valid @RequestBody AssetRequest request) {
        return ResponseUtils.created(assetService.createAsset(request), "Thêm thiết bị thành công");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssetResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request) {
        return ResponseUtils.ok(assetService.updateAsset(id, request), "Cập nhật thiết bị thành công");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetResponse>> getOne(@PathVariable Long id) {
        return ResponseUtils.ok(assetService.getAssetById(id), "Lấy chi tiết thành công");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseUtils.noContent("Xóa thiết bị thành công");
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<AssetResponse>>>
    getAvailableAssets(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Page<AssetResponse>>builder()
                .result(assetService.getAvailableAssets(pageable))
                .build());
    }

}