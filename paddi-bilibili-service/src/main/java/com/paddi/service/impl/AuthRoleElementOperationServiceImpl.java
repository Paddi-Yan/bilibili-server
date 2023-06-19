package com.paddi.service.impl;

import com.paddi.entity.auth.AuthRoleElementOperation;
import com.paddi.mapper.AuthRoleElementOperationMapper;
import com.paddi.service.AuthRoleElementOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:49:58
 */
@Service
public class AuthRoleElementOperationServiceImpl implements AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationMapper authRoleElementOperationMapper;

    @Override
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIds) {
        return authRoleElementOperationMapper.getRoleElementOperationsByRoleIds(roleIds);
    }
}
