package com.study.ticketservice.event.application;

import com.study.ticketservice.event.domain.Event;

/**
 * Event 쓰기 전용 인터페이스
 * 이벤트 생성, 수정, 삭제 관련 모든 쓰기 작업을 담당
 */
public interface EventWriter {
    
    /**
     * 이벤트 저장 (생성 및 수정)
     * @param event 저장할 Event 도메인 객체
     * @return 저장된 Event 객체 (ID가 할당된 상태)
     */
    Event save(Event event);
    
    /**
     * 이벤트 삭제
     * @param eventId 삭제할 이벤트 ID
     */
    void delete(Long eventId);
}