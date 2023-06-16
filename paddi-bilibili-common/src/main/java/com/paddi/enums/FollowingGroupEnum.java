package com.paddi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
    STEALTHILY_FOLLOWING("悄悄关注", "1"),
    DEFAULT("默认分组", "2"),
    CUSTOM("自定义分组", "3")
    ;

    private static List<String> defaultGroupType = new ArrayList<>();
    static {
        defaultGroupType.add(ESPECIAL_FOLLOWING.getType());
        defaultGroupType.add(STEALTHILY_FOLLOWING.getType());
        defaultGroupType.add(DEFAULT.getType());
    }

    private String name;

    private String type;

    public static List<String> getDefaultGroupType() {
        return defaultGroupType;
    }

}
