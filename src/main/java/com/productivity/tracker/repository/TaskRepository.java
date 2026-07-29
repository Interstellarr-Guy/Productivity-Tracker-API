package com.productivity.tracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.productivity.tracker.entity.ProductivityWorkspace;
import com.productivity.tracker.entity.Task;
import com.productivity.tracker.entity.User;


public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByWorkspace(ProductivityWorkspace workspace);
    
	List<Task> findByWorkspaceUser(User user);
	
	List<Task> findByWorkspaceUserAndCompletedDate(
	        User user,
	        LocalDate completedDate
	);
}