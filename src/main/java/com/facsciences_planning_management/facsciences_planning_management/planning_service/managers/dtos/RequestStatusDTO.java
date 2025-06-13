package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestStatusDTO {
    private RequestStatus status;
    private String rejectionReason;
}