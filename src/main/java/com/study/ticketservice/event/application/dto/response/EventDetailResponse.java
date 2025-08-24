package com.study.ticketservice.event.application.dto.response;

import com.study.ticketservice.event.domain.Event;
import com.study.ticketservice.event.domain.EventStatus;

import java.time.LocalDateTime;

/**
 * 이벤트 상세 정보 응답 DTO
 */
public record EventDetailResponse(

    Long eventId,
    String title,
    String description,
    EventStatus status,
    LocalDateTime startsAt,
    LocalDateTime endsAt,
    int totalRows,
    int totalCols,
    int totalSeats,
    int reservedSeats,
    long paidAmount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * @param event 변환할 Event 도메인 객체
     * @return EventDetailResponse DTO
     */
    public static EventDetailResponse from(Event event) {
        return new EventDetailResponse(
            event.getEventId(),
            event.getTitle(),
            event.getDescription(),
            event.getStatus(),
            event.getStartsAt(),
            event.getEndsAt(),
            event.getTotalRows(),
            event.getTotalCols(),
            event.getTotalSeats(),
            event.getReservedSeats(),
            event.getPaidAmount(),
            event.getCreatedAt(),
            event.getUpdatedAt()
        );
    }
}