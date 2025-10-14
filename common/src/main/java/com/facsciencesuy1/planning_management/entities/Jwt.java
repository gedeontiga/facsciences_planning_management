package com.facsciencesuy1.planning_management.entities;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
