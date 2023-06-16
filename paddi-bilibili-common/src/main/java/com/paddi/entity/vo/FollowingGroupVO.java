package com.paddi.entity.vo;

import com.paddi.entity.po.FollowingGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月16日 11:40:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowingGroupVO {

    private Long userid;

    private String name;

    private String type;

    private List<UserInfoVO> followingUserInfoList;

    public FollowingGroupVO(FollowingGroup followingGroup, List<UserInfoVO> followingUserInfoList) {
        if(followingGroup != null) {
            BeanUtils.copyProperties(followingGroup, this);
        }
        this.followingUserInfoList = followingUserInfoList;
    }
}
