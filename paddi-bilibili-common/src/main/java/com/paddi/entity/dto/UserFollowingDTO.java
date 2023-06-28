package com.paddi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 01:02:31
 */
@Data
public class UserFollowingDTO {

    private Long userId;

    @NotNull(message = "关注用户编号不能为空")
    private Long followingId;

    private Long groupId;
}
