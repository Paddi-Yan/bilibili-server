package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 11:41:00
 */
@Mapper
public interface RefreshTokenMapper extends BaseMapper<RefreshToken> {
    RefreshToken getRefreshTokenDetail(@Param("refreshToken") String refreshToken);
}
