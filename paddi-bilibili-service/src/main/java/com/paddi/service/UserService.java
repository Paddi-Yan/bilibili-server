package com.paddi.service;

import com.paddi.entity.dto.*;
import com.paddi.entity.po.User;
import com.paddi.entity.po.UserInfo;
import com.paddi.entity.vo.PageResult;
import com.paddi.entity.vo.UserInfoVO;
import com.paddi.entity.vo.UserLoginVo;
import com.paddi.entity.vo.UserVO;

import java.util.List;
import java.util.Set;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 20:09:54
 */
public interface UserService {
    void registryUser(UserRegistryDTO userRegistryDTO);

    String login(UserLoginDTO userLoginDTO) throws Exception;

    UserVO getUserInfo(Long userId);

    void updateUser(Long userId, UserUpdateDTO userUpdateDTO) throws Exception;

    void updateUserInfo(Long userId, UserInfoUpdateDTO userInfoUpdateDTO);

    User getUserById(Long userId);

    List<UserInfo> getUserByUserIds(Set<Long> userFollowingIds);

    PageResult<UserInfoVO> getUserInfoPageResult(UserInfoPageQueryDTO pageQueryDTO, Long userId);


    UserLoginVo loginForDoubleTokens(UserLoginDTO userLoginDTO) throws Exception;

    void logout(Long userId, String refreshToken, String accessToken);

    UserLoginVo refreshToken(String refreshToken) throws Exception;
}
