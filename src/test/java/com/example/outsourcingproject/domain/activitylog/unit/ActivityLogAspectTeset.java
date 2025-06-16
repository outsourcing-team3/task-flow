package com.example.outsourcingproject.domain.activitylog.unit;

import com.example.outsourcingproject.domain.auth.enums.UserRole;
import com.example.outsourcingproject.global.aop.annotation.LogActivity;
import com.example.outsourcingproject.global.aop.aspect.ActivityLogAspect;
import com.example.outsourcingproject.global.aop.event.ActivityLogPublisher;
import com.example.outsourcingproject.global.aop.event.dto.ActivityLogEventDto;
import com.example.outsourcingproject.global.enums.ActivityType;
import com.example.outsourcingproject.global.enums.TargetType;
import com.example.outsourcingproject.global.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import com.example.outsourcingproject.global.enums.RequestMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityLogAspectTest {

    @Mock
    private ActivityLogPublisher activityLogPublisher;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private ActivityLogAspect activityLogAspect;

    @Test
    void 로그_정상_발행_테스트() throws Throwable {
        // given
        LogActivity logActivity = mock(LogActivity.class);
        when(logActivity.type()).thenReturn(ActivityType.USER_LOGGED_IN);
        when(logActivity.target()).thenReturn(TargetType.USER);

        // HttpServletRequest Mock
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/v1/auth/login");

        // 인증 Mock
        UserPrincipal mockUserPrincipal = UserPrincipal.builder()
                .id(Long.parseLong("1"))
                .email("email")
                .role(UserRole.of("USER"))
                .build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(mockUserPrincipal);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // joinPoint Mock
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Object expectedResult = "result";
        when(joinPoint.proceed()).thenReturn(expectedResult);

        // when
        Object actualResult = activityLogAspect.logActivity(joinPoint, logActivity);

        // then
        assertEquals(expectedResult, actualResult);

        ArgumentCaptor<ActivityLogEventDto> captor = ArgumentCaptor.forClass(ActivityLogEventDto.class);
        verify(activityLogPublisher, times(1)).publish(captor.capture());

        ActivityLogEventDto eventDto = captor.getValue();
        assertEquals(1L, eventDto.getUserId());
        assertEquals(ActivityType.USER_LOGGED_IN, eventDto.getActivityType());
        assertEquals(TargetType.USER, eventDto.getTargetType());
        assertEquals("127.0.0.1", eventDto.getRequestIp());
        assertEquals(RequestMethod.POST, eventDto.getRequestMethod());
        assertEquals("/api/v1/auth/login", eventDto.getRequestUrl());
    }

    @Test
    void 인증없으면_이벤트_발행_안함() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("result");

        LogActivity logActivity = mock(LogActivity.class);

        // 인증 정보 없음
        SecurityContextHolder.clearContext();

        // when
        Object result = activityLogAspect.logActivity(joinPoint, logActivity);

        // then
        assertEquals("result", result);
        verify(activityLogPublisher, never()).publish(any());
    }
}

