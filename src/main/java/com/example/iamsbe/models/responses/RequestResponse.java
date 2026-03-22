package com.example.iamsbe.models.responses;

import com.example.iamsbe.models.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RequestResponse {
    private Long id;
    private String username;
    private String assetName;   // Tên thiết bị
    private String assetTag;    // Mã thiết bị để dễ đối chiếu
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private RequestStatus status; // PENDING, APPROVED, REJECTED
    private String rejectReason; // Lý do nếu bị từ chối
    private LocalDateTime createdAt;
}