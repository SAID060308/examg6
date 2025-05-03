package com.example.examg6.controller;

import com.example.examg6.entity.User;
import com.example.examg6.entity.attachment.Attachment;
import com.example.examg6.entity.attachment.AttachmentContent;
import com.example.examg6.repo.*;
import com.example.examg6.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller

public class UserController {
    private final AttachmentContentRepository attachmentContentRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;

    public UserController(UserService userService, TaskRepository taskRepository, AttachmentContentRepository attachmentContentRepository, AttachmentRepository attachmentRepository, TaskRepository taskRepository1, StatusRepository statusRepository, UserRepository userRepository) {
        this.userService = userService;
        this.attachmentContentRepository = attachmentContentRepository;
        this.attachmentRepository = attachmentRepository;
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
        model.addAttribute("isProfilePage", false); // Asosiy sahifa uchun
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


    @GetMapping("/profile")
    public String showProfile(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(email);
        model.addAttribute("user", user.get());
        model.addAttribute("isProfilePage", true); // Profile sahifasi uchun
        return "profile";
    }


    @PostMapping("/profile/update")
    @Transactional
    public String updateProfile(@RequestParam(value = "file", required = false) MultipartFile file,
                                @RequestParam("username") String username,
                                Model model) throws IOException {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setUsername(username);

            if (file != null && !file.isEmpty()) {
                updateUserProfilePicture(user, file);
            }

            userRepository.save(user);
            model.addAttribute("message", "Profile updated successfully!");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
            return "redirect:/profile";
        }
    }

     @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void updateUserProfilePicture(User user, MultipartFile file) throws IOException {
        if (user.getAttachment() != null && !"default.jpg".equals(user.getAttachment().getFileName())) {
            if (file != null && !file.isEmpty() ) {
                Attachment attachment = new Attachment();
                attachment.setFileName(file.getOriginalFilename());
                attachment.setContentType(file.getContentType());
                attachmentRepository.save(attachment);

                AttachmentContent content = new AttachmentContent();
                content.setAttachment(attachment);
                content.setContent(file.getBytes());
                attachmentContentRepository.save(content);

                user.setAttachment(attachment);
            }

        }

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment = attachmentRepository.save(attachment);

        AttachmentContent content = new AttachmentContent();
        content.setAttachment(attachment);
        content.setContent(file.getBytes());
        attachmentContentRepository.save(content);

        user.setAttachment(attachment);
    }

}
