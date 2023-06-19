package com.paddi.entity.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthMenu {

    private Long id;

    private String name;

    private String code;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
