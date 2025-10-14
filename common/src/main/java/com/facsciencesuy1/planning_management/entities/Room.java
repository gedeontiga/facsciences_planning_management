package com.facsciencesuy1.planning_management.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.facsciencesuy1.planning_management.dtos.RoomDTO;
import com.facsciencesuy1.planning_management.entities.types.RoomType;

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

    // private String facultyId;

    public RoomDTO toDTO() {
        return RoomDTO.fromRoom(this);
    }
}
