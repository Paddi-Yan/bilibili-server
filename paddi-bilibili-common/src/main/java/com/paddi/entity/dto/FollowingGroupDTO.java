package com.paddi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 15:44:28
 */
@Data
public class FollowingGroupDTO {
    private Long userId;

    @NotBlank(message = "关注分组名称不能为空")
    private String name;
}
