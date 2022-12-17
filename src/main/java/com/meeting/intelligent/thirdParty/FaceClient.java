package com.meeting.intelligent.thirdParty;

import com.baidu.aip.face.AipFace;
import com.meeting.intelligent.config.FaceConfig;

public class FaceClient {

    private static String appId;
    private static String apiKey;
    private static String secretKey;

    private FaceClient() {
    }

    public static void setFaceParameter(FaceConfig faceConfig) {
        appId = faceConfig.getAppId();
        apiKey = faceConfig.getApiKey();
        secretKey = faceConfig.getSecretKey();
    }

    public static AipFace getInstance() {
        return FaceClientFactory.instance;
    }

    private static class FaceClientFactory {
        private static final AipFace instance = new AipFace(appId, apiKey, secretKey);
    }


}
