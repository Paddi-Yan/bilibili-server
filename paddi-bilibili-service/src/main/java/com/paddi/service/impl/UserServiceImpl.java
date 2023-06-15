package com.paddi.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paddi.constants.UserConstants;
import com.paddi.entity.dto.UserInfoUpdateDTO;
import com.paddi.entity.dto.UserLoginDTO;
import com.paddi.entity.dto.UserRegistryDTO;
import com.paddi.entity.dto.UserUpdateDTO;
import com.paddi.entity.po.User;
import com.paddi.entity.po.UserInfo;
import com.paddi.entity.vo.UserInfoVO;
import com.paddi.entity.vo.UserVO;
import com.paddi.exception.ConditionException;
import com.paddi.mapper.UserInfoMapper;
import com.paddi.mapper.UserMapper;
import com.paddi.service.UserService;
import com.paddi.util.MD5Util;
import com.paddi.util.RSAUtil;
import com.paddi.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 20:10:19
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void registryUser(UserRegistryDTO userRegistryDTO) {
        User userFromDB = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User :: getPhone, userRegistryDTO.getPhone()));
        if(userFromDB != null) {
            throw new ConditionException("该手机号已经注册!");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = userRegistryDTO.getPassword();
        //未经过加密处理的密码
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch(Exception e) {
            throw new ConditionException("密码解密失败!");
        }
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        LocalDateTime createTime = LocalDateTime.now();
        User user = User.builder()
                        .phone(userRegistryDTO.getPhone())
                        .email(userRegistryDTO.getEmail())
                        .password(md5Password)
                        .salt(salt)
                        .createTime(createTime)
                        .build();
        userMapper.insert(user);
        //添加用户信息
        UserInfo userInfo = UserInfo.builder()
                                 .userId(user.getId())
                                 .nick(UserConstants.DEFAULT_NICK)
                                 .birth(UserConstants.DEFAULT_BIRTH)
                                 .gender(UserConstants.GENDER_UNKNOWN)
                                 .createTime(createTime)
                                 .build();
        userInfoMapper.insert(userInfo);

    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        User userFromDB = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, userLoginDTO.getPhone()));
        if(userFromDB == null) {
            throw new ConditionException("当前用户不存在!");
        }
        String password = userLoginDTO.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch(Exception e) {
            throw new ConditionException("密码解密失败!");
        }
        String salt = userFromDB.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if(!md5Password.equals(userFromDB.getPassword())) {
            throw new ConditionException("密码错误!");
        }
        String token = TokenUtil.generateToken(userFromDB.getId());
        return token;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User :: getId, userId));
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo :: getUserId, userId));
        UserVO userVO = new UserVO(user);
        UserInfoVO userInfoVO = new UserInfoVO(userInfo);
        userVO.setUserInfoVO(userInfoVO);
        return userVO;
    }

    @Override
    public void updateUser(Long userId, UserUpdateDTO userUpdateDTO) throws Exception {
        User userFromDB = userMapper.selectById(userId);
        if(userFromDB == null) {
            throw new ConditionException("当前用户不存在!");
        }
        User user = new User(userUpdateDTO);
        if(StrUtil.isNotBlank(userUpdateDTO.getPassword())) {
            String rawPassword = RSAUtil.decrypt(userUpdateDTO.getPassword());
            String md5Password = MD5Util.sign(rawPassword, userFromDB.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        user.setUpdateTime(LocalDateTime.now());
        user.setId(userId);
        userMapper.updateUser(user);
    }

    @Override
    public void updateUserInfo(Long userId, UserInfoUpdateDTO userInfoUpdateDTO) {
        UserInfo userInfo = new UserInfo(userInfoUpdateDTO);
        userInfo.setUserId(userId);
        userInfo.setUpdateTime(LocalDateTime.now());
        userInfoMapper.updateUserInfo(userInfo);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }
}
