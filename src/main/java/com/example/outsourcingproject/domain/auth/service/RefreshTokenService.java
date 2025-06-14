package com.example.outsourcingproject.domain.auth.service;

import com.example.outsourcingproject.domain.auth.entity.RefreshToken;
import com.example.outsourcingproject.domain.auth.repository.RefreshTokenRepository;
import com.example.outsourcingproject.domain.auth.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String createRefreshToken(Long userId) {

        refreshTokenRepository.deleteByUserId(userId);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(14);

        RefreshToken refreshToken = new RefreshToken(token, userId, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("유효하지 않은 Refresh Token 입니다."));

        if (refreshToken.isExpired()) {
            throw new InvalidCredentialsException("만료된 Refresh Token 입니다.");
        }

        return refreshToken;
    }

    @Transactional
    public void deleteRefreshTokenByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}