package com.hossam.onlinechatapp.repository;

import com.hossam.onlinechatapp.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
}
