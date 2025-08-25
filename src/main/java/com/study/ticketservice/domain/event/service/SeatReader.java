package com.study.ticketservice.domain.event.service;


import com.study.ticketservice.domain.event.entity.Seat;
import com.study.ticketservice.domain.event.enums.SeatStatus;

import java.util.List;

/**
 * Seat 읽기 전용 인터페이스
 * 좌석 조회 관련 모든 읽기 작업을 담당
 */
public interface SeatReader {
    
    /**
     * 좌석 ID로 단일 좌석 조회
     * @param seatId 조회할 좌석 ID
     * @return Seat 도메인 객체
     * @throws com.study.ticketservice.common.exception.ApiException 좌석이 존재하지 않을 경우 SEAT_NOT_FOUND
     */
    Seat findById(Long seatId);
    
    /**
     * 특정 이벤트의 모든 좌석 조회
     * @param eventId 이벤트 ID
     * @return 좌석 목록 (행, 열 순으로 정렬)
     */
    List<Seat> findByEventId(Long eventId);
    
    /**
     * 특정 이벤트의 특정 상태 좌석 조회
     * @param eventId 이벤트 ID
     * @param status 조회할 좌석 상태
     * @return 해당 상태의 좌석 목록 (행, 열 순으로 정렬)
     */
    List<Seat> findByEventIdAndStatus(Long eventId, SeatStatus status);
}