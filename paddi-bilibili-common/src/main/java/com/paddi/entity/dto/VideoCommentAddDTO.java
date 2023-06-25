package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月25日 23:28:13
 */
@Data
public class VideoCommentAddDTO {

    private Long videoId;

    private String comment;

    private Long replyUserId;

    private Long rootId;

}
