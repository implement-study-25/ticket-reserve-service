package com.study.ticketservice.domain.event.service;

import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.domain.event.entity.Event;
import com.study.ticketservice.domain.event.entity.EventEntity;
import com.study.ticketservice.domain.event.enums.EventErrorCode;
import com.study.ticketservice.domain.event.repository.EventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Event 읽기 전용 Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class EventReaderImpl implements EventReader {
    
    private final EventJpaRepository eventJpaRepository;

    /**
     * 이벤트 ID로 단일 이벤트 조회
     * 
     * @param eventId 조회할 이벤트 ID
     * @return Event 도메인 객체
     * @throws ApiException 이벤트가 존재하지 않을 경우 EVENT_NOT_FOUND
     */
    @Override
    public Event findById(Long eventId) {
        return eventJpaRepository.findById(eventId)
                .orElseThrow(() -> new ApiException(EventErrorCode.EVENT_NOT_FOUND))
                .toDomain();
    }

    /**
     * 이벤트 목록 조회 (페이징 및 키워드 검색 지원)
     * 
     * @param keyword 검색 키워드 (제목에서 검색, null이면 전체 조회)
     * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬)
     * @return 페이징된 이벤트 목록 (도메인 객체)
     */
    @Override
    public Page<Event> findEvents(String keyword, Pageable pageable) {
        Page<EventEntity> entities = eventJpaRepository.findEventsWithKeyword(keyword, pageable);
        
        return entities.map(EventEntity::toDomain);
    }

    /**
     * 이벤트 존재 여부 확인
     * 좌석 조회 등에서 이벤트 존재 확인용으로 사용
     * @param eventId 확인할 이벤트 ID
     * @return 존재하면 true, 없으면 false
     */
    @Override
    public boolean existsById(Long eventId) {
        return eventJpaRepository.existsById(eventId);
    }
}