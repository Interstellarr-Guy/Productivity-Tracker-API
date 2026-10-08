package com.productivity.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.productivity.tracker.repository.ProductivityWorkspaceRepository;
import com.productivity.tracker.repository.TaskCompletionRepository;
import com.productivity.tracker.repository.TaskRepository;
import com.productivity.tracker.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.productivity.tracker.dto.TaskCompletionRequest;
import com.productivity.tracker.dto.TaskCompletionResponse;
import com.productivity.tracker.dto.TaskRequest;
import com.productivity.tracker.dto.TaskResponse;
import com.productivity.tracker.dto.TaskStatusRequest;
import com.productivity.tracker.entity.ProductivityWorkspace;
import com.productivity.tracker.entity.RepeatType;
import com.productivity.tracker.entity.Task;
import com.productivity.tracker.entity.TaskCompletion;
import com.productivity.tracker.entity.TaskStatus;
import com.productivity.tracker.entity.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.productivity.tracker.exception.WorkspaceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
	
	private static final Logger log =
	        LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProductivityWorkspaceRepository productivityWorkspaceRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private  TaskCompletionRepository taskCompletionRepository;
    
    
    
    public TaskResponse createTask(
            Long workspaceId,
            TaskRequest request,
            UserDetails userDetails) {

        long totalStart = System.currentTimeMillis();


        // 1. GET USER


        long userStart = System.currentTimeMillis();

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        log.info(
                "PERFORMANCE CREATE TASK - Get user took {} ms",
                System.currentTimeMillis() - userStart
        );


   
        // 2. GET + AUTHORIZE WORKSPACE

        long workspaceStart = System.currentTimeMillis();

        ProductivityWorkspace workspace =
                productivityWorkspaceRepository
                        .findByIdAndUser_Id(
                                workspaceId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new WorkspaceNotFoundException(
                                        "Workspace not found or access denied"
                                ));

        log.info(
                "PERFORMANCE CREATE TASK - Get authorized workspace took {} ms",
                System.currentTimeMillis() - workspaceStart
        );


        // 3. BUILD TASK OBJECT

        long buildStart = System.currentTimeMillis();

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setWorkspace(workspace);
        task.setRepeatType(request.getRepeatType());

        log.info(
                "PERFORMANCE CREATE TASK - Build entity took {} ms",
                System.currentTimeMillis() - buildStart
        );

        
        // 4. SAVE TASK
      
        long saveStart = System.currentTimeMillis();

        Task savedTask = taskRepository.save(task);

        log.info(
                "PERFORMANCE CREATE TASK - DB save took {} ms",
                System.currentTimeMillis() - saveStart
        );

        // 5. DTO MAPPING


        long mappingStart = System.currentTimeMillis();

        TaskResponse response = convertToResponse(savedTask);

        log.info(
                "PERFORMANCE CREATE TASK - DTO mapping took {} ms",
                System.currentTimeMillis() - mappingStart
        );


        // TOTAL
        

        log.info(
                "PERFORMANCE CREATE TASK - TOTAL took {} ms",
                System.currentTimeMillis() - totalStart
        );

        return response;
    }
    
//    public TaskResponse createTask(Long workspaceId, 
//            TaskRequest request,
//            UserDetails userDetails) {
//
//       User user = userRepository.findByEmail(userDetails.getUsername())
//      .orElseThrow(() -> new RuntimeException("User not found"));
//
//       ProductivityWorkspace workspace =
//    		   productivityWorkspaceRepository.findById(workspaceId)
//      .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
//
//       if (!workspace.getUser().getId().equals(user.getId())) {
//       throw new RuntimeException("You are not allowed to add tasks to this workspace");
//      }
//
//      Task task = new Task();
//
//      task.setTitle(request.getTitle());
//      task.setDescription(request.getDescription());
//      task.setStatus(request.getStatus());
//      task.setPriority(request.getPriority());
//      task.setDueDate(request.getDueDate());
//      task.setCreatedAt(LocalDateTime.now());
//      task.setWorkspace(workspace);
//      task.setRepeatType(request.getRepeatType());
//      
//
//      Task savedTask = taskRepository.save(task);  //edited
// 
//      return convertToResponse(savedTask);  //edited
//     }
    
    
                         
                    //Get Task
    
//    public List<TaskResponse> getTasks(Long workspaceId,
//            UserDetails userDetails) {
//
//     User user = userRepository.findByEmail(userDetails.getUsername())
//     .orElseThrow(() -> new RuntimeException("User not found"));
//     //debug
//     System.out.println("Logged User ID = " + user.getId());
//
//     ProductivityWorkspace workspace =
//    		 productivityWorkspaceRepository.findById(workspaceId)
//    .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
//    //debug
//    System.out.println("Workspace ID = " + workspace.getId());
//    System.out.println("Workspace Owner = " + workspace.getUser().getId());
//    if (!workspace.getUser().getId().equals(user.getId())) {
//    throw new RuntimeException("You are not allowed to view these tasks");
//    }
//    
//    //for now no need
//   // resetDailyTasks(workspace);
//    
//    // for Debug
//    List<Task> tasks = taskRepository.findByWorkspace(workspace);
//
//    System.out.println("===== TASKS FOUND =====");
//    System.out.println("Count = " + tasks.size());
//
//    for (Task t : tasks) {
//        System.out.println(
//            "Task " + t.getId()
//            + " | " + t.getTitle()
//            + " | Workspace = " + t.getWorkspace().getId()
//        );
//    }
//    
//    return tasks.stream()
//            .map(this::convertToResponse)
//            .toList();
//   }
    
    public List<TaskResponse> getTasks(
            Long workspaceId,
            UserDetails userDetails) {

        long totalStart = System.currentTimeMillis();


        // 1. GET CURRENT USER


        long userStart = System.currentTimeMillis();

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        log.info(
                "PERFORMANCE TASKS (Siva) - Get user took {} ms",
                System.currentTimeMillis() - userStart
        );


        // 2. GET WORKSPACE
       

        long workspaceStart = System.currentTimeMillis();

        ProductivityWorkspace workspace =
                productivityWorkspaceRepository
                        .findByIdAndUser_Id(
                        workspaceId,
                        user.getId()
                )
                        .orElseThrow(() ->
                                new WorkspaceNotFoundException(
                                        "Workspace not found"
                                ));

        log.info(
                "PERFORMANCE TASKS - Get workspace took {} ms",
                System.currentTimeMillis() - workspaceStart
        );


        // 3. AUTHORIZATION CHECK
    

        long authStart = System.currentTimeMillis();

        if (!workspace.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to view these tasks"
            );
        }

        log.info(
                "PERFORMANCE TASKS - Authorization check took {} ms",
                System.currentTimeMillis() - authStart
        );


        // 4. GET TASKS
    

        long dbStart = System.currentTimeMillis();

        List<Task> tasks =
                taskRepository.findByWorkspace(workspace);

        log.info(
                "PERFORMANCE TASKS - DB findByWorkspace took {} ms - {} tasks returned",
                System.currentTimeMillis() - dbStart,
                tasks.size()
        );


        // 5. DTO MAPPING
      

        long mappingStart = System.currentTimeMillis();

        List<TaskResponse> result =
                tasks.stream()
                        .map(this::convertToResponse)
                        .toList();

        log.info(
                "PERFORMANCE TASKS - DTO mapping took {} ms",
                System.currentTimeMillis() - mappingStart
        );


        // TOTAL
      

        log.info(
                "PERFORMANCE TASKS - TOTAL getTasks() took {} ms",
                System.currentTimeMillis() - totalStart
        );

        return result;
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
    
    //Record completion
    @Transactional
    public void recordCompletion(
            Long taskId,
            TaskCompletionRequest request,
            UserDetails userDetails
    ) {

        long totalStart = System.currentTimeMillis();


        // 1. GET TASK
      

        long taskStart = System.currentTimeMillis();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new RuntimeException("Task not found"));

        log.info(
                "PERFORMANCE COMPLETE TASK - Get task took {} ms",
                System.currentTimeMillis() - taskStart
        );


   
        // 2. GET USER

        long userStart = System.currentTimeMillis();

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        log.info(
                "PERFORMANCE COMPLETE TASK - Get user took {} ms",
                System.currentTimeMillis() - userStart
        );


        // 3. AUTHORIZATION


        long authStart = System.currentTimeMillis();

        if (!task.getWorkspace()
                 .getUser()
                 .getId()
                 .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to update this task"
            );
        }

        log.info(
                "PERFORMANCE COMPLETE TASK - Authorization took {} ms",
                System.currentTimeMillis() - authStart
        );


     
        // 4. BUILD COMPLETION
   
        long buildStart = System.currentTimeMillis();

        TaskCompletion completion = new TaskCompletion();

        completion.setTask(task);
        completion.setWorkedMinutes(request.getWorkedMinutes());
        completion.setCompletedDate(request.getCompletedDate());

        log.info(
                "PERFORMANCE COMPLETE TASK - Build completion took {} ms",
                System.currentTimeMillis() - buildStart
        );


  
        // 5. SAVE COMPLETION
    
        long completionSaveStart = System.currentTimeMillis();

        taskCompletionRepository.save(completion);

        log.info(
                "PERFORMANCE COMPLETE TASK - Save completion took {} ms",
                System.currentTimeMillis() - completionSaveStart
        );

        // 6. UPDATE TASK

        long updateStart = System.currentTimeMillis();

        task.setWorkedMinutes(
                task.getWorkedMinutes()
                        + request.getWorkedMinutes()
        );

        if (task.getRepeatType() == RepeatType.NONE) {

            task.setCompletedDate(
                    request.getCompletedDate()
            );

            task.setStatus(TaskStatus.DONE);
        }

        log.info(
                "PERFORMANCE COMPLETE TASK - Update entity took {} ms",
                System.currentTimeMillis() - updateStart
        );

        // 7. SAVE TASK


        long taskSaveStart = System.currentTimeMillis();

      //  taskRepository.save(task);

//        log.info(
//                "PERFORMANCE COMPLETE TASK - Save task took {} ms",
//                System.currentTimeMillis() - taskSaveStart
//        );


        // TOTAL
       

        log.info(
                "PERFORMANCE COMPLETE TASK - TOTAL took {} ms",
                System.currentTimeMillis() - totalStart
        );
    }
    
    // Get completed Data
    public List<TaskCompletionResponse> getCompletionsForDate(
            LocalDate date,
            UserDetails userDetails
    ) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<TaskCompletion> completions =
                taskCompletionRepository.findByCompletedDate(date);

//        return completions.stream()
//                .filter(c ->
//                        c.getTask()
//                         .getWorkspace()
//                         .getUser()
//                         .getId()
//                         .equals(user.getId()))
//                .toList();
        
        return completions.stream()
                .filter(c ->
                        c.getTask()
                         .getWorkspace()
                         .getUser()
                         .getId()
                         .equals(user.getId()))
                .map(c -> {

                    TaskCompletionResponse dto =
                            new TaskCompletionResponse();

                    dto.setTaskId(c.getTask().getId());
                    dto.setTaskTitle(c.getTask().getTitle());
                    dto.setWorkedMinutes(c.getWorkedMinutes());
                    dto.setCompletedDate(c.getCompletedDate());

                    return dto;

                })
                .toList();
        
    }
    
 // Get All Completions
    public List<TaskCompletionResponse> getAllCompletions(
            UserDetails userDetails
    ) {

        long serviceStart = System.currentTimeMillis();


        // GET CURRENT USER
    

        long userStart = System.currentTimeMillis();

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        log.info(
                "PERFORMANCE COMPLETIONS - Get user took {} ms",
                System.currentTimeMillis() - userStart
        );



        // GET ONLY THIS USER'S COMPLETIONS
   

        long dbStart = System.currentTimeMillis();

        List<TaskCompletion> completions =
                taskCompletionRepository
                        .findByTask_Workspace_User_Id(
                                user.getId()
                        );

        log.info(
                "PERFORMANCE COMPLETIONS - DB query took {} ms - {} records returned",
                System.currentTimeMillis() - dbStart,
                completions.size()
        );

        // CONVERT TO DTO
      

        long mappingStart = System.currentTimeMillis();

        List<TaskCompletionResponse> result =
                completions.stream()
                        .map(c -> {

                            TaskCompletionResponse dto =
                                    new TaskCompletionResponse();

                            dto.setTaskId(
                                    c.getTask().getId()
                            );

                            dto.setTaskTitle(
                                    c.getTask().getTitle()
                            );

                            dto.setWorkedMinutes(
                                    c.getWorkedMinutes()
                            );

                            dto.setCompletedDate(
                                    c.getCompletedDate()
                            );

                            return dto;

                        })
                        .toList();


        log.info(
                "PERFORMANCE COMPLETIONS - DTO mapping took {} ms",
                System.currentTimeMillis() - mappingStart
        );


        // TOTAL
    

        log.info(
                "PERFORMANCE COMPLETIONS - TOTAL getAllCompletions() took {} ms",
                System.currentTimeMillis() - serviceStart
        );


        return result;
    }


}