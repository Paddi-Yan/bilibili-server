package com.paddi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.paddi.constants.RedisKey;
import com.paddi.entity.dto.PageParam;
import com.paddi.entity.dto.VideoPostDTO;
import com.paddi.entity.po.*;
import com.paddi.entity.vo.PageResult;
import com.paddi.entity.vo.VideoStatisticsDataVO;
import com.paddi.entity.vo.VideoVO;
import com.paddi.enums.VideoOperationType;
import com.paddi.exception.BadRequestException;
import com.paddi.mapper.VideoCollectionGroupMapper;
import com.paddi.mapper.VideoMapper;
import com.paddi.message.VideoOperationMessage;
import com.paddi.redis.RedisCache;
import com.paddi.service.VideoService;
import com.paddi.util.FastDFSUtils;
import com.paddi.util.PageUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.paddi.constants.RocketMQConstants.VIDEO_TOPIC;
import static com.paddi.constants.SystemConstants.BATCH_SIZE;
import static com.paddi.message.VideoOperationMessage.COLLECTION_GROUP_ID;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:47:43
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private FastDFSUtils fastDFSUtils;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private VideoCollectionGroupMapper videoCollectionGroupMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void postVideo(VideoPostDTO videoPostDTO) {
        List<Long> videoTagIds = videoPostDTO.getVideoTagIdList();
        Video video = new Video(videoPostDTO);
        videoMapper.insert(video);
        if(CollectionUtil.isNotEmpty(videoTagIds)) {
            List<VideoTag> videoTags = videoTagIds.parallelStream()
                                                .map(tagId -> VideoTag.builder()
                                                                      .videoId(video.getId())
                                                                      .tagId(tagId)
                                                                      .createTime(LocalDateTime.now())
                                                                      .build())
                                                .collect(Collectors.toList());
            //批量插入
            List<List<VideoTag>> partitions = ListUtil.split(videoTags, BATCH_SIZE);
            partitions.forEach(videoMapper::insertVideoTagsBatch);
        }

    }

    @Override
    public PageResult<VideoVO> pageListVideos(PageParam pageParam, String area) {
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        PageUtils.pageCheck(pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<Video> videoList = videoMapper.selectList(new LambdaQueryWrapper<Video>().eq(Video :: getArea, area));
        PageInfo<Video> pageInfo = new PageInfo<>(videoList);
        List<VideoVO> videoVOList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(videoList)) {
             videoVOList = videoList.parallelStream().map(video -> {
                VideoVO videoVO = new VideoVO(video);
                List<Tag> videoTags = videoMapper.getVideoTags(video.getId());
                videoVO.setVideoTagList(videoTags);
                return videoVO;
            }).collect(Collectors.toList());
        }
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getPages(), videoVOList);
    }

    @Override
    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        fastDFSUtils.viewVideoOnlineBySlices(request, response, url);
    }

    @Override
    public void addVideoLike(Long userId, Long videoId) {
        checkVideo(videoId);
        //检查是否已经点过赞
        VideoLike videoLike = videoMapper.getVideoLikeByVideoIdAndUserId(videoId, userId);
        if(videoLike != null) {
            throw new BadRequestException("该用户已经点赞过此视频!");
        }
        String videoLikeKey = RedisKey.VIDEO_LIKES + videoId;
        if(redisCache.hasKey(videoLikeKey)) {
            redisCache.increment(videoLikeKey);
        }
        //使用消息队列异步落库
        VideoOperationMessage message = VideoOperationMessage.builder()
                                                           .userId(userId)
                                                           .videoId(videoId)
                                                           .type(VideoOperationType.ADD_LIKE)
                                                           .build();
        rocketMQTemplate.convertAndSend(VIDEO_TOPIC, JSON.toJSONString(message));
    }

    @Override
    public void cancelVideoLike(Long videoId, Long userId) {
        checkVideo(videoId);
        String videoLikeKey = RedisKey.VIDEO_LIKES + videoId;
        boolean alreadyLike = videoMapper.getVideoLikeByVideoIdAndUserId(videoId, userId) != null;
        if(!alreadyLike) {
            throw new BadRequestException("该用户未点赞该视频!");
        }
        if(redisCache.hasKey(videoLikeKey)) {
            redisCache.decrement(videoLikeKey);
        }
        //使用消息队列异步删除
        VideoOperationMessage message = VideoOperationMessage.builder()
                                                           .userId(userId)
                                                           .videoId(videoId)
                                                           .type(VideoOperationType.CANCEL_LIKE)
                                                           .build();
        rocketMQTemplate.convertAndSend(VIDEO_TOPIC, JSON.toJSONString(message));
    }

    @Override
    public VideoStatisticsDataVO getVideoLikes(Long videoId, Long userId) {
        checkVideo(videoId);
        String videoLikeKey = RedisKey.VIDEO_LIKES + videoId;
        Boolean liked = videoMapper.getVideoLikeByVideoIdAndUserId(videoId, userId) != null;
        Integer count = 0;
        VideoStatisticsDataVO videoStatisticsDataVO = new VideoStatisticsDataVO();
        videoStatisticsDataVO.setFlag(liked);
        if(redisCache.hasKey(videoLikeKey)) {
            count = redisCache.getCacheObject(videoLikeKey);
            videoStatisticsDataVO.setCount(count);
            return videoStatisticsDataVO;
        }
        count = videoMapper.getVideoLikeCount(videoId);
        redisCache.setCacheObject(videoLikeKey, count, 60, TimeUnit.MINUTES);
        videoStatisticsDataVO.setCount(count);
        return videoStatisticsDataVO;
    }

    @Override
    public void addVideoCollection(Long userId, Long videoId, Long groupId) {
        checkVideo(videoId);
        checkCollectionGroup(groupId);
        String videoCollectionKey = RedisKey.VIDEO_COLLECTIONS + videoId;
        VideoCollection videoCollection = videoMapper.getVideoCollection(userId, videoId, groupId);
        if(videoCollection != null) {
            throw new BadRequestException("用户已收藏该视频!");
        }
        if(redisCache.hasKey(videoCollectionKey)) {
            redisCache.increment(videoCollectionKey);
        }
        VideoOperationMessage message = VideoOperationMessage.builder()
                                                           .userId(userId)
                                                           .videoId(videoId)
                                                           .type(VideoOperationType.ADD_COLLECTION)
                                                           .attachments(ImmutableMap.of(COLLECTION_GROUP_ID, groupId))
                                                           .build();
        rocketMQTemplate.convertAndSend(VIDEO_TOPIC, JSON.toJSONString(message));
    }

    @Override
    public void cancelVideoCollection(Long userId, Long videoId, Long groupId) {
        checkVideo(videoId);
        checkCollectionGroup(groupId);
        String videoCollectionKey = RedisKey.VIDEO_COLLECTIONS + videoId;
        VideoCollection videoCollection = videoMapper.getVideoCollection(userId, videoId, groupId);
        if(videoCollection == null) {
            throw new BadRequestException("用户未收藏该视频!");
        }
        if(redisCache.hasKey(videoCollectionKey)) {
            redisCache.decrement(videoCollectionKey);
        }
        VideoOperationMessage message = VideoOperationMessage.builder()
                                                             .userId(userId)
                                                             .videoId(videoId)
                                                             .type(VideoOperationType.CANCEL_COLLECTION)
                                                             .attachments(ImmutableMap.of(COLLECTION_GROUP_ID, groupId))
                                                             .build();
        rocketMQTemplate.convertAndSend(VIDEO_TOPIC, JSON.toJSONString(message));
    }

    @Override
    public VideoStatisticsDataVO getVideoCollectionCount(Long videoId, Long userId) {
        checkVideo(videoId);
        String videoCollectionKey = RedisKey.VIDEO_COLLECTIONS + videoId;
        Boolean collected = videoMapper.getVideoCollection(userId, videoId, null) != null;
        Integer count = 0;
        VideoStatisticsDataVO videoStatisticsDataVO = new VideoStatisticsDataVO();
        videoStatisticsDataVO.setFlag(collected);
        if(redisCache.hasKey(videoCollectionKey)) {
            count = redisCache.getCacheObject(videoCollectionKey);
            videoStatisticsDataVO.setCount(count);
            return videoStatisticsDataVO;
        }
        count = videoMapper.getVideoCollectionCount(videoId);
        redisCache.setCacheObject(videoCollectionKey, count, 60, TimeUnit.MINUTES);
        videoStatisticsDataVO.setCount(count);
        return videoStatisticsDataVO;
    }

    private void checkCollectionGroup(Long groupId) {
        CollectionGroup collectionGroup = videoCollectionGroupMapper.selectById(groupId);
        if(collectionGroup == null) {
            throw new BadRequestException("收藏分组不存在!");
        }
    }

    private Video checkVideo(Long videoId) {
        Video video = videoMapper.selectById(videoId);
        if(video == null) {
            throw new BadRequestException("视频不存在!");
        }
        return video;
    }

}
