package com.paddi.entity.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月26日 11:10:32
 */
@Data
@Builder
public class VideoDetailsVO {

    /**
     * 视频详情
     */
    private VideoVO videoVO;

    /**
     * 点赞
     */
    private VideoStatisticsDataVO likes;

    /**
     * 投币
     */
    private VideoStatisticsDataVO coins;

    /**
     * 收藏
     */
    private VideoStatisticsDataVO collections;

    /**
     * 评论
     */
    private PageResult<VideoCommentVO> videoComments;

    /**
     * Up主信息,其中包含是否关注以及发布视频数量
     */
    private UserInfoVO userInfoVO;
}
