package com.meeting.intelligent.utils;

import com.meeting.intelligent.Exception.GlobalException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.CAPTCHA_CODE_BUSY_EXCEPTION;
import static com.meeting.intelligent.Exception.ExceptionCodeEnum.PARAMETER_CHECK_EXCEPTION;
import static com.meeting.intelligent.utils.Constant.CAPTCHA_CODE_CACHE_PREFIX;

@Component
public class CaptchaCode {

    private static final String[] metaCode = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
        "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${mail.fromMail.fromAddress}")
    private String fromAddress;

    private String generateCaptchaCodeText(int codeLength) {
        Random random = new Random();
        StringBuilder captchaCode = new StringBuilder();
        while (captchaCode.length() < codeLength) {
            int i = random.nextInt(metaCode.length);
            captchaCode.append(metaCode[i]);
        }
        return captchaCode.toString();
    }

    public void sendInEmail(String email) throws MessagingException {
        if (!Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", email)) {
            throw new GlobalException("邮箱格式不正确", PARAMETER_CHECK_EXCEPTION.getCode());
        }
        String preEmail = CAPTCHA_CODE_CACHE_PREFIX + email;
        antiRush(preEmail);
        String code = generateCaptchaCodeText(6);
        Context context = new Context();
        context.setVariable("captchaCode", Arrays.asList(code.split("")));
        String emailContent = templateEngine.process("EmailVerificationCode", context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject("智能会议室管理系统验证码");
        helper.setText(emailContent, true);
        mailSender.send(message);
        redisTemplate.opsForValue().set(preEmail, code, 60 * 5);
    }

    public void sendInSMS(String phone) {
        if (!Pattern.matches("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", phone)) {
            throw new GlobalException("手机号格式不正确", PARAMETER_CHECK_EXCEPTION.getCode());
        }
        String prePhone = CAPTCHA_CODE_CACHE_PREFIX + phone;
        antiRush(prePhone);
        String code = generateCaptchaCodeText(6);
        // 未备案，无SMS服务，验证码打印在控制台中自行读取供测试用
        System.out.println(phone + "验证码：" + code);
        redisTemplate.opsForValue().set(
            prePhone,
            code + "_" + System.currentTimeMillis(),
            10L,
            TimeUnit.MINUTES
        );
    }

    private void antiRush(String preRequester) {
        String preCode = redisTemplate.opsForValue().get(preRequester);
        if (StringUtils.isNotBlank(preCode)) {
            long pre = Long.parseLong(preCode.split("_")[1]);
            if (System.currentTimeMillis() - pre < 60000) {
                throw new GlobalException(CAPTCHA_CODE_BUSY_EXCEPTION.getMsg(), CAPTCHA_CODE_BUSY_EXCEPTION.getCode());
            }
        }
        redisTemplate.delete(preRequester);
    }
}