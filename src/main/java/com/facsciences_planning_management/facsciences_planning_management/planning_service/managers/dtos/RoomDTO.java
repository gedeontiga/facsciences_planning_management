package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.RoomType;

public record RoomDTO(
		String id,
		String code,
		RoomType type,
		Long capacity,
		String building,
		Boolean availability) {
	public static RoomDTO fromRoom(Room room) {
		return new RoomDTO(room.getId(), room.getCode(), room.getType(), room.getCapacity(), room.getBuilding(),
				room.getAvailability());
	}
}
