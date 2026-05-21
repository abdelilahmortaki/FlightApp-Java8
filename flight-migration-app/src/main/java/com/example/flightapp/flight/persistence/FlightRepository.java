package com.example.flightapp.flight.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<FlightEntity, Long> {

    boolean existsByFlightNumberIgnoreCase(String flightNumber);

    Optional<FlightEntity> findByFlightNumberIgnoreCase(String flightNumber);
}
