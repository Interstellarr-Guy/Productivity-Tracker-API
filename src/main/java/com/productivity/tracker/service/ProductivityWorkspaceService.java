package com.productivity.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.productivity.tracker.entity.ProductivityWorkspace;
import com.productivity.tracker.entity.User;
import com.productivity.tracker.repository.ProductivityWorkspaceRepository;
import com.productivity.tracker.repository.UserRepository;

@Service
public class ProductivityWorkspaceService {

    @Autowired
    private ProductivityWorkspaceRepository workspaceRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns the logged-in user's workspace.
     * If it doesn't exist, create one automatically.
     */
    public ProductivityWorkspace getOrCreateWorkspace(UserDetails userDetails) {

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return workspaceRepository.findByUser(user)
                .orElseGet(() -> {

                    ProductivityWorkspace workspace = new ProductivityWorkspace();

                    workspace.setUser(user);

                    // Empty JSON object
                    workspace.setProductivityData("{}");

                    return workspaceRepository.save(workspace);

                });
    }
    
    public ProductivityWorkspace updateWorkspace(
            UserDetails userDetails,
            String productivityData) {

        ProductivityWorkspace workspace =
                getOrCreateWorkspace(userDetails);

        workspace.setProductivityData(productivityData);

        return workspaceRepository.save(workspace);
    }

}