package com.paddi.controller;

import com.paddi.core.ret.Result;
import com.paddi.entity.dto.UserInfoUpdateDTO;
import com.paddi.entity.dto.UserLoginDTO;
import com.paddi.entity.dto.UserRegistryDTO;
import com.paddi.entity.dto.UserUpdateDTO;
import com.paddi.entity.vo.UserVO;
import com.paddi.service.UserService;
import com.paddi.support.UserSupport;
import com.paddi.util.RSAUtil;
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
}
