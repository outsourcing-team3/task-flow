package com.example.outsourcingproject.domain.auth.controller;

import com.example.outsourcingproject.domain.auth.dto.request.RefreshTokenRequest;
import com.example.outsourcingproject.domain.auth.dto.request.SigninRequest;
import com.example.outsourcingproject.domain.auth.dto.request.SignupRequest;
import com.example.outsourcingproject.domain.auth.dto.response.SigninResponse;
import com.example.outsourcingproject.domain.auth.dto.response.SignupResponse;
import com.example.outsourcingproject.domain.auth.service.AuthService;
import com.example.outsourcingproject.global.dto.ApiResponse;
import com.example.outsourcingproject.global.security.JwtAuthenticationProvider;
import com.example.outsourcingproject.global.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse response = authService.signup(signupRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입이 완료되었습니다."));
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<ApiResponse<SigninResponse>> signin(@Valid @RequestBody SigninRequest signinRequest) {
        SigninResponse response = authService.signin(signinRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<SigninResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        SigninResponse response = authService.refreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "토큰이 갱신되었습니다."));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                    HttpServletRequest request) {
        String accessToken = jwtAuthenticationProvider.extractTokenFromRequest(request);
        authService.logout(userPrincipal.getId(), accessToken);
        return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다."));
    }
}