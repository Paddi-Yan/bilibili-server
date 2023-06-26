package com.paddi.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 09:53:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoStatisticsDataVO {

    private Long count;

    private Boolean flag;
}
