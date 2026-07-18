package com.productivity.tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.productivity.tracker.dto.WorkspaceRequest;
import com.productivity.tracker.entity.User;
import com.productivity.tracker.entity.Workspace;
import com.productivity.tracker.exception.WorkspaceNotFoundException;
import com.productivity.tracker.repository.UserRepository;
import com.productivity.tracker.repository.WorkspaceRepository;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private UserRepository userRepository;
    
    

    public Workspace createWorkspace(WorkspaceRequest request,
                                     UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Workspace workspace = new Workspace();

        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setCreatedAt(LocalDateTime.now());
        workspace.setUser(user);

        return workspaceRepository.save(workspace);
    }
    
    public List<Workspace> getUserWorkspaces(UserDetails userDetails) {

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return workspaceRepository.findByUser(user);
    }
    
    public Workspace getWorkspaceById(Long id, UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return workspaceRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
    }
    
    public Workspace updateWorkspace(Long id,
            WorkspaceRequest request,
            UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
       .orElseThrow(() -> new RuntimeException("User not found"));

       Workspace workspace = workspaceRepository
       .findByIdAndUser(id, user)
       .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

       workspace.setName(request.getName());
       workspace.setDescription(request.getDescription());

       return workspaceRepository.save(workspace);
    }
    
    public void deleteWorkspace(Long id, UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        if (!workspace.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this workspace");
        }

        workspaceRepository.delete(workspace);
    }

}