package com.study.ticketservice.common.response;

import com.study.ticketservice.event.application.dto.response.EventListResponse;
import java.util.List;

/**
 * 이벤트 목록 API 응답 전용 
 */
public record EventListApiResponse(
    String code,
    PageInfo page,
    List<EventListResponse> items
) {
    public static EventListApiResponse of(String code, PageInfo page, List<EventListResponse> items) {
        return new EventListApiResponse(code, page, items);
    }
}
