package com.meeting.intelligent.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;

import com.meeting.intelligent.dao.MeetingRoomTypeDao;
import com.meeting.intelligent.entity.MeetingRoomTypeEntity;
import com.meeting.intelligent.service.MeetingRoomTypeService;


@Service("meetingRoomTypeService")
public class MeetingRoomTypeServiceImpl extends ServiceImpl<MeetingRoomTypeDao, MeetingRoomTypeEntity> implements MeetingRoomTypeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MeetingRoomTypeEntity> page = this.page(
            new Query<MeetingRoomTypeEntity>().getPage(params),
            new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

}