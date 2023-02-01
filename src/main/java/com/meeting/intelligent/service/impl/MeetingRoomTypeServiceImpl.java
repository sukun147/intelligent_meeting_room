package com.meeting.intelligent.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.meeting.intelligent.Exception.GlobalException;
import com.meeting.intelligent.entity.MeetingRoomEntity;
import com.meeting.intelligent.service.MeetingRoomService;
import com.meeting.intelligent.vo.MeetingRoomRespVo;
import com.meeting.intelligent.vo.MeetingRoomTypeVo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Query;

import com.meeting.intelligent.dao.MeetingRoomTypeDao;
import com.meeting.intelligent.entity.MeetingRoomTypeEntity;
import com.meeting.intelligent.service.MeetingRoomTypeService;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.ROOM_TYPE_IN_USE_EXCEPTION;
import static com.meeting.intelligent.Exception.ExceptionCodeEnum.ROOM_TYPE_NOT_EXIST_EXCEPTION;


@Service("meetingRoomTypeService")
public class MeetingRoomTypeServiceImpl extends ServiceImpl<MeetingRoomTypeDao, MeetingRoomTypeEntity> implements MeetingRoomTypeService {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private CacheManager cacheManager;
    private Cache<String, MeetingRoomTypeEntity> typeCache;

    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder("type_cache_").expire(Duration.ofDays(1)).cacheType(CacheType.REMOTE).build();
        typeCache = cacheManager.getOrCreateCache(qc);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MeetingRoomTypeEntity> page = this.page(
            new Query<MeetingRoomTypeEntity>().getPage(params),
            new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public void deleteTypes(List<Long> typeIds) {
        QueryWrapper<MeetingRoomEntity> queryWrapper = new QueryWrapper<MeetingRoomEntity>()
            .eq("room_status", 1)
            .in("type_id", typeIds);
        if (meetingRoomService.count(queryWrapper) > 0) {
            throw new GlobalException(ROOM_TYPE_IN_USE_EXCEPTION.getMsg(), ROOM_TYPE_IN_USE_EXCEPTION.getCode());
        }
        this.removeByIds(typeIds);
        typeCache.removeAll(typeIds.stream().map(String::valueOf).collect(Collectors.toSet()));
    }

    @Override
    public void checkTypeExist(Long typeId) {
        if (this.getById(typeId) == null) {
            throw new GlobalException(ROOM_TYPE_NOT_EXIST_EXCEPTION.getMsg(), ROOM_TYPE_NOT_EXIST_EXCEPTION.getCode());
        }
    }
}