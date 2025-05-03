package com.example.examg6.controller;

import com.example.examg6.entity.Role;
import com.example.examg6.entity.Status;
import com.example.examg6.entity.Task;
import com.example.examg6.entity.User;
import com.example.examg6.entity.attachment.Attachment;
import com.example.examg6.entity.attachment.AttachmentContent;
import com.example.examg6.repo.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {


    private final StatusRepository statusRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;
    private final RoleRepository roleRepository;

    public AdminController(StatusRepository statusRepository, TaskRepository taskRepository, UserRepository userRepository, AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository, RoleRepository roleRepository) {
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/statuses")
    public String statuses(Model model) {
        List<Status> activeStatuses = statusRepository.findByIsActivePositionNumberNotNullOrderByIsActivePositionNumberAsc();
        List<Status> inactiveStatuses = statusRepository.findByIsNotActivePositionNumberNotNullOrderByIsNotActivePositionNumberAsc();
        model.addAttribute("activeStatuses", activeStatuses);
        model.addAttribute("inactiveStatuses", inactiveStatuses);
        return "statuses";
    }

    @PostMapping("/change")
    public String changeStatus(@RequestParam Integer id, @RequestParam String changeActive) {
        List<Status> activeStatuses = statusRepository.findByIsActivePositionNumberNotNullOrderByIsActivePositionNumberAsc();
        Integer lastActive = 0;
        if (!activeStatuses.isEmpty()){
            lastActive = activeStatuses.get(activeStatuses.size()-1).getIsActivePositionNumber();
        }
        List<Status> notActiveStatuses = statusRepository.findByIsNotActivePositionNumberNotNullOrderByIsNotActivePositionNumberAsc();
        Integer lastNotActive = 0;
        if (!notActiveStatuses.isEmpty()){
            lastNotActive = notActiveStatuses.get(notActiveStatuses.size()-1).getIsNotActivePositionNumber();
        }
        if (changeActive.equals("active")) {
            Status status = statusRepository.findById(id).get();
            status.setIsActivePositionNumber(lastActive + 1);
            status.setIsNotActivePositionNumber(null);
            statusRepository.save(status);
        }else {
            Status status = statusRepository.findById(id).get();
            status.setIsNotActivePositionNumber(lastNotActive + 1);
            status.setIsActivePositionNumber(null);
            statusRepository.save(status);
        }
        return "redirect:/admin/statuses";
    }

    @PostMapping("/save-change")
    public String saveChange(
            @RequestParam(value = "activeIds", required = false) List<Integer> activeIds,
            @RequestParam(value = "activePositions", required = false) List<Integer> activePositions,
            @RequestParam(value = "inactiveIds", required = false) List<Integer> inactiveIds,
            @RequestParam(value = "inactivePositions", required = false) List<Integer> inactivePositions
    ) {
        // Save active statuses
        if (activeIds != null && activePositions != null) {
            for (int i = 0; i < activeIds.size(); i++) {
                Integer id = activeIds.get(i);
                Integer position = activePositions.get(i);
                Status status = statusRepository.findById(id).orElse(null);
                if (status != null) {
                    status.setIsActivePositionNumber(position);
                    status.setIsNotActivePositionNumber(null);
                    statusRepository.save(status);
                }
            }
        }

        // Save inactive statuses
        if (inactiveIds != null && inactivePositions != null) {
            for (int i = 0; i < inactiveIds.size(); i++) {
                Integer id = inactiveIds.get(i);
                Integer position = inactivePositions.get(i);
                Status status = statusRepository.findById(id).orElse(null);
                if (status != null) {
                    status.setIsNotActivePositionNumber(position);
                    status.setIsActivePositionNumber(null);
                    statusRepository.save(status);
                }
            }
        }

        return "redirect:/";
    }

    @GetMapping("/task/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));


        model.addAttribute("task_id", id);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        model.addAttribute("task", task);

        return "task-edit";
    }

    @PostMapping("/task/update/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute Task task,
                             @RequestParam("file") MultipartFile file) throws IOException {
        // 1. Eski taskni topish
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        existingTask.setTitle(task.getTitle());
        existingTask.setUser(task.getUser());
        existingTask.setStatus(task.getStatus());

        if (file != null && !file.isEmpty() ) {
            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setContentType(file.getContentType());
            attachmentRepository.save(attachment);

            AttachmentContent content = new AttachmentContent();
            content.setAttachment(attachment);
            content.setContent(file.getBytes());
            attachmentContentRepository.save(content);

            existingTask.setAttachment(attachment);
        }

        taskRepository.save(existingTask);

        return "redirect:/";
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userRepository.findAllByOrderByUsername();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/roles/{id}")
    public String roles(@PathVariable Integer id, Model model) {
        Optional<User> byId = userRepository.findById(id);
        User user = byId.get();
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "roles";
    }

    @PostMapping("/users/assign-role")
    public String assignRole(@RequestParam Integer userId,
                             @RequestParam Integer roleId) {
        User user = userRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();

        List<Role> userRoles = user.getRoles();

        if (userRoles.contains(role)) {
            userRoles.remove(role);
        } else {
            userRoles.add(role);
        }

        user.setRoles(userRoles);
        userRepository.save(user);

        return "redirect:/admin/users/roles/" + userId;
    }

    @PostMapping("/users/deactivate/{id}")
    public String deactivateUser(@PathVariable Integer id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }

    @PostMapping("/users/activate/{id}")
    public String activateUser(@PathVariable Integer id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(true);
            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }



}
