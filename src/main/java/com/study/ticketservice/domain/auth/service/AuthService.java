package com.study.ticketservice.domain.auth.service;

import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.domain.auth.entity.User;
import com.study.ticketservice.domain.auth.repository.UserRepository;
import com.study.ticketservice.domain.auth.repository.UserRoleMapRepository;
import com.study.ticketservice.domain.auth.controller.request.LoginRequest;
import com.study.ticketservice.domain.auth.controller.response.LoginResponse;
import com.study.ticketservice.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserRoleMapRepository userRoleMapRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(401, "INVALID_CREDENTIALS", null));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(401, "INVALID_CREDENTIALS", null);
        }

        Long userId = user.getUserId();
        String email = user.getEmail();
        String name = user.getName();
        List<String> roles = userRoleMapRepository.findRoleNamesByUserId(userId);

        String access = jwtUtil.createAccessToken(userId, roles);
        String refresh = jwtUtil.createRefreshToken(userId, roles);
        jwtUtil.setTokenCookies(response, access, refresh);

        return LoginResponse.of(userId, email, name, roles);
    }

    public String refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.extractRefreshTokenFromCookie(request);
        if (!StringUtils.hasText(refreshToken) || !jwtUtil.validateRefreshToken(refreshToken)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }

        var claims = jwtUtil.extractRefreshClaims(refreshToken);

        Number userIdNum = claims.get("userId", Number.class);
        if (userIdNum == null) throw new ApiException(401, "INVALID_CLAIMS_USER_ID", null);
        Long userId = userIdNum.longValue();

        List<?> rawRoles = claims.get("roles", List.class);
        List<String> roles = (rawRoles == null) ?
                List.of() : rawRoles.stream().map(String::valueOf).toList();

        if (roles.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }

        String newAccess = jwtUtil.createAccessToken(userId, roles);
        String newRefresh = jwtUtil.createRefreshToken(userId, roles);
        jwtUtil.setTokenCookies(response, newAccess, newRefresh);
        return "ACCESS_TOKEN_REFRESHED";
    }

    public void logout(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("accessToken", "");
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}


