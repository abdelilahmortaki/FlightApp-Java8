package com.example.flightapp.flight.api;

import com.example.flightapp.flight.domain.FlightStatus;
import javax.validation.constraints.NotNull;

public class FlightStatusRequest {

    @NotNull
    private FlightStatus status;

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }
}
