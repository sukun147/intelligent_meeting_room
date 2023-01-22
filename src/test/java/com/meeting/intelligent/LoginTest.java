package com.meeting.intelligent;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntelligentMeetingRoomApplication.class)
class LoginTest {

    @Test
    void cryptTest() {
        String pwd = "21232f297a57a5a743894a0e4a801fc3"; // admin的MD5摘要
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPwd = encoder.encode(pwd);
        System.out.println(encodedPwd);
        System.out.println(encoder.matches(pwd, encodedPwd));
    }

}
