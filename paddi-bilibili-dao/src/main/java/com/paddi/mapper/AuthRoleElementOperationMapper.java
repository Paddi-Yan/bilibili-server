package com.paddi.mapper;

import com.paddi.entity.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:52:16
 */
@Mapper
public interface AuthRoleElementOperationMapper {
    List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(@Param("roleIds") Set<Long> roleIds);
}
