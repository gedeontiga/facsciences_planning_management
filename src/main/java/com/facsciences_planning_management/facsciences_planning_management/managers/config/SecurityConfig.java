package com.facsciences_planning_management.facsciences_planning_management.managers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.facsciences_planning_management.facsciences_planning_management.managers.services.JwtFilter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

        private final JwtFilter jwtFilter;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http.csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(
                                                authorize -> authorize
                                                                .requestMatchers("/api/auth/**").permitAll()
                                                                .requestMatchers("/api/admin/**")
                                                                .hasRole("ADMINISTRATOR")
                                                                .anyRequest().authenticated())
                                .sessionManagement(
                                                httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        @Bean
        AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }
}
