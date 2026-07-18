package com.productivity.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.productivity.tracker.entity.ProductivityWorkspace;
import com.productivity.tracker.entity.Task;


public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByWorkspace(ProductivityWorkspace workspace);

}