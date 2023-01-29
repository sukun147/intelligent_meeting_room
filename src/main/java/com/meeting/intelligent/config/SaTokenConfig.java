package com.meeting.intelligent.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            SaRouter.match("/**")
                .notMatch("/user/login")
                .notMatch("/admin/login")
                .notMatch("/user/register")
                .notMatch("/captcha/**")
                .notMatch("/meeting/room_meetings")
                .notMatch("/meeting/sign_in")
                .notMatch("/meeting/finish")
                .check(r -> StpUtil.checkLogin());
            SaRouter.match("/admin/**")
                .notMatch("/admin/login")
                .check(r -> StpUtil.checkRole("admin"));
        })).addPathPatterns("/**");
    }
}
