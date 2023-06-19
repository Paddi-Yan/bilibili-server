package com.paddi.service;

import com.paddi.entity.dto.UserMomentsDTO;
import com.paddi.entity.po.UserMoments;

import java.util.List;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月17日 20:09:30
 */
public interface UserMomentsService {
    void postMoments(UserMomentsDTO userMomentsDTO);

    List<UserMoments> getUserSubscribedMoments(Long userId);
}
