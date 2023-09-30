package com.hossam.onlinechatapp.config;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String id;
    private String accessToken;

}