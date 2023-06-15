package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paddi.entity.dto.UserFollowingDTO;
import com.paddi.entity.po.FollowingGroup;
import com.paddi.entity.po.User;
import com.paddi.entity.po.UserFollowing;
import com.paddi.enums.FollowingGroupEnum;
import com.paddi.exception.ConditionException;
import com.paddi.mapper.UserFollowingMapper;
import com.paddi.service.FollowingGroupService;
import com.paddi.service.UserFollowingService;
import com.paddi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:44:17
 */
@Service
public class UserFollowingServiceImpl implements UserFollowingService {

    @Autowired
    private UserFollowingMapper userFollowingMapper;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserFollowings(UserFollowingDTO userFollowingDTO) {
        Long groupId = userFollowingDTO.getGroupId();
        if(groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getByType(FollowingGroupEnum.DEFAULT.getType());
            userFollowingDTO.setGroupId(followingGroup.getId());
        }else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if(followingGroup == null) {
                throw new ConditionException("关注分组不存在!");
            }
        }
        Long followingId = userFollowingDTO.getFollowingId();
        User user = userService.getUserById(followingId);
        if(user == null) {
            throw new ConditionException("关注的用户不存在!");
        }
        //TODO 已经关注如何处理
        userFollowingMapper.delete(new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getUserId, userFollowingDTO.getUserId())
                .eq(UserFollowing::getFollowingId, userFollowingDTO.getFollowingId()));
        UserFollowing userFollowing = new UserFollowing(userFollowingDTO);
        userFollowing.setCreateTime(LocalDateTime.now());
        userFollowingMapper.insert(userFollowing);
    }
}
