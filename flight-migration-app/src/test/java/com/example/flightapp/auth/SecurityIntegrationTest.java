package com.example.flightapp.auth;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedMeReturns401() throws Exception {
        mockMvc.perform(get("/api/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void viewerCannotCreateBooking() throws Exception {
        mockMvc.perform(
                post("/api/bookings")
                    .with(httpBasic("viewer", "viewer123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"flightId\":1,\"passengerName\":\"View User\",\"passengerEmail\":\"view@example.com\"}")
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void agentCannotLaunchBatch() throws Exception {
        mockMvc.perform(
                post("/api/batch/jobs/flightImportJob/launch")
                    .with(httpBasic("agent", "agent123"))
            )
            .andExpect(status().isForbidden());
    }
}
