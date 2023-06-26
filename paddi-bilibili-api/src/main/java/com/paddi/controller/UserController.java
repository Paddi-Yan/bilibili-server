package com.paddi.controller;

import com.paddi.constants.SystemConstants;
import com.paddi.core.ret.Result;
import com.paddi.entity.dto.*;
import com.paddi.entity.vo.PageResult;
import com.paddi.entity.vo.UserInfoVO;
import com.paddi.entity.vo.UserLoginVo;
import com.paddi.entity.vo.UserVO;
import com.paddi.service.UserService;
import com.paddi.support.UserSupport;
import com.paddi.util.RSAUtil;
import com.paddi.util.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 20:10:36
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @GetMapping("/users")
    public Result<UserVO> getUserInfo() {
        Long userId = userSupport.getCurrentUserId();
        return Result.success(userService.getUserInfo(userId));
    }

    @GetMapping("/other-users")
    public Result getUserInfo(@RequestParam Long queryUserId) {
        //TODO 查询其他人的用户信息
        Long userId = userSupport.getCurrentUserId();
        UserInfoVO userInfo = userService.getUserInfo(userId, queryUserId);
        return Result.success(userInfo);
    }


    @GetMapping("/rsa-public-keys")
    public Result<String> getRsaPublicKey() {
        return Result.success(RSAUtil.getPublicKeyStr());
    }

    @PostMapping("/users")
    public Result<String> registryUser(@RequestBody @Valid UserRegistryDTO userRegistryDTO) {
        userService.registryUser(userRegistryDTO);
        return Result.success();
    }

    @PostMapping("/user-tokens")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO) throws Exception {
        String token = userService.login(userLoginDTO);
        return Result.success(token);
    }

    @PutMapping("/user")
    public Result<String> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userService.updateUser(userId, userUpdateDTO);
        return Result.success();
    }

    @PutMapping("/user-infos")
    public Result<String> updateUserInfo(@RequestBody UserInfoUpdateDTO userInfoUpdateDTO) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userService.updateUserInfo(userId, userInfoUpdateDTO);
        return Result.success();
    }

    @GetMapping("/user-infos")
    public Result<PageResult<UserInfoVO>> getUserInfoPageResult(@RequestBody UserInfoPageQueryDTO pageQueryDTO) {
        Long userId = userSupport.getCurrentUserId();
        return Result.success(userService.getUserInfoPageResult(pageQueryDTO, userId));
    }

    @PostMapping("/user-double-tokens")
    public Result loginForDoubleTokens(@RequestBody UserLoginDTO userLoginDTO) throws Exception {
        UserLoginVo userLoginVo = userService.loginForDoubleTokens(userLoginDTO);
        return Result.success(userLoginVo);
    }

    @DeleteMapping("/user-tokens")
    public Result logout() {
        String accessToken = ServletUtils.getRequest().getHeader(SystemConstants.TOKEN_REQUEST_HEAD);
        String refreshToken = ServletUtils.getRequest().getHeader(SystemConstants.REFRESH_TOKEN_REQUEST_HEAD);
        Long userId = userSupport.getCurrentUserId();
        userService.logout(userId, refreshToken, accessToken);
        return Result.success();
    }

    @PostMapping("/access-tokens")
    public Result refreshAccessToken() throws Exception {
        String refreshToken = ServletUtils.getRequest().getHeader(SystemConstants.REFRESH_TOKEN_REQUEST_HEAD);
        UserLoginVo userLoginVo = userService.refreshToken(refreshToken);
        return Result.success(userLoginVo);
    }
}
