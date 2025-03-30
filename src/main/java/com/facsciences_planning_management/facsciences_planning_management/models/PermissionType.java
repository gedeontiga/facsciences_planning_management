package com.facsciences_planning_management.facsciences_planning_management.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PermissionType {
    DEPARTMENT_CHIEF_READ_PLANNING,
    DEPARTMENT_CHIEF_CUSTOMIZE_PLANNING,
    DEAN_CREATE_ALL,
    DEAN_READ_ALL,
    DEAN_UPDATE_ALL,
    DEAN_DELETE_ALL,
    VICE_DEAN_CREATE_PLANNING,
    VICE_DEAN_READ_PLANNING,
    VICE_DEAN_UPDATE_PLANNING,
    VICE_DEAN_DELETE_PLANNING,
    TEACHER_READ_PLANNING,
    STUDENT_GET_PLANNING;

    @Getter
    private String permission;
}
