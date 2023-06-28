package com.paddi.listener.danmaku;

import com.alibaba.fastjson.JSON;
import com.paddi.constants.RedisKey;
import com.paddi.entity.dto.DanmakuDTO;
import com.paddi.entity.po.Danmaku;
import com.paddi.mapper.DanmakuMapper;
import com.paddi.redis.RedisCache;
import com.paddi.util.TimeUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.paddi.constants.RocketMQConstants.*;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月29日 00:45:09
 */
@Component
@RocketMQMessageListener(
        consumerGroup = DANMAKU_SAVE_GROUP,
        topic = DANMAKU_TOPIC,
        selectorExpression = DANMAKU_SAVE_TAG
)
public class DanmakuSaveListener implements RocketMQListener<String> {

    @Autowired
    private DanmakuMapper danmakuMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(String s) {
        DanmakuDTO danmakuDTO = JSON.parseObject(s, DanmakuDTO.class);
        Danmaku danmaku = new Danmaku(danmakuDTO);
        danmaku.setCreateTime(LocalDateTime.now());
        danmakuMapper.insert(danmaku);

        String danmakuZsetKey = RedisKey.DANMAKU_ZSET + danmakuDTO.getVideoId();
        //需要先判断是否存在再存入
        redisCache.addValueToZSet(danmakuZsetKey, danmaku.getId(), TimeUtils.parseTimeToSeconds(danmaku.getDanmakuTime()));

        String danmakuHashKey = RedisKey.DANMAKU_HASH + danmakuDTO.getVideoId();
        redisCache.addValueToHash(danmakuHashKey, danmaku.getId(), danmaku.getContent());
    }
}
