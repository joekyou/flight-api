package com.flight.controller;

import com.flight.dto.BaseResponse;
import com.flight.dto.FlightDTO;
import com.flight.service.FlightService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<FlightDTO>>> getAllFlights() {
        List<FlightDTO> flights = flightService.getAllFlights();
        return ResponseEntity.ok(new BaseResponse<>(200, "Success", flights));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<FlightDTO>> getFlightById(@PathVariable Long id) {
        FlightDTO flight = flightService.getFlightById(id);
        return ResponseEntity.ok(new BaseResponse<>(200, "Success", flight));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<FlightDTO>>> searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String departDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam Integer passengers,
            @RequestParam(required = false) String flightClass) {
        
        LocalDateTime departureDateTime;
        LocalDateTime returnDateTime = null;
        
        try {
            departureDateTime = parseDateTime(departDate);
            if (returnDate != null && !returnDate.isEmpty()) {
                returnDateTime = parseDateTime(returnDate);
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(400, "Invalid date format. Expected format: ISO date or ISO datetime", null));
        }

        List<FlightDTO> flights = flightService.searchFlights(from, to, departureDateTime, returnDateTime);
        return ResponseEntity.ok(new BaseResponse<>(200, "Success", flights));
    }

    private LocalDateTime parseDateTime(String dateStr) {
        try {
            // 尝试解析完整的日期时间格式
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            // 如果失败，尝试只解析日期部分
            // 使用当天的16:00作为默认时间，这样可以避免时区转换导致日期偏差
            return LocalDateTime.of(
                java.time.LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE),
                java.time.LocalTime.of(16, 0)
            );
        }
    }
}
