package com.example.flightapp.flight.persistence;

import com.example.flightapp.flight.domain.Flight;

public final class FlightMapper {

    private FlightMapper() {
    }

    public static Flight toDomain(FlightEntity entity) {
        Flight flight = new Flight();
        flight.setId(entity.getId());
        flight.setFlightNumber(entity.getFlightNumber());
        flight.setOriginAirportCode(entity.getOriginAirportCode());
        flight.setDestinationAirportCode(entity.getDestinationAirportCode());
        flight.setDepartureTime(entity.getDepartureTime());
        flight.setArrivalTime(entity.getArrivalTime());
        flight.setCapacity(entity.getCapacity());
        flight.setAvailableSeats(entity.getAvailableSeats());
        flight.setStatus(entity.getStatus());
        return flight;
    }

    public static FlightEntity toEntity(Flight flight) {
        FlightEntity entity = new FlightEntity();
        copyToEntity(flight, entity);
        return entity;
    }

    public static void copyToEntity(Flight flight, FlightEntity entity) {
        entity.setId(flight.getId());
        entity.setFlightNumber(flight.getFlightNumber());
        entity.setOriginAirportCode(flight.getOriginAirportCode());
        entity.setDestinationAirportCode(flight.getDestinationAirportCode());
        entity.setDepartureTime(flight.getDepartureTime());
        entity.setArrivalTime(flight.getArrivalTime());
        entity.setCapacity(flight.getCapacity());
        entity.setAvailableSeats(flight.getAvailableSeats());
        entity.setStatus(flight.getStatus());
    }
}
