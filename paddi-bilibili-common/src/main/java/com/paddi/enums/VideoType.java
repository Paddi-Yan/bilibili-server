package com.paddi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月23日 19:20:22
 */
@AllArgsConstructor
@Getter
public enum VideoType {

    ORIGINAL("原创", 0),
    REPRODUCE("转载", 1),
    ;
    private String type;

    private Integer value;

}
