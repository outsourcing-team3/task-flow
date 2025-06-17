package com.example.outsourcingproject.domain.activitylog.controller.dto;

import com.example.outsourcingproject.global.enums.TargetType;
import lombok.Getter;

@Getter
public class TargetTypeResponseDto {
    private int id;
    private String code;

    public TargetTypeResponseDto(TargetType targetType) {
        this.code = targetType.toString();
        this.id = targetType.getId();
    }
}
