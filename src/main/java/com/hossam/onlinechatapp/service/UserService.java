package com.hossam.onlinechatapp.service;


import com.hossam.onlinechatapp.model.ChatRoom;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.ChatRoomRepository;
import com.hossam.onlinechatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public UserService(UserRepository userRepository, ChatRoomRepository chatRoomRepository) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public User findByID(String userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new DataIntegrityViolationException("User ID not found!")
        );
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void addFriend(String userId, String friendId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new DataIntegrityViolationException("User ID not found!")
        );

        User friend = userRepository.findById(friendId).orElseThrow(
                () -> new DataIntegrityViolationException("Friend ID not found!")
        );

        // if they are already friends -> Just skip
        for (String id : user.getFriendsList())
            if (friendId.equals(id))
                return;

        ChatRoom chatRoom = ChatRoom.builder()
                .Type(ChatRoom.Type.PRIVATE)
                .userList(List.of(user.getId(), friend.getId()))
                .adminList(List.of(user.getId(), friend.getId()))
                .createdAt(new Date(System.currentTimeMillis()))
                .build();

        chatRoomRepository.save(chatRoom);

        user.addChatRoom(chatRoom.getId());
        user.addFriend(friendId);

        friend.addChatRoom(chatRoom.getId());
        friend.addFriend(userId);

        userRepository.save(user);
        userRepository.save(friend);
    }

    public User deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new DataIntegrityViolationException("User not found")
        );
        userRepository.delete(user);
        return user;
    }

    public User mergeUsers(User oldUser, User newUser) {
        if (newUser.getFirstName() != null)
            oldUser.setFirstName(newUser.getFirstName());

        if (newUser.getLastName() != null)
            oldUser.setLastName(newUser.getLastName());

        if (newUser.getEmail() != null)
            oldUser.setEmail(newUser.getEmail());

        if (newUser.getPassword() != null)
            oldUser.setPassword(newUser.getPassword());

        if (newUser.getStatus() != null)
            oldUser.setStatus(newUser.getStatus());

        return oldUser;
    }
}
