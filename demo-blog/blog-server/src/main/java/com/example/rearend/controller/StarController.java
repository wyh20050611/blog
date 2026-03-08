package com.example.rearend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.rearend.dto.StarDTO;
import com.example.rearend.entity.Star;
import com.example.rearend.entity.Writing;
import com.example.rearend.result.Result;
import com.example.rearend.service.IStarService;
import com.example.rearend.service.IWritingService;
import com.example.rearend.service.impl.StarServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 点赞表 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-08-25
 */
@RestController
@RequestMapping("/admin/star")
@Slf4j
@Api(tags = "点赞相关接口")
@RequiredArgsConstructor
public class StarController {

    private final IStarService starService;

    private final IWritingService writingService;

    @GetMapping
    @ApiOperation("查询点赞状态")
    public Result selectStarStatus(StarDTO starDTO) {
        log.info("查询点赞状态:{}", starDTO);
        //先查询是否有过点赞记录，如果没有，则返回0，有则放回点赞状态
        QueryWrapper<Star> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(Star::getDeviceId, starDTO.getDeviceId())  // 修改这里
                .eq(Star::getWritingId, starDTO.getWritingId());
        Star star = starService.getOne(wrapper);
        if (star == null) {
            return Result.success(0);
        }
        return Result.success(star.getStatus());
    }


    @PostMapping("/add")
    @ApiOperation("添加点赞")
    public Result addStar(@RequestBody StarDTO starDTO) {
        log.info("添加点赞:{}", starDTO);
        //先查询是否有过点赞记录，如果有，则直接修改
        QueryWrapper<Star> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(Star::getDeviceId, starDTO.getDeviceId())  // 修改这里
                .eq(Star::getWritingId, starDTO.getWritingId());
        Star star1 = starService.getOne(wrapper);
        if (star1 != null) {
            UpdateWrapper<Star> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .eq(Star::getDeviceId, starDTO.getDeviceId())  // 修改这里
                    .eq(Star::getWritingId, starDTO.getWritingId())
                    .set(Star::getStatus, 1L);
            starService.update(updateWrapper);
        } else {
            //如果没有，则添加点赞记录
            Star star = new Star();
            BeanUtils.copyProperties(starDTO, star);
            star.setStatus(1L);
            starService.save(star);
        }
        Long star2 = writingService.getById(starDTO.getWritingId()).getStar();
        UpdateWrapper<Writing> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Writing::getId, starDTO.getWritingId()).set(Writing::getStar, star2 + 1);
        writingService.update(updateWrapper);
        return Result.success();
    }

    @PutMapping("/delete")
    @ApiOperation("取消点赞")
    public Result decreaseStar(@RequestBody StarDTO starDTO) {
        log.info("取消点赞:{}", starDTO);
        UpdateWrapper<Star> wrapper = new UpdateWrapper<>();
        wrapper.lambda()
                .eq(Star::getDeviceId, starDTO.getDeviceId())  // 修改这里
                .eq(Star::getWritingId, starDTO.getWritingId())
                .set(Star::getStatus, 2L);
        starService.update(wrapper);
        Long star1 = writingService.getById(starDTO.getWritingId()).getStar();
        UpdateWrapper<Writing> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Writing::getId, starDTO.getWritingId()).set(Writing::getStar, star1 - 1);
        writingService.update(updateWrapper);
        return Result.success();
    }

    /**
     * 查询点赞总数
     */
    @GetMapping("/count")
    @ApiOperation("查询点赞总数")
    public Result selectStarCount() {
        log.info("查询点赞总数");
        // 查询writing表的star列总数值
        QueryWrapper<Writing> writingQueryWrapper = new QueryWrapper<>();
        writingQueryWrapper.select("sum(star) as star");
        Object starObj = writingService.getBaseMapper().selectMaps(writingQueryWrapper).get(0).get("star");
        Long count = (starObj == null) ? 0L : ((Number) starObj).longValue();
        return Result.success(count);
    }

}
