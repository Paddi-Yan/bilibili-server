package com.paddi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 13:53:12
 */
@Getter
@AllArgsConstructor
public enum SortType {
    DEFAULT("默认", 0),
    BY_TIME_ASC("按照时间升序排列", 1),
    BY_TIME_DESC("按照时间降序排列", 2),
    BY_NAME("按照名称升序排列", 3),
    ;

    private static Map<Integer, SortType> valueToSortTypeMap = new HashMap<>();
    static {
        valueToSortTypeMap.put(0, DEFAULT);
        valueToSortTypeMap.put(1, BY_TIME_ASC);
        valueToSortTypeMap.put(2, BY_TIME_DESC);
        valueToSortTypeMap.put(3, BY_NAME);
    }

    private String sortType;

    private Integer value;

    public static SortType getSortType(Integer value) {
        return valueToSortTypeMap.get(value);
    }
}
