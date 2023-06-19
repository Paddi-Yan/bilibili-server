package com.paddi.service.impl;

import com.paddi.entity.auth.UserRole;
import com.paddi.mapper.UserRoleMapper;
import com.paddi.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:31:18
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleMapper.getUserRoleByUserId(userId);
    }

    @Override
    public void addUserRole(UserRole userRole) {
        userRoleMapper.insert(userRole);
    }
}
