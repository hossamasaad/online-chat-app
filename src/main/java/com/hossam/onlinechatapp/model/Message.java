package com.hossam.onlinechatapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Message {

    @Id
    private String id;
    private String senderId;
    private String receiverId;
    private String chatroomId;
    private String message;
    private Date sentAt;

}
