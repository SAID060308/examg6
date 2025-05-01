package com.example.examg6.controller;

import com.example.examg6.entity.Comment;
import com.example.examg6.repo.CommentRepository;
import com.example.examg6.entity.Task;
import com.example.examg6.entity.User;
import com.example.examg6.repo.TaskRepository;
import com.example.examg6.repo.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentController(TaskRepository taskRepository, UserRepository userRepository,
                             CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/show/{id}")
    public String comments(Model model, @PathVariable Integer id) {
        Task task = taskRepository.findById(Long.valueOf(id)).orElse(null);
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("task", task);
        model.addAttribute("name", name);
        return "comments";
    }

    @PostMapping("/add/{id}")
    public String addComment(Model model, @PathVariable Integer id , @RequestParam String comment , @RequestParam String username, @RequestParam Integer task_id) {
        Task task = taskRepository.findById(Long.valueOf(id)).orElse(null);
        Optional<User> user = userRepository.findByEmail(username);
        Comment newComment = new Comment();
        newComment.setComment(comment);
        newComment.setUser(user.get());

        newComment.setCreationDate(LocalDateTime.now());
        commentRepository.save(newComment);
        assert task != null;
        task.getComments().add(newComment);
        taskRepository.save(task);
        return "redirect:/comments/show/" + task_id;
    }

}
