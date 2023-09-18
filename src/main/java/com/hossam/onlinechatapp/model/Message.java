package com.hossam.onlinechatapp.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Message {

    @Id
    private String id;
    private String userId;
    private String chatRoomId;
    private String message;
    private Date sentAt;

}
