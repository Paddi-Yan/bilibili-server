package com.paddi.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 23:53:42
 */
public interface FileService {
    String uploadFileBySlices(MultipartFile file, String fileMd5, Integer currentSliceNum, Integer totalSliceNum) throws Exception;

    String getFileMD5(MultipartFile file) throws Exception;
}
