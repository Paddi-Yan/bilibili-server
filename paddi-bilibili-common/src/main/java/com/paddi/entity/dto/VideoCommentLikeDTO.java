package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月26日 10:16:02
 */
@Data
public class VideoCommentLikeDTO {

    private Long commentId;

    private Long videoId;
}
