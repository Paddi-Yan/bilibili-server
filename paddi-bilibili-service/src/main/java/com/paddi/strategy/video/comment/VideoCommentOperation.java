package com.paddi.strategy.video.comment;

import com.paddi.enums.VideoCommentOperationType;
import com.paddi.message.VideoCommentMessage;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月28日 15:10:56
 */
public interface VideoCommentOperation {
    /**
     * 根据不同策略执行操作处理Message
     * @param message
     */
    void execute(VideoCommentMessage message);

    /**
     * 获取该操作的枚举类型
     * @return
     */
    VideoCommentOperationType getOperationType();
}
