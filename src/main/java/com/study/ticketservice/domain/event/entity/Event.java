package com.study.ticketservice.domain.event.entity;
import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.domain.event.enums.EventErrorCode;
import com.study.ticketservice.domain.event.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Event 도메인 엔티티 - Aggregate Root
 * 이벤트의 모든 정보와 비즈니스 규칙을 관리하는 도메인 모델
 * 좌석 생성, 상태 변경 등의 핵심 비즈니스 로직을 포함
 */
public class Event {
    
    // 이벤트 기본 정보
    private Long eventId;
    private String title;
    private String description;
    private EventStatus status;
    
    // 이벤트 일정
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    
    // 좌석 레이아웃 정보
    private int totalRows;
    private int totalCols;
    private int totalSeats;
    
    // 예약 현황
    private int reservedSeats;
    private long paidAmount;
    
    // 생성/수정 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 기본 생성자
     * JPA 및 프레임워크에서 사용
     */
    public Event() {}

    /**
     * 새 이벤트 생성용 생성자
     * 이벤트 생성 시 비즈니스 규칙을 적용하여 초기화
     */
    public Event(String title, String description, LocalDateTime startsAt, LocalDateTime endsAt,
                 int totalSeats, int totalRows, int totalCols) {
        this.title = validateTitle(title);
        this.description = validateDescription(description);
        this.status = EventStatus.DRAFT;
        this.startsAt = validateStartTime(startsAt);
        this.endsAt = validateEndTime(startsAt, endsAt);
        this.totalRows = validateRows(totalRows);
        this.totalCols = validateCols(totalCols);
        
        int calculatedSeats = totalRows * totalCols;
        if (totalSeats != calculatedSeats) {
            throw new ApiException(EventErrorCode.INVALID_PARAMETER,
                "총 좌석 수(" + totalSeats + ")는 행×열(" + calculatedSeats + ")과 일치해야 합니다");
        }
        
        this.totalSeats = totalSeats;
        this.reservedSeats = 0;
        this.paidAmount = 0L;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 전체 필드 생성자
     * Repository에서 엔티티를 도메인으로 변환할 때 사용
     */
    public Event(Long eventId, String title, String description, EventStatus status,
                 LocalDateTime startsAt, LocalDateTime endsAt, int totalRows, int totalCols,
                 int totalSeats, int reservedSeats, long paidAmount,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.totalRows = totalRows;
        this.totalCols = totalCols;
        this.totalSeats = totalSeats;
        this.reservedSeats = reservedSeats;
        this.paidAmount = paidAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 이벤트 발행
     * DRAFT 상태에서만 PUBLISHED로 변경 가능
     */
    public void publish() {
        if (this.status != EventStatus.DRAFT) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_STATUS, "DRAFT 상태의 이벤트만 발행할 수 있습니다");
        }
        if (startsAt.isBefore(LocalDateTime.now())) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TIME, "과거 시간의 이벤트는 발행할 수 없습니다");
        }
        this.status = EventStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 이벤트 종료
     * PUBLISHED 상태에서만 CLOSED로 변경 가능
     */
    public void close() {
        if (this.status != EventStatus.PUBLISHED) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_STATUS, "PUBLISHED 상태의 이벤트만 종료할 수 있습니다");
        }
        this.status = EventStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 이벤트 취소
     * CLOSED 상태를 제외한 모든 상태에서 CANCELED로 변경 가능
     */
    public void cancel() {
        if (this.status == EventStatus.CANCELED) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_STATUS, "이미 취소된 이벤트입니다");
        }
        if (this.status == EventStatus.CLOSED) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_STATUS, "종료된 이벤트는 취소할 수 없습니다");
        }
        this.status = EventStatus.CANCELED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 이벤트 기본 정보 수정
     * DRAFT 상태에서만 수정 가능
     */
    public void updateBasicInfo(String title, String description, LocalDateTime startsAt, LocalDateTime endsAt) {
        if (this.status != EventStatus.DRAFT) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_STATUS, "DRAFT 상태의 이벤트만 수정할 수 있습니다");
        }
        this.title = validateTitle(title);
        this.description = validateDescription(description);
        this.startsAt = validateStartTime(startsAt);
        this.endsAt = validateEndTime(startsAt, endsAt);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 예약 현황 업데이트
     * 예약 서비스에서 좌석 예약 완료 시 호출
     */
    public void updateReservationSummary(int reservedSeats, long paidAmount) {
        this.reservedSeats = Math.max(0, reservedSeats);
        this.paidAmount = Math.max(0L, paidAmount);
        this.updatedAt = LocalDateTime.now();
    }

    // 🔍 검증 메서드들 (빠뜨렸던 부분!)
    
    /**
     * 제목 검증
     * @param title 검증할 제목
     * @return 유효한 제목
     * @throws ApiException 제목이 null이거나 빈 문자열, 또는 255자 초과인 경우
     */
    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TITLE, "이벤트 제목은 필수입니다");
        }
        if (title.length() > 255) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TITLE, "이벤트 제목은 255자를 초과할 수 없습니다");
        }
        return title.trim();
    }

    /**
     * 설명 검증
     * @param description 검증할 설명
     * @return 유효한 설명 (null 허용)
     * @throws ApiException 설명이 1000자를 초과하는 경우
     */
    private String validateDescription(String description) {
        if (description == null) {
            return null;
        }
        if (description.length() > 1000) {
            throw new ApiException(EventErrorCode.INVALID_PARAMETER, "설명은 1000자를 초과할 수 없습니다");
        }
        return description.trim();
    }

    /**
     * 시작 시간 검증
     * @param startsAt 검증할 시작 시간
     * @return 유효한 시작 시간
     * @throws ApiException 시작 시간이 null인 경우
     */
    private LocalDateTime validateStartTime(LocalDateTime startsAt) {
        if (startsAt == null) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TIME, "시작 시간은 필수입니다");
        }
        return startsAt;
    }

    /**
     * 종료 시간 검증
     * @param startsAt 시작 시간
     * @param endsAt 검증할 종료 시간
     * @return 유효한 종료 시간
     * @throws ApiException 종료 시간이 null이거나 시작 시간보다 이른 경우
     */
    private LocalDateTime validateEndTime(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (endsAt == null) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TIME, "종료 시간은 필수입니다");
        }
        if (startsAt != null && (endsAt.isBefore(startsAt) || endsAt.isEqual(startsAt))) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TIME, "종료 시간은 시작 시간보다 늦어야 합니다");
        }
        return endsAt;
    }

    /**
     * 행 수 검증
     * @param totalRows 검증할 행 수
     * @return 유효한 행 수
     * @throws ApiException 행 수가 1~50 범위를 벗어나는 경우
     */
    private int validateRows(int totalRows) {
        if (totalRows <= 0 || totalRows > 50) {
            throw new ApiException(EventErrorCode.INVALID_PARAMETER, "행 수는 1~50 사이여야 합니다");
        }
        return totalRows;
    }

    /**
     * 열 수 검증
     * @param totalCols 검증할 열 수
     * @return 유효한 열 수
     * @throws ApiException 열 수가 1~50 범위를 벗어나는 경우
     */
    private int validateCols(int totalCols) {
        if (totalCols <= 0 || totalCols > 50) {
            throw new ApiException(EventErrorCode.INVALID_PARAMETER, "열 수는 1~50 사이여야 합니다");
        }
        return totalCols;
    }

    // Getter 메서드들 (Read-Only 접근)
    public Long getEventId() { return eventId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public EventStatus getStatus() { return status; }
    public LocalDateTime getStartsAt() { return startsAt; }
    public LocalDateTime getEndsAt() { return endsAt; }
    public int getTotalRows() { return totalRows; }
    public int getTotalCols() { return totalCols; }
    public int getTotalSeats() { return totalSeats; }
    public int getReservedSeats() { return reservedSeats; }
    public long getPaidAmount() { return paidAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}