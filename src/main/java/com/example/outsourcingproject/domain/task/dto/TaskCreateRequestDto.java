package com.example.outsourcingproject.domain.task.dto;

import com.example.outsourcingproject.domain.task.enums.Priority;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime deadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
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
