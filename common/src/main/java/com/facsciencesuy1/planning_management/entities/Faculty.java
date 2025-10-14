package com.facsciencesuy1.planning_management.entities;

import java.util.HashSet;
import java.util.Set;

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
@Document(collection = "faculties")
public class Faculty {
    @Id
    private String id;
    private String name;
    private String code;

    @Builder.Default
    @DocumentReference(lazy = true, collection = "branches")
    private Set<Branch> branches = new HashSet<>();

    @Builder.Default
    @DocumentReference(lazy = true, collection = "rooms")
    private Set<Room> rooms = new HashSet<>();
}
