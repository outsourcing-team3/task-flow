package com.example.outsourcingproject.domain.dashboard.repository;

import com.example.outsourcingproject.domain.dashboard.dto.DailyTaskTrendDto;
import com.example.outsourcingproject.domain.dashboard.dto.MonthlyTaskTrendDto;
import com.example.outsourcingproject.domain.dashboard.dto.TaskStatusCountDto;
import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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


    //특정 유저의 해당 날짜 마감인 태스크 목록 조회
    @Query("""
            SELECT t 
            FROM Task t 
            WHERE t.assigneeId = :userId 
            AND DATE(t.deadline) = :date 
            AND t.status IN (:statuses)
            AND t.isDeleted = false
                        """)
    List<Task> findAllByUserIdAndDate(@Param("userId") Long userId,
                                      @Param("date") LocalDate date,
                                      @Param("statuses") List<TaskStatus> statuses);


    //마감기한이 지난 작업 개수
    @Query("""
                SELECT COUNT(t)
                FROM Task t
                WHERE t.status IN (:statuses)
                  AND t.deadline < :now
                  AND t.isDeleted = false
            """)
    long countOverdueTasks(@Param("statuses") List<TaskStatus> statuses, @Param("now") LocalDateTime now);


    /*최근 7일간 날짜 별 태스크 생성 갯수 및 완료 수
    일별 작업 트렌드 그래프 용 (createdAt 기준)*/
    @Query("""
                SELECT new com.example.outsourcingproject.domain.dashboard.dto.DailyTaskTrendDto(
                 DATE(t.createdAt),      
                 COUNT(t),              
                 SUM(CASE WHEN t.status ='DONE' THEN 1L ELSE 0L END)
                 )
                 FROM Task t
                 WHERE t.createdAt BETWEEN :start AND :end
                    AND t.isDeleted = false
                    GROUP BY DATE(t.createdAt)
                    ORDER BY DATE(t.createdAt)
            """)
    List<DailyTaskTrendDto> fetchDailyTrend(@Param("start") LocalDateTime start,
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
                     WHERE t.assigneeId = :userId
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
    @Query(
            """
                    SELECT new com.example.outsourcingproject.domain.dashboard.dto.MonthlyTaskTrendDto(
                            MONTH(t.createdAt),
                            COUNT(t),
                            SUM(CASE WHEN t.status = 'DONE' THEN 1L ELSE 0L END)
                            )
                            FROM Task t
                            WHERE YEAR(t.createdAt) = :year
                            AND t.isDeleted = false
                            GROUP BY MONTH(t.createdAt)
                            ORDER BY MONTH(t.createdAt)
                    
                    """)
    List<MonthlyTaskTrendDto> fetchFixedMonthlyTrend(@Param("year") int year);


}
