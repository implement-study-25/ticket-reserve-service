package com.study.ticketservice.domain.event.service;


import com.study.ticketservice.domain.event.entity.Seat;

import java.util.List;

/**
 * Seat 쓰기 전용 인터페이스
 * 좌석 생성, 수정 관련 모든 쓰기 작업을 담당
 */
public interface SeatWriter {
    
    /**
     * 단일 좌석 저장 (생성 및 수정)
     * @param seat 저장할 Seat 도메인 객체
     * @return 저장된 Seat 객체 (ID가 할당된 상태)
     */
    Seat save(Seat seat);
    
    /**
     * 다중 좌석 일괄 저장
     * 이벤트 생성 시 모든 좌석을 한 번에 생성할 때 사용
     * @param seats 저장할 Seat 객체 리스트
     * @return 저장된 Seat 객체 리스트 (ID가 할당된 상태)
     */
    List<Seat> saveAll(List<Seat> seats);
}