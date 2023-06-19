package com.paddi.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paddi.constants.RedisKey;
import com.paddi.entity.po.UserMoments;
import com.paddi.entity.vo.UserFollowingVO;
import com.paddi.redis.RedisCache;
import com.paddi.service.UserFollowingService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.paddi.constants.RocketMQConstants.MOMENTS_GROUP;
import static com.paddi.constants.RocketMQConstants.MOMENTS_TOPIC;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月17日 20:31:26
 */
@Component
@RocketMQMessageListener(consumerGroup = MOMENTS_GROUP, topic = MOMENTS_TOPIC)
@Slf4j
public class UserMomentsListener implements RocketMQListener<String> {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void onMessage(String message) {
        UserMoments userMoments = JSON.parseObject(message, UserMoments.class);
        log.info("接收到消息:{}", userMoments);
        Long userId = userMoments.getUserId();
        List<UserFollowingVO> userFansList = userFollowingService.getUserFans(userId);
        for(UserFollowingVO fan : userFansList) {
            String key = RedisKey.SUBSCRIBED + fan.getUserId();
            List<UserMoments> subscribedList = redisCache.<List>getCacheObject(key);
            if(CollectionUtil.isEmpty(subscribedList)) {
                subscribedList = new ArrayList<>();
            }
            subscribedList.add(userMoments);
            redisCache.setCacheObject(key, subscribedList);
        }
    }
}
