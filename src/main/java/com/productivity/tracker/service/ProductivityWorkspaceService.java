package com.productivity.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.productivity.tracker.entity.ProductivityWorkspace;
import com.productivity.tracker.entity.User;
import com.productivity.tracker.repository.ProductivityWorkspaceRepository;
import com.productivity.tracker.repository.UserRepository;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductivityWorkspaceService {

	private static final Logger log =
	        LoggerFactory.getLogger(ProductivityWorkspaceService.class);
	
    @Autowired
    private ProductivityWorkspaceRepository workspaceRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns the logged-in user's workspace.
     * If it doesn't exist, create one automatically.
     */
    
    public ProductivityWorkspace getOrCreateWorkspace(
            UserDetails userDetails) {

        long totalStart = System.currentTimeMillis();

        String email = userDetails.getUsername();

        // 1. FIND WORKSPACE DIRECTLY
        long workspaceStart = System.currentTimeMillis();

        Optional<ProductivityWorkspace> existingWorkspace =
                workspaceRepository.findByUser_Email(email);

        log.info(
                "PERFORMANCE WORKSPACE - Direct workspace query took {} ms",
                System.currentTimeMillis() - workspaceStart
        );

        // 2. RETURN IF FOUND
        if (existingWorkspace.isPresent()) {

            log.info(
                    "PERFORMANCE WORKSPACE - TOTAL took {} ms",
                    System.currentTimeMillis() - totalStart
            );

            return existingWorkspace.get();
        }

        // 3. GET USER ONLY IF WORKSPACE IS MISSING
        long userStart = System.currentTimeMillis();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        log.info(
                "PERFORMANCE WORKSPACE - Get user for creation took {} ms",
                System.currentTimeMillis() - userStart
        );

        // 4. CREATE WORKSPACE
        ProductivityWorkspace workspace =
                new ProductivityWorkspace();

        workspace.setUser(user);
        workspace.setProductivityData("{}");

        long saveStart = System.currentTimeMillis();

        ProductivityWorkspace savedWorkspace =
                workspaceRepository.save(workspace);

        log.info(
                "PERFORMANCE WORKSPACE - Create workspace took {} ms",
                System.currentTimeMillis() - saveStart
        );

        log.info(
                "PERFORMANCE WORKSPACE - TOTAL took {} ms",
                System.currentTimeMillis() - totalStart
        );

        return savedWorkspace;
    }
    
//    public ProductivityWorkspace getOrCreateWorkspace(
//            UserDetails userDetails) {
//
//        long totalStart = System.currentTimeMillis();
//
//        // 1. GET USER
//      
//
//        long userStart = System.currentTimeMillis();
//
//        User user = userRepository
//                .findByEmail(userDetails.getUsername())
//                .orElseThrow(() ->
//                        new RuntimeException("User not found"));
//
//        log.info(
//                "PERFORMANCE WORKSPACE - Get user took {} ms",
//                System.currentTimeMillis() - userStart
//        );
//
//
//        // 2. FIND EXISTING WORKSPACE
//      
//
//        long workspaceStart = System.currentTimeMillis();
//
//        Optional<ProductivityWorkspace> existingWorkspace =
//                workspaceRepository.findByUser(user);
//
//        log.info(
//                "PERFORMANCE WORKSPACE - Find workspace took {} ms",
//                System.currentTimeMillis() - workspaceStart
//        );
//
//
//        // 3. RETURN EXISTING WORKSPACE
// 
//
//        if (existingWorkspace.isPresent()) {
//
//            log.info(
//                    "PERFORMANCE WORKSPACE - TOTAL took {} ms",
//                    System.currentTimeMillis() - totalStart
//            );
//
//            return existingWorkspace.get();
//        }
//
//
//        // 4. CREATE WORKSPACE IF MISSING
//       
//
//        ProductivityWorkspace workspace =
//                new ProductivityWorkspace();
//
//        workspace.setUser(user);
//        workspace.setProductivityData("{}");
//
//        long saveStart = System.currentTimeMillis();
//
//        ProductivityWorkspace savedWorkspace =
//                workspaceRepository.save(workspace);
//
//        log.info(
//                "PERFORMANCE WORKSPACE - Create workspace took {} ms",
//                System.currentTimeMillis() - saveStart
//        );
//
//        log.info(
//                "PERFORMANCE WORKSPACE - TOTAL took {} ms",
//                System.currentTimeMillis() - totalStart
//        );
//
//        return savedWorkspace;
//    }
//    public ProductivityWorkspace getOrCreateWorkspace(UserDetails userDetails) {
//
//        User user = userRepository
//                .findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return workspaceRepository.findByUser(user)
//                .orElseGet(() -> {
//
//                    ProductivityWorkspace workspace = new ProductivityWorkspace();
//
//                    workspace.setUser(user);
//
//                    // Empty JSON object
//                    workspace.setProductivityData("{}");
//
//                    return workspaceRepository.save(workspace);
//
//                });
//    }
    
    public ProductivityWorkspace updateWorkspace(
            UserDetails userDetails,
            String productivityData) {

        ProductivityWorkspace workspace =
                getOrCreateWorkspace(userDetails);

        workspace.setProductivityData(productivityData);

        return workspaceRepository.save(workspace);
    }

}