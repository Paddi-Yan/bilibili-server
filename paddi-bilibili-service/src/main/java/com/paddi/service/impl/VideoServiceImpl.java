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
import com.paddi.entity.dto.VideoCommentAddDTO;
import com.paddi.entity.dto.VideoCommentPageListDTO;
import com.paddi.entity.dto.VideoPostDTO;
import com.paddi.entity.po.*;
import com.paddi.entity.vo.PageResult;
import com.paddi.entity.vo.VideoCommentVO;
import com.paddi.entity.vo.VideoStatisticsDataVO;
import com.paddi.entity.vo.VideoVO;
import com.paddi.enums.VideoOperationType;
import com.paddi.enums.VideoType;
import com.paddi.exception.BadRequestException;
import com.paddi.mapper.VideoCoinMapper;
import com.paddi.mapper.VideoCollectionGroupMapper;
import com.paddi.mapper.VideoCommentMapper;
import com.paddi.mapper.VideoMapper;
import com.paddi.message.VideoOperationMessage;
import com.paddi.redis.RedisCache;
import com.paddi.service.UserCoinService;
import com.paddi.service.VideoService;
import com.paddi.util.AssertUtils;
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
import java.util.Objects;
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

    @Autowired
    private UserCoinService userCoinService;

    @Autowired
    private VideoCoinMapper videoCoinMapper;

    @Autowired
    private VideoCommentMapper videoCommentMapper;


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
        String videoLikeKey = RedisKey.VIDEO_LIKES + videoId;
        Boolean videoLiked = redisCache.isMember(videoLikeKey, userId);
        AssertUtils.isError(videoLiked, new BadRequestException("该用户已经点赞过此视频!"));
        redisCache.addValueToSet(videoLikeKey, userId);
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
        Boolean videoLiked = redisCache.isMember(videoLikeKey, userId);
        AssertUtils.isError(!videoLiked, new BadRequestException("该用户未点赞该视频!"));
        redisCache.removeValueFromSet(videoLikeKey, userId);
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
        Boolean videoLiked = redisCache.isMember(videoLikeKey, userId);
        Long count = 0L;
        VideoStatisticsDataVO videoStatisticsDataVO = new VideoStatisticsDataVO();
        videoStatisticsDataVO.setFlag(videoLiked);
        if(redisCache.hasKey(videoLikeKey)) {
            count = redisCache.size(videoLikeKey);
            videoStatisticsDataVO.setCount(count);
            return videoStatisticsDataVO;
        }
        List<Long> likedUserIds = videoMapper.getVideoLikedUserIdList(videoId);
        if(CollectionUtil.isNotEmpty(likedUserIds)) {
            count = Long.valueOf(likedUserIds.size());
            redisCache.addValuesToSet(videoLikeKey, likedUserIds);
        }
        videoStatisticsDataVO.setCount(count);
        return videoStatisticsDataVO;
    }

    @Override
    public void addVideoCollection(Long userId, Long videoId, Long groupId) {
        checkVideo(videoId);
        checkCollectionGroup(groupId);
        String videoCollectionKey = RedisKey.VIDEO_COLLECTIONS + videoId;
        VideoCollection videoCollection = videoMapper.getVideoCollection(userId, videoId, groupId);
        Boolean videoCollected = videoCollection != null;
        AssertUtils.isError(videoCollected, new BadRequestException("用户已收藏该视频!"));
        redisCache.addValueToSet(videoCollectionKey, userId);
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
        AssertUtils.isNull(videoCollection, new BadRequestException("用户未收藏该视频!"));

        //查询用户所有的分组中此视频的收藏结果
        List<VideoCollection> videoCollections = videoMapper.getUserVideoCollections(userId, videoId);
        if(CollectionUtil.isNotEmpty(videoCollections) && videoCollections.size() == 1) {
            //该用户仅有一个收藏夹中收藏该视频并且取消了收藏
            if(Objects.equals(videoCollection.getId(), videoCollections.get(0).getId())) {
                //移除视频收藏用户列表
                redisCache.removeValueFromSet(videoCollectionKey, userId);
            }
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
        Boolean collected = redisCache.isMember(videoCollectionKey, userId);
        Long count = 0L;
        VideoStatisticsDataVO videoStatisticsDataVO = new VideoStatisticsDataVO();
        videoStatisticsDataVO.setFlag(collected);
        if(redisCache.hasKey(videoCollectionKey)) {
            count = redisCache.size(videoCollectionKey);
            videoStatisticsDataVO.setCount(count);
            return videoStatisticsDataVO;
        }
        List<Long> videoCollectedUserIds = videoMapper.getVideoCollectedUserIdList(videoId);
        if(CollectionUtil.isNotEmpty(videoCollectedUserIds)) {
            count = Long.valueOf(videoCollectedUserIds.size());
            redisCache.addValuesToSet(videoCollectionKey, videoCollectedUserIds);
        }
        videoStatisticsDataVO.setCount(count);
        return videoStatisticsDataVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addVideoCoins(Long userId, Long videoId, Integer amount) {
        Video video = checkVideo(videoId);
        Integer userCoinsAmount = userCoinService.getUserCoinsAmountByUserId(userId);
        userCoinsAmount = userCoinsAmount == null ? 0 : userCoinsAmount;
        AssertUtils.isError(amount > userCoinsAmount, new BadRequestException("用户硬币余额不足!"));

        //计算该视频一个用户的投币数量限制
        int limitCount = 0;
        if(VideoType.ORIGINAL.getValue().equals(video.getType())) {
            limitCount = 2;
        } else if(VideoType.REPRODUCE.getType().equals(video.getType())) {
            limitCount = 1;
        }

        AssertUtils.isError(amount > limitCount, new BadRequestException("投币个数超过限制,最多只能投币" + limitCount + "个"));

        VideoCoin videoCoin = videoMapper.getVideoCoinsByVideoIdAndUserId(videoId, userId);
        if(videoCoin == null) {
            //第一次投币
            videoCoin = VideoCoin.builder()
                                 .videoId(videoId)
                                 .userId(userId)
                                 .amount(amount)
                                 .createTime(LocalDateTime.now())
                                 .build();
            videoCoinMapper.insert(videoCoin);
        }else {
            //新增投币
            Integer alreadyAddAmount = videoCoin.getAmount();
            AssertUtils.isError(alreadyAddAmount + amount > limitCount, new BadRequestException("投币个数超过限制,最多只能投币" + limitCount + "个"));
            alreadyAddAmount += amount;
            videoCoin.setAmount(alreadyAddAmount);
            videoCoin.setUpdateTime(LocalDateTime.now());
            videoCoinMapper.updateById(videoCoin);
        }

        String redisKey = RedisKey.VIDEO_COINS + videoId;
        if(redisCache.hasKey(redisKey)) {
            redisCache.increment(redisKey, Long.valueOf(amount));
        }

    }

    @Override
    public VideoStatisticsDataVO getVideoCoins(Long videoId, Long userId) {
        checkVideo(videoId);
        VideoStatisticsDataVO videoStatisticsDataVO = new VideoStatisticsDataVO();
        Boolean liked = videoCoinMapper.getVideoCoinByVideoIdAndUserId(videoId, userId) != null;
        videoStatisticsDataVO.setFlag(liked);

        String redisKey = RedisKey.VIDEO_COINS + videoId;
        Integer totalVideoCoins = null;
        if(redisCache.hasKey(redisKey)) {
            totalVideoCoins = redisCache.getCacheObject(redisKey);
            videoStatisticsDataVO.setCount(Long.valueOf(totalVideoCoins));
            return videoStatisticsDataVO;
        }

        totalVideoCoins = videoCoinMapper.getVideoCoins(videoId);
        videoStatisticsDataVO.setCount(Long.valueOf(totalVideoCoins));
        redisCache.setCacheObject(redisKey, totalVideoCoins, 60, TimeUnit.MINUTES);
        return videoStatisticsDataVO;
    }

    @Override
    public void addVideoComment(Long userId, VideoCommentAddDTO videoCommentAddDTO) {
        Long videoId = videoCommentAddDTO.getVideoId();
        Video video = videoMapper.selectById(videoId);
        AssertUtils.isNull(video, new BadRequestException("视频不存在!"));
        VideoComment videoComment = new VideoComment(videoCommentAddDTO);
        videoComment.setUserId(userId);
        videoComment.setCreateTime(LocalDateTime.now());
        videoCommentMapper.insert(videoComment);
    }

    @Override
    public PageResult<VideoCommentVO> pageListVideoComments(Long userId, VideoCommentPageListDTO videoCommentPageListDTO) {
        Long videoId = videoCommentPageListDTO.getVideoId();
        PageParam pageParam = videoCommentPageListDTO.getPageParam();
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        PageUtils.pageCheck(pageNum, pageSize);
        checkVideo(videoId);
        PageHelper.startPage(pageNum, pageSize);
        List<VideoComment> videoRootComments = videoCommentMapper.pageListVideoRootComments(videoId);
        PageInfo<VideoComment> pageInfo = new PageInfo<>(videoRootComments);
        Long total = pageInfo.getTotal();
        Integer pages = pageInfo.getPageSize();
        List<VideoCommentVO> list = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(videoRootComments)) {
            List<Long> parentIdList = videoRootComments.stream().map(VideoComment :: getId).collect(Collectors.toList());
            //TODO 使用SQL进行分组或者Stream进行分组
            videoMapper.getVideoCommentsGroupByRootIds(parentIdList);
        }
        return new PageResult<>(total, pages, list);
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
