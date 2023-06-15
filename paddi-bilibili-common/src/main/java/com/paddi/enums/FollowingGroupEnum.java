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

    ESPECIAL_FOLLOWING("特别关注", "0"),
    stealthily_FOLLOWING("悄悄关注", "1"),
    DEFAULT("特别关注", "2"),

    ;

    private String name;

    private String type;

}
