package com.example.outsourcingproject.domain.task.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TaskCreateRequestDto {

    @NotNull
    private String title;

    private String description;

    private String priority;

    private String assigneeName;

    private LocalDateTime deadline;

    private LocalDateTime startedAt;

    public TaskCreateRequestDto(String title, String description, String priority, String assigneeName, LocalDateTime deadline, LocalDateTime startedAt) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.assigneeName = assigneeName;
        this.deadline = deadline;
        this.startedAt = startedAt;
    }
}
