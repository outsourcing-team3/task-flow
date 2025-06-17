package com.example.outsourcingproject.domain.activitylog.controller.dto;

import com.example.outsourcingproject.global.enums.ActivityType;
import lombok.Getter;

@Getter
public class ActivityTypeResponseDto {
    private final String code;
    private final String label;
    private final String message1;
    private final String message2;

    public ActivityTypeResponseDto(ActivityType activityType) {
        this.code = activityType.toString();
        this.label = activityType.getLabel();
        this.message1 = activityType.getMessage1();
        this.message2 = activityType.getMessage2();
    }
}
