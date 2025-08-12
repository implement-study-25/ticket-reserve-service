package com.study.ticketservice.test.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthTestTestController {

    @Autowired
    MockMvc mockMvc;

    @Nested
    @DisplayName("ADMIN 영역")
    class AdminArea {
        @Test
        @WithMockUser(authorities = {"ADMIN"})
        @DisplayName("ADMIN 권한으로 /admin/event/create 접근 성공")
        void adminRole_canAccess_adminCreate() throws Exception {
            mockMvc.perform(get("/api/mock/admin/event/create").accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andExpect(content().string("EVENT_CREATE_OK"));
        }

        @Test
        @WithMockUser(authorities = {"EVENT_CREATE"})
        @DisplayName("EVENT_CREATE 권한으로 /admin/event/create//privilege 접근 성공")
        void eventCreatePrivilege_canAccess_adminCreateByPrivilege() throws Exception {
            mockMvc.perform(get("/api/mock/admin/event/create/privilege"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("EVENT_CREATE_OK"));
        }

        @Test
        @WithMockUser(authorities = {"USER"})
        @DisplayName("USER 권한으로 /admin/event/update/privilege 접근 실패(403)")
        void user_cannotAccess_adminUpdate() throws Exception {
            mockMvc.perform(get("/api/mock/admin/event/update/privilege"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = {"EVENT_UPDATE"})
        @DisplayName("EVENT_UPDATE 권한으로 /admin/event/update/privilege 접근 성공")
        void eventUpdatePrivilege_canAccess_adminUpdate() throws Exception {
            mockMvc.perform(get("/api/mock/admin/event/update/privilege"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("EVENT_UPDATE_OK"));
        }

        @Test
        @WithMockUser(authorities = {"EVENT_CHANGE_STATUS"})
        @DisplayName("EVENT_CHANGE_STATUS 권한으로 /admin/event/change-status/privilege 접근 성공")
        void changeStatusPrivilege_canAccess() throws Exception {
            mockMvc.perform(get("/api/mock/admin/event/change-status/privilege"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("EVENT_CHANGE_STATUS_OK"));
        }
    }

    @Nested
    @DisplayName("USER 영역")
    class UserArea {
        @Test
        @WithMockUser(authorities = {"USER"})
        @DisplayName("USER 권한으로 /user/event/seat/reserve 접근 성공")
        void userRole_canReserve() throws Exception {
            mockMvc.perform(get("/api/mock/user/event/seat/reserve"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("EVENT_SEAT_REVERSE_OK"));
        }

        @Test
        @WithMockUser(authorities = {"EVENT_SEAT_RESERVE"})
        @DisplayName("EVENT_SEAT_RESERVE 권한으로 /user/event/seat/reserve/privilege 접근 성공")
        void reservePrivilege_canReserveByPrivilege() throws Exception {
            mockMvc.perform(get("/api/mock/user/event/seat/reserve/privilege"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("EVENT_SEAT_REVERSE_OK"));
        }

        @Test
        @WithMockUser(authorities = {"EVENT_SEAT_CANCEL"})
        @DisplayName("EVENT_SEAT_CANCEL 권한으로 /user/event/seat/cancel/privilege 접근 성공")
        void cancelPrivilege_canCancel() throws Exception {
            mockMvc.perform(get("/api/mock/user/event/seat/cancel/privilege"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("EVENT_SEAT_CANCEL_OK"));
        }

        @Test
        @WithMockUser(authorities = {"USER"})
        @DisplayName("USER 권한으로 /admin/event/create/privilege 접근 실패(403)")
        void user_cannotAccess_adminCreateByPrivilege() throws Exception {
            mockMvc.perform(get("/api/mock/admin/event/create/privilege"))
                    .andExpect(status().isForbidden());
        }
    }
}


