package com.paddi.support;

import com.paddi.constants.RedisKey;
import com.paddi.exception.AuthorizationException;
import com.paddi.util.ServletUtils;
import com.paddi.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.paddi.constants.SystemConstants.TOKEN_REQUEST_HEAD;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 23:00:18
 */
@Component
public class UserSupport {

    @Autowired
    private RedisTemplate redisTemplate;

    public Long getCurrentUserId() {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = request.getHeader(TOKEN_REQUEST_HEAD);
        Long userId = TokenUtil.verifyToken(token);
        //判断accessToken是否存在于黑名单
        Boolean isTokenInBlackList = redisTemplate.opsForHash().hasKey(RedisKey.ACCESS_TOKEN_BLACKLIST, token);
        if(isTokenInBlackList) {
            //判断accessToken是否已经过期
            Date tokenExpireTime = (Date) redisTemplate.opsForHash().get(RedisKey.ACCESS_TOKEN_BLACKLIST, token);
            if(tokenExpireTime.before(new Date())) {
                //accessToken过期后从黑名单中删除
                //不需要再维护因为accessToken已经无法成功解析了
                redisTemplate.opsForHash().delete(RedisKey.ACCESS_TOKEN_BLACKLIST, token);
            }
            throw new AuthorizationException("该授权令牌已失效!");
        }
        return userId;
    }
}
