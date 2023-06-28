package com.paddi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月26日 10:16:02
 */
@Data
public class VideoCommentLikeDTO {

    @NotNull(message = "评论编号不能为空")
    private Long commentId;

    @NotNull(message = "视频编号不能为空")
    private Long videoId;
}
