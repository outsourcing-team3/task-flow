package com.example.outsourcingproject.domain.activitylog.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ActivityLogResponseDto {
    private Long id;
    private UserResponseDto user;
    private ActivityTypeResponseDto activityType;
    private TargetTypeResponseDto targetType;
    private String requestIp;
    private String requestMethod;
    private String requestUrl;

    private LocalDateTime createdAt;
}
