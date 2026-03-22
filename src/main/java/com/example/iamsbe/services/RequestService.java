package com.example.iamsbe.services;

import com.example.iamsbe.models.requests.BorrowRequest;
import com.example.iamsbe.models.responses.RequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface RequestService {
    RequestResponse createRequest(BorrowRequest borrowRequest);
    Page<RequestResponse> getMyRequests(Pageable pageable);
    RequestResponse approveRequest(Long requestId);
    RequestResponse rejectRequest(Long requestId, String reason);
    RequestResponse returnAsset(Long requestId);
    Page<RequestResponse> getAllRequests(Pageable pageable);
    RequestResponse requestReturn(Long requestId);
    void autoExpireRequests();
    long countRequestsByStatus(String status);
    List<Map<String, Object>> getBorrowingTrend();
    Page<RequestResponse> getPendingRequests(int page, int size);
}
