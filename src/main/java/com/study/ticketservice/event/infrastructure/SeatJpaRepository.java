package com.study.ticketservice.event.infrastructure;

import com.study.ticketservice.event.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {
    
    /**
     * @param eventId 이벤트 ID
     * @return 행, 열 순으로 정렬된 좌석 목록
     */
    List<SeatEntity> findByEventIdOrderByRowAscColAsc(Long eventId);
    
    /**
     * 특정 이벤트의 특정 상태 좌석 조회 (행, 열 순 정렬)
     * 예약 가능한 좌석만 보기, 판매된 좌석만 보기 등에 사용
     * 
     * @param eventId 이벤트 ID
     * @param status 조회할 좌석 상태
     * @return 해당 상태의 좌석 목록 (행, 열 순 정렬)
     */
    List<SeatEntity> findByEventIdAndStatusOrderByRowAscColAsc(Long eventId, SeatStatus status);
    
    /**
     * 특정 이벤트의 상태별 좌석 개수 조회
     * 이벤트 상세 정보에서 예약 현황 표시 시 사용
     * 
     * @param eventId 이벤트 ID
     * @param status 조회할 좌석 상태
     * @return 해당 상태의 좌석 개수
     */
    long countByEventIdAndStatus(Long eventId, SeatStatus status);
    
    /**
     * 만료된 HOLD 좌석 조회
     * 만료된 HOLD 상태를 AVAILABLE로 변경할 때 사용
     * @param currentTime 현재 시간
     * @return 만료된 HOLD 좌석 목록
     */
    @Query("SELECT s FROM SeatEntity s WHERE " +
           "s.status = 'HOLD' AND s.holdExpiresAt < :currentTime")
    List<SeatEntity> findExpiredHoldSeats(@Param("currentTime") LocalDateTime currentTime);
    

}