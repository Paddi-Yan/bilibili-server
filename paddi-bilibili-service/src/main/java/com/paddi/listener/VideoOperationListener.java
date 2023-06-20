package com.paddi.listener;

import com.alibaba.fastjson.JSON;
import com.paddi.factory.VideoOperationFactory;
import com.paddi.message.VideoOperationMessage;
import com.paddi.strategy.video.VideoOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.paddi.constants.RocketMQConstants.VIDEO_GROUP;
import static com.paddi.constants.RocketMQConstants.VIDEO_TOPIC;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 00:25:41
 */
@Component
@RocketMQMessageListener(consumerGroup = VIDEO_GROUP, topic = VIDEO_TOPIC)
@Slf4j
public class VideoOperationListener implements RocketMQListener<String> {

    @Autowired
    private VideoOperationFactory factory;

    @Override
    public void onMessage(String s) {
        VideoOperationMessage message = JSON.parseObject(s, VideoOperationMessage.class);
        VideoOperation videoOperation = factory.getInstance(message.getType());
        videoOperation.execute(message);
    }
}
