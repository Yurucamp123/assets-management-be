package com.example.iamsbe.configurations;

import com.example.iamsbe.security.jwt.JwtAuthenticationFilter;
import com.example.iamsbe.security.jwt.JwtUtils;
import com.example.iamsbe.security.services.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomOAuth2UserService oauth2UserService;

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtils jwtUtils) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/auth/**",
                                "/login/oauth2/**",
                                "/oauth2/**",
                                "/favicon.ico", "/*.png", "/*.gif", "/*.svg", "/*.jpg"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
                        .successHandler((request, response, authentication) -> {
                            Object principal = authentication.getPrincipal();
                            String username;

                            if (principal instanceof UserDetails) {
                                username = ((UserDetails) principal).getUsername();
                            } else if (principal instanceof OAuth2User) {
                                // Lấy email làm username nếu dùng Google
                                username = ((OAuth2User) principal).getAttribute("email");
                            } else {
                                username = principal.toString();
                            }
                            // Tạo token dựa trên username (Bình có thể viết thêm hàm này trong JwtUtils)
                            String token = jwtUtils.generateTokenFromUsername(username);
                            String targetUrl = frontendUrl + "/oauth2/callback?token=" + token;
                            response.sendRedirect(targetUrl);
                        })
                )
                .rememberMe(rememberMe -> rememberMe
                        .key(secretKey)
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .rememberMeParameter("remember-me") // Tên field gửi lên từ Frontend
                        .userDetailsService(userDetailsService) // Service để load lại User khi quay lại
                )

                .logout(logout -> logout
                        .deleteCookies("remember-me") // Xóa cookie này khi User bấm Logout
                        .logoutSuccessUrl("/login")
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.setCharacterEncoding("UTF-8");

                            response.getWriter().write("{\"message\": \"Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn!\"}");
                        })
                )
        ;
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}