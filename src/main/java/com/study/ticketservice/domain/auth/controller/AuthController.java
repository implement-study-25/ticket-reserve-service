package com.study.ticketservice.domain.auth.controller;

import com.study.ticketservice.common.response.ApiResponse;
import com.study.ticketservice.domain.auth.service.AuthService;
import com.study.ticketservice.domain.auth.controller.request.LoginRequest;
import com.study.ticketservice.domain.auth.controller.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request,
                                                            HttpServletResponse response) {
        return ApiResponse.success(authService.login(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(HttpServletRequest request,
                                                              HttpServletResponse response) {
        return ApiResponse.success(authService.refresh(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        authService.logout(response);
        return ApiResponse.success(null);
    }
}