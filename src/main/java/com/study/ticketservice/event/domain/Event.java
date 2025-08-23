package com.study.ticketservice.event.domain;
import com.study.ticketservice.common.exception.ApiException;
import java.time.LocalDateTime;

/**
 * Event 도메인 엔티티 - Aggregate Root
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

    public Event() {}

    /**
     * 새 이벤트 생성용 생성자
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

    // 비즈니스 메서드들
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

    public void close() {
        if (this.status != EventStatus.PUBLISHED) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_STATUS, "PUBLISHED 상태의 이벤트만 종료할 수 있습니다");
        }
        this.status = EventStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

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

    public void updateReservationSummary(int reservedSeats, long paidAmount) {
        this.reservedSeats = Math.max(0, reservedSeats);
        this.paidAmount = Math.max(0L, paidAmount);
        this.updatedAt = LocalDateTime.now();
    }

    // 검증 메서드들
    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TITLE, "이벤트 제목은 필수입니다");
        }
        if (title.length() > 255) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TITLE, "이벤트 제목은 255자를 초과할 수 없습니다");
        }
        return title.trim();
    }

    private String validateDescription(String description) {
        if (description == null) {
            return null;
        }
        if (description.length() > 1000) {
            throw new ApiException(EventErrorCode.INVALID_PARAMETER, "설명은 1000자를 초과할 수 없습니다");
        }
        return description.trim();
    }

    private LocalDateTime validateStartTime(LocalDateTime startsAt) {
        if (startsAt == null) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TIME, "시작 시간은 필수입니다");
        }
        return startsAt;
    }

    private LocalDateTime validateEndTime(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (endsAt == null) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TIME, "종료 시간은 필수입니다");
        }
        if (startsAt != null && (endsAt.isBefore(startsAt) || endsAt.isEqual(startsAt))) {
            throw new ApiException(EventErrorCode.INVALID_EVENT_TIME, "종료 시간은 시작 시간보다 늦어야 합니다");
        }
        return endsAt;
    }

    private int validateRows(int totalRows) {
        if (totalRows <= 0 || totalRows > 50) {
            throw new ApiException(EventErrorCode.INVALID_PARAMETER, "행 수는 1~50 사이여야 합니다");
        }
        return totalRows;
    }

    private int validateCols(int totalCols) {
        if (totalCols <= 0 || totalCols > 50) {
            throw new ApiException(EventErrorCode.INVALID_PARAMETER, "열 수는 1~50 사이여야 합니다");
        }
        return totalCols;
    }

    // Getter만 제공 (Read-Only 접근)
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