package com.paddi.mapper;

import com.paddi.entity.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 01:03:26
 */
@Mapper
public interface AuthRoleMenuMapper {
    List<AuthRoleMenu> getAuthRoleMenusByRoleIds(@Param("roleIds") Set<Long> roleIds);
}
