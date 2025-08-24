package com.study.ticketservice.event.application.dto.request;

import java.time.LocalDateTime;

/**
 * 이벤트 생성 요청 DTO
 */
public record EventCreateRequest(
    String title,
    String description,
    LocalDateTime startsAt, 
    LocalDateTime endsAt,
    int totalSeats,
    int totalRows,
    int totalCols
) {

}