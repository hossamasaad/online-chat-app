package com.hossam.onlinechatapp.controller;


import com.hossam.onlinechatapp.model.ChatRoom;
import com.hossam.onlinechatapp.model.Message;
import com.hossam.onlinechatapp.security.AuthorizeFilter;
import com.hossam.onlinechatapp.service.ChatRoomService;
import com.hossam.onlinechatapp.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chat-rooms")
@CrossOrigin(origins = "*")
public class ChatRoomController {

    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final AuthorizeFilter authorizeFilter;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @Autowired
    public ChatRoomController(MessageService messageService, ChatRoomService chatRoomService, AuthorizeFilter authorizeFilter, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.chatRoomService = chatRoomService;
        this.authorizeFilter = authorizeFilter;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping
    public List<ChatRoom> getChatRoom(HttpServletRequest request, HttpServletResponse response){
        String email = authorizeFilter.authorizerRequestAndGetSubject(request, response);
        return chatRoomService.getChatRoom(email);
    }

    @GetMapping("/messages/{chatroomId}")
    public List<Message> getMessagesForChatroom(@PathVariable String chatroomId){
        return chatRoomService.getMessageForChatroom(chatroomId);
    }

    @MessageMapping("/chat.sendMessage")
    public Message sendMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getSenderId(), "/private", message);
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), "/private", message);
        return messageService.save(message);
    }

}
