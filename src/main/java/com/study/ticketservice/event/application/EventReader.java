package com.study.ticketservice.event.application;

import com.study.ticketservice.event.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Event 읽기 전용 인터페이스
 * 이벤트 조회 관련 모든 읽기 작업을 담당
 */
public interface EventReader {
    
    /**
     * 이벤트 ID로 단일 이벤트 조회
     * @param eventId 조회할 이벤트 ID
     * @return Event 도메인 객체
     * @throws ApiException 이벤트가 존재하지 않을 경우 EVENT_NOT_FOUND
     */
    Event findById(Long eventId);
    
    /**
     * 이벤트 목록 조회 (페이징 및 키워드 검색 지원)
     * @param keyword 검색 키워드 (제목에서 검색, null이면 전체 조회)
     * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬)
     * @return 페이징된 이벤트 목록
     */
    Page<Event> findEvents(String keyword, Pageable pageable);
    
    /**
     * 이벤트 존재 여부 확인
     * 좌석 조회 등에서 이벤트 존재 확인용으로 사용
     * @param eventId 확인할 이벤트 ID
     * @return 존재하면 true, 없으면 false
     */
    boolean existsById(Long eventId);
}