package com.paddi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 10:31:38
 */
@AllArgsConstructor
@Getter
public enum MomentsType {
    VIDEO("视频", 0),
    LIVE("直播", 1),
    SPECIAL_COLUMN("专栏", 2),
    ;
    private String type;

    private Integer value;
}
