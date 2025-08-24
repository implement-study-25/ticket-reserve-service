package com.study.ticketservice.event.infrastructure;

import com.study.ticketservice.event.domain.Seat;
import com.study.ticketservice.event.domain.SeatStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "seats", indexes = {
    @Index(name = "idx_seats_event_id_status", columnList = "event_id, status"),
    @Index(name = "idx_seats_hold_expires_at", columnList = "hold_expires_at")
})
@Getter
@NoArgsConstructor
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "`row`", nullable = false)
    private int row;

    @Column(name = "`col`", nullable = false)
    private int col;

    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber;

    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeatStatus status;

    /**
     * HOLD 만료 시간
     */
    @Column(name = "hold_expires_at")
    private LocalDateTime holdExpiresAt;

    /**
     * 생성 시간
     * 좌석 생성 시 자동 설정
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * @return Seat 도메인 객체
     */
    public Seat toDomain() {
        Seat seat = new Seat();
        seat.setSeatId(this.seatId);
        seat.setEventId(this.eventId);
        seat.setRow(this.row);
        seat.setCol(this.col);
        seat.setSeatNumber(this.seatNumber);
        seat.setPrice(this.price);
        seat.setStatus(this.status);
        seat.setHoldExpiresAt(this.holdExpiresAt);
        seat.setCreatedAt(this.createdAt);
        seat.setUpdatedAt(this.updatedAt);
        return seat;
    }

    /*
     * @param domain Seat 도메인 객체
     * @return SeatEntity JPA 엔티티
     */
    public static SeatEntity from(Seat domain) {
        SeatEntity entity = new SeatEntity();
        entity.seatId = domain.getSeatId();
        entity.eventId = domain.getEventId();
        entity.row = domain.getRow();
        entity.col = domain.getCol();
        entity.seatNumber = domain.getSeatNumber();
        entity.price = domain.getPrice();
        entity.status = domain.getStatus();
        entity.holdExpiresAt = domain.getHoldExpiresAt();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }
}