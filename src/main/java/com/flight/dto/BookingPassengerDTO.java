package com.flight.dto;

import com.flight.entity.enums.PassengerType;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookingPassengerDTO {
    private Long id;
    private Long bookingId;
    private Long passengerId;
    private PassengerType passengerType;
    private String seatPreference;
    private String specialRequirements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 包含乘客信息，用于显示
    private UserPassengerDTO passenger;
}
