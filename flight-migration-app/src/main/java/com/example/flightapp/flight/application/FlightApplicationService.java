package com.example.flightapp.flight.application;

import com.example.flightapp.common.domain.BusinessRuleException;
import com.example.flightapp.flight.domain.Flight;
import com.example.flightapp.flight.domain.FlightPolicy;
import com.example.flightapp.flight.domain.FlightStatus;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class FlightApplicationService {

    private final Map<Long, Flight> flights = new LinkedHashMap<Long, Flight>();
    private final AtomicLong sequence = new AtomicLong(1L);
    private final FlightPolicy flightPolicy = new FlightPolicy();

    public Flight create(Flight flight) {
        flightPolicy.validateForCreation(flight);
        Long id = sequence.getAndIncrement();
        flight.setId(id);
        flights.put(id, flight);
        return flight;
    }

    public List<Flight> findAll() {
        return new ArrayList<Flight>(flights.values());
    }

    public Flight findById(Long id) {
        Flight flight = flights.get(id);
        if (flight == null) {
            throw new BusinessRuleException("Flight not found");
        }
        return flight;
    }

    public Flight updateStatus(Long id, FlightStatus status) {
        Flight flight = findById(id);
        flight.setStatus(status);
        return flight;
    }
}
