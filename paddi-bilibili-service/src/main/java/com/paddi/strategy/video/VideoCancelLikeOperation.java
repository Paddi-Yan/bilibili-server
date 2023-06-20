package com.paddi.strategy.video;

import com.paddi.enums.VideoOperationType;
import com.paddi.message.VideoOperationMessage;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 00:15:37
 */
public class VideoCancelLikeOperation implements VideoOperation {
    @Override
    public void execute(VideoOperationMessage message) {

    }

    @Override
    public VideoOperationType getOperationType() {
        return VideoOperationType.CANCEL_LIKE;
    }
}
