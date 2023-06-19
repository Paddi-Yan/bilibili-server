package com.paddi.aspect;

import com.paddi.entity.auth.UserRole;
import com.paddi.exception.AuthorizationException;
import com.paddi.service.UserRoleService;
import com.paddi.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 09:42:23
 */
@Aspect
@Order(1)
@Component
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.paddi.aspect.ApiLimitedRole)")
    public void check() {

    }

    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole) {
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(userId);
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        Set<String> userRoleCodeSet = userRoles.stream().map(UserRole :: getRoleCode).collect(Collectors.toSet());
        userRoleCodeSet.retainAll(limitedRoleCodeSet);
        if(userRoleCodeSet.size() > 0) {
            throw new AuthorizationException("接口调用权限不足!");
        }
    }
}
