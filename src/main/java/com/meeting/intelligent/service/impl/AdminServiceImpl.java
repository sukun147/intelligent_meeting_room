package com.meeting.intelligent.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.vo.LoginVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.meeting.intelligent.dao.AdminDao;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.service.AdminService;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.ACCOUNT_PASSWORD_WRONG_EXCEPTION;


@Service("adminService")
public class AdminServiceImpl extends ServiceImpl<AdminDao, AdminEntity> implements AdminService {

    @Override
    public void login(LoginVo loginVo) {
        AdminEntity admin = this.getOne(new QueryWrapper<AdminEntity>().eq("username", loginVo.getUsername()));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (admin == null || !encoder.matches(loginVo.getPassword(), admin.getPassword())) {
            throw new GlobalException(ACCOUNT_PASSWORD_WRONG_EXCEPTION.getMsg(), ACCOUNT_PASSWORD_WRONG_EXCEPTION.getCode());
        }
    }

    @Override
    public void checkPassword(String password) {
        AdminEntity admin = this.getById(1L);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, admin.getPassword())) {
            throw new GlobalException(ACCOUNT_PASSWORD_WRONG_EXCEPTION.getMsg(), ACCOUNT_PASSWORD_WRONG_EXCEPTION.getCode());
        }
    }

}