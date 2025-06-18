package com.example.outsourcingproject.domain.task.dto;

import com.example.outsourcingproject.domain.task.enums.Priority;
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

    private String status;

    private LocalDateTime deadline;

    private LocalDateTime startedAt;

    public TaskCreateRequestDto(String title, String description, Priority priority, String assigneeName, LocalDateTime deadline, LocalDateTime startedAt) {
        this.title = title;
        this.description = description;
        this.priority = priority.name();
        this.assigneeName = assigneeName;
        this.deadline = deadline;
        this.startedAt = startedAt;
    }
}
