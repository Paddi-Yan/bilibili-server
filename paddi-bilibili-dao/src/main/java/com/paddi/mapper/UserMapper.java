package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 21:58:22
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    void updateUser(User user);
}
