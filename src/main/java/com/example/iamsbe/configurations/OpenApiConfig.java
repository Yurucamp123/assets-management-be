package com.example.iamsbe.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI iamsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IAMS API Documentation")
                        .description("Hệ thống quản lý tài sản nội bộ - Dự án Wrap-up")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Le Thanh Binh")
                                .email("lethanhbinh6122003@gmail.com")
                                .url("https://www.binhle.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}