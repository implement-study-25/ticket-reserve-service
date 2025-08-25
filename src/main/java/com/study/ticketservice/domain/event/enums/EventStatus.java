package com.study.ticketservice.domain.event.enums;

/**
 * 이벤트 상태를 나타내는 열거형
 */
public enum EventStatus {
    /**
     * 초안 상태 - 이벤트가 생성되었지만 아직 발행되지 않은 상태
     * 이 상태에서만 이벤트 정보 수정이 가능
     */
    DRAFT,
    
    /**
     * 발행됨 - 사용자들이 예약할 수 있는 상태
     * 이 상태에서만 예약이 가능하며, 종료로 변경 가능
     */
    PUBLISHED,
    
    /**
     * 종료됨 - 이벤트가 정상적으로 종료된 상태
     * 더 이상 예약을 받지 않으며, 취소할 수 없음
     */
    CLOSED,
    
    /**
     * 취소됨 - 이벤트가 취소된 상태
     * 예약된 모든 좌석은 환불 처리되어야 함
     */
    CANCELED
}