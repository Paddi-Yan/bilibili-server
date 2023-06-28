package com.paddi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月17日 20:15:29
 */
@Data
public class UserMomentsDTO {
    private Long userId;

    private String type;

    @NotBlank(message = "动态内容不能为空")
    private Long contentId;
}
