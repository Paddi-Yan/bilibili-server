package com.paddi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:46:01
 */
@Getter
@AllArgsConstructor
public enum FollowingGroupEnum {

    ALL("全部关注", "-1"),
    ESPECIAL_FOLLOWING("特别关注", "0"),
    stealthily_FOLLOWING("悄悄关注", "1"),
    DEFAULT("默认分组", "2"),

    ;

    private String name;

    private String type;

}
