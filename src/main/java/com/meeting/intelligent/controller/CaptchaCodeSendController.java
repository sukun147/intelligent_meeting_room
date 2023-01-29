package com.meeting.intelligent.controller;

import com.meeting.intelligent.utils.Result;
import com.meeting.intelligent.utils.CaptchaCode;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/captcha")
public class CaptchaCodeSendController {
    @Autowired
    private CaptchaCode captchaCode;

    /**
     * 发送验证码到邮箱
     */
    @GetMapping(value = "/send_code_email")
    public Result sendCodeEmail(@RequestParam("email") String email) throws MessagingException {
        captchaCode.sendInEmail(email);
        return Result.success();
    }

    /**
     * 发送验证码到手机
     */
    @GetMapping(value = "/send_code_SMS")
    public Result sendCodeSMS(@RequestParam("phone") String phone) {
        captchaCode.sendInSMS(phone);
        return Result.success();
    }
}
