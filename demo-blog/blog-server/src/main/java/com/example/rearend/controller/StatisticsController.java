package com.example.rearend.controller;

import com.example.rearend.result.Result;
import com.example.rearend.service.impl.VisitorStatisticsService;
import com.example.rearend.service.impl.WebsiteStatisticsService;
import com.example.rearend.vo.VisitorStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/statistics")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "记录网站信息接口")
public class StatisticsController {

    private final VisitorStatisticsService visitorStatisticsService;

    private final WebsiteStatisticsService websiteStatisticsService;

    /**
     * 获取网站访客统计数据
     * @return VisitorStatisticsDTO
     */
    @GetMapping("/visitors")
    @ApiOperation("获取网站访客统计数据")
    public Result<VisitorStatisticsVO> getVisitorStatistics() {
        try {
            log.info("获取网站访问统计数据");
            Long uniqueVisitors = visitorStatisticsService.getTotalUniqueVisitors();
            Long totalVisits = visitorStatisticsService.getTotalVisits();

            VisitorStatisticsVO statistics = new VisitorStatisticsVO(uniqueVisitors, totalVisits);
            return Result.success(statistics);
        } catch (Exception e) {
            // 添加详细日志
            log.error("获取统计数据失败", e);
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }


    /**
     * 获取网站运行统计信息
     * @return 网站运行天数
     */
    @GetMapping("/website")
    @ApiOperation("获取网站运行统计信息")
    public Result<Map<String, Object>> getWebsiteStatistics() {
        try {
            log.info("获取网站运行天数");
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("runningDays", websiteStatisticsService.getWebsiteRunningDays());
            statistics.put("startDate", websiteStatisticsService.getWebsiteStartDate());
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取网站运行统计失败", e);
            return Result.error("获取网站运行统计失败: " + e.getMessage());
        }
    }
}