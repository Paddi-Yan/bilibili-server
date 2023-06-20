package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.Tag;
import com.paddi.entity.po.Video;
import com.paddi.entity.po.VideoLike;
import com.paddi.entity.po.VideoTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:48:02
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    void insertVideoTagsBatch(List<VideoTag> videoTags);

    List<Tag> getVideoTags(Long videoId);

    VideoLike getVideoLikeByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);
}
