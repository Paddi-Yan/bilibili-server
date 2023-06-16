package com.paddi.strategy.following;

import com.paddi.entity.po.UserFollowing;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 13:59:20
 */
public interface UserFollowingsSortStrategy {
    List<UserFollowing> sort(List<UserFollowing> userFollowings);
}
