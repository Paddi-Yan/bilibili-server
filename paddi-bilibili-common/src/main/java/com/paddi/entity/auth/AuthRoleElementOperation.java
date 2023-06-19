package com.paddi.entity.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthRoleElementOperation {

    private Long id;

    private Long roleId;

    private Long elementOperationId;

    private LocalDateTime createTime;

    private AuthElementOperation authElementOperation;
}
