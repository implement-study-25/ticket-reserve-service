package com.study.ticketservice.event.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 이벤트 수정 요청 DTO
 */
public record EventUpdateRequest(

    String title,
    String description,

    @NotNull(message = "시작 시간은 필수입니다")
    LocalDateTime startsAt,

    @NotNull(message = "종료 시간은 필수입니다")
    LocalDateTime endsAt
    
) {
    public EventUpdateRequest {
        if (startsAt != null && endsAt != null && !endsAt.isAfter(startsAt)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다");
        }
    }
}