package com.paddi.strategy.video.comment;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.paddi.constants.RedisKey;
import com.paddi.entity.po.VideoComment;
import com.paddi.enums.VideoCommentOperationType;
import com.paddi.mapper.VideoCommentAreaMapper;
import com.paddi.mapper.VideoCommentMapper;
import com.paddi.message.VideoCommentMessage;
import com.paddi.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.paddi.message.VideoCommentMessage.REPLY_USER_ID;
import static com.paddi.message.VideoCommentMessage.ROOT_COMMENT_ID;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月28日 14:31:38
 */
@Service
@Slf4j
public class VideoAddCommentOperation implements VideoCommentOperation {

    @Autowired
    private VideoCommentMapper videoCommentMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private VideoCommentAreaMapper videoCommentAreaMapper;

    private static final String scriptPath = "scripts/videoCommentCounter.lua";

    private static final String getCounterScriptPath = "scripts/getAndDelCounter.lua";

    private static final Integer THRESHOLD = 2;

    private static final Integer CREATED = -1;
    private static final Integer SUCCESS = 0;

    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    private class MergeVideoCommentCounterTask implements Runnable{

        private Long areaId;

        public MergeVideoCommentCounterTask(Long areaId) {
            this.areaId = areaId;
        }

        @Override
        public void run() {
            DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>(FileUtil.readString(ResourceUtil.getResource(getCounterScriptPath), "UTF-8"), Integer.class);
            String counterKey = RedisKey.VIDEO_COMMENT_COUNTER + areaId;
            Integer value = redisCache.execute(redisScript, Collections.singletonList(counterKey));
            log.info("合并视频评论区[{}]评论数量[{}]", areaId, value);
            videoCommentAreaMapper.increaseCommentCount(areaId, value);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(VideoCommentMessage message) {
        Long videoId = message.getVideoId();
        Long userId = message.getUserId();
        String comment = message.getComment();
        Long commentAreaId = message.getCommentAreaId();
        Map<String, Long> attachments = message.getAttachments();
        Long rootId = attachments.get(ROOT_COMMENT_ID);
        Long replyUserId = attachments.get(REPLY_USER_ID);
        //落库
        Long contentId = videoCommentMapper.insertCommentContent(comment);
        VideoComment videoComment = VideoComment.builder()
                                         .videoId(videoId)
                                         .userId(userId)
                                         .contentId(contentId)
                                         .replyUserId(replyUserId)
                                         .rootId(rootId)
                                         .childCommentCount(0)
                                         .createTime(LocalDateTime.now())
                                         .build();
        videoCommentMapper.insert(videoComment);
        //更新计数类数据 评论区的评论统计
        //回复一级评论
        if(rootId != null) {
            //增加回复数量计数
            VideoComment rootComment = videoCommentMapper.selectById(rootId);
            Integer count = rootComment.getChildCommentCount();
            videoCommentMapper.updateReplyCommentCount(rootId, count + 1, count);
        }
        //TODO 计数类数据内存合并再进行落库
        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>(FileUtil.readString(ResourceUtil.getResource(scriptPath), "UTF-8"), Integer.class);
        String counterKey = RedisKey.VIDEO_COMMENT_COUNTER + commentAreaId;
        Integer result = redisCache.execute(redisScript, Collections.singletonList(counterKey), THRESHOLD);
        if(CREATED.equals(result)) {
            //创建定时任务
            executor.schedule(new MergeVideoCommentCounterTask(commentAreaId), 30, TimeUnit.SECONDS);
        } else if(SUCCESS.equals(result)) {
            //不需要执行操作
        } else if(result >= THRESHOLD) {
            //合并内存更新数据库
            videoCommentAreaMapper.increaseCommentCount(commentAreaId, result);
        }
        //TODO 缓存刷新
    }

    @Override
    public VideoCommentOperationType getOperationType() {
        return VideoCommentOperationType.ADD_COMMENT;
    }
}
