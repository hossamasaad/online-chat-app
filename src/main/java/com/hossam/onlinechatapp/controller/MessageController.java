package com.hossam.onlinechatapp.controller;


import com.hossam.onlinechatapp.model.Message;
import com.hossam.onlinechatapp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/chat-rooms")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @GetMapping("/{chatRoomId}/messages")
    public List<Message> getMessages(@PathVariable String chatRoomId) {
        return messageService.getMessages(chatRoomId);
    }


    @GetMapping("/{chatRoomId}/messages/{messageId}")
    public Message getMessage(
            @PathVariable String chatRoomId,
            @PathVariable String messageId
    ) {
        return messageService.getMessage(chatRoomId, messageId);
    }

    @PostMapping("/{chatRoomId}/messages")
    public Message addMessage(
            @PathVariable String chatRoomId,
            @RequestBody Message message
    ) {
        return messageService.addMessage(chatRoomId, message);
    }

    @DeleteMapping("/{chatRoomId}/messages/{messageId}")
    public Message deleteMessage(
            @PathVariable String chatRoomId,
            @PathVariable String messageId
    ) {
        return messageService.deleteMessage(chatRoomId, messageId);
    }
}
