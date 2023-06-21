package com.paddi.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 11:06:45
 */
@Data
public class CollectionGroup {
    private Long id;
    private Long userId;
    private String name;
    private String type;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
