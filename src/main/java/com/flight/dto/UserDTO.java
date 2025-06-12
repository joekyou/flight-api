package com.flight.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {
    /**
     * 用户ID
     */
    private Long id;
    
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
     * 国家
     */
    private String country;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 邮政编码
     */
    private String zipCode;
    
    /**
     * 用户角色
     */
    private String role;
    
    /**
     * JWT令牌
     */
    private String token;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
