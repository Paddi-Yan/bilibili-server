package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月25日 23:48:57
 */
@Data
public class VideoCommentPageListDTO {

    private Long videoId;

    private PageParam pageParam;
}
