package com.hossam.onlinechatapp.service;

import com.hossam.onlinechatapp.model.ChatRoom;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.ChatRoomRepository;
import com.hossam.onlinechatapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public UserService(UserRepository userRepository, ChatRoomRepository chatRoomRepository) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public void addFriend(String userEmail, String friendEmail) {
        User user = userRepository.findUserByEmail(userEmail);
        User friend = userRepository.findUserByEmail(friendEmail);

        user.addFriend(friend.getId());
        friend.addFriend(user.getId());

        ChatRoom chatRoom = ChatRoom.builder()
                .users(new ArrayList<>(List.of(user.getId(), friend.getId())))
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);

        user.addChatRoom(chatRoom.getId());
        friend.addChatRoom(chatRoom.getId());

        userRepository.save(user);
        userRepository.save(friend);
    }
}
