package com.example.outsourcingproject.domain.task.dto;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Task 수정 요청 DTO
 * - 제목, 내용, 우선순위, 담당자, 마감일, 시작일
 */
@Getter
public class TaskUpdateRequestDto {

    private String title;
    private String description;
    private String priority;
    private String assigneeName;
    private LocalDateTime deadline;
    private LocalDateTime startedAt;
}

