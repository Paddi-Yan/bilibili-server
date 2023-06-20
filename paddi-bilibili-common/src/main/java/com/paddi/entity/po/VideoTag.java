package com.paddi.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:54:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoTag {
    private Long id;

    private Long videoId;

    private Long tagId;

    private LocalDateTime createTime;

}
