package com.paddi.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paddi.entity.po.File;
import com.paddi.mapper.FileMapper;
import com.paddi.service.FileService;
import com.paddi.util.FastDFSUtils;
import com.paddi.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 23:53:48
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FastDFSUtils fastDFSUtils;

    @Autowired
    private FileMapper fileMapper;

    @Override
    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer currentSliceNum, Integer totalSliceNum) throws Exception {
        File fileInfo = fileMapper.selectOne(new LambdaQueryWrapper<File>().eq(File :: getMd5, fileMd5));
        if(fileInfo != null) {
            return fileInfo.getUrl();
        }

        String url = fastDFSUtils.uploadFileBySlices(file, fileMd5, currentSliceNum, totalSliceNum);
        if(StrUtil.isNotEmpty(url)) {
            fileMapper.insert(File.builder()
                                  .url(url)
                                  .type(fastDFSUtils.getFileType(file))
                                  .md5(fileMd5)
                                  .createTime(LocalDateTime.now())
                                  .build());
        }
        return url;
    }

    @Override
    public String getFileMD5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }
}
