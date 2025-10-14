package com.facsciencesuy1.planning_management.entities;

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

    @DocumentReference(lazy = true, collection = "users")
    private Users user;

    private String activationToken;

    private Instant expired;
}
