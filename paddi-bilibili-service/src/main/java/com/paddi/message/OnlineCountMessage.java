package com.paddi.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月29日 00:08:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineCountMessage {

    private String message;

    private String sessionId;
}
