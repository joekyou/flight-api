package com.flight.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDTO {

    private Long id;

    @NotBlank(message = "乘客名字不能为空")
    private String firstName;

    @NotBlank(message = "乘客姓氏不能为空")
    private String lastName;

    @NotBlank(message = "乘客邮箱不能为空")
    @Email(message = "请输入有效的邮箱地址")
    private String email;

    private String phone;
}
