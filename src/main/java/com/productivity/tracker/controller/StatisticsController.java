package com.productivity.tracker.controller;

import com.productivity.tracker.dto.StatisticsResponse;
import com.productivity.tracker.service.StatisticsService;
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

}