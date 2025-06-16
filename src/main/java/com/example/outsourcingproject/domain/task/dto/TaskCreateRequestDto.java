package com.example.outsourcingproject.domain.task.dto;

import com.example.outsourcingproject.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskCreateRequestDto {

    @NotNull
    private String title;

    private String description = "No description provided.";

    private String priority = "MEDIUM";

    private User assignee;

    private LocalDateTime deadline;

    public TaskCreateRequestDto(String title, String description, String priority, User assignee, LocalDateTime deadline) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.assignee = assignee;
        this.deadline = deadline;
    }
}
