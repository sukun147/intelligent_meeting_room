package com.meeting.intelligent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.dao.AdminDao;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.service.AdminService;
import com.meeting.intelligent.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.ACCOUNT_PASSWORD_WRONG_EXCEPTION;
import static com.meeting.intelligent.Exception.ExceptionCodeEnum.USERNAME_DISABLE_EXCEPTION;
import static com.meeting.intelligent.utils.Constant.LOGIN_LIMIT_USERNAME_CACHE_PREFIX;

@Slf4j
@Service("adminService")
public class AdminServiceImpl extends ServiceImpl<AdminDao, AdminEntity> implements AdminService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void login(LoginVo loginVo) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String username = loginVo.getUsername();
        String limit = LOGIN_LIMIT_USERNAME_CACHE_PREFIX + username;
        String count = ops.get(limit);
        if (StringUtils.isNotBlank(count) && Integer.parseInt(count) > 5) {
            redisTemplate.expire(limit, 1, TimeUnit.DAYS);
            log.warn("用户{}登录失败次数超过5次，禁用一天", username);
            // TODO 发送邮件通知管理员
            throw new GlobalException(USERNAME_DISABLE_EXCEPTION.getMsg(), USERNAME_DISABLE_EXCEPTION.getCode());
        }
        AdminEntity admin = this.getOne(new QueryWrapper<AdminEntity>().eq("username", username));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (admin == null || !encoder.matches(loginVo.getPassword(), admin.getPassword())) {
            throw new GlobalException(ACCOUNT_PASSWORD_WRONG_EXCEPTION.getMsg(), ACCOUNT_PASSWORD_WRONG_EXCEPTION.getCode());
        }
    }

    @Override
    public void checkPassword(String password) {
        AdminEntity admin = this.getById(0L);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, admin.getPassword())) {
            throw new GlobalException(ACCOUNT_PASSWORD_WRONG_EXCEPTION.getMsg(), ACCOUNT_PASSWORD_WRONG_EXCEPTION.getCode());
        }
    }

    @Override
    public void updateAdmin(AdminEntity admin) {
        admin.setAdminId(0L);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        admin.setPassword(encoder.encode(admin.getPassword()));
        this.updateById(admin);
    }

}