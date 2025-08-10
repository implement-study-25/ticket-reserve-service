package com.study.ticketservice.test.application.dto;

import com.study.ticketservice.common.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestErrorCode implements ErrorCode {
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String message;
}
