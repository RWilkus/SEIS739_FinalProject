package com.skycontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SkyControl Pro - Main Spring Boot Application
 * 
 * This is the entry point for the Drone Fleet Management System backend.
 * 
 * Features:
 * - RESTful API for drone fleet management
 * - PostgreSQL database integration
 * - Swagger/OpenAPI documentation
 * - JWT authentication
 * - CORS configuration for frontend
 * 
 * @author SkyControl Team
 * @version 1.0.0
 */
@SpringBootApplication
public class SkyControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyControlApplication.class, args);
        System.out.println("=================================================");
        System.out.println("SkyControl Pro Backend Started Successfully!");
        System.out.println("=================================================");
        System.out.println("API Documentation: http://localhost:8080/swagger-ui.html");
        System.out.println("API Endpoints: http://localhost:8080/api");
        System.out.println("=================================================");
    }

    /**
     * CORS Configuration
     * Allows the React frontend to communicate with the backend
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000", "http://localhost:3001")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
