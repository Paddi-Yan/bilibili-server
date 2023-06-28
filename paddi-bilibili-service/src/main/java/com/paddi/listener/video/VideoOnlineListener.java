package com.paddi.listener.video;

import com.alibaba.fastjson.JSON;
import com.paddi.manager.WebSocketManager;
import com.paddi.message.OnlineCountMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

import static com.paddi.constants.RocketMQConstants.*;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月29日 00:09:51
 */
@Component
@RocketMQMessageListener(
        consumerGroup = VIDEO_ONLINE_GROUP,
        topic = VIDEO_TOPIC,
        selectorExpression = VIDEO_ONLINE_COUNT_TAG
)
@Slf4j
public class VideoOnlineListener implements RocketMQListener<String> {

    @SneakyThrows
    @Override
    public void onMessage(String s) {
        OnlineCountMessage message = JSON.parseObject(s, OnlineCountMessage.class);
        String sessionId = message.getSessionId();
        Session session = WebSocketManager.SESSIONS_MANAGER.get(sessionId);
        if(session != null && session.isOpen()) {
            session.getBasicRemote().sendText(message.getMessage());
        }
    }
}
