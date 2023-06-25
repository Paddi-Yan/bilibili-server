package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月23日 18:59:37
 */
@Data
public class VideoCoinsDTO {
    private Long videoId;

    private Integer amount;
}
