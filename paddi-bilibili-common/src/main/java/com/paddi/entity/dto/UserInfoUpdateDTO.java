package com.paddi.entity.dto;

import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:19:49
 */
@Data
public class UserInfoUpdateDTO {

    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;

}
