package com.example.outsourcingproject.domain.dashboard.service;

import com.example.outsourcingproject.domain.dashboard.dto.*;
import com.example.outsourcingproject.domain.dashboard.repository.TaskStatisticsRepository;
import com.example.outsourcingproject.domain.task.dto.TaskSimpleResponseDto;
import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;


import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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


    public DashboardStatisticsDto getStatistics(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        long total = taskStatisticsRepository.countByCreatedAtBetween(start, end);
        long todo = taskStatisticsRepository.countByStatusAndPeriod(TaskStatus.TODO, start, end);
        long inProgress = taskStatisticsRepository.countByStatusAndPeriod(TaskStatus.IN_PROGRESS, start, end);
        long done = taskStatisticsRepository.countByStatusAndPeriod(TaskStatus.DONE, start, end);

        long overdue = taskStatisticsRepository.countOverdueTasks(
                List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS),
                LocalDateTime.now());

        double completionRate = rate(done, total);
        double inProgressRate = rate(inProgress, total);
        double overdueRate = rate(overdue, total);


        //주간 증가율 계산

        LocalDateTime thisWeekStart = to.minusDays(6).atStartOfDay();
        LocalDateTime thisWeekEnd = to.plusDays(1).atStartOfDay();
        LocalDateTime lastWeekStart = thisWeekStart.minusDays(7);
        LocalDateTime lastWeekEnd = thisWeekStart;

        long thisWeekCount = taskStatisticsRepository.countByCreatedAtBetween(thisWeekStart, thisWeekEnd);
        long lastWeekCount = taskStatisticsRepository.countByCreatedAtBetween(lastWeekStart, lastWeekEnd);


        //((이번주 개수 - 지난 주 개수)/ 지난 주 개수) * 100
        double weeklyChangeRate = (lastWeekCount == 0)
                ? (thisWeekCount > 0 ? 100.0 : 0.0)
                : Math.round((thisWeekCount - lastWeekCount) * 10000.0 / lastWeekCount) / 100.0;


        return DashboardStatisticsDto.of(
                total,
                done,
                weeklyChangeRate,
                inProgress,
                inProgressRate,
                todo,
                completionRate,
                overdue,
                overdueRate
        );

    }


    private double rate(long part, long all) {
        if (all == 0) return 0;
        return Math.round(part * 10000.0 / all) / 100.0; // 소수점 둘째 자리
    }


    public List<TaskSimpleResponseDto> getMyTasksByDate(Long userId, LocalDate date) {
        List<Task> tasks = taskStatisticsRepository.findAllByUserIdAndDate(
                userId,
                date,
                List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS));
        return tasks.stream()
                .map(TaskSimpleResponseDto::from)
                .collect(Collectors.toList());

    }


    //최근 1주(월~일) 트렌드 반환
    public List<DailyTaskTrendDto> getWeeklyTrend(LocalDate today) {

        //이번주 월요일 00:00
        LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);
        LocalDateTime start = monday.atStartOfDay();
        //다음 주 월요일 00:00
        LocalDateTime end = monday.plusWeeks(1).atStartOfDay();


        return taskStatisticsRepository.fetchDailyTrend(start, end);

    }

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


}
