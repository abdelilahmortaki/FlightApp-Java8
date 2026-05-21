package com.example.flightapp.booking.application;

import com.example.flightapp.booking.domain.Booking;
import com.example.flightapp.booking.domain.BookingStatus;
import com.example.flightapp.booking.persistence.BookingEntity;
import com.example.flightapp.booking.persistence.BookingMapper;
import com.example.flightapp.booking.persistence.BookingRepository;
import com.example.flightapp.common.domain.BusinessRuleException;
import com.example.flightapp.flight.application.FlightApplicationService;
import com.example.flightapp.flight.domain.Flight;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingApplicationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final BookingRepository repository;
    private final FlightApplicationService flightService;

    public BookingApplicationService(BookingRepository repository, FlightApplicationService flightService) {
        this.repository = repository;
        this.flightService = flightService;
    }

    @Transactional
    public Booking create(Long flightId, String passengerName, String passengerEmail) {
        validateEmail(passengerEmail);
        Flight flight = flightService.findById(flightId);
        if (alreadyBooked(flightId, passengerEmail)) {
            throw new BusinessRuleException("Passenger already booked on this flight");
        }
        flight.ensureBookable();
        if (!flight.hasSeatAvailable()) {
            throw new BusinessRuleException("Flight is full");
        }
        flightService.reserveSeat(flightId);
        Booking booking = new Booking(
            null,
            UUID.randomUUID().toString(),
            flightId,
            passengerName,
            passengerEmail
        );
        try {
            return BookingMapper.toDomain(repository.save(BookingMapper.toEntity(booking)));
        } catch (DataIntegrityViolationException exception) {
            flightService.releaseSeat(flightId);
            throw new BusinessRuleException("Passenger already booked on this flight");
        }
    }

    @Transactional(readOnly = true)
    public Booking findById(Long id) {
        return BookingMapper.toDomain(findEntityById(id));
    }

    @Transactional
    public Booking cancel(Long id) {
        BookingEntity entity = findEntityById(id);
        Booking booking = BookingMapper.toDomain(entity);
        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw new BusinessRuleException("Booking already cancelled");
        }
        flightService.releaseSeat(booking.getFlightId());
        booking.setStatus(BookingStatus.CANCELLED);
        BookingMapper.copyToEntity(booking, entity);
        return BookingMapper.toDomain(repository.save(entity));
    }

    private boolean alreadyBooked(Long flightId, String passengerEmail) {
        return repository.existsByFlightIdAndPassengerEmailIgnoreCaseAndStatus(
            flightId,
            passengerEmail,
            BookingStatus.CONFIRMED
        );
    }

    private BookingEntity findEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BusinessRuleException("Booking not found"));
    }

    private void validateEmail(String passengerEmail) {
        if (passengerEmail == null || !EMAIL_PATTERN.matcher(passengerEmail).matches()) {
            throw new BusinessRuleException("Passenger email is invalid");
        }
    }
}
