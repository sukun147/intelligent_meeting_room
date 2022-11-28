package com.meeting.intelligent.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;

import com.meeting.intelligent.dao.MeetingRoomDao;
import com.meeting.intelligent.entity.MeetingRoomEntity;
import com.meeting.intelligent.service.MeetingRoomService;


@Service("meetingRoomService")
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomDao, MeetingRoomEntity> implements MeetingRoomService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MeetingRoomEntity> page = this.page(
            new Query<MeetingRoomEntity>().getPage(params),
            new QueryWrapper<MeetingRoomEntity>()
        );

        return new PageUtils(page);
    }

}