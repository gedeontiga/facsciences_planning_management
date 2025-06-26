package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "jwts")
public class Jwt {

    @Id
    private String id;

    private String token;
    private Instant expiredAt;
    private Instant createdAt;

    @DocumentReference(lazy = true, collection = "users")
    private Users user;
}
