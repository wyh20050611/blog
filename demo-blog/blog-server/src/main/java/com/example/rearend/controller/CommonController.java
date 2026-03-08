package com.example.rearend.controller;

import com.example.rearend.constant.MessageConstant;
import com.example.rearend.result.Result;
import com.example.rearend.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author 34932
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
@RequiredArgsConstructor
public class CommonController {

    private final AliOssUtil aliOssUtil;

    /**
     * 文件上传接口
     *
     * @param file 文件
     * @return Result
     */
    @PostMapping("/upload")
    @ApiOperation("图片上传接口")
    public Result<String> upload(MultipartFile file) {
        log.info("开始文件上传：{}", file);
        try {
            //1、获取文件名
            String originalFilename = file.getOriginalFilename();
            //2、获取文件名后缀
            String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
            //3、获取最终文件名
            String newFileName = UUID.randomUUID() + substring;
            //4开始文件上传
            String upload = aliOssUtil.upload(file.getBytes(), newFileName);
            //5、返回
            return Result.success(upload);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //上传失败
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
