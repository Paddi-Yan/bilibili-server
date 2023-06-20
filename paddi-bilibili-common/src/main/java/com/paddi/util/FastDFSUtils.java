package com.paddi.util;

import cn.hutool.core.util.StrUtil;
import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.paddi.constants.RedisKey;
import com.paddi.exception.BadRequestException;
import com.paddi.exception.ConditionException;
import com.paddi.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月19日 22:00:45
 */
@Component
public class FastDFSUtils {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisCache redisCache;

    @Value("${fdfs.http.storage-addr}")
    private String httpStorageAddr;

    private static final String DEFAULT_GROUP = "group1";

    private static final int SLICE_SIZE = 1024 * 1024;

    public String getFileType(MultipartFile file) {
        if(file == null) {
            throw new ConditionException("文件为空!");
        }
        String filename = file.getOriginalFilename();
        int index = filename.lastIndexOf(".");
        return filename.substring(index + 1);
    }

    public String getFileName(MultipartFile file) {
        if(file == null) {
            throw new ConditionException("文件为空!");
        }
        String filename = file.getOriginalFilename();
        int index = filename.lastIndexOf(".");
        return filename.substring(0, index);
    }

    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = this.getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }

    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }

    public String uploadAppenderFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws Exception {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer currentSliceNum, Integer totalSliceNum) throws Exception {
        if(file == null || currentSliceNum == null || totalSliceNum == null) {
            throw new BadRequestException("文件上传参数异常!");
        }
        //文件路径Key
        String pathKey = RedisKey.PATH_KEY + fileMd5;
        //已上传文件大小Key
        String uploadedSizeKey = RedisKey.UPLOADED_SIZE_KEY + fileMd5;
        //已上传文件分片数量Key
        String uploadedNumKey = RedisKey.UPLOADED_NUM_KEY + fileMd5;
        //获取已上传的文件大小
        Long uploadedSize = redisCache.getCacheObject(uploadedSizeKey);
        if(uploadedSize == null) {
            uploadedSize = 0L;
        }
        String fileType = this.getFileType(file);
        //上传的是第一个分片
        if(currentSliceNum == 1) {
            String path = this.uploadAppenderFile(file);
            if(StrUtil.isEmpty(path)) {
                throw new ConditionException("上传失败!");
            }
            //设置上传路径
            redisCache.setCacheObject(pathKey, path);
            //设置已上传分片数量
            redisCache.setCacheObject(uploadedNumKey, 1);
        }else {
            String path = redisCache.getCacheObject(pathKey);
            if(StrUtil.isEmpty(path)) {
                throw new ConditionException("上传失败!");
            }
            //上传后续的文件分片
            this.modifyAppenderFile(file, path, uploadedSize);
            //已上传的文件分片数量自增加一
            redisCache.increment(uploadedNumKey);
        }
        //更新文件上传大小
        uploadedSize += file.getSize();
        redisCache.setCacheObject(uploadedSizeKey, uploadedSize);

        //判断文件分片是否已经全部上传
        Integer uploadedNum = redisCache.getCacheObject(uploadedNumKey);
        String resultPath = "";
        if(uploadedNum != null && uploadedNum.equals(totalSliceNum)) {
            //删除所有分片上传的RedisKey
            resultPath = redisCache.getCacheObject(pathKey);
            List<String> redisKey = Arrays.asList(uploadedNumKey, uploadedSizeKey, pathKey);
            redisCache.deleteObject(redisKey);
        }
        return resultPath;
    }

    public void convertFileToSlices(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String fileType = this.getFileType(multipartFile);
        File file = multipartFileToFile(multipartFile);
        long fileSize = file.length();
        int count = 1;
        for(int i = 0; i < fileSize; i += SLICE_SIZE, count++) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            int readLength = randomAccessFile.read(bytes);
            String path = "E:\\IntelliJ-IDEA-WorkPlace\\paddi-bilibili-server\\temp\\" + count + "." + fileType;
            File slice = new File(path);
            FileOutputStream stream = new FileOutputStream(slice);
            stream.write(bytes, 0, readLength);
            stream.close();
            randomAccessFile.close();
        }
        file.delete();
    }

    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = this.getFileName(multipartFile);
        String fileType = this.getFileType(multipartFile);
        System.out.println("filename: " + fileName + "." + fileType);
        File file = File.createTempFile(fileName, "." + fileType);
        multipartFile.transferTo(file);
        return file;
    }

    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        long totalFileSize = fileInfo.getFileSize();
        String url = httpStorageAddr + path;
        Enumeration<String> headerNames = request.getHeaderNames();
        HashMap<String, Object> headers = new HashMap<>();
        while(headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        String rangeStr = request.getHeader(HttpHeaders.RANGE);
        String[] range;
        if(StrUtil.isEmpty(rangeStr)) {
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }
        range = rangeStr.split("bytes=|-");
        long begin = 0;
        if(range.length >= 2) {
            begin = Long.parseLong(range[1]);
        }
        long end = totalFileSize - 1;
        if(range.length >= 3) {
            end = Long.parseLong(range[2]);
        }
        long len = (end - begin) + 1;
        String contentRange = "bytes " + begin + "-" + end + "/" + totalFileSize;
        response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "video/mp4");
        response.setContentLength((int) len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        HTTPUtils.get(url, headers, response);
    }
}
