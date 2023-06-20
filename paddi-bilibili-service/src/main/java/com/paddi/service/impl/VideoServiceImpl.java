package com.paddi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paddi.constants.RedisKey;
import com.paddi.constants.RocketMQConstants;
import com.paddi.entity.dto.PageParam;
import com.paddi.entity.dto.VideoPostDTO;
import com.paddi.entity.po.Tag;
import com.paddi.entity.po.Video;
import com.paddi.entity.po.VideoLike;
import com.paddi.entity.po.VideoTag;
import com.paddi.entity.vo.PageResult;
import com.paddi.entity.vo.VideoVO;
import com.paddi.enums.VideoOperationType;
import com.paddi.exception.BadRequestException;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.paddi.constants.SystemConstants.BATCH_SIZE;

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
        String videoLikeKey = RedisKey.VIDEO_LIKES + videoId;
        Set<Long> videoLikesUserSet = redisCache.getCacheSet(videoLikeKey);
        if(CollectionUtil.isNotEmpty(videoLikesUserSet)) {
            //缓存中存在Video的点赞用户集合
            if(videoLikesUserSet.contains(userId)) {
                throw new BadRequestException("该用户已经点赞过此视频!");
            }
            redisCache.addItemToCacheSet(videoLikeKey, userId);
        }else {
            //缓存中不存在Video的点赞用户集合
            VideoLike videoLike = videoMapper.getVideoLikeByVideoIdAndUserId(videoId, userId);
            if(videoLike != null) {
                throw new BadRequestException("该用户已经点赞过此视频!");
            }
        }
        //使用消息队列异步落库
        VideoOperationMessage message = VideoOperationMessage.builder()
                                                           .userId(userId)
                                                           .videoId(videoId)
                                                           .type(VideoOperationType.ADD_LIKE)
                                                           .build();
        rocketMQTemplate.convertAndSend(RocketMQConstants.VIDEO_TOPIC, JSON.toJSONString(message));
    }

    @Override
    public void cancelVideoLike(Long videoId, Long userId) {
        checkVideo(videoId);

    }

    private Video checkVideo(Long videoId) {
        Video video = videoMapper.selectById(videoId);
        if(video == null) {
            throw new BadRequestException("视频不存在!");
        }
        return video;
    }

}
