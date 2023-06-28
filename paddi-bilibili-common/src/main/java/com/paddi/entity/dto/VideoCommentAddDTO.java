package com.paddi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月25日 23:28:13
 */
@Data
public class VideoCommentAddDTO {

    @NotNull(message = "视频编号不能为空")
    private Long videoId;

    @NotNull(message = "视频评论区编号不能为空")
    private Long commentAreaId;

    @NotBlank(message = "评论内容不能为空")
    private String comment;

    private Long replyUserId;

    private Long rootId;

}
