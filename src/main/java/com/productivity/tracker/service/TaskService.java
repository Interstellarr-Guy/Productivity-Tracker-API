package com.productivity.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.productivity.tracker.repository.ProductivityWorkspaceRepository;
import com.productivity.tracker.repository.TaskRepository;
import com.productivity.tracker.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.productivity.tracker.dto.TaskRequest;
import com.productivity.tracker.dto.TaskResponse;
import com.productivity.tracker.dto.TaskStatusRequest;
import com.productivity.tracker.entity.ProductivityWorkspace;
import com.productivity.tracker.entity.Task;
import com.productivity.tracker.entity.TaskStatus;
import com.productivity.tracker.entity.User;

import com.productivity.tracker.exception.WorkspaceNotFoundException;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProductivityWorkspaceRepository productivityWorkspaceRepository;

    @Autowired
    private UserRepository userRepository;
    
    public TaskResponse createTask(Long workspaceId,  //edited
            TaskRequest request,
            UserDetails userDetails) {

       User user = userRepository.findByEmail(userDetails.getUsername())
      .orElseThrow(() -> new RuntimeException("User not found"));

       ProductivityWorkspace workspace =
    		   productivityWorkspaceRepository.findById(workspaceId)
      .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

       if (!workspace.getUser().getId().equals(user.getId())) {
       throw new RuntimeException("You are not allowed to add tasks to this workspace");
      }

      Task task = new Task();

      task.setTitle(request.getTitle());
      task.setDescription(request.getDescription());
      task.setStatus(request.getStatus());
      task.setPriority(request.getPriority());
      task.setDueDate(request.getDueDate());
      task.setCreatedAt(LocalDateTime.now());
      task.setWorkspace(workspace);
      
      

      // for debug
//      Task saved = taskRepository.save(task);
//
//      System.out.println("===== TASK SAVED =====");
//      System.out.println("Task ID = " + saved.getId());
//      System.out.println("Workspace ID = " + saved.getWorkspace().getId());
//      System.out.println("Title = " + saved.getTitle());
//
//      return saved;
      
      System.out.println("===== CREATE TASK =====");

      System.out.println(request.getTitle());

      Task savedTask = taskRepository.save(task);  //edited
 
      System.out.println("Saved Task ID = " + savedTask.getId());

      return convertToResponse(savedTask);  //edited
     }
                         
                    //Get Task
    
    public List<TaskResponse> getTasks(Long workspaceId,
            UserDetails userDetails) {

     User user = userRepository.findByEmail(userDetails.getUsername())
     .orElseThrow(() -> new RuntimeException("User not found"));
     //debug
     System.out.println("Logged User ID = " + user.getId());

     ProductivityWorkspace workspace =
    		 productivityWorkspaceRepository.findById(workspaceId)
    .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
    //debug
    System.out.println("Workspace ID = " + workspace.getId());
    System.out.println("Workspace Owner = " + workspace.getUser().getId());
    if (!workspace.getUser().getId().equals(user.getId())) {
    throw new RuntimeException("You are not allowed to view these tasks");
    }
    
    // for Debug
    List<Task> tasks = taskRepository.findByWorkspace(workspace);

    System.out.println("===== TASKS FOUND =====");
    System.out.println("Count = " + tasks.size());

    for (Task t : tasks) {
        System.out.println(
            "Task " + t.getId()
            + " | " + t.getTitle()
            + " | Workspace = " + t.getWorkspace().getId()
        );
    }
    
    return tasks.stream()
            .map(this::convertToResponse)
            .toList();
   }
    
    public Task getTask(Long taskId,
            UserDetails userDetails) {

    	User user = userRepository.findByEmail(userDetails.getUsername())
    .orElseThrow(() -> new RuntimeException("User not found"));

    Task task = taskRepository.findById(taskId)
    .orElseThrow(() -> new RuntimeException("Task not found"));

    if (!task.getWorkspace().getUser().getId().equals(user.getId())) {
    throw new RuntimeException("You are not allowed to view this task");
    }

    return task;
   }
    
    public Task updateTask(Long taskId,
            TaskRequest request,
            UserDetails userDetails) {
    	
    	Task task = taskRepository.findById(taskId)
    	        .orElseThrow(() ->
    	                new RuntimeException("Task not found"));
    	
    	
    	User user = userRepository.findByEmail(userDetails.getUsername())
    	        .orElseThrow(() ->
    	                new RuntimeException("User not found"));
    	
    	if (!task.getWorkspace().getUser().getId().equals(user.getId())) {
    	    throw new RuntimeException("You are not allowed to update this task");
    	}
    	
    	task.setTitle(request.getTitle());
    	task.setDescription(request.getDescription());
    	task.setStatus(request.getStatus());
    	
    	if (request.getStatus() == TaskStatus.DONE) {
    	    task.setCompletedDate(LocalDate.now());
    	} else {
    	    task.setCompletedDate(null);
    	}
    	
    	task.setPriority(request.getPriority());
    	task.setDueDate(request.getDueDate());
    	
    	task.setWorkedMinutes(request.getWorkedMinutes());
    	task.setCompletedDate(request.getCompletedDate());

    	return taskRepository.save(task);
    
    
}
    
    //Task Status
    public TaskResponse updateTaskStatus(Long taskId,
            TaskStatusRequest request,
            UserDetails userDetails) {

      User user = userRepository.findByEmail(userDetails.getUsername())
      .orElseThrow(() -> new RuntimeException("User not found"));

      Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new RuntimeException("Task not found"));
 
      if (!task.getWorkspace().getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Not your task");
     }

      task.setStatus(request.getStatus());
      
      if ("DONE".equals(request.getStatus())) {
    	    task.setCompletedDate(LocalDate.now());
    	} else {
    	    task.setCompletedDate(null);     //to clear completed date 
    	    task.setWorkedMinutes(0);         // to clear entered hour
    	}

      Task updatedTask = taskRepository.save(task);
      
      

      return convertToResponse(updatedTask);
}
    
    public void deleteTask(Long taskId, UserDetails userDetails) {

    	Task task = taskRepository.findById(taskId)
    	        .orElseThrow(() ->
    	                new RuntimeException("Task not found"));
    	
    	User user = userRepository.findByEmail(userDetails.getUsername())
    	        .orElseThrow(() ->
    	                new RuntimeException("User not found"));
    	
    	if (!task.getWorkspace().getUser().getId().equals(user.getId())) {
    	    throw new RuntimeException("You are not allowed to delete this task");
    	}
    	
    	taskRepository.delete(task);
    }
    
    private TaskResponse convertToResponse(Task task) {

        TaskResponse response = new TaskResponse();

        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        
        response.setWorkedMinutes(task.getWorkedMinutes());
        response.setCompletedDate(task.getCompletedDate());

        return response;
    }
    

}