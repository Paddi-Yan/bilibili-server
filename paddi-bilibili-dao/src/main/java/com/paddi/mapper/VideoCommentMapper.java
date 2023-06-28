package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.VideoComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月25日 23:45:20
 */
@Mapper
public interface VideoCommentMapper extends BaseMapper<VideoComment> {
    List<VideoComment> pageListVideoRootComments(Long videoId);

    Long insertCommentContent(String comment);

    void updateReplyCommentCount(@Param("id") Long rootId, @Param("value") Integer value, @Param("expect") Integer expect);
}
