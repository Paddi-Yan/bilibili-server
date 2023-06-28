package com.paddi.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.paddi.entity.dto.VideoCommentAddDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月25日 23:28:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoComment {
    private Long id;

    private Long videoId;

    private Long userId;

    private Long contentId;

    private Long replyUserId;

    private Long rootId;

    private Integer childCommentCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<VideoComment> childList;

    @TableField(exist = false)
    private UserInfo userInfo;

    @TableField(exist = false)
    private UserInfo replyUserInfo;




    public VideoComment(VideoCommentAddDTO videoCommentAddDTO) {
        if(videoCommentAddDTO != null) {
            BeanUtils.copyProperties(videoCommentAddDTO, this);
        }
    }
}
