package com.study.ticketservice.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.ticketservice.domain.auth.controller.request.LoginRequest;
import com.study.ticketservice.domain.auth.controller.response.LoginResponse;
import com.study.ticketservice.domain.auth.service.AuthService;
import com.study.ticketservice.common.security.RbacAuthoritiesFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    RbacAuthoritiesFilter rbacAuthoritiesFilter;

    private static final String CODE = "$.code";
    private static final String USER_ID = "$.data.userId";
    private static final String ROLES_FIRST = "$.data.roles[0]";

    @Test
    @DisplayName("/api/v1/auth/login 성공")
    void login_success() throws Exception {
        LoginRequest req = new LoginRequest("user@test.com", "password");
        LoginResponse res = new LoginResponse(200L, "user@test.com", "user", List.of("USER"));

        given(authService.login(any(LoginRequest.class), any())).willReturn(res);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(200))
                .andExpect(jsonPath(USER_ID).value(200))
                .andExpect(jsonPath(ROLES_FIRST).value("USER"));
    }

    @Test
    @DisplayName("/api/v1/auth/refresh 성공")
    void refresh_success() throws Exception {
        String res = "ACCESS_TOKEN_REFRESHED";
        given(authService.refresh(any(), any())).willReturn(res);

        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(200));
    }

    @Test
    @DisplayName("/api/v1/auth/logout 성공")
    void logout_success() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(200));
    }
}


