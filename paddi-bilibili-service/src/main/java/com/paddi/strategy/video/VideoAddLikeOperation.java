package com.paddi.strategy.video;

import com.paddi.entity.po.VideoLike;
import com.paddi.enums.VideoOperationType;
import com.paddi.mapper.VideoLikeMapper;
import com.paddi.mapper.VideoMapper;
import com.paddi.message.VideoOperationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 00:02:38
 */
@Service
@Slf4j
public class VideoAddLikeOperation implements VideoOperation{

    @Autowired
    private VideoLikeMapper videoLikeMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Override
    public void execute(VideoOperationMessage message) {
        Boolean notExecute = videoMapper.getVideoLikeByVideoIdAndUserId(message.getVideoId(), message.getUserId()) == null;
        if(notExecute) {
            VideoLike videoLike = new VideoLike(message.getUserId(), message.getVideoId(), LocalDateTime.now());
            log.info("用户[{}]点赞视频[{}]", videoLike.getUserId(), videoLike.getVideoId());
            videoLikeMapper.insert(videoLike);
        }
    }

    @Override
    public VideoOperationType getOperationType() {
        return VideoOperationType.ADD_LIKE;
    }
}
