package com.study.ticketservice.event.domain;

import com.study.ticketservice.common.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event 도메인 관련 에러 코드 정의
 * 도메인 레이어에 위치하여 순수성 보장
 */
@Getter
@AllArgsConstructor
public enum EventErrorCode implements ErrorCode {
    
    EVENT_NOT_FOUND(404, "이벤트를 찾을 수 없습니다"),
    INVALID_EVENT_STATUS(400, "잘못된 이벤트 상태입니다"),
    INVALID_EVENT_TIME(400, "잘못된 이벤트 시간입니다"),
    INVALID_EVENT_TITLE(400, "잘못된 이벤트 제목입니다"),
    SEAT_NOT_FOUND(404, "좌석을 찾을 수 없습니다"),
    SEAT_NOT_AVAILABLE(400, "예약 가능한 좌석이 아닙니다"),
    INVALID_SEAT_STATUS(400, "잘못된 좌석 상태입니다"),
    INVALID_SEAT_NUMBER(400, "잘못된 좌석 번호입니다"),
    INVALID_PARAMETER(400, "잘못된 파라미터입니다");

    private final int code;
    private final String message;
}
