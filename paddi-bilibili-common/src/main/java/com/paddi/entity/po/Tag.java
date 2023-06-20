package com.paddi.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 10:02:46
 */
@Data
public class Tag {
    private Long id;

    private String name;

    private LocalDateTime createTime;
}
