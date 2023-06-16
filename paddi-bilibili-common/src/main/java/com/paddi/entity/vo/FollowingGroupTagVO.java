package com.paddi.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 15:52:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowingGroupTagVO {
    private Long groupId;

    private String name;

    private String type;
}
