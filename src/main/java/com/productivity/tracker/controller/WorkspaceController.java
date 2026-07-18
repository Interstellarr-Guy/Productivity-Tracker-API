package com.productivity.tracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.productivity.tracker.dto.WorkspaceRequest;
import com.productivity.tracker.entity.Workspace;
import com.productivity.tracker.service.WorkspaceService;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @PostMapping
    public Workspace createWorkspace(
            @RequestBody WorkspaceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return workspaceService.createWorkspace(request, userDetails);
    }
    @GetMapping
    public List<Workspace> getWorkspaces(
            @AuthenticationPrincipal UserDetails userDetails) {

        return workspaceService.getUserWorkspaces(userDetails);
    }
    
    @GetMapping("/{id}")
    public Workspace getWorkspaceById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return workspaceService.getWorkspaceById(id, userDetails);
    }
    
    @PutMapping("/{id}")
    public Workspace updateWorkspace(
            @PathVariable Long id,
            @RequestBody WorkspaceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return workspaceService.updateWorkspace(id, request, userDetails);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorkspace(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        workspaceService.deleteWorkspace(id, userDetails);

        return ResponseEntity.ok("Workspace deleted successfully");
    }
}