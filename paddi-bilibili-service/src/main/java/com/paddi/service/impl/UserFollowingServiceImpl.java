package com.paddi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paddi.entity.dto.FollowingGroupDTO;
import com.paddi.entity.dto.UserFollowingDTO;
import com.paddi.entity.po.FollowingGroup;
import com.paddi.entity.po.User;
import com.paddi.entity.po.UserFollowing;
import com.paddi.entity.po.UserInfo;
import com.paddi.entity.vo.FollowingGroupTagVO;
import com.paddi.entity.vo.FollowingGroupVO;
import com.paddi.entity.vo.UserFollowingVO;
import com.paddi.entity.vo.UserInfoVO;
import com.paddi.enums.FollowingGroupEnum;
import com.paddi.enums.SortType;
import com.paddi.exception.BadRequestException;
import com.paddi.exception.ConditionException;
import com.paddi.factory.UserFollowingSortStrategyFactory;
import com.paddi.mapper.FollowingGroupMapper;
import com.paddi.mapper.UserFollowingMapper;
import com.paddi.service.FollowingGroupService;
import com.paddi.service.UserFollowingService;
import com.paddi.service.UserService;
import com.paddi.strategy.following.UserFollowingsSortStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    private FollowingGroupMapper followingGroupMapper;

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
        SortType sortTypeEnum = SortType.getSortType(sortType);
        if(sortTypeEnum == null) {
            throw new BadRequestException("非法的排序类型");
        }
        UserFollowingsSortStrategy sortStrategy = UserFollowingSortStrategyFactory.createSortStrategy(sortTypeEnum);
        sortStrategy.sort(userFollowings);

        //获取用户的关注分组
        List<FollowingGroup> followingGroupList = followingGroupService.getByUserId(userId);
        //创建全部关注列表
        FollowingGroupVO allGroup = FollowingGroupVO.builder()
                .userId(userId)
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

    /**
     * 第一步：获取当前用户的粉丝列表
     * 第二步：根据粉丝的用户id查询基本信息
     * 第三步：查询当前用户是否已经关注该粉丝
     * @param userId
     * @return
     */
    @Override
    public List<UserFollowingVO> getUserFans(Long userId) {
        //获取粉丝列表
        List<UserFollowing> fansList = userFollowingMapper.selectList(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing :: getFollowingId, userId));
        Set<Long> fanIdList = fansList.stream().map(UserFollowing :: getUserId).collect(Collectors.toSet());
        List<UserInfo> fansUserInfoList = new ArrayList<>();
        //获取粉丝具体用户信息
        if(CollectionUtil.isNotEmpty(fansList)) {
            fansUserInfoList = userService.getUserByUserIds(fanIdList);
        }
        //查询该用户的关注列表
        List<UserFollowing> userFollowingsList = userFollowingMapper.selectList(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing :: getUserId, userId));
        Set<Long> userFollowingsIdSet = new HashSet<>();
        if(CollectionUtil.isNotEmpty(userFollowingsList)) {
            userFollowingsIdSet = userFollowingsList.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        }

        Map<Long, UserInfo> userIdToUserInfo = fansUserInfoList.parallelStream().collect(Collectors.toMap(UserInfo :: getUserId, Function.identity()));
        List<UserFollowingVO> result = new ArrayList<>();
        //遍历粉丝列表
        for(UserFollowing fan : fansList) {
            Long fanUserId = fan.getUserId();
            UserInfo userInfo = userIdToUserInfo.get(fanUserId);
            fan.setUserInfo(userInfo);
            //未互相关注
            userInfo.setFollowed(false);

            //转化成VO对象
            UserInfoVO userInfoVO = new UserInfoVO(userInfo);
            UserFollowingVO fansFollowingVO = new UserFollowingVO(fan);
            fansFollowingVO.setUserInfoVO(userInfoVO);

            //查看是否互相关注
            if(userFollowingsIdSet.contains(fanUserId)) {
                userInfoVO.setFollowed(true);
            }
        }
        return result;
    }

    @Override
    public Long addUserFollowingGroups(FollowingGroupDTO followingGroupDTO) {
        FollowingGroup followingGroup = FollowingGroup.builder()
                                             .userId(followingGroupDTO.getUserId())
                                             .name(followingGroupDTO.getName())
                                             .type(FollowingGroupEnum.CUSTOM.getType())
                                             .createTime(LocalDateTime.now())
                                             .build();
        followingGroupMapper.insert(followingGroup);
        return followingGroup.getId();
    }

    @Override
    public List<FollowingGroupTagVO> getUserFollowingGroups(Long userId) {
        //查询用户字对应的分组以及系统的默认分组
        List<FollowingGroup> followingGroups
                = followingGroupMapper.selectList(new LambdaQueryWrapper<FollowingGroup>()
                .eq(FollowingGroup :: getUserId, userId)
                .or(wrapper -> wrapper.in(FollowingGroup :: getType, FollowingGroupEnum.getDefaultGroupType())));
        List<FollowingGroupTagVO> followingGroupTags = followingGroups.parallelStream()
                                                           .map(followingGroup -> new FollowingGroupTagVO(followingGroup.getId(), followingGroup.getName(), followingGroup.getType()))
                                                           .collect(Collectors.toList());
        return followingGroupTags;
    }


}
