package com.example.outsourcingproject.domain.activitylog.unit;

import com.example.outsourcingproject.domain.activitylog.domain.model.ActivityLog;
import com.example.outsourcingproject.domain.activitylog.domain.repository.ActivityLogRepository;
import com.example.outsourcingproject.domain.activitylog.listener.ActivityLogEventListener;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import com.example.outsourcingproject.global.aop.event.dto.ActivityLogEventDto;
import com.example.outsourcingproject.global.enums.ActivityType;
import com.example.outsourcingproject.global.enums.RequestMethod;
import com.example.outsourcingproject.global.enums.TargetType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActivityLogEventListenerTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ActivityLogRepository activityLogRepository;

    @InjectMocks
    ActivityLogEventListener listener;

    @Test
    void ActivityLogEventListener_정상동작() {
        // given
        Long userId = 1L;
        ActivityLogEventDto event = new ActivityLogEventDto(
                userId,
                ActivityType.USER_LOGGED_IN,
                TargetType.USER,
                123123L,
                "테스트",
                "127.0.0.1",
                RequestMethod.GET,
                "/api/test"
        );

        User user = new User(
                1L,
                "홍길동",
                "test@gmail.com"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(activityLogRepository.save(any(ActivityLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        listener.handleActivityLogEvent(event);

        // then
        verify(userRepository).findById(userId);
        verify(activityLogRepository).save(any(ActivityLog.class));
    }
}
