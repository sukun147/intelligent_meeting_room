package com.meeting.intelligent;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.util.Base64Util;
import com.meeting.intelligent.thirdParty.FaceClient;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntelligentMeetingRoomApplication.class)
class FaceTest {

    @Test
    void faceRegisterTest() {
        AipFace client = FaceClient.getInstance();
        try (InputStream is = new FileInputStream("C:\\Users\\14772\\Downloads\\1.jpg")) {
            String image = Base64Util.encode(is.readAllBytes());
            JSONObject jsonObject = client.addUser(image, "BASE64", "1", "1", null);
            System.out.println(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void faceDeleteTest() {
        AipFace client = FaceClient.getInstance();
        JSONObject jsonObject = client.deleteUser("1", "1", null);
        System.out.println(jsonObject.toString());
    }
}
