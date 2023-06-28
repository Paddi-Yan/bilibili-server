package com.paddi.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.paddi.entity.dto.VideoPostDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:48:17
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Video {
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

    private Long commentAreaId;

    /**
     *标签列表
     */
    @TableField(exist = false)
    private List<VideoTag> videoTagList;

    /**
     * 简介
     */
    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Video(VideoPostDTO videoPostDTO) {
        if(videoPostDTO != null) {
            BeanUtils.copyProperties(videoPostDTO, this);
        }
    }
}
