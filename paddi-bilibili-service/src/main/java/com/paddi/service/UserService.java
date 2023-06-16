package com.paddi.service;

import com.paddi.entity.dto.UserInfoUpdateDTO;
import com.paddi.entity.dto.UserLoginDTO;
import com.paddi.entity.dto.UserRegistryDTO;
import com.paddi.entity.dto.UserUpdateDTO;
import com.paddi.entity.po.User;
import com.paddi.entity.po.UserInfo;
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
}
