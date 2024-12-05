package com.be.WebsocketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendMessageToFrontend(String message) {
        template.convertAndSend("/topic/topic1", message);
    }
}
