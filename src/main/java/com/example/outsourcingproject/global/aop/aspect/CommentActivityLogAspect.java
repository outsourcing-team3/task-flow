package com.example.outsourcingproject.global.aop.aspect;

import com.example.outsourcingproject.domain.task.dto.TaskCreateResponseDto;
import com.example.outsourcingproject.global.aop.annotation.CommentActivityLog;
import com.example.outsourcingproject.global.aop.annotation.TaskActivityLog;
import com.example.outsourcingproject.global.aop.aspect.util.ActivityLogAspectUtil;
import com.example.outsourcingproject.global.aop.event.ActivityLogPublisher;
import com.example.outsourcingproject.global.aop.event.dto.ActivityLogEventDto;
import com.example.outsourcingproject.global.dto.ApiResponse;
import com.example.outsourcingproject.global.enums.ActivityType;
import com.example.outsourcingproject.global.enums.RequestMethod;
import com.example.outsourcingproject.global.enums.TargetType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommentActivityLogAspect {

    private final ActivityLogPublisher activityLogPublisher;
    private final HttpServletRequest httpServletRequest;

    // TODO: 주석 풀기

    @Around("@annotation(commentActivityLog)")
    public Object logActivity(ProceedingJoinPoint joinPoint, CommentActivityLog commentActivityLog) throws Throwable {

        log.info("logActivity aop 실행");

        Object response = joinPoint.proceed();

        Long userId = ActivityLogAspectUtil.getUserIdFromUserPrincipal();
        if (userId == null) return response;


        Long commentId = null;

        ActivityLogEventDto activityLogEventDto = new ActivityLogEventDto(
                userId,
                commentActivityLog.type(),
                TargetType.COMMENT,
                commentId,
                commentActivityLog.type().getMessage1(),
                httpServletRequest.getRemoteAddr(),
                RequestMethod.valueOf(httpServletRequest.getMethod()),
                httpServletRequest.getRequestURI()
        );

        // 이벤트 발행
        activityLogPublisher.publish(activityLogEventDto);

        return response;
    }



}
