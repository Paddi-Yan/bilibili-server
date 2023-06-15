package com.paddi.entity.vo;

import com.paddi.entity.po.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 23:18:18
 */
@Data
@NoArgsConstructor
public class UserVO {

    private Long id;

    private String phone;

    private String email;

    private UserInfoVO userInfoVO;

    public UserVO(User user) {
        if(user != null) {
            BeanUtils.copyProperties(user, this);
        }
    }
}
