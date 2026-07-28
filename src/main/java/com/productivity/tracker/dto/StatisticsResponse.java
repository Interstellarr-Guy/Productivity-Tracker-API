package com.productivity.tracker.dto;

public class StatisticsResponse {

	private int todayMinutes;

	private int weekMinutes;

	private int monthMinutes;

	private long completedTasks;
	
	public StatisticsResponse() {
		
	}

	public int getTodayMinutes() {
		return todayMinutes;
	}

	public void setTodayMinutes(int todayMinutes) {
		this.todayMinutes = todayMinutes;
	}

	public int getWeekMinutes() {
		return weekMinutes;
	}

	public void setWeekMinutes(int weekMinutes) {
		this.weekMinutes = weekMinutes;
	}

	public int getMonthMinutes() {
		return monthMinutes;
	}

	public void setMonthMinutes(int monthMinutes) {
		this.monthMinutes = monthMinutes;
	}

	public long getCompletedTasks() {
		return completedTasks;
	}

	public void setCompletedTasks(long completedTasks) {
		this.completedTasks = completedTasks;
	}

	public int getCurrentStreak() {
		return currentStreak;
	}

	public void setCurrentStreak(int currentStreak) {
		this.currentStreak = currentStreak;
	}

	private int currentStreak;
}
