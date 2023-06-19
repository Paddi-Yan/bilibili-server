package com.paddi.service;

import com.paddi.entity.auth.AuthRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:50:20
 */
public interface AuthRoleMenuService {
    List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIds);
}
