package com.example.flightapp.flight.domain;

import com.example.flightapp.common.domain.BusinessRuleException;

public class FlightPolicy {

    public void validateForCreation(Flight flight) {
        if (flight.getFlightNumber() == null || flight.getFlightNumber().trim().isEmpty()) {
            throw new BusinessRuleException("Flight number is required");
        }
        if (flight.getOriginAirportCode() == null || flight.getOriginAirportCode().trim().isEmpty()) {
            throw new BusinessRuleException("Origin airport is required");
        }
        if (flight.getDestinationAirportCode() == null || flight.getDestinationAirportCode().trim().isEmpty()) {
            throw new BusinessRuleException("Destination airport is required");
        }
        if (flight.getDepartureTime() == null || flight.getArrivalTime() == null) {
            throw new BusinessRuleException("Flight times are required");
        }
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
