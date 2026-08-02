package com.productivity.tracker.dto;

import java.time.LocalDate;

public class TaskCompletionRequest {

    private Integer workedMinutes;

    private LocalDate completedDate;
    
 // Getters and Setters

	public Integer getWorkedMinutes() {
		return workedMinutes;
	}

	public void setWorkedMinutes(Integer workedMinutes) {
		this.workedMinutes = workedMinutes;
	}

	public LocalDate getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(LocalDate completedDate) {
		this.completedDate = completedDate;
	}

    

}
