package com.paddi.entity.po;

import lombok.Data;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月26日 01:32:07
 */
@Data
public class RootVideoComment {

    private Long rootId;

    private List<VideoComment> childrenComments;
}
