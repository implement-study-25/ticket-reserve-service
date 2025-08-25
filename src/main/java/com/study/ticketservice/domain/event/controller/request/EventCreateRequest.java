package com.study.ticketservice.domain.event.controller.request;

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