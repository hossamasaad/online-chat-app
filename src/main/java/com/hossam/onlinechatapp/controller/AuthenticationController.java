package com.hossam.onlinechatapp.controller;


import com.hossam.onlinechatapp.config.AuthRequest;
import com.hossam.onlinechatapp.config.AuthResponse;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authService;

    @Autowired
    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public User register (@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/authenticate")
    public AuthResponse authenticate(@RequestBody AuthRequest request){
        return authService.authenticate(request);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

}
