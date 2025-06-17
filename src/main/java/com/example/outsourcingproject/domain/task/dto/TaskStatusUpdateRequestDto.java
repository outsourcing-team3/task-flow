package com.example.outsourcingproject.domain.task.dto;

import lombok.Getter;

/**
 * Task 상태 수정 요청 DTO -> TODO / IN_PROGRESS / DONE
 */
@Getter
public class TaskStatusUpdateRequestDto {

    private String status;
}
