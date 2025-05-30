package com.flight.service;

import com.flight.dto.BookingDTO;
import com.flight.entity.Booking;
import com.flight.entity.Flight;
import com.flight.entity.User;
import com.flight.repository.BookingRepository;
import com.flight.repository.FlightRepository;
import com.flight.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public BookingService(BookingRepository bookingRepository, 
                         FlightRepository flightRepository,
                         UserRepository userRepository, 
                         ModelMapper modelMapper) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Flight flight = flightRepository.findById(bookingDTO.getFlightId())
                .orElseThrow(() -> new RuntimeException("航班未找到"));

        if (flight.getAvailableSeats() < bookingDTO.getNumberOfPassengers()) {
            throw new RuntimeException("座位数不足");
        }

        // 手动创建和设置Booking实体
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        booking.setBookingReference(generateBookingReference());
        booking.setNumberOfPassengers(bookingDTO.getNumberOfPassengers());
        booking.setTotalPrice(calculateTotalPrice(flight, bookingDTO.getNumberOfPassengers()));

        // 手动设置乘客信息
        if (bookingDTO.getPassengers() != null) {
            booking.setPassengers(bookingDTO.getPassengers().stream()
                .map(passengerDTO -> {
                    var passenger = modelMapper.map(passengerDTO, com.flight.entity.Passenger.class);
                    passenger.setBooking(booking);
                    return passenger;
                })
                .collect(Collectors.toList()));
        }

        // 更新可用座位数
        flight.setAvailableSeats(flight.getAvailableSeats() - bookingDTO.getNumberOfPassengers());
        flightRepository.save(flight);

        Booking savedBooking = bookingRepository.save(booking);
        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    public List<BookingDTO> getCurrentUserBookings(Authentication authentication, String status) {
        User user = getUserFromAuthentication(authentication);
        List<Booking> bookings;
        
        if (status != null) {
            bookings = bookingRepository.findByUserAndStatus(user, status);
        } else {
            bookings = bookingRepository.findByUser(user);
        }
        
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    public BookingDTO getBookingById(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Booking booking = bookingRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("预订未找到"));
        return modelMapper.map(booking, BookingDTO.class);
    }

    public BookingDTO getBookingByReference(String reference, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Booking booking = bookingRepository.findByBookingReferenceAndUser(reference, user)
                .orElseThrow(() -> new RuntimeException("预订未找到"));
        return modelMapper.map(booking, BookingDTO.class);
    }

    @Transactional
    public void cancelBooking(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Booking booking = bookingRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("预订未找到"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("预订已取消");
        }

        // 更新可用座位数
        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getNumberOfPassengers());
        flightRepository.save(flight);

        // 取消预订
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    public List<BookingDTO> getUpcomingBookings(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<Booking> bookings = bookingRepository.findUpcomingBookings(user, LocalDateTime.now());
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getPastBookings(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<Booking> bookings = bookingRepository.findPastBookings(user, LocalDateTime.now());
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getAllBookings(String status) {
        List<Booking> bookings;
        if (status != null) {
            bookings = bookingRepository.findByStatus(status);
        } else {
            bookings = bookingRepository.findAll();
        }
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDTO updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预订未找到"));
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        return modelMapper.map(updatedBooking, BookingDTO.class);
    }

    private User getUserFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
    }

    private String generateBookingReference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private double calculateTotalPrice(Flight flight, int passengers) {
        double basePrice = flight.getPrice() * passengers;
        double taxes = basePrice * 0.1; // 10% 税费
        double fees = 25 * passengers;  // 每位乘客25美元服务费
        return basePrice + taxes + fees;
    }
}
