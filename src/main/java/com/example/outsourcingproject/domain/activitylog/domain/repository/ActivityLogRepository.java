package com.example.outsourcingproject.domain.activitylog.domain.repository;

import com.example.outsourcingproject.domain.activitylog.domain.model.ActivityLog;
import com.example.outsourcingproject.global.enums.ActivityType;
import com.example.outsourcingproject.global.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @Query("""
        SELECT a FROM ActivityLog a
        WHERE (:userId IS NULL OR a.auth.id = :userId)
          AND (:activityType IS NULL OR a.activityType = :activityType)
          AND (:targetType IS NULL OR a.targetType = :targetType)
          AND (:startDate IS NULL OR a.createdAt >= :startDate)
          AND (:endDate IS NULL OR a.createdAt <= :endDate)
        ORDER BY a.createdAt DESC
    """)
    Page<ActivityLog> findActivityLogs(
            @Param("userId") Long userId,
            @Param("activityType") ActivityType activityType,
            @Param("targetType") TargetType targetType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
