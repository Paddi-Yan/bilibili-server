package com.paddi.service;

import com.paddi.entity.po.FollowingGroup;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:40:20
 */
public interface FollowingGroupService {

    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);
}
