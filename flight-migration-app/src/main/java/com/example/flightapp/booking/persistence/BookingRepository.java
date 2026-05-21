package com.example.flightapp.booking.persistence;

import com.example.flightapp.booking.domain.BookingStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    boolean existsByFlightIdAndPassengerEmailIgnoreCaseAndStatus(Long flightId, String passengerEmail, BookingStatus status);

    List<BookingEntity> findByStatus(BookingStatus status);
}
