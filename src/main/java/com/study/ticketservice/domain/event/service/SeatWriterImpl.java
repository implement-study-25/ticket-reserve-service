package com.study.ticketservice.domain.event.service;

import com.study.ticketservice.domain.event.entity.Seat;
import com.study.ticketservice.domain.event.entity.SeatEntity;
import com.study.ticketservice.domain.event.repository.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Seat 쓰기 전용 Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class SeatWriterImpl implements SeatWriter {

    private final SeatJpaRepository seatJpaRepository;

    /**
     * 단일 좌석 저장 (생성 및 수정)
     * 좌석 상태 변경
     * 
     * @param seat 저장할 Seat 도메인 객체
     * @return 저장된 Seat 객체 (ID가 할당된 상태)
     */
    @Override
    public Seat save(Seat seat) {

        SeatEntity entity = SeatEntity.from(seat);

        SeatEntity savedEntity = seatJpaRepository.save(entity);
    
        return savedEntity.toDomain();
    }

    /**
     * 다중 좌석 일괄 저장
     * 이벤트 생성 시 모든 좌석을 한 번에 생성할 때 사용
     * 
     * @param seats 저장할 Seat 객체 리스트
     * @return 저장된 Seat 객체 리스트 (ID가 할당된 상태)
     */
    @Override
    public List<Seat> saveAll(List<Seat> seats) {

        List<SeatEntity> entities = seats.stream()
                .map(SeatEntity::from)
                .toList();

        List<SeatEntity> savedEntities = seatJpaRepository.saveAll(entities);
        
        return savedEntities.stream()
                .map(SeatEntity::toDomain) 
                .toList();
    }
}