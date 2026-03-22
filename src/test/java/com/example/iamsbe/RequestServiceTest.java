package com.example.iamsbe;

import com.example.iamsbe.exceptions.AppException;
import com.example.iamsbe.models.entities.Asset;
import com.example.iamsbe.models.entities.Request;
import com.example.iamsbe.models.entities.User;
import com.example.iamsbe.models.enums.AssetCategory;
import com.example.iamsbe.models.enums.AssetStatus;
import com.example.iamsbe.models.enums.RequestStatus;
import com.example.iamsbe.models.requests.BorrowRequest;
import com.example.iamsbe.models.responses.RequestResponse;
import com.example.iamsbe.repositories.AssetRepository;
import com.example.iamsbe.repositories.RequestRepository;
import com.example.iamsbe.repositories.UserRepository;
import com.example.iamsbe.services.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RequestServiceTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    private User mockUser;
    private Asset mockAsset;

    @BeforeEach
    void setUp() {
        // 1. Tạo User giả lập - Nhớ điền đầy đủ các trường bắt buộc (fullName, role, v.v.)
        mockUser = new User();
        mockUser.setUsername("binh_test");
        mockUser.setFullName("Nguyen Van Binh"); // THÊM DÒNG NÀY ĐỂ HẾT LỖI
        mockUser.setEmail("binh@gmail.com");
        mockUser.setEnabled(true);
        // Nếu Entity User yêu cầu Role, bạn cũng nên set luôn
        // mockUser.setRole(UserRole.ROLE_USER);
        userRepository.save(mockUser);

        // 2. Tạo Asset giả lập - Kiểm tra xem có trường nào bắt buộc khác không (ví dụ: Category)
        mockAsset = new Asset();
        mockAsset.setAssetName("Macbook M3");
        mockAsset.setAssetTag("LAP-001"); // Thêm Tag nếu cần
        mockAsset.setStatus(AssetStatus.AVAILABLE);
        // Trong Log thấy có trường Category enum (OTHER, LAPTOP...)
        mockAsset.setCategory(AssetCategory.LAPTOP); // THÊM DÒNG NÀY NẾU BẮT BUỘC
        assetRepository.save(mockAsset);

        // 3. Giả lập SecurityContext
        Authentication auth = new UsernamePasswordAuthenticationToken("binh_test", "password");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Tạo yêu cầu mượn máy thành công")
    void createRequest_Success() {
        // Given
        BorrowRequest requestDto = new BorrowRequest();
        requestDto.setAssetId(mockAsset.getId());
        requestDto.setBorrowDate(LocalDate.now().plusDays(1));
        requestDto.setReturnDate(LocalDate.now().plusDays(5));

        // When
        RequestResponse response = requestService.createRequest(requestDto);

        // Then
        assertNotNull(response);
        assertEquals(RequestStatus.PENDING, response.getStatus());
        assertEquals(mockAsset.getAssetName(), response.getAssetName());

        assertTrue(requestRepository.existsByUserAndAssetAndStatus(mockUser, mockAsset, RequestStatus.PENDING.name()));
    }

    @Test
    @DisplayName("Admin duyệt yêu cầu thành công - Trạng thái máy đổi sang ASSIGNED")
    void approveRequest_Success() {
        // Given: Tạo sẵn một yêu cầu PENDING bằng Setter
        Request pendingReq = new Request();
        pendingReq.setUser(mockUser);
        pendingReq.setAsset(mockAsset);
        pendingReq.setStatus("PENDING");
        pendingReq.setBorrowDate(LocalDate.now());
        pendingReq.setReturnDate(LocalDate.now().plusDays(3));
        pendingReq = requestRepository.save(pendingReq);

        // When: Admin duyệt
        RequestResponse response = requestService.approveRequest(pendingReq.getId());

        // Then
        assertEquals(RequestStatus.APPROVED, response.getStatus());

        Asset updatedAsset = assetRepository.findById(mockAsset.getId()).get();
        assertEquals(AssetStatus.ASSIGNED, updatedAsset.getStatus());
    }

    @Test
    @DisplayName("Không cho phép mượn máy nếu máy đã bị ASSIGNED")
    void createRequest_Fail_WhenAssetAssigned() {
        // Given
        mockAsset.setStatus(AssetStatus.ASSIGNED);
        assetRepository.save(mockAsset);

        BorrowRequest requestDto = new BorrowRequest();
        requestDto.setAssetId(mockAsset.getId());
        requestDto.setBorrowDate(LocalDate.now().plusDays(1));
        requestDto.setReturnDate(LocalDate.now().plusDays(3));

        // When & Then
        assertThrows(AppException.class, () -> {
            requestService.createRequest(requestDto);
        });
    }
}