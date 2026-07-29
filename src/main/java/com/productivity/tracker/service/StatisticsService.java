package com.productivity.tracker.service;

import com.productivity.tracker.dto.HeatmapDTO;
import com.productivity.tracker.dto.StatisticsResponse;
import com.productivity.tracker.dto.WeeklyProductivityDTO;
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
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;
import java.util.HashMap;

@Service
public class StatisticsService {
	
	private final UserRepository userRepository;
	
	private final TaskRepository taskRepository;

	public StatisticsService(
	        TaskRepository taskRepository,
	        UserRepository userRepository) {

	    this.taskRepository = taskRepository;
	    this.userRepository = userRepository;
	}

	public StatisticsResponse getStatistics() {

	    StatisticsResponse response = new StatisticsResponse();

	    LocalDate today = LocalDate.now();

	    User user = getCurrentUser();

	    List<Task> tasks =
	            taskRepository.findByWorkspaceUser(user);

	    // Today Minutes
	    int todayMinutes = tasks.stream()

	            .filter(task -> task.getCompletedDate() != null)

	            .filter(task -> task.getCompletedDate().equals(today))

	            .mapToInt(Task::getWorkedMinutes)

	            .sum();

	    response.setTodayMinutes(todayMinutes);
	    
	    //Week Minutes
	    LocalDate startOfWeek =
	            today.with(java.time.DayOfWeek.MONDAY);

	    int weekMinutes = tasks.stream()

	            .filter(task -> task.getCompletedDate() != null)

	            .filter(task ->
	                    !task.getCompletedDate().isBefore(startOfWeek)
	                            && !task.getCompletedDate().isAfter(today)
	            )

	            .mapToInt(Task::getWorkedMinutes)

	            .sum();

	    response.setWeekMinutes(weekMinutes);
	    
	    //Month Minutes
	    LocalDate firstDayOfMonth = today.withDayOfMonth(1);

	    int monthMinutes = tasks.stream()

	            .filter(task -> task.getCompletedDate() != null)

	            .filter(task ->
	                    !task.getCompletedDate().isBefore(firstDayOfMonth)
	                            && !task.getCompletedDate().isAfter(today)
	            )

	            .mapToInt(Task::getWorkedMinutes)

	            .sum();

	    response.setMonthMinutes(monthMinutes);
	    
	    //Completed Tasks
	    long completedTasks = tasks.stream()

	            .filter(task -> task.getStatus() != null)

	            .filter(task -> task.getStatus().name().equals("DONE"))

	            .count();

	    response.setCompletedTasks((int) completedTasks);
	    
	    //Completed Days
	    Set<LocalDate> completedDays = tasks.stream()

	            .filter(task -> task.getCompletedDate() != null)

	            .filter(task -> task.getWorkedMinutes() > 0)

	            .map(Task::getCompletedDate)

	            .collect(Collectors.toSet());
	    
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
    public List<WeeklyProductivityDTO> getWeeklyProductivity() {
    	User user = getCurrentUser();
    	
        LocalDate today = LocalDate.now();

        LocalDate startOfWeek = today.minusDays(6);

        List<WeeklyProductivityDTO> weeklyData = new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            LocalDate currentDay = startOfWeek.plusDays(i);

            int totalMinutes = taskRepository
                    .findByWorkspaceUserAndCompletedDate(user, currentDay)
                    .stream()
                    .filter(task -> task.getStatus() == TaskStatus.DONE)
                    .mapToInt(task ->
                            task.getWorkedMinutes() == null
                                    ? 0
                                    : task.getWorkedMinutes())
                    .sum();

            String dayName = currentDay
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            weeklyData.add(
                    new WeeklyProductivityDTO(dayName, totalMinutes)
            );
        }

        return weeklyData;
    }
    
    //Heat map 
    public List<HeatmapDTO> getHeatmap() {

        User user = getCurrentUser();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(364);

        List<Task> tasks =
                taskRepository.findByWorkspaceUserAndCompletedDateBetween(
                        user,
                        startDate,
                        endDate
                );

        Map<LocalDate, Integer> dailyMinutes = new HashMap<>();

        for (Task task : tasks) {

            if (task.getStatus() != TaskStatus.DONE)
                continue;

            LocalDate date = task.getCompletedDate();

            int minutes =
                    task.getWorkedMinutes() == null
                            ? 0
                            : task.getWorkedMinutes();

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