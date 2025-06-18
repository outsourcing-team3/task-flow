package com.example.outsourcingproject.global.aop.aspect;

import com.example.outsourcingproject.domain.auth.dto.response.SigninResponseDto;
import com.example.outsourcingproject.global.aop.annotation.UserActivityLog;
import com.example.outsourcingproject.global.aop.aspect.util.ActivityLogAspectUtil;
import com.example.outsourcingproject.global.aop.event.ActivityLogPublisher;
import com.example.outsourcingproject.global.aop.event.dto.ActivityLogEventDto;
import com.example.outsourcingproject.global.dto.ApiResponse;
import com.example.outsourcingproject.global.enums.ActivityType;
import com.example.outsourcingproject.global.enums.RequestMethod;
import com.example.outsourcingproject.global.enums.TargetType;
import com.example.outsourcingproject.global.security.JwtTokenProvider;
import com.example.outsourcingproject.global.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserActivityLogAspect {

    private final ActivityLogPublisher activityLogPublisher;
    private final HttpServletRequest httpServletRequest;


    @Around("@annotation(userActivityLog)")
    public Object logActivity(ProceedingJoinPoint joinPoint, UserActivityLog userActivityLog) throws Throwable {

        log.info("UserActivityLog aop 실행");

        Object response = joinPoint.proceed();

        Long userId = getUserId(userActivityLog.type(), response);
        if (userId == null) return response;


        ActivityLogEventDto activityLogEventDto = new ActivityLogEventDto(
                userId,
                userActivityLog.type(),
                TargetType.USER,
                userId,
                userActivityLog.type().getMessage1(),
                httpServletRequest.getRemoteAddr(),
                RequestMethod.valueOf(httpServletRequest.getMethod()),
                httpServletRequest.getRequestURI()
        );

        // 이벤트 발행
        activityLogPublisher.publish(activityLogEventDto);

        return response;
    }

    private Long getUserId(ActivityType activityType, Object response) {

        if (activityType.equals(ActivityType.USER_LOGGED_IN)) {
            // 로그인 시 응답 바디에서 id 값 가져오기
            return getUserIdFromResponseDto(response);
        } else {
            // UserPrincipal 에서 id 값 가져오기
            return ActivityLogAspectUtil.getUserIdFromUserPrincipal();
        }
    }

    private Long getUserIdFromResponseDto(Object response) {
        if (
            response instanceof ResponseEntity<?> entity
            && entity.getBody() instanceof ApiResponse<?> apiResponse
            && apiResponse.getData() instanceof SigninResponseDto dto
        ) {
            return dto.getUserId();
        }
        return null;
    }
}
