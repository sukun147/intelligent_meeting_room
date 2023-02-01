package com.meeting.intelligent;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMethodCache(basePackages = "com.meeting.intelligent")
public class IntelligentMeetingRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelligentMeetingRoomApplication.class, args);
    }

}
