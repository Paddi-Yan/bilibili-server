package com.paddi.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import static com.paddi.constants.RocketMQConstants.VIDEO_COMMENT_TOPIC;
import static com.paddi.constants.RocketMQConstants.VIDEO_GROUP;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月28日 14:53:22
 */
@Component
@RocketMQMessageListener(consumerGroup = VIDEO_GROUP, topic = VIDEO_COMMENT_TOPIC)
@Slf4j
public class VideoCommentListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {

    }
}
