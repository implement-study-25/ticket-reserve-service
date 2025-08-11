package com.study.ticketservice.test.infrastructure;

import com.study.ticketservice.test.application.TestWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TestWriterImpl implements TestWriter {
    private final TestJpaRepository testJpaRepository;

}
