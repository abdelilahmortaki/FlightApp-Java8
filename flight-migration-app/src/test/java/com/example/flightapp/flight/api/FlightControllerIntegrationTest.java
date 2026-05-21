package com.example.flightapp.flight.api;

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

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FlightControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getFlightByIdReturnsCreatedFlight() throws Exception {
        createFlight("EGA200");

        mockMvc.perform(get("/api/flights/1").with(httpBasic("viewer", "viewer123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.flightNumber").value("EGA200"));
    }

    @Test
    void patchStatusUpdatesFlightStatus() throws Exception {
        createFlight("EGA201");

        mockMvc.perform(
                patch("/api/flights/1/status")
                    .with(httpBasic("admin", "admin123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"BOARDING\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("BOARDING"));
    }

    @Test
    void patchCancelCancelsFlight() throws Exception {
        createFlight("EGA202");

        mockMvc.perform(patch("/api/flights/1/cancel").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void listFlightsWithoutPaginationKeepsArrayResponse() throws Exception {
        createFlight("EGA203");

        mockMvc.perform(get("/api/flights").with(httpBasic("viewer", "viewer123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].flightNumber").value("EGA203"));
    }

    @Test
    void listFlightsWithPaginationReturnsPageMetadata() throws Exception {
        createFlight("EGA204");
        createFlight("EGA205");
        createFlight("EGA206");

        mockMvc.perform(get("/api/flights?page=0&size=2").with(httpBasic("viewer", "viewer123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.items[0].flightNumber").value("EGA204"))
            .andExpect(jsonPath("$.items[1].flightNumber").value("EGA205"));
    }

    @Test
    void duplicateFlightNumberRejected() throws Exception {
        createFlight("EGA207");

        postFlight("EGA207", "LIS", "CDG", "2026-06-01T10:00:00", "2026-06-01T12:00:00", 10)
            .andExpect(status().isConflict());
    }

    @Test
    void sameOriginAndDestinationRejected() throws Exception {
        postFlight("EGA208", "LIS", "LIS", "2026-06-01T10:00:00", "2026-06-01T12:00:00", 10)
            .andExpect(status().isConflict());
    }

    @Test
    void departureAfterArrivalRejected() throws Exception {
        postFlight("EGA209", "LIS", "CDG", "2026-06-01T14:00:00", "2026-06-01T12:00:00", 10)
            .andExpect(status().isConflict());
    }

    @Test
    void invalidCapacityRejected() throws Exception {
        postFlight("EGA210", "LIS", "CDG", "2026-06-01T10:00:00", "2026-06-01T12:00:00", 0)
            .andExpect(status().isBadRequest());
    }

    @Test
    void departedFlightCannotBeModified() throws Exception {
        createFlight("EGA211");
        mockMvc.perform(
                patch("/api/flights/1/status")
                    .with(httpBasic("admin", "admin123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"DEPARTED\"}")
            )
            .andExpect(status().isOk());

        mockMvc.perform(patch("/api/flights/1/cancel").with(httpBasic("admin", "admin123")))
            .andExpect(status().isConflict());
    }

    private void createFlight(String flightNumber) throws Exception {
        postFlight(flightNumber, "LIS", "CDG", "2026-06-01T10:00:00", "2026-06-01T12:00:00", 10)
            .andExpect(status().isOk());
    }

    private org.springframework.test.web.servlet.ResultActions postFlight(String flightNumber,
                                                                          String origin,
                                                                          String destination,
                                                                          String departureTime,
                                                                          String arrivalTime,
                                                                          int capacity) throws Exception {
        return mockMvc.perform(
            post("/api/flights")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{"
                        + "\"flightNumber\":\"" + flightNumber + "\","
                        + "\"originAirportCode\":\"" + origin + "\","
                        + "\"destinationAirportCode\":\"" + destination + "\","
                        + "\"departureTime\":\"" + departureTime + "\","
                        + "\"arrivalTime\":\"" + arrivalTime + "\","
                        + "\"capacity\":" + capacity
                        + "}"
                )
        );
    }
}
