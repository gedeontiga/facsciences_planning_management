package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "validations")
public class Validation {
    @Id
    private String id;

    @DocumentReference(collection = "users")
    private Users user;

    private String activationToken;

    private Instant expired;
}
