package com.paddi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paddi.entity.dto.UserFollowingDTO;
import com.paddi.entity.po.FollowingGroup;
import com.paddi.entity.po.User;
import com.paddi.entity.po.UserFollowing;
import com.paddi.entity.po.UserInfo;
import com.paddi.entity.vo.FollowingGroupVO;
import com.paddi.entity.vo.UserInfoVO;
import com.paddi.enums.FollowingGroupEnum;
import com.paddi.enums.SortType;
import com.paddi.exception.ConditionException;
import com.paddi.factory.UserFollowingSortStrategyFactory;
import com.paddi.mapper.UserFollowingMapper;
import com.paddi.service.FollowingGroupService;
import com.paddi.service.UserFollowingService;
import com.paddi.service.UserService;
import com.paddi.strategy.following.UserFollowingsSortStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    /**
     * 将关注列表按照不同枚举值进行排序
     * 第一步：获取关注的用户列表
     * 第二步：根据关注用户的id查询关注用户的基本信息
     * 第三步：将关注用户按关注分组进行分类
     * @param userId 用户编号
     * @param sortType 排序类型枚举值
     * @return 经过排序后的用户列表
     */
    @Override
    public List<FollowingGroupVO> getUserFollowings(Long userId, Integer sortType) {
        //获取到该用户关注列表基本信息
        List<UserFollowing> userFollowings = userFollowingMapper.selectList(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing :: getUserId, userId));
        //获取到用户关注的用户编号列表
        Set<Long> userFollowingIds = userFollowings.stream().map(UserFollowing :: getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(userFollowingIds)) {
            //查询关注用户具体信息
            userInfoList = userService.getUserByUserIds(userFollowingIds);
        }
        //构建用户编号和用户具体信息的映射Map
        Map<Long, UserInfo> userIdToUserInfo = userInfoList.parallelStream()
                                                  .collect(Collectors.toMap(UserInfo :: getUserId, Function.identity()));
        List<UserInfoVO> userInfoVOList = userInfoList.parallelStream()
                                               .map(UserInfoVO :: new)
                                               .collect(Collectors.toList());
        //将关注用户的具体信息填充到对应的userFollowing中
        for(UserFollowing userFollowing : userFollowings) {
            userFollowing.setUserInfo(userIdToUserInfo.get(userFollowing.getFollowingId()));
        }

        //根据不同排序类型进行排序
        UserFollowingsSortStrategy sortStrategy = UserFollowingSortStrategyFactory.createSortStrategy(SortType.getSortType(sortType));
        sortStrategy.sort(userFollowings);

        //获取用户的关注分组
        List<FollowingGroup> followingGroupList = followingGroupService.getByUserId(userId);
        //创建全部关注列表
        FollowingGroupVO allGroup = FollowingGroupVO.builder()
                .userid(userId)
                .name(FollowingGroupEnum.ALL.getName())
                .type(FollowingGroupEnum.ALL.getType())
                .followingUserInfoList(userInfoVOList)
                .build();

        //不同分组中的关注好友
        List<FollowingGroupVO> userFollowingGroupList = new ArrayList<>(followingGroupList.size() + 1);
        userFollowingGroupList.add(allGroup);
        //遍历用户的关注分组
        for(FollowingGroup followingGroup : followingGroupList) {
            //当前分组中的好友信息
            List<UserInfoVO> currentGroupUserInfoList = new ArrayList<>();
            for(UserFollowing userFollowing : userFollowings) {
                if(followingGroup.getId().equals(userFollowing.getGroupId())) {
                    currentGroupUserInfoList.add(new UserInfoVO(userFollowing.getUserInfo()));
                }
            }
            FollowingGroupVO followingGroupVO = new FollowingGroupVO(followingGroup, currentGroupUserInfoList);
            userFollowingGroupList.add(followingGroupVO);
        }
        //TODO 将关注列表按照关注先后进行排序还是按照名称进行排序
        return userFollowingGroupList;
    }



}
