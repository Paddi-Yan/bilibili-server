package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月28日 23:49:13
 */
@Data
public class DanmakuDTO {
    private Long videoId;

    private Long userId;

    private String content;

    private String danmakuTime;
}
