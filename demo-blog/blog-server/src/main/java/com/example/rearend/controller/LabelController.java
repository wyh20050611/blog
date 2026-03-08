package com.example.rearend.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rearend.entity.Label;
import com.example.rearend.entity.Writing;
import com.example.rearend.result.Result;
import com.example.rearend.service.ILabelService;
import com.example.rearend.vo.LabelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * 标签表 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-08-07
 */
@RestController
@RequestMapping("/admin/label")
@Api(tags = "标签表相关接口")
@Slf4j
@RequiredArgsConstructor
public class LabelController {

    private final ILabelService labelService;

    /**
     * 根据文章id查询文章标签
     *
     * @param id 文章id
     * @return Result
     */
    @GetMapping("/{id}")
    @ApiOperation("根据文章id查询文章标签")
    public Result<List<String>> selectByWritingId(@PathVariable Long id) {
        log.info("根据文章id查询文章标签:{}", id);
        List<String> list = labelService.selectByWritingId(id);
        return Result.success(list);
    }

    /**
     * 查询所有的标签
     *
     * @return Result
     */
    @GetMapping
    @ApiOperation("查询所有的标签")
    public Result<HashSet<LabelVO>> selectAll() {
        log.info("查询所有的标签");
        HashSet<LabelVO> list = labelService.selectAll();
        return Result.success(list);
    }

    /**
     * 根据标签名分页查询文章
     *
     * @param labelName 标签名
     * @param page 当前页码，默认为1
     * @param size 每页大小，默认为10
     * @return 分页结果
     */
    @GetMapping("/labelName/page")
    @ApiOperation("根据标签名分页查询文章")
    public Result<Page<Writing>> selectByLabelNameWithPage(
            @RequestParam String labelName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("根据标签名分页查询文章: labelName={}, page={}, size={}", labelName, page, size);
        return labelService.selectByLabelNameWithPage(labelName, page, size);
    }


}
