package com.example.examg6.service;

import com.example.examg6.entity.Role;
import com.example.examg6.entity.User;
import com.example.examg6.entity.attachment.Attachment;
import com.example.examg6.entity.attachment.AttachmentContent;
import com.example.examg6.entity.repo.AttachmentContentRepository;
import com.example.examg6.entity.repo.AttachmentRepository;
import com.example.examg6.entity.repo.RoleRepository;
import com.example.examg6.entity.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
    }


    public String registerUser(User user, MultipartFile file) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        // 1. ROLE biriktirish
        Role role = roleRepository.findByName("PROGRAMMER")
                .orElseGet(() -> roleRepository.save(new Role(null, "PROGRAMMER")));
        user.setRoles(Collections.singletonList(role));

        // 2. Fayl saqlash
        if (file != null && !file.isEmpty()) {
            try {
                Attachment attachment = Attachment.builder()
                        .fileName(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .build();
                attachmentRepository.save(attachment);

                AttachmentContent content = AttachmentContent.builder()
                        .attachment(attachment)
                        .content(file.getBytes())
                        .build();
                attachmentContentRepository.save(content);

                user.setAttachment(attachment);
            } catch (Exception e) {
                return "Rasmni saqlashda xatolik: " + e.getMessage();
            }
        }


        return "Ro'yxatdan o'tdingiz! Email tasdiqlash kod yuborildi.";
    }


}
