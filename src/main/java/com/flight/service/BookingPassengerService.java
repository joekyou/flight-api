package com.flight.service;

import com.flight.dto.BookingPassengerDTO;
import com.flight.entity.Booking;
import com.flight.entity.BookingPassenger;
import com.flight.entity.UserPassenger;
import com.flight.repository.BookingPassengerRepository;
import com.flight.repository.BookingRepository;
import com.flight.repository.UserPassengerRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public class BookingPassengerService {
    private final BookingPassengerRepository bookingPassengerRepository;
    private final BookingRepository bookingRepository;
    private final UserPassengerRepository userPassengerRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public BookingPassengerService(
            BookingPassengerRepository bookingPassengerRepository,
            BookingRepository bookingRepository,
            UserPassengerRepository userPassengerRepository,
            UserService userService,
            ModelMapper modelMapper) {
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.bookingRepository = bookingRepository;
        this.userPassengerRepository = userPassengerRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public List<BookingPassengerDTO> getBookingPassengers(Long bookingId, Authentication authentication) {
        validateBookingOwnership(bookingId, authentication);
        return bookingPassengerRepository.findByBookingId(bookingId)
                .stream()
                .map(passenger -> modelMapper.map(passenger, BookingPassengerDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingPassengerDTO addPassengerToBooking(
            Long bookingId,
            BookingPassengerDTO passengerDTO,
            Authentication authentication) {
        Booking booking = validateBookingOwnership(bookingId, authentication);
        
        UserPassenger passenger = userPassengerRepository.findById(passengerDTO.getPassengerId())
                .orElseThrow(() -> new RuntimeException("乘客不存在"));

        if (!passenger.getUser().getId().equals(userService.getCurrentUser(authentication).getId())) {
            throw new RuntimeException("无权添加此乘客");
        }

        BookingPassenger bookingPassenger = new BookingPassenger();
        bookingPassenger.setBooking(booking);
        bookingPassenger.setPassenger(passenger);
        bookingPassenger.setPassengerType(passengerDTO.getPassengerType());
        bookingPassenger.setSeatPreference(passengerDTO.getSeatPreference());
        bookingPassenger.setSpecialRequirements(passengerDTO.getSpecialRequirements());

        BookingPassenger savedBookingPassenger = bookingPassengerRepository.save(bookingPassenger);
        return modelMapper.map(savedBookingPassenger, BookingPassengerDTO.class);
    }

    @Transactional
    public void removePassengerFromBooking(
            Long bookingId,
            Long passengerId,
            Authentication authentication) {
        validateBookingOwnership(bookingId, authentication);
        bookingPassengerRepository.deleteByBookingId(bookingId);
    }

    private Booking validateBookingOwnership(Long bookingId, Authentication authentication) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("预订不存在"));

        if (!booking.getUser().getId().equals(userService.getCurrentUser(authentication).getId())) {
            throw new RuntimeException("无权访问此预订");
        }

        return booking;
    }
}
