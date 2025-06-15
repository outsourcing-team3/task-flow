package com.example.outsourcingproject.domain.user.service;

import com.example.outsourcingproject.domain.auth.event.UserRegisteredEvent;
import com.example.outsourcingproject.domain.auth.event.UserWithdrawnEvent;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final UserRepository userRepository;

    @EventListener
    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("사용자 등록 이벤트 처리: userId={}, name={}", event.getUserId(), event.getName());

        User user = new User(event.getUserId(), event.getName(), event.getEmail());
        userRepository.save(user);

        log.info("사용자 프로필 생성 완료: userId={}", event.getUserId());
    }

    @EventListener
    @Transactional
    public void handleUserWithdrawn(UserWithdrawnEvent event) {
        log.info("사용자 탈퇴 이벤트 처리: userId={}", event.getUserId());

        userRepository.findByIdAndIsDeletedFalse(event.getUserId())
                .ifPresent(user -> {
                    user.delete();
                    userRepository.save(user);
                    log.info("사용자 프로필 삭제 완료: userId={}", event.getUserId());
                });
    }
}
