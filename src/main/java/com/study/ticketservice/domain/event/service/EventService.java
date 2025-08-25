package com.study.ticketservice.domain.event.service;

import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.domain.event.controller.request.EventCreateRequest;
import com.study.ticketservice.domain.event.controller.request.EventUpdateRequest;
import com.study.ticketservice.domain.event.controller.response.EventDetailResponse;
import com.study.ticketservice.domain.event.controller.response.EventListResponse;
import com.study.ticketservice.domain.event.controller.response.SeatDetailResponse;
import com.study.ticketservice.domain.event.entity.Event;
import com.study.ticketservice.domain.event.entity.Seat;
import com.study.ticketservice.domain.event.enums.EventErrorCode;
import com.study.ticketservice.domain.event.enums.SeatStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
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
@Slf4j
public class EventService {

    private final EventReader eventReader;
    private final EventWriter eventWriter;
    private final SeatReader seatReader;
    private final SeatWriter seatWriter;

    /**
     * 이벤트 생성 - 비동기 좌석 생성
     */
    @Transactional
    public EventDetailResponse createEvent(EventCreateRequest request) {
        log.info("이벤트 생성 시작: {}", request.title());
        
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
        
        createSeatsAsync(savedEvent);
        
        log.info("이벤트 생성 완료: eventId={}", savedEvent.getEventId());
        return EventDetailResponse.from(savedEvent);
    }

    /**
     * 비동기 좌석 생성
     */
    @Async
    public void createSeatsAsync(Event event) {
        log.info("좌석 생성 시작 (비동기): eventId={}", event.getEventId());
        long startTime = System.currentTimeMillis();
        
        try {
            List<Seat> seats = new ArrayList<>();
            
            for (int row = 1; row <= event.getTotalRows(); row++) {
                for (int col = 1; col <= event.getTotalCols(); col++) {
                    seats.add(new Seat(event.getEventId(), row, col, 10000));
                }
            }
            
            seatWriter.saveAll(seats);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("좌석 생성 완료 (비동기): eventId={}, 소요시간: {}ms", 
                     event.getEventId(), duration);
                     
        } catch (Exception e) {
            log.error("좌석 생성 실패 (비동기): eventId={}", event.getEventId(), e);
            // 이벤트는 보존, 좌석 생성만 실패
        }
    }

    /**
     * 이벤트 수정
     */
    @Transactional
    public EventDetailResponse updateEvent(Long eventId, EventUpdateRequest request) {
        log.info("이벤트 수정 시작: eventId={}", eventId);
        
        Event event = eventReader.findById(eventId);
            event.updateBasicInfo(
            request.title(),
            request.description(),
            request.startsAt(),
            request.endsAt()
        );
        
        Event updatedEvent = eventWriter.save(event);
        log.info("이벤트 수정 완료: eventId={}", eventId);
        return EventDetailResponse.from(updatedEvent);
    }

    /**
     *  이벤트 발행
     */
    @Transactional
    public EventDetailResponse publishEvent(Long eventId) {
        log.info("이벤트 발행 시작: eventId={}", eventId);
        
        Event event = eventReader.findById(eventId);
        event.publish();
        Event savedEvent = eventWriter.save(event);
        
        log.info("이벤트 발행 완료: eventId={}", eventId);
        return EventDetailResponse.from(savedEvent);
    }

    /**
     * 이벤트 종료
     */
    @Transactional
    public EventDetailResponse closeEvent(Long eventId) {
        log.info("이벤트 종료 시작: eventId={}", eventId);
        
        Event event = eventReader.findById(eventId);
        event.close();
        Event savedEvent = eventWriter.save(event);
        
        log.info("이벤트 종료 완료: eventId={}", eventId);
        return EventDetailResponse.from(savedEvent);
    }

    /**
     * 이벤트 취소
     */
    @Transactional
    public EventDetailResponse cancelEvent(Long eventId) {
        log.info("이벤트 취소 시작: eventId={}", eventId);
        
        Event event = eventReader.findById(eventId);
        event.cancel();
        Event savedEvent = eventWriter.save(event);
        
        log.info("이벤트 취소 완료: eventId={}", eventId);
        return EventDetailResponse.from(savedEvent);
    }

    /**
     * 이벤트 목록 조회
     */
    public Page<EventListResponse> getEvents(String keyword, Pageable pageable) {
        Page<Event> events = eventReader.findEvents(keyword, pageable);
        return events.map(EventListResponse::from);
    }

    /**
     * 이벤트 상세 조회
     */
    public EventDetailResponse getEvent(Long eventId) {
        Event event = eventReader.findById(eventId);
        return EventDetailResponse.from(event);
    }

    /**
     * 이벤트 좌석 조회
     */
    public List<SeatDetailResponse> getEventSeats(Long eventId, SeatStatus status) {
        List<Seat> seats;
        if (status != null) {
            seats = seatReader.findByEventIdAndStatus(eventId, status);
        } else {
            seats = seatReader.findByEventId(eventId);
        }
        
        // 좌석이 없고 이벤트도 존재하지 않는 경우에만 예외 발생
        if (seats.isEmpty() && !eventReader.existsById(eventId)) {
            throw new ApiException(EventErrorCode.EVENT_NOT_FOUND);
        }
        
        return seats.stream().map(SeatDetailResponse::from).toList();
    }
}