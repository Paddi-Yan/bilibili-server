package com.paddi.controller;

import com.paddi.core.ret.Result;
import com.paddi.service.FileService;
import com.paddi.util.FastDFSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 23:41:19
 */
@RestController
public class FileController {

    @Autowired
    private FastDFSUtils fastDFSUtils;

    @Autowired
    private FileService fileService;

    @GetMapping("/slices")
    public void slices(MultipartFile file) throws IOException {
        fastDFSUtils.convertFileToSlices(file);
    }

    @PutMapping("/file-slices")
    public Result uploadFileBySlices(MultipartFile slice, String fileMd5, Integer currentSliceNum, Integer totalSliceNum) throws Exception {
        String path = fileService.uploadFileBySlices(slice, fileMd5, currentSliceNum, totalSliceNum);
        return Result.success(path);
    }

    @GetMapping("/md5files")
    public Result getFileMD5(MultipartFile file) throws Exception {
        String fileMD5 = fileService.getFileMD5(file);
        return Result.success(fileMD5);
    }

}
