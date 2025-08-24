package com.study.ticketservice.event.domain;

/**
 * 좌석 상태를 나타내는 열거형
 * DB 스키마의 ENUM과 일치하도록 설계
 */
public enum SeatStatus {
    /**
     * 예약 가능 - 누구나 선택하고 예약할 수 있는 상태
     * 이벤트 생성 시 모든 좌석의 초기 상태
     */
    AVAILABLE,
    
    /**
     * 임시 선점 - 특정 사용자가 예약 과정에서 임시로 선점한 상태
     */
    HOLD,
    
    /**
     * 판매 완료 - 결제가 완료되어 예약이 확정된 상태
     * 더 이상 변경할 수 없는 최종 상태
     */
    SOLD
}