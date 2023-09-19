package com.hossam.onlinechatapp.repository;


import com.hossam.onlinechatapp.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findAllByChatRoomId(String chatRoomId);

}
