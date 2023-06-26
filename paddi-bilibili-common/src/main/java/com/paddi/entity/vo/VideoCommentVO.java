package com.paddi.entity.vo;

import com.paddi.entity.po.UserInfo;
import com.paddi.entity.po.VideoComment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月25日 23:50:57
 */
@Data
public class VideoCommentVO {
    private Long id;

    private Long videoId;

    private Long userId;

    private String comment;

    private Long replyUserId;

    private Long rootId;

    private Boolean liked;

    private Long likeCount;

    private LocalDateTime createTime;

    private List<VideoCommentVO> childList;

    private UserInfo userInfo;

    private UserInfo replyUserInfo;

    public VideoCommentVO(VideoComment videoComment) {
        if(videoComment != null) {
            BeanUtils.copyProperties(videoComment, this);
        }
    }
}
