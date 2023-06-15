package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 01:02:31
 */
@Data
public class UserFollowingDTO {
    private Long userId;

    private Long followingId;

    private Long groupId;
}
