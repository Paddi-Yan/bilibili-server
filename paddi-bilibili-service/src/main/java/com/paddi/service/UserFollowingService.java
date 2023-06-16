package com.paddi.service;

import com.paddi.entity.dto.UserFollowingDTO;
import com.paddi.entity.vo.FollowingGroupVO;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:43:28
 */
public interface UserFollowingService {

    void addUserFollowings(UserFollowingDTO userFollowingDTO);

    List<FollowingGroupVO> getUserFollowings(Long userId, Integer sortType);
}
