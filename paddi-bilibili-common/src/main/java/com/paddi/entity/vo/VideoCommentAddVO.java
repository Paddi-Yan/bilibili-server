package com.paddi.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月27日 16:51:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCommentAddVO {

    private String comment;

    private LocalDateTime createTime;
}
