// package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.configs;

// import java.util.Arrays;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.reactive.CorsConfigurationSource;
// import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class CorsConfig implements WebMvcConfigurer {
    
//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
        
//         // Specify exact origins instead of "*"
//         configuration.setAllowedOrigins(Arrays.asList(
//             "https://facsciences-planning-management.netlify.app",
//             "http://localhost:3000",
//             "http://localhost:5173",
//             "http://localhost:4200"
//         ));
        
//         configuration.setAllowedMethods(Arrays.asList(
//             "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
//         ));
        
//         configuration.setAllowedHeaders(Arrays.asList("*"));
//         configuration.setAllowCredentials(true);
//         configuration.setMaxAge(3600L);
        
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);
//         return source;
//     }
// }
