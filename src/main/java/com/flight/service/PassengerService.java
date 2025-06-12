package com.flight.service;

import com.flight.dto.PassengerDTO;
import com.flight.entity.Passenger;
import com.flight.entity.User;
import com.flight.repository.PassengerRepository;
import com.flight.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 乘客服务类
 */
@Service
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;

    public PassengerService(PassengerRepository passengerRepository, UserRepository userRepository) {
        this.passengerRepository = passengerRepository;
        this.userRepository = userRepository;
    }

    /**
     * 获取当前用户的所有乘客
     */
    public List<PassengerDTO> getAllPassengers(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<Passenger> passengers = passengerRepository.findByUser(user);
        return passengers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 添加新乘客
     */
    @Transactional
    public PassengerDTO addPassenger(PassengerDTO passengerDTO, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        
        // 如果这是用户的第一个乘客，设置为默认乘客
        boolean shouldBeDefault = passengerRepository.countByUser(user) == 0;
        
        Passenger passenger = new Passenger();
        passenger.setUser(user);
        passenger.setFirstName(passengerDTO.getFirstName());
        passenger.setLastName(passengerDTO.getLastName());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setPhone(passengerDTO.getPhone());
        passenger.setDefault(shouldBeDefault || passengerDTO.isDefault());
        
        // 如果新乘客被设置为默认，取消其他乘客的默认状态
        if (passenger.isDefault()) {
            passengerRepository.clearDefaultPassenger(user.getId());
        }
        
        Passenger savedPassenger = passengerRepository.save(passenger);
        return convertToDTO(savedPassenger);
    }

    /**
     * 更新乘客信息
     */
    @Transactional
    public PassengerDTO updatePassenger(Long id, PassengerDTO passengerDTO, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Passenger passenger = passengerRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("乘客未找到"));
        
        passenger.setFirstName(passengerDTO.getFirstName());
        passenger.setLastName(passengerDTO.getLastName());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setPhone(passengerDTO.getPhone());
        
        // 处理默认乘客设置
        if (passengerDTO.isDefault() && !passenger.isDefault()) {
            passengerRepository.clearDefaultPassenger(user.getId());
            passenger.setDefault(true);
        }
        
        Passenger updatedPassenger = passengerRepository.save(passenger);
        return convertToDTO(updatedPassenger);
    }

    /**
     * 删除乘客
     */
    @Transactional
    public void deletePassenger(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Passenger passenger = passengerRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("乘客未找到"));
        
        // 如果要删除的是默认乘客，且还有其他乘客，则设置另一个乘客为默认
        if (passenger.isDefault()) {
            List<Passenger> otherPassengers = passengerRepository.findByUserAndIdNot(user, id);
            if (!otherPassengers.isEmpty()) {
                Passenger newDefault = otherPassengers.get(0);
                newDefault.setDefault(true);
                passengerRepository.save(newDefault);
            }
        }
        
        passengerRepository.delete(passenger);
    }

    /**
     * 获取乘客详情
     */
    public PassengerDTO getPassenger(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Passenger passenger = passengerRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("乘客未找到"));
        return convertToDTO(passenger);
    }

    /**
     * 将乘客实体转换为DTO
     */
    private PassengerDTO convertToDTO(Passenger passenger) {
        PassengerDTO dto = new PassengerDTO();
        dto.setId(passenger.getId());
        dto.setUserId(passenger.getUser().getId());
        if (passenger.getBooking() != null) {
            dto.setBookingId(passenger.getBooking().getId());
        }
        dto.setFirstName(passenger.getFirstName());
        dto.setLastName(passenger.getLastName());
        dto.setEmail(passenger.getEmail());
        dto.setPhone(passenger.getPhone());
        dto.setDefault(passenger.isDefault());
        dto.setCreatedAt(passenger.getCreatedAt());
        dto.setUpdatedAt(passenger.getUpdatedAt());
        return dto;
    }

    /**
     * 从Authentication获取用户信息
     */
    private User getUserFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
    }
}
