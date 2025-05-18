package com.facsciences_planning_management.facsciences_planning_management.planning_service.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.RoomDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.types.RoomType;

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
    private String name;
    private String code;
    private RoomType type;
    private Long capacity;
    private Boolean availability;

    public RoomDTO toDTO() {
        return RoomDTO.fromRoom(this);
    }
}
