package com.flight.service;

import com.flight.dto.UserPassengerDTO;
import com.flight.entity.User;
import com.flight.entity.UserPassenger;
import com.flight.repository.UserPassengerRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;



@Service
public class UserPassengerService {
    private final UserPassengerRepository userPassengerRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserPassengerService(
            UserPassengerRepository userPassengerRepository,
            UserService userService,
            ModelMapper modelMapper) {
        this.userPassengerRepository = userPassengerRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public List<UserPassengerDTO> getAllPassengers(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        return userPassengerRepository.findByUserId(user.getId())
                .stream()
                .map(passenger -> modelMapper.map(passenger, UserPassengerDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserPassengerDTO createPassenger(UserPassengerDTO passengerDTO, Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        UserPassenger passenger = modelMapper.map(passengerDTO, UserPassenger.class);
        passenger.setUser(user);

        if (passenger.isDefault()) {
            userPassengerRepository.unsetDefaultForOthers(user.getId(), null);
        }

        UserPassenger savedPassenger = userPassengerRepository.save(passenger);
        return modelMapper.map(savedPassenger, UserPassengerDTO.class);
    }

    @Transactional
    public UserPassengerDTO updatePassenger(Long id, UserPassengerDTO passengerDTO, Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        UserPassenger passenger = userPassengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("乘客不存在"));

        if (!passenger.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权修改此乘客信息");
        }

        modelMapper.map(passengerDTO, passenger);
        passenger.setUser(user);

        if (passenger.isDefault()) {
            userPassengerRepository.unsetDefaultForOthers(user.getId(), passenger.getId());
        }

        UserPassenger updatedPassenger = userPassengerRepository.save(passenger);
        return modelMapper.map(updatedPassenger, UserPassengerDTO.class);
    }

    @Transactional
    public void deletePassenger(Long id, Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        UserPassenger passenger = userPassengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("乘客不存在"));

        if (!passenger.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权删除此乘客信息");
        }

        userPassengerRepository.delete(passenger);
    }

    public UserPassengerDTO getPassenger(Long id, Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        UserPassenger passenger = userPassengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("乘客不存在"));

        if (!passenger.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权查看此乘客信息");
        }

        return modelMapper.map(passenger, UserPassengerDTO.class);
    }

    public UserPassengerDTO getDefaultPassenger(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        return userPassengerRepository.findByUserIdAndIsDefaultTrue(user.getId())
                .map(passenger -> modelMapper.map(passenger, UserPassengerDTO.class))
                .orElse(null);
    }

    @Transactional
    public List<UserPassengerDTO> updatePassengers(List<UserPassengerDTO> passengerDTOs, Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        
        // 先删除所有现有的乘客信息
        userPassengerRepository.deleteByUserId(user.getId());
        
        // 检查是否有多个默认乘客
        long defaultCount = passengerDTOs.stream()
                .mapToLong(dto -> dto.isDefault() ? 1 : 0)
                .sum();
        
        if (defaultCount > 1) {
            throw new RuntimeException("只能设置一个默认乘客");
        }
        
        // 保存新的乘客信息
        return passengerDTOs.stream()
                .map(dto -> {
                    UserPassenger passenger = modelMapper.map(dto, UserPassenger.class);
                    passenger.setUser(user);
                    passenger.setId(null); // 确保创建新记录
                    
                    UserPassenger savedPassenger = userPassengerRepository.save(passenger);
                    return modelMapper.map(savedPassenger, UserPassengerDTO.class);
                })
                .collect(Collectors.toList());
    }
}
