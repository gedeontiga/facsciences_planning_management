package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.RoleDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Document(collection = "roles")
public class Role {

    @Id
    private String id;

    @NonNull
    private RoleType type;

    public RoleDTO toDTO() {
        return RoleDTO.fromRole(this);
    }
}
