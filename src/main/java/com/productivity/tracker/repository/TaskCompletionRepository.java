package com.productivity.tracker.repository;

import java.time.LocalDate;
import java.util.List;
import com.productivity.tracker.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import com.productivity.tracker.entity.TaskCompletion;

public interface TaskCompletionRepository
        extends JpaRepository<TaskCompletion, Long> {

    List<TaskCompletion> findByCompletedDate(LocalDate completedDate);
  
    
    
    boolean existsByTaskAndCompletedDate(
            Task task,
            LocalDate completedDate
    );
    
    TaskCompletion findByTaskAndCompletedDate(
            Task task,
            LocalDate completedDate
    );
    
    List<TaskCompletion> findByTask_Workspace_User_Id(
            Long userId
    );
    
    //Optimized query for weekly statistics
    
    List<TaskCompletion> findByTask_Workspace_User_IdAndCompletedDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
