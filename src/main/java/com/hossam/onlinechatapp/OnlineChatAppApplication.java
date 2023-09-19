package com.hossam.onlinechatapp;

import com.hossam.onlinechatapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;



@SpringBootApplication
public class OnlineChatAppApplication {

    public final UserRepository userRepository;

    public OnlineChatAppApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(OnlineChatAppApplication.class, args);
    }

    // Test some parts of the app if it works correctly
    @Bean
    public CommandLineRunner commandLineRunner() {
        return runner -> {
            // userRepository.save(
            //         User.builder()
            //             .firstName("Ahmed")
            //             .lastName("Khaled")
            //             .email("ak@gmail.com")
            //             .password("XD")
            //             .status(User.Status.OFFLINE)
            //             .createdAt(new Date(System.currentTimeMillis()))
            //             .build()
            // );

            System.out.println("Everything is gonna be OK!");
        };
    }
}
