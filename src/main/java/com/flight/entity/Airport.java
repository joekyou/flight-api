package com.flight.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;



@Data
@ToString(exclude = {"departingFlights", "arrivingFlights"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "airports")
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @JsonManagedReference("departing")
    @OneToMany(mappedBy = "departureAirport")
    private List<Flight> departingFlights;

    @JsonManagedReference("arriving")
    @OneToMany(mappedBy = "destinationAirport")
    private List<Flight> arrivingFlights;
}