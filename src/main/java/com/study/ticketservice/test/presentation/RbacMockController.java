package com.study.ticketservice.test.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mock")
public class RbacMockController {

    // ADMIN 영역
    @GetMapping("/admin/event/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> adminCreateByRole() {
        return ResponseEntity.ok("EVENT_CREATE_OK");
    }

    @GetMapping("/admin/event/create/privilege")
    @PreAuthorize("hasAuthority('EVENT_CREATE')")
    public ResponseEntity<String> adminCreateByPrivilege() {
        return ResponseEntity.ok("EVENT_CREATE_OK");
    }

    @GetMapping("/admin/event/update/privilege")
    @PreAuthorize("hasAuthority('EVENT_UPDATE')")
    public ResponseEntity<String> adminUpdateByPrivilege() {
        return ResponseEntity.ok("EVENT_UPDATE_OK");
    }

    @GetMapping("/admin/event/change-status/privilege")
    @PreAuthorize("hasAuthority('EVENT_CHANGE_STATUS')")
    public ResponseEntity<String> adminChangeStatusByPrivilege() {
        return ResponseEntity.ok("EVENT_CHANGE_STATUS_OK");
    }

    // USER 영역
    @GetMapping("/user/event/seat/reserve")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> userReserveByRole() {
        return ResponseEntity.ok("EVENT_SEAT_REVERSE_OK");
    }

    @GetMapping("/user/event/seat/reserve/privilege")
    @PreAuthorize("hasAuthority('EVENT_SEAT_RESERVE')")
    public ResponseEntity<String> userReserveByPrivilege() {
        return ResponseEntity.ok("EVENT_SEAT_REVERSE_OK");
    }

    @GetMapping("/user/event/seat/cancel/privilege")
    @PreAuthorize("hasAuthority('EVENT_SEAT_CANCEL')")
    public ResponseEntity<String> userCancelByPrivilege() {
        return ResponseEntity.ok("EVENT_SEAT_CANCEL_OK");
    }
}


