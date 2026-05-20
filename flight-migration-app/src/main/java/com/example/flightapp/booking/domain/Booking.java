package com.example.flightapp.booking.domain;

import java.time.LocalDateTime;

public class Booking {

    private Long id;
    private String bookingReference;
    private Long flightId;
    private String passengerName;
    private String passengerEmail;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public Booking() {
    }

    public Booking(Long id, String bookingReference, Long flightId,
                   String passengerName, String passengerEmail) {
        this.id = id;
        this.bookingReference = bookingReference;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.status = BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
