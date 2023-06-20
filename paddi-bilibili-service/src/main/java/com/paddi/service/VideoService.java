package com.paddi.service;

import com.paddi.entity.dto.PageParam;
import com.paddi.entity.dto.VideoPostDTO;
import com.paddi.entity.vo.PageResult;
import com.paddi.entity.vo.VideoVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:47:39
 */
public interface VideoService {

    void postVideo(VideoPostDTO videoPostDTO);

    PageResult<VideoVO> pageListVideos(PageParam pageParam, String area);

    void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception;

    void addVideoLike(Long userId, Long videoId);

    void cancelVideoLike(Long videoId, Long userId);
}
