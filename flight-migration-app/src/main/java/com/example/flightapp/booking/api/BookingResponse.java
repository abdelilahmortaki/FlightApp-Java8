package com.example.flightapp.booking.api;

import com.example.flightapp.booking.domain.Booking;
import com.example.flightapp.booking.domain.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private String bookingReference;
    private Long flightId;
    private String passengerName;
    private String passengerEmail;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public static BookingResponse from(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.id = booking.getId();
        response.bookingReference = booking.getBookingReference();
        response.flightId = booking.getFlightId();
        response.passengerName = booking.getPassengerName();
        response.passengerEmail = booking.getPassengerEmail();
        response.status = booking.getStatus();
        response.createdAt = booking.getCreatedAt();
        return response;
    }

    public Long getId() { return id; }
    public String getBookingReference() { return bookingReference; }
    public Long getFlightId() { return flightId; }
    public String getPassengerName() { return passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
