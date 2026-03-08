package com.example.rearend.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.example.rearend.dto.WritingPageDTO;
import com.example.rearend.entity.Label;
import com.example.rearend.entity.Writing;
import com.example.rearend.properties.FileProperties;
import com.example.rearend.result.PageResult;
import com.example.rearend.result.Result;
import com.example.rearend.service.ILabelService;
import com.example.rearend.service.IWritingService;
import com.example.rearend.vo.HotWritingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author 34932
 */
@RequestMapping("/admin/writing")
@Slf4j
@Api(tags = "文章相关接口")
@RestController
@RequiredArgsConstructor
public class WritingController {

    private final IWritingService writingService;

    private final ILabelService labelService;

    /**
     * 上传文章
     *
     * @param param 前端传来的数据
     * @return Result
     */
    @PostMapping("/save/{writingId}")
    @ApiOperation("新增文章接口")
    public Result<String> save(@RequestBody JSONObject param,@PathVariable Long writingId) {
        log.info("上传文章->md:{}", param);
        ArrayList<String> list = new ArrayList<>();
        //取出java中对应参数的值
        String content = param.getString("content");
        String name = param.getString("name");
        JSONArray types = param.getJSONArray("types");
        //查询表里是否有该文章，有就执行更新操作
        if (writingId!=0) {
            //判断标签是否已经存在，不存在则新增
            IntStream.range(0, types.size()).mapToObj(types::getString).filter(Objects::nonNull).forEachOrdered(type -> {
                list.add(type);
                QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
                labelQueryWrapper.lambda().eq(Label::getLabelName, type).eq(Label::getWritingId,writingId);
                Label label = labelService.getOne(labelQueryWrapper);
                if (label == null) {
                    Label label1 = Label.builder()
                            .writingId(writingId)
                            .labelName(type)
                            .build();
                    labelService.save(label1);
                }
            });
            UpdateWrapper<Writing> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .eq(Writing::getId, writingId)
                    .set(Writing::getTitle,name)
                    .set(Writing::getContent, content)
                    .set(Writing::getLabelName,String.valueOf(list));
            writingService.update(null, updateWrapper);
            return Result.success("OK");
        }
        for (int i = 0; i < types.size(); i++) {
            String type = types.getString(i);
            if (type == null) {
                continue;
            }
            list.add(type);
        }
        //往表里加数据
        Writing writing = Writing.builder()
                .content(content)
                .title(name)
                .labelName(String.valueOf(list))
                .build();
        writingService.save(writing);
        //查询该文章id
        Long id = writingService.selectId(name);
        //往标签表里加数据
        for (int i = 0; i < types.size(); i++) {
            String type = types.getString(i);
            if (type == null) {
                continue;
            }
            Label label = Label.builder()
                    .labelName(type)
                    .writingId(id)
                    .build();
            labelService.save(label);
        }
        return Result.success("OK");
    }

    /**
     * 分页查询
     *
     * @param writingPageDTO 分页查询数据
     * @return Result
     */
    @GetMapping("/page")
    @ApiOperation("文章分页查询")
    public Result<PageResult> page(WritingPageDTO writingPageDTO) {
        log.info("分页查询:{}", writingPageDTO);
        PageResult list = writingService.selectPage(writingPageDTO);
        return Result.success(list);
    }

    /**
     * 根据id查询文章
     *
     * @param id 文章id
     * @return Result
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询文章")
    public Result<Writing> selectById(@PathVariable Long id) {
        log.info("根据id查询文章:{}", id);
        Writing writing = writingService.getById(id);
        // 每次访问时，增加访问次数并更新数据库
        if (writing != null) {
            writing.setViewCount(writing.getViewCount() + 1);
            writingService.updateById(writing); // 更新数据库中的viewCount字段
        }
        return Result.success(writing);
    }

    /**
     * 根据标题查询文章
     *
     * @param title 文章标题
     * @return Result
     */
    @GetMapping("/title")
    @ApiOperation("根据标题查询文章")
    public Result<List<Writing>> selectByTitle(String title) {
        log.info("根据标题查询文章:{}", title);
        LambdaQueryWrapper<Writing> writingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Writing> list = writingService.list(writingLambdaQueryWrapper.like(Writing::getTitle, title));
        return Result.success(list);
    }

    /**
     * 查询最热文章
     *
     * @return Result
     */
    @GetMapping("/hot")
    @ApiOperation("查询最热文章")
    public Result<List<HotWritingVO>> selectHotWriting() {
        List<HotWritingVO> list = writingService.selectHotWriting();
        return Result.success(list);
    }

    /**
     * 查询所有文章总数
     *
     * @return Result
     */
    @GetMapping("/count")
    @ApiOperation("查询所有文章总数")
    public Result<Integer> selectCount() {
        Integer count = writingService.count();
        return Result.success(count);
    }

}
