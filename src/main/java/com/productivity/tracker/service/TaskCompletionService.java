package com.productivity.tracker.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.productivity.tracker.entity.Task;
import com.productivity.tracker.entity.TaskCompletion;
import com.productivity.tracker.repository.TaskCompletionRepository;

@Service
public class TaskCompletionService {

    private final TaskCompletionRepository repository;

    public TaskCompletionService(TaskCompletionRepository repository) {
        this.repository = repository;
    }
    
    public TaskCompletion recordCompletion(
            Task task,
            Integer workedMinutes,
            LocalDate completedDate
    ) {

        TaskCompletion completion = new TaskCompletion();

        completion.setTask(task);
        completion.setWorkedMinutes(workedMinutes);
        completion.setCompletedDate(completedDate);

        return repository.save(completion);
    }

}