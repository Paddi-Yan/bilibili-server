package com.paddi.strategy.following;

import com.paddi.entity.po.UserFollowing;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 14:28:03
 */
@Component
public class UserFollowingsDefaultSortStrategy implements UserFollowingsSortStrategy {
    @Override
    public List<UserFollowing> sort(List<UserFollowing> userFollowings) {
        return userFollowings;
    }
}
