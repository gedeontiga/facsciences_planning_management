package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.types.RoomType;

public record RoomDTO(
		String id,
		String name,
		String code,
		RoomType type,
		Long capacity,
		Boolean availability) {
	public static RoomDTO fromRoom(Room room) {
		return new RoomDTO(room.getId(), room.getName(), room.getCode(), room.getType(), room.getCapacity(),
				room.getAvailability());
	}
}
