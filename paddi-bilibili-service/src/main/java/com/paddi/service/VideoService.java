package com.paddi.service;

import com.paddi.entity.dto.*;
import com.paddi.entity.vo.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:47:39
 */
public interface VideoService {

    void postVideo(VideoPostDTO videoPostDTO);

    PageResult<VideoVO> pageListVideos(PageParam pageParam, String area);

    VideoVO getVideoInfo(Long videoId);

    void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception;

    void addVideoLike(Long userId, Long videoId);

    void cancelVideoLike(Long videoId, Long userId);

    VideoStatisticsDataVO getVideoLikes(Long videoId, Long userId);

    void addVideoCollection(Long userId, Long videoId, Long groupId);

    void cancelVideoCollection(Long userId, Long videoId, Long groupId);

    VideoStatisticsDataVO getVideoCollectionCount(Long videoId, Long userId);

    void addVideoCoins(Long userId, Long videoId, Integer amount);

    VideoStatisticsDataVO getVideoCoins(Long videoId, Long userId);

    VideoCommentAddVO addVideoComment(Long userId, VideoCommentAddDTO videoCommentAddDTO);

    PageResult<VideoCommentVO> pageListVideoComments(Long userId, VideoCommentPageListDTO videoCommentPageListDTO,
                                                     Integer sortType);

    void addVideoCommentLike(Long userId, VideoCommentLikeDTO videoCommentLikeDTO);

    VideoStatisticsDataVO getVideoCommentLike(Long userId, Long commentId);

    Long getVideoCommentLike(Long commentId);

    VideoDetailsVO getVideoDetails(Long userId, Long videoId) throws ExecutionException, InterruptedException;
}
