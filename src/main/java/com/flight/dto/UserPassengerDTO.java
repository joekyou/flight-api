package com.flight.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserPassengerDTO {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
