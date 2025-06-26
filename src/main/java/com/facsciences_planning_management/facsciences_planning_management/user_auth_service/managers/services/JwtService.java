package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Jwt;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.repositories.JwtRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String TOKEN_NOT_FOUND = "Token not found";
    private static final String BEARER = "Bearer";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.hours:12}")
    private long tokenValidityHours;

    private final UserRepository userRepository;
    private final JwtRepository jwtRepository;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Map<String, String> generate(final String email) {
        final Users user = userRepository.findByEmailAndEnabledIsTrue(email)
                .orElseThrow(() -> new CustomBusinessException("User not found"));

        // Invalidate any existing tokens for this user
        invalidateUserTokens(user.getId());

        return this.generateJwt(user);
    }

    public Map<String, String> refreshToken(final String token) {
        final String email = getEmailFromToken(token);
        final Users user = userRepository.findByEmailAndEnabledIsTrue(email)
                .orElseThrow(() -> new CustomBusinessException("User not found"));

        // Validate current token exists and is not expired
        Jwt currentJwt = getJwtByToken(token);
        if (currentJwt.getExpiredAt().isBefore(Instant.now())) {
            throw new CustomBusinessException("Cannot refresh expired token");
        }

        // Invalidate current token
        jwtRepository.delete(currentJwt);

        return this.generateJwt(user);
    }

    public String getEmailFromToken(final String token) {
        return getClaim(token, Claims::getId);
    }

    public String getRoleFromToken(final String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Jwt getJwtByToken(final String token) {
        return jwtRepository.findByToken(token)
                .orElseThrow(() -> new CustomBusinessException(TOKEN_NOT_FOUND));
    }

    public boolean isTokenValid(final String token) {
        try {
            final Jwt jwtEntity = getJwtByToken(token);

            // Check if token exists in database and is not expired
            if (jwtEntity.getExpiredAt().isBefore(Instant.now())) {
                return false;
            }

            // Validate JWT signature and expiration
            final Date expiration = getClaim(token, Claims::getExpiration);
            return expiration != null && !expiration.before(new Date());

        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(final String token) {
        final Date expiration = getClaim(token, Claims::getExpiration);
        if (expiration != null && expiration.before(new Date())) {
            throw new CustomBusinessException("Token has expired");
        }
        return false;
    }

    public void invalidateToken(final String token) {
        try {
            Jwt jwt = getJwtByToken(token);
            jwtRepository.delete(jwt);
            log.info("Token invalidated for user: {}", getEmailFromToken(token));
        } catch (Exception e) {
            log.warn("Failed to invalidate token: {}", e.getMessage());
        }
    }

    private void invalidateUserTokens(final String userId) {
        List<Jwt> userTokens = jwtRepository.findByUserId(userId);
        if (!userTokens.isEmpty()) {
            jwtRepository.deleteAll(userTokens);
            log.info("Invalidated {} existing tokens for user: {}", userTokens.size());
        }
    }

    @Scheduled(cron = "@daily")
    public void removeExpiredTokens() {
        log.info("Starting cleanup of expired tokens at {}", Instant.now());
        jwtRepository.deleteAllByExpiredAtIsBefore(Instant.now());
        log.info("Removed {} expired tokens");
    }

    private <T> T getClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new CustomBusinessException("Invalid JWT token: " + e.getMessage());
        }
    }

    private Map<String, String> generateJwt(final Users user) {
        final Instant now = Instant.now();
        final Instant expirationInstant = now.plusSeconds(tokenValidityHours * 3600);

        // Convert to Date for JWT claims (JWT library expects Date objects)
        final Date issuedAt = Date.from(now);
        final Date expirationDate = Date.from(expirationInstant);

        final String token = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claims()
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .id(user.getEmail())
                .subject(user.getRole().getType().toString())
                .add("userId", user.getId())
                .and()
                .signWith(getSigningKey())
                .compact();

        // Save token to database
        Jwt jwt = Jwt.builder()
                .token(token)
                .expiredAt(expirationInstant)
                .user(user)
                .createdAt(now)
                .build();

        jwt = jwtRepository.save(jwt);

        log.info("Generated new JWT token for user: {}, expires at: {}",
                user.getEmail(), expirationInstant);

        return Map.of(
                BEARER, jwt.getToken(),
                "expiresAt", expirationInstant.toString(),
                "expiresIn", String.valueOf(tokenValidityHours * 3600));
    }
}