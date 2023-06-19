package com.paddi.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 10:57:51
 */
@Data
@AllArgsConstructor
public class UserLoginVo {
    private String accessToken;
    private String refreshToken;
}
