package com.hossam.onlinechatapp.service;


import com.hossam.onlinechatapp.config.AuthRequest;
import com.hossam.onlinechatapp.config.AuthResponse;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.repository.UserRepository;
import com.hossam.onlinechatapp.security.JwtEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public User register(User user){

        if (userRepository.findUserByEmail(user.getEmail()) != null)
            throw new RuntimeException("Email is already registered!");

        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return userRepository.save(user);
    }

    public AuthResponse authenticate(AuthRequest request){
        User user = userRepository.findUserByEmail(request.getEmail());

        if (user == null)
            throw new RuntimeException("The email is not found!");

        jwtEncoder.encode(user);
        return AuthResponse.builder().id(user.getId()).accessToken(jwtEncoder.getAccessToken()).build();
    }

}
