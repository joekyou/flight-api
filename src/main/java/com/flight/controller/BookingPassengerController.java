package com.flight.controller;

import com.flight.dto.BookingPassengerDTO;
import com.flight.service.BookingPassengerService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/bookings")
@PreAuthorize("hasRole('USER')")
public class BookingPassengerController {
    
    private final BookingPassengerService bookingPassengerService;

    public BookingPassengerController(BookingPassengerService bookingPassengerService) {
        this.bookingPassengerService = bookingPassengerService;
    }

    @GetMapping("/{bookingId}/passengers")
    public ResponseEntity<List<BookingPassengerDTO>> getBookingPassengers(
            @PathVariable Long bookingId,
            Authentication authentication) {
        return ResponseEntity.ok(bookingPassengerService.getBookingPassengers(bookingId, authentication));
    }

    @PostMapping("/{bookingId}/passengers")
    public ResponseEntity<BookingPassengerDTO> addPassengerToBooking(
            @PathVariable Long bookingId,
            @RequestBody BookingPassengerDTO passengerDTO,
            Authentication authentication) {
        return ResponseEntity.ok(bookingPassengerService.addPassengerToBooking(bookingId, passengerDTO, authentication));
    }

    @DeleteMapping("/{bookingId}/passengers/{passengerId}")
    public ResponseEntity<Void> removePassengerFromBooking(
            @PathVariable Long bookingId,
            @PathVariable Long passengerId,
            Authentication authentication) {
        bookingPassengerService.removePassengerFromBooking(bookingId, passengerId, authentication);
        return ResponseEntity.ok().build();
    }
}
