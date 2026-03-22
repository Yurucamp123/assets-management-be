package com.example.iamsbe.controllers;

import com.example.iamsbe.models.requests.BorrowRequest;
import com.example.iamsbe.models.responses.ApiResponse;
import com.example.iamsbe.models.responses.RequestResponse;
import com.example.iamsbe.services.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<RequestResponse>> createBorrowRequest(
            @Valid @RequestBody BorrowRequest borrowRequest) {
        return ResponseEntity.ok(ApiResponse.<RequestResponse>builder()
                .result(requestService.createRequest(borrowRequest))
                .build());
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<RequestResponse>>> getMyHistory(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Page<RequestResponse>>builder()
                .result(requestService.getMyRequests(pageable))
                .build());
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<ApiResponse<RequestResponse>> requestReturn(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<RequestResponse>builder()
                .result(requestService.requestReturn(id))
                .build());
    }
}
