package com.example.outsourcingproject.global.aop.aspect;

import com.example.outsourcingproject.global.aop.annotation.ActivityLog;
import com.example.outsourcingproject.global.aop.aspect.strategy.factory.ActivityLogStrategyFactory;
import com.example.outsourcingproject.global.aop.aspect.strategy.ActivityLogStrategy;
import com.example.outsourcingproject.global.aop.event.ActivityLogPublisher;
import com.example.outsourcingproject.global.aop.event.dto.ActivityLogEventDto;
import com.example.outsourcingproject.global.enums.RequestMethod;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLogAspect {

    private final ActivityLogPublisher activityLogPublisher;
    private final HttpServletRequest httpServletRequest;

    private final ActivityLogStrategyFactory activityLogStrategyFactory;

    @Around("@annotation(activityLog)")
    public Object logActivity(ProceedingJoinPoint joinPoint, ActivityLog activityLog) throws Throwable {

        log.info("ActivityLog aop 실행");

        Object response = joinPoint.proceed();

        ActivityLogStrategy strategy = activityLogStrategyFactory.create(activityLog.target(), joinPoint, activityLog, response);

        Long userId = strategy.getUserId();
        if (userId == null) return response;

        Long targetId = strategy.getTargetId();
        String message = strategy.getMessage();

        ActivityLogEventDto activityLogEventDto = new ActivityLogEventDto(
                userId,
                activityLog.type(),
                activityLog.target(),
                targetId,
                message,
                httpServletRequest.getRemoteAddr(),
                RequestMethod.valueOf(httpServletRequest.getMethod()),
                httpServletRequest.getRequestURI()
        );

        // 이벤트 발행
        activityLogPublisher.publish(activityLogEventDto);

        return response;
    }




}
