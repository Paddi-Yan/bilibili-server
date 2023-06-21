package com.paddi.strategy.video;

import com.paddi.enums.VideoOperationType;
import com.paddi.mapper.VideoMapper;
import com.paddi.message.VideoOperationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 11:28:08
 */
@Service
@Slf4j
public class VideoCancelCollectOperation implements VideoOperation {

    @Autowired
    private VideoMapper videoMapper;

    @Override
    public void execute(VideoOperationMessage message) {
        Long videoId = message.getVideoId();
        Long userId = message.getUserId();
        Long groupId = (Long) message.getAttachments().get(VideoOperationMessage.COLLECTION_GROUP_ID);
        videoMapper.deleteVideoCollection(videoId, userId, groupId);
        log.info("用户[{}]收藏了视频[{}],收藏分组为[{}]", userId, videoId, groupId);
    }

    @Override
    public VideoOperationType getOperationType() {
        return VideoOperationType.CANCEL_COLLECTION;
    }
}
