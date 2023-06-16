package com.paddi.strategy;

import com.paddi.entity.po.UserFollowing;
import com.paddi.strategy.following.UserFollowingsSortStrategy;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 14:28:03
 */
public class UserFollowingsDefaultSortStrategy implements UserFollowingsSortStrategy {
    @Override
    public List<UserFollowing> sort(List<UserFollowing> userFollowings) {
        return userFollowings;
    }
}
