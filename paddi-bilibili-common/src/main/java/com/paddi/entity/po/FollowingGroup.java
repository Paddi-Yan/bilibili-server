package com.paddi.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:38:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowingGroup {

    private Long id;

    private Long userId;

    private String name;

    private String type;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
