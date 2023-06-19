package com.paddi.mapper;

import com.paddi.entity.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:33:09
 */
@Mapper
public interface UserRoleMapper {
    List<UserRole> getUserRoleByUserId(Long userId);

    void insert(UserRole userRole);
}
