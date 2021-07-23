package com.example.controller;

import com.example.domain.User;
import com.example.exception.UserNotFoundException;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/london")
    public User[] getUserInAndAroundLondon(){
        User[] usersInOrAroundLondon = userService.getUsersInOrAroundLondon();

        if (usersInOrAroundLondon == null || usersInOrAroundLondon.length == 0) {
            throw new UserNotFoundException();
        }
        return usersInOrAroundLondon;
    }
}
