package com.example.outsourcingproject.domain.task.entity;

import com.example.outsourcingproject.domain.task.enums.Priority;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import com.example.outsourcingproject.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Column(name = "assignee_id")
    private Long assigneeId; // 담당자 ID (User FK)

    @Column(name = "creator_id", nullable = false)
    private Long creatorId; // 생성자 ID (User FK)

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
}