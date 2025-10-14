package com.facsciencesuy1.planning_management.entities.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomType {
    AMPHITHEATER("Amphi"), CLASSROOM("Room"), LABORATORY("Labo"), CONFERENCE_ROOM("Conference Room");

    private final String label;
}
