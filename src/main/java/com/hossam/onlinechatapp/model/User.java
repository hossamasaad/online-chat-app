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
public class User {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Status status;
    private Date createdAt;

    private List<String> chatRoomsList = new ArrayList<>();
    private List<String> friendsList = new ArrayList<>();

    public void addChatRoom(String groupId) {
        this.chatRoomsList.add(groupId);
    }

    public void addFriend(String friendId) {
        this.friendsList.add(friendId);
    }

    public enum Status {
        OFFLINE,
        ONLINE
    }
}
