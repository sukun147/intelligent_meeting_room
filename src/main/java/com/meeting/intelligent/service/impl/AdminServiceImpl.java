package com.meeting.intelligent.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;

import com.meeting.intelligent.dao.AdminDao;
import com.meeting.intelligent.entity.AdminEntity;
import com.meeting.intelligent.service.AdminService;


@Service("adminService")
public class AdminServiceImpl extends ServiceImpl<AdminDao, AdminEntity> implements AdminService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AdminEntity> page = this.page(
                new Query<AdminEntity>().getPage(params),
                new QueryWrapper<AdminEntity>()
        );

        return new PageUtils(page);
    }

}