package com.paddi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
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
    @NotBlank(message = "视频地址不能为空")
    private String url;

    /**
     * 封面
     */
    private String thumbnail;

    /**
     * 标题
     */
    @NotBlank(message = "视频标题不能为空")
    private String title;

    /**
     *  0自制 1转载
     */
    private String type;

    /**
     * 时长
     */
    @NotBlank(message = "视频时长不能为空")
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
    @NotBlank(message = "视频简介不能为空")
    private String description;
}
