package com.paddi.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月27日 15:55:01
 */
@Data
public class VideoCommentArea {

    private Long id;

    private Long videoId;

    private Long userId;

    private Integer totalCommentCount;

    /**
     * 评论区状态
     * 开启-true
     * 关闭-false
     */
    private Boolean status;

    private LocalDateTime createTime;
}
