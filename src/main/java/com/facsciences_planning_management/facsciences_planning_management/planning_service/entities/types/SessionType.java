package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessionType {
    COURSE("Course"),
    LECTURE("Lecture"),
    TUTORIAL("TD"),
    NORMAL_SESSION("SN"),
    CONTINUOUS_ASSESSMENT("CA");

    private final String label;
}
