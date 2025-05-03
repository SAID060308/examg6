package com.example.examg6.repo;

import com.example.examg6.entity.attachment.AttachmentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AttachmentContentRepository extends JpaRepository<AttachmentContent, Integer> {
    Optional<AttachmentContent> findByAttachmentId(Integer attachmentId);

    @Transactional
    void deleteByAttachmentId(Integer attachmentId);
}