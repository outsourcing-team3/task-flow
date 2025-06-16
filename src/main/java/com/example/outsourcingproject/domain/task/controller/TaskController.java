package com.example.outsourcingproject.domain.task.controller;

import com.example.outsourcingproject.domain.task.dto.TaskCreateRequestDto;
import com.example.outsourcingproject.domain.task.dto.TaskCreateResponseDto;
import com.example.outsourcingproject.domain.task.service.TaskService;
import com.example.outsourcingproject.global.dto.ApiResponse;
import com.example.outsourcingproject.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskCreateResponseDto>> createTask(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody TaskCreateRequestDto requestDto) {

        Long currentUserId = userPrincipal.getId();

        TaskCreateResponseDto responseDto = taskService.createTask(currentUserId, requestDto);

        return new ResponseEntity<>(ApiResponse.success(responseDto), HttpStatus.CREATED);

    }
}
