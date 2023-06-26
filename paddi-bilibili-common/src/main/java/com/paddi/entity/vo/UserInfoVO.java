package com.paddi.entity.vo;

import com.paddi.entity.po.UserInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 23:14:22
 */
@Data
@NoArgsConstructor
public class UserInfoVO {
    private Long id;

    private Long userId;

    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;

    private Boolean followed;

    private Integer postVideoCount;

    public UserInfoVO(UserInfo userInfo) {
        if(userInfo != null) {
            BeanUtils.copyProperties(userInfo, this);
        }
    }
}
