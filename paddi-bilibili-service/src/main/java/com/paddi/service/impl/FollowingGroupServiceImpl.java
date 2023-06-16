package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paddi.entity.po.FollowingGroup;
import com.paddi.mapper.FollowingGroupMapper;
import com.paddi.service.FollowingGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:41:05
 */
@Service
public class FollowingGroupServiceImpl implements FollowingGroupService {

    @Autowired
    private FollowingGroupMapper followingGroupMapper;

    @Override
    public FollowingGroup getByType(String type) {
        return followingGroupMapper.selectOne(new LambdaQueryWrapper<FollowingGroup>().eq(FollowingGroup::getType, type));
    }

    @Override
    public FollowingGroup getById(Long id) {
        return followingGroupMapper.selectById(id);
    }

    @Override
    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupMapper.selectList(new LambdaQueryWrapper<FollowingGroup>().eq(FollowingGroup::getUserid, userId));
    }

}
