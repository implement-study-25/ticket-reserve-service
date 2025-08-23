package com.study.ticketservice.event.application.dto.response;

import com.study.ticketservice.event.domain.Seat;
import com.study.ticketservice.event.domain.SeatStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 좌석 상세 정보 응답 DTO
 */
public record SeatDetailResponse(

    @JsonProperty("row")
    int row,
    
    @JsonProperty("col") 
    int col,

    String seatNumber,
    int price,
    SeatStatus status

) {
    /**
     * @param seat 변환할 Seat 도메인 객체
     * @return SeatDetailResponse DTO
     */
    public static SeatDetailResponse from(Seat seat) {
        return new SeatDetailResponse(
            seat.getRow(),
            seat.getCol(),
            seat.getSeatNumber(),
            seat.getPrice(),
            seat.getStatus()
        );
    }
}