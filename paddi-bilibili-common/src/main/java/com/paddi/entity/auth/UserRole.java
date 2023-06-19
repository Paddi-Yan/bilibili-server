package com.paddi.entity.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRole {

    private Long id;

    private Long userId;

    private Long roleId;

    private String roleName;

    private String roleCode;

    private LocalDateTime createTime;


}
