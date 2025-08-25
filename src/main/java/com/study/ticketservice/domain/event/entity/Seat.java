package com.study.ticketservice.domain.event.entity;

import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.domain.event.enums.EventErrorCode;
import com.study.ticketservice.domain.event.enums.SeatStatus;

import java.time.LocalDateTime;

/**
 * Seat 도메인 엔티티
 * 이벤트의 개별 좌석을 나타내는 도메인 모델
 * Event 애그리게이트에 속하는 엔티티
 */
public class Seat {
    // 좌석 기본 정보
    private Long seatId;
    private Long eventId;
    private int row;
    private int col;
    private String seatNumber;
    
    // 가격 및 상태
    private int price;
    private SeatStatus status;
    
    // 임시 선점 관련
    private LocalDateTime holdExpiresAt;
    
    // 생성/수정 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Seat() {}

    /**
     * 새 좌석 생성용 생성자
     * 이벤트 생성 시 자동으로 좌석들이 생성될 때 사용
     */
    public Seat(Long eventId, int row, int col, int price) {
        this.eventId = eventId;
        this.row = row;
        this.col = col;
        this.seatNumber = generateSeatNumber(row, col); 
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
        this.holdExpiresAt = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 예약 과정에서 사용자가 좌석을 선택했을 때 다른 사용자의 예약을 방지
     */
    public void hold(int holdMinutes) {
        // 상태 검증: AVAILABLE 상태만 HOLD 가능
        if (this.status != SeatStatus.AVAILABLE) {
            throw new ApiException(EventErrorCode.SEAT_NOT_AVAILABLE, "예약 가능한 좌석이 아닙니다");
        }
        // 상태 변경 및 만료 시간 설정
        this.status = SeatStatus.HOLD;
        this.holdExpiresAt = LocalDateTime.now().plusMinutes(holdMinutes);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * HOLD 시간이 만료되거나 사용자가 예약을 취소했을 때 사용
     */
    public void release() {
        // 상태 검증: HOLD 상태만 해제 가능
        if (this.status != SeatStatus.HOLD) {
            throw new ApiException(EventErrorCode.INVALID_SEAT_STATUS, "HOLD 상태의 좌석만 해제할 수 있습니다");
        }
        // 상태를 AVAILABLE로 변경하고 만료 시간 제거
        this.status = SeatStatus.AVAILABLE;
        this.holdExpiresAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 결제가 완료되었을 때 HOLD 상태에서 SOLD 상태로 변경
     */
    public void sell() {
        // 상태 검증: HOLD 상태만 판매 가능
        if (this.status != SeatStatus.HOLD) {
            throw new ApiException(EventErrorCode.INVALID_SEAT_STATUS, "HOLD 상태의 좌석만 판매할 수 있습니다");
        }
        // 상태를 SOLD로 변경하고 만료 시간 제거
        this.status = SeatStatus.SOLD;
        this.holdExpiresAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     *만료된 HOLD를 정리할 때 사용
     */
    public boolean isExpired() {
        return status == SeatStatus.HOLD && 
               holdExpiresAt != null && 
               holdExpiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * 행과 열 번호를 기반으로 A1, B2 형태의 좌석 번호 생성
     */
    private String generateSeatNumber(int row, int col) {
        char rowChar = (char) ('A' + row - 1);
        return rowChar + String.valueOf(col);
    }

    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }
    
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    
    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }
    
    public LocalDateTime getHoldExpiresAt() { return holdExpiresAt; }
    public void setHoldExpiresAt(LocalDateTime holdExpiresAt) { this.holdExpiresAt = holdExpiresAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}