package com.flight.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 乘客数据传输对象
 */
@Data
public class PassengerDTO {
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 预订ID（可选，仅当乘客与特定预订关联时使用）
     */
    private Long bookingId;
    
    /**
     * 名字
     */
    private String firstName;
    
    /**
     * 姓氏
     */
    private String lastName;
    
    /**
     * 电子邮件
     */
    private String email;
    
    /**
     * 电话号码
     */
    private String phone;
    
    /**
     * 是否为默认乘客
     */
    private boolean isDefault;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
