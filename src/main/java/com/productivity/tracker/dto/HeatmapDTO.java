package com.productivity.tracker.dto;

import java.time.LocalDate;

public class HeatmapDTO {

    private LocalDate date;

    private int minutes;

    public HeatmapDTO() {
    }

    public HeatmapDTO(LocalDate date, int minutes) {
        this.date = date;
        this.minutes = minutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

}