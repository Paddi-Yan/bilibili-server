package com.paddi.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.paddi.core.ret.Result;
import com.paddi.entity.dto.DanmakuDTO;
import com.paddi.entity.po.Video;
import com.paddi.mapper.VideoMapper;
import com.paddi.message.DanmakuMessage;
import com.paddi.message.OnlineCountMessage;
import com.paddi.redis.RedisCache;
import com.paddi.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.paddi.constants.RocketMQConstants.*;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月28日 21:30:31
 */
@Component
@ServerEndpoint("/bilibili/im-server/{token}/{videoId}")
@Slf4j
public class WebSocketManager {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private VideoMapper videoMapper;

    public static final ConcurrentHashMap<Long, Set<Session>> VIDEO_SESSION_MANAGER = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String, Session> SESSIONS_MANAGER = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token, @PathParam("videoId") Long videoId) {
        Long userId = null;
        Video video = videoMapper.selectById(videoId);
        if(video != null) {
            try{
                userId = TokenUtil.verifyToken(token);
                Set<Session> sessions = VIDEO_SESSION_MANAGER.computeIfAbsent(videoId, v -> new HashSet<>());
                sessions.add(session);
                SESSIONS_MANAGER.put(session.getId(), session);
                log.info("编号为[{}]的用户使用客户端[{}]连接成功,当前视频[{}]在线观看人数为[{}]人", userId, session.getId(), video.getTitle(), VIDEO_SESSION_MANAGER.get(videoId).size());
            }catch (Exception ignored){}
        }

    }

    @OnClose
    public void onClose(Session session, @PathParam("videoId") Long videoId) {
        Set<Session> sessions = VIDEO_SESSION_MANAGER.get(session);
        sessions.remove(videoId);
        if(CollectionUtil.isEmpty(sessions)) {
            VIDEO_SESSION_MANAGER.remove(videoId);
        }
    }

    @OnMessage
    public void onMessage(Session session,
                          @PathParam("token") String token,
                          @PathParam("videoId") Long videoId,
                          String message) {
        Long userId = null;
        try {
            userId = TokenUtil.verifyToken(token);
        } catch(Exception ignored) { }
        if(userId != null && StrUtil.isNotEmpty(message)) {
            log.info("编号为[{}]的用户使用客户端[{}]发送消息[{}]", userId, session.getId(), message);
            Set<Session> sessions = VIDEO_SESSION_MANAGER.get(videoId);
            DanmakuDTO danmakuDTO = JSON.parseObject(message, DanmakuDTO.class);
            for(Session otherSessionInThisVideo : sessions) {
                String sessionId = otherSessionInThisVideo.getId();
                DanmakuMessage danmakuMessage = new DanmakuMessage(sessionId, danmakuDTO);
                rocketMQTemplate.convertAndSend(DANMAKU_TOPIC + ":" + DANMAKU_LAUNCH_TAG, danmakuMessage);
            }
            //异步落库
            rocketMQTemplate.convertAndSend(DANMAKU_TOPIC + ":" + DANMAKU_SAVE_TAG, danmakuDTO);
        }
    }

    @Scheduled(fixedRate = 5000)
    private void noticeOnlineCount() {
        for(Map.Entry<Long, Set<Session>> entry : VIDEO_SESSION_MANAGER.entrySet()) {
            Set<Session> sessions = entry.getValue();
            int onlineCount = sessions.size();
            for(Session session : sessions) {
                OnlineCountMessage message = new OnlineCountMessage("当前在线人数为" + onlineCount + "人", session.getId());
                rocketMQTemplate.convertAndSend(VIDEO_TOPIC + ":" + VIDEO_ONLINE_COUNT_TAG, JSON.toJSONString(message));
            }
        }
    }

    public void error(Session session, Result result) throws IOException {
        session.getBasicRemote().sendText(JSON.toJSONString(result));
    }
}
