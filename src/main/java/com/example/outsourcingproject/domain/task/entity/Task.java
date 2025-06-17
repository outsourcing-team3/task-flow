package com.example.outsourcingproject.domain.task.entity;

import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "longtext")
    private String description;

    private String priority;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    private LocalDateTime deadline;

    private String status;

    private LocalDateTime startedAt;

    public Task() {
    }

    public Task(String title, String description, String priority, User assignee, User creator, String status, LocalDateTime deadline, LocalDateTime startedAt) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.assignee = assignee;
        this.creator = creator;
        this.status = status;
        this.deadline = deadline;
        this.startedAt = startedAt;
    }
}
