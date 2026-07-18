package com.productivity.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.productivity.tracker.entity.ProductivityWorkspace;
import com.productivity.tracker.entity.User;

public interface ProductivityWorkspaceRepository
        extends JpaRepository<ProductivityWorkspace, Long> {

    Optional<ProductivityWorkspace> findByUser(User user);

}