package com.study.ticketservice.common.response;

/**
 * 페이지 정보 전용 DTO
 */
public record PageInfo(
    int number,
    int size,
    long totalElements,
    int totalPages
) {
    public static PageInfo from(org.springframework.data.domain.Page<?> page) {
        return new PageInfo(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
