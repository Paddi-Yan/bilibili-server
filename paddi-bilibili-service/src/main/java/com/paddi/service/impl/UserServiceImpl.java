package com.paddi.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paddi.constants.HttpStatus;
import com.paddi.constants.RedisKey;
import com.paddi.constants.UserConstants;
import com.paddi.entity.dto.*;
import com.paddi.entity.po.RefreshToken;
import com.paddi.entity.po.User;
import com.paddi.entity.po.UserFollowing;
import com.paddi.entity.po.UserInfo;
import com.paddi.entity.vo.PageResult;
import com.paddi.entity.vo.UserInfoVO;
import com.paddi.entity.vo.UserLoginVo;
import com.paddi.entity.vo.UserVO;
import com.paddi.exception.ConditionException;
import com.paddi.mapper.RefreshTokenMapper;
import com.paddi.mapper.UserFollowingMapper;
import com.paddi.mapper.UserInfoMapper;
import com.paddi.mapper.UserMapper;
import com.paddi.service.UserAuthoritiesService;
import com.paddi.service.UserService;
import com.paddi.util.MD5Util;
import com.paddi.util.PageUtils;
import com.paddi.util.RSAUtil;
import com.paddi.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private UserFollowingMapper userFollowingMapper;

    @Autowired
    private UserAuthoritiesService userAuthoritiesService;

    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    @Autowired
    private RedisTemplate redisTemplate;

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

        //为用户添加默认的权限和角色
        userAuthoritiesService.addUserDefaultRole(user.getId());
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

    @Override
    public List<UserInfo> getUserByUserIds(Set<Long> userFollowingIds) {
        return userInfoMapper.selectList(new LambdaQueryWrapper<UserInfo>().in(UserInfo :: getUserId, userFollowingIds));
    }

    @Override
    public PageResult<UserInfoVO> getUserInfoPageResult(UserInfoPageQueryDTO pageQueryDTO, Long userId) {
        PageParam pageParam = pageQueryDTO.getPageParam();
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        PageUtils.pageCheck(pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<UserInfo> userInfoList = userInfoMapper.selectList(new LambdaQueryWrapper<UserInfo>().like(UserInfo :: getNick, pageQueryDTO.getNickname() + "%"));
        PageInfo<UserInfo> pageInfo = new PageInfo<>(userInfoList);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getPages(), buildAndFillFollowingStatus(userInfoList, userId));
    }

    @Override
    public UserLoginVo loginForDoubleTokens(UserLoginDTO userLoginDTO) throws Exception {
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
        Long userId = userFromDB.getId();
        String accessToken = TokenUtil.generateToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        //保存refreshToken到数据库
        userMapper.deleteRefreshToken(refreshToken, userId);
        userMapper.insertRefreshToken(refreshToken, userId, LocalDateTime.now());
        UserLoginVo userLoginVo = new UserLoginVo(accessToken, refreshToken);
        return userLoginVo;
    }

    @Override
    public void logout(Long userId, String refreshToken, String accessToken) {
        userMapper.deleteRefreshToken(refreshToken, userId);
        //将accessToken加入黑名单
        try {
            Date expireTime = TokenUtil.getExpireTime(accessToken);
            if(expireTime.after(new Date())) {
                redisTemplate.opsForHash().put(RedisKey.ACCESS_TOKEN_BLACKLIST, accessToken, expireTime);
            }
        } catch(Exception e) {
            //不作处理直接返回即可
        }
    }

    @Override
    public UserLoginVo refreshToken(String refreshToken) throws Exception {
        RefreshToken refreshTokenDetail = refreshTokenMapper.getRefreshTokenDetail(refreshToken);
        if(refreshTokenDetail == null || TokenUtil.verifyToken(refreshToken) == null) {
            throw new ConditionException(HttpStatus.FORBIDDEN, "refreshToken不存在或过期,刷新失败!");
        }
        Long userId = refreshTokenDetail.getUserId();
        return new UserLoginVo(TokenUtil.generateToken(userId), refreshToken);
    }

    /**
     * 构建以及填充关注状态
     * 如果查询到的用户是已关注的需要标识
     * @param userInfoList
     * @param userId
     * @return
     */
    private List<UserInfoVO> buildAndFillFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        //获取已关注的用户列表
        List<UserFollowing> userFollowings = userFollowingMapper.selectList(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing :: getUserId, userId));
        Set<Long> followedUserIds = userFollowings.parallelStream()
                                          .map(UserFollowing :: getFollowingId)
                                          .collect(Collectors.toSet());
        List<UserInfoVO> userInfoVOList = new ArrayList<>(userFollowings.size());
        for(UserInfo userInfo : userInfoList) {
            UserInfoVO userInfoVO = new UserInfoVO(userInfo);
            if(followedUserIds.contains(userInfo.getUserId())) {
                userInfoVO.setFollowed(true);
            }
            userInfoVOList.add(userInfoVO);
        }
        return userInfoVOList;
    }


}
