package com.example.outsourcingproject.domain.dashboard.repository;


import com.example.outsourcingproject.domain.dashboard.dto.TaskStatusCountDto;
import com.example.outsourcingproject.domain.dashboard.dto.TodayTaskItemDto;
import com.example.outsourcingproject.domain.dashboard.projection.DailyTaskTrendProjection;
import com.example.outsourcingproject.domain.dashboard.projection.MonthlyTaskTrendProjection;
import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TaskStatisticsRepository extends JpaRepository<Task, Long> {


    //기간 내 전체 task 개수
    @Query("""
            SELECT COUNT(t)
            FROM Task t
            WHERE t.isDeleted = false
            AND t.createdAt BETWEEN :from AND :to
            
            """
    )
    long countByCreatedAtBetween(@Param("from") LocalDateTime from,
                                 @Param("to") LocalDateTime to);


    //기간+상태별 task 수

    @Query("""
            SELECT COUNT(t)
            FROM Task t
            WHERE t.status IN :status
            AND t.isDeleted = false 
            AND t.createdAt BETWEEN :from AND :to
            """)
    long countByStatusAndPeriod(@Param("status") TaskStatus status,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);


    @Query("""
            SELECT new com.example.outsourcingproject.domain.dashboard.dto.TodayTaskItemDto(
                    t.id, t.title, t.status, t.priority, t.deadline)
            FROM Task t
            WHERE t.assignee.id = :userId
             AND t.isDeleted = false
             AND t.status IN (:statuses)
             AND t.deadline BETWEEN :start AND :end
             AND t.deadline >= :now
            ORDER BY t.deadline ASC 
            """)
    List<TodayTaskItemDto> findTodayTasks(
            @Param("userId") Long userId,
            @Param("statuses") List<TaskStatus> statuses,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("now") LocalDateTime now, // overdue 제외
            Pageable pageable
    );


//
//    @Query("""
//            SELECT t
//            FROM Task t
//            WHERE t.assigneeId = :userId
//            AND DATE(t.deadline) = :date
//            AND t.status IN (:statuses)
//            AND t.isDeleted = false
//                        """)
//    List<Task> findAllByUserIdAndDate(@Param("userId") Long userId,
//                                      @Param("date") LocalDate date,
//                                      @Param("statuses") List<TaskStatus> statuses);


    @Query("""
                SELECT COUNT(t)
                FROM Task t
                WHERE t.status IN (:statuses)
                  AND t.deadline < :now
                  AND t.deadline >= t.createdAt
                  AND t.createdAt BETWEEN :from AND :to
                  AND t.isDeleted = false
            """)
    long countOverdueTasksInPeriod(@Param("statuses") List<TaskStatus> statuses,
                                   @Param("now") LocalDateTime now,
                                   @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to);


    /*최근 7일간 날짜 별 태스크 생성 갯수 및 완료 수
    일별 작업 트렌드 그래프 용 (createdAt 기준)*/
    @Query(value = """
                SELECT DATE(t.created_at) AS date,
                       COUNT(*) AS totalCount,
                       SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) AS doneCount
                FROM tasks t
                WHERE t.created_at BETWEEN :start AND :end
                  AND t.is_deleted = false
                GROUP BY DATE(t.created_at)
                ORDER BY DATE(t.created_at)
            """, nativeQuery = true)
    List<DailyTaskTrendProjection> fetchDailyTrend(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);

    /*전체 태스크 상태별 개수 통계
    전체 비율 통계 계산용*/
    @Query("""
                SELECT new com.example.outsourcingproject.domain.dashboard.dto.TaskStatusCountDto(
                    t.status,
                    COUNT(t)
                )
                FROM Task t
                WHERE t.isDeleted = false
                GROUP BY t.status
            """)
    List<TaskStatusCountDto> countGroupByStatus();


    //특정 유저(로그인 한 사용자)의 상태별 태스크 개수(개인 비율 계산용)
    @Query("""
                     SELECT new com.example.outsourcingproject.domain.dashboard.dto.TaskStatusCountDto(
                                t.status, COUNT(t))
                     FROM Task t
                     WHERE t.assignee.id = :userId
                     AND t.isDeleted = false
                     GROUP BY t.status
            """)
    List<TaskStatusCountDto> countMyStatus(@Param("userId") Long userId);


    //전체 팀의 상태별 태스크 개수 (팀 비율 계산용)
    @Query("""
                SELECT new com.example.outsourcingproject.domain.dashboard.dto.TaskStatusCountDto(
                       t.status,
                       COUNT(t))
                  FROM Task t
                 WHERE t.isDeleted = false
                 GROUP BY t.status
            """)
    List<TaskStatusCountDto> countTeamStatus();


    /*특정 연도에 월 단위로 생성도니 태스크 개수 및 완료 개수
    월간 그래프 용*/
    @Query(value = """
                SELECT MONTH(t.created_at) AS month,
                       COUNT(*) AS totalCount,
                       SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) AS doneCount
                FROM tasks t
                WHERE YEAR(t.created_at) = :year
                  AND t.is_deleted = false
                GROUP BY MONTH(t.created_at)
                ORDER BY MONTH(t.created_at)
            """, nativeQuery = true)
    List<MonthlyTaskTrendProjection> fetchFixedMonthlyTrend(@Param("year") int year);


}
