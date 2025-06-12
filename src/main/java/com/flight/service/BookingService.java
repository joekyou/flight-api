package com.flight.service;

import com.flight.dto.AirportDTO;
import com.flight.dto.BookingDTO;
import com.flight.dto.BookingPassengerDTO;
import com.flight.dto.FlightDTO;
import com.flight.dto.UserPassengerDTO;
import com.flight.entity.Booking;
import com.flight.entity.BookingPassenger;
import com.flight.entity.Flight;
import com.flight.entity.User;
import com.flight.entity.UserPassenger;
import com.flight.repository.BookingRepository;
import com.flight.repository.FlightRepository;
import com.flight.repository.UserPassengerRepository;
import com.flight.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final UserPassengerRepository userPassengerRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, 
                         FlightRepository flightRepository,
                         UserRepository userRepository,
                         UserPassengerRepository userPassengerRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.userPassengerRepository = userPassengerRepository;
    }

    @Transactional
    @Retryable(
        value = {CannotAcquireLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public BookingDTO createBooking(BookingDTO bookingDTO, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        
        Flight mainFlight;
        Flight returnFlight = null;
        String mainFlightType = bookingDTO.getMainFlightType();

        if ("OUTBOUND".equals(mainFlightType)) {
            mainFlight = flightRepository.findById(bookingDTO.getFlightId())
                    .orElseThrow(() -> new RuntimeException("出发航班未找到"));
            if (bookingDTO.getReturnFlightId() != null) {
                returnFlight = flightRepository.findById(bookingDTO.getReturnFlightId())
                        .orElseThrow(() -> new RuntimeException("返程航班未找到"));
            }
        } else if ("RETURN".equals(mainFlightType)) {
            mainFlight = flightRepository.findById(bookingDTO.getFlightId())
                    .orElseThrow(() -> new RuntimeException("返程航班未找到"));
        } else {
            throw new RuntimeException("无效的航班类型");
        }

        if (mainFlight.getAvailableSeats() < bookingDTO.getNumberOfPassengers()) {
            throw new RuntimeException("航班座位数不足");
        }
        if (returnFlight != null && returnFlight.getAvailableSeats() < bookingDTO.getNumberOfPassengers()) {
            throw new RuntimeException("返程航班座位数不足");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(mainFlight);
        booking.setReturnFlight(returnFlight);
        booking.setFlightType(returnFlight != null ? "ROUND_TRIP" : "ONE_WAY");
        booking.setMainFlightType(mainFlightType);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        booking.setBookingReference(generateBookingReference());
        booking.setNumberOfPassengers(bookingDTO.getNumberOfPassengers());
        booking.setTotalPrice(calculateTotalPrice(mainFlight, returnFlight, bookingDTO.getNumberOfPassengers()));

        // 处理乘客信息
        if (bookingDTO.getPassengerIds() != null && !bookingDTO.getPassengerIds().isEmpty()) {
            List<UserPassenger> passengers = userPassengerRepository.findAllById(bookingDTO.getPassengerIds());
            List<BookingPassenger> bookingPassengers = new ArrayList<>();
            
            for (UserPassenger passenger : passengers) {
                BookingPassenger bookingPassenger = new BookingPassenger();
                bookingPassenger.setBooking(booking);
                bookingPassenger.setPassenger(passenger);
                bookingPassenger.setPassengerType(passenger.equals(passengers.get(0)) ? 
                    com.flight.entity.enums.PassengerType.PRIMARY : 
                    com.flight.entity.enums.PassengerType.ACCOMPANYING);
                bookingPassengers.add(bookingPassenger);
            }
            
            booking.setBookingPassengers(bookingPassengers);
        }

        // 更新主航班座位数
        mainFlight.setAvailableSeats(mainFlight.getAvailableSeats() - bookingDTO.getNumberOfPassengers());
        flightRepository.save(mainFlight);

        // 更新返程航班座位数（如果有）
        if (returnFlight != null) {
            returnFlight.setAvailableSeats(returnFlight.getAvailableSeats() - bookingDTO.getNumberOfPassengers());
            flightRepository.save(returnFlight);
        }

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setFlightId(booking.getFlight().getId());
        dto.setUserId(booking.getUser().getId());
        dto.setNumberOfPassengers(booking.getNumberOfPassengers());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setBookingDate(booking.getBookingDate());
        dto.setFlightType(booking.getFlightType());
        dto.setMainFlightType(booking.getMainFlightType());

        if (booking.getReturnFlight() != null) {
            dto.setReturnFlightId(booking.getReturnFlight().getId());
        }

        // 转换主航班
        Flight flight = booking.getFlight();
        FlightDTO flightDTO = convertFlightToDTO(flight);
        dto.setFlight(flightDTO);

        // 转换返程航班（如果有）
        if (booking.getReturnFlight() != null) {
            FlightDTO returnFlightDTO = convertFlightToDTO(booking.getReturnFlight());
            dto.setReturnFlight(returnFlightDTO);
        }

        // 转换乘客信息
        if (booking.getBookingPassengers() != null) {
            List<BookingPassengerDTO> bookingPassengerDTOs = booking.getBookingPassengers().stream()
                .map(bookingPassenger -> {
                    BookingPassengerDTO bookingPassengerDTO = new BookingPassengerDTO();
                    UserPassenger userPassenger = bookingPassenger.getPassenger();
                    
                    // 设置乘客基本信息
                    UserPassengerDTO passengerDTO = new UserPassengerDTO();
                    passengerDTO.setId(userPassenger.getId());
                    passengerDTO.setFirstName(userPassenger.getFirstName());
                    passengerDTO.setLastName(userPassenger.getLastName());
                    passengerDTO.setEmail(userPassenger.getEmail());
                    passengerDTO.setPhone(userPassenger.getPhone());
                    
                    // 设置预订乘客信息
                    bookingPassengerDTO.setId(bookingPassenger.getId());
                    bookingPassengerDTO.setPassenger(passengerDTO);
                    bookingPassengerDTO.setPassengerType(bookingPassenger.getPassengerType());
                    bookingPassengerDTO.setSeatPreference(bookingPassenger.getSeatPreference());
                    bookingPassengerDTO.setSpecialRequirements(bookingPassenger.getSpecialRequirements());
                    
                    return bookingPassengerDTO;
                })
                .collect(Collectors.toList());
            
            dto.setBookingPassengers(bookingPassengerDTOs);
        }

        return dto;
    }

    private FlightDTO convertFlightToDTO(Flight flight) {
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setId(flight.getId());
        flightDTO.setFlightNumber(flight.getFlightNumber());
        flightDTO.setAirline(flight.getAirline());
        
        // 创建出发机场DTO
        AirportDTO departureAirport = new AirportDTO(
            flight.getDepartureAirport().getCode(),
            flight.getDepartureAirport().getName()
        );
        flightDTO.setDepartureAirport(departureAirport);
        
        // 创建到达机场DTO
        AirportDTO destinationAirport = new AirportDTO(
            flight.getDestinationAirport().getCode(),
            flight.getDestinationAirport().getName()
        );
        flightDTO.setArrivalAirport(destinationAirport);
        
        flightDTO.setDepartureTime(flight.getDepartureTime());
        flightDTO.setArrivalTime(flight.getArrivalTime());
        flightDTO.setPrice(flight.getPrice());
        flightDTO.setAvailableSeats(flight.getAvailableSeats());
        flightDTO.setAircraftType(flight.getAircraftType());
        return flightDTO;
    }

    private User getUserFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
    }

    private String generateBookingReference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private double calculateTotalPrice(Flight flight, Flight returnFlight, int passengers) {
        double basePrice = flight.getPrice() * passengers;
        double taxes = basePrice * 0.1; // 10% 税费
        double totalPrice = basePrice + taxes;

        if (returnFlight != null) {
            double returnBasePrice = returnFlight.getPrice() * passengers;
            double returnTaxes = returnBasePrice * 0.1;
            totalPrice += returnBasePrice + returnTaxes;
        }

        double fees = 25 * passengers;  // 每位乘客25美元服务费
        return totalPrice + fees;
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookingDTO getBookingById(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Booking booking = bookingRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("预订未找到"));
        return convertToDTO(booking);
    }

    public BookingDTO getBookingByReference(String reference, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Booking booking = bookingRepository.findByBookingReferenceAndUser(reference, user)
                .orElseThrow(() -> new RuntimeException("预订未找到"));
        return convertToDTO(booking);
    }

    @Transactional
    @Retryable(
        value = {CannotAcquireLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public void cancelBooking(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Booking booking = bookingRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("预订未找到"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("预订已取消");
        }

        // 恢复出发航班座位
        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getNumberOfPassengers());
        flightRepository.save(flight);

        // 恢复返程航班座位（如果有）
        if (booking.getReturnFlight() != null) {
            Flight returnFlight = booking.getReturnFlight();
            returnFlight.setAvailableSeats(returnFlight.getAvailableSeats() + booking.getNumberOfPassengers());
            flightRepository.save(returnFlight);
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    public List<BookingDTO> getUpcomingBookings(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<Booking> bookings = bookingRepository.findUpcomingBookings(user, LocalDateTime.now());
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getPastBookings(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<Booking> bookings = bookingRepository.findPastBookings(user, LocalDateTime.now());
        return bookings.stream()
                .map(this::convertToDTO)
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Retryable(
        value = {CannotAcquireLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public BookingDTO updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预订未找到"));
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDTO(updatedBooking);
    }
}
