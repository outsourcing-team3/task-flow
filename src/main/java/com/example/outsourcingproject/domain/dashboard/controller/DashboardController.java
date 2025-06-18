package com.example.outsourcingproject.domain.dashboard.controller;


import com.example.outsourcingproject.domain.dashboard.dto.*;
import com.example.outsourcingproject.domain.dashboard.dto.TodayTaskItemDto;
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
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(dashboardService.getWeeklyStatistics(date), "통계 조회 성공");
    }


    @GetMapping("/dashboard/my-tasks")
    public ApiResponse<List<TodayTaskItemDto>> getMyTodayTasks(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.success(dashboardService.getMyTodayTasks(principal.getId()), "오늘의 태스크 조회 성공");
    }

//    @GetMapping("/dashboard/my-tasks")
//    public ApiResponse<List<TodayTaskItemDto>> getMyTaskByDate(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
//    ) {
//
//        return ApiResponse.success(dashboardService.getMyTasksByDate(userPrincipal.getId(), date), "나의 태스크 조회 성공");
//    }

    @GetMapping("/dashboard/trend/week")
    public ApiResponse<List<DailyTaskTrendDto>> weeklyTrend(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(dashboardService.getWeeklyTrend(date), "주간 트렌드 조회 성공");
    }
    @GetMapping("/dashboard/status-ratio")
    public ApiResponse<TaskStatusRatioDto> statusRatio() {
        return ApiResponse.success(dashboardService.getStatusRatio(), "작업 상태 분포 조회 성공");
    }


    @GetMapping("/dashboard/progress-ratio")
    public ApiResponse<ProgressRatioDto> progressRatio(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(dashboardService.getProgressRatio(userPrincipal.getId()), "진행률 통계 조회 성공");
    }


    @GetMapping("/dashboard/trend/month")
    public ApiResponse<List<MonthlyTaskTrendDto>> monthTrend() {
        return ApiResponse.success(dashboardService.getMonthlyTrend(), "월간 작업 추세");
    }


    @GetMapping("/dashboard/activity-feed")
    public ApiResponse<List<ActivityFeedDto>> feed() {
        return ApiResponse.success(dashboardService.getAcitivityFeed(), "활동 피드를 조회했습니다.");
    }

}
