package com.flight.service;

import com.flight.dto.UserDTO;
import com.flight.entity.User;
import com.flight.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务类
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 获取用户个人资料
     */
    public UserDTO getUserProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return convertToDTO(user);
    }

    /**
     * 更新用户个人资料
     */
    @Transactional
    public UserDTO updateUserProfile(UserDTO userDTO, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        // 更新用户信息，但保持email不变（email是唯一标识符）
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setCountry(userDTO.getCountry());
        user.setGender(userDTO.getGender());
        user.setAge(userDTO.getAge());
        user.setAddress(userDTO.getAddress());
        user.setZipCode(userDTO.getZipCode());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    /**
     * 将用户实体转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setCountry(user.getCountry());
        dto.setGender(user.getGender());
        dto.setAge(user.getAge());
        dto.setAddress(user.getAddress());
        dto.setZipCode(user.getZipCode());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    /**
     * 获取当前登录用户信息
     */
    public User getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
    }
}
