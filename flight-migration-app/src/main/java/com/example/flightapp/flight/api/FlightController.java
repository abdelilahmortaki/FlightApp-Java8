package com.example.flightapp.flight.api;

import com.example.flightapp.flight.application.FlightApplicationService;
import com.example.flightapp.flight.domain.Flight;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightApplicationService service;

    public FlightController(FlightApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<FlightResponse> findAll() {
        List<FlightResponse> responses = new ArrayList<FlightResponse>();
        for (Flight flight : service.findAll()) {
            responses.add(FlightResponse.from(flight));
        }
        return responses;
    }

    @PostMapping
    public FlightResponse create(@Valid @RequestBody FlightRequest request) {
        Flight flight = new Flight(
            null,
            request.getFlightNumber(),
            request.getOriginAirportCode(),
            request.getDestinationAirportCode(),
            request.getDepartureTime(),
            request.getArrivalTime(),
            request.getCapacity()
        );
        return FlightResponse.from(service.create(flight));
    }

    @PatchMapping("/{id}/status")
    public FlightResponse updateStatus(@PathVariable Long id,
                                       @Valid @RequestBody FlightStatusRequest request) {
        return FlightResponse.from(service.updateStatus(id, request.getStatus()));
    }
}
