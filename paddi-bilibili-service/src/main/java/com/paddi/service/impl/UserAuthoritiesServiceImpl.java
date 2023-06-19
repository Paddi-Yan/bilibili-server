package com.paddi.service.impl;

import com.paddi.entity.auth.*;
import com.paddi.service.AuthRoleService;
import com.paddi.service.UserAuthoritiesService;
import com.paddi.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.paddi.constants.UserRoleConstants.ROLE_CODE_LV0;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:28:10
 */
@Service
public class UserAuthoritiesServiceImpl implements UserAuthoritiesService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthRoleService authRoleService;

    @Override
    public UserAuthorities getUserAuthorities(Long userId) {
        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(userId);
        Set<Long> roleIds = userRoles.stream().map(UserRole :: getRoleId).collect(Collectors.toSet());
        List<AuthRoleElementOperation> roleElementOperations = authRoleService.getRoleElementOperationsByRoleIds(roleIds);
        List<AuthRoleMenu> authRoleMenus = authRoleService.getAuthRoleMenusByRoleIds(roleIds);
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(roleElementOperations);
        userAuthorities.setRoleMenuList(authRoleMenus);
        return userAuthorities;
    }

    @Override
    public void addUserDefaultRole(Long userId) {
        UserRole userRole = new UserRole();
        AuthRole authRole = authRoleService.getAuthRoleByCode(ROLE_CODE_LV0);
        userRole.setUserId(userId);
        userRole.setRoleId(authRole.getId());
        userRole.setCreateTime(LocalDateTime.now());
        userRoleService.addUserRole(userRole);
    }
}
