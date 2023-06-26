package com.paddi.controller;

import com.paddi.core.ret.Result;
import com.paddi.entity.dto.*;
import com.paddi.entity.vo.*;
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

    @GetMapping("/video-likes")
    public Result getVideoLikes(@RequestParam Long videoId){
        Long userId = null;
        try{
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored){}
        VideoStatisticsDataVO result = videoService.getVideoLikes(videoId, userId);
        return Result.success(result);
    }

    @PostMapping("/video-collections")
    public Result addVideoCollection(@RequestBody VideoCollectionDTO videoCollectionDTO) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCollection(userId, videoCollectionDTO.getVideoId(), videoCollectionDTO.getGroupId());
        return Result.success();
    }

    @DeleteMapping("/video-collections")
    public Result cancelVideoCollection(@RequestBody VideoCollectionDTO videoCollectionDTO){
        Long userId = userSupport.getCurrentUserId();
        videoService.cancelVideoCollection(userId, videoCollectionDTO.getVideoId(), videoCollectionDTO.getGroupId());
        return Result.success();
    }

    @GetMapping("/video-collections")
    public Result getVideoCollectionCount(@RequestParam Long videoId) {
        Long userId = null;
        try{
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored){}
        VideoStatisticsDataVO videoCollectionCount = videoService.getVideoCollectionCount(videoId, userId);
        return Result.success(videoCollectionCount);
    }

    @PostMapping("/video-coins")
    public Result addVideoCoins(@RequestBody VideoCoinsDTO videoCoinsDTO) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCoins(userId, videoCoinsDTO.getVideoId(), videoCoinsDTO.getAmount());
        return Result.success();
    }

    @GetMapping("/video-coins")
    public Result getVideoCoins(@RequestParam Long videoId) {
        Long userId = null;
        try{
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored){}
        VideoStatisticsDataVO videoCoins = videoService.getVideoCoins(videoId, userId);
        return Result.success(videoCoins);
    }

    @PostMapping("/video-comments")
    public Result addVideoComments(@RequestBody VideoCommentAddDTO videoCommentAddDTO) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoComment(userId, videoCommentAddDTO);
        return Result.success();
    }

    @GetMapping("/video-comments")
    public Result pageListVideoComments(@RequestBody VideoCommentPageListDTO videoCommentPageListDTO, @RequestParam Integer sortType) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch(Exception ignored) { }
        PageResult<VideoCommentVO> pageResult = videoService.pageListVideoComments(userId, videoCommentPageListDTO, sortType);
        return Result.success(pageResult);
    }

    @PostMapping("/video-comment-likes")
    public Result addVideoCommentLiked(@RequestBody VideoCommentLikeDTO videoCommentLikeDTO) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCommentLike(userId, videoCommentLikeDTO);
        return Result.success();
    }

    @GetMapping("/video-comment-likes")
    public Result getVideoCommentLiked(@RequestParam Long commentId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch(Exception ignored) { }
        VideoStatisticsDataVO videoCommentLike = videoService.getVideoCommentLike(userId, commentId);
        return Result.success(videoCommentLike);
    }


    @GetMapping("/video-details")
    public Result getVideoDetails(@RequestParam Long videoId) throws Exception {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch(Exception ignored) { }
        VideoDetailsVO videoDetails = videoService.getVideoDetails(userId, videoId);
        return Result.success(videoDetails);
    }


}
