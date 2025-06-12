package com.flight.controller;

import com.flight.dto.PassengerDTO;
import com.flight.service.PassengerService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


/**
 * 乘客管理控制器
 */
@RestController
@RequestMapping("/api/passengers")
@CrossOrigin(origins = "http://localhost:3000")
public class PassengerController {
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    /**
     * 获取当前用户的所有乘客
     */
    @GetMapping
    public ResponseEntity<List<PassengerDTO>> getAllPassengers(Authentication authentication) {
        try {
            List<PassengerDTO> passengers = passengerService.getAllPassengers(authentication);
            return ResponseEntity.ok(passengers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 添加新乘客
     */
    @PostMapping
    public ResponseEntity<PassengerDTO> addPassenger(@RequestBody PassengerDTO passengerDTO, 
                                                    Authentication authentication) {
        try {
            PassengerDTO savedPassenger = passengerService.addPassenger(passengerDTO, authentication);
            return ResponseEntity.ok(savedPassenger);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取乘客详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<PassengerDTO> getPassenger(@PathVariable Long id, 
                                                    Authentication authentication) {
        try {
            PassengerDTO passenger = passengerService.getPassenger(id, authentication);
            return ResponseEntity.ok(passenger);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新乘客信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<PassengerDTO> updatePassenger(@PathVariable Long id, 
                                                       @RequestBody PassengerDTO passengerDTO,
                                                       Authentication authentication) {
        try {
            PassengerDTO updatedPassenger = passengerService.updatePassenger(id, passengerDTO, authentication);
            return ResponseEntity.ok(updatedPassenger);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除乘客
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id, 
                                               Authentication authentication) {
        try {
            passengerService.deletePassenger(id, authentication);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
