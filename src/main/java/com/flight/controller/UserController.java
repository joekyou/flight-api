package com.flight.controller;

import com.flight.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
}
