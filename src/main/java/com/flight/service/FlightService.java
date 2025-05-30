package com.flight.service;

import com.flight.dto.FlightDTO;
import com.flight.entity.Flight;
import com.flight.repository.FlightRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<FlightDTO> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FlightDTO getFlightById(Long id) {
        return flightRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public Flight saveFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    public List<FlightDTO> searchFlights(String departure, String destination, 
            LocalDateTime departureDate, LocalDateTime returnDate) {
        // 查询出发当天的航班
        List<Flight> departureFlights = flightRepository.findFlights(
            departure, destination, departureDate);
        
        List<FlightDTO> result = departureFlights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 如果有返程日期，查询返程当天的航班
        if (returnDate != null) {
            List<Flight> returnFlights = flightRepository.findFlights(
                destination, departure, returnDate);
            
            result.addAll(returnFlights.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        }

        return result;
    }

    private FlightDTO convertToDTO(Flight flight) {
        FlightDTO dto = new FlightDTO();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setAirline(flight.getAirline());
        dto.setDepartureAirport(flight.getDepartureAirport().getCode());
        dto.setDestinationAirport(flight.getDestinationAirport().getCode());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setPrice(flight.getPrice());
        dto.setAvailableSeats(flight.getAvailableSeats());
        dto.setAircraftType(flight.getAircraftType());
        return dto;
    }
}
