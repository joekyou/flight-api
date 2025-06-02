package com.flight.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = {"departureAirport", "destinationAirport"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private String airline;

    @ManyToOne
    @JoinColumn(name = "departure_airport_id", nullable = false)
    @JsonBackReference("departing")
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "destination_airport_id", nullable = false)
    @JsonBackReference("arriving")
    private Airport destinationAirport;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int availableSeats;

    @Column(nullable = false)
    private String aircraftType;
}
