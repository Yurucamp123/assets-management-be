package com.example.iamsbe.controllers;

import com.example.iamsbe.models.enums.AssetStatus;
import com.example.iamsbe.models.responses.ApiResponse;
import com.example.iamsbe.models.responses.RequestResponse;
import com.example.iamsbe.services.AssetService;
import com.example.iamsbe.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final RequestService requestService;
    private final AssetService assetService;

    // Xem tất cả yêu cầu mượn máy
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<Page<RequestResponse>>> getAllRequests(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Page<RequestResponse>>builder()
                .result(requestService.getAllRequests(pageable))
                .build());
    }

    // Duyệt yêu cầu
    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<ApiResponse<RequestResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<RequestResponse>builder()
                .result(requestService.approveRequest(id))
                .build());
    }

    // Từ chối và gửi lý do
    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<ApiResponse<RequestResponse>> reject(
            @PathVariable Long id, @RequestBody String reason) {
        return ResponseEntity.ok(ApiResponse.<RequestResponse>builder()
                .result(requestService.rejectRequest(id, reason))
                .build());
    }

    // Xác nhận đã trả máy
    @PostMapping("/requests/{id}/return")
    public ResponseEntity<ApiResponse<RequestResponse>> returnAsset(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<RequestResponse>builder()
                .result(requestService.returnAsset(id))
                .build());
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Thống kê tổng quan
        summary.put("totalAssets", assetService.countAllAssets());
        summary.put("pendingRequests", requestService.countRequestsByStatus("PENDING"));
        summary.put("activeBorrowing",assetService.countAssetsByStatus("ASSIGNED"));
        summary.put("brokenAssets", assetService.countAssetsByStatus("BROKEN"));
        summary.put("maintenanceAssets", assetService.countAssetsByStatus("MAINTENANCE"));
        summary.put("availableAssets", assetService.countAssetsByStatus("AVAILABLE"));

        // Dữ liệu biểu đồ
        summary.put("chartData", requestService.getBorrowingTrend());

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .result(summary)
                .build());
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<Page<RequestResponse>>> getPendingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.<Page<RequestResponse>>builder()
                .result(requestService.getPendingRequests(page, size))
                .build());
    }

    @PatchMapping("/assets/{id}/status")
    public ResponseEntity<?> updateAssetStatus(
            @PathVariable Long id,
            @RequestParam AssetStatus newStatus) {
        return ResponseEntity.ok(assetService.updateStatus(id, newStatus));
    }
}