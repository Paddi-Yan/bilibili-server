package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.UserFollowing;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:44:38
 */
@Mapper
public interface UserFollowingMapper extends BaseMapper<UserFollowing> {
}
