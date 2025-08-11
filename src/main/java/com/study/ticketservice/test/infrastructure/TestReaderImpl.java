package com.study.ticketservice.test.infrastructure;

import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.test.application.TestReader;
import com.study.ticketservice.test.application.dto.TestErrorCode;
import com.study.ticketservice.test.domain.Test;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TestReaderImpl implements TestReader {
    private final TestJpaRepository testJpaRepository;

    @Override
    public Test findById(Long id) {
        return testJpaRepository.findById(id)
                .orElseThrow(() -> new ApiException(TestErrorCode.NOT_FOUND))
                .toDomain();
    }
}
