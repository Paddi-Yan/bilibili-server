package com.paddi.aspect;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 09:39:33
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Component
public @interface ApiLimitedRole {
    String[] limitedRoleCodeList() default {};
}
