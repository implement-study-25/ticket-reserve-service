package com.study.ticketservice.domain.auth.enums;

public enum PrivilegeEnum {
    // ADMIN 권한
    EVENT_CREATE,
    EVENT_UPDATE,
    EVENT_CHANGE_STATUS,

    // 유저 권한
    EVENT_SEAT_RESERVE,
    EVENT_SEAT_CANCEL
}
