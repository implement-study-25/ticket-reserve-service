package com.study.ticketservice.event.infrastructure;

import com.study.ticketservice.event.domain.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {
    
    /**
     * @param keyword 검색 키워드 (null이면 전체 조회)
     * @param pageable 페이징 정보
     * @return 페이징된 이벤트 엔티티 목록
     */
    @Query("SELECT e FROM EventEntity e WHERE " +
           "(:keyword IS NULL OR e.title LIKE %:keyword%) " +
           "ORDER BY e.createdAt DESC")
    Page<EventEntity> findEventsWithKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * @param keyword 검색 키워드
     * @param status 이벤트 상태
     * @param pageable 페이징 정보
     * @return 페이징된 이벤트 엔티티 목록
     */
    @Query("SELECT e FROM EventEntity e WHERE " +
           "(:keyword IS NULL OR e.title LIKE %:keyword%) " +
           "AND (:status IS NULL OR e.status = :status) " +
           "ORDER BY e.createdAt DESC")
    Page<EventEntity> findEventsWithKeywordAndStatus(@Param("keyword") String keyword, 
                                                    @Param("status") EventStatus status, 
                                                    Pageable pageable);
    
}