package com.paddi.controller;

import com.paddi.core.ret.Result;
import com.paddi.entity.dto.FollowingGroupDTO;
import com.paddi.entity.dto.UserFollowingDTO;
import com.paddi.entity.vo.FollowingGroupTagVO;
import com.paddi.entity.vo.FollowingGroupVO;
import com.paddi.entity.vo.UserFollowingVO;
import com.paddi.service.UserFollowingService;
import com.paddi.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 15:23:35
 */
@RestController
public class UserFollowingController {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    @PostMapping("/user-followings")
    public Result addUserFollowings(@RequestBody UserFollowingDTO userFollowingDTO) {
        Long userId = userSupport.getCurrentUserId();
        userFollowingDTO.setUserId(userId);
        userFollowingService.addUserFollowings(userFollowingDTO);
        return Result.success();
    }

    @GetMapping("/user-followings")
    public Result getUserFollowings(@RequestParam Integer sortType) {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroupVO> userFollowings = userFollowingService.getUserFollowings(userId, sortType);
        return Result.success(userFollowings);
    }

    @GetMapping("/user-fans")
    public Result getUserFans() {
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowingVO> userFans = userFollowingService.getUserFans(userId);
        return Result.success(userFans);
    }

    @PostMapping("/user-following-groups")
    public Result addUserFollowingGroups(@RequestBody FollowingGroupDTO followingGroupDTO) {
        Long userId = userSupport.getCurrentUserId();
        followingGroupDTO.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroupDTO);
        return Result.success(groupId);
    }

    @GetMapping("/user-following-groups")
    public Result getUserFollowingGroups() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroupTagVO> userFollowingGroups = userFollowingService.getUserFollowingGroups(userId);
        return Result.success(userFollowingGroups);
    }



}
