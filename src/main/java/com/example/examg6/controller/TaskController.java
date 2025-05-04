package com.example.examg6.controller;

import com.example.examg6.dto.TaskDto;
import com.example.examg6.entity.Status;
import com.example.examg6.entity.Task;
import com.example.examg6.repo.*;
import com.example.examg6.service.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;

    public TaskController(TaskRepository taskRepository, StatusRepository statusRepository,
                          UserRepository userRepository, TaskService taskService,
                          AttachmentRepository attachmentRepository,
                          AttachmentContentRepository attachmentContentRepository) {
        this.taskRepository = taskRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
    }

    @GetMapping("/task/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    public String showAddForm(Model model) {
        model.addAttribute("taskDto", new TaskDto());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        return "task-add";
    }

    @PostMapping("/task/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    public String addTask(@ModelAttribute TaskDto taskDto,
                          @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        taskService.save(taskDto, file);
        return "redirect:/";
    }

    @GetMapping("/task/add-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    public String addStatus(Model model) {
        if (hasAdminOrMaintainerRole()) {
            return "redirect:/";
        }
        return "add-status";
    }

    @PostMapping("/status/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    public String saveStatus(@RequestParam String status,
                             @RequestParam String positionType) {
        if (hasAdminOrMaintainerRole()) {
            return "redirect:/";
        }

        Status newStatus = new Status();
        newStatus.setStatus(status);
        List<Status> activeStatuses = statusRepository.findByIsActivePositionNumberNotNullOrderByIsActivePositionNumberAsc();
        List<Status> allStatus = statusRepository.findAll();

        if (positionType.equals("active")) {
            newStatus.setIsActivePositionNumber(activeStatuses.size()+1);
            newStatus.setIsNotActivePositionNumber(null);
        } else {
            newStatus.setIsActivePositionNumber(null);
            newStatus.setIsNotActivePositionNumber(allStatus.size()- activeStatuses.size()+1);
        }

        statusRepository.save(newStatus);
        return "redirect:/";
    }

    @PostMapping("/task/changeStatusMinus/{taskId}")
    public String changeTaskStatusMinus(@PathVariable Integer taskId) {
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
    public String changeTaskStatusPlus(@PathVariable Integer taskId,
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

    private boolean hasAdminOrMaintainerRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .noneMatch(g -> g.getAuthority().equals("ROLE_ADMIN") ||
                        g.getAuthority().equals("ROLE_MAINTAINER"));
    }
}