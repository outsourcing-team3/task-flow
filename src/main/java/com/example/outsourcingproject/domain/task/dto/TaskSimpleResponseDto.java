package com.example.outsourcingproject.domain.task.dto;

import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.enums.Priority;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "from")
public class TaskSimpleResponseDto {
    private Long id;
    private String title;
    private TaskStatus status;
    private Priority priority;
    private LocalDateTime deadline;

    public static TaskSimpleResponseDto from(Task task) {
        return new TaskSimpleResponseDto(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getPriority(),
                task.getDeadline()
        );
    }
}

