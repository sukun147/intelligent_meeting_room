package com.meeting.intelligent.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.meeting.intelligent.entity.MeetingRoomTypeEntity;
import com.meeting.intelligent.service.MeetingRoomTypeService;
import com.meeting.intelligent.utils.PageUtils;
import com.meeting.intelligent.utils.Result;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.meeting.intelligent.Exception.ExceptionCodeEnum.ROOM_TYPE_NOT_EXIST_EXCEPTION;


/**
 * @author sukun
 * @email 1477264431@qq.com
 * @date 2022-11-27 21:01:36
 */
@RestController
@RequestMapping("/meeting_room_type")
public class MeetingRoomTypeController {
    @Autowired
    private MeetingRoomTypeService meetingRoomTypeService;

    @Autowired
    private CacheManager cacheManager;
    private Cache<String, MeetingRoomTypeEntity> typeCache;

    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder("type_cache_").expire(Duration.ofDays(1)).cacheType(CacheType.REMOTE).build();
        typeCache = cacheManager.getOrCreateCache(qc);
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = meetingRoomTypeService.queryPage(params);
        return Result.success().setData(page);
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Long typeId) {
        MeetingRoomTypeEntity cache = typeCache.get(typeId.toString());
        if (cache != null) {
            return Result.success().setData(cache);
        }
        MeetingRoomTypeEntity meetingRoomType = meetingRoomTypeService.getById(typeId);
        if (meetingRoomType == null) {
            return Result.error(ROOM_TYPE_NOT_EXIST_EXCEPTION.getCode(), ROOM_TYPE_NOT_EXIST_EXCEPTION.getMsg());
        }
        typeCache.put(typeId.toString(), meetingRoomType, 55 + new Random().nextInt(10), TimeUnit.MINUTES);
        return Result.success().setData(meetingRoomType);
    }

    /**
     * 保存
     */
    @PostMapping
    @SaCheckRole("admin")
    public Result save(@RequestBody @Valid MeetingRoomTypeEntity meetingRoomType) {
        meetingRoomTypeService.save(meetingRoomType);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @SaCheckRole("admin")
    public Result update(@RequestBody @Valid MeetingRoomTypeEntity meetingRoomType) {
        meetingRoomTypeService.updateById(meetingRoomType);
        typeCache.remove(meetingRoomType.getTypeId().toString());
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    @SaCheckRole("admin")
    public Result delete(@RequestParam("id") List<Long> typeIds) {
        meetingRoomTypeService.deleteTypes(typeIds);
        return Result.success();
    }

}
