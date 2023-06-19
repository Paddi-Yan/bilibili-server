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
    LIVE("视频", 0),
    SPECIAL_COLUMN("视频", 0),
    ;
    private String type;

    private Integer value;
}
