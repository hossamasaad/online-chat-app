package com.hossam.onlinechatapp.service;


import com.hossam.onlinechatapp.model.ChatRoom;
import com.hossam.onlinechatapp.model.Message;
import com.hossam.onlinechatapp.repository.ChatRoomRepository;
import com.hossam.onlinechatapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, ChatRoomRepository chatRoomRepository) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public List<Message> getMessages(String chatRoomId) {
        return messageRepository.findAllByChatRoomId(chatRoomId);
    }

    public Message getMessage(String chatRoomId, String messageId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new DataIntegrityViolationException("Chat Room NOT found!")
        );

        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new DataIntegrityViolationException("Message NOT found!")
        );

        // Check if the message belongs to the chat room
        if (!message.getChatRoomId().equals(chatRoomId))
            throw new DataIntegrityViolationException("This message doesn't belong to the chat room");

        return message;
    }

    public Message addMessage(String chatRoomId, Message message) {

        // Get the chat-room by roomId
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new DataIntegrityViolationException("Chat Room NOT found!")
        );


        message.setChatRoomId(chatRoomId);                      // Sure the message have the chatroom id
        message.setSentAt(new Date(System.currentTimeMillis()));

        Message savedMessage = messageRepository.save(message); // Save the message
        chatRoom.addMessage(savedMessage.getId());              // Add the message to the chat-room
        chatRoomRepository.save(chatRoom);

        return savedMessage;
    }

    public Message deleteMessage(String chatRoomId, String messageId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new DataIntegrityViolationException("Chat Room NOT found!")
        );

        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new DataIntegrityViolationException("Message NOT found!")
        );

        // Check if the message belongs to the chat room
        if (!message.getChatRoomId().equals(chatRoomId))
            throw new DataIntegrityViolationException("This message doesn't belong to the chat room");

        // Remove the message from the chat-room
        List<String> messages = chatRoom.getMessageList();
        messages.remove(messageId);
        chatRoom.setMessageList(messages);

        // Delete the message
        messageRepository.deleteById(messageId);

        return message;
    }
}
