package com.study.ticketservice.test.presentation;

import com.study.ticketservice.common.exception.ApiException;
import com.study.ticketservice.common.response.ApiResponse;
import com.study.ticketservice.test.application.dto.TestErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestApiController {

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Void>> test() {
        return ApiResponse.success(null);
    }

    @GetMapping("/error1")
    public ResponseEntity<ApiResponse<Void>> error() {
        throw new ApiException(TestErrorCode.NOT_FOUND, null);
    }
}
