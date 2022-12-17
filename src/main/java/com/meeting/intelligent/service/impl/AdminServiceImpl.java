package com.meeting.intelligent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.meeting.intelligent.dao.AdminDao;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.service.AdminService;
import org.springframework.util.DigestUtils;

import java.util.Arrays;


@Service("adminService")
public class AdminServiceImpl extends ServiceImpl<AdminDao, AdminEntity> implements AdminService {

    @Override
    public boolean login(AdminEntity adminEntity) {
        String username = adminEntity.getUsername();
        QueryWrapper<AdminEntity> queryWrapper = new QueryWrapper<AdminEntity>().eq("username", username);
        AdminEntity admin = this.baseMapper.selectOne(queryWrapper);
        if (admin == null) {
            return false;
        }
        String cryptPwd = DigestUtils.md5DigestAsHex((adminEntity.getPassword() + admin.getSalt()).getBytes());
        String realPwd = admin.getPassword();
        return cryptPwd.equals(realPwd);
    }
}