package com.example.examg6.repo;

import com.example.examg6.entity.attachment.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    boolean existsByFileName(String fileName);
    Optional<Attachment> findByFileName(String fileName);
}