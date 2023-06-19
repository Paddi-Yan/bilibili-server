package com.paddi.entity.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthRoleMenu {

    private Long id;

    private Long roleId;

    private Long menuId;

    private LocalDateTime createTime;

    private AuthMenu authMenu;

}
