package com.paddi.message;

import com.paddi.enums.VideoOperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 23:14:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoOperationMessage {

    private Long userId;

    private Long videoId;

    private VideoOperationType type;

    private Map<String, Object> attachments;

    public final static String CREATE_TIME = "createTime";
}
