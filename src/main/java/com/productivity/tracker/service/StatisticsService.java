package com.productivity.tracker.service;

import com.productivity.tracker.dto.StatisticsResponse;
import com.productivity.tracker.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import com.productivity.tracker.entity.Task;
import com.productivity.tracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

	public StatisticsResponse getStatistics() {

	    StatisticsResponse response = new StatisticsResponse();

	    LocalDate today = LocalDate.now();

	    List<Task> tasks = taskRepository.findAll();

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
	
    private final TaskRepository taskRepository;

    public StatisticsService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

}