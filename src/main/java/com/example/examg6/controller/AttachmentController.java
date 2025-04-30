package com.example.examg6.controller;

import com.example.examg6.entity.attachment.AttachmentContent;
import com.example.examg6.repo.AttachmentContentRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/attachments")
public class AttachmentController {
    private final AttachmentContentRepository attachmentContentRepository;

    public AttachmentController(AttachmentContentRepository attachmentContentRepository) {
        this.attachmentContentRepository = attachmentContentRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getAttachment(@PathVariable Integer id) {
        AttachmentContent content = attachmentContentRepository.findByAttachmentId(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(content.getAttachment().getContentType()));
        headers.setContentDispositionFormData("inline", content.getAttachment().getFileName());

        return new ResponseEntity<>(content.getContent(), headers, HttpStatus.OK);
    }
}
