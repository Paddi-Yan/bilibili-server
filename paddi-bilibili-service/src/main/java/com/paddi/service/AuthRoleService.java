package com.paddi.service;

import com.paddi.entity.auth.AuthRole;
import com.paddi.entity.auth.AuthRoleElementOperation;
import com.paddi.entity.auth.AuthRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:31:44
 */
public interface AuthRoleService {
    List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIds);

    List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIds);

    AuthRole getAuthRoleByCode(String roleCode);
}
