package com.facsciences_planning_management.facsciences_planning_management.models;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    @Indexed(unique = true)
    private String email;

    private String activationCode;

    private Instant expired;
}
