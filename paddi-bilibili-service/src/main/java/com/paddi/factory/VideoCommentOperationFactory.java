package com.paddi.factory;

import com.paddi.enums.VideoCommentOperationType;
import com.paddi.strategy.video.comment.VideoCommentOperation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月28日 16:36:38
 */
@Component
public class VideoCommentOperationFactory implements ApplicationContextAware {

    private final Map<VideoCommentOperationType, VideoCommentOperation> INSTANCE = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, VideoCommentOperation> beans = applicationContext.getBeansOfType(VideoCommentOperation.class);
        beans.values().forEach(videoCommentOperation -> INSTANCE.put(videoCommentOperation.getOperationType(), videoCommentOperation));
    }

    public VideoCommentOperation getInstance(VideoCommentOperationType type) {
        return INSTANCE.get(type);
    }
}
