package com.paddi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.paddi.constants.RedisKey;
import com.paddi.entity.dto.*;
import com.paddi.entity.po.*;
import com.paddi.entity.vo.*;
import com.paddi.enums.SortType;
import com.paddi.enums.VideoCommentOperationType;
import com.paddi.enums.VideoOperationType;
import com.paddi.enums.VideoType;
import com.paddi.exception.BadRequestException;
import com.paddi.mapper.*;
import com.paddi.message.VideoCommentMessage;
import com.paddi.message.VideoOperationMessage;
import com.paddi.redis.RedisCache;
import com.paddi.service.UserCoinService;
import com.paddi.service.UserService;
import com.paddi.service.VideoService;
import com.paddi.util.AssertUtils;
import com.paddi.util.FastDFSUtils;
import com.paddi.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.paddi.constants.RocketMQConstants.VIDEO_COMMENT_TOPIC;
import static com.paddi.constants.RocketMQConstants.VIDEO_TOPIC;
import static com.paddi.constants.SystemConstants.BATCH_SIZE;
import static com.paddi.message.VideoCommentMessage.REPLY_USER_ID;
import static com.paddi.message.VideoCommentMessage.ROOT_COMMENT_ID;
import static com.paddi.message.VideoOperationMessage.COLLECTION_GROUP_ID;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月20日 00:47:43
 */
@Service
@Slf4j
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

    @Autowired
    private UserService userService;

    @Autowired
    private VideoCommentAreaMapper videoCommentAreaMapper;


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
             videoVOList = videoList.parallelStream().map(video -> buildVideoVO(video)).collect(Collectors.toList());
        }
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getPages(), videoVOList);
    }

    private VideoVO buildVideoVO(Video video) {
        VideoVO videoVO = new VideoVO(video);
        List<Tag> videoTags = videoMapper.getVideoTags(video.getId());
        videoVO.setVideoTagList(videoTags);
        return videoVO;
    }

    @Override
    public VideoVO getVideoInfo(Long videoId) {
        checkVideo(videoId);
        Video video = videoMapper.selectById(videoId);
        return buildVideoVO(video);
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
            count = redisCache.getSetSize(videoLikeKey);
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
            count = redisCache.getSetSize(videoCollectionKey);
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
    public VideoCommentAddVO addVideoComment(Long userId, VideoCommentAddDTO videoCommentAddDTO) {
        Long videoId = videoCommentAddDTO.getVideoId();
        Video video = videoMapper.selectById(videoId);
        VideoCommentArea videoCommentArea = videoCommentAreaMapper.selectById(videoCommentAddDTO.getCommentAreaId());
        AssertUtils.isNull(new BadRequestException("参数错误!"), video, videoCommentArea);
        AssertUtils.isError(BooleanUtil.isFalse(videoCommentArea.getStatus()), new BadRequestException("评论区已关闭"));
        Long replyUserId = videoCommentAddDTO.getReplyUserId();
        if(replyUserId != null) {
            User replyUser = userService.getUserById(replyUserId);
            AssertUtils.isNull(replyUser, new BadRequestException("参数错误!"));
        }
        Long rootId = videoCommentAddDTO.getRootId();
        if(rootId != null) {
            VideoComment rootComment = videoCommentMapper.selectById(rootId);
            AssertUtils.isNull(rootComment, new BadRequestException("参数错误!"));
        }

        //TODO 内容审核
        //审核完毕
        String comment = videoCommentAddDTO.getComment();
        VideoCommentAddVO result = new VideoCommentAddVO(comment, LocalDateTime.now());

        HashMap<String, Long> attachments = new HashMap<>();
        attachments.put(ROOT_COMMENT_ID, rootId);
        attachments.put(REPLY_USER_ID, replyUserId);
        VideoCommentMessage message = VideoCommentMessage.builder()
                                                       .userId(userId)
                                                       .videoId(videoId)
                                                       .commentAreaId(videoCommentArea.getId())
                                                       .comment(comment)
                                                       .operationType(VideoCommentOperationType.ADD_COMMENT)
                                                       .attachments(attachments).build();
        //以评论区为维度串行处理
        rocketMQTemplate.syncSendOrderly(VIDEO_COMMENT_TOPIC, JSON.toJSONString(message), String.valueOf(videoCommentArea.getId()));
        //返回评论发布结果给前端完成一次交互
        return result;
    }

    /**
     * 分页查询视频评论
     *
     * @param userId 可为空 即游客模式
     * @param videoCommentPageListDTO
     * @param value
     * @return
     */
    @Override
    public PageResult<VideoCommentVO> pageListVideoComments(Long userId, VideoCommentPageListDTO videoCommentPageListDTO,
                                                            Integer value) {
        Long videoId = videoCommentPageListDTO.getVideoId();
        PageParam pageParam = videoCommentPageListDTO.getPageParam();
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        PageUtils.pageCheck(pageNum, pageSize);
        checkVideo(videoId);

        SortType sortType = SortType.getSortType(value);
        AssertUtils.isNull(sortType, new BadRequestException("错误的排序类型!"));

        List<VideoComment> videoRootComments = null;
        Long total = null;
        Integer pages = null;
        if(SortType.DEFAULT.equals(sortType)) {
            //按照热度分页查询一级评论
            String commentsPopularityKey = RedisKey.VIDEO_ROOT_COMMENTS_POPULARITY + videoId;
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = startIndex + pageSize - 1;
            //查询出较高热度的一级评论编号
            Set<Long> rootCommentIdList = redisCache.getCacheZSetValues(commentsPopularityKey, startIndex, endIndex);
            videoRootComments = videoCommentMapper.selectBatchIds(rootCommentIdList);
            total = redisCache.getZSetSize(commentsPopularityKey);
            pages = getPages(total, pageSize);
        } else if(SortType.BY_TIME_DESC.equals(sortType)) {
            //按照时间降序分页查询一级评论
            PageHelper.startPage(pageNum, pageSize);
            videoRootComments = videoCommentMapper.pageListVideoRootComments(videoId);
            PageInfo<VideoComment> pageInfo = new PageInfo<>(videoRootComments);
            total = pageInfo.getTotal();
            pages = pageInfo.getPageSize();
        }
        List<VideoCommentVO> result = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(videoRootComments)) {
            List<Long> rootCommentIdList = videoRootComments.stream()
                                                            .map(VideoComment :: getId)
                                                            .collect(Collectors.toList());
            List<RootVideoComment> rootIdAndChildrenComments = videoMapper.getVideoCommentsGroupByRootIds(rootCommentIdList);
            //构建一级评论编号和其二级评论的映射
            Map<Long, List<VideoComment>> rootIdToChildrenComments = rootIdAndChildrenComments.stream()
                                                                                              .collect(Collectors.toMap(RootVideoComment :: getRootId, RootVideoComment :: getChildrenComments));
            //获取一级评论和二级评论所有的用户编号
            Set<Long> userIdList = videoRootComments.stream()
                                                    .map(VideoComment :: getUserId)
                                                    .collect(Collectors.toSet());
            for(List<VideoComment> videoComments : rootIdToChildrenComments.values()) {
                for(VideoComment videoComment : videoComments) {
                    userIdList.add(videoComment.getUserId());
                    userIdList.add(videoComment.getReplyUserId());
                }
            }
            //查询相关评论的用户信息
            List<UserInfo> userInfoList = userService.getUserByUserIds(userIdList);
            Map<Long, UserInfo> userIdToInfoMap = userInfoList.stream()
                                                              .collect(Collectors.toMap(UserInfo :: getUserId, Function.identity()));
            //遍历一级评论
            for(VideoComment videoRootComment : videoRootComments) {
                //构建一级评论
                VideoCommentVO videoRootCommentVO = buildVideoCommentVO(userId, userIdToInfoMap, videoRootComment);

                List<VideoCommentVO> childrenCommentList = new ArrayList<>();
                //遍历该一级评论下的二级评论
                for(VideoComment childrenComment : rootIdToChildrenComments.get(videoRootComment.getId())) {
                    //构建二级评论
                    VideoCommentVO videoChildrenCommentVO = buildVideoCommentVO(userId, userIdToInfoMap, childrenComment);
                    childrenCommentList.add(videoChildrenCommentVO);
                }
                videoRootCommentVO.setChildList(childrenCommentList);
                result.add(videoRootCommentVO);
            }
        }
        return new PageResult<>(total, pages, result);
    }

    private Integer getPages(Long total, Integer pageSize) {
        return Math.toIntExact((total + pageSize - 1) / pageSize);
    }


    private VideoCommentVO buildVideoCommentVO(Long userId, Map<Long, UserInfo> userIdToInfoMap,
                                             VideoComment videoComment) {
        VideoCommentVO videoRootCommentVO = new VideoCommentVO(videoComment);
        VideoStatisticsDataVO videoStatisticsData = getVideoCommentLike(userId, videoComment.getId());
        videoRootCommentVO.setLiked(videoStatisticsData.getFlag());
        videoRootCommentVO.setLikeCount(videoStatisticsData.getCount());
        videoRootCommentVO.setUserInfo(userIdToInfoMap.get(videoComment.getUserId()));
        videoRootCommentVO.setReplyUserInfo(userIdToInfoMap.get(videoComment.getReplyUserId()));
        return videoRootCommentVO;
    }

    @Override
    public void addVideoCommentLike(Long userId, VideoCommentLikeDTO videoCommentLikeDTO) {
        Long videoId = videoCommentLikeDTO.getVideoId();
        checkVideo(videoId);
        Long commentId = videoCommentLikeDTO.getCommentId();
        String commentsLikesKey = RedisKey.VIDEO_COMMENTS_LIKE + commentId;
        String commentsPopularityKey;
        redisCache.addValueToSet(commentsLikesKey, userId);
        VideoComment videoComment = videoCommentMapper.selectById(commentId);
        AssertUtils.isNull(videoComment, new BadRequestException("评论不存在!"));
        //区分一级评论/二级评论
        if(videoComment.getRootId() == null) {
            //一级评论
            commentsPopularityKey = RedisKey.VIDEO_ROOT_COMMENTS_POPULARITY + videoId;
        }else {
            //二级评论
            commentsPopularityKey = RedisKey.VIDEO_ROOT_COMMENTS_POPULARITY + videoComment.getRootId();
        }
        redisCache.addValueToZSet(commentsPopularityKey, commentId, 1);

        //TODO 缓存重建
        //TODO 使用消息队列进行点赞记录异步落库
    }

    /**
     * 关于视频评论缓存
     * 1.评论点赞用户的集合,其中存储了点赞的用户编号,可以快速判断用户是否点赞以及获取该评论的点赞数量
     * 2.视频中所有评论的点赞排序集合,其中存储了一个视频中的每条评论以及每条评论对应的点赞数量,用户评论获取时按照热度进行从高到低排序
     * @param userId
     * @param commentId
     * @return
     */
    @Override
    public VideoStatisticsDataVO getVideoCommentLike(Long userId, Long commentId) {
        String redisKey = RedisKey.VIDEO_COMMENTS_LIKE + commentId;
        Boolean liked = userId != null && redisCache.isMember(redisKey, userId);
        Long count = getVideoCommentLike(commentId);
        return new VideoStatisticsDataVO(count, liked);
    }

    @Override
    public Long getVideoCommentLike(Long commentId) {
        String redisKey = RedisKey.VIDEO_COMMENTS_LIKE + commentId;
        return redisCache.getSetSize(redisKey);
    }

    @Override
    public VideoDetailsVO getVideoDetails(Long userId, Long videoId) {
        CompletableFuture<VideoVO> videoFuture = CompletableFuture.supplyAsync(() -> getVideoInfo(videoId));
        CompletableFuture<VideoStatisticsDataVO> videoLikesFuture = CompletableFuture.supplyAsync(() -> getVideoLikes(videoId, userId));
        CompletableFuture<VideoStatisticsDataVO> videoCoinsFuture = CompletableFuture.supplyAsync(() -> getVideoCoins(videoId, userId));
        CompletableFuture<VideoStatisticsDataVO> videoCollectionsFuture = CompletableFuture.supplyAsync(() -> getVideoCollectionCount(videoId, userId));
        CompletableFuture<PageResult<VideoCommentVO>> videoCommentsFuture = CompletableFuture.supplyAsync(() -> {
            VideoCommentPageListDTO param = new VideoCommentPageListDTO();
            param.setVideoId(videoId);
            param.setPageParam(new PageParam(1, 20));
            return pageListVideoComments(userId, param, SortType.DEFAULT.getValue());
        });
        CompletableFuture<UserInfoVO> userInfoFuture = videoFuture.thenApply(videoVO -> userService.getUserInfo(userId, videoVO.getUserId()));
        CompletableFuture<VideoDetailsVO> videoDetailsFuture = CompletableFuture.allOf(videoFuture, videoLikesFuture, videoCoinsFuture, videoCollectionsFuture, userInfoFuture, videoCommentsFuture)
                                                                .thenApply(result -> {
                VideoVO videoVO = videoFuture.join();
                VideoStatisticsDataVO likes = videoLikesFuture.join();
                VideoStatisticsDataVO coins = videoCoinsFuture.join();
                VideoStatisticsDataVO collections = videoCollectionsFuture.join();
                PageResult<VideoCommentVO> pageResult = videoCommentsFuture.join();
                UserInfoVO userInfoVO = userInfoFuture.join();
                return VideoDetailsVO.builder()
                                     .videoVO(videoVO)
                                     .likes(likes)
                                     .coins(coins)
                                     .collections(collections)
                                     .videoComments(pageResult)
                                     .userInfoVO(userInfoVO)
                                     .build();
        });
        VideoDetailsVO videoDetailsVO = null;
        try {
            videoDetailsVO = videoDetailsFuture.get();
        } catch(Exception e) {
            Throwable cause = e.getCause();
            if(cause instanceof BadRequestException) {
                throw (BadRequestException) cause;
            }else {
                log.error("发生异常: {}", cause.getMessage());
            }
        }
        return videoDetailsVO;
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
