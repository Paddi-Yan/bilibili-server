package com.paddi.service;

import com.paddi.entity.dto.FollowingGroupDTO;
import com.paddi.entity.dto.UserFollowingDTO;
import com.paddi.entity.vo.FollowingGroupTagVO;
import com.paddi.entity.vo.FollowingGroupVO;
import com.paddi.entity.vo.UserFollowingVO;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:43:28
 */
public interface UserFollowingService {

    void addUserFollowings(UserFollowingDTO userFollowingDTO);

    List<FollowingGroupVO> getUserFollowings(Long userId, Integer sortType);

    List<UserFollowingVO> getUserFans(Long userId);

    Long addUserFollowingGroups(FollowingGroupDTO followingGroupDTO);

    List<FollowingGroupTagVO> getUserFollowingGroups(Long userId);
}
