package com.productivity.tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.productivity.tracker.entity.User;
import com.productivity.tracker.entity.Workspace;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {


    List<Workspace> findByUser(User user);
    
    Optional<Workspace> findByIdAndUser(Long id, User user);
    
}