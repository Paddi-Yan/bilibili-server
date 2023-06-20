package com.paddi.controller;

import com.paddi.core.ret.Result;
import com.paddi.entity.dto.VideoPageListDTO;
import com.paddi.entity.dto.VideoPostDTO;
import com.paddi.entity.vo.PageResult;
import com.paddi.entity.vo.VideoVO;
import com.paddi.service.VideoService;
import com.paddi.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:47:12
 */
@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserSupport userSupport;

    @PostMapping("/videos")
    public Result postVideo(@RequestBody VideoPostDTO videoPostDTO) {
        Long userId = userSupport.getCurrentUserId();
        videoPostDTO.setUserId(userId);
        videoService.postVideo(videoPostDTO);
        return Result.success();
    }


    @GetMapping("/videos")
    public Result pageListVideos(@RequestBody VideoPageListDTO videoPageListDTO) {
        PageResult<VideoVO> result = videoService.pageListVideos(videoPageListDTO.getPageParam(), videoPageListDTO.getArea());
        return Result.success(result);
    }

    @GetMapping("/video-slices")
    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        videoService.viewVideoOnlineBySlices(request, response, url);
    }

    @PostMapping("/video-likes")
    public Result addVideoLike(@RequestParam Long videoId) throws Exception{
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoLike(userId, videoId);
        return Result.success();
    }


    @DeleteMapping("/video-likes")
    public Result cancelVideoLike(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserId();
        videoService.cancelVideoLike(videoId, userId);
        return Result.success();
    }
}
