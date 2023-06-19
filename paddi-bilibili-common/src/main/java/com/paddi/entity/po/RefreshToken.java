package com.paddi.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 11:39:58
 */
@Data
public class RefreshToken {
    private Long userId;

    private String refreshToken;

    private LocalDateTime createTime;
}
