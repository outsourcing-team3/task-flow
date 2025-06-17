package com.example.outsourcingproject.domain.task.controller;

import com.example.outsourcingproject.domain.task.dto.TaskCreateRequestDto;
import com.example.outsourcingproject.domain.task.dto.TaskCreateResponseDto;
import com.example.outsourcingproject.domain.task.dto.TaskReadResponseDto;
import com.example.outsourcingproject.domain.task.service.TaskService;
import com.example.outsourcingproject.global.dto.ApiResponse;
import com.example.outsourcingproject.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Task 생성
    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskCreateResponseDto>> createTask(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody TaskCreateRequestDto requestDto) {

        Long currentUserId = userPrincipal.getId();

        TaskCreateResponseDto responseDto = taskService.createTask(currentUserId, requestDto);

//        return new ResponseEntity<>(ApiResponse.success(responseDto), HttpStatus.CREATED);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto, "Task 가 생성되었습니다."));

    }

    // Task - 전체 조회
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<TaskReadResponseDto>>> getAllTasks() {
        List<TaskReadResponseDto> getTasks = taskService.getAllTasks();
        return ResponseEntity.ok(ApiResponse.success(getTasks, "모든 Task 를 조회하였습니다."));
    }

    // Task - 단건 조회
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<TaskReadResponseDto>> getTaskById(@PathVariable Long taskId) {
        TaskReadResponseDto getTask = taskService.getTaskById(taskId);
        return ResponseEntity.ok(ApiResponse.success(getTask, "[ " + getTask.getTitle() + "] Task 를 조회하였습니다."));
    }

}
