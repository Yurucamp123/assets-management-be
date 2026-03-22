package com.example.iamsbe.configurations;

import com.example.iamsbe.annotations.LogActivity;
import com.example.iamsbe.models.entities.User;
import com.example.iamsbe.models.enums.Role;
import com.example.iamsbe.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.init.admin.username}")
    private String adminUsername;

    @Value("${app.init.admin.password}")
    private String adminPassword;

    @Override
    @LogActivity("DATA_ADMIN_INIT")
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            logger.info(">>> Hệ thống chưa có Admin. Đang khởi tạo tài khoản mặc định...");
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFullName("Administrator");
            admin.setEmail("admin@aspas-edu.site");
            admin.setRole(Role.ROLE_ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            logger.info(">>> Khởi tạo Admin thành công với Username: {}", adminUsername);
        } else {
            logger.info(">>> Tài khoản Admin đã tồn tại. Bỏ qua bước khởi tạo.");
        }
    }
}