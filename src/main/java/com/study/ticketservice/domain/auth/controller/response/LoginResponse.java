package com.study.ticketservice.domain.auth.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LoginResponse {
    private Long userId;
    private String email;
    private String name;
    private List<String> roles;

    public static LoginResponse of(Long userId, String email, String name, List<String> roles) {
        return LoginResponse.builder()
                .userId(userId)
                .email(email)
                .roles(roles)
                .name(name)
                .build();
    }
}