package com.paddi.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 10:56:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoCollection {
    private Long id;

    private Long videoId;

    private Long userId;

    private Long groupId;

    private LocalDateTime createTime;
}
