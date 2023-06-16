package com.paddi.entity.vo;

import com.paddi.entity.po.UserFollowing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 15:06:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFollowingVO {
    private Long userId;

    private Long followingId;

    private Long groupId;

    private UserInfoVO userInfoVO;

    public UserFollowingVO(UserFollowing userFollowing) {
        if(userFollowing != null) {
            BeanUtils.copyProperties(userFollowing, this);
        }
    }
}
