package com.productivity.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.productivity.tracker.dto.LoginRequest;
import com.productivity.tracker.dto.LoginResponse;
import com.productivity.tracker.dto.RegisterResponse;
import com.productivity.tracker.entity.User;
import com.productivity.tracker.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody User user) {
    	
    	//debug
    	System.out.println("REGISTER API HIT");

        User savedUser = userService.register(user);

        RegisterResponse response = new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }
}