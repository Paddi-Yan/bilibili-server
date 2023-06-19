package com.paddi.controller;

import com.paddi.aspect.ApiLimitedRole;
import com.paddi.aspect.DataLimited;
import com.paddi.core.ret.Result;
import com.paddi.entity.dto.UserMomentsDTO;
import com.paddi.entity.po.UserMoments;
import com.paddi.service.UserMomentsService;
import com.paddi.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.paddi.constants.UserRoleConstants.ROLE_CODE_LV1;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月17日 20:09:01
 */
@RestController
public class UserMomentsController {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;

    @ApiLimitedRole(limitedRoleCodeList = {ROLE_CODE_LV1})
    @DataLimited
    @PostMapping("/user-moments")
    public Result postMoments(@RequestBody UserMomentsDTO userMomentsDTO) {
        Long userId = userSupport.getCurrentUserId();
        userMomentsDTO.setUserId(userId);
        userMomentsService.postMoments(userMomentsDTO);
        return Result.success();
    }

    @GetMapping("/user-subscribed-moments")
    public Result getUserSubscribedMoments() {
        Long userId = userSupport.getCurrentUserId();
        List<UserMoments> userSubscribedMoments = userMomentsService.getUserSubscribedMoments(userId);
        return Result.success(userSubscribedMoments);
    }

}
