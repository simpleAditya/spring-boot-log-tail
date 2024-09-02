package com.websocket.logger.controller;

import com.websocket.logger.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class FileWatcherController {

    @MessageMapping("/logs")
    @SendTo("/topic/log")
    public Message getFileUpdates(Message message) {
        return message;
    }
}
