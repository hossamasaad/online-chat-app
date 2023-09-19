package com.hossam.onlinechatapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ChatRoom {

    @Id
    private String id;
    private Type Type;
    private Date createdAt;
    private List<String> userList;
    private List<String> adminList;
    private List<String> messageList;

    public void addUser(String userId) {
        if (this.userList == null)
            this.userList = new ArrayList<>();

        this.userList.add(userId);
    }

    public void addAdmin(String userId) {
        if (this.adminList == null)
            this.adminList = new ArrayList<>();

        this.adminList.add(userId);
    }

    public void addMessage(String messageId) {
        if (this.messageList == null)
            this.messageList = new ArrayList<>();

        this.messageList.add(messageId);
    }

    public enum Type {
        GROUP, PRIVATE
    }
}
