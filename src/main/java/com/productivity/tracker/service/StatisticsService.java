package com.productivity.tracker.service;

import com.productivity.tracker.dto.HeatmapDTO;
import com.productivity.tracker.dto.StatisticsResponse;
import com.productivity.tracker.dto.WeeklyProductivityDTO;
import com.productivity.tracker.repository.TaskCompletionRepository;
import com.productivity.tracker.repository.TaskRepository;
import com.productivity.tracker.repository.UserRepository;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.productivity.tracker.entity.Task;
import com.productivity.tracker.entity.TaskStatus;
import com.productivity.tracker.entity.User;
import com.productivity.tracker.repository.TaskRepository;
import com.productivity.tracker.entity.TaskCompletion;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StatisticsService {
	
	private static final Logger log =
	        LoggerFactory.getLogger(StatisticsService.class);
	
	private final UserRepository userRepository;
	private final TaskRepository taskRepository;
	private final TaskCompletionRepository taskCompletionRepository;

	public StatisticsService(
	        TaskRepository taskRepository,
	        TaskCompletionRepository taskCompletionRepository,
	        UserRepository userRepository) {

	    this.taskRepository = taskRepository;
	    this.taskCompletionRepository = taskCompletionRepository;
	    this.userRepository = userRepository;
	}

	public StatisticsResponse getStatistics() {

	    StatisticsResponse response = new StatisticsResponse();

	    LocalDate today = LocalDate.now();

	    User user = getCurrentUser();

//	    List<Task> tasks =
//	            taskRepository.findByWorkspaceUser(user);
	    List<TaskCompletion> completions =
	            taskCompletionRepository.findAll()
	                    .stream()
	                    .filter(c ->
	                            c.getTask()
	                             .getWorkspace()
	                             .getUser()
	                             .getId()
	                             .equals(user.getId()))
	                    .toList();

	    // Today Minutes
	    int todayMinutes = completions.stream()

	            .filter(c -> c.getCompletedDate().equals(today))

	            .mapToInt(TaskCompletion::getWorkedMinutes)

	            .sum();

	    response.setTodayMinutes(todayMinutes);
	    
	    //Week Minutes
	    LocalDate startOfWeek =
	            today.with(java.time.DayOfWeek.MONDAY);

	    int weekMinutes = completions.stream()

	            .filter(c ->
	                    !c.getCompletedDate().isBefore(startOfWeek)
	                            && !c.getCompletedDate().isAfter(today)
	            )

	            .mapToInt(TaskCompletion::getWorkedMinutes)

	            .sum();

	    response.setWeekMinutes(weekMinutes);
	    
	    //Month Minutes
	    LocalDate firstDayOfMonth = today.withDayOfMonth(1);

	    int monthMinutes = completions.stream()

	            .filter(c ->
	                    !c.getCompletedDate().isBefore(firstDayOfMonth)
	                            && !c.getCompletedDate().isAfter(today)
	            )

	            .mapToInt(TaskCompletion::getWorkedMinutes)

	            .sum();

	    response.setMonthMinutes(monthMinutes);
	    

	 // Completed Tasks
	    int completedTasks = completions.size();

	    response.setCompletedTasks(completedTasks);
	    
//	    long completedTasks = tasks.stream()
//
//	            .filter(task -> task.getStatus() != null)
//
//	            .filter(task -> task.getStatus().name().equals("DONE"))
//
//	            .count();
//
//	    response.setCompletedTasks((int) completedTasks);
//	    
//	    //Completed Days
	    Set<LocalDate> completedDays = completions.stream()

	            .map(TaskCompletion::getCompletedDate)

	            .collect(Collectors.toSet());
	    
//	    Set<LocalDate> completedDays = tasks.stream()
//
//	            .filter(task -> task.getCompletedDate() != null)
//
//	            .filter(task -> task.getWorkedMinutes() > 0)
//
//	            .map(Task::getCompletedDate)
//
//	            .collect(Collectors.toSet());
	    
	    //consecutive days
	    int streak = 0;

	    LocalDate current = LocalDate.now();

	    while (completedDays.contains(current)) {

	        streak++;

	        current = current.minusDays(1);

	    }

	    response.setCurrentStreak(streak);
	    
	    
	    return response;
	}
	
     //Helper method
	private User getCurrentUser() {

	    Authentication authentication =
	            SecurityContextHolder.getContext().getAuthentication();

	    return userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() -> new RuntimeException("User not found"));
	}
    
    
    
    //Weekly productivity
//    public List<WeeklyProductivityDTO> getWeeklyProductivity() {
//    	User user = getCurrentUser();
//    	
//        LocalDate today = LocalDate.now();
//
//        LocalDate startOfWeek = today.minusDays(6);
//
//        List<WeeklyProductivityDTO> weeklyData = new ArrayList<>();
//
//        for (int i = 0; i < 7; i++) {
//
//            LocalDate currentDay = startOfWeek.plusDays(i);
//
//            int totalMinutes = taskCompletionRepository.findAll()
//                    .stream()
//                    .filter(c ->
//                            c.getTask()
//                             .getWorkspace()
//                             .getUser()
//                             .getId()
//                             .equals(user.getId()))
//                    .filter(c -> c.getCompletedDate().equals(currentDay))
//                    .mapToInt(TaskCompletion::getWorkedMinutes)
//                    .sum();
//
//            String dayName = currentDay
//                    .getDayOfWeek()
//                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
//
//            weeklyData.add(
//                    new WeeklyProductivityDTO(dayName, totalMinutes)
//            );
//        }
//
//        return weeklyData;
//    }
	
	// Weekly productivity
	public List<WeeklyProductivityDTO> getWeeklyProductivity() {

	    long serviceStart = System.currentTimeMillis();

	    // Get current user
	    long userStart = System.currentTimeMillis();

	    User user = getCurrentUser();

	    log.info(
	            "PERFORMANCE - Get current user took {} ms",
	            System.currentTimeMillis() - userStart
	    );


	    LocalDate today = LocalDate.now();
	    LocalDate startOfWeek = today.minusDays(6);

	    // DATABASE - CALL ONLY ONCE
	  

	    long dbStart = System.currentTimeMillis();

//	    List<TaskCompletion> allCompletions =
//	            taskCompletionRepository.findAll();

	    List<TaskCompletion> completions =
	            taskCompletionRepository
	                    .findByTask_Workspace_User_IdAndCompletedDateBetween(
	                            user.getId(),
	                            startOfWeek,
	                            today
	                    );
	    
	    log.info(
	            "PERFORMANCE -Optimized weekly DB query took {} ms - {} records returned",
	            System.currentTimeMillis() - dbStart,
	            completions.size()
	    );

	    // FILTER CURRENT USER ONCE
	 
//
//	    long filterStart = System.currentTimeMillis();
//
//	    List<TaskCompletion> userCompletions =
//	            allCompletions.stream()
//	                    .filter(c ->
//	                            c.getTask()
//	                             .getWorkspace()
//	                             .getUser()
//	                             .getId()
//	                             .equals(user.getId()))
//	                    .toList();
//
//	    log.info(
//	            "PERFORMANCE - User filtering took {} ms - {} records",
//	            System.currentTimeMillis() - filterStart,
//	            userCompletions.size()
//	    );

	    // Build weekly response
	   

	    long calculationStart =
	            System.currentTimeMillis();

	    List<WeeklyProductivityDTO> weeklyData =
	            new ArrayList<>();


	    for (int i = 0; i < 7; i++) {

	        LocalDate currentDay =
	                startOfWeek.plusDays(i);


	        int totalMinutes =
	                completions.stream()

	                        .filter(c ->
	                                c.getCompletedDate()
	                                        .equals(currentDay))

	                        .mapToInt(
	                                TaskCompletion::getWorkedMinutes
	                        )

	                        .sum();


	        String dayName =
	                currentDay
	                        .getDayOfWeek()
	                        .getDisplayName(
	                                TextStyle.SHORT,
	                                Locale.ENGLISH
	                        );


	        weeklyData.add(
	                new WeeklyProductivityDTO(
	                        dayName,
	                        totalMinutes
	                )
	        );
	    }


	    log.info(
	            "PERFORMANCE - Weekly calculation took {} ms",
	            System.currentTimeMillis() - calculationStart
	    );


	    // Total
	    
	    log.info(
	            "PERFORMANCE - TOTAL optimized getWeeklyProductivity() took {} ms",
	            System.currentTimeMillis() - serviceStart
	    );


	    return weeklyData;
	}
    
    //Heat map 
    public List<HeatmapDTO> getHeatmap() {

        User user = getCurrentUser();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(364);

        List<TaskCompletion> completions =
                taskCompletionRepository.findAll()
                        .stream()
                        .filter(c ->
                                c.getTask()
                                 .getWorkspace()
                                 .getUser()
                                 .getId()
                                 .equals(user.getId()))
                        .filter(c ->
                                !c.getCompletedDate().isBefore(startDate)
                                        && !c.getCompletedDate().isAfter(endDate))
                        .toList();

        Map<LocalDate, Integer> dailyMinutes = new HashMap<>();

        for (TaskCompletion completion : completions) {

            LocalDate date = completion.getCompletedDate();

            int minutes = completion.getWorkedMinutes();

            dailyMinutes.put(
                    date,
                    dailyMinutes.getOrDefault(date, 0) + minutes
            );
        }

        List<HeatmapDTO> result = new ArrayList<>();

        for (Map.Entry<LocalDate, Integer> entry : dailyMinutes.entrySet()) {

            result.add(
                    new HeatmapDTO(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return result;
    }
    
    
    
    

}