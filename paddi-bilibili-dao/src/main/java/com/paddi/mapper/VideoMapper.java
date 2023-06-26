package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.*;
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

    Integer getVideoLikeCount(Long videoId);

    VideoCollection getVideoCollection(@Param("userId") Long userId, @Param("videoId") Long videoId, @Param("groupId")Long groupId);

    void deleteVideoCollection(@Param("userId") Long userId, @Param("videoId") Long videoId, @Param("groupId")Long groupId);

    Integer getVideoCollectionCount(Long videoId);

    VideoCoin getVideoCoinsByVideoIdAndUserId(Long videoId, Long userId);

    List<Long> getVideoLikedUserIdList(Long videoId);

    List<VideoCollection> getUserVideoCollections(Long userId, Long videoId);

    List<Long> getVideoCollectedUserIdList(Long videoId);

    List<RootVideoComment> getVideoCommentsGroupByRootIds(@Param("list") List<Long> parentIdList);
}
