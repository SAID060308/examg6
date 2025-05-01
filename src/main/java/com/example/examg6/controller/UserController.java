package com.example.examg6.controller;

import com.example.examg6.entity.User;
import com.example.examg6.repo.StatusRepository;
import com.example.examg6.repo.TaskRepository;
import com.example.examg6.repo.UserRepository;
import com.example.examg6.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller

public class UserController {
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;

    public UserController(UserService userService, TaskRepository taskRepository, TaskRepository taskRepository1, StatusRepository statusRepository, UserRepository userRepository) {
        this.userService = userService;
        this.taskRepository = taskRepository1;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(email);
        model.addAttribute("user", user.get());
        model.addAttribute("tasks", taskRepository.findAll());
        model.addAttribute("statuses", statusRepository.findByIsActivePositionNumberNotNullOrderByIsActivePositionNumberAsc());
        return "index";
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam("file") MultipartFile file,
                               Model model) {
        String response = userService.registerUser(user, file);
        model.addAttribute("message", response);
        return "verify";
    }

    @GetMapping("/verify")
    public String showVerifyForm() {
        return "verify";
    }

    @PostMapping("/verify")
    public String verifyUser( @RequestParam String code, Model model) {
        String response = userService.verifyUser(code);
        model.addAttribute("message", response);
        return "login";
    }
}
