package com.example.outsourcingproject.domain.activitylog.controller.dto;

import com.example.outsourcingproject.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final Long userId;
    private final String name;
    private final String email;

    public UserResponseDto(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
