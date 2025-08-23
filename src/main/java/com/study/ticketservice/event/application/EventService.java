package com.study.ticketservice.event.application;

import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.event.domain.EventErrorCode;
import com.study.ticketservice.event.application.dto.request.EventCreateRequest;
import com.study.ticketservice.event.application.dto.request.EventUpdateRequest;
import com.study.ticketservice.event.application.dto.response.EventDetailResponse;
import com.study.ticketservice.event.application.dto.response.EventListResponse;
import com.study.ticketservice.event.application.dto.response.SeatDetailResponse;
import com.study.ticketservice.event.domain.Event;
import com.study.ticketservice.event.domain.Seat;
import com.study.ticketservice.event.domain.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Event 애플리케이션 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    
    private final EventReader eventReader;
    private final EventWriter eventWriter;
    private final SeatReader seatReader;
    private final SeatWriter seatWriter;

    /**
     * 이벤트 생성 유스케이스
     * 1. 도메인 객체 생성 (비즈니스 규칙 적용)
     * 2. 이벤트 저장
     * 3. 좌석 생성 및 저장
     */
    @Transactional
    public EventDetailResponse createEvent(EventCreateRequest request) {
        Event event = new Event(
            request.title(),
            request.description(),
            request.startsAt(),
            request.endsAt(),
            request.totalSeats(),
            request.totalRows(),
            request.totalCols()
        );

        Event savedEvent = eventWriter.save(event);
        
        List<Seat> seats = createSeats(savedEvent);
        seatWriter.saveAll(seats);

        return EventDetailResponse.from(savedEvent);
    }

    /**
     * DRAFT 상태의 이벤트만 수정 가능 (도메인 규칙)
     */
    @Transactional
    public EventDetailResponse updateEvent(Long eventId, EventUpdateRequest request) {

        Event event = eventReader.findById(eventId);
            event.updateBasicInfo(
            request.title(),
            request.description(),
            request.startsAt(),
            request.endsAt()
        );

        Event updatedEvent = eventWriter.save(event);
        return EventDetailResponse.from(updatedEvent);
    }

    /**
     * DRAFT -> PUBLISHED 상태 변경
     */
    @Transactional
    public EventDetailResponse publishEvent(Long eventId) {
        Event event = eventReader.findById(eventId);
        event.publish();
        Event savedEvent = eventWriter.save(event);
        return EventDetailResponse.from(savedEvent);
    }

    /**
     * PUBLISHED -> CLOSED 상태 변경
     */
    @Transactional
    public EventDetailResponse closeEvent(Long eventId) {
        Event event = eventReader.findById(eventId);
        event.close(); // 도메인 로직으로 상태 검증
        Event savedEvent = eventWriter.save(event);
        return EventDetailResponse.from(savedEvent);
    }

    /**
     * DRAFT/PUBLISHED -> CANCELED 상태 변경
     */
    @Transactional
    public EventDetailResponse cancelEvent(Long eventId) {
        Event event = eventReader.findById(eventId);
        event.cancel();
        Event savedEvent = eventWriter.save(event);
        return EventDetailResponse.from(savedEvent);
    }

    /*
     * 페이징과 키워드 검색 지원
     */
    public Page<EventListResponse> getEvents(String keyword, Pageable pageable) {
        Page<Event> events = eventReader.findEvents(keyword, pageable);
        return events.map(EventListResponse::from);
    }

    /**
     * 이벤트 상세 조회 유스케이스
     */
    public EventDetailResponse getEvent(Long eventId) {
        Event event = eventReader.findById(eventId);
        return EventDetailResponse.from(event);
    }

    /**
     * 이벤트 좌석 조회 유스케이스
     * 상태별 필터링 지원
     */
    public List<SeatDetailResponse> getEventSeats(Long eventId, SeatStatus status) {
        if (!eventReader.existsById(eventId)) {
            throw new ApiException(EventErrorCode.EVENT_NOT_FOUND);
        }
        List<Seat> seats;
        if (status != null) {
            seats = seatReader.findByEventIdAndStatus(eventId, status);
        } else {
            seats = seatReader.findByEventId(eventId);
        }
        return seats.stream()
                .map(SeatDetailResponse::from)
                .toList();
    }

    /**
     * 좌석 생성 로직
     * 이벤트의 행/열 정보를 기반으로 모든 좌석을 생성
     */
    private List<Seat> createSeats(Event event) {
        List<Seat> seats = new ArrayList<>();
        
        for (int row = 1; row <= event.getTotalRows(); row++) {
            for (int col = 1; col <= event.getTotalCols(); col++) {
                seats.add(new Seat(event.getEventId(), row, col, 10000));
            }
        }
        
        return seats;
    }
}