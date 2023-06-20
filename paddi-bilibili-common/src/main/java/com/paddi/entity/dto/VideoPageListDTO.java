package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 09:35:00
 */
@Data
public class VideoPageListDTO {

    private String area;

    private PageParam pageParam;
}
