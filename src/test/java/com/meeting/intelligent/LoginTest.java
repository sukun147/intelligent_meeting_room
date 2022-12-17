package com.meeting.intelligent;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.UUID;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntelligentMeetingRoomApplication.class)
class LoginTest {

    @Test
    void cryptTest() {
        String salt = UUID.randomUUID().toString();
        System.out.println(salt);
        String rawPwd = DigestUtils.md5DigestAsHex("admin".getBytes());
        System.out.println(rawPwd);
        String cryptPwd = DigestUtils.md5DigestAsHex((rawPwd + salt).getBytes());
        System.out.println(cryptPwd);
    }

}
