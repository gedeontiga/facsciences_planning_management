package com.facsciencesuy1.planning_management.dtos;

import com.facsciencesuy1.planning_management.entities.Room;
import com.facsciencesuy1.planning_management.entities.types.RoomType;

public record RoomDTO(String id, String code, RoomType type, Long capacity, String building, Boolean availability) {
	public static RoomDTO fromRoom(Room room) {
		return new RoomDTO(room.getId(), room.getCode(), room.getType(), room.getCapacity(), room.getBuilding(),
				room.getAvailability());
	}
}
