package com.paddi.factory;

import com.paddi.enums.VideoOperationType;
import com.paddi.strategy.video.VideoOperation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 00:03:38
 */
@Component
public class VideoOperationFactory implements ApplicationContextAware {

    private final Map<VideoOperationType, VideoOperation> INSTANCE = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, VideoOperation> beans = applicationContext.getBeansOfType(VideoOperation.class);
        beans.values().forEach(videoOperation -> INSTANCE.put(videoOperation.getOperationType(), videoOperation));
    }

    public VideoOperation getInstance(VideoOperationType type) {
        return INSTANCE.get(type);
    }
}
