package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 15:44:28
 */
@Data
public class FollowingGroupDTO {
    private Long userId;

    private String name;
}
