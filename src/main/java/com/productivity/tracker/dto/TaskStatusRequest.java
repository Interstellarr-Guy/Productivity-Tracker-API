package com.productivity.tracker.dto;

import com.productivity.tracker.entity.TaskStatus;

public class TaskStatusRequest {

    private TaskStatus status;
    
    private Integer workedMinutes;

	public Integer getWorkedMinutes() {
		return workedMinutes;
	}

	public void setWorkedMinutes(Integer workedMinutes) {
		this.workedMinutes = workedMinutes;
	}

	public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}