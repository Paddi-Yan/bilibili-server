package com.paddi.support;

import com.paddi.util.ServletUtils;
import com.paddi.util.TokenUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static com.paddi.constants.SystemConstants.TOKEN_REQUEST_HEAD;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 23:00:18
 */
@Component
public class UserSupport {

    public Long getCurrentUserId() {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = request.getHeader(TOKEN_REQUEST_HEAD);
        Long userId = TokenUtil.verifyToken(token);
        return userId;
    }
}
