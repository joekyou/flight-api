package com.flight.repository;

import com.flight.entity.Booking;
import com.flight.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUser(User user);
    
    List<Booking> findByUserAndStatus(User user, String status);
    
    Optional<Booking> findByIdAndUser(Long id, User user);
    
    Optional<Booking> findByBookingReferenceAndUser(String bookingReference, User user);
    
    List<Booking> findByStatus(String status);
    
    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.flight.departureTime > :now ORDER BY b.flight.departureTime")
    List<Booking> findUpcomingBookings(@Param("user") User user, @Param("now") LocalDateTime now);
    
    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.flight.departureTime <= :now ORDER BY b.flight.departureTime DESC")
    List<Booking> findPastBookings(@Param("user") User user, @Param("now") LocalDateTime now);
    
    boolean existsByFlightIdAndUserAndStatus(Long flightId, User user, String status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.flight.id = :flightId AND b.status = 'CONFIRMED'")
    int countConfirmedBookingsByFlight(@Param("flightId") Long flightId);
}
