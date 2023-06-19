package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 21:58:22
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    void updateUser(User user);

    void deleteRefreshToken(@Param("refreshToken") String refreshToken, @Param("userId") Long userId);

    void insertRefreshToken(@Param("refreshToken")String refreshToken, @Param("userId")Long userId, @Param("date")LocalDateTime date);
}
