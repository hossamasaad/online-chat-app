package com.hossam.onlinechatapp.controller;

import com.hossam.onlinechatapp.config.AddingFriendRequest;
import com.hossam.onlinechatapp.model.User;
import com.hossam.onlinechatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) {
        return userService.findByID(userId);
    }


    @PutMapping("/{userId}")
    public User updateUser(
            @PathVariable String userId,
            @RequestBody User newUser
    ) {
        // Retrieve the user by user id
        User oldUser = userService.findByID(userId);
        if (userId.equals(newUser.getId()))
            throw new DataIntegrityViolationException("You can't update the user ID");

        User updatedUser = userService.mergeUsers(oldUser, newUser);
        return userService.save(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId);
    }

    @PostMapping("/friends")
    public String addFriend(
            @RequestBody AddingFriendRequest addingFriendRequest
    ) {
        userService.addFriend(
                addingFriendRequest.getUserId(),
                addingFriendRequest.getFriendId()
        );
        return "Added Successfully!";
    }
}
