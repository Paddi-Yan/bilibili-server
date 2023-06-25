package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.po.CollectionGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月21日 11:05:38
 */
@Mapper
public interface VideoCollectionGroupMapper extends BaseMapper<CollectionGroup> {
}
