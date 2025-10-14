package com.facsciencesuy1.planning_management.entities.types;

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
