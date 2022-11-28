package com.meeting.intelligent.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;

import com.meeting.intelligent.dao.MeetingDao;
import com.meeting.intelligent.entity.MeetingEntity;
import com.meeting.intelligent.service.MeetingService;


@Service("meetingService")
public class MeetingServiceImpl extends ServiceImpl<MeetingDao, MeetingEntity> implements MeetingService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MeetingEntity> page = this.page(
            new Query<MeetingEntity>().getPage(params),
            new QueryWrapper<MeetingEntity>()
        );

        return new PageUtils(page);
    }

}