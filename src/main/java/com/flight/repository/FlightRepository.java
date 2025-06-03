package com.flight.repository;

import com.flight.entity.Flight;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    @Query("SELECT DISTINCT f FROM Flight f " +
           "JOIN FETCH f.departureAirport " +
           "JOIN FETCH f.destinationAirport")
    List<Flight> findAll();

    @Query("SELECT DISTINCT f FROM Flight f " +
           "JOIN FETCH f.departureAirport " +
           "JOIN FETCH f.destinationAirport " +
           "WHERE f.id = :id")
    java.util.Optional<Flight> findById(@Param("id") Long id);


    @Query("SELECT DISTINCT f FROM Flight f " +
           "JOIN FETCH f.departureAirport " +
           "JOIN FETCH f.destinationAirport " +
           "WHERE f.departureAirport.code = :departure " +
           "AND f.destinationAirport.code = :destination " +
           "AND CAST(f.departureTime AS date) = CAST(:departureDate AS date) " +
           "AND f.availableSeats > 0")
    List<Flight> findFlights(@Param("departure") String departure,
                           @Param("destination") String destination,
                           @Param("departureDate") LocalDateTime departureDate);

    @Query("SELECT DISTINCT a.code FROM Flight f " +
           "JOIN f.departureAirport a " +
           "UNION " +
           "SELECT DISTINCT a.code FROM Flight f " +
           "JOIN f.destinationAirport a")
    List<String> findAllAirports();
}
