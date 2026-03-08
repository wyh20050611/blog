package com.example.rearend.interceptor;

import com.example.rearend.service.impl.VisitorStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UniqueVisitorInterceptor implements HandlerInterceptor {
    
    @Autowired
    private VisitorStatisticsService visitorStatisticsService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        visitorStatisticsService.recordVisit(request, response);
        return true;
    }
}