package com.example.flightapp.booking.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:bookingtest;DB_CLOSE_ON_EXIT=FALSE")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createBookingDecrementsSeatsAndCanBeRead() throws Exception {
        createFlight("BKG100", 2);

        createBooking("user1@example.com")
            .andExpect(jsonPath("$.status").value("CONFIRMED"));

        mockMvc.perform(get("/api/bookings/1").with(httpBasic("agent", "agent123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.passengerEmail").value("user1@example.com"));
        mockMvc.perform(get("/api/flights/1").with(httpBasic("viewer", "viewer123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableSeats").value(1));
    }

    @Test
    void duplicatePassengerEmailRejected() throws Exception {
        createFlight("BKG101", 2);
        createBooking("dupe@example.com");

        createBooking("dupe@example.com")
            .andExpect(status().isConflict());
    }

    @Test
    void invalidPassengerEmailRejected() throws Exception {
        createFlight("BKG102", 2);

        createBooking("not-email")
            .andExpect(status().isBadRequest());
    }

    @Test
    void cancelledAndFullFlightsCannotBeBooked() throws Exception {
        createFlight("BKG103", 1);
        createBooking("full@example.com");
        createBooking("second@example.com")
            .andExpect(status().isConflict());

        createFlight("BKG104", 1);
        mockMvc.perform(patch("/api/flights/2/cancel").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk());
        createBookingForFlight(2L, "cancelled@example.com")
            .andExpect(status().isConflict());
    }

    @Test
    void cancelBookingRestoresSeatAndCannotCancelTwice() throws Exception {
        createFlight("BKG105", 1);
        createBooking("cancel@example.com");

        mockMvc.perform(patch("/api/bookings/1/cancel").with(httpBasic("agent", "agent123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
        mockMvc.perform(get("/api/flights/1").with(httpBasic("viewer", "viewer123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableSeats").value(1));
        mockMvc.perform(patch("/api/bookings/1/cancel").with(httpBasic("agent", "agent123")))
            .andExpect(status().isConflict());
    }

    private org.springframework.test.web.servlet.ResultActions createBooking(String email) throws Exception {
        return createBookingForFlight(1L, email);
    }

    private org.springframework.test.web.servlet.ResultActions createBookingForFlight(Long flightId, String email) throws Exception {
        return mockMvc.perform(
            post("/api/bookings")
                .with(httpBasic("agent", "agent123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"flightId\":" + flightId + ","
                    + "\"passengerName\":\"Test User\","
                    + "\"passengerEmail\":\"" + email + "\""
                    + "}")
        );
    }

    private void createFlight(String flightNumber, int capacity) throws Exception {
        mockMvc.perform(
                post("/api/flights")
                    .with(httpBasic("admin", "admin123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{"
                        + "\"flightNumber\":\"" + flightNumber + "\","
                        + "\"originAirportCode\":\"LIS\","
                        + "\"destinationAirportCode\":\"CDG\","
                        + "\"departureTime\":\"2026-06-01T10:00:00\","
                        + "\"arrivalTime\":\"2026-06-01T12:00:00\","
                        + "\"capacity\":" + capacity
                        + "}")
            )
            .andExpect(status().isOk());
    }
}
