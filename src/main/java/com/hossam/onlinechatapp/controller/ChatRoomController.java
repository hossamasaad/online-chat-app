package com.hossam.onlinechatapp.controller;


import com.hossam.onlinechatapp.model.ChatRoom;
import com.hossam.onlinechatapp.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping
    public List<ChatRoom> getChatRooms() {
        return chatRoomService.getChatRooms();
    }

    @GetMapping("/{chatRoomId}")
    public ChatRoom getChatRoom(@PathVariable String chatRoomId) {
        return chatRoomService.getChatRoom(chatRoomId);
    }

    @PostMapping
    public ChatRoom addChatRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.addChatRoom(chatRoom);
    }

    @PutMapping("/{adminId}")
    public ChatRoom updateChatRoom(
            @PathVariable String adminId,
            @RequestBody ChatRoom chatRoom
    ) {
        return chatRoomService.updateChatRoom(chatRoom, adminId);
    }

    @DeleteMapping("/{chatRoomId}")
    public ChatRoom deleteChatRoom(@PathVariable String chatRoomId) {
        return chatRoomService.deleteChatRoom(chatRoomId);
    }
}
