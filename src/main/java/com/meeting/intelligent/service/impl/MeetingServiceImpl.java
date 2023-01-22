package com.meeting.intelligent.service.impl;

import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.entity.MeetingRoomEntity;
import com.meeting.intelligent.vo.MeetingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;

import com.meeting.intelligent.dao.MeetingDao;
import com.meeting.intelligent.entity.MeetingEntity;
import com.meeting.intelligent.service.MeetingService;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.VALID_EXCEPTION;


@Service("meetingService")
public class MeetingServiceImpl extends ServiceImpl<MeetingDao, MeetingEntity> implements MeetingService {
    @Autowired
    private MeetingRoomServiceImpl meetingRoomService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MeetingEntity> page = this.page(
            new Query<MeetingEntity>().getPage(params),
            new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public void bookMeeting(MeetingVo meetingVo) {
        Date startTime = meetingVo.getStartTime();
        Date endTime = meetingVo.getEndTime();
        if (startTime.after(endTime)) {
            throw new GlobalException("会议时间非正确时间段", VALID_EXCEPTION.getCode());
        }
        if (startTime.getTime() - new Date().getTime() < 48 * 60 * 60 * 1000) {
            // 48小时内立即调度
        } else if (startTime.getTime() - new Date().getTime() < 7 * 24 * 60 * 60 * 1000) {
            // 3-7天内24小时调度
        } else {
            if (meetingVo.getAdjustAble()) {
                // 一周调度
            } else {
                // 24小时调度
            }
        }
    }

}