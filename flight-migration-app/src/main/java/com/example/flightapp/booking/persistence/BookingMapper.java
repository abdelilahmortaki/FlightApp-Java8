package com.example.flightapp.booking.persistence;

import com.example.flightapp.booking.domain.Booking;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static Booking toDomain(BookingEntity entity) {
        Booking booking = new Booking();
        booking.setId(entity.getId());
        booking.setBookingReference(entity.getBookingReference());
        booking.setFlightId(entity.getFlightId());
        booking.setPassengerName(entity.getPassengerName());
        booking.setPassengerEmail(entity.getPassengerEmail());
        booking.setStatus(entity.getStatus());
        booking.setCreatedAt(entity.getCreatedAt());
        return booking;
    }

    public static BookingEntity toEntity(Booking booking) {
        BookingEntity entity = new BookingEntity();
        copyToEntity(booking, entity);
        return entity;
    }

    public static void copyToEntity(Booking booking, BookingEntity entity) {
        entity.setId(booking.getId());
        entity.setBookingReference(booking.getBookingReference());
        entity.setFlightId(booking.getFlightId());
        entity.setPassengerName(booking.getPassengerName());
        entity.setPassengerEmail(booking.getPassengerEmail());
        entity.setStatus(booking.getStatus());
        entity.setCreatedAt(booking.getCreatedAt());
    }
}
