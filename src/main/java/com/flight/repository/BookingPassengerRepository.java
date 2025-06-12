package com.flight.repository;

import com.flight.entity.BookingPassenger;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookingPassengerRepository extends JpaRepository<BookingPassenger, Long> {
    List<BookingPassenger> findByBookingId(Long bookingId);
    
    void deleteByBookingId(Long bookingId);
}
