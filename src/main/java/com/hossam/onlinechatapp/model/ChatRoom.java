package com.hossam.onlinechatapp.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ChatRoom {

    @Id
    private String id;
    private String title;
    private List<String> users = new ArrayList<>();
    private List<String> messages = new ArrayList<>();

    public void addUser(String userId) {
        users.add(userId);
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}
