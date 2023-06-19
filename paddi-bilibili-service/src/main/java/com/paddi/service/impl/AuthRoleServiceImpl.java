package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paddi.entity.auth.AuthRole;
import com.paddi.entity.auth.AuthRoleElementOperation;
import com.paddi.entity.auth.AuthRoleMenu;
import com.paddi.mapper.AuthRoleMapper;
import com.paddi.service.AuthRoleElementOperationService;
import com.paddi.service.AuthRoleMenuService;
import com.paddi.service.AuthRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:31:52
 */
@Service
public class AuthRoleServiceImpl implements AuthRoleService {

    @Autowired
    private AuthRoleMapper authRoleMapper;

    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    @Override
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIds) {
        return authRoleElementOperationService.getRoleElementOperationsByRoleIds(roleIds);
    }

    @Override
    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIds) {
        return authRoleMenuService.getAuthRoleMenusByRoleIds(roleIds);
    }

    @Override
    public AuthRole getAuthRoleByCode(String roleCode) {
        return authRoleMapper.selectOne(new LambdaQueryWrapper<AuthRole>().eq(AuthRole :: getCode, roleCode));
    }
}
