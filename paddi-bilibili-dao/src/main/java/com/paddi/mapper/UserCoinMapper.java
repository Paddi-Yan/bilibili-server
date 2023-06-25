package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.UserCoin;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月23日 19:08:25
 */
@Mapper
public interface UserCoinMapper extends BaseMapper<UserCoin> {
    Integer getUserCoinsAmountByUserId(Long userId);
}
