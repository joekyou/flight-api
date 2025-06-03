package com.flight.controller;

import com.flight.dto.UserPassengerDTO;
import com.flight.service.UserPassengerService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/user-passengers")
@PreAuthorize("hasRole('USER')")
public class UserPassengerController {
    
    private final UserPassengerService userPassengerService;

    public UserPassengerController(UserPassengerService userPassengerService) {
        this.userPassengerService = userPassengerService;
    }

    @GetMapping
    public ResponseEntity<List<UserPassengerDTO>> getAllPassengers(Authentication authentication) {
        return ResponseEntity.ok(userPassengerService.getAllPassengers(authentication));
    }

    @PostMapping
    public ResponseEntity<List<UserPassengerDTO>> updatePassengers(
            @RequestBody List<UserPassengerDTO> passengers,
            Authentication authentication) {
        return ResponseEntity.ok(userPassengerService.updatePassengers(passengers, authentication));
    }

    @PostMapping("/single")
    public ResponseEntity<UserPassengerDTO> createPassenger(
            @RequestBody UserPassengerDTO passengerDTO,
            Authentication authentication) {
        return ResponseEntity.ok(userPassengerService.createPassenger(passengerDTO, authentication));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserPassengerDTO> updatePassenger(
            @PathVariable Long id,
            @RequestBody UserPassengerDTO passengerDTO,
            Authentication authentication) {
        return ResponseEntity.ok(userPassengerService.updatePassenger(id, passengerDTO, authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(
            @PathVariable Long id,
            Authentication authentication) {
        userPassengerService.deletePassenger(id, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPassengerDTO> getPassenger(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(userPassengerService.getPassenger(id, authentication));
    }

    @GetMapping("/default")
    public ResponseEntity<UserPassengerDTO> getDefaultPassenger(Authentication authentication) {
        UserPassengerDTO passenger = userPassengerService.getDefaultPassenger(authentication);
        return passenger != null ? ResponseEntity.ok(passenger) : ResponseEntity.notFound().build();
    }
}
