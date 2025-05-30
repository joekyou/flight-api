package com.flight.config;

import com.flight.dto.BookingDTO;
import com.flight.dto.FlightDTO;
import com.flight.entity.Booking;
import com.flight.entity.Flight;
import com.flight.entity.Passenger;
import com.flight.repository.FlightRepository;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class ModelMapperConfig {

    @Autowired
    private FlightRepository flightRepository;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Flight -> FlightDTO 映射配置
        modelMapper.typeMap(Flight.class, FlightDTO.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getDepartureAirport().getCode(), FlightDTO::setDepartureAirport);
                mapper.map(src -> src.getDestinationAirport().getCode(), FlightDTO::setDestinationAirport);
            });

        // BookingDTO -> Booking 映射配置
        modelMapper.typeMap(BookingDTO.class, Booking.class)
            .addMappings(mapper -> {
                // 跳过所有自动映射
                mapper.skip(Booking::setFlight);
                mapper.skip(Booking::setPassengers);
            })
            .setPreConverter(context -> {
                BookingDTO source = context.getSource();
                Booking destination = context.getDestination();
                
                // 根据flightId设置flight实体
                if (source.getFlightId() != null) {
                    flightRepository.findById(source.getFlightId())
                        .ifPresent(flight -> {
                            destination.setFlight(flight);
                            destination.setTotalPrice(flight.getPrice() * source.getNumberOfPassengers());
                        });
                }
                
                // 手动设置乘客信息
                if (source.getPassengers() != null) {
                    destination.setPassengers(source.getPassengers().stream()
                        .map(passengerDTO -> {
                            var passenger = new Passenger();
                            passenger.setFirstName(passengerDTO.getFirstName());
                            passenger.setLastName(passengerDTO.getLastName());
                            passenger.setEmail(passengerDTO.getEmail());
                            passenger.setPhone(passengerDTO.getPhone());
                            passenger.setBooking(destination);
                            return passenger;
                        })
                        .collect(Collectors.toList()));
                }
                
                return destination;
            });

        // Booking -> BookingDTO 映射配置
        modelMapper.typeMap(Booking.class, BookingDTO.class)
            .addMappings(mapper -> {
                // 映射基本字段
                mapper.map(Booking::getId, BookingDTO::setId);
                mapper.map(Booking::getBookingReference, BookingDTO::setBookingReference);
                mapper.map(Booking::getNumberOfPassengers, BookingDTO::setNumberOfPassengers);
                mapper.map(Booking::getTotalPrice, BookingDTO::setTotalPrice);
                mapper.map(Booking::getStatus, BookingDTO::setStatus);
                mapper.map(Booking::getBookingDate, BookingDTO::setBookingDate);
                
                // 映射关联实体
                mapper.map(src -> src.getFlight().getId(), BookingDTO::setFlightId);
                mapper.map(src -> src.getUser().getId(), BookingDTO::setUserId);
                
                // 映射Flight实体到FlightDTO
                mapper.map(Booking::getFlight, BookingDTO::setFlight);
            });

        return modelMapper;
    }
}
