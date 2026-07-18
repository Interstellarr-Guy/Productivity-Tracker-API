package com.productivity.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.productivity.tracker.dto.ProductivityDataRequest;
import com.productivity.tracker.entity.ProductivityWorkspace;
import com.productivity.tracker.service.ProductivityWorkspaceService;

@RestController
@RequestMapping("/api/productivity-workspace")
public class ProductivityWorkspaceController {

    @Autowired
    private ProductivityWorkspaceService workspaceService;

    @GetMapping
    public ProductivityWorkspace getWorkspace(
            @AuthenticationPrincipal UserDetails userDetails) {

    	//debug
    	 System.out.println("Controller user = " + userDetails);
    	 
        return workspaceService.getOrCreateWorkspace(userDetails);
    }
    
    @PutMapping
    public ProductivityWorkspace updateWorkspace(
            @RequestBody ProductivityDataRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return workspaceService.updateWorkspace(
                userDetails,
                request.getProductivityData());
    }

}