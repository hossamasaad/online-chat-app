package com.hossam.onlinechatapp;

import com.hossam.onlinechatapp.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class OnlineChatAppApplication {

    public final UserService userService;

    public OnlineChatAppApplication(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(OnlineChatAppApplication.class, args);
    }

    // Test some parts of the app if it works correctly
    @Bean
    public CommandLineRunner commandLineRunner(){
        return runner -> {
            System.out.println("Every thing is going to be ok!");
        };
    }
}
