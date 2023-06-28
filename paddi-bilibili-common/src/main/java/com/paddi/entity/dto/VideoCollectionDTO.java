package com.paddi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 10:57:35
 */
@Data
public class VideoCollectionDTO {

    @NotNull(message = "视频编号不能为空")
    private Long videoId;

    @NotNull(message = "分组编号不能为空")
    private Long groupId;
}
