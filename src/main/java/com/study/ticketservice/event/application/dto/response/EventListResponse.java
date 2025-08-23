package com.study.ticketservice.event.application.dto.response;

import com.study.ticketservice.event.domain.Event;
import com.study.ticketservice.event.domain.EventStatus;

import java.time.LocalDateTime;

/*
 * 이벤트 목록 조회 응답 DTO
 */
public record EventListResponse(
 
    Long eventId,
    String title,
    EventStatus status,
    LocalDateTime startsAt,
    LocalDateTime endsAt,
    int totalSeats,
    int reservedSeats
) {
    /**
     * @param event 변환할 Event 도메인 객체
     * @return EventListResponse DTO
     */
    public static EventListResponse from(Event event) {
        return new EventListResponse(
            event.getEventId(),
            event.getTitle(),
            event.getStatus(),
            event.getStartsAt(),
            event.getEndsAt(),
            event.getTotalSeats(),
            event.getReservedSeats()
        );
    }
    
    /**
     * @return 예약 가능한 좌석 수 (총 좌석 - 예약된 좌석)
     */
    public int getAvailableSeats() {
        return totalSeats - reservedSeats;
    }
    
}