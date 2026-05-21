package com.example.flightapp.flight.application;

import com.example.flightapp.common.domain.BusinessRuleException;
import com.example.flightapp.flight.domain.Flight;
import com.example.flightapp.flight.domain.FlightPolicy;
import com.example.flightapp.flight.domain.FlightStatus;
import com.example.flightapp.flight.persistence.FlightEntity;
import com.example.flightapp.flight.persistence.FlightMapper;
import com.example.flightapp.flight.persistence.FlightRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlightApplicationService {

    private final FlightRepository repository;
    private final FlightPolicy flightPolicy = new FlightPolicy();

    public FlightApplicationService(FlightRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Flight create(Flight flight) {
        flightPolicy.validateForCreation(flight);
        ensureUniqueFlightNumber(flight.getFlightNumber());
        try {
            return FlightMapper.toDomain(repository.save(FlightMapper.toEntity(flight)));
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessRuleException("Flight number already exists");
        }
    }

    @Transactional(readOnly = true)
    public List<Flight> findAll() {
        List<Flight> flights = new ArrayList<Flight>();
        for (FlightEntity entity : repository.findAll()) {
            flights.add(FlightMapper.toDomain(entity));
        }
        return flights;
    }

    @Transactional(readOnly = true)
    public List<Flight> findPage(int page, int size) {
        if (page < 0) {
            throw new BusinessRuleException("Page index must not be negative");
        }
        if (size <= 0) {
            throw new BusinessRuleException("Page size must be positive");
        }
        List<Flight> flights = new ArrayList<Flight>();
        for (FlightEntity entity : repository.findAll(PageRequest.of(page, size)).getContent()) {
            flights.add(FlightMapper.toDomain(entity));
        }
        return flights;
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    public Flight findById(Long id) {
        return FlightMapper.toDomain(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public Flight findByFlightNumber(String flightNumber) {
        FlightEntity entity = repository.findByFlightNumberIgnoreCase(flightNumber)
            .orElseThrow(() -> new BusinessRuleException("Flight not found"));
        return FlightMapper.toDomain(entity);
    }

    @Transactional
    public Flight updateStatus(Long id, FlightStatus status) {
        FlightEntity entity = findEntityById(id);
        Flight flight = FlightMapper.toDomain(entity);
        flight.changeStatus(status);
        FlightMapper.copyToEntity(flight, entity);
        return FlightMapper.toDomain(repository.save(entity));
    }

    @Transactional
    public Flight cancel(Long id) {
        FlightEntity entity = findEntityById(id);
        Flight flight = FlightMapper.toDomain(entity);
        flight.cancel();
        FlightMapper.copyToEntity(flight, entity);
        return FlightMapper.toDomain(repository.save(entity));
    }

    @Transactional
    public void reserveSeat(Long id) {
        FlightEntity entity = findEntityById(id);
        Flight flight = FlightMapper.toDomain(entity);
        flight.reserveSeat();
        FlightMapper.copyToEntity(flight, entity);
        repository.save(entity);
    }

    @Transactional
    public void releaseSeat(Long id) {
        FlightEntity entity = findEntityById(id);
        Flight flight = FlightMapper.toDomain(entity);
        flight.releaseSeat();
        FlightMapper.copyToEntity(flight, entity);
        repository.save(entity);
    }

    @Transactional
    public Flight createOrUpdateByFlightNumber(Flight flight) {
        flightPolicy.validateForCreation(flight);
        FlightEntity entity = repository.findByFlightNumberIgnoreCase(flight.getFlightNumber()).orElse(null);
        if (entity == null) {
            return create(flight);
        }
        Flight existing = FlightMapper.toDomain(entity);
        existing.changeStatus(existing.getStatus());
        flight.setId(entity.getId());
        flight.setStatus(entity.getStatus());
        if (flight.getAvailableSeats() > flight.getCapacity()) {
            flight.setAvailableSeats(flight.getCapacity());
        }
        FlightMapper.copyToEntity(flight, entity);
        return FlightMapper.toDomain(repository.save(entity));
    }

    private void ensureUniqueFlightNumber(String flightNumber) {
        if (repository.existsByFlightNumberIgnoreCase(flightNumber)) {
            throw new BusinessRuleException("Flight number already exists");
        }
    }

    private FlightEntity findEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BusinessRuleException("Flight not found"));
    }
}
