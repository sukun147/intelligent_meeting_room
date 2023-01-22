package com.meeting.intelligent.config;

import com.meeting.intelligent.thirdParty.FaceClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Data
@Configuration
public class FaceConfig {
    @Value("${BaiduAip.appId}")
    private String appId;

    @Value("${BaiduAip.apiKey}")
    private String apiKey;

    @Value("${BaiduAip.secretKey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        FaceClient.setFaceParameter(this);
    }
}
