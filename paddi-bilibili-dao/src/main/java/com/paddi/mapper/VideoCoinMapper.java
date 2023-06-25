package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.VideoCoin;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月25日 09:44:54
 */
@Mapper
public interface VideoCoinMapper extends BaseMapper<VideoCoin> {
    VideoCoin getVideoCoinByVideoIdAndUserId(Long videoId, Long userId);

    Integer getVideoCoins(Long videoId);
}
