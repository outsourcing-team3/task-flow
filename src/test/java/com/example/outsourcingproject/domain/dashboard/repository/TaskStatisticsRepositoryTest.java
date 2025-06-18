package com.example.outsourcingproject.domain.dashboard.repository;

import com.example.outsourcingproject.domain.dashboard.dto.DailyTaskTrendDto;
import com.example.outsourcingproject.domain.dashboard.dto.TodayTaskItemDto;
import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.enums.Priority;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest      // JPA 컴포넌트만 로드 (Service, Controller 등 제외)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // h2 디폴트를 쓰려면 생략 가능
class TaskStatisticsRepositoryTest {

    @Autowired TaskStatisticsRepository repo;
    @Autowired EntityManager em;                     // 필요 시 flush / clear 제어용

    @Test
    @DisplayName("일별 트렌드 집계 – createdAt 기준")
    void fetchDailyTrend_success() {
        LocalDate base = LocalDate.of(2025, 6, 1);

        // 6/1에 5건
        for (int i = 0; i < 5; i++) {
            repo.save(task("d1-" + i, TaskStatus.TODO, base.atTime(9, 0)));
        }

        // 6/2에 2건 (1건 완료됨)
        repo.save(task("d2-1", TaskStatus.DONE, base.plusDays(1).atTime(9, 0)));
        repo.save(task("d2-2", TaskStatus.TODO, base.plusDays(1).atTime(10, 0)));

        List<DailyTaskTrendDto> trend = repo.fetchDailyTrend(
                base.atStartOfDay(),
                base.plusDays(2).atStartOfDay()
        );

        // 검증
        assertThat(trend).hasSize(2);

        // 첫 번째 날짜 (6/1)
        DailyTaskTrendDto day1 = trend.get(0);
        assertThat(day1.getDate()).isEqualTo(base); // 6/1
        assertThat(day1.getTotalCount()).isEqualTo(5);
        assertThat(day1.getDoneCount()).isEqualTo(0);

        // 두 번째 날짜 (6/2)
        DailyTaskTrendDto day2 = trend.get(1);
        assertThat(day2.getDate()).isEqualTo(base.plusDays(1)); // 6/2
        assertThat(day2.getTotalCount()).isEqualTo(2);
        assertThat(day2.getDoneCount()).isEqualTo(1);}

        private Task task(String title, TaskStatus status, LocalDateTime createdAt) {
            return new Task(
                    title,
                    Priority.LOW,
                    status,
                    1L,
                    1L,
                    createdAt.plusDays(3),
                    createdAt
            );
    }

}
