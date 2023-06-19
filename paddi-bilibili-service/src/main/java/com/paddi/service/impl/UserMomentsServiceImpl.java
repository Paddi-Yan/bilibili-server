package com.paddi.service.impl;

import com.alibaba.fastjson.JSON;
import com.paddi.constants.RedisKey;
import com.paddi.entity.dto.UserMomentsDTO;
import com.paddi.entity.po.UserMoments;
import com.paddi.mapper.UserMomentsMapper;
import com.paddi.redis.RedisCache;
import com.paddi.service.UserMomentsService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.paddi.constants.RocketMQConstants.MOMENTS_TOPIC;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月17日 20:09:37
 */
@Service
public class UserMomentsServiceImpl implements UserMomentsService {

    @Autowired
    private UserMomentsMapper userMomentsMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedisCache redisCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void postMoments(UserMomentsDTO userMomentsDTO) {
        UserMoments userMoments = new UserMoments(userMomentsDTO);
        userMoments.setCreateTime(LocalDateTime.now());
        userMomentsMapper.insert(userMoments);
        rocketMQTemplate.convertAndSend(MOMENTS_TOPIC, JSON.toJSONString(userMoments));
    }

    @Override
    public List<UserMoments> getUserSubscribedMoments(Long userId) {
        String redisKey = RedisKey.SUBSCRIBED + userId;
        List<UserMoments> subscribedMoments = redisCache.<List<UserMoments>>getCacheObject(redisKey);
        return subscribedMoments;
    }
}
