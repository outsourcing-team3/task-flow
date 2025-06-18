package com.example.outsourcingproject.domain.dashboard.repository;

import com.example.outsourcingproject.domain.dashboard.dto.ActivityFeedDto;
import com.example.outsourcingproject.domain.log.entity.ActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AcitivityFeedRepository extends JpaRepository<ActivityLog, Long> {


    @Query("""
            SELECT new com.example.outsourcingproject.domain.dashboard.dto.ActivityFeedDto(
                  u.name,
                  al.message,
                  al.createdAt)
             FROM ActivityLog al
            JOIN al.user u
            WHERE al.createdAt BETWEEN :from AND :to
             AND u.isDeleted = false
            """)
    List<ActivityFeedDto> fetchFeed(@Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to,
                                             Pageable pageable);

}
