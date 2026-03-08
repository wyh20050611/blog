package com.example.rearend.config;

import com.example.rearend.service.impl.WebsiteStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements CommandLineRunner {
    
    @Autowired
    private WebsiteStatisticsService websiteStatisticsService;
    
    @Override
    public void run(String... args) throws Exception {
        websiteStatisticsService.initWebsiteStartDate();
    }
}