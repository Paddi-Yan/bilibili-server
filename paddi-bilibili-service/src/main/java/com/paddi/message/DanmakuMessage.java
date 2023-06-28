package com.paddi.message;

import com.paddi.entity.dto.DanmakuDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月28日 22:41:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DanmakuMessage {
    private String sessionId;

    private DanmakuDTO danmakuDTO;
}
