package com.productivity.tracker.dto;

public class WeeklyProductivityDTO {

    private String day;

    private int minutes;

    public WeeklyProductivityDTO() {
    }

    public WeeklyProductivityDTO(String day, int minutes) {
        this.day = day;
        this.minutes = minutes;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}