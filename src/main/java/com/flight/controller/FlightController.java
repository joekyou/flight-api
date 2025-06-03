package com.flight.controller;

import com.flight.dto.BaseResponse;
import com.flight.dto.FlightDTO;
import com.flight.service.FlightService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<FlightDTO>>> getAllFlights() {
        List<FlightDTO> flights = flightService.getAllFlights();
        return ResponseEntity.ok(new BaseResponse<>(200, "Success", flights));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<FlightDTO>> getFlightById(@PathVariable Long id) {
        FlightDTO flight = flightService.getFlightById(id);
        return ResponseEntity.ok(new BaseResponse<>(200, "Success", flight));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<FlightDTO>>> searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String departDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam Integer passengers,
            @RequestParam(required = false) String flightClass) {
        
        LocalDate departureDate;
        LocalDate returnLocalDate = null;
        
        try {
            departureDate = LocalDate.parse(departDate, DateTimeFormatter.ISO_DATE);
            if (returnDate != null && !returnDate.isEmpty()) {
                returnLocalDate = LocalDate.parse(returnDate, DateTimeFormatter.ISO_DATE);
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(400, "Invalid date format. Expected format: ISO date (YYYY-MM-DD)", null));
        }

        List<FlightDTO> flights = flightService.searchFlights(from, to, departureDate, returnLocalDate, passengers);
        return ResponseEntity.ok(new BaseResponse<>(200, "Success", flights));
    }
}
