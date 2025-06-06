package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PermissionType {
    DEPARTMENT_CHIEF_READ_PLANNING,
    DEPARTMENT_CHIEF_CUSTOMIZE_PLANNING,
    ADMIN_CREATE_ALL,
    ADMIN_READ_ALL,
    ADMIN_UPDATE_ALL,
    ADMIN_DELETE_ALL,
    SECRETARY_READ_PLANNING,
    SECRETARY_UPDATE_PLANNING,
    SECRETARY_DELETE_PLANNING,
    TEACHER_READ_PLANNING,
    STUDENT_GET_PLANNING;

    @Getter
    private String permission;
}
