package com.study.ticketservice.event.infrastructure;

import com.study.ticketservice.event.domain.Event;
import com.study.ticketservice.event.domain.EventStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event 엔티티
 */
@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_events_status", columnList = "status"),
    @Index(name = "idx_events_starts_at", columnList = "starts_at"),
    @Index(name = "idx_events_status_starts_at", columnList = "status, starts_at")
})
@Getter
@NoArgsConstructor
public class EventEntity {

    /**
     * 이벤트 기본키
     * AUTO_INCREMENT 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    /**
     * 이벤트 제목
     * NOT NULL, 최대 255자
     */
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /**
     * 이벤트 설명
     * TEXT 타입으로 긴 설명 지원, NULL 허용
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 이벤트 상태
     * ENUM 타입으로 STRING 저장 (ORDINAL 대신 STRING 사용으로 안정성 확보)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    /**
     * 이벤트 시작 시간
     * UTC 기준으로 저장
     */
    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    /**
     * 이벤트 종료 시간
     * UTC 기준으로 저장
     */
    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    /**
     * 총 좌석 수
     * 계산된 값 (totalRows * totalCols)
     */
    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    /**
     * 총 행 수
     * 좌석 레이아웃 정보
     */
    @Column(name = "total_rows", nullable = false)
    private int totalRows;

    /**
     * 총 열 수
     * 좌석 레이아웃 정보
     */
    @Column(name = "total_cols", nullable = false)
    private int totalCols;

    /**
     * 현재 예약된 좌석 수
     * 기본값 0, 예약 시스템에서 업데이트
     */
    @Column(name = "reserved_seats", nullable = false)
    private int reservedSeats = 0;

    /**
     * 결제 완료 누적 금액
     * 기본값 0, 결제 완료 시 누적
     */
    @Column(name = "paid_amount", nullable = false)
    private long paidAmount = 0L;

    /**
     * 생성 시간
     * 엔티티 생성 시 자동 설정
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     * 엔티티 수정 시마다 업데이트
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * @return Event 도메인 객체
     */
    public Event toDomain() {
        return new Event(
            this.eventId,
            this.title,
            this.description,
            this.status,
            this.startsAt,
            this.endsAt,
            this.totalRows,
            this.totalCols,
            this.totalSeats,
            this.reservedSeats,
            this.paidAmount,
            this.createdAt,
            this.updatedAt
        );
    }

    /**
     * Domain -> Infrastructure 변환 팩토리 메서드
     * 도메인 객체를 JPA 엔티티로 변환
     * 
     * @param domain Event 도메인 객체
     * @return EventEntity JPA 엔티티
     */
    public static EventEntity from(Event domain) {
        EventEntity entity = new EventEntity();
        entity.eventId = domain.getEventId();
        entity.title = domain.getTitle();
        entity.description = domain.getDescription();
        entity.status = domain.getStatus();
        entity.startsAt = domain.getStartsAt();
        entity.endsAt = domain.getEndsAt();
        entity.totalSeats = domain.getTotalSeats();
        entity.totalRows = domain.getTotalRows();
        entity.totalCols = domain.getTotalCols();
        entity.reservedSeats = domain.getReservedSeats();
        entity.paidAmount = domain.getPaidAmount();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }
}