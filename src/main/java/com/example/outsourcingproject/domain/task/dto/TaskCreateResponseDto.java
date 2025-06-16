package com.example.outsourcingproject.domain.task.dto;

import com.example.outsourcingproject.domain.task.entity.Task;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskCreateResponseDto {

    private String title;

    private String description;

    private String status;

    private LocalDateTime deadline;

    private String priority;

    private String assignee;

    private String creator;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public TaskCreateResponseDto(String title, String description, String status, LocalDateTime deadline, String priority, String assignee, String creator, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.priority = priority;
        this.assignee = assignee;
        this.creator = creator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TaskCreateResponseDto toDto(Task task) {
        return new TaskCreateResponseDto(
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadline(),
                task.getPriority(),
                task.getAssignee().getUsername(),
                task.getCreator().getUsername(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
