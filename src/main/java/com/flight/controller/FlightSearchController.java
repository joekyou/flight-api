package com.flight.controller;

import com.flight.dto.FlightDTO;
import com.flight.service.FlightService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/flight-search")
@CrossOrigin(origins = "*")
public class FlightSearchController {

    private final FlightService flightService;

    public FlightSearchController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<List<FlightDTO>> searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam(defaultValue = "1") int passengers) {
        
        List<FlightDTO> flights = flightService.searchFlights(from, to, departDate, returnDate, passengers);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/airports")
    public ResponseEntity<List<String>> getAirports() {
        List<String> airports = flightService.getAllAirports();
        return ResponseEntity.ok(airports);
    }
}
