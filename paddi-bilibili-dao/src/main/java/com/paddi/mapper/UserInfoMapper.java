package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 22:32:57
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    void updateUserInfo(UserInfo userInfo);
}
