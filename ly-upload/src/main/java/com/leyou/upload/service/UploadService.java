package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {
    @Autowired
    private UploadProperties uploadProperties;

    @Autowired
    private FastFileStorageClient storageClient;

    /**
     * 上传图片
     * @param file
     * @return
     */
    public String uploadImage(MultipartFile file) {
        try {
            //获取上传文件类型
            String contentType = file.getContentType();

            //判断是否为允许类型
            if(!uploadProperties.getAllowTypes().contains(contentType)){
                throw new LyException(ExceptionEnum.FILE_TYPE_NOT_ALLOW);
            }

            //获取图片对象
            BufferedImage image = ImageIO.read(file.getInputStream());

            //校验文件内容
            if(image == null){
                throw new LyException(ExceptionEnum.FILE_TYPE_NOT_ALLOW);
            }

            //获取文件后缀
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");

            //上传到FastDFS
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            //路径返回
            return uploadProperties.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            //上传失败
            log.error("[文件上传]文件上传失败",e);
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
    }
}
