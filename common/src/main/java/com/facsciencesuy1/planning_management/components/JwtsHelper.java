package com.facsciencesuy1.planning_management.components;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtsHelper {
    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getEmailFromToken() {
        return getClaim(getToken(), Claims::getId);
    }

    public String getMetadataFromToken() {
        return getClaim(getToken(), claims -> claims.get("metadata", String.class));
    }

    public String getRoleFromToken() {
        return getClaim(getToken(), Claims::getSubject);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaims(final String token) {
        try {
            return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage());
        }
    }

    private <T> T getClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Remove "Bearer " prefix
        }

        throw new JwtException("No JWT token found in request");
    }
}