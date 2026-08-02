package com.productivity.tracker.dto;

import java.time.LocalDate;

public class TaskCompletionResponse {

	    private Long taskId;
	    private String taskTitle;
	    private Integer workedMinutes;
	    private LocalDate completedDate;
	    
	    public  TaskCompletionResponse() {
	    	
	    }
	    
		public Long getTaskId() {
			return taskId;
		}
		public void setTaskId(Long taskId) {
			this.taskId = taskId;
		}
		public String getTaskTitle() {
			return taskTitle;
		}
		public void setTaskTitle(String taskTitle) {
			this.taskTitle = taskTitle;
		}
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
