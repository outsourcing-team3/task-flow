package com.example.outsourcingproject.domain.auth.service;

import com.example.outsourcingproject.domain.auth.dto.request.SignupRequestDto;
import com.example.outsourcingproject.domain.auth.dto.response.SignupResponseDto;
import com.example.outsourcingproject.domain.auth.entity.Auth;
import com.example.outsourcingproject.domain.auth.enums.UserRole;
import com.example.outsourcingproject.domain.auth.event.UserRegisteredEvent;
import com.example.outsourcingproject.domain.auth.repository.AuthRepository;
import com.example.outsourcingproject.global.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                authRepository,
                passwordEncoder,
                jwtTokenProvider,
                refreshTokenService,
                tokenBlacklistService,
                loginAttemptService,
                eventPublisher
        );
    }

    private void setId(Auth auth) {
        try {
            java.lang.reflect.Field idField = Auth.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(auth, 1L);
        } catch (Exception e) {
            throw new RuntimeException("ID 설정 실패", e);
        }
    }

    @DisplayName("회원가입 성공 테스트")
    @Test
    void signup_Success() {
        // given
        SignupRequestDto signupRequest = new SignupRequestDto(
                "차준호",
                "Juno",
                "ckwnsgh@example.com",
                "Password123!"
        );

        given(authRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
        given(authRepository.existsByUsername(signupRequest.getUsername())).willReturn(false);
        given(passwordEncoder.encode(signupRequest.getPassword())).willReturn("encodedPassword");

        Auth savedAuth = new Auth(
                signupRequest.getName(),
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                "encodedPassword",
                UserRole.USER
        );

        setId(savedAuth);

        given(authRepository.save(any(Auth.class))).willReturn(savedAuth);

        // when
        SignupResponseDto result = authService.signup(signupRequest);

        // then
        assertThat(result.getUsername()).isEqualTo(signupRequest.getUsername());
        assertThat(result.getEmail()).isEqualTo(signupRequest.getEmail());

        // 이벤트 발행 검증
        ArgumentCaptor<UserRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        UserRegisteredEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.getUserId()).isEqualTo(1L);
        assertThat(publishedEvent.getName()).isEqualTo(signupRequest.getName());
        assertThat(publishedEvent.getEmail()).isEqualTo(signupRequest.getEmail());
    }
}
