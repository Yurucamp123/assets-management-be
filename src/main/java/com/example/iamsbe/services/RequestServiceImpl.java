package com.example.iamsbe.services;

import com.example.iamsbe.annotations.LogActivity;
import com.example.iamsbe.exceptions.AppException;
import com.example.iamsbe.exceptions.ErrorCode;
import com.example.iamsbe.models.entities.Asset;
import com.example.iamsbe.models.entities.Request;
import com.example.iamsbe.models.entities.User;
import com.example.iamsbe.models.enums.AssetStatus;
import com.example.iamsbe.models.enums.RequestStatus;
import com.example.iamsbe.models.mapper.RequestMapper;
import com.example.iamsbe.models.requests.BorrowRequest;
import com.example.iamsbe.models.responses.RequestResponse;
import com.example.iamsbe.repositories.AssetRepository;
import com.example.iamsbe.repositories.RequestRepository;
import com.example.iamsbe.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    @LogActivity("CREATE_REQUEST")
    public RequestResponse createRequest(BorrowRequest borrowRequest) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Asset asset = assetRepository.findById(borrowRequest.getAssetId())
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        if ("ASSIGNED".equals(asset.getStatus().name())) {
            throw new AppException(ErrorCode.ASSET_ALREADY_ASSIGNED);
        }

        boolean alreadyPending = requestRepository.existsByUserAndAssetAndStatus(user, asset, "PENDING");
        if (alreadyPending) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_EXISTS);
        }

        if (borrowRequest.getBorrowDate().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.BORROW_DATE_INVALID);
        }
        if (borrowRequest.getReturnDate().isBefore(borrowRequest.getBorrowDate())) {
            throw new AppException(ErrorCode.RETURN_DATE_BEFORE_BORROW);
        }

        Request request = requestMapper.toEntity(borrowRequest);
        request.setUser(user);
        request.setAsset(asset);
        request.setStatus("PENDING");

        return requestMapper.toResponse(requestRepository.save(request));
    }

    @Override
    public Page<RequestResponse> getMyRequests(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return requestRepository.findAllByUserUsername(username, pageable)
                .map(requestMapper::toResponse);
    }

    @Override
    @Transactional
    @LogActivity("APPROVE_REQUEST")
    public RequestResponse approveRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        if (!"PENDING".equals(request.getStatus())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        Asset asset = request.getAsset();
        if (asset.getStatus() == AssetStatus.ASSIGNED) {
            throw new AppException(ErrorCode.ASSET_ALREADY_ASSIGNED_ADMIN);
        }
        request.setStatus("APPROVED");
        asset.setStatus(AssetStatus.ASSIGNED);

        assetRepository.save(asset);
        return requestMapper.toResponse(requestRepository.save(request));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @LogActivity("REJECT_REQUEST")
    public RequestResponse rejectRequest(Long requestId, String reason) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        if (!"PENDING".equals(request.getStatus())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        request.setStatus("REJECTED");
        request.setRejectReason(reason);

        return requestMapper.toResponse(requestRepository.save(request));
    }

    @Override
    @Transactional
    @LogActivity("RETURN_ASSET")
    public RequestResponse returnAsset(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        if (!"APPROVED".equals(request.getStatus())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        // Đổi trạng thái yêu cầu về RETURNED và giải phóng máy về AVAILABLE
        request.setStatus("RETURNED");
        request.getAsset().setStatus(AssetStatus.AVAILABLE);

        return requestMapper.toResponse(requestRepository.save(request));
    }

    @Override
    public Page<RequestResponse> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable).map(requestMapper::toResponse);
    }

    @Transactional
    @LogActivity("REQUEST_RETURN_ASSET")
    public RequestResponse requestReturn(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

        if (!"APPROVED".equals(request.getStatus())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        request.setStatus("WAITING_FOR_RETURN");
        return requestMapper.toResponse(requestRepository.save(request));
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void autoExpireRequests() {
        log.info("Bắt đầu dọn dẹp các yêu cầu quá hạn...");
        List<Request> expiredList = requestRepository
                .findAllByStatusAndBorrowDateBefore("PENDING", LocalDate.now());

        for (Request req : expiredList) {
            req.setStatus("EXPIRED");
        }
        requestRepository.saveAll(expiredList);
    }

    @Override
    public long countRequestsByStatus(String status) {
        return requestRepository.countByStatus(status);
    }

    @Override
    public List<Map<String, Object>> getBorrowingTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);
            long count = requestRepository.countByBorrowDate(targetDate);

            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("day", targetDate.getDayOfWeek().toString().substring(0, 3)); // VD: MON, TUE
            dataPoint.put("requests", count);
            trend.add(dataPoint);
        }
        return trend;
    }

    @Override
    public Page<RequestResponse> getPendingRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());

        Page<Request> requests = requestRepository.findAllByStatus(RequestStatus.PENDING.toString(), pageable);

        return requests.map(request -> RequestResponse.builder()
                .id(request.getId())
                .assetName(request.getAsset().getAssetName())
                .username(request.getUser().getUsername())
                .borrowDate(request.getBorrowDate())
                .returnDate(request.getReturnDate())
                .status(RequestStatus.valueOf(request.getStatus()))
                .build());
    }
}