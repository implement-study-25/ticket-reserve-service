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

@RestController
@RequiredArgsConstructor
public class EventApiController {

    private final EventService eventService;

    @PostMapping("/v1/admin/events")
    @PreAuthorize("hasAuthority('EVENT_CREATE')")
    public ResponseEntity<ApiResponse<EventDetailResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request) {
        
        EventDetailResponse response = eventService.createEvent(request);
        return ApiResponse.success(201, response);
    }

    @PutMapping("/v1/admin/events/{eventId}")
    @PreAuthorize("hasAuthority('EVENT_UPDATE')")
    public ResponseEntity<ApiResponse<EventDetailResponse>> updateEvent(
            @PathVariable @Min(1) Long eventId,
            @Valid @RequestBody EventUpdateRequest request) {
        
        EventDetailResponse response = eventService.updateEvent(eventId, request);
        return ApiResponse.success(response);
    }

    @PostMapping("/v1/admin/events/{eventId}/publish")
    @PreAuthorize("hasAuthority('EVENT_CHANGE_STATUS')")
    public ResponseEntity<ApiResponse<EventStatusResponse>> publishEvent(@PathVariable Long eventId) {
        EventDetailResponse eventDetail = eventService.publishEvent(eventId);
        EventStatusResponse response = EventStatusResponse.from(eventDetail);
        
        return ApiResponse.success(response);
    }

    @PostMapping("/v1/admin/events/{eventId}/close")
    @PreAuthorize("hasAuthority('EVENT_CHANGE_STATUS')")
    public ResponseEntity<ApiResponse<EventStatusResponse>> closeEvent(@PathVariable Long eventId) {
        EventDetailResponse eventDetail = eventService.closeEvent(eventId);
        EventStatusResponse response = EventStatusResponse.from(eventDetail);
        
        return ApiResponse.success(response);
    }

    @PostMapping("/v1/admin/events/{eventId}/cancel")
    @PreAuthorize("hasAuthority('EVENT_CHANGE_STATUS')")
    public ResponseEntity<ApiResponse<EventStatusResponse>> cancelEvent(@PathVariable Long eventId) {
        EventDetailResponse eventDetail = eventService.cancelEvent(eventId);
        EventStatusResponse response = EventStatusResponse.from(eventDetail);
        
        return ApiResponse.success(response);
    }

    @GetMapping("/v1/events")
    public ResponseEntity<EventListApiResponse> getEvents(
            @RequestParam(required = false) String keyword,
            @PageableDefault Pageable pageable) {
        
        Page<EventListResponse> events = eventService.getEvents(keyword, pageable);
        
        PageInfo pageInfo = PageInfo.from(events);
        List<EventListResponse> items = events.getContent();
        
        return ResponseEntity.ok(EventListApiResponse.of("EVENTS_LIST", pageInfo, items));
    }

    @GetMapping("/v1/events/{eventId}")
    public ResponseEntity<ApiResponse<EventDetailResponse>> getEvent(@PathVariable Long eventId) {
        EventDetailResponse response = eventService.getEvent(eventId);
        return ApiResponse.success(response);
    }

    @GetMapping("/v1/events/{eventId}/seats")
    public ResponseEntity<ApiResponse<List<SeatDetailResponse>>> getEventSeats(
            @PathVariable Long eventId,
            @RequestParam(required = false) SeatStatus status) {
        
        List<SeatDetailResponse> seats = eventService.getEventSeats(eventId, status);
        return ApiResponse.success(seats);
    }
}