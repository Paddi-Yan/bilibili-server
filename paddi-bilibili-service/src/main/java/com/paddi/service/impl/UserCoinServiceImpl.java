package com.paddi.service.impl;

import com.paddi.mapper.UserCoinMapper;
import com.paddi.service.UserCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月23日 19:08:01
 */
@Service
public class UserCoinServiceImpl implements UserCoinService {

    @Autowired
    private UserCoinMapper userCoinMapper;


    @Override
    public Integer getUserCoinsAmountByUserId(Long userId) {
        return userCoinMapper.getUserCoinsAmountByUserId(userId);
    }
}
