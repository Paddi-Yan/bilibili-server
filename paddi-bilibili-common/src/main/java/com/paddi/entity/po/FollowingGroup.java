package com.paddi.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:38:45
 */
@Data
public class FollowingGroup {

    private Long id;

    private Long userid;

    private String name;

    private String type;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
