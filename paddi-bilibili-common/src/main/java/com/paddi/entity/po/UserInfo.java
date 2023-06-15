package com.paddi.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.paddi.entity.dto.UserInfoUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 20:08:39
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Long id;

    private Long userId;

    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Boolean followed;

    public UserInfo(UserInfoUpdateDTO userInfoUpdateDTO) {
        if(userInfoUpdateDTO != null) {
            BeanUtils.copyProperties(userInfoUpdateDTO, this);
        }
    }
}
