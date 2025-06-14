package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PermissionType {
    READ_PLANNING,
    UPDATE_PLANNING,
    CREATE_PLANNING,
    DELETE_PLANNING,;

    @Getter
    private String permission;
}
