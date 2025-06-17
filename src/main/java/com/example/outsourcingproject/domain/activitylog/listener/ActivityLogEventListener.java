package com.example.outsourcingproject.domain.activitylog.listener;

import com.example.outsourcingproject.domain.activitylog.domain.model.ActivityLog;
import com.example.outsourcingproject.domain.activitylog.domain.repository.ActivityLogRepository;
import com.example.outsourcingproject.domain.auth.exception.UserNotFoundException;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import com.example.outsourcingproject.global.aop.event.dto.ActivityLogEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityLogEventListener {
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    @Async
    @EventListener
    public void handleActivityLogEvent(ActivityLogEventDto event) {

        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 엔티티로 변환
        ActivityLog activityLog = new ActivityLog(
                user,
                event.getActivityType(),
                event.getTargetType(),
                "메세지",
                event.getRequestIp(),
                event.getRequestMethod(),
                event.getRequestUrl()
        );

        // DB 저장
        activityLogRepository.save(activityLog);
    }
}
