package com.example.outsourcingproject.domain.activitylog.unit;

import com.example.outsourcingproject.domain.activitylog.domain.model.ActivityLog;
import com.example.outsourcingproject.domain.activitylog.domain.repository.ActivityLogRepository;
import com.example.outsourcingproject.domain.auth.entity.Auth;
import com.example.outsourcingproject.domain.auth.enums.UserRole;
import com.example.outsourcingproject.domain.auth.repository.AuthRepository;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import com.example.outsourcingproject.global.config.JpaConfig;
import com.example.outsourcingproject.global.enums.ActivityType;
import com.example.outsourcingproject.global.enums.RequestMethod;
import com.example.outsourcingproject.global.enums.TargetType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Slf4j
@DataJpaTest
@Import(JpaConfig.class)
public class ActivityLogRepositoryTest {

    @Autowired
    ActivityLogRepository activityLogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthRepository authRepository;

    @Test
    void 로그_저장_테스트() {

        // given
        Auth auth = new Auth("홍길동", "test1234", "test@gmail.com", "test1234", UserRole.USER);
        auth = authRepository.save(auth);

        User user = new User(auth.getId(), auth.getName(), auth.getEmail());
        user = userRepository.save(user);

        ActivityLog activityLog = new ActivityLog(
                user,
                ActivityType.USER_LOGGED_IN,
                TargetType.USER,
                "로그인 완료",
                "127.0.0.1",
                RequestMethod.POST,
                "/api/v1/auth/login"
        );

        // when
        ActivityLog savedActivityLog = activityLogRepository.save(activityLog);

        //then
        Assertions.assertThat(savedActivityLog.getId()).isNotNull();
    }

}
