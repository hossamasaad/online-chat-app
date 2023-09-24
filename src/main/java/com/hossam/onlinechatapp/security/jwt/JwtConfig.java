package com.hossam.onlinechatapp.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtEncoder jwtEncoder(){
        return new JwtEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        return new JwtDecoder();
    }

}
