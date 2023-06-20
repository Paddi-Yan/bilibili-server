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
    CANCEL_LIKE("撤销点赞", 1)
    ;

    private String operation;

    private Integer value;

}
