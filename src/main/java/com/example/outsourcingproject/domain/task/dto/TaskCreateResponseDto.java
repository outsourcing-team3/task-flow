package com.example.outsourcingproject.domain.task.dto;

import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskCreateResponseDto {

    private Long Id;

    private String title;

    private String description;

    private String status;

    private LocalDateTime deadline;

    private String priority;

    private String assignee;

    private String creator;

    private LocalDateTime startedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public TaskCreateResponseDto(Long Id, String title, String description, TaskStatus status, LocalDateTime deadline, String priority, String assignee, String creator, LocalDateTime startedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.Id = Id;
        this.title = title;
        this.description = description;
        this.status = status.name();
        this.deadline = deadline;
        this.priority = priority;
        this.assignee = assignee;
        this.creator = creator;
        this.startedAt = startedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TaskCreateResponseDto toDto(Task task) {
        return new TaskCreateResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadline(),
                task.getPriority(),
                task.getAssignee().getName(),
                task.getCreator().getName(),
                task.getStartedAt(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
