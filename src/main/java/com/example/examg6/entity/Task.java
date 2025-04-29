package com.example.examg6.entity;

import com.example.examg6.entity.attachment.Attachment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Status status;


    @ManyToOne
    private User user;

    private String title;

    @OneToMany
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.EAGER)
    Attachment attachment;

}
