package com.example.examg6.controller;

import com.example.examg6.dto.TaskDto;
import com.example.examg6.entity.Status;
import com.example.examg6.entity.Task;
import com.example.examg6.repo.StatusRepository;
import com.example.examg6.repo.TaskRepository;
import com.example.examg6.repo.UserRepository;
import com.example.examg6.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class TaskController {
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;
    public TaskController(TaskRepository taskRepository, StatusRepository statusRepository, UserRepository userRepository, TaskService taskService) {
        this.taskRepository = taskRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
    }

    @GetMapping("/task/add")
    public String showAddForm(Model model) {
        model.addAttribute("taskDto", new TaskDto());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        return "task-add";
    }

    @PostMapping("/task/add")
    public String addTask(@ModelAttribute TaskDto taskDto,
                          @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        taskService.save(taskDto, file);
        return "redirect:/";
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
