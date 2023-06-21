package com.paddi.strategy.video;

import com.paddi.entity.po.VideoCollection;
import com.paddi.enums.VideoOperationType;
import com.paddi.mapper.VideoCollectionMapper;
import com.paddi.mapper.VideoMapper;
import com.paddi.message.VideoOperationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.paddi.message.VideoOperationMessage.COLLECTION_GROUP_ID;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 11:18:04
 */
@Service
@Slf4j
public class VideoAddCollectOperation implements VideoOperation {

    @Autowired
    private VideoCollectionMapper videoCollectionMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Override
    public void execute(VideoOperationMessage message) {
        Long userId = message.getUserId();
        Long videoId = message.getVideoId();
        Long groupId = (Long) message.getAttachments().get(COLLECTION_GROUP_ID);
        Boolean notExecute = videoMapper.getVideoCollection(userId, videoId, groupId) == null;
        if(notExecute) {
            log.info("用户[{}]收藏了视频[{}],收藏分组为[{}]", userId, videoId, groupId);
            videoCollectionMapper.insert(VideoCollection.builder()
                                                        .userId(userId)
                                                        .videoId(videoId)
                                                        .groupId(groupId)
                                                        .createTime(LocalDateTime.now())
                                                        .build());
        }
    }

    @Override
    public VideoOperationType getOperationType() {
        return VideoOperationType.ADD_COLLECTION;
    }
}
