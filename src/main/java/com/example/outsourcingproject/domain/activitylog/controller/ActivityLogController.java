package com.example.outsourcingproject.domain.activitylog.controller;

import com.example.outsourcingproject.domain.activitylog.controller.dto.ActivityLogResponseDto;
import com.example.outsourcingproject.domain.activitylog.service.ActivityLogService;

import com.example.outsourcingproject.domain.activitylog.service.dto.FindAllOptionDto;
import com.example.outsourcingproject.global.dto.ApiResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/activity-logs")
    public ResponseEntity<ApiResponse<List<ActivityLogResponseDto>>> findAll(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,

            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) Integer targetId,

            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createdAt");

        FindAllOptionDto findAllOptionDto = new FindAllOptionDto(
                pageable,
                userId,
                activityType,
                targetId,
                startDate,
                endDate
        );

        List<ActivityLogResponseDto> response = activityLogService.findAll(findAllOptionDto);
        return ResponseEntity.ok(ApiResponse.success(response, "활동 로그 조회가 완료되었습니다."));
    }
}
