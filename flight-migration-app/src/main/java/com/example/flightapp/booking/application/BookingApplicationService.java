package com.example.flightapp.booking.application;

import com.example.flightapp.booking.domain.Booking;
import com.example.flightapp.booking.domain.BookingStatus;
import com.example.flightapp.common.domain.BusinessRuleException;
import com.example.flightapp.flight.application.FlightApplicationService;
import com.example.flightapp.flight.domain.Flight;
import com.example.flightapp.flight.domain.FlightStatus;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class BookingApplicationService {

    private final Map<Long, Booking> bookings = new LinkedHashMap<Long, Booking>();
    private final AtomicLong sequence = new AtomicLong(1L);
    private final FlightApplicationService flightService;

    public BookingApplicationService(FlightApplicationService flightService) {
        this.flightService = flightService;
    }

    public Booking create(Long flightId, String passengerName, String passengerEmail) {
        Flight flight = flightService.findById(flightId);
        if (FlightStatus.CANCELLED.equals(flight.getStatus())) {
            throw new BusinessRuleException("Cannot book a cancelled flight");
        }
        if (!flight.hasSeatAvailable()) {
            throw new BusinessRuleException("Flight is full");
        }
        if (alreadyBooked(flightId, passengerEmail)) {
            throw new BusinessRuleException("Passenger already booked on this flight");
        }
        flight.reserveSeat();
        Booking booking = new Booking(
            sequence.getAndIncrement(),
            UUID.randomUUID().toString(),
            flightId,
            passengerName,
            passengerEmail
        );
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public Booking cancel(Long id) {
        Booking booking = bookings.get(id);
        if (booking == null) {
            throw new BusinessRuleException("Booking not found");
        }
        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw new BusinessRuleException("Booking already cancelled");
        }
        Flight flight = flightService.findById(booking.getFlightId());
        flight.releaseSeat();
        booking.setStatus(BookingStatus.CANCELLED);
        return booking;
    }

    private boolean alreadyBooked(Long flightId, String passengerEmail) {
        for (Booking booking : bookings.values()) {
            if (flightId.equals(booking.getFlightId())
                && passengerEmail.equalsIgnoreCase(booking.getPassengerEmail())
                && BookingStatus.CONFIRMED.equals(booking.getStatus())) {
                return true;
            }
        }
        return false;
    }
}
