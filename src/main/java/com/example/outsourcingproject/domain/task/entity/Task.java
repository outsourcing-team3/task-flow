package com.example.outsourcingproject.domain.task.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "longtext")
    private String description;

    private String priority;

//    @ManyToOne
//    @JoinColumn(name = "assignee_id")
//    private User assignee;
//
//    @ManyToOne
//    @JoinColumn(name = "creator_id")
//    private User creator;

    private LocalDateTime deadline;

    private String status;

    private LocalDateTime started_at;

}
