package com.study.ticketservice.event.presentation;

import com.study.ticketservice.common.response.ApiResponse;
import com.study.ticketservice.event.application.EventService;
import com.study.ticketservice.event.application.dto.request.EventCreateRequest;
import com.study.ticketservice.event.application.dto.request.EventUpdateRequest;
import com.study.ticketservice.event.application.dto.response.EventDetailResponse;
import com.study.ticketservice.event.application.dto.response.EventListResponse;
import com.study.ticketservice.event.application.dto.response.EventStatusResponse;
import com.study.ticketservice.event.application.dto.response.SeatDetailResponse;
import com.study.ticketservice.event.domain.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import com.study.ticketservice.common.response.PageInfo;
import com.study.ticketservice.common.response.EventListApiResponse;

/**
 * Event API 컨트롤러
 * 관리자용 이벤트 관리 기능과 공개용 조회 기능으로 구분
 */
@RestController
@RequiredArgsConstructor
public class EventApiController {

    private final EventService eventService;

    /**
     * 이벤트 생성 API
     * 관리자만 접근 가능하며 이벤트와 좌석을 함께 생성
     * @param request 이벤트 생성 요청 정보
     * @return 생성된 이벤트 상세 정보 (201 Created)
     */
    @PostMapping("/v1/admin/events")
    @PreAuthorize("hasAuthority('EVENT_CREATE')")
    public ResponseEntity<ApiResponse<EventDetailResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request) {
        
        EventDetailResponse response = eventService.createEvent(request);
        return ApiResponse.success(201, response);
    }

    /**
     * 이벤트 수정 API
     * 관리자만 접근 가능하며 DRAFT 상태의 이벤트만 수정 가능
     * @param eventId 수정할 이벤트 ID
     * @param request 이벤트 수정 요청 정보
     * @return 수정된 이벤트 상세 정보
     */
    @PutMapping("/v1/admin/events/{eventId}")
    @PreAuthorize("hasAuthority('EVENT_UPDATE')")
    public ResponseEntity<ApiResponse<EventDetailResponse>> updateEvent(
            @PathVariable @Min(1) Long eventId,
            @Valid @RequestBody EventUpdateRequest request) {
        
        EventDetailResponse response = eventService.updateEvent(eventId, request);
        return ApiResponse.success(response);
    }

    /**
     * 이벤트 발행 API
     * 관리자만 접근 가능하며 DRAFT 상태의 이벤트를 PUBLISHED로 변경
     * @param eventId 발행할 이벤트 ID
     * @return 이벤트 상태 변경 결과
     */
    @PostMapping("/v1/admin/events/{eventId}/publish")
    @PreAuthorize("hasAuthority('EVENT_CHANGE_STATUS')")
    public ResponseEntity<ApiResponse<EventStatusResponse>> publishEvent(@PathVariable Long eventId) {
        EventDetailResponse eventDetail = eventService.publishEvent(eventId);
        EventStatusResponse response = EventStatusResponse.from(eventDetail);
        
        return ApiResponse.success(response);
    }

    /**
     * 이벤트 종료 API
     * 관리자만 접근 가능하며 PUBLISHED 상태의 이벤트를 CLOSED로 변경
     * @param eventId 종료할 이벤트 ID
     * @return 이벤트 상태 변경 결과
     */
    @PostMapping("/v1/admin/events/{eventId}/close")
    @PreAuthorize("hasAuthority('EVENT_CHANGE_STATUS')")
    public ResponseEntity<ApiResponse<EventStatusResponse>> closeEvent(@PathVariable Long eventId) {
        EventDetailResponse eventDetail = eventService.closeEvent(eventId);
        EventStatusResponse response = EventStatusResponse.from(eventDetail);
        
        return ApiResponse.success(response);
    }

    /**
     * 이벤트 취소 API
     * 관리자만 접근 가능하며 DRAFT/PUBLISHED 상태의 이벤트를 CANCELED로 변경
     * @param eventId 취소할 이벤트 ID
     * @return 이벤트 상태 변경 결과
     */
    @PostMapping("/v1/admin/events/{eventId}/cancel")
    @PreAuthorize("hasAuthority('EVENT_CHANGE_STATUS')")
    public ResponseEntity<ApiResponse<EventStatusResponse>> cancelEvent(@PathVariable Long eventId) {
        EventDetailResponse eventDetail = eventService.cancelEvent(eventId);
        EventStatusResponse response = EventStatusResponse.from(eventDetail);
        
        return ApiResponse.success(response);
    }

    /**
     * 이벤트 목록 조회 API (공개)
     * 모든 사용자가 접근 가능하며 키워드 검색과 페이징을 지원
     * @param keyword 검색 키워드 (선택사항)
     * @param pageable 페이징 정보
     * @return 페이징된 이벤트 목록
     */
    @GetMapping("/v1/events")
    public ResponseEntity<EventListApiResponse> getEvents(
            @RequestParam(required = false) String keyword,
            @PageableDefault Pageable pageable) {
        
        Page<EventListResponse> events = eventService.getEvents(keyword, pageable);
        
        PageInfo pageInfo = PageInfo.from(events);
        List<EventListResponse> items = events.getContent();
        
        return ResponseEntity.ok(EventListApiResponse.of("EVENTS_LIST", pageInfo, items));
    }

    /**
     * 이벤트 상세 조회 API (공개)
     * 모든 사용자가 접근 가능하며 특정 이벤트의 상세 정보를 제공
     * @param eventId 조회할 이벤트 ID
     * @return 이벤트 상세 정보
     */
    @GetMapping("/v1/events/{eventId}")
    public ResponseEntity<ApiResponse<EventDetailResponse>> getEvent(@PathVariable Long eventId) {
        EventDetailResponse response = eventService.getEvent(eventId);
        return ApiResponse.success(response);
    }

    /**
     * 이벤트 좌석 조회 API (공개)
     * 모든 사용자가 접근 가능하며 좌석 상태별 필터링을 지원
     * @param eventId 조회할 이벤트 ID
     * @param status 좌석 상태 필터 (선택사항)
     * @return 이벤트의 좌석 목록
     */
    @GetMapping("/v1/events/{eventId}/seats")
    public ResponseEntity<ApiResponse<List<SeatDetailResponse>>> getEventSeats(
            @PathVariable Long eventId,
            @RequestParam(required = false) SeatStatus status) {
        
        List<SeatDetailResponse> seats = eventService.getEventSeats(eventId, status);
        return ApiResponse.success(seats);
    }
}