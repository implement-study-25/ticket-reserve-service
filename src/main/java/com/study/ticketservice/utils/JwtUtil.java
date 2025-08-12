package com.study.ticketservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtUtil {

    // Access Token
    @Value("${jwt.token.access.expire-second}")
    private int ACCESS_TOKEN_EXPIRE_COUNT;

    @Value("${jwt.token.access.access-secret}")
    private String ACCESS_TOKEN_SECRET;

    // Refresh Token
    @Value("${jwt.token.refresh.expire-second}")
    private int REFRESH_TOKEN_EXPIRE_COUNT;

    @Value("${jwt.token.refresh.refresh-secret}")
    private String REFRESH_TOKEN_SECRET;

    public String createAccessToken(Long userId, List<String> roles) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_COUNT * 1000L);

        return Jwts.builder()
                .setSubject("AccessToken")
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSecretKey(ACCESS_TOKEN_SECRET))
                .compact();
    }

    public String createRefreshToken(Long userId, List<String> roles) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_COUNT * 1000L);

        return Jwts.builder()
                .setSubject("RefreshToken")
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSecretKey(REFRESH_TOKEN_SECRET))
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validate(token, ACCESS_TOKEN_SECRET);
    }

    public boolean validateRefreshToken(String token) {
        return validate(token, REFRESH_TOKEN_SECRET);
    }

    private boolean validate(String token, String secret) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSecretKey(secret))
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    public Claims extractAccessClaims(String token) {
        return extractClaims(token, ACCESS_TOKEN_SECRET);
    }

    public Claims extractRefreshClaims(String token) {
        return extractClaims(token, REFRESH_TOKEN_SECRET);
    }

    private Claims extractClaims(String token, String secret) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSecretKey(String secret) {
        byte[] keyBytes;

        if (secret != null && secret.length() % 2 == 0 && secret.matches("^[0-9a-fA-F]+$")) {
            keyBytes = hexToBytes(secret);
        } else {
            try {
                keyBytes = Decoders.BASE64.decode(secret);
            } catch (IllegalArgumentException e) {
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            }
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException("Jwt Secret이 32바이트 미만입니다.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }


    public String extractAccessTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setMaxAge(ACCESS_TOKEN_EXPIRE_COUNT);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(REFRESH_TOKEN_EXPIRE_COUNT);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}
