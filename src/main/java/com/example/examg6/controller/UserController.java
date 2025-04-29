package com.example.examg6.controller;

import com.example.examg6.entity.User;
import com.example.examg6.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    public String verifyUser(@RequestParam String email, @RequestParam String code, Model model) {
        String response = userService.verifyUser(email, code);
        model.addAttribute("message", response);
        return "home";
    }
}
