package com.productivity.tracker.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.productivity.tracker.dto.TaskCompletionRequest;
import com.productivity.tracker.dto.TaskCompletionResponse;
import com.productivity.tracker.dto.TaskRequest;
import com.productivity.tracker.entity.Task;
import com.productivity.tracker.entity.TaskCompletion;
import com.productivity.tracker.service.TaskService;
import com.productivity.tracker.dto.TaskResponse;
import com.productivity.tracker.dto.TaskStatusRequest;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/workspaces/{workspaceId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long workspaceId,
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        TaskResponse task = taskService.createTask(workspaceId, request, userDetails);

        return ResponseEntity.ok(task);
    }
    
    //Task Status
    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody TaskStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        TaskResponse response =
                taskService.updateTaskStatus(taskId, request, userDetails);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/workspaces/{workspaceId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(
            @PathVariable Long workspaceId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<TaskResponse> tasks = taskService.getTasks(workspaceId, userDetails);

        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Task> getTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Task task = taskService.getTask(taskId, userDetails);

        return ResponseEntity.ok(task);
    }
    
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Task task = taskService.updateTask(taskId, request, userDetails);
        
        return ResponseEntity.ok(task);
    }
    
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails userDetails) {

        taskService.deleteTask(taskId, userDetails);

        return ResponseEntity.ok("Task deleted successfully");
    }
    
    //Complete Task 2-August
    
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<?> completeTask(
            @PathVariable Long taskId,
            @RequestBody TaskCompletionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        taskService.recordCompletion(
                taskId,
                request,
                userDetails
                
        ) ;

        return ResponseEntity.ok().build();
    }
    
    //temporary controller
    @GetMapping("/completions")
    public ResponseEntity<List<TaskCompletionResponse>> getCompletions(
            @RequestParam String date,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        return ResponseEntity.ok(
                taskService.getCompletionsForDate(
                        LocalDate.parse(date),
                        userDetails
                )
        );
    }
    
    // completion + all
    @GetMapping("/completions/all")
    public ResponseEntity<List<TaskCompletionResponse>> getAllCompletions(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        return ResponseEntity.ok(
                taskService.getAllCompletions(userDetails)
        );

    }
   
} 