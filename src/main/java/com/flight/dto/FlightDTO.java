package com.flight.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {
    private Long id;
    private String flightNumber;
    private String airline;
    private AirportDTO departureAirport;
    private AirportDTO arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private int availableSeats;
    private String aircraftType;
}
