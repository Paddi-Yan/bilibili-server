package com.paddi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 22:38:21
 */
@Data
public class UserLoginDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String password;
}
