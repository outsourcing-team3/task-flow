package com.example.outsourcingproject.domain.dashboard.service;

import com.example.outsourcingproject.domain.dashboard.dto.*;
import com.example.outsourcingproject.domain.dashboard.repository.AcitivityFeedRepository;
import com.example.outsourcingproject.domain.dashboard.repository.TaskStatisticsRepository;
import com.example.outsourcingproject.domain.task.enums.Priority;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks private DashboardService dashboardService;

    @Mock private TaskStatisticsRepository taskRepo;
    @Mock private AcitivityFeedRepository feedRepo;

    /* ------------------------------------------------------------------ */
    @Test
    @DisplayName("주간 증가율 계산")
    void 주간증가율_성공() {
        LocalDate target   = LocalDate.of(2025, 6, 17);             // 화
        LocalDateTime from = LocalDate.of(2025, 6, 16).atStartOfDay(); // 이번 주 월
        LocalDateTime to   = LocalDate.of(2025, 6, 23).atStartOfDay(); // 다음 주 월
        LocalDateTime prevFrom = from.minusWeeks(1);                 // 전주 월
        LocalDateTime prevTo   = from;                               // 전주 일 24:00

        when(taskRepo.countByCreatedAtBetween(from, to)).thenReturn(10L);
        when(taskRepo.countByStatusAndPeriod(TaskStatus.TODO,        from, to)).thenReturn(3L);
        when(taskRepo.countByStatusAndPeriod(TaskStatus.IN_PROGRESS, from, to)).thenReturn(4L);
        when(taskRepo.countByStatusAndPeriod(TaskStatus.DONE,        from, to)).thenReturn(3L);
        when(taskRepo.countByCreatedAtBetween(prevFrom, prevTo)).thenReturn(5L);
        when(taskRepo.countOverdueTasks(anyList(), any())).thenReturn(2L);

        DashboardStatisticsDto dto = dashboardService.getWeeklyStatistics(target);

        assertThat(dto.getCompletionRate()).isEqualTo(30.0);
        assertThat(dto.getInProgressRate()).isEqualTo(40.0);
        assertThat(dto.getOverdueRate()).isEqualTo(20.0);
        assertThat(dto.getWeeklyChangeRate()).isEqualTo(100.0);
        assertThat(dto.getTotalCount()).isEqualTo(10);
        assertThat(dto.getDoneCount()).isEqualTo(3);

        ArgumentCaptor<LocalDateTime> fc = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> tc = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(taskRepo, times(2)).countByCreatedAtBetween(fc.capture(), tc.capture());
        assertThat(fc.getAllValues()).containsExactly(from, prevFrom);
        assertThat(tc.getAllValues()).containsExactly(to,   prevTo);
    }

    /* ------------------------------------------------------------------ */
    @Test
    @DisplayName("오늘의 태스크 5건 조회")
    void getMyTodayTasks_success() {
        LocalDate fixedToday = LocalDate.of(2025, 6, 18);
        try (MockedStatic<LocalDate> mock = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mock.when(LocalDate::now).thenReturn(fixedToday);

            Long userId = 1L;
            LocalDateTime start = fixedToday.atStartOfDay();
            LocalDateTime end   = fixedToday.plusDays(1).atStartOfDay();
            Pageable top5 = PageRequest.of(0, 5);

            List<TodayTaskItemDto> stub = List.of(
                    TodayTaskItemDto.from(1L, "T1", TaskStatus.TODO,        Priority.HIGH,   start.plusHours(2)),
                    TodayTaskItemDto.from(2L, "T2", TaskStatus.IN_PROGRESS, Priority.MEDIUM, start.plusHours(3))
            );
            when(taskRepo.findTodayTasks(eq(userId),
                    eq(List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS)),
                    eq(start), eq(end), eq(top5)))
                    .thenReturn(stub);

            List<TodayTaskItemDto> result = dashboardService.getMyTodayTasks(userId);

            assertThat(result).isEqualTo(stub);
            verify(taskRepo).findTodayTasks(eq(userId), anyList(), eq(start), eq(end), eq(top5));
        }
    }

    /* ------------------------------------------------------------------ */
    @Test
    @DisplayName("주간 트렌드 조회")
    void getWeeklyTrend_success() {
        LocalDate today = LocalDate.of(2025, 6, 19);      // 목
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDateTime start = monday.atStartOfDay();
        LocalDateTime end   = monday.plusWeeks(1).atStartOfDay();

        when(taskRepo.fetchDailyTrend(start, end)).thenReturn(
                List.of(
                        DailyTaskTrendDto.of(monday,          5L, 2L),
                        DailyTaskTrendDto.of(monday.plusDays(1), 8L, 3L))
        );

        assertThat(dashboardService.getWeeklyTrend(today)).hasSize(2);
    }

    /* ------------------------------------------------------------------ */
    @Test
    @DisplayName("작업 상태 비율 계산")
    void getStatusRatio_success() {
        when(taskRepo.countGroupByStatus()).thenReturn(
                List.of(
                        TaskStatusCountDto.of(TaskStatus.TODO, 3),
                        TaskStatusCountDto.of(TaskStatus.IN_PROGRESS, 2),
                        TaskStatusCountDto.of(TaskStatus.DONE, 1))
        );

        TaskStatusRatioDto ratio = dashboardService.getStatusRatio();
        assertThat(ratio.getTodoCount()).isEqualTo(3);
        assertThat(ratio.getInProgressCount()).isEqualTo(2);
        assertThat(ratio.getDoneCount()).isEqualTo(1);
    }

    /* ------------------------------------------------------------------ */
    @Test
    @DisplayName("개인 vs 팀 진행률")
    void getProgressRatio_success() {
        Long userId = 99L;

        when(taskRepo.countMyStatus(userId)).thenReturn(
                List.of(TaskStatusCountDto.of(TaskStatus.TODO, 1),
                        TaskStatusCountDto.of(TaskStatus.DONE, 3))
        );
        when(taskRepo.countTeamStatus()).thenReturn(
                List.of(TaskStatusCountDto.of(TaskStatus.TODO, 4),
                        TaskStatusCountDto.of(TaskStatus.IN_PROGRESS, 2),
                        TaskStatusCountDto.of(TaskStatus.DONE, 4))
        );

        ProgressRatioDto pr = dashboardService.getProgressRatio(userId);
        assertThat(pr.getMyRate()).isEqualTo(75.0);
        assertThat(pr.getTeamRate()).isEqualTo(40.0);
    }

    /* ------------------------------------------------------------------ */
    @Test
    @DisplayName("월별 추세 – 빈 달 채우기")
    void getMonthlyTrend_fillZeros() {
        int year = Year.now().getValue();
        when(taskRepo.fetchFixedMonthlyTrend(year)).thenReturn(
                List.of(MonthlyTaskTrendDto.of(1, 5L, 2L),
                        MonthlyTaskTrendDto.of(2, 3L, 1L),
                        MonthlyTaskTrendDto.of(6,10L, 4L))
        );

        List<MonthlyTaskTrendDto> months = dashboardService.getMonthlyTrend();
        assertThat(months).hasSize(12);
        MonthlyTaskTrendDto march = months.get(2);      // month=3
        assertThat(march.getTotalCount()).isZero();
        assertThat(march.getDoneCount()).isZero();
    }

    /* ------------------------------------------------------------------ */
    @Test
    @DisplayName("최근 활동 피드 5건 조회")
    void getActivityFeed_success() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 18, 12, 0);
        when(feedRepo.fetchFeed(any(), any(), any(Pageable.class)))
                .thenReturn(List.of(
                        ActivityFeedDto.of("Alice", "게시글 작성", now.minusHours(1)),
                        ActivityFeedDto.of("Bob",   "댓글 작성", now.minusMinutes(30))
                ));

        List<ActivityFeedDto> feed = dashboardService.getAcitivityFeed();

        assertThat(feed).hasSize(2);
        ArgumentCaptor<Pageable> cap = ArgumentCaptor.forClass(Pageable.class);
        verify(feedRepo).fetchFeed(any(), any(), cap.capture());
        assertThat(cap.getValue().getPageSize()).isEqualTo(5);
        assertThat(cap.getValue().getSort())
                .isEqualTo(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
