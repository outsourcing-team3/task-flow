package com.example.outsourcingproject.domain.dashboard.service;

import com.example.outsourcingproject.domain.dashboard.dto.DashboardStatisticsDto;
import com.example.outsourcingproject.domain.dashboard.repository.AcitivityFeedRepository;
import com.example.outsourcingproject.domain.dashboard.repository.TaskStatisticsRepository;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {


    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private TaskStatisticsRepository taskStatisticsRepository;

    @Mock
    private AcitivityFeedRepository acitivityFeedRepository;



    @Test
    void 주간증가율_성공(){

        //given
        LocalDate targetDate = LocalDate.of(2025, 6, 17);   // 화요일
        LocalDateTime weekStart    = LocalDate.of(2025, 6, 16).atStartOfDay(); // 이번 주 월 00:00
        LocalDateTime weekEnd      = LocalDate.of(2025, 6, 23).atStartOfDay(); // 다음 주 월 00:00
        LocalDateTime prevWeekStart = weekStart.minusWeeks(1);                 // 전주 월 00:00
        LocalDateTime prevWeekEnd   = weekStart;                               // 전주 일 24:00


        //이번 주
        when(taskStatisticsRepository.countByCreatedAtBetween(weekStart, weekEnd)).thenReturn(10L);
        when(taskStatisticsRepository.countByStatusAndPeriod(TaskStatus.TODO,        weekStart, weekEnd)).thenReturn(3L);
        when(taskStatisticsRepository.countByStatusAndPeriod(TaskStatus.IN_PROGRESS, weekStart, weekEnd)).thenReturn(4L);
        when(taskStatisticsRepository.countByStatusAndPeriod(TaskStatus.DONE,        weekStart, weekEnd)).thenReturn(3L);

        // 전 주
        when(taskStatisticsRepository.countByCreatedAtBetween(prevWeekStart, prevWeekEnd)).thenReturn(5L);

        // 기한 초과(현재 시각 이전 마감)
        when(taskStatisticsRepository.countOverdueTasks(anyList(), any(LocalDateTime.class))).thenReturn(2L);

        //when
        DashboardStatisticsDto dto = dashboardService.getWeeklyStatistics(targetDate);

        //then
        //1. 비율 계산 확인

        assertThat(dto.getCompletionRate()).isEqualTo(30.0);      // 3 / 10
        assertThat(dto.getInProgressRate()).isEqualTo(40.0);      // 4 / 10
        assertThat(dto.getOverdueRate()).isEqualTo(20.0);         // 2 / 10



        //2. 주간 증감률 = ((10-5)/5)*100
        assertThat(dto.getWeeklyChangeRate()).isEqualTo(100.0);

        //3. 총계 등 기본 필드
        assertThat(dto.getTotalCount()).isEqualTo(10L);
        assertThat(dto.getDoneCount()).isEqualTo(3L);
        assertThat(dto.getInProgressCount()).isEqualTo(4L);
        assertThat(dto.getTodoCount()).isEqualTo(3L);
        assertThat(dto.getOverdueCount()).isEqualTo(2L);


        //리포지토리 호출 파라미터 검증
        ArgumentCaptor<LocalDateTime> fromCap = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> toCap   = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(taskStatisticsRepository, times(2))
                .countByCreatedAtBetween(fromCap.capture(), toCap.capture());

        // 각각의 호출에서 전달된 인자를 가져옴
        List<LocalDateTime> capturedFrom = fromCap.getAllValues();
        List<LocalDateTime> capturedTo   = toCap.getAllValues();

        // 첫 번째 호출: 이번 주
        assertThat(capturedFrom.get(0)).isEqualTo(weekStart);
        assertThat(capturedTo.get(0)).isEqualTo(weekEnd);

        // 두 번째 호출: 지난 주
        assertThat(capturedFrom.get(1)).isEqualTo(prevWeekStart);
        assertThat(capturedTo.get(1)).isEqualTo(prevWeekEnd);
    }

}
