package com.paddi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 23:15:36
 */
@AllArgsConstructor
@Getter
public enum VideoOperationType {

    ADD_LIKE("点赞", 0),
    CANCEL_LIKE("撤销点赞", 1),
    ADD_COLLECTION("添加收藏", 2),
    CANCEL_COLLECTION("撤销收藏", 3);

    private String operation;

    private Integer value;

}
