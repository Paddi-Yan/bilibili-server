package com.paddi.entity.po;

import com.paddi.entity.dto.DanmakuDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月29日 00:54:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Danmaku {
    private Long id;
    private Long userId;
    private Long videoId;
    private String content;
    private String danmakuTime;
    private LocalDateTime createTime;

    public Danmaku(DanmakuDTO danmakuDTO) {
        if(danmakuDTO != null) {
            BeanUtils.copyProperties(danmakuDTO, this);
        }
    }
}
