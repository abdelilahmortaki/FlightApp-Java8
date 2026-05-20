package com.example.flightapp.flight.api;

import com.example.flightapp.flight.domain.Flight;
import com.example.flightapp.flight.domain.FlightStatus;
import java.time.LocalDateTime;

public class FlightResponse {

    private Long id;
    private String flightNumber;
    private String originAirportCode;
    private String destinationAirportCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int capacity;
    private int availableSeats;
    private FlightStatus status;

    public static FlightResponse from(Flight flight) {
        FlightResponse response = new FlightResponse();
        response.id = flight.getId();
        response.flightNumber = flight.getFlightNumber();
        response.originAirportCode = flight.getOriginAirportCode();
        response.destinationAirportCode = flight.getDestinationAirportCode();
        response.departureTime = flight.getDepartureTime();
        response.arrivalTime = flight.getArrivalTime();
        response.capacity = flight.getCapacity();
        response.availableSeats = flight.getAvailableSeats();
        response.status = flight.getStatus();
        return response;
    }

    public Long getId() { return id; }
    public String getFlightNumber() { return flightNumber; }
    public String getOriginAirportCode() { return originAirportCode; }
    public String getDestinationAirportCode() { return destinationAirportCode; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public int getCapacity() { return capacity; }
    public int getAvailableSeats() { return availableSeats; }
    public FlightStatus getStatus() { return status; }
}
