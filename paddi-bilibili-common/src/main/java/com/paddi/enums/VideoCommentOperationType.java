package com.paddi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月28日 15:08:42
 */
@AllArgsConstructor
@Getter
public enum VideoCommentOperationType {

    ADD_COMMENT("发布评论", 4),
    ;
    private String operation;

    private Integer value;
}
