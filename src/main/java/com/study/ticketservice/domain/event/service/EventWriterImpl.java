package com.study.ticketservice.event.infrastructure;

import com.study.ticketservice.event.application.EventWriter;
import com.study.ticketservice.event.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Event 쓰기 전용 Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class EventWriterImpl implements EventWriter {
    
    private final EventJpaRepository eventJpaRepository;

    /**
     * 이벤트 저장 (생성 및 수정)
     * 도메인 객체를 JPA 엔티티로 변환하여 저장 후 다시 도메인 객체로 반환
     * 
     * @param event 저장할 Event 도메인 객체
     * @return 저장된 Event 객체 (ID가 할당된 상태)
     */
    @Override
    public Event save(Event event) {
        EventEntity entity = EventEntity.from(event);
        EventEntity savedEntity = eventJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    /**
     * 이벤트 삭제
     * 
     * @param eventId 삭제할 이벤트 ID
     */
    @Override
    public void delete(Long eventId) {
        eventJpaRepository.deleteById(eventId);
    }
}