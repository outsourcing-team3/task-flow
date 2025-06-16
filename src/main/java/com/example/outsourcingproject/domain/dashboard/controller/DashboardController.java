package com.example.outsourcingproject.domain.dashboard.controller;


import com.example.outsourcingproject.domain.dashboard.dto.*;
import com.example.outsourcingproject.domain.task.dto.TaskSimpleResponseDto;
import com.example.outsourcingproject.global.dto.ApiResponse;
import com.example.outsourcingproject.domain.dashboard.service.DashboardService;
import com.example.outsourcingproject.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard/statistics")
    public ApiResponse<DashboardStatisticsDto> statistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be before 'to'");
        }


        return ApiResponse.success(dashboardService.getStatistics(from, to), "통계 조회 성공");
    }


    @GetMapping("/dashboard/my-tasks")
    public ApiResponse<List<TaskSimpleResponseDto>> getMyTaskByDate(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {


        List<TaskSimpleResponseDto> myTasks = dashboardService.getMyTasksByDate(userPrincipal.getId(), date);

        return ApiResponse.success(myTasks, "나의 태스크 조회 성공");
    }

    @GetMapping("/dashboard/trend/week")
    public ApiResponse<List<DailyTaskTrendDto>> weeklyTrend() {
        List<DailyTaskTrendDto> data =
                dashboardService.getWeeklyTrend(LocalDate.now());
        return ApiResponse.success(data, "주간 트렌드 조회 성공");
    }

    @GetMapping("/dashboard/status-ratio")
    public ApiResponse<TaskStatusRatioDto> statusRatio() {
        return ApiResponse.success(dashboardService.getStatusRatio(), "작업 상태 분포 조회 성공");
    }


    @GetMapping("/progress-ratio")
    public ApiResponse<ProgressRatioDto> progressRatio(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ProgressRatioDto dto = dashboardService.getProgressRatio(userPrincipal.getId());
        return ApiResponse.success(dto, "진행률 통계 조회 성공");
    }


    @GetMapping("/trend/month")
    public ApiResponse<List<MonthlyTaskTrendDto>> monthTrend() {
        return ApiResponse.success(
                dashboardService.getMonthlyTrend(), "월간 작업 추세"
               );
    }

}

