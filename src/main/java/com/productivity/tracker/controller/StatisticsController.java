package com.productivity.tracker.controller;

import com.productivity.tracker.dto.StatisticsResponse;
import com.productivity.tracker.dto.WeeklyProductivityDTO;
import com.productivity.tracker.entity.User;
import com.productivity.tracker.service.StatisticsService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    } 
    
    @GetMapping
    public StatisticsResponse getStatistics() {

        return statisticsService.getStatistics();

    }
    
    @GetMapping("/weekly")
    public List<WeeklyProductivityDTO> getWeeklyProductivity() {

        return statisticsService.getWeeklyProductivity();

    }

}