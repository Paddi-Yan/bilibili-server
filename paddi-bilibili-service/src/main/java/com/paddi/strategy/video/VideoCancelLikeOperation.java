package com.paddi.strategy.video;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paddi.entity.po.VideoLike;
import com.paddi.enums.VideoOperationType;
import com.paddi.mapper.VideoLikeMapper;
import com.paddi.message.VideoOperationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 00:15:37
 */
@Service
@Slf4j
public class VideoCancelLikeOperation implements VideoOperation {

    @Autowired
    private VideoLikeMapper videoLikeMapper;

    @Override
    public void execute(VideoOperationMessage message) {
        log.info("用户[{}]取消点赞视频[{}]", message.getUserId(), message.getVideoId());
        videoLikeMapper.delete(new LambdaQueryWrapper<VideoLike>()
                .eq(VideoLike::getVideoId, message.getVideoId())
                .eq(VideoLike::getUserId, message.getUserId()));
    }

    @Override
    public VideoOperationType getOperationType() {
        return VideoOperationType.CANCEL_LIKE;
    }
}
