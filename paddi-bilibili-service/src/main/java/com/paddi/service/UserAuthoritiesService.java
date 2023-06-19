package com.paddi.service;

import com.paddi.entity.auth.UserAuthorities;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 00:28:03
 */
public interface UserAuthoritiesService {
    UserAuthorities getUserAuthorities(Long userId);

    void addUserDefaultRole(Long userId);
}
