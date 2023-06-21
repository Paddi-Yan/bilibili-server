package com.paddi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 10:59:56
 */
@Getter
@AllArgsConstructor
public enum CollectionGroupType {
    DEFAULT("默认分组", "0"),
    CUSTOM("自定义分组", "1")

    ;

    private String name;

    private String type;
}
