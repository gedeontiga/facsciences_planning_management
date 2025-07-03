package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HeadCountLabel {
    GROUP_1("Group 1"),
    GROUP_2("Group 2"),
    GROUP_3("Group 3"),
    GROUP_4("Group 4"),
    GROUP_5("Group 5");

    private final String label;

    public static HeadCountLabel fromLabel(String label) {
        for (HeadCountLabel headCountLabel : HeadCountLabel.values()) {
            if (headCountLabel.label.equals(label)) {
                return headCountLabel;
            }
        }
        return null;
    }
}
