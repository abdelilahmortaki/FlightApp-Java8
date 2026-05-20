package com.example.flightapp.booking.api;

import com.example.flightapp.booking.application.BookingApplicationService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingApplicationService service;

    public BookingController(BookingApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public BookingResponse create(@Valid @RequestBody BookingRequest request) {
        return BookingResponse.from(
            service.create(
                request.getFlightId(),
                request.getPassengerName(),
                request.getPassengerEmail()
            )
        );
    }

    @PatchMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id) {
        return BookingResponse.from(service.cancel(id));
    }
}
