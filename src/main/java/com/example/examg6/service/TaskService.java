package com.example.examg6.service;

import com.example.examg6.dto.TaskDto;
import com.example.examg6.entity.Task;
import com.example.examg6.entity.attachment.Attachment;
import com.example.examg6.entity.attachment.AttachmentContent;
import com.example.examg6.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class TaskService {

   private final TaskRepository taskRepository;
   private final UserRepository userRepository;
   private final StatusRepository statusRepository;
   private final AttachmentRepository attachmentRepository;
   private final AttachmentContentRepository attachmentContentRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, StatusRepository statusRepository, AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.statusRepository = statusRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
    }

    public void save(TaskDto dto, MultipartFile file) throws IOException {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        task.setStatus(statusRepository.findById(dto.getStatusId()).orElse(null));

        if (file != null && !file.isEmpty()) {
            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setContentType(file.getContentType());
            attachmentRepository.save(attachment);

            AttachmentContent content = new AttachmentContent();
            content.setAttachment(attachment);
            content.setContent(file.getBytes());
            attachmentContentRepository.save(content);

            task.setAttachment(attachment);
        }

        taskRepository.save(task);
    }
}
