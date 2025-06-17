package com.example.outsourcingproject.global.aop.aspect;

import com.example.outsourcingproject.domain.auth.dto.response.SigninResponseDto;
import com.example.outsourcingproject.global.aop.annotation.LogActivity;
import com.example.outsourcingproject.global.aop.event.ActivityLogPublisher;
import com.example.outsourcingproject.global.aop.event.dto.ActivityLogEventDto;
import com.example.outsourcingproject.global.dto.ApiResponse;
import com.example.outsourcingproject.global.enums.ActivityType;
import com.example.outsourcingproject.global.enums.RequestMethod;
import com.example.outsourcingproject.global.security.JwtTokenProvider;
import com.example.outsourcingproject.global.security.UserPrincipal;
import io.jsonwebtoken.Claims;
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
public class ActivityLogAspect {

    private final ActivityLogPublisher activityLogPublisher;

    private final HttpServletRequest httpServletRequest;

    private final JwtTokenProvider jwtTokenProvider;

    @Around("@annotation(logActivity)")
    public Object logActivity(ProceedingJoinPoint joinPoint, LogActivity logActivity) throws Throwable {

        log.info("logActivity aop 실행");

        ResponseEntity<?> result = (ResponseEntity<?>) joinPoint.proceed();

        // userId
        Long userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal) {
                userId = userPrincipal.getId();
            }
        }

        // 로그인 시 응답 바디에서 id 값 가져오기
        if(logActivity.type().equals(ActivityType.USER_LOGGED_IN)) {
            Object body = result.getBody();
            if (body instanceof ApiResponse<?> apiResponse) {
                Object data = apiResponse.getData();
                if(data instanceof SigninResponseDto signinResponseDto) {
                    userId = signinResponseDto.getUserId();
                }
            }
        }

        if(userId == null) {
            // 인증 없는 요청이면 일단 넘김
            return result;
        }

        String message = null;
        // TODO: Task 상태 변경 시 로직 추가 필요
//        if(logActivity.type().equals(ActivityType.TASK_STATUS_CHANGED){
//
//            message = String.format(ActivityType.TASK_STATUS_CHANGED.getMessage1(), )
//        } else {
//
//        }

        ActivityLogEventDto activityLogEventDto = new ActivityLogEventDto(
                userId,
                logActivity.type(),
                logActivity.target(),
                logActivity.type().getMessage1(),
                httpServletRequest.getRemoteAddr(),
                RequestMethod.valueOf(httpServletRequest.getMethod()),
                httpServletRequest.getRequestURI()
        );

        // 이벤트 발행
        activityLogPublisher.publish(activityLogEventDto);

        return result;
    }
}
