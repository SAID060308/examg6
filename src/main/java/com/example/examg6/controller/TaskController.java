package com.example.examg6.controller;

import com.example.examg6.entity.Status;
import com.example.examg6.entity.Task;
import com.example.examg6.repo.StatusRepository;
import com.example.examg6.repo.TaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class TaskController {
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;

    public TaskController(TaskRepository taskRepository, StatusRepository statusRepository) {
        this.taskRepository = taskRepository;
        this.statusRepository = statusRepository;
    }

    @PostMapping("/task/changeStatusMinus/{taskId}")
    public String changeTaskStatus(@PathVariable Integer taskId) {
        Optional<Task> optionalTask = taskRepository.findById(Long.valueOf(taskId));
        if (optionalTask.isEmpty()) return "redirect:/error";

        Task task = optionalTask.get();
        Status currentStatus = task.getStatus();


        int newPosition = currentStatus.getIsActivePositionNumber() - 1;


        Status newStatus = statusRepository.findByIsActivePositionNumber(newPosition);
        if (newStatus != null) {
            task.setStatus(newStatus);
            taskRepository.save(task);
        }

        return "redirect:/";
    }

    @PostMapping("/task/changeStatusPlus/{taskId}")
    public String changeTaskStatus(@PathVariable Integer taskId,
                                   @RequestParam String direction) {
        Optional<Task> optionalTask = taskRepository.findById(Long.valueOf(taskId));
        if (optionalTask.isEmpty()) return "redirect:/error";

        Task task = optionalTask.get();
        Status currentStatus = task.getStatus();
        int current = currentStatus.getIsActivePositionNumber();

        int newPosition = direction.equals("prev") ? current - 1 : current + 1;

        Status newStatus = statusRepository.findByIsActivePositionNumber(newPosition);
        if (newStatus != null) {
            task.setStatus(newStatus);
            taskRepository.save(task);
        }

        return "redirect:/";
    }


}
