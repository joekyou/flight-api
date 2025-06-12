package com.flight.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入有效的邮箱地址")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}
