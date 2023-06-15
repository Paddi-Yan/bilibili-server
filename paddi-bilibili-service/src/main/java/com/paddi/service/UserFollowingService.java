package com.paddi.service;

import com.paddi.entity.dto.UserFollowingDTO;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:43:28
 */
public interface UserFollowingService {

    void addUserFollowings(UserFollowingDTO userFollowingDTO);
}
