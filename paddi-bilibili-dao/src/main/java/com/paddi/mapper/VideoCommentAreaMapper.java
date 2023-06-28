package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.VideoCommentArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月27日 16:06:36
 */
@Mapper
public interface VideoCommentAreaMapper extends BaseMapper<VideoCommentArea> {
    void increaseCommentCount(@Param("id") Long areaId, @Param("count") Integer count);
}
