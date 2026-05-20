package com.example.flightapp.flight.api;

import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FlightRequest {

    @NotBlank
    private String flightNumber;

    @NotBlank
    private String originAirportCode;

    @NotBlank
    private String destinationAirportCode;

    @NotNull
    private LocalDateTime departureTime;

    @NotNull
    private LocalDateTime arrivalTime;

    @Min(1)
    private int capacity;

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
}
