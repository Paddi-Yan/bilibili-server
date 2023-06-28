package com.paddi.listener;

import com.alibaba.fastjson.JSON;
import com.paddi.factory.VideoCommentOperationFactory;
import com.paddi.message.VideoCommentMessage;
import com.paddi.strategy.video.comment.VideoCommentOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.paddi.constants.RocketMQConstants.VIDEO_COMMENT_GROUP;
import static com.paddi.constants.RocketMQConstants.VIDEO_COMMENT_TOPIC;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月28日 14:53:22
 */
@Component
@RocketMQMessageListener(
        consumerGroup = VIDEO_COMMENT_GROUP,
        topic = VIDEO_COMMENT_TOPIC,
        consumeMode = ConsumeMode.ORDERLY
)
@Slf4j
public class VideoCommentListener implements RocketMQListener<String> {

    @Autowired
    private VideoCommentOperationFactory factory;

    @Override
    public void onMessage(String s) {
        VideoCommentMessage message = JSON.parseObject(s, VideoCommentMessage.class);
        VideoCommentOperation videoCommentOperation = factory.getInstance(message.getOperationType());
        videoCommentOperation.execute(message);
    }
}
