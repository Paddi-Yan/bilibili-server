package com.paddi.strategy.video;

import com.paddi.enums.VideoOperationType;
import com.paddi.message.VideoOperationMessage;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 23:29:54
 */
public interface VideoOperation {
    /**
     * 根据不同策略执行操作处理Message
     * @param message
     */
    void execute(VideoOperationMessage message);

    /**
     * 获取该操作的枚举类型
     * @return
     */
    VideoOperationType getOperationType();
}
