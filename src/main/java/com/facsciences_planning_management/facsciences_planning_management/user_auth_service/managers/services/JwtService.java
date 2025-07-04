package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

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
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.LoginResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String TOKEN_NOT_FOUND = "Token not found";

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

    public LoginResponse generate(final String email) {
        final Users user = userRepository.findByEmailAndEnabledIsTrue(email)
                .orElseThrow(() -> new CustomBusinessException("User not found"));

        invalidateUserTokens(user.getId());

        return this.generateJwt(user);
    }

    public LoginResponse refreshToken(final String token) {

        if (!isValidTokenFormat(token)) {
            throw new JwtException("Invalid token format");
        }

        final String email = getEmailFromToken(token);
        final Users user = userRepository.findByEmailAndEnabledIsTrue(email)
                .orElseThrow(() -> new CustomBusinessException("User not found"));

        Jwt currentJwt = getJwtByToken(token);
        if (currentJwt.getExpiredAt().isBefore(Instant.now())) {
            throw new JwtException("Cannot refresh expired token");
        }

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
                .orElseThrow(() -> new JwtException(TOKEN_NOT_FOUND));
    }

    public boolean isTokenValid(final String token) {

        if (!isValidTokenFormat(token)) {

            throw new JwtException("Invalid token format");
        }

        final Jwt jwtEntity = getJwtByToken(token);

        if (jwtEntity.getExpiredAt().isBefore(Instant.now())) {

            throw new JwtException("Token has expired");
        }

        final Date expiration = getClaim(token, Claims::getExpiration);
        if (expiration == null || expiration.before(new Date())) {
            throw new JwtException("Token has expired");
        }

        String tokenEmail = getEmailFromToken(token);
        return jwtEntity.getUser().getEmail().equals(tokenEmail);
    }

    public boolean isTokenExpired(final String token) {
        try {
            if (!isValidTokenFormat(token)) {
                return true;
            }

            final Date jwtExpiration = getClaim(token, Claims::getExpiration);
            if (jwtExpiration != null && jwtExpiration.before(new Date())) {
                return true;
            }

            final Jwt jwtEntity = getJwtByToken(token);
            return jwtEntity.getExpiredAt().isBefore(Instant.now());

        } catch (Exception e) {
            return true;
        }
    }

    public void validateTokenExpiration(final String token) {
        if (!isValidTokenFormat(token)) {
            throw new JwtException("Invalid token format");
        }

        final Date expiration = getClaim(token, Claims::getExpiration);
        if (expiration != null && expiration.before(new Date())) {
            throw new JwtException("Token has expired");
        }

        final Jwt jwtEntity = getJwtByToken(token);
        if (jwtEntity.getExpiredAt().isBefore(Instant.now())) {
            throw new JwtException("Token has expired");
        }
    }

    public void invalidateToken(final String token) {
        try {
            if (isValidTokenFormat(token)) {
                Jwt jwt = getJwtByToken(token);
                jwtRepository.delete(jwt);
            }
        } catch (Exception e) {

        }
    }

    private void invalidateUserTokens(final String userId) {
        List<Jwt> userTokens = jwtRepository.findByUserId(userId);
        if (!userTokens.isEmpty()) {
            jwtRepository.deleteAll(userTokens);
        }
    }

    @Scheduled(cron = "@daily")
    public void removeExpiredTokens() {
        jwtRepository.deleteAllByExpiredAtIsBefore(Instant.now());
    }

    private boolean isValidTokenFormat(final String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        String[] parts = token.split("\\.");
        return parts.length == 3;
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
            throw new JwtException("Invalid JWT token: " + e.getMessage());
        }
    }

    private LoginResponse generateJwt(final Users user) {
        final Instant now = Instant.now();
        final Instant expirationInstant = now.plusSeconds(tokenValidityHours * 3600);

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

        Jwt jwt = Jwt.builder()
                .token(token)
                .expiredAt(expirationInstant)
                .user(user)
                .createdAt(now)
                .build();

        jwt = jwtRepository.save(jwt);

        return new LoginResponse(jwt.getToken(), user.getRole().getType().name(), expirationInstant.toString(),
                String.valueOf(tokenValidityHours * 3600));
    }
}