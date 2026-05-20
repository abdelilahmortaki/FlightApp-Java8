package com.example.flightapp.flight.domain;

import com.example.flightapp.common.domain.BusinessRuleException;

public class FlightPolicy {

    public void validateForCreation(Flight flight) {
        if (flight.getOriginAirportCode().equals(flight.getDestinationAirportCode())) {
            throw new BusinessRuleException("Origin and destination must be different");
        }
        if (!flight.getDepartureTime().isBefore(flight.getArrivalTime())) {
            throw new BusinessRuleException("Departure must be before arrival");
        }
        if (flight.getCapacity() <= 0) {
            throw new BusinessRuleException("Capacity must be positive");
        }
    }
}
