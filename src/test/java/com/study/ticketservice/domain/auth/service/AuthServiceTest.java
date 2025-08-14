package com.study.ticketservice.domain.auth.service;

import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.domain.auth.controller.request.LoginRequest;
import com.study.ticketservice.domain.auth.repository.UserRepository;
import com.study.ticketservice.domain.auth.repository.UserRoleMapRepository;
import com.study.ticketservice.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks AuthService authService;

    @Mock JwtUtil jwtUtil;
    @Mock UserRepository userRepository;
    @Mock UserRoleMapRepository userRoleMapRepository;
    @Mock PasswordEncoder passwordEncoder;

    // 로컬 Mockito 목 사용
    jakarta.servlet.http.HttpServletResponse httpServletResponse = org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletResponse.class);
    jakarta.servlet.http.HttpServletRequest httpServletRequest = org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);

    @Test
    @DisplayName("login: 사용자 없음. 401 ApiException")
    void login_user_not_found() {
        given(userRepository.findByEmail("no@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("no@test.com", "pw"), httpServletResponse))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("refresh: 쿠키 없음/무효. 401 반환 및 null")
    void refresh_unauthorized() {
        given(jwtUtil.extractRefreshTokenFromCookie(httpServletRequest)).willReturn(null);
        given(userRepository.findByEmail("no@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("no@test.com", "pw"), httpServletResponse))
                .isInstanceOf(ApiException.class);
        String res = authService.refresh(httpServletRequest, httpServletResponse);
        assertThat(res).isNull();
        verify(httpServletResponse).setStatus(HttpStatus.UNAUTHORIZED.value());

    }

    @Test
    @DisplayName("logout: 쿠키 만료 세팅")
    void logout_success() {
        authService.logout(httpServletResponse);
        verify(httpServletResponse, atLeastOnce()).addCookie(any());
    }
}


