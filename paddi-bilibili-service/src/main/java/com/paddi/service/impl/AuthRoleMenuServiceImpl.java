package com.paddi.service.impl;

import com.paddi.entity.auth.AuthRoleMenu;
import com.paddi.mapper.AuthRoleMenuMapper;
import com.paddi.service.AuthRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:50:24
 */
@Service
public class AuthRoleMenuServiceImpl implements AuthRoleMenuService {

    @Autowired
    private AuthRoleMenuMapper authRoleMenuMapper;

    @Override
    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIds) {
        return authRoleMenuMapper.getAuthRoleMenusByRoleIds(roleIds);
    }
}
