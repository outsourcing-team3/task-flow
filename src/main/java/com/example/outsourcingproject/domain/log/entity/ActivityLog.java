package com.example.outsourcingproject.domain.log.entity;

import com.example.outsourcingproject.domain.log.enums.ActivityType;
import com.example.outsourcingproject.domain.log.enums.TargetType;
import com.example.outsourcingproject.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityLog {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", length = 50)
    private TargetType targetType;

    private Long targetId;
    private String message;

    private String requestIp;
    private String requestMethod;
    private String requestUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 정적 팩토리 메서드
    public static ActivityLog of(User user,
                                 ActivityType activityType,
                                 TargetType targetType,
                                 Long targetId,
                                 String message,
                                 HttpServletRequest req) {
        return new ActivityLog(
                user,
                activityType,
                targetType,
                targetId,
                message,
                req.getRemoteAddr(),
                req.getMethod(),
                req.getRequestURI(),
                LocalDateTime.now().withNano(0)
        );
    }


    public ActivityLog(User user,
                       ActivityType activityType,
                       TargetType targetType,
                       Long targetId,
                       String message,
                       String requestIp,
                       String requestMethod,
                       String requestUrl,
                       LocalDateTime createdAt) {
        this.user = user;
        this.activityType = activityType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.message = message;
        this.requestIp = requestIp;
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.createdAt = createdAt;
    }
}
