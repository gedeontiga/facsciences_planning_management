package com.facsciencesuy1.planning_management.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.facsciencesuy1.planning_management.dtos.RoleDTO;
import com.facsciencesuy1.planning_management.entities.types.RoleType;

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
