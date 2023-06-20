package com.paddi.factory;

import com.paddi.enums.SortType;
import com.paddi.exception.ConditionException;
import com.paddi.strategy.following.*;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 14:20:04
 */
public class UserFollowingSortStrategyFactory {
    public static UserFollowingsSortStrategy createSortStrategy(SortType sortType) {
        switch(sortType) {
            case BY_TIME_ASC:
                return new UserFollowingsSortByTimeAscStrategy();
            case BY_TIME_DESC:
                return new UserFollowingsSortByTimeDescStrategy();
            case BY_NAME:
                return new UserFollowingsSortByNameStrategy();
            case DEFAULT:
                return new UserFollowingsDefaultSortStrategy();
            default:
                throw new ConditionException("未知的排序类型");
        }
    }
}
