package com.example.flightapp.flight.domain;

import java.time.LocalDateTime;

public class Flight {

    private Long id;
    private String flightNumber;
    private String originAirportCode;
    private String destinationAirportCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int capacity;
    private int availableSeats;
    private FlightStatus status;

    public Flight() {
    }

    public Flight(Long id, String flightNumber, String originAirportCode,
                  String destinationAirportCode, LocalDateTime departureTime,
                  LocalDateTime arrivalTime, int capacity) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.originAirportCode = originAirportCode;
        this.destinationAirportCode = destinationAirportCode;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.capacity = capacity;
        this.availableSeats = capacity;
        this.status = FlightStatus.SCHEDULED;
    }

    public boolean hasSeatAvailable() {
        return availableSeats > 0;
    }

    public void reserveSeat() {
        this.availableSeats = this.availableSeats - 1;
    }

    public void releaseSeat() {
        this.availableSeats = this.availableSeats + 1;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public String getOriginAirportCode() { return originAirportCode; }
    public void setOriginAirportCode(String originAirportCode) { this.originAirportCode = originAirportCode; }
    public String getDestinationAirportCode() { return destinationAirportCode; }
    public void setDestinationAirportCode(String destinationAirportCode) { this.destinationAirportCode = destinationAirportCode; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus status) { this.status = status; }
}
