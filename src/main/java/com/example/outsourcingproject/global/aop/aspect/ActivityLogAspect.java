package com.example.outsourcingproject.global.aop.aspect;

import com.example.outsourcingproject.global.aop.annotation.LogActivity;
import com.example.outsourcingproject.global.aop.event.ActivityLogPublisher;
import com.example.outsourcingproject.global.aop.event.dto.ActivityLogEventDto;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

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

        // 로그인 시 응답 헤더에서 id 추출
        if(logActivity.type().equals(ActivityType.USER_LOGGED_IN)) {
            HttpHeaders headers = result.getHeaders();
            String bearerToken = headers.getFirst("Authorization");
            String token = Objects.requireNonNull(bearerToken).substring(7);

            Claims claims = jwtTokenProvider.parseToken(token);
            userId = Long.parseLong(claims.getSubject());
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
