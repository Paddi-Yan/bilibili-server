package com.paddi.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 16:45:12
 */
@Data
@AllArgsConstructor
public class PageResult<T> {

    private Long total;

    private Integer pages;

    private List<T> list;
}
