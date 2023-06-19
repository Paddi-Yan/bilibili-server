package com.paddi.entity.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthElementOperation {

    private Long id;

    private String elementName;

    private String elementCode;

    private String operationType;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
