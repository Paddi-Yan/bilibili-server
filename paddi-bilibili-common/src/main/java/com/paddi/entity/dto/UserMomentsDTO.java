package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月17日 20:15:29
 */
@Data
public class UserMomentsDTO {
    private Long userId;

    private String type;

    private Long contentId;
}
