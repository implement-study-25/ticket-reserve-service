package com.study.ticketservice.event.application;

import com.study.ticketservice.event.domain.Event;

/**
 * Event 쓰기 전용 인터페이스
 */
public interface EventWriter {
    
    /**
     * @param event 저장할 Event 도메인 객체
     * @return 저장된 Event 객체 (ID가 할당된 상태)
     */
    Event save(Event event);
    
    /**
     * @param eventId 삭제할 이벤트 ID
     */
    void delete(Long eventId);
}