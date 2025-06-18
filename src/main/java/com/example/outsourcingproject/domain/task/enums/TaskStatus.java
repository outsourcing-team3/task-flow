package com.example.outsourcingproject.domain.task.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    TODO("할 일"),
    IN_PROGRESS("진행 중"),
    DONE("완료");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }
}
