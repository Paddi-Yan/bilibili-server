package com.paddi.strategy.following;

import com.paddi.entity.po.UserFollowing;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 14:12:43
 */
public class UserFollowingsSortByNameStrategy implements UserFollowingsSortStrategy {
    @Override
    public List<UserFollowing> sort(List<UserFollowing> userFollowings) {
        return userFollowings.stream().sorted(Comparator.comparing(userFollowing -> userFollowing.getUserInfo().getNick()))
                             .collect(Collectors.toList());
    }
}
