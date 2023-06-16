package com.paddi.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.paddi.entity.dto.UserFollowingDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 00:37:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFollowing {
    private Long id;

    private Long userId;

    private Long followingId;

    private Long groupId;

    private LocalDateTime createTime;

    @TableField(exist = false)
    private UserInfo userInfo;

    public UserFollowing(UserFollowingDTO userFollowingDTO) {
        if(userFollowingDTO != null) {
            BeanUtils.copyProperties(userFollowingDTO, this);
        }
    }
}
