package com.paddi.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:30:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class File {
    private Long id;

    private String url;

    private String type;

    private String md5;

    private LocalDateTime createTime;
}
