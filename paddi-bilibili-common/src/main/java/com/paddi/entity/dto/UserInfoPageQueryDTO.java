package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 16:57:17
 */
@Data
public class UserInfoPageQueryDTO {

    private String nickname;

    private PageParam pageParam;
}
