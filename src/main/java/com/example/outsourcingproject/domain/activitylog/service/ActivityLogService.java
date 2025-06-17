package com.example.outsourcingproject.domain.activitylog.service;

import com.example.outsourcingproject.domain.activitylog.controller.dto.ActivityLogResponseDto;
import com.example.outsourcingproject.domain.activitylog.controller.dto.ActivityTypeResponseDto;
import com.example.outsourcingproject.domain.activitylog.controller.dto.TargetTypeResponseDto;
import com.example.outsourcingproject.domain.activitylog.controller.dto.UserResponseDto;
import com.example.outsourcingproject.domain.activitylog.domain.model.ActivityLog;
import com.example.outsourcingproject.domain.activitylog.domain.repository.ActivityLogRepository;
import com.example.outsourcingproject.domain.activitylog.service.dto.FindAllOptionDto;
import com.example.outsourcingproject.domain.auth.exception.UserNotFoundException;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import com.example.outsourcingproject.global.enums.ActivityType;
import com.example.outsourcingproject.global.enums.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public List<ActivityLogResponseDto> findAll(FindAllOptionDto option) {

        if (option.getUserId() != null) {
            userRepository.findById(option.getUserId()).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        }

        ActivityType activityType = ActivityType.of(option.getActivityType()).orElse(null);
        TargetType targetType = TargetType.fromId(option.getTargetId()).orElse(null);

        LocalDateTime startDateTime = option.getStartDate() != null ? option.getStartDate().atStartOfDay() : null;
        LocalDateTime endDateTime = option.getEndDate() != null ? option.getEndDate().atTime(LocalTime.MAX) : null;

        List<ActivityLog> activityLogs = activityLogRepository.findActivityLogs(
                option.getUserId(),
                activityType,
                targetType,
                startDateTime,
                endDateTime,
                option.getPageable()
        );

        return activityLogs.stream()
                .map(activityLog -> new ActivityLogResponseDto(
                        activityLog.getId(),
                        new UserResponseDto(activityLog.getUser()),
                        new ActivityTypeResponseDto(activityLog.getActivityType()),
                        new TargetTypeResponseDto(activityLog.getTargetType()),
                        activityLog.getRequestIp(),
                        activityLog.getRequestMethod().toString(),
                        activityLog.getRequestUrl(),
                        activityLog.getCreatedAt()
                    )
                )
                .toList();
    }
}
