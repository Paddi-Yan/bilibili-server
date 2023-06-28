package com.paddi.entity.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月23日 18:59:37
 */
@Data
public class VideoCoinsDTO {

    @NotNull(message = "视频编号不能为空")
    private Long videoId;

    @NotNull
    @Min(value = 1, message = "投币数量不合法")
    @Min(value = 2, message = "投币数量不合法")
    private Integer amount;
}
