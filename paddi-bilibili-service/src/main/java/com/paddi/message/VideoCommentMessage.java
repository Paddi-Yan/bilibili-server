package com.paddi.message;

import com.paddi.enums.VideoCommentOperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月27日 17:25:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoCommentMessage {

    private Long userId;

    private Long videoId;

    private Long commentAreaId;

    private String comment;

    private Map<String, Long> attachments;

    private VideoCommentOperationType operationType;

    public final static String ROOT_COMMENT_ID = "rootId";

    public final static String REPLY_USER_ID = "replyUserId";

    public final static String COMMENT_ID = "commentId";
}
