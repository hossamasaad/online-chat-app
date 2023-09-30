package com.hossam.onlinechatapp.service;


import com.hossam.onlinechatapp.model.ChatRoom;
import com.hossam.onlinechatapp.model.Message;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.ChatRoomRepository;
import com.hossam.onlinechatapp.repository.MessageRepository;
import com.hossam.onlinechatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomService(UserRepository userRepository, MessageRepository messageRepository, ChatRoomRepository chatRoomRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }


    public List<ChatRoom> getChatRoom(String email) {
        User user = userRepository.findUserByEmail(email);
        List<ChatRoom> chatRooms = chatRoomRepository.findAllById(user.getChatRoomsList());

        for (ChatRoom chatRoom: chatRooms){
            User friend;
            if (chatRoom.getUsers().get(0).equals(user.getId()))
                friend = userRepository.findById(chatRoom.getUsers().get(1)).get();
            else
                friend = userRepository.findById(chatRoom.getUsers().get(0)).get();

            chatRoom.setTitle(friend.getFirstName() + " " + friend.getLastName());
        }

        return chatRooms;
    }

    public List<Message> getMessageForChatroom(String chatroomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId).orElse(null);

        if (chatRoom == null)
            return null;

        return messageRepository.findAllById(chatRoom.getMessages());
    }
}
