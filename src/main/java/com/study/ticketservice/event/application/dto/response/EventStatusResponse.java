package com.study.ticketservice.event.application.dto.response;

import com.study.ticketservice.event.domain.EventStatus;
import java.time.LocalDateTime;

/**
 * 이벤트 상태 변경 응답 DTO
 */
public record EventStatusResponse(
    Long id,
    EventStatus status,
    LocalDateTime updatedAt
) {
    public static EventStatusResponse from(EventDetailResponse eventDetail) {
        return new EventStatusResponse(
            eventDetail.eventId(),
            eventDetail.status(),
            eventDetail.updatedAt()
        );
    }
}
