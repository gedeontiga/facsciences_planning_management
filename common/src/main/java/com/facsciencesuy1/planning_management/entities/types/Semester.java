package com.facsciencesuy1.planning_management.entities.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Semester {
    SEMESTER_1("Semester 1"), SEMESTER_2("Semester 2"), SEMESTER_3("Semester 3"), SEMESTER_4("Semester 4");

    private final String label;
}