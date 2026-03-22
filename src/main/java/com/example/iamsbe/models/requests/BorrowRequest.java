package com.example.iamsbe.models.requests;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BorrowRequest {
    @NotNull(message = "ASSET_ID_REQUIRED")
    private Long assetId;

    @NotNull(message = "BORROW_DATE_REQUIRED")
    @FutureOrPresent(message = "BORROW_DATE_INVALID") // Ngày mượn phải từ hôm nay trở đi
    private LocalDate borrowDate;

    @NotNull(message = "RETURN_DATE_REQUIRED")
    private LocalDate returnDate;

    private String note; // Ghi chú lý do mượn
}