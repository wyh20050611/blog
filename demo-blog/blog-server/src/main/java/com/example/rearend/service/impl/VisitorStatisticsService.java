package com.example.rearend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class VisitorStatisticsService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String VISITOR_PREFIX = "visitor:";
    private static final String TOTAL_VISITORS_KEY = "total_unique_visitors";


    public void recordVisit(HttpServletRequest request, HttpServletResponse response) {
        String visitorId = getOrCreateVisitorId(request, response);
        String redisKey = VISITOR_PREFIX + visitorId;

        // 检查是否已记录过此访客
        Boolean hasVisited = redisTemplate.hasKey(redisKey);
        if (Boolean.FALSE.equals(hasVisited)) {
            // 首次访问，记录访客并增加总访客数
            redisTemplate.opsForValue().set(redisKey, System.currentTimeMillis());
            redisTemplate.opsForValue().increment(TOTAL_VISITORS_KEY, 1);
        } else {
            // 更新最后访问时间
            redisTemplate.opsForValue().set(redisKey, System.currentTimeMillis());
        }

        // 记录总访问次数
        redisTemplate.opsForValue().increment("total_visits", 1);
    }

    public Long getTotalVisits() {
        Object count = redisTemplate.opsForValue().get("total_visits");
        return count == null ? 0L : Long.valueOf(count.toString());
    }

    public Long getTotalUniqueVisitors() {
        Object count = redisTemplate.opsForValue().get(TOTAL_VISITORS_KEY);
        return count == null ? 0L : Long.valueOf(count.toString());
    }

    private String getOrCreateVisitorId(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("visitorId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 创建新的访客ID
        String visitorId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("visitorId", visitorId);
        cookie.setMaxAge(1 * 24 * 60 * 60); // 一天过期
        cookie.setPath("/");
        response.addCookie(cookie);

        return visitorId;
    }

}