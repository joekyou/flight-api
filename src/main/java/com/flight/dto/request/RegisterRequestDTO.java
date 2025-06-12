package com.flight.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入有效的邮箱地址")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", 
            message = "密码必须至少8个字符，包含至少一个字母和一个数字")
    private String password;
    
    @NotBlank(message = "名字不能为空")
    private String firstName;
    
    @NotBlank(message = "姓氏不能为空")
    private String lastName;
    
    @NotBlank(message = "国家/地区不能为空")
    private String country;
    
    private String phone;
}
