package com.example.examg6.service;

import com.example.examg6.entity.Role;
import com.example.examg6.entity.User;
import com.example.examg6.entity.attachment.Attachment;
import com.example.examg6.entity.attachment.AttachmentContent;
import com.example.examg6.repo.AttachmentContentRepository;
import com.example.examg6.repo.AttachmentRepository;
import com.example.examg6.repo.RoleRepository;
import com.example.examg6.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;
    private final EmailService emailService;

    @Bean
    public ApplicationRunner initDefaultImage(AttachmentRepository attachmentRepository, AttachmentContentRepository contentRepository) {
        return args -> {
            if (!attachmentRepository.existsByFileName("default.jpg")) {
                InputStream is = getClass().getResourceAsStream("/static/files/default.jpg");
                byte[] bytes = is.readAllBytes();
                Attachment attachment = new Attachment(null, "default.jpg", "image/jpeg");
                attachmentRepository.save(attachment);

                AttachmentContent content = new AttachmentContent(null, attachment, bytes);
                contentRepository.save(content);
            }
        };
    }


    public String registerUser(User user, MultipartFile file) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        // Save role
        Role role = roleRepository.findByName("PROGRAMMER")
                .orElseGet(() -> roleRepository.save(new Role(null, "PROGRAMMER")));
        user.setRoles(Collections.singletonList(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save photo
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
        } else {
            // default photo
            Optional<Attachment> defaultImage = attachmentRepository.findByFileName("default.jpg");
            defaultImage.ifPresent(user::setAttachment);
        }


        String verifyCode = String.format("%04d", new Random().nextInt(10000));
        user.setVerifiedCode(verifyCode);
        userRepository.save(user);

        emailService.sendEmail(user.getEmail(), "Tasdiqlash kodi", "Sizning kodingiz: " + verifyCode);
        return "Ro'yxatdan o'tdingiz! Email tasdiqlash kod yuborildi.";
    }

    public String verifyUser(String code) {
        Optional<User> optionalUser = userRepository.findByVerifiedCode(code);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerifiedCode(null);
            userRepository.save(user);
            return "Tasdiqlash muvaffaqiyatli bajarildi!";
        }
        return "Noto‘g‘ri kod yoki email!";
    }
    public boolean hasAdminRole(User user) {
        return user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()));
    }
}
