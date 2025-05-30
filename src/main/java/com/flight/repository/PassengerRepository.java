package com.flight.repository;

import com.flight.entity.Booking;
import com.flight.entity.Passenger;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    List<Passenger> findByBooking(Booking booking);
    void deleteByBooking(Booking booking);
}
