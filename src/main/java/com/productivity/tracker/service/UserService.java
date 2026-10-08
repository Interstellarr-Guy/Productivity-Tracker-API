package com.productivity.tracker.service;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger log =
	        LoggerFactory.getLogger(UserService.class);

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

        long totalStart = System.currentTimeMillis();


        // 1. FIND USER

        long userStart = System.currentTimeMillis();

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        log.info(
                "PERFORMANCE LOGIN - Find user took {} ms",
                System.currentTimeMillis() - userStart
        );


 
        // 2. VERIFY PASSWORD
        
        long passwordStart = System.currentTimeMillis();

        boolean passwordValid = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        log.info(
                "PERFORMANCE LOGIN - Password verification took {} ms",
                System.currentTimeMillis() - passwordStart
        );

        if (!passwordValid) {
            throw new RuntimeException("Invalid email or password");
        }


        // 3. GENERATE JWT


        long jwtStart = System.currentTimeMillis();

        String token = jwtService.generateToken(user.getEmail());

        log.info(
                "PERFORMANCE LOGIN - JWT generation took {} ms",
                System.currentTimeMillis() - jwtStart
        );


        // 4. BUILD RESPONSE
       

        long responseStart = System.currentTimeMillis();

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );

        log.info(
                "PERFORMANCE LOGIN - Build response took {} ms",
                System.currentTimeMillis() - responseStart
        );


        // TOTAL
        

        log.info(
                "PERFORMANCE LOGIN - TOTAL took {} ms",
                System.currentTimeMillis() - totalStart
        );

        return response;
    }
    
//    public LoginResponse login(LoginRequest request) {
//
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
//        
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid email or password");
//        }
//        String token = jwtService.generateToken(user.getEmail());
//        
//        return new LoginResponse(
//        		user.getId(),
//        		user.getName(),
//        		user.getEmail(),
//        		token
//        		);
//        
//    }
     

}