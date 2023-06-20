package com.paddi.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 23:10:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoLike {
    private Long id;

    private Long userId;

    private Long videoId;

    private LocalDateTime createTime;

    public VideoLike(Long userId, Long videoId, LocalDateTime createTime) {
        this.userId = userId;
        this.videoId = videoId;
        this.createTime = createTime;
    }
}
