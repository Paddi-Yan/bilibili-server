package com.paddi.entity.po;

import com.paddi.entity.dto.UserMomentsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月17日 20:10:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMoments implements Serializable {
    private static final long serialVersionUID = 8231305242567196334L;

    private Long id;

    private Long userId;

    private String type;

    private Long contentId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public UserMoments(UserMomentsDTO userMomentsDTO) {
        if(userMomentsDTO != null) {
            BeanUtils.copyProperties(userMomentsDTO, this);
        }
    }
}
