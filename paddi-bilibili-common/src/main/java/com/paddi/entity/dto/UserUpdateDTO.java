package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:04:48
 */
@Data
public class UserUpdateDTO {
    private String phone;
    private String email;
    private String password;
}
