package com.example.iamsbe.utils;

import com.example.iamsbe.annotations.LogActivity;
import com.example.iamsbe.models.entities.AuditLog;
import com.example.iamsbe.models.entities.User;
import com.example.iamsbe.repositories.AuditLogRepository;
import com.example.iamsbe.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLoggingAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @AfterReturning(pointcut = "@annotation(logActivity)", returning = "result")
    public void logAfter(JoinPoint joinPoint, LogActivity logActivity, Object result) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return;

            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) return;

            String actionType = logActivity.value();
            Object[] args = joinPoint.getArgs();
            String details = "";

            switch (actionType) {
                // --- USER SERVICE ---
                case "CHANGE_USER_ROLE":
                    details = String.format("Admin [%s] đã thay đổi Role của User (ID: %s) thành [%s].",
                            username, args[0], args[1]);
                    break;

                case "DELETE_USER":
                    details = String.format("Admin [%s] đã vô hiệu hóa người dùng (ID: %s).", username, args[0]);
                    break;

                case "CHANGE_USER_STATUS":
                    String status = (boolean) args[1] ? "Kích hoạt" : "Khóa";
                    details = String.format("Admin [%s] đã thay đổi trạng thái User (ID: %s) thành [%s].",
                            username, args[0], status);
                    break;

                // --- AUTH SERVICE ---
                case "NEW_USER_REGISTER":
                    // Ở đây args[0] là RegisterRequest
                    details = String.format("Người dùng mới đăng ký tài khoản thành công với username: [%s].", username);
                    break;

                // --- ASSET SERVICE ---
                case "CREATE_ASSET":
                    details = String.format("Admin [%s] đã thêm mới một tài sản vào kho.", username);
                    if (args.length > 0) details += " Dữ liệu: " + args[0].toString();
                    break;

                case "UPDATE_ASSET":
                    details = String.format("Admin [%s] đã cập nhật thông tin tài sản (ID: %s).", username, args[0]);
                    if (args.length > 1) details += " Dữ liệu mới: " + args[1].toString();
                    break;

                case "DELETE_ASSET":
                    details = String.format("Admin [%s] đã xóa tài sản khỏi hệ thống (ID: %s).", username, args[0]);
                    break;
                case "UPDATE_ASSET_STATUS":
                    details = String.format("Admin [%s] đã cập nhật trạng thái tài sản (ID: %s).", username, args[0]);
                    break;

                // --- REQUEST SERVICE ---
                case "CREATE_REQUEST":
                    details = String.format("Người dùng [%s] đã tạo yêu cầu mượn thiết bị.", username);
                    break;
                case "APPROVE_REQUEST":
                    details = String.format("Admin [%s] đã duyệt yêu cầu mượn (ID: %s).", username, args[0]);
                    break;
                case "REJECT_REQUEST":
                    details = String.format("Admin [%s] đã từ chối yêu cầu (ID: %s). Lý do: %s", username, args[0], args[1]);
                    break;
                case "RETURN_ASSET":
                    details = String.format("Xác nhận hoàn trả thiết bị thành công (ID yêu cầu: %s).", args[0]);
                    break;
                case "REQUEST_RETURN_ASSET":
                    details = String.format("Người dùng [%s] yêu cầu trả thiết bị (ID yêu cầu: %s).", username, args[0]);
                    break;
                case "DATA_ADMIN_INIT":
                    details = "Đã tạo tài khoản admin khởi đầu";
                    break;

                default:
                    details = String.format("Thao tác: %s bởi [%s]", actionType, username);
                    break;
            }

            AuditLog auditLog = AuditLog.builder()
                    .userId(user.getId())
                    .action(actionType)
                    .details(details)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            log.error("Audit Log Error: {}", e.getMessage());
        }
    }
}