package com.hossam.onlinechatapp.controller;


import com.hossam.onlinechatapp.config.AuthRequest;
import com.hossam.onlinechatapp.config.AuthResponse;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public AuthResponse authenticate(@RequestBody AuthRequest request){
        return authenticationService.authenticate(request);
    }

    @PostMapping("/register")
    public User register(@RequestBody User user){
        return authenticationService.register(user);
    }

}
