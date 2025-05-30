package com.flight.controller;

import com.flight.dto.BaseResponse;
import com.flight.entity.Airport;
import com.flight.repository.AirportRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportRepository airportRepository;

    public AirportController(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Airport>>> getAllAirports() {
        List<Airport> airports = airportRepository.findAll();
        return ResponseEntity.ok(new BaseResponse<>(200, "获取机场列表成功", airports));
    }
}
