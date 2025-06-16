package com.example.outsourcingproject.global.aop.aspect;

import com.example.outsourcingproject.global.aop.annotation.LogActivity;
import com.example.outsourcingproject.global.aop.event.ActivityLogPublisher;
import com.example.outsourcingproject.global.aop.event.dto.ActivityLogEventDto;
import com.example.outsourcingproject.global.enums.RequestMethod;
import com.example.outsourcingproject.global.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLogAspect {

    private final ActivityLogPublisher activityLogPublisher;

    private final HttpServletRequest httpServletRequest;

    @Around("@annotation(logActivity)")
    public Object logActivity(ProceedingJoinPoint joinPoint, LogActivity logActivity) throws Throwable {

        Object result = joinPoint.proceed();


        // userId
        Long userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal) {
                userId = userPrincipal.getId();
            }
        }

        if(userId == null) {
            // 인증 없는 요청이면 일단 넘김
            return result;
        }

        ActivityLogEventDto activityLogEventDto = new ActivityLogEventDto(
                userId,
                logActivity.type(),
                logActivity.target(),
                httpServletRequest.getRemoteAddr(),
                RequestMethod.valueOf(httpServletRequest.getMethod()),
                httpServletRequest.getRequestURI()
        );

        // 이벤트 발행
        activityLogPublisher.publish(activityLogEventDto);

        return result;
    }
}
