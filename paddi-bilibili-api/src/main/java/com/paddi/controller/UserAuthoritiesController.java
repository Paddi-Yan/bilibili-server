package com.paddi.controller;

import com.paddi.core.ret.Result;
import com.paddi.entity.auth.UserAuthorities;
import com.paddi.service.UserAuthoritiesService;
import com.paddi.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:27:21
 */
@RestController
public class UserAuthoritiesController {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthoritiesService userAuthoritiesService;

    @GetMapping("/user-authorities")
    public Result getUserAuthorities() {
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthoritiesService.getUserAuthorities(userId);
        return Result.success(userAuthorities);
    }
}
