package com.hossam.onlinechatapp.controller;

import com.hossam.onlinechatapp.security.AuthorizeFilter;
import com.hossam.onlinechatapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AuthorizeFilter authorizeFilter;

    @Autowired
    public UserController(UserService userService, AuthorizeFilter authorizeFilter) {
        this.userService = userService;
        this.authorizeFilter = authorizeFilter;
    }

    @PostMapping("/add-friend/{friendEmail}")
    public void addFriend(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String friendEmail
    ){
        String userEmail = authorizeFilter.authorizerRequestAndGetSubject(request, response);
        userService.addFriend(userEmail, friendEmail);
    }

}
