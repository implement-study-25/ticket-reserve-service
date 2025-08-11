package com.study.ticketservice.test.application;

import com.study.ticketservice.test.domain.Test;

public interface TestReader {
    Test findById(Long id);
}
