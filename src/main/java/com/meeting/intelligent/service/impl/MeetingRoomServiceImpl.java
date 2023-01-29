package com.meeting.intelligent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.dao.MeetingRoomDao;
import com.meeting.intelligent.entity.MeetingEntity;
import com.meeting.intelligent.entity.MeetingRoomEntity;
import com.meeting.intelligent.entity.MeetingRoomTypeEntity;
import com.meeting.intelligent.service.MeetingRoomService;
import com.meeting.intelligent.service.MeetingRoomTypeService;
import com.meeting.intelligent.service.MeetingService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;
import com.meeting.intelligent.vo.MeetingRoomRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.MEETING_ROOM_IN_USE_EXCEPTION;

@Service("meetingRoomService")
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomDao, MeetingRoomEntity> implements MeetingRoomService {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MeetingRoomTypeService typeService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MeetingRoomRespVo> page = this.page(
            new Query<MeetingRoomEntity>().getPage(params),
            new QueryWrapper<>()
        ).convert(meetingRoomEntity -> {
            MeetingRoomRespVo meetingRoomRespVo = new MeetingRoomRespVo();
            BeanUtils.copyProperties(meetingRoomEntity, meetingRoomRespVo);
            MeetingRoomTypeEntity type = typeService.getById(meetingRoomEntity.getTypeId());
            meetingRoomRespVo.setTypeName(type.getTypeName());
            return meetingRoomRespVo;
        });
        return new PageUtils(page);
    }

    @Override
    public MeetingRoomRespVo info(Long roomId) {
        MeetingRoomEntity meetingRoom = this.getById(roomId);
        MeetingRoomTypeEntity type = typeService.getById(meetingRoom.getTypeId());
        MeetingRoomRespVo meetingRoomRespVo = new MeetingRoomRespVo();
        BeanUtils.copyProperties(meetingRoom, meetingRoomRespVo);
        meetingRoomRespVo.setTypeName(type.getTypeName());
        return meetingRoomRespVo;
    }

    @Override
    public void saveRoom(MeetingRoomEntity meetingRoom) {
        meetingRoom.setRoomId(null);
        Integer typeId = meetingRoom.getTypeId();
        typeService.checkTypeExist(typeId);
        this.save(meetingRoom);
    }

    @Override
    public void updateRoom(MeetingRoomEntity meetingRoom) {
        Integer typeId = meetingRoom.getTypeId();
        typeService.checkTypeExist(typeId);
        this.updateById(meetingRoom);
    }

    @Override
    public void deleteRoom(List<Long> roomIds) {
        QueryWrapper<MeetingEntity> queryWrapper = new QueryWrapper<MeetingEntity>()
            .eq("meeting_status", 1)
            .in("room_id", roomIds);
        if (meetingService.count(queryWrapper) > 0) {
            throw new GlobalException(MEETING_ROOM_IN_USE_EXCEPTION.getMsg(), MEETING_ROOM_IN_USE_EXCEPTION.getCode());
        }
        this.removeByIds(roomIds);
    }


}