package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketUpdateService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendUpdate(String path, Object object) {

        messagingTemplate.convertAndSend(path, object);
    }
}
