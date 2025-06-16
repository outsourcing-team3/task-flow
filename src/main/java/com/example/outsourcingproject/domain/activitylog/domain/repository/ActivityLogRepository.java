package com.example.outsourcingproject.domain.activitylog.domain.repository;

import com.example.outsourcingproject.domain.activitylog.domain.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

}
