package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.RoomType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.RoomDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "rooms")
public class Room {
    @Id
    private String id;
    private String code;
    private RoomType type;
    private Long capacity;
    private String building;
    private Boolean availability;

    public RoomDTO toDTO() {
        return RoomDTO.fromRoom(this);
    }
}
