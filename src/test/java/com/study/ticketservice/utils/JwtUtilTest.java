package com.study.ticketservice.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

    @Test
    @DisplayName("액세스/리프레시 토큰 생성 및 파싱")
    void createAndParseTokens() {
        Long userId = 100L;
        List<String> roles = List.of("USER", "ADMIN");

        String access = jwtUtil.createAccessToken(userId, roles);
        String refresh = jwtUtil.createRefreshToken(userId, roles);

        assertThat(jwtUtil.validateAccessToken(access)).isTrue();
        assertThat(jwtUtil.validateRefreshToken(refresh)).isTrue();

        Claims a = jwtUtil.extractAccessClaims(access);
        Claims r = jwtUtil.extractRefreshClaims(refresh);

        assertThat(a.get("userId", Number.class).longValue()).isEqualTo(userId);
        assertThat(r.get("userId", Number.class).longValue()).isEqualTo(userId);
        assertThat((List<String>) a.get("roles", List.class)).contains("USER", "ADMIN");
        assertThat((List<String>) r.get("roles", List.class)).contains("USER", "ADMIN");
        assertThat(a.get("type", String.class)).isEqualTo("access");
        assertThat(r.get("type", String.class)).isEqualTo("refresh");
    }
}


