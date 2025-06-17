package com.example.outsourcingproject.domain.dashboard.service;

import com.example.outsourcingproject.domain.dashboard.dto.*;
import com.example.outsourcingproject.domain.dashboard.repository.AcitivityFeedRepository;
import com.example.outsourcingproject.domain.dashboard.repository.TaskStatisticsRepository;
import com.example.outsourcingproject.domain.task.dto.TodayTaskItemDto;
import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final TaskStatisticsRepository taskStatisticsRepository;
    private final AcitivityFeedRepository acitivityFeedRepository;


    /**
     * 기준일(date)을 포함한 주(월~일)와 그 전 주를 비교해 통계와 주간 증감률을 반환.
     */
    public DashboardStatisticsDto getWeeklyStatistics(LocalDate date) {

        LocalDate monday           = date.with(DayOfWeek.MONDAY);
        LocalDateTime weekStart    = monday.atStartOfDay();
        LocalDateTime weekEnd      = monday.plusWeeks(1).atStartOfDay();


        LocalDateTime prevWeekStart = monday.minusWeeks(1).atStartOfDay();
        LocalDateTime prevWeekEnd   = weekStart;

        long total      = taskStatisticsRepository.countByCreatedAtBetween(weekStart, weekEnd);
        long todo       = taskStatisticsRepository.countByStatusAndPeriod(TaskStatus.TODO,        weekStart, weekEnd);
        long inProgress = taskStatisticsRepository.countByStatusAndPeriod(TaskStatus.IN_PROGRESS, weekStart, weekEnd);
        long done       = taskStatisticsRepository.countByStatusAndPeriod(TaskStatus.DONE,        weekStart, weekEnd);
        long overdue    = taskStatisticsRepository.countOverdueTasks(List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS), LocalDateTime.now());

        double completionRate  = rate(done, total);
        double inProgressRate  = rate(inProgress, total);
        double overdueRate     = rate(overdue, total);


        //주간 증가율 계산

        long lastWeekTotal = taskStatisticsRepository.countByCreatedAtBetween(prevWeekStart, prevWeekEnd);

        double weeklyChangeRate = (lastWeekTotal == 0)
                ? (total > 0 ? 100.0 : 0.0)
                : Math.round((total - lastWeekTotal) * 10000.0 / lastWeekTotal) / 100.0;

        return DashboardStatisticsDto.of(
                total,
                weeklyChangeRate,
                done,
                completionRate,
                inProgress,
                inProgressRate,
                todo,
                overdue,
                overdueRate
        );
    }



    private double rate(long part, long total) {
        return (total == 0) ? 0.0 : Math.round(part * 10000.0 / total) / 100.0;
    }



    public List<TodayTaskItemDto> getMyTodayTasks(Long userId){
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Pageable topFive = PageRequest.of(0, 5);

        return taskStatisticsRepository.findTodayTasks(
                userId,
                List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS),
                start, end,
                topFive
        );

    }

//
//    public List<TodayTaskItemDto> getMyTasksByDate(Long userId, LocalDate date) {
//        List<Task> tasks = taskStatisticsRepository.findAllByUserIdAndDate(
//                userId,
//                date,
//                List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS));
//        return tasks.stream()
//                .map(TodayTaskItemDto::from)
//                .collect(Collectors.toList());
//
//    }


    //최근 1주(월~일) 트렌드 반환
    public List<DailyTaskTrendDto> getWeeklyTrend(LocalDate today) {

        //이번주 월요일 00:00
        LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);
        LocalDateTime start = monday.atStartOfDay();
        //다음 주 월요일 00:00
        LocalDateTime end = monday.plusWeeks(1).atStartOfDay();


        return taskStatisticsRepository.fetchDailyTrend(start, end);

    }

    //작업 상태
    public TaskStatusRatioDto getStatusRatio() {

        List<TaskStatusCountDto> result = taskStatisticsRepository.countGroupByStatus();

        long todo = 0, inProgress = 0, done = 0;

        for (TaskStatusCountDto dto : result) {
            switch (dto.getStatus()) {
                case TODO -> todo = dto.getCount();
                case IN_PROGRESS -> inProgress = dto.getCount();
                case DONE -> done = dto.getCount();
            }
        }

        return TaskStatusRatioDto.of(todo, inProgress, done);

    }

    //진행률 계산
    public ProgressRatioDto getProgressRatio(Long userId) {

        //내 상태별 카운트
        Map<TaskStatus, Long> myMap = toMap(taskStatisticsRepository.countMyStatus(userId));

        //팀 상태별 카운트
        Map<TaskStatus, Long> teamMap = toMap(taskStatisticsRepository.countTeamStatus());


        long myDone = myMap.getOrDefault(TaskStatus.DONE, 0L);
        long myTotal = myMap.values().stream().mapToLong(Long::longValue).sum();

        long teamDone = teamMap.getOrDefault(TaskStatus.DONE, 0L);
        long teamTotal = teamMap.values().stream().mapToLong(Long::longValue).sum();

        double myRate   = rate(myDone,   myTotal);
        double teamRate = rate(teamDone, teamTotal);

        return ProgressRatioDto.of(myRate, teamRate);

    }



    private Map<TaskStatus, Long> toMap(List<TaskStatusCountDto> list) {

        return list.stream()
                    .collect(Collectors.toMap(
                            TaskStatusCountDto::getStatus,
                            TaskStatusCountDto::getCount));

    }


    //월별 추세 구하기
    public List<MonthlyTaskTrendDto> getMonthlyTrend() {
        int year = Year.now().getValue();
        List<MonthlyTaskTrendDto> result = taskStatisticsRepository.fetchFixedMonthlyTrend(year);

        Map<Integer, MonthlyTaskTrendDto> map = result.stream()
                .collect(Collectors.toMap(
                        MonthlyTaskTrendDto::getMonth,
                        dto -> dto
                ));

        List<MonthlyTaskTrendDto> fullYear = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            fullYear.add(map.getOrDefault(m, MonthlyTaskTrendDto.of(m, 0, 0)));
        }

        return fullYear;

    }


    public List<ActivityFeedDto> getAcitivityFeed(){
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusDays(7);
        return acitivityFeedRepository.fetchFeed(from, to);

    }


}
