package com.facsciencesuy1.planning_management.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Document(collection = "academic_years")
public class AcademicYear {
    @Id
    private String id;
    @NonNull
    @Indexed(unique = true)
    private String label;
}
