package com.example.outsourcingproject.global.aop.aspect;

import com.example.outsourcingproject.domain.auth.dto.response.SigninResponseDto;
import com.example.outsourcingproject.domain.task.dto.TaskCreateResponseDto;
import com.example.outsourcingproject.domain.task.dto.TaskReadResponseDto;
import com.example.outsourcingproject.global.aop.annotation.TaskActivityLog;
import com.example.outsourcingproject.global.aop.annotation.UserActivityLog;
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
public class TaskActivityLogAspect {

    private final ActivityLogPublisher activityLogPublisher;
    private final HttpServletRequest httpServletRequest;

    // TODO: 주석 풀기

    @Around("@annotation(taskActivityLog)")
    public Object logActivity(ProceedingJoinPoint joinPoint, TaskActivityLog taskActivityLog) throws Throwable {

        log.info("logActivity aop 실행");

        Object response = joinPoint.proceed();

        Long userId = ActivityLogAspectUtil.getUserIdFromUserPrincipal();
        if (userId == null) return response;

//        Long taskId = getTaskId(taskActivityLog, joinPoint, response);
        Long taskId = null;

//        String message = extractTaskMessage(taskActivityLog, response);
        String message = taskActivityLog.type().getMessage1();


        ActivityLogEventDto activityLogEventDto = new ActivityLogEventDto(
                userId,
                taskActivityLog.type(),
                TargetType.TASK,
                taskId,
                message,
                httpServletRequest.getRemoteAddr(),
                RequestMethod.valueOf(httpServletRequest.getMethod()),
                httpServletRequest.getRequestURI()
        );

        // 이벤트 발행
        activityLogPublisher.publish(activityLogEventDto);

        return response;
    }

//    private String extractTaskMessage(TaskActivityLog taskActivityLog, Object response) {
//        if(taskActivityLog.type().equals(ActivityType.TASK_STATUS_CHANGED)){
//            return generateTaskStatusChangeMessage(response);
//        }
//        return taskActivityLog.type().getMessage1();
//    }
//
//    private String generateTaskStatusChangeMessage(Object response) {
//        if (
//                response instanceof ResponseEntity<?> entity
//                && entity.getBody() instanceof ApiResponse<?> apiResponse
//                && apiResponse.getData() instanceof TaskReadResponseDto dto
//        ) {
//            if(dto.getStatus().equals(TaskStatus.IN_PROGRESS)) {
//                return String.format(ActivityType.TASK_STATUS_CHANGED.getMessage1(), TaskStatus.TODO, TaskStatus.IN_PROGRESS);
//            } else if(dto.getStatus().equals(TaskStatus.DONE)) {
//                return String.format(ActivityType.TASK_STATUS_CHANGED.getMessage1(), TaskStatus.IN_PROGRESS, TaskStatus.DONE);
//            }
//        }
//        return null;
//    }


//    private Long getTaskId(TaskActivityLog taskActivityLog, JoinPoint joinPoint, Object response) {
//        if(taskActivityLog.type().equals(ActivityType.TASK_CREATED)) {
//            return getTaskIdFromResponseDto(response);
//        } else {
//            return getTaskIdFromRequestParams(joinPoint);
//        }
//    }
//
//    private Long getTaskIdFromResponseDto(Object response) {
//        if (
//                response instanceof ResponseEntity<?> entity
//                && entity.getBody() instanceof ApiResponse<?> apiResponse
//                && apiResponse.getData() instanceof TaskCreateResponseDto dto
//        ) {
//            return dto.getId();
//        }
//        return null;
//    }
//
//    private Long getTaskIdFromRequestParams(JoinPoint joinPoint) {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String[] parameterNames = signature.getParameterNames();
//        Object[] args = joinPoint.getArgs();
//
//        for (int i = 0; i < parameterNames.length; i++) {
//            if ("taskId".equals(parameterNames[i]) && args[i] instanceof Long longArg) {
//                return longArg;
//            }
//        }
//        return null;
//    }
}
