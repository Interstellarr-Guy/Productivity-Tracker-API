package com.productivity.tracker.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.productivity.tracker.entity.User;
import com.productivity.tracker.repository.UserRepository;
import com.productivity.tracker.security.JwtService;
import com.productivity.tracker.dto.LoginRequest;
import com.productivity.tracker.dto.LoginResponse;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;

    public User register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    
    
    
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        //debug
        System.out.println(request.getPassword());
        System.out.println(user.getPassword());
        System.out.println(passwordEncoder.matches(request.getPassword(), user.getPassword()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getEmail());
        
        return new LoginResponse(
        		user.getId(),
        		user.getName(),
        		user.getEmail(),
        		token
        		);
        
    }
     

}