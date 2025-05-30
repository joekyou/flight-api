package com.flight.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    
    private Long id;
    
    private String bookingReference;
    
    @NotNull(message = "航班ID不能为空")
    private Long flightId;
    
    private Long userId;
    
    @NotNull(message = "乘客人数不能为空")
    @Min(value = 1, message = "乘客人数必须大于0")
    private Integer numberOfPassengers;
    
    private Double totalPrice;
    
    private String status;
    
    private LocalDateTime bookingDate;
    
    private FlightDTO flight;
    
    @NotNull(message = "乘客信息不能为空")
    @Valid
    private List<PassengerDTO> passengers;
}
