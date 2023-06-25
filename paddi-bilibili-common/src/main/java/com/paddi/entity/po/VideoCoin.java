package com.paddi.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月23日 19:14:19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoCoin {
    private Long id;

    private Long videoId;

    private Long userId;

    private Integer amount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
