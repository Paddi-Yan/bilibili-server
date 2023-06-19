package com.paddi.service;

import com.paddi.entity.auth.AuthRoleElementOperation;

import java.util.List;
import java.util.Set;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:49:53
 */
public interface AuthRoleElementOperationService {
    List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIds);
}
