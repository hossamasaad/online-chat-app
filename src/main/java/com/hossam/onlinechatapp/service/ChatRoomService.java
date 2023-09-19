package com.hossam.onlinechatapp.service;

import com.hossam.onlinechatapp.model.ChatRoom;
import com.hossam.onlinechatapp.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }


    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findAll();
    }


    public ChatRoom getChatRoom(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new DataIntegrityViolationException("Chat room NOT found")
        );
    }

    public ChatRoom addChatRoom(ChatRoom chatRoom) {
        chatRoom.setCreatedAt(new Date(System.currentTimeMillis()));
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom updateChatRoom(ChatRoom chatRoom, String adminId) {
        boolean isAdmin = false;
        for (String id : chatRoom.getAdminList())
            if (id.equals(adminId)) {
                isAdmin = true;
                break;
            }

        if (!isAdmin)
            throw new DataIntegrityViolationException("Wrong Admin ID");

        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom deleteChatRoom(String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new DataIntegrityViolationException("Chat room not found")
        );

        chatRoomRepository.delete(chatRoom);
        return chatRoom;
    }

}
