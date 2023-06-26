package com.paddi.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 16:51:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParam {

    private Integer pageNum;

    private Integer pageSize;

}
