package com.paddi.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:49:43
 */
@Data
public class VideoPostDTO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 视频链接
     */
    private String url;

    /**
     * 封面
     */
    private String thumbnail;

    /**
     * 标题
     */
    private String title;

    /**
     *  0自制 1转载
     */
    private String type;

    /**
     * 时长
     */
    private String duration;

    /**
     * 分区 0鬼畜 1音乐 2电影
     */
    private String area;

    /**
     *标签列表
     */
    private List<Long> videoTagIdList;

    /**
     * 简介
     */
    private String description;
}
