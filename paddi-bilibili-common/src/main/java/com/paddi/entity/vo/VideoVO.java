package com.paddi.entity.vo;

import com.paddi.entity.po.Tag;
import com.paddi.entity.po.Video;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 09:59:19
 */
@Data
public class VideoVO {
    private Long id;

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
    private List<Tag> videoTagList;

    /**
     * 简介
     */
    private String description;

    public VideoVO(Video video) {
        if(video != null) {
            BeanUtils.copyProperties(video, this);
        }
    }
}
