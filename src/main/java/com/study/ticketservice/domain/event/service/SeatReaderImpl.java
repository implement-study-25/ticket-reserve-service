package com.study.ticketservice.domain.event.service;

import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.domain.event.entity.Seat;
import com.study.ticketservice.domain.event.entity.SeatEntity;
import com.study.ticketservice.domain.event.enums.EventErrorCode;
import com.study.ticketservice.domain.event.enums.SeatStatus;
import com.study.ticketservice.domain.event.repository.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatReaderImpl implements SeatReader {
    
    private final SeatJpaRepository seatJpaRepository;

    /**
     * @param seatId 조회할 좌석 ID
     * @return Seat 도메인 객체
     * @throws ApiException 좌석이 존재하지 않을 경우 SEAT_NOT_FOUND
     */
    @Override
    public Seat findById(Long seatId) {
        return seatJpaRepository.findById(seatId)
                .orElseThrow(() -> new ApiException(EventErrorCode.SEAT_NOT_FOUND))
                .toDomain(); // JPA Entity -> Domain 변환
    }

    /**
     * 특정 이벤트의 모든 좌석 조회
     * @param eventId 이벤트 ID
     * @return 좌석 목록 (행, 열 순으로 정렬된 도메인 객체)
     */
    @Override
    public List<Seat> findByEventId(Long eventId) {
        return seatJpaRepository.findByEventIdOrderByRowAscColAsc(eventId)
                .stream()
                .map(SeatEntity::toDomain)
                .toList();
    }

    /**
     * 특정 이벤트의 특정 상태 좌석 조회
     * @param eventId 이벤트 ID
     * @param status 조회할 좌석 상태
     * @return 해당 상태의 좌석 목록 (행, 열 순으로 정렬)
     */
    @Override
    public List<Seat> findByEventIdAndStatus(Long eventId, SeatStatus status) {
        // 상태별 필터링된 좌석 조회
        return seatJpaRepository.findByEventIdAndStatusOrderByRowAscColAsc(eventId, status)
                .stream()
                .map(SeatEntity::toDomain)
                .toList();
    }
}