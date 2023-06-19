package com.paddi.aspect;

import com.paddi.entity.auth.UserRole;
import com.paddi.entity.po.UserMoments;
import com.paddi.enums.MomentsType;
import com.paddi.exception.AuthorizationException;
import com.paddi.service.UserRoleService;
import com.paddi.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.paddi.constants.UserRoleConstants.ROLE_CODE_LV1;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 10:27:24
 */
@Aspect
public class DataLimitedAspect {
    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.paddi.aspect.DataLimited)")
    public void check() {

    }

    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(userId);
        Set<String> userRoleSet = userRoles.stream().map(UserRole :: getRoleCode).collect(Collectors.toSet());
        for(Object arg : joinPoint.getArgs()) {
            if(arg instanceof UserMoments) {
                UserMoments userMoments = (UserMoments) arg;
                if(userRoleSet.contains(ROLE_CODE_LV1) && !MomentsType.VIDEO.getValue().equals(userMoments.getType())) {
                    throw new AuthorizationException("发布视频接口权限不足!");
                }
            }
        }
    }
}
