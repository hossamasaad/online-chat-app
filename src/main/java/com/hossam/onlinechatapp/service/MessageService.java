package com.hossam.onlinechatapp.service;


import com.hossam.onlinechatapp.model.ChatRoom;
import com.hossam.onlinechatapp.model.Message;
import com.hossam.onlinechatapp.repository.ChatRoomRepository;
import com.hossam.onlinechatapp.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public MessageService(MessageRepository messageRepository, ChatRoomRepository chatRoomRepository) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public Message save(Message message) {
        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatroomId()).orElse(null);

        if (chatRoom == null)
            return null;

        message = messageRepository.save(message);

        chatRoom.addMessage(message.getId());
        chatRoomRepository.save(chatRoom);

        return message;
    }

}
