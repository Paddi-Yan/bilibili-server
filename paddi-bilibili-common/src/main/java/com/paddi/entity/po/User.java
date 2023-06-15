package com.paddi.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.paddi.entity.dto.UserUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 20:06:18
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;

    private String phone;

    private String email;

    private String password;

    private String salt;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private UserInfo userInfo;

    public User(UserUpdateDTO userUpdateDTO) {
        if(userUpdateDTO != null) {
            BeanUtils.copyProperties(userUpdateDTO, this);
        }
    }
}
