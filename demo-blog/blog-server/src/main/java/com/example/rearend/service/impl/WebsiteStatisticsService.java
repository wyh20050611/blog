package com.example.rearend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class WebsiteStatisticsService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String WEBSITE_START_DATE_KEY = "website_start_date";

    // 设置网站正式上线日期
    private static final String OFFICIAL_START_DATE = "2024-12-01";

    /**
     * 初始化网站启动日期
     */
    @PostConstruct
    public void initWebsiteStartDate() {
        // 检查是否已设置启动日期
        Object startDate = redisTemplate.opsForValue().get(WEBSITE_START_DATE_KEY);
        if (startDate == null) {
            // 使用预设的正式上线日期
            redisTemplate.opsForValue().set(WEBSITE_START_DATE_KEY, OFFICIAL_START_DATE);
            log.info("网站启动日期已初始化为正式上线日期: {}", OFFICIAL_START_DATE);
        } else {
            log.info("网站启动日期已存在: {}", startDate);
        }
    }

    /**
     * 获取网站运行天数（初始值为1）
     * @return 运行天数
     */
    public Long getWebsiteRunningDays() {
        try {
            Object startDateStr = redisTemplate.opsForValue().get(WEBSITE_START_DATE_KEY);
            if (startDateStr != null) {
                LocalDate startDate = LocalDate.parse(startDateStr.toString());
                long days = ChronoUnit.DAYS.between(startDate, LocalDate.now());
                // 初始值为1，之后每天递增
                return Math.max(1L, days + 1);
            }
            return 1L;
        } catch (Exception e) {
            log.warn("Failed to calculate website running days", e);
            return 1L;
        }
    }

    /**
     * 获取网站启动日期
     * @return 启动日期
     */
    public String getWebsiteStartDate() {
        try {
            Object startDate = redisTemplate.opsForValue().get(WEBSITE_START_DATE_KEY);
            return startDate != null ? startDate.toString() : "";
        } catch (Exception e) {
            log.warn("Failed to get website start date", e);
            return "";
        }
    }
}