package com.paddi.listener.danmaku;

import com.alibaba.fastjson.JSON;
import com.paddi.manager.WebSocketManager;
import com.paddi.message.DanmakuMessage;
import lombok.SneakyThrows;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

import static com.paddi.constants.RocketMQConstants.*;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月29日 00:45:09
 */
@Component
@RocketMQMessageListener(
        consumerGroup = DANMAKU_LAUNCH_GROUP,
        topic = DANMAKU_TOPIC,
        selectorExpression = DANMAKU_LAUNCH_TAG
)
public class DanmakuLaunchListener implements RocketMQListener<String> {
    @SneakyThrows
    @Override
    public void onMessage(String s) {
        DanmakuMessage danmakuMessage = JSON.parseObject(s, DanmakuMessage.class);
        String sessionId = danmakuMessage.getSessionId();
        Session session = WebSocketManager.SESSIONS_MANAGER.get(sessionId);
        if(session != null && session.isOpen()) {
            session.getBasicRemote().sendText(JSON.toJSONString(danmakuMessage.getDanmakuDTO()));
        }
    }
}
