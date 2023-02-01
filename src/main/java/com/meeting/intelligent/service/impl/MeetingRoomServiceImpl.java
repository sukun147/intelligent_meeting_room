package com.meeting.intelligent.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.MEETING_ROOM_IN_USE_EXCEPTION;
import static com.meeting.intelligent.Exception.ExceptionCodeEnum.MEETING_ROOM_NOT_EXIST_EXCEPTION;

@Service("meetingRoomService")
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomDao, MeetingRoomEntity> implements MeetingRoomService {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MeetingRoomTypeService typeService;

    @Autowired
    private CacheManager cacheManager;
    private Cache<String, MeetingRoomRespVo> roomCache;

    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder("room_cache_").expire(Duration.ofDays(1)).cacheType(CacheType.REMOTE).build();
        roomCache = cacheManager.getOrCreateCache(qc);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MeetingRoomRespVo> page = this.page(
            new Query<MeetingRoomEntity>().getPage(params),
            new QueryWrapper<>()
        ).convert(meetingRoomEntity -> {
            MeetingRoomRespVo meetingRoomRespVo = new MeetingRoomRespVo();
            BeanUtils.copyProperties(meetingRoomEntity, meetingRoomRespVo);
            return meetingRoomRespVo;
        });
        List<MeetingRoomRespVo> records = page.getRecords();
        Set<Long> typeIds = new HashSet<>();
        records.forEach(record -> typeIds.add(record.getTypeId()));
        Map<Long, String> typeMap = typeService.listByIds(typeIds).stream().collect(
            HashMap::new, (m, v) -> m.put(v.getTypeId(), v.getTypeName()), HashMap::putAll
        );
        records.forEach(record -> record.setTypeName(typeMap.get(record.getTypeId())));
        page.setRecords(records);
        return new PageUtils(page);
    }

    @Override
    public MeetingRoomRespVo info(Long roomId) {
        MeetingRoomRespVo cache = roomCache.get(roomId.toString());
        if (cache != null) {
            return cache;
        }
        MeetingRoomEntity meetingRoom = this.getById(roomId);
        if (meetingRoom == null) {
            throw new GlobalException(MEETING_ROOM_NOT_EXIST_EXCEPTION.getMsg(), MEETING_ROOM_NOT_EXIST_EXCEPTION.getCode());
        }
        MeetingRoomTypeEntity type = typeService.getById(meetingRoom.getTypeId());
        MeetingRoomRespVo meetingRoomRespVo = new MeetingRoomRespVo();
        BeanUtils.copyProperties(meetingRoom, meetingRoomRespVo);
        meetingRoomRespVo.setTypeName(type.getTypeName());
        roomCache.put(roomId.toString(), meetingRoomRespVo, 55 + new Random().nextInt(10), TimeUnit.MINUTES);
        return meetingRoomRespVo;
    }

    @Override
    public void saveRoom(MeetingRoomEntity meetingRoom) {
        meetingRoom.setRoomId(null);
        Long typeId = meetingRoom.getTypeId();
        typeService.checkTypeExist(typeId);
        this.save(meetingRoom);
    }

    @Override
    public void updateRoom(MeetingRoomEntity meetingRoom) {
        Long typeId = meetingRoom.getTypeId();
        typeService.checkTypeExist(typeId);
        this.updateById(meetingRoom);
        roomCache.remove(meetingRoom.getRoomId().toString());
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
        roomCache.removeAll(roomIds.stream().map(String::valueOf).collect(Collectors.toSet()));
    }


}